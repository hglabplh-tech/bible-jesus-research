package org.harry.jesus.jesajautils;

import generated.*;
import javafx.scene.control.IndexRange;
import javafx.scene.paint.Color;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.browse.TextStyle;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.*;

public class TextRendering {

    private final BibleTextUtils bibleUtils;
    private final FoldableStyledArea area;

    Map<Integer, IndexRange> chapterMap = new LinkedHashMap<>();

    Map<IndexRange, TextStyle> renderMap = new LinkedHashMap<>();

    Map<BigInteger,Map<Integer, IndexRange>> book = new LinkedHashMap<>();

    List<String> notes = new ArrayList<>();

    public TextRendering(BibleTextUtils bibleUtils, FoldableStyledArea area) {
        this.bibleUtils = bibleUtils;
        this.area = area;
    }

    public List<String> getNotes() {
        return notes;
    }

    public String render(XMLBIBLE bible, String xmlBookName, Integer chapterNo) {
        Optional<BIBLEBOOK> book = this.bibleUtils.getBookByName(bible, xmlBookName);
        Integer start = 0;
        StringBuffer strContent = new StringBuffer();
        if (book.isPresent()) {
            Optional<CHAPTER> chapter = this.bibleUtils.getChapter(book.get(), chapterNo);
            if (chapter.isPresent()) {
                renderChapter(start, strContent, chapter);
            }
        }
        area.replaceText(strContent.toString());
        for (Map.Entry<IndexRange, TextStyle> entry:renderMap.entrySet()) {
            IndexRange range = entry.getKey();
            TextStyle style = entry.getValue();
            area.setStyle(range.getStart(), range.getEnd(), style);
        }
        return strContent.toString();
    }

    private void renderChapter(Integer start, StringBuffer strContent, Optional<CHAPTER> chapter) {
        CHAPTER realChapter = chapter.get();
        area.setEditable(false);
        area.setLayoutX(0.0);
        area.setLayoutY(0.0);
        for (JAXBElement verseOr: realChapter.getPROLOGOrCAPTIONOrVERS()) {
            Object thing = verseOr.getValue();
            if (thing instanceof VERS) {
                renderVers(strContent, (VERS) thing);
                IndexRange range = new IndexRange(start, start + strContent.toString().length() - 1);
                start = start + strContent.toString().length();
                this.chapterMap.put(realChapter.getCnumber().intValue(), range);
            } else if (thing instanceof CAPTION) {
                CAPTION caption = (CAPTION)thing;
                String type = caption.getType().value();
                Integer vRef = caption.getVref().intValue();
                for (Object content : caption.getContent()) {
                    if (content instanceof String) {
                        strContent.append(content);
                    }
                }
            } else if (thing instanceof PROLOG) {

            }


        }
    }

    private void renderVers(StringBuffer strContent, VERS vers) {
        strContent.append(vers.getVnumber().toString(10))
                .append(". ");
        int start = 0;
        List<Object> contents = null;
        int breakPoint = 0;
        contents = vers.getContent();

        for (Object content: contents) {

            if (content instanceof String) {
                String text = (String)content;
                strContent.append(text);
                breakPoint = breakPoint + text.length();

            } else if (content instanceof JAXBElement) {
                 Class jaxbClazz = ((JAXBElement<?>) content).getDeclaredType();
                if (jaxbClazz.getName().equals(STYLE.class.getName()) ) {
                    STYLE styled = (STYLE) ((JAXBElement<?>) content).getValue();
                    for (Object styledContent : styled.getContent()) {
                        if (styledContent instanceof String) {
                            String text = (String) styledContent;
                            start = strContent.toString().length() - 1;
                            IndexRange range = new IndexRange(start, start + text.length() + 1);
                            TextStyle style = TextStyle.bold(true).updateBackgroundColor(Color.AZURE);
                            renderMap.put(range, style);
                            strContent.append(text);
                            breakPoint = breakPoint + text.length();

                        }

                    }
                } else if (jaxbClazz.getName().equals(DIV.class.getName()) ) {
                    DIV div = (DIV)((JAXBElement<?>) content).getValue();
                    List<Object> divContents = div.getContent();
                    for (Object divCont: divContents) {
                        if (divCont instanceof JAXBElement) {
                            if (((JAXBElement)divCont).getValue() instanceof NOTE) {
                                NOTE theNote = (NOTE)((JAXBElement)divCont).getValue();
                                saveNote(theNote, vers.getVnumber().intValue());
                                strContent.append("[N" + notes.size() + "]");
                            }
                        }
                    }
                } else if (jaxbClazz.getName().equals(NOTE.class.getName()) ) {
                    NOTE note = (NOTE)((JAXBElement<?>) content).getValue();
                    saveNote(note, vers.getVnumber().intValue());
                    strContent.append("[N" + notes.size() + "]");
                } else if (jaxbClazz.getName().equals(BR.class.getName()) ) {
                    BR br = (BR)((JAXBElement<?>) content).getValue();
                    BreakType type = br.getArt();
                    type.value();
                    strContent.append("\n");
                }
            }


        }

        strContent.append("\n");
    }

    private void saveNote(NOTE theNote, Integer vNumber) {
        List<Object> noteContents = theNote.getContent();
        StringBuffer buffer = new StringBuffer();
        buffer.append("(" + (notes.size() + 1) + ") <VERS: " + vNumber + "> ");
        for (Object noteContent : noteContents) {
            if (noteContent instanceof String) {
                String text = (String) noteContent;
                buffer.append(text);
            }
        }
        notes.add(buffer.toString());
    }
}
