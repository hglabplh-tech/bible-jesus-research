package org.harry.jesus.jesajautils;

import generated.*;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.pmw.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HTMLRendering {

    public static StringBuffer buildAccordance(BibleTextUtils utils, Dictionary accordance) {
        StringBuffer htmlBuffer = new StringBuffer();
        buildAccHead(htmlBuffer);
        buildAccItemEntriesHTML(utils, htmlBuffer, accordance.getItem());
        buildAccFoot(htmlBuffer);
        Logger.trace(htmlBuffer.toString());
        return htmlBuffer;
    }

    public static void buildAccHead(StringBuffer htmlBuffer) {
        htmlBuffer.append("<html>\n" +
                " <head>\n" +
                "     <script>\n" +
                "         function scrollTo(elementId) {\n" +
                "             document.getElementById(elementId).scrollIntoView();\n" +
                "         }\n" +
                "     </script>\n" +
                " </head>\n<body>\n");
    }

    public static void buildAccFoot(StringBuffer htmlBuffer) {
        htmlBuffer.append("</body>\n" +
                "</html>");
    }

    public static void buildAccItemEntriesHTML(BibleTextUtils utils,
                                               StringBuffer htmlBuffer,
                                               List<TItem> items) {
        int index = 0;
        for (TItem item: items) {
            htmlBuffer.append("<<H5 id=\"" +
                    item.getId()
                    +  "\">"
                    + item.getId()
                    + "</H5>\n");
            List<Serializable> objects = item.getContent();
            for (Serializable object : objects) {
                if (object instanceof JAXBElement) {
                    JAXBElement jaxbElement = (JAXBElement) object;
                    if (((JAXBElement) object).getName().getLocalPart().equals("description")) {
                        TParagraph thisElenent = (TParagraph) (((JAXBElement) object).getValue());
                        buildAccParagraphEntryHTML(utils, htmlBuffer, thisElenent);
                    } else if (jaxbElement.getName().getLocalPart().equals("title")) {
                            MyAnyType title = (MyAnyType)jaxbElement.getValue();
                            List<Serializable> contents = title.getContent();
                            StringBuffer titleBuffer = new StringBuffer();
                            for (Serializable cont : contents) {
                                if (cont instanceof String) {
                                    titleBuffer.append((String)cont);
                                }
                            }
                            htmlBuffer.append("<p> Title: " + titleBuffer.toString() + "</p>\n");
                    }

                }
            }
            index++;
            Logger.trace("Accordance Item No: " + index + " processed");
        }
    }

    public static void buildAccParagraphEntryHTML(BibleTextUtils utils,
                                                  StringBuffer htmlBuffer,
                                                  TParagraph paragraph) {
        List<Serializable> objects = paragraph.getContent();
        for  (Serializable pContent: paragraph.getContent()) {
            if (pContent instanceof JAXBElement) {
                JAXBElement jaxbElement = (JAXBElement)pContent;
                Object thisContent = ((JAXBElement)pContent).getValue();
                if (jaxbElement.getName().getLocalPart().equals("title")) {
                    String title = (String) thisContent;
                    htmlBuffer.append("<p>description title: "
                            + title
                            + "</p>\n"
                    );
                } else if (jaxbElement.getName().getLocalPart().equals("see")) {
                    SeeType see = (SeeType)jaxbElement.getValue();

                } else if (thisContent instanceof BibLinkType) {
                    BibLinkType bibleLink = (BibLinkType) thisContent;
                    Logger.trace("BibLink: ["
                            + bibleLink.getBn() + ","
                            + bibleLink.getCn1() + ","
                            + bibleLink.getVn1() + "]\n");
                } else if (thisContent instanceof RefLinkType){
                    RefLinkType refLink = (RefLinkType) thisContent;
                    String mScope = refLink.getMscope();
                    createAccBibleLink(utils, htmlBuffer, mScope);
                } else if (thisContent instanceof String) {
                    Logger.trace("String content: " + thisContent);
                }
            }
        }

    }

    public static void buildSee(SeeType see, StringBuffer htmlBuffer) {
        String target = see.getTarget();
        if (target.equals("x-self")) {
            htmlBuffer.append("<p><A href=\"http://_self/#"
                    + see.getContent()
                    + "\">"
                    + see.getContent()
                    + "</A></p>\n");
        }
    }

    public static void createAccBibleLink(BibleTextUtils utils, StringBuffer htmlBuffer,
                                            String mScope) {
        String [] parts = mScope.split(";");
        Integer book = Integer.parseInt(parts[0]);
        Integer chapter = Integer.parseInt(parts[1]);
        Integer verse = Integer.parseInt(parts[2]);
        BibleTextUtils.BookLabel link  = utils.getBookLabMap().get(book);
        String longName = link.getLongName();
        String theFinal = "["
                + longName
                + " "
                + chapter.toString()
                + "," + verse.toString()
                + "]";
        generateHyperLink(htmlBuffer, theFinal);
        htmlBuffer.append('\n');

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
                    generateHyperLink(htmlContent, "[" + labelClass.getLongName()
                            + " " + link.getChapter()
                            + "," + versNo
                            + "]");
                    renderVers(htmlContent, versText);


                }
            }
        }
        return htmlContent.toString();
    }




    private static String generateHyperLink(StringBuffer buffer, String link) {
        String href = "http://bible/"+ link;
        href = href.replace(",", "/vers/");
        href = href.replace(" ", "\\");
        href = href.replace("[", "opstart");
        href = href.replace("]", "opend");

        buffer.append("<p><a href=\"" + href + "\">" + link + "</a></p>");
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
                String linkText = LinkHandler.buildVersLinkEnhanced(utils, book.getBnumber().intValue(),
                        chapter.get().getCnumber().intValue(), vers.getVers());
                renderVers(buffer, linkText);
                renderVers(buffer, vers.getVtext());
            }
        }
        return buffer.toString();
    }


}
