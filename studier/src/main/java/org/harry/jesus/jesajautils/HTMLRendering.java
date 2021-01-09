package org.harry.jesus.jesajautils;

import generated.*;
import jesus.harry.org.versnotes._1.Vers;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.pmw.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HTMLRendering {

    public static StringBuffer buildAccordance(BibleTextUtils utils, Dictionary accordance) {
        StringBuffer htmlBuffer = new StringBuffer();
        buildAccHead(htmlBuffer);
        buidAccordanceHeader(htmlBuffer, accordance);
        buildAccItemEntriesHTML(utils, htmlBuffer, accordance.getItem());
        buildAccFoot(htmlBuffer);
        Logger.trace(htmlBuffer.toString());
        return htmlBuffer;
    }

    public static void buidAccordanceHeader(StringBuffer htmlBuffer, Dictionary accordance) {
        htmlBuffer.append("<H1 id=\"header\">Dictionary</H1><hr><p><span style=\"font-size: small; font-family: &quot;Times New Roman&quot;;\">");
        TINFORMATION dictionaryInfo = accordance.getINFORMATION();
        for (JAXBElement element : dictionaryInfo.getTitleOrCreatorOrDescription()) {

            if (element.getName().getLocalPart().equals("title")) {
                htmlBuffer.append("<br>title: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("creator")) {
                htmlBuffer.append("<br>creator: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("description")) {
                htmlBuffer.append("<br>description: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("publisher")) {
                htmlBuffer.append("<br>publisher: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("date")) {
                htmlBuffer.append("<br>date: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("subject")) {
                htmlBuffer.append("<br>subject: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("contributors")) {
                htmlBuffer.append("<br>contributors: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("type")) {
                htmlBuffer.append("<br>type: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("identifier")) {
                htmlBuffer.append("<br>identifier: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("format")) {
                htmlBuffer.append("<br>format: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("source")) {
                htmlBuffer.append("<br>source: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("language")) {
                htmlBuffer.append("<br>language: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("coverage")) {
                htmlBuffer.append("<br>coverage: " + element.getValue());
            } else if (element.getName().getLocalPart().equals("rights")) {
                htmlBuffer.append("<br>rights: " + element.getValue());
            }
        }
        htmlBuffer.append("</span></p><hr>");
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
            htmlBuffer.append("<H5 id=\"" +
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
                    }  else if (((JAXBElement) object).getName().getLocalPart().equals("reflink")) {
                        RefLinkType refLink = (RefLinkType) (((JAXBElement) object).getValue());
                        setRefLinkToBuffer(utils, htmlBuffer, refLink);
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
        int indexHyper = 0;
        List<Serializable> objects = paragraph.getContent();
        for  (Serializable pContent: paragraph.getContent()) {
            if (pContent instanceof JAXBElement) {

                JAXBElement jaxbElement = (JAXBElement) pContent;
                Object thisContent = ((JAXBElement) pContent).getValue();
                if (jaxbElement.getName().getLocalPart().equals("title")) {
                    indexHyper = 0;
                    String title = (String) thisContent;
                    htmlBuffer.append("<p>description title: "
                            + title
                            + "</p>\n"
                    );
                } else if (jaxbElement.getName().getLocalPart().equals("see")) {
                    indexHyper = 0;
                    SeeType see = (SeeType) jaxbElement.getValue();
                    buildSee(see, htmlBuffer);
                } else if (thisContent instanceof BibLinkType) {
                    indexHyper = 0;
                    BibLinkType bibleLink = (BibLinkType) thisContent;
                    Logger.trace("BibLink: ["
                            + bibleLink.getBn() + ","
                            + bibleLink.getCn1() + ","
                            + bibleLink.getVn1() + "]\n");
                } else if (thisContent instanceof RefLinkType) {
                    if (indexHyper == 0) {
                        htmlBuffer.append("<p>");
                    } else if ((indexHyper % 5) == 0) {
                        htmlBuffer.append("<br>");
                    } else {
                        htmlBuffer.append(" , ");
                    }

                    RefLinkType refLink = (RefLinkType) thisContent;
                    setRefLinkToBuffer(utils, htmlBuffer, refLink);
                    indexHyper++;
                }

            } else if (pContent instanceof String) {
                htmlBuffer.append(pContent);
            }
        }

    }

    private static void setRefLinkToBuffer(BibleTextUtils utils, StringBuffer htmlBuffer, RefLinkType refLink) {
        String mScope = refLink.getMscope();
        if (mScope != null && !mScope.isEmpty()) {
            createAccBibleLink(utils, htmlBuffer, mScope);
        }
        String target = refLink.getTarget();
        if (target != null && !target.isEmpty()) {
            createAccBibleLink(utils, htmlBuffer, target);
        }
        String content = refLink.getContent();
        if (content != null && !content.isEmpty()) {
            htmlBuffer.append("<br>" + content);
        }
    }

    public static void buildSee(SeeType see, StringBuffer htmlBuffer) {
        String target = see.getTarget();
        if (target.equals("x-self")) {
            htmlBuffer.append("<p>See: <A href=\"http://_self/#"
                    + see.getContent()
                    + "\">"
                    + see.getContent()
                    + "</A></p>\n");
        }
    }

    public static void createAccBibleLink(BibleTextUtils utils, StringBuffer htmlBuffer,
                                            String mScope) {
        String [] parts = mScope.split(";");
        if (parts.length > 1) {
            Integer book = Integer.parseInt(parts[0]);
            String chapter = parts[1];
            String verse = "1";
            if (parts.length == 3) {
                verse = parts[2];
            }
            BibleTextUtils.BookLabel link = utils.getBookLabMap().get(book);
            String longName = link.getLongName();
            String theFinal = "["
                    + longName
                    + " "
                    + chapter.toString()
                    + "," + verse
                    + "]";
            generateHyperLink(htmlBuffer, theFinal);
        } else {
            htmlBuffer.append("#target#");
        }


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
                    renderVers(htmlContent, versText);


                }
            }
        }
        return htmlContent.toString();
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
