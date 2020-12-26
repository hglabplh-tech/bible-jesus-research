package org.harry.jesus.jesajautils;

import generated.BIBLEBOOK;
import generated.CHAPTER;
import generated.VERS;
import generated.XMLBIBLE;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class HTMLRendering {

    public static StringBuffer buildVersHTML(BibleFulltextEngine.BibleTextKey link, String linkText, CHAPTER chapter) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(linkText + " ");
        getVersText(link.getVers(), chapter, buffer);
        return buffer;
    }

    private static void getVersText(Integer versNumber,CHAPTER chapter, StringBuffer buffer) {
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

    public static void noteToHTML(String noteText, StringBuffer buffer, StringBuffer htmlBuffer) {
        htmlBuffer.append("<div><p style=\"font-family:verdana\">")
                .append(buffer.toString())
                .append("</p><p style=\"font-family:verdana\">")
                .append(noteText)
                .append("</p></div>");
    }

    public static void renderVers(StringBuffer htmlBuffer, String buffer) {
        htmlBuffer.append("<p style=\"font-family:verdana\">")
                .append(buffer.toString())
                .append("</p>");
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