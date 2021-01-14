package org.harry.jesus.jesajautils;

import generated.*;
import javafx.scene.control.IndexRange;
import javafx.scene.paint.Color;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.fxutils.SettingsDialog;
import org.harry.jesus.jesajautils.BibleTextUtils.BookLink;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.harry.jesus.synchjeremia.ApplicationProperties;
import org.harry.jesus.synchjeremia.BibleThreadPool;


import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.*;

/**
 * This class is fr the rendering (setting styles to the whole text or to areas of the text)
 * of the text area for the displayed chapter
 * @author Harald Glab-Plhak
 */
public class TextRendering {

    /**
     * the text utils
     */
    private final BibleTextUtils bibleUtils;

    /**
     * The text area
     */
    private final FoldableStyledArea area;

    /**
     * The map holding the index ranges/verses for the text
     */
    private Map<Integer, IndexRange> chapterMap = new LinkedHashMap<>();

    /**
     * The map holds the style of the different verses
     */
    private Map<IndexRange, TextStyle> renderMap = new LinkedHashMap<>();

    /**
     * The list for the notes in the chapter
     */
    private List<String> notes = new ArrayList<>();

    private Integer lastPosINBuffer = 0;

    /**
     * The maximal width of the text
     */
    private static final int TEXT_WIDTH = 160;

    /**
     * The label of the actual bible book
     */
    private final String actBookLabel;

    /**
     * The book number
     */
    private final Integer actBookNo;

    /**
     * The actual displayed chapter
     */
    private final Integer actChapter;


    /**
     * The Ctor for the rendering
     * @param bibleUtils the text utils class instance
     * @param area the text area
     * @param actBookLabel the actual book label
     * @param actChapter the actual chapter
     */
    public TextRendering(BibleTextUtils bibleUtils,
                         FoldableStyledArea area,
                         String actBookLabel,
                         Integer actChapter) {
        this.bibleUtils = bibleUtils;
        this.area = area;
        this.actBookLabel = actBookLabel;
        String bookNo = actBookLabel.split(",")[0];
        this.actBookNo = Integer.parseInt(bookNo);
        this.actChapter = actChapter;
    }

    public Map<Integer, IndexRange> getChapterMap() {
        return Collections.unmodifiableMap(chapterMap);
    }
    /**
     * store the color for a vers
     * @param versList the list of verses affected
     * @param chosenColor the chosen color
     */
    public static void storeVersRendering(List<Vers> versList, Color chosenColor) {
        BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
        for (Vers actVers : versList) {
            Tuple<Integer, Integer> key =
                    new Tuple<Integer, Integer>(actVers.getBook().intValue(),
                            actVers.getChapter().intValue());
            Map<Integer, String> renderVersMap = new LinkedHashMap<>();
            Color color;
            if (chosenColor != null) {
                color = chosenColor;
            } else {
                color = Color.YELLOWGREEN;
            }
            for (BigInteger bigVersNo: actVers.getVers()) {
                renderVersMap.put(bigVersNo.intValue(), color.toString());

            }
            Map<Integer, String> temp = context.getRenderMap().get(key);
            if (temp != null) {
                temp.putAll(renderVersMap);
                context.addRenderElement(key, temp);
            } else {
                context.addRenderElement(key, renderVersMap);
            }

        }
    }

    public void showBibleInfo(XMLBIBLE bible) {
        StringBuffer textBuffer = new StringBuffer();
        textBuffer.append("Bible Book Information\n\n\n");
        IndexRange titleRange = new IndexRange(0, textBuffer.toString().length());
        INFORMATION info = (INFORMATION)bible.getINFORMATION().getValue();
        for (JAXBElement element : info.getTitleOrCreatorOrDescription()) {

            if (element.getName().getLocalPart().equals("title")) {
                
            } else if (element.getName().getLocalPart().equals("creator")) {
                textBuffer.append("\ncreator: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("description")) {
                textBuffer.append("\ndescription: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("publisher")) {
                textBuffer.append("\npublisher: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("date")) {
                textBuffer.append("\ndate: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("subject")) {
                textBuffer.append("\nsubject: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("contributors")) {
                textBuffer.append("\ncontributors: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("type")) {
                textBuffer.append("\ntype: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("identifier")) {
                textBuffer.append("\nidentifier: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("format")) {
                textBuffer.append("\nformat: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("source")) {
                textBuffer.append("\nsource: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("language")) {
                textBuffer.append("\nlanguage: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("coverage")) {
                textBuffer.append("\ncoverage: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("rights")) {
                textBuffer.append("\nrights: " + element.getValue());
            }
        }
        area.replaceText(textBuffer.toString());
        area.setStyle(titleRange.getStart(), titleRange.getEnd(), TextStyle.bold(true)
                .updateFontSize(14).updateItalic(true).updateFontFamily("Times New Roman"));
    }

    /**
     * clear the rendering map
     */
    public void clearRendering() {
        renderMap.clear();
    }

    /**
     * get the notes inside the chapter
     * @return the notes list
     */
    public List<String> getNotes() {
        return notes;
    }

    /**
     * render the links
     * @param bible the bible
     * @param links the links
     * @return the content returned
     */
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

    /**
     *
     * @param bible the bible
     * @param bookLabel the book label
     * @param chapterNo the chapter number
     * @return rendering ok ?
     */
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

    /**
     * This method selects a verse by the given ÄIndexRange in the text
     * @param range the range by which we search for the verse in the text
     * @param toggler
     * @return the selected the entry for the verse
     */
    public Map.Entry<Integer, IndexRange> selectVerseByGivenRange(IndexRange range, Boolean toggler) {
        int start = range.getStart();

        for (Map.Entry<Integer, IndexRange> entry: this.chapterMap.entrySet()) {
            IndexRange compRange = entry.getValue();
            if (start >= compRange.getStart() && start <= compRange.getEnd()) {
                refreshAreaStyle(area, compRange, toggler);
                return entry;
            }
        }
        return null;
    }

    /**
     * This method selects a verse by the given ÄIndexRange in the text
     * @param range the range by which we search for the verse in the text
     * @return the selected the entry for the verse
     */
    public Map.Entry<Integer, IndexRange> selectVerseColorByGivenRange(IndexRange range, Color color) {
        int start = range.getStart();

        for (Map.Entry<Integer, IndexRange> entry: this.chapterMap.entrySet()) {
            IndexRange compRange = entry.getValue();
            if (start >= compRange.getStart() && start <= compRange.getEnd()) {
                TextStyle style = this.area.getInitialTextStyle();
                style = style.updateBackgroundColor(color);
                this.area.setStyle(compRange.getStart(), compRange.getEnd(), style);
                return entry;
            }
        }
        return null;
    }

    /**
     * set the area text with it's styles
     * @param strContent the actual content
     */
    public void setAreaText(StringBuffer strContent) {
        area.replaceText(strContent.toString());
        refreshAreaStyle(area, null, false);
        for (Map.Entry<IndexRange, TextStyle> entry : renderMap.entrySet()) {
            IndexRange range = entry.getKey();
            TextStyle style = entry.getValue();
            mergeRangeStyle(this.area, style, range);
        }
        Tuple<Integer, Integer> key = new Tuple<>(actBookNo, actChapter);
        Map<Integer, String> value = BibleThreadPool.getContext().getRenderMap().get(key);
        if (value != null) {

            for (Map.Entry<Integer, String> versInfo : value.entrySet()) {
                IndexRange range = chapterMap.get(versInfo.getKey());
                setRangeColor(this.area, versInfo.getValue(), range);
            }
        }
    }

    public static void setRangeColor(FoldableStyledArea area, String color, IndexRange range) {
        Integer fontSize = ApplicationProperties.getFontSize();
        Optional<String> fontFamily = ApplicationProperties.getFontFamily();
        TextStyle appStyle = ApplicationProperties.getShape();
        TextStyle theStyle = TextStyle.backgroundColor(Color.web(color))
                .updateFontFamily(fontFamily.get())
                .updateTextColor(appStyle.getTextColor().get())
                .updateFontSize(fontSize);
        area.setStyle(range.getStart(), range.getEnd(), theStyle);
    }

    public static void mergeRangeStyle(FoldableStyledArea area, TextStyle style, IndexRange range) {
        Integer fontSize = ApplicationProperties.getFontSize();
        Optional<String> fontFamily = ApplicationProperties.getFontFamily();
        TextStyle appStyle = ApplicationProperties.getShape();
        TextStyle theStyle = style
                .updateFontFamily(fontFamily.get())
                .updateTextColor(appStyle.getTextColor().get())
                .updateFontSize(fontSize);
        area.setStyle(range.getStart(), range.getEnd(), theStyle);
    }

    public static void refreshAreaStyle(FoldableStyledArea textArea, IndexRange range, Boolean toggle) {
       Integer fontSize = ApplicationProperties.getFontSize();
       Optional<String> fontFamily = ApplicationProperties.getFontFamily();
       TextStyle theStyle = ApplicationProperties.getShape();
       if (range == null) {
           if (fontFamily.isPresent()) {
               SettingsDialog.setPreview(textArea, theStyle, fontFamily.get(), fontSize);
           }
       } else {
           TextStyle underLined = theStyle.updateUnderline(toggle);
           if (fontFamily.isPresent()) {
               SettingsDialog.setPreview(textArea, underLined, fontFamily.get(), fontSize, range);
           }
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
                    String cssString = styled.getCss();
                    for (Object styledContent : styled.getContent()) {
                        if (styledContent instanceof String) {
                            String text = (String) styledContent;
                            start = strContent.toString().length() - 1;
                            IndexRange range = new IndexRange(start, start + text.length() + 1);
                            TextStyle style = new TextStyle().fromCss(cssString);
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
                    this.lastPosINBuffer = block.substring(lastPos, pos).length() + (pos - lastPos);
                } else {
                    actLength = block.length();
                    pos = actLength;

                    strContent.append(block.substring(lastPos));
                    this.lastPosINBuffer = block.substring(lastPos).length() + (pos + 1);
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
