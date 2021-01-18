package org.harry.jesus.jesajautils;

import generated.*;
import javafx.scene.paint.Color;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

public class HTMLRendering {


    public static void buildHead(StringBuffer htmlBuffer) {
        htmlBuffer.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                " <head>\n" +
                "<meta charset=\"utf-8\"/>\n" +
                "     <script>\n" +
                "         function scrollTo(elementId) {\n" +
                "             document.getElementById(elementId).scrollIntoView();\n" +
                "         }\n" +
                "     </script>\n" +
                " </head>\n<body>\n");
    }

    public static void buildFoot(StringBuffer htmlBuffer) {
        htmlBuffer.append("</body>\n" +
                "</html>");
    }

    public static StringBuffer buildVersHTML(BibleFulltextEngine.BibleTextKey link, String linkText, CHAPTER chapter) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(linkText + " ");
        getVersText(link.getVers(), chapter, buffer);
        return buffer;
    }

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
                    generateHyperLink(htmlContent, "[" + labelClass.getLongName()
                            + " " + link.getChapter()
                            + "," + versNo
                            + "]");
                    htmlContent.append("</p>");
                    renderVers(htmlContent, versText, null);


                }
            }
        }
        return htmlContent.toString();
    }

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

    public static void buildHeading(StringBuffer htmlBuffer, String idText) {
        htmlBuffer.append("<H5 id=\"" +
                idText
                + "\">("
                + escapeHtml4(idText)
                + ")</H5>\n");
    }

    public static void buildLinkInternal(String link, StringBuffer htmlBuffer) {
            htmlBuffer.append("http://_self/#"
                    + link);
    }



    public static String generateHyperLink(StringBuffer buffer, String link) {
        String href = "http://bible/"+ link;
        href = href.replace(",", "/vers/");
        href = href.replace(" ", "\\");
        href = href.replace("[", "opstart");
        href = href.replace("]", "opend");

        buffer.append("<a href=\"" + href + "\">" + link + "</a>");
        return buffer.toString();
    }




    public static void noteToHTML(String noteText, StringBuffer buffer, StringBuffer htmlBuffer) {
        htmlBuffer.append("<hr><p><span style=\"font-size: small; font-family: &quot;Times New Roman&quot;;\">")
                .append(buffer.toString())
                .append("</span></p><hr>")
                .append("<hr><p><span style=\"font-size: small; font-family: &quot;Times New Roman&quot;;\">")
                .append(noteText)
                .append("</p><hr>");
    }

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

    public static String renderVerses(XMLBIBLE selected, BibleTextUtils utils, List<Vers> verses) {
        StringBuffer buffer = new StringBuffer();
        for (Vers vers: verses) {
            BIBLEBOOK book = utils.getBooks(selected).get(vers.getBook().intValue() - 1);
            Optional<CHAPTER> chapter = utils.getChapter(book, vers.getChapter().intValue());
            if (chapter.isPresent()) {
                String linkText = LinkHandler.buildVersLinkEnhanced(utils, book.getBnumber().intValue(),
                        chapter.get().getCnumber().intValue(), vers.getVers());
                renderVers(buffer, linkText, null);
                renderVers(buffer, vers.getVtext(), null);
            }
        }
        return buffer.toString();
    }


}
