package org.harry.jesus.jesajautils;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.VERS;
import generated.XMLBIBLE;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HTMLRendering {

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
                    generateHyperLink(htmlContent, "[" + link.getBookLabel()
                            + link.getChapter()
                            + "," + versNo
                            + "]");
                    renderVers(htmlContent, versText);


                }
            }
        }
        return htmlContent.toString();
    }




    private static String generateHyperLink(StringBuffer buffer, String link) {
        buffer.append("<p><a href=\"" + link + "\">" + link + "</a></p>");
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

    public static void renderVers(StringBuffer htmlBuffer, String buffer) {
        htmlBuffer.append("<hr><p><span style=\"font-size: small; font-family: &quot;Times New Roman&quot;;\">")
                .append(buffer.toString())
                .append("</span></p><hr>");
    }

    public static String renderVersesASDoc(XMLBIBLE selected, BibleTextUtils utils, List<Vers> verses) {
        StringBuffer buffer = new StringBuffer().append("<html><title>Some selected verses</title><body>");
        buffer.append(renderVerses(selected, utils, verses));
        buffer.append("</body></html>");
        return buffer.toString();
    }

    public static String renderVerses(XMLBIBLE selected, BibleTextUtils utils, List<Vers> verses) {
        StringBuffer buffer = new StringBuffer();
        for (Vers vers: verses) {
            BIBLEBOOK book = utils.getBooks(selected).get(vers.getBook().intValue() - 1);
            Optional<CHAPTER> chapter = utils.getChapter(book, vers.getChapter().intValue());
            if (chapter.isPresent()) {
                String linkText = BibleTextUtils.buildVersLinkEnhanced(utils, book.getBnumber().intValue(),
                        chapter.get().getCnumber().intValue(), vers.getVers());
                renderVers(buffer, linkText);
                renderVers(buffer, vers.getVtext());
            }
        }
        return buffer.toString();
    }


}
