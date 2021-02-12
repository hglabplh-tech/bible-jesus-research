package org.harry.jesus.jesajautils;

import generated.*;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.fxutils.LinkHandler;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.harry.jesus.jesajautils.httpSrv.BibleHTTPSrv;
import org.harry.jesus.jesajautils.judaerrmsg.BibleStudyException;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.*;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

/**
 * The type Html rendering.
 */
public class HTMLRendering {


    /**
     * Build head.
     *
     * @param htmlBuffer the html buffer
     */
    public static void buildHead(StringBuffer htmlBuffer) {
        htmlBuffer.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                " <head>\n" +
                "<style>" +
                ".sidenav {\n" +
                        "  height: 100%; \n" +
                        "  width: 25%; \n" +
                        "  position: fixed; \n" +
                        "  z-index: 1; \n" +
                        "  top: 0; \n" +
                        "  left: 0;\n" +
                        "  background-color: #111; \n" +
                        "  overflow-x: hidden; \n" +
                        "  padding-top: 60px; \n" +
                        "  transition: 0.5s; \n" +
                        "}\n" +
                        ".sidenav a {\n" +
                        "  padding: 8px 8px 8px 32px;\n" +
                        "  text-decoration: none;\n" +
                        "  font-size: 25px;\n" +
                        "  color: #818181;\n" +
                        "  display: block;\n" +
                        "  transition: 0.3s;\n" +
                        "}\n" +
                        ".sidenav a:hover {\n" +
                        "  color: #f1f1f1;\n" +
                        "}\n" +
                        ".sidenav .closebtn {\n" +
                        "  position: absolute;\n" +
                        "  top: 0;\n" +
                        "  right: 25px;\n" +
                        "  font-size: 36px;\n" +
                        "  margin-left: 50px;\n" +
                        "}\n" +
                        ".main {  \n" +
                        " height: 100%; \n" +
                        "  width: 70%; \n" +
                        "  position: fixed; \n" +
                        "  z-index: 1; \n" +
                        "  top: 0; \n" +
                        "  left: 28%;\n" +
                        "  background-color: #fff; \n" +
                        "  overflow-x: hidden; \n" +
                        "  padding-top: 60px; \n" +
                        "  transition: 0.5s; \n" +
                        "}" +
                "</style>" +
                "<meta charset=\"utf-8\"/>\n" +
                "     <script>\n" +
                "         function scrollTo(elementId) {\n" +
                "             document.getElementById(elementId).scrollIntoView();\n" +
                "         }\n" +
                "     </script>\n");
        htmlBuffer.append("</head>\n<body>");
        addNavToBuffer(htmlBuffer);
        htmlBuffer.append("<div class=\"main\">\n");
        htmlBuffer.append("\n");
    }

    public static void addNavToBuffer(StringBuffer htmlBuffer) {
        htmlBuffer.append("<div class=\"sidenav\"><ul>");
        htmlBuffer.append("<li aria-current=\"page\">" +
                "<a href=\"http://localhost:" +
                        BibleHTTPSrv.PORT +
                "/searchInput\">Search</a></li>");
        htmlBuffer.append("<li><a href=\"#\">Bible</a> <ul>");
        buildBooksTree(htmlBuffer);
        htmlBuffer.append("</ul></li>");
        htmlBuffer.append("</ul></div>");
    }

    public static void  buildBooksTree(StringBuffer htmlBuffer) {
        BibleTextUtils utils = BibleTextUtils.getInstance();
        if (utils.getSelected() != null) {
            TreeItem<String> root = new TreeItem<>();
            root.setValue("The books");
            List<BIBLEBOOK> theBooks = utils.getBooks(utils.getSelected());

            for (BIBLEBOOK theBook : theBooks) {
                if (theBook.getBnumber().intValue() >= 1 && theBook.getBnumber().intValue() <= 66) {
                    String labelString = utils.getBookLabels().get(theBook.getBnumber().intValue() - 1);
                    BibleTextUtils.BookLabel label = utils.getBookLabelAsClass(labelString);
                    for (JAXBElement<CHAPTER> chapter : theBook.getCHAPTER()) {
                        Integer chapterNo = chapter.getValue().getCnumber().intValue();
                        htmlBuffer.append("<li>\n");
                        LinkHandler.generateHyperChapterLink(htmlBuffer, label.getBookNumber(), chapterNo);
                        htmlBuffer.append("</li>\n");
                    }
                }

            }
        }
    }


    /**
     * Build foot.
     *
     * @param htmlBuffer the html buffer
     */
    public static void buildFoot(StringBuffer htmlBuffer) {
        htmlBuffer.append("</div></body>\n" +
                "</html>");
    }

    /**
     * Build vers html string buffer.
     *
     * @param link     the link
     * @param linkText the link text
     * @param chapter  the chapter
     * @return the string buffer
     */
    public static StringBuffer buildVersHTML(BibleFulltextEngine.BibleTextKey link, String linkText, CHAPTER chapter) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(linkText + " ");
        getVersText(link.getVers(), chapter, buffer);
        return buffer;
    }

    /**
     * Gets vers text.
     *
     * @param versNumber the vers number
     * @param chapter    the chapter
     * @param buffer     the buffer
     */
    public static void getVersText(Integer versNumber,CHAPTER chapter, StringBuffer buffer) {
        for (Object obj: chapter.getPROLOGOrCAPTIONOrVERS()) {
            Object thing = ((JAXBElement)obj).getValue();
            if (thing instanceof VERS) {
                VERS vers = (VERS)thing;
                if (vers.getVnumber().intValue() == versNumber) {
                    for (Object object : vers.getContent()) {
                        if (object instanceof String) {
                            buffer.append((String)object);
                        }
                    }
                }
            }
        }
    }

    private static void putTitle(StringBuffer buffer, BibleTextUtils.BibleBookInstance bible) {
        String title = BibleDictUtil
                .getTitleFromBibleInfo(
                        bible.getBible().getINFORMATION()
                                .getValue());
        String id = getActBibleId(bible.getBible());
        buffer.append("<p>" + title + "("+ id + ")</p>");
    }

    /**
     * Render link string.
     *
     * @param utils the utils
     * @param bible the bible
     * @param links the links
     * @return the string
     */
    public static String renderLink(BibleTextUtils utils, XMLBIBLE bible,
                                                List<BibleTextUtils.BookLink> links) {
        StringBuffer htmlContent = new StringBuffer();

        Integer start = 0;
        for (BibleTextUtils.BookLink link:links) {
            Optional<BIBLEBOOK> book = utils.getBookByLabel(bible, link.getBookLabel());
            if (book.isPresent()) {
                Vers vers = new Vers();
                vers.setBook(book.get().getBnumber());
                vers.setChapter(BigInteger.valueOf(link.getChapter()));
                for (Integer versNo: link.getVerses()) {
                    vers.getVers().add(BigInteger.valueOf(versNo));
                    JAXBElement<CHAPTER> jaxbChapter = book.get().getCHAPTER().get(link.getChapter() - 1);
                    BibleFulltextEngine.BibleTextKey key =
                            new BibleFulltextEngine.BibleTextKey(book.get().getBnumber().intValue(),
                                    link.getChapter(), versNo, null);
                    BibleFulltextEngine.BibleTextKey mapEntry =
                            utils.getVersEntry(jaxbChapter.getValue(), key);
                    String versText = mapEntry.getVerseText();
                    vers.setVtext(versText);
                    BibleTextUtils.BookLabel labelClass = new BibleTextUtils.BookLabel(link.getBookLabel());
                    htmlContent.append("<p>");
                    LinkHandler.generateHyperLink(htmlContent,
                            link.getBookLabelClass().getBookNumber(),
                            link.getChapter(),
                            versNo);

                    htmlContent.append("</p>");
                    renderVers(htmlContent, versText, null);


                }
            }
        }
        return htmlContent.toString();
    }

    public static String renderException(Exception ex) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><body><hr><p><span style=\"font-size: small; font-family: &quot;Times New Roman&quot;;\">");
        if (ex instanceof BibleStudyException) {
            buffer.append(((BibleStudyException) ex).getMessageHTML());
        } else {
            buffer.append(ex.getMessage());
            StackTraceElement[] elements = ex.getStackTrace();
            putStackTraceToBuffer(buffer, elements, true);
        }
        buffer.append("</span></p><hr></body></html>");
        return buffer.toString();
    }

    /**
     * Render full chapter string.
     *
     * @param utils the utils
     * @param bible the bible
     * @param links the links
     * @return the string
     */
    public static String renderFullChapter(BibleTextUtils utils, XMLBIBLE bible,
                                    List<BibleTextUtils.BookLink> links) {
        StringBuffer htmlContent = new StringBuffer();

        XMLBIBLE saveBib = utils.getSelected();
        utils.setSelected(bible);
        buildHead(htmlContent);
        Integer start = 0;
        htmlContent.append("<div class=\"main\"><h4>"+ links.get(0).toString() + " whole chapter</h4>");
        Optional<BIBLEBOOK> book = utils.getBookByLabel(bible, links.get(0).getBookLabel());
        if (book.isPresent()) {
            JAXBElement<CHAPTER> jaxbChapter = book.get().getCHAPTER().get(
                    links.get(0).getChapter() - 1);
            List<Integer> versesNoList = new ArrayList<>();
            for (Object obj : jaxbChapter.getValue().getPROLOGOrCAPTIONOrVERS()) {
                Object value = ((JAXBElement)obj).getValue();
                if (value instanceof VERS) {
                    Integer temp = ((VERS)value).getVnumber().intValue();
                    versesNoList.add(temp);
                }
            }
            for  (Integer versNo: versesNoList) {
                Integer bookNo = links.get(0).getBookLabelClass().getBookNumber();
                Integer chapterNo  = links.get(0).getChapter();
                BibleFulltextEngine.BibleTextKey key =
                        new BibleFulltextEngine.BibleTextKey(bookNo, chapterNo, versNo, null);
                htmlContent.append("<br>");
                htmlContent.append("(" + Integer.toString(versNo) + ")<br>");
                String bibleId = getActBibleId(BibleTextUtils.getInstance()
                        .getSelected());
                if (links.get(0).getVerses().contains(versNo)) {
                    renderVersNew(htmlContent, jaxbChapter.getValue(), bibleId, key, Color.GREEN, true);
                } else {
                    renderVersNew(htmlContent, jaxbChapter.getValue(), bibleId, key, null, true);
                }
            }
        }

        htmlContent.append("<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br></div>");
        buildFoot(htmlContent);
        utils.setSelected(saveBib);
        return htmlContent.toString();
    }

    private static String getActBibleId(XMLBIBLE selected) {
        return BibleDictUtil
                .getIdFromBibleInfo(
                        selected
                                .getINFORMATION()
                                .getValue());
    }

    /**
     * Build heading.
     *
     * @param htmlBuffer the html buffer
     * @param idText     the id text
     */
    public static void buildHeading(StringBuffer htmlBuffer, String idText) {
        htmlBuffer.append("<H5 id=\"" +
                idText
                + "\">("
                + escapeHtml4(idText)
                + ")</H5>\n");
    }


    /**
     * Note to html.
     *
     * @param noteText   the note text
     * @param buffer     the buffer
     * @param htmlBuffer the html buffer
     */
    public static void noteToHTML(String noteText, StringBuffer buffer, StringBuffer htmlBuffer) {
        htmlBuffer.append("<hr><p><span style=\"font-size: small; font-family: &quot;Times New Roman&quot;;\">")
                .append(buffer.toString())
                .append("</span></p><hr>")
                .append("<hr><p><span style=\"font-size: small; font-family: &quot;Times New Roman&quot;;\">")
                .append(noteText)
                .append("</p><hr>");
    }

    /**
     * Render vers.
     *
     * @param htmlBuffer the html buffer
     * @param buffer     the buffer
     * @param color      the color
     */
    public static void renderVers(StringBuffer htmlBuffer, String buffer, Color color) {
        if (color == null) {
            htmlBuffer.append("<hr><p><span style=\"font-size: small; font-family: &quot;Times New Roman&quot;;\">")
                    .append(escapeHtml4(buffer.toString()))
                    .append("</span></p><hr>");
        } else {
            htmlBuffer.append("<hr><p><span style=\"background-color:#ddd;\" style=\"background-color:"
                    + color.toString().replace("0x", "#") + ";font-size: small; font-family: &quot;Times New Roman&quot;;\">")
                    .append(escapeHtml4(buffer.toString()))
                    .append("</span></p><hr>");
        }
    }

    /**
     * render a verse with our parser
     * @param htmlBuffer the buffer containing html
     * @param chapter the chapter
     * @param bibleId the bible id
     * @param key the verse key
     * @param color the optional color
     * @param getAll get also captions and prologs
     */
    public static void renderVersNew(StringBuffer htmlBuffer,
                                     CHAPTER chapter,
                                     String bibleId,
                                     BibleFulltextEngine.BibleTextKey key,
                                     Color color, Boolean getAll) {
        final boolean[] caption = {false};
        StringBuffer buffer = new StringBuffer();
        BibleRenderParser parser = new BibleRenderParser(new RenderParserCallback() {
            @Override
            public boolean textCallBack(String text) {
                buffer.append(escapeHtml4(text));
                return false;
            }

            @Override
            public boolean gramCallback(String prefix, GRAM gram, String text) {
                String grString = gram.getStr();
                grString = prefix + grString;
                buffer.append(text);
                String rmac = gram.getRmac();
                buffer.append(" <a href=\""
                        + buildDictLink(bibleId, grString)
                        + "\">[" + grString + "]</a> ");
                return false;
            }

            @Override
            public boolean styleCallback(STYLE style, StringBuffer collected) {
                buffer.append("<span style=\"" + style.getCss() + "\">" + collected.toString() + "</span>");
                return false;
            }

            @Override
            public boolean beforeNoteCallback(NOTE note, Integer verseNo) {
                TNotesFix type = note.getType();
                buffer.append(escapeHtml4("Note{"+ type.value() + "} = ("));
                return false;
            }

            @Override
            public boolean afterNoteCallback(NOTE note, Integer verseNo) {
                buffer.append(")");
                return false;
            }

            @Override
            public boolean beforeDivCallback(DIV div, Integer verseNo) {
                //buffer.append("<div>");
                return false;
            }

            @Override
            public boolean afterDivCallback(DIV div, Integer verseNo) {
                //buffer.append("</div>");
                return false;
            }

            @Override
            public boolean breakCallback(BreakType type, Integer count) {
                String breakString = "<br>";
                switch(type) {
                    case X_NL:
                        breakString = "<br>";
                        break;
                    case X_P:
                        breakString = "<p/>";
                        break;
                }
                if (count != null) {
                    for (Integer index = 0 ; index <= count; index++) {
                        buffer.append(breakString);
                    }
                } else {
                    buffer.append(breakString);
                }
                return false;
            }

            @Override
            public boolean startCaption(CaptionType captionType) {
                caption[0] = true;
                String hx = getCaptionTypeHTMLTag(captionType);
                buffer.append("<" + hx + ">");
                return false;
            }

            @Override
            public boolean endCaption(CaptionType captionType) {
                String hx = getCaptionTypeHTMLTag(captionType);
                buffer.append("</" + hx + ">");
                return false;
            }
        });
        parser.getVersORCaptionOrEntry(chapter, key, getAll);
        if (color == null && !caption[0]) {
            htmlBuffer.append("<hr><p><span style=\"font-size: small; font-family: &quot;Times New Roman&quot;;\">");
        } else if (color != null && !caption[0]){
            htmlBuffer.append("<hr><p><span style=\"background-color:#ddd;\" style=\"background-color:"
                    + color
                    .toString()
                    .replace("0x", "#")
                    + ";font-size: small; font-family: &quot;Times New Roman&quot;;\">");
        }
        htmlBuffer.append(buffer.toString());
        if (!caption[0]) {
            htmlBuffer.append("</span></p><hr>");
        }
    }

    @NotNull
    private static String buildDictLink(String bibleId, String grString) {
        return "http://localhost:"
                + BibleHTTPSrv.PORT
                + "/dictionary?bible="
                + bibleId + "#" + grString;
    }

    @NotNull
    private static String getCaptionTypeHTMLTag(CaptionType captionType) {
        String hx = "H1";
        switch(captionType) {
            case X_H_1:
                hx = "H1";
                break;
            case X_H_2:
                hx = "H2";
                break;
            case X_H_3:
                hx = "H3";
                break;
            case X_H_4:
                hx = "H4";
                break;
            case X_H_5:
                hx = "H5";
                break;
            case X_H_6:
                hx = "H6";
                break;
        }
        return hx;
    }

    /**
     * Render verses as doc string.
     *
     * @param selected the selected
     * @param utils    the utils
     * @param verses   the verses
     * @param noteText the note text
     * @return the string
     */
    public static String renderVersesASDoc(XMLBIBLE selected,
                                           BibleTextUtils utils, List<Vers> verses, String noteText) {
        StringBuffer buffer = new StringBuffer().append("<html><title>Some selected verses</title><body>");
        buffer.append(renderVerses(selected, utils, verses));
        if (noteText != null) {
            renderVers(buffer, noteText, null);
        }
        buffer.append("</body></html>");
        return buffer.toString();
    }

    /**
     * Render verses string.
     *
     * @param selected the selected
     * @param utils    the utils
     * @param verses   the verses
     * @return the string
     */
    public static String renderVerses(XMLBIBLE selected, BibleTextUtils utils, List<Vers> verses) {
        StringBuffer buffer = new StringBuffer();
        for (Vers vers: verses) {
            BIBLEBOOK book = utils.getBooks(selected).get(vers.getBook().intValue() - 1);
            Optional<CHAPTER> chapter = utils.getChapter(book, vers.getChapter().intValue());
            if (chapter.isPresent()) {
                String linkText = LinkDetector.buildVersLinkEnhanced(utils, book.getBnumber().intValue(),
                        chapter.get().getCnumber().intValue(), vers.getVers());
                renderVers(buffer, linkText, null);
                renderVers(buffer, vers.getVtext(), null);
            }
        }
        return buffer.toString();
    }


    /**
     * Render link for compare string.
     *
     * @param links the links
     * @return the string
     */
    public static String renderLinkForCompare(List<BibleTextUtils.BookLink> links) {
        StringBuffer buffer = new StringBuffer();
        for (BibleTextUtils.BookLink link:links) {
            for (BibleTextUtils.BibleBookInstance bible :
                    BibleTextUtils.getInstance().getBibleInstances()) {
                BibleTextUtils.getInstance().setSelected(bible.getBible());
                putTitle(buffer, bible);
                buffer.append(renderLink(
                        BibleTextUtils.getInstance(),
                        bible.getBible(),
                        Arrays.asList(link)));
            }
        }
        return buffer.toString();
    }

    public static void putStackTraceToBuffer(StringBuffer buffer, StackTraceElement[] elements, Boolean htmlText) {
        int index = 0;
        for (StackTraceElement element: elements) {
            if (index < 7) {
                if (htmlText) {
                    buffer.append("<br>");
                } else {
                    buffer.append("\n");
                }
                String eleString;
                if (htmlText) {
                    eleString = escapeHtml4(element.toString());
                } else {
                    eleString = element.toString();
                }
                buffer.append(eleString);
            }
            index++;
        }
    }
}
