package org.harry.jesus.jesajautils;

import generated.*;
import javafx.scene.paint.Color;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.fxutils.LinkHandler;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;

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
                " <style>\n" +
                        "         @media screen\n" +
                        "         {\n" +
                        "             p {font-size: 1.0em;\n" +
                        "                 font-family: \"Times New Roman\";\n" +
                        "                 fill:azure\n" +
                        "             }\n" +
                        "             a {\n" +
                        "                 font-size: 0.9em;\n" +
                        "                 font-feature-settings: revert;\n" +
                        "                 font-family: \"Courier New\";\n" +
                        "                 fill:green;\n" +
                        "             }\n" +
                        "             h5 {\n" +
                        "                 font-size: 1.2em;\n" +
                        "                 font-family: \"Arial Rounded MT Bold\";\n" +
                        "                 fill:blueviolet;\n" +
                        "             }\n" +
                        "             h6 {\n" +
                        "                 font-size: 1.0em;\n" +
                        "                 font-family: \"Britannic Bold\";\n" +
                        "                 fill:blueviolet;\n" +
                        "             }\n" +
                        "         }\n" +
                        "\n" +
                        "         @media print\n" +
                        "         {\n" +
                        "             p {font-size: 13pt;\n" +
                        "                 font-family: \"Times New Roman\";\n" +
                        "                 fill: azure;\n" +
                        "             }\n" +
                        "         }\n" +
                        "     </style>" +
                "<meta charset=\"utf-8\"/>\n" +
                "     <script>\n" +
                "         function scrollTo(elementId) {\n" +
                "             document.getElementById(elementId).scrollIntoView();\n" +
                "         }\n" +
                "     </script>\n" +
                " </head>\n<body>\n");
    }

    /**
     * Build foot.
     *
     * @param htmlBuffer the html buffer
     */
    public static void buildFoot(StringBuffer htmlBuffer) {
        htmlBuffer.append("</body>\n" +
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
        String id = BibleDictUtil
                .getIdFromBibleInfo(
                        bible.getBible().getINFORMATION()
                                .getValue());
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
                                    link.getChapter(), versNo);
                    Map.Entry<BibleFulltextEngine.BibleTextKey, String> mapEntry =
                            utils.getVersEntry(jaxbChapter.getValue(), key);
                    String versText = mapEntry.getValue();
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

        buildHead(htmlContent);
        Integer start = 0;

        htmlContent.append("<h4>"+ links.get(0).toString() + " whole chapter</h4>");
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
                        new BibleFulltextEngine.BibleTextKey(bookNo, chapterNo, versNo);
                Map.Entry<BibleFulltextEngine.BibleTextKey, String> mapEntry =
                        utils.getVersEntry(jaxbChapter.getValue(), key);
                String versText = mapEntry.getValue();
                htmlContent.append("<p/>");
                htmlContent.append("(" + Integer.toString(versNo) + ")<br>");
                if (links.get(0).getVerses().contains(versNo)) {
                    renderVers(htmlContent, versText, Color.GREEN);
                } else {
                    renderVers(htmlContent, versText, null);
                }



            }
        }

        buildFoot(htmlContent);
        return htmlContent.toString();
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
                    .append(buffer.toString())
                    .append("</span></p><hr>");
        } else {
            htmlBuffer.append("<hr><p><span style=\"background-color:#ddd;\" style=\"background-color:"
                    + color.toString().replace("0x", "#") + ";font-size: small; font-family: &quot;Times New Roman&quot;;\">")
                    .append(buffer.toString())
                    .append("</span></p><hr>");
        }
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
}
