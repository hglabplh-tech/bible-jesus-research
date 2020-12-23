package org.harry.jesus.jesajautils;

import generated.*;
import javafx.scene.control.IndexRange;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.model.TwoDimensional;
import org.harry.jesus.jesajautils.BibleTextUtils.BookLink;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.*;

public class TextRendering {

    private final BibleTextUtils bibleUtils;
    private final FoldableStyledArea area;

    private Map<Integer, IndexRange> chapterMap = new LinkedHashMap<>();

    private Map<IndexRange, TextStyle> renderMap = new LinkedHashMap<>();

    private Map<BigInteger,Map<Integer, IndexRange>> book = new LinkedHashMap<>();

    private List<String> notes = new ArrayList<>();

    private Integer lastPosINBuffer = 0;

    private static final int TEXT_WIDTH = 80;

    private final String actBookLabel;
    private final Integer actChapter;


    public TextRendering(BibleTextUtils bibleUtils, FoldableStyledArea area, String actBookLabel, Integer actChapter) {
        this.bibleUtils = bibleUtils;
        this.area = area;
        this.actBookLabel = actBookLabel;
        this.actChapter = actChapter;
    }

    public List<String> getNotes() {
        return notes;
    }

    public String render(XMLBIBLE bible, List<BookLink> links) {
        StringBuffer strContent = new StringBuffer();
        Integer start = 0;
        for (BookLink link:links) {
            Optional<BIBLEBOOK> book = this.bibleUtils.getBookByLabel(bible, link.getBookLabel());
            if (book.isPresent()) {
                Map<Integer, List<Integer>> linkMap = new LinkedHashMap<>();
                linkMap.put(link.getChapter(), link.getVerses());
                renderLink(bible, start, link, strContent, linkMap);
            }
        }
        setAreaText(strContent);
        return strContent.toString();

    }

    public boolean render(XMLBIBLE bible, String bookLabel, Integer chapterNo) {
        boolean chapterFound = false;
        Optional<BIBLEBOOK> book = this.bibleUtils.getBookByLabel(bible, bookLabel);
        Integer start = 0;
        StringBuffer strContent = new StringBuffer();
        if (book.isPresent()) {
            Optional<CHAPTER> chapter = this.bibleUtils.getChapter(book.get(), chapterNo);
            if (chapter.isPresent()) {
                chapterFound = true;
                renderChapter(start, strContent, chapter);
            }
        }
        setAreaText(strContent);
        return chapterFound;
    }

    public Map.Entry<Integer, IndexRange> selectVerseByGivenRange(IndexRange range) {
        int start = range.getStart();

        for (Map.Entry<Integer, IndexRange> entry: this.chapterMap.entrySet()) {
            IndexRange compRange = entry.getValue();
            if (start >= compRange.getStart() && start <= compRange.getEnd()) {
                this.area.setStyle(compRange.getStart(), compRange.getEnd(), TextStyle.underline(true));
                return entry;
            }
        }
        return null;
    }

    private void setAreaText(StringBuffer strContent) {
        area.replaceText(strContent.toString());
        area.setStyle(0, strContent.toString().length(), TextStyle.bold(false)
                .updateTextColor(Color.BLACK)
                .updateItalic(false)
                .updateBackgroundColor(Color.WHITE)
        );
        for (Map.Entry<IndexRange, TextStyle> entry : renderMap.entrySet()) {
            IndexRange range = entry.getKey();
            TextStyle style = entry.getValue();
            area.setStyle(range.getStart(), range.getEnd(), style);
        }
    }

    private void renderLink(XMLBIBLE bible, Integer start, BookLink link, StringBuffer strContent, Map<Integer, List<Integer>> chapterVerses) {
        Optional<BIBLEBOOK> book = this.bibleUtils.getBookByLabel(bible, link.getBookLabel());
        strContent.append("Link for link: " + link.getBookLabel() + " chapter: " + link.getChapter() + " Verses: ");
        for (Integer verse : link.getVerses()) {
            strContent.append(verse.toString() + ",");
        }
        IndexRange range = new IndexRange(start, start + strContent.toString().length() - 1);
        start = start + strContent.toString().length();
        this.renderMap.put(range, TextStyle.bold(true).updateItalic(true).updateUnderline(true));
        strContent.append("\n");

        if (book.isPresent()) {
        for (Map.Entry<Integer, List<Integer>> entry : chapterVerses.entrySet()) {
                Optional<CHAPTER> chapter = this.bibleUtils.getChapter(book.get(), entry.getKey());
                if (chapter.isPresent()) {
                    CHAPTER realChapter = chapter.get();
                    int index = 0;

                    for (JAXBElement verseOr : realChapter.getPROLOGOrCAPTIONOrVERS()) {
                        Object thing = verseOr.getValue();
                        if (thing instanceof VERS) {
                            if (entry.getValue().size() > index) {
                                if (((VERS) thing).getVnumber().intValue() == entry.getValue().get(index)) {
                                    renderVers(strContent, (VERS) thing);
                                    range = new IndexRange(start, start + strContent.toString().length() - 1);
                                    start = start + strContent.toString().length();
                                    this.chapterMap.put(realChapter.getCnumber().intValue(), range);
                                    index++;
                                    strContent.append("\n");
                                }
                            }
                        }

                    }

              }

            }
        }
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
                start = setVerseRangeToChapterMap(start, strContent, (VERS) thing);
            } else if (thing instanceof CAPTION) {
                CAPTION caption = (CAPTION)thing;
                String type = caption.getType().value();
                Integer vRef = caption.getVref().intValue();
                for (Object content : caption.getContent()) {
                    if (content instanceof String) {
                        String text = (String)content;
                        start = strContent.toString().length() - 1;
                        IndexRange range = new IndexRange(start, start + text.length() + 1);
                        TextStyle style = TextStyle.bold(true)
                                .updateUnderline(true);
                        strContent.append(text);
                        renderMap.put(range, style);
                    }
                }
            } else if (thing instanceof PROLOG) {

            }


        }
    }

    @NotNull
    private Integer setVerseRangeToChapterMap(Integer start, StringBuffer strContent, VERS thing) {
        IndexRange range = new IndexRange(start, strContent.toString().length() - 1);
        this.chapterMap.put(thing.getVnumber().intValue(), range);
        start = strContent.toString().length();
        return start;
    }

    private void renderVers(StringBuffer strContent, VERS vers) {
        strContent.append(vers.getVnumber().toString(10))
                .append(". ");
        int start;
        List<Object> contents = null;
        contents = vers.getContent();

        for (Object content: contents) {

            if (content instanceof String) {
                String text = (String)content;
                renderBlock(strContent, text);
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
                            renderBlock(strContent, text);
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
                                renderFootNote(strContent);
                            }
                        }
                    }
                } else if (jaxbClazz.getName().equals(NOTE.class.getName()) ) {
                    NOTE note = (NOTE)((JAXBElement<?>) content).getValue();
                    saveNote(note, vers.getVnumber().intValue());
                   renderFootNote(strContent);

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

    private void renderFootNote(StringBuffer strContent) {
        int start;
        String footNote = "[" + notes.size() + "]";
        start = strContent.toString().length() - 1;
        IndexRange range = new IndexRange(start, start + footNote.length() + 1);
        TextStyle style = TextStyle.bold(true)
                .updateBackgroundColor(Color.BLUE)
                .updateItalic(true)
                .updateTextColor(Color.WHITE);
        renderMap.put(range, style);
        strContent.append(footNote);
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

    private int renderBlock(StringBuffer strContent, String block) {
        StringBuffer lineBuffer = new StringBuffer();
        if (block.length() >= TEXT_WIDTH) {
            int pos = 0;
            int actLength = 0;
            int blockL = block.length();
            while (actLength < blockL) {
                int temp = (actLength + pos + 1);
                actLength = Math.min(temp, block.length());
                int lastPos = actLength -1;
                pos = block.indexOf(" ", lastPos + TEXT_WIDTH);
                if (pos != -1) {
                    strContent.append(block.substring(lastPos, pos) + "\n");
                    this.lastPosINBuffer = lineBuffer.toString().length() + (pos - lastPos);
                } else {
                    actLength = block.length();
                    pos = actLength;

                    strContent.append(block.substring(lastPos));
                    this.lastPosINBuffer = lineBuffer.toString().length() + (pos + 1);
                }
            }
        }else {
            strContent.append(block);
            this.lastPosINBuffer = lineBuffer.toString().length() + block.length();
        }
        strContent.append(lineBuffer.toString());
        return lineBuffer.toString().length();
    }

}
