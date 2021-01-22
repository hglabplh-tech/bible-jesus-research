package org.harry.jesus.fxutils;

import generated.*;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.Tuple;
import org.pmw.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

import static java.lang.String.*;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;


public class GenDictHTMLScene {

    public static void generateDictHTML(BibleTextUtils utils, File accordenceAppDir) {

        Stage stage = new Stage();
        stage.setTitle("Dictionary HTML Generation");
        VBox generationVBOX = new VBox();
        generationVBOX.setMinHeight(350);
        generationVBOX.setMinWidth(550);
        GridPane grid = new GridPane();
        Label title = new Label("Generate dictionaries progress:");
        Label progressText = new Label("");
        ProgressIndicator bar = new ProgressIndicator();
        grid.add(title, 0, 0);
        grid.add(bar, 0, 1);
        grid.add(progressText, 0, 2);

        Worker<Void> worker = new Worker<>(utils, accordenceAppDir);
        bar.progressProperty().bind(worker.progressProperty());
        progressText.textProperty().bind(worker.messageProperty());

        generationVBOX.setAlignment(Pos.CENTER);
        generationVBOX.getChildren().add(grid);

        Scene secondScene = new Scene(generationVBOX);
        stage.setScene(secondScene);
        stage.show();
        new Thread(worker).start();
    }

    public static class Worker<T> extends Task<T> {

        private final BibleTextUtils utils;

        private static final Integer UPDATE_INTERVALL = 30;

        private Integer updateCounter = 0;

        private final File accordenceAppDir;

        private String actDictionary = "";



        public Worker(BibleTextUtils utils, File accordenceAppDir) {
            this.utils = utils;
            this.accordenceAppDir = accordenceAppDir;
        }

        public Tuple<Long, StringBuffer> buildAccordance(BibleTextUtils utils, Dictionary accordance, long total, long done) {
            StringBuffer htmlBuffer = new StringBuffer();
            HTMLRendering.buildHead(htmlBuffer);
            buidAccordanceHeader(htmlBuffer, accordance);
            done = buildAccItemEntriesHTML(utils, htmlBuffer, accordance.getItem(), total, done);
            HTMLRendering.buildFoot(htmlBuffer);
            Logger.trace(htmlBuffer.toString());
            return new Tuple<>(done, htmlBuffer);
        }

        public void buidAccordanceHeader(StringBuffer htmlBuffer, Dictionary accordance) {
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

        public long buildAccItemEntriesHTML(BibleTextUtils utils,
                                            StringBuffer htmlBuffer,
                                            List<TItem> items, long total, long done) {
            int index = 0;
            for (TItem item : items) {
                if (item.getId() != null) {
                    htmlBuffer.append("<H5 id=\"" +
                            item.getId()
                            + "\">"
                            + escapeHtml4(item.getId())
                            + "</H5>\n");
                }
                if (item.getStrongId() != null) {
                    htmlBuffer.append("<H5 id=\"" +
                            item.getStrongId()
                            + "\">"
                            + escapeHtml4(item.getStrongId())
                            + "</H5>\n");
                }
                List<Serializable> objects = item.getContent();
                for (Serializable object : objects) {
                    if (object instanceof JAXBElement) {
                        JAXBElement jaxbElement = (JAXBElement) object;
                        if (((JAXBElement) object).getName().getLocalPart().equals("description")) {
                            TParagraph thisElenent = (TParagraph) (((JAXBElement) object).getValue());
                            buildAccParagraphEntryHTML(utils, htmlBuffer, thisElenent);
                        } else if (jaxbElement.getName().getLocalPart().equals("title")) {
                            MyAnyType title = (MyAnyType) jaxbElement.getValue();
                            List<Serializable> contents = title.getContent();
                            StringBuffer titleBuffer = new StringBuffer();
                            for (Serializable cont : contents) {
                                if (cont instanceof String) {
                                    titleBuffer.append((String) cont);
                                }
                            }
                            htmlBuffer.append("<p> Title: " + escapeHtml4(titleBuffer.toString()) + "</p>\n");
                        } else if (((JAXBElement) object).getName().getLocalPart().equals("reflink")) {
                            RefLinkType refLink = (RefLinkType) (((JAXBElement) object).getValue());
                            setRefLinkToBuffer(utils, htmlBuffer, refLink);
                        }




                    }
                }
                index++;
                Logger.trace("Accordance Item No: " + index + " processed");
                done = done+ 1;
                updateGenDictProgress(done, total);
            }
            return done;
        }

        public void buildAccParagraphEntryHTML(BibleTextUtils utils,
                                               StringBuffer htmlBuffer,
                                               TParagraph paragraph) {
            StringBuffer tempBuffer = new StringBuffer();
            int indexHyper = 0;
            String idString = null;
            if (paragraph.getId() != null) {
                idString = "<H6 id=\"" +
                        paragraph.getId()
                        + "\">"
                        + escapeHtml4(paragraph.getId())
                        + "</H6>\n";
            }
            List<Serializable> objects = paragraph.getContent();
            String title = null;
            for (Serializable pContent : paragraph.getContent()) {
                if (pContent instanceof JAXBElement) {

                    JAXBElement jaxbElement = (JAXBElement) pContent;
                    Object thisContent = ((JAXBElement) pContent).getValue();
                    if (jaxbElement.getName().getLocalPart().equals("title")) {
                        indexHyper = 0;
                        String titleCont = (String) thisContent;
                        title = "<p>description title: "
                                + escapeHtml4(titleCont)
                                + "</p>\n";
                    } else if (jaxbElement.getName().getLocalPart().equals("see")) {
                        indexHyper = 0;
                        SeeType see = (SeeType) jaxbElement.getValue();
                        buildSee(see, tempBuffer);
                    } else if (thisContent instanceof BibLinkType) {
                        indexHyper = 0;
                        BibLinkType bibleLink = (BibLinkType) thisContent;
                        Logger.trace("BibLink: ["
                                + bibleLink.getBn() + ","
                                + bibleLink.getCn1() + ","
                                + bibleLink.getVn1() + "]\n");
                    } else if (thisContent instanceof RefLinkType) {
                        if (indexHyper == 0) {
                            tempBuffer.append("<p>");
                        } else if ((indexHyper % 5) == 0) {
                            tempBuffer.append("<br>");
                        } else {
                            tempBuffer.append(" , ");
                        }

                        RefLinkType refLink = (RefLinkType) thisContent;
                        setRefLinkToBuffer(utils, tempBuffer, refLink);
                        indexHyper++;
                    }


                } else if (pContent instanceof String) {
                    tempBuffer.append(pContent);
                }
            }
            if (idString != null) {
                htmlBuffer.append(idString);
            }
            if (title != null) {
                htmlBuffer.append(title);
            }
            htmlBuffer.append(tempBuffer.toString());
        }

        private void setRefLinkToBuffer(BibleTextUtils utils, StringBuffer htmlBuffer, RefLinkType refLink) {
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

        public void buildSee(SeeType see, StringBuffer htmlBuffer) {
            String target = see.getTarget();
            if (target.equals("x-self")) {
                htmlBuffer.append("<p>See: <A href=\"http://_self/#"
                        + see.getContent()
                        + "\">"
                        + see.getContent()
                        + "</A></p>\n");
            }
        }

        public void createAccBibleLink(BibleTextUtils utils, StringBuffer htmlBuffer,
                                       String mScope) {
            String[] parts = mScope.split(";");
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
                HTMLRendering.generateHyperLink(htmlBuffer, theFinal);
            } else {
                htmlBuffer.append("#target#");
            }


        }

        @Override
        protected T call() throws Exception {
            T dummy = null;
            long total = 0;
            long done = 0;
            for (BibleTextUtils.DictionaryInstance dictInstance: utils.getDictInstances()) {
                total = total + dictInstance.getDictionary().getItem().size();
            }
            for (BibleTextUtils.DictionaryInstance dictInstance: utils.getDictInstances()) {
                actDictionary = dictInstance.getDictionaryRef().getDictionaryName();
                done = genDictionary(dictInstance, total, done);
            }
            updateProgress(total, total);
            setLabelText(total, total);
            return dummy;
        }


        public long genDictionary(BibleTextUtils.DictionaryInstance dictInstance, long total, long done) {
            String fileName = dictInstance.getDictionaryRef().getFilename() + ".html";
            int part = dictInstance.getDictionary().getItem().size();
            File outFile = new File(accordenceAppDir, fileName);
            if (outFile.exists()) {
                done = done + part;
                updateGenDictProgress(done, total);
                return done;
            }
            Logger.trace("Start build accordance file: " + outFile.getAbsolutePath());
            Tuple<Long, StringBuffer> genResult = buildAccordance(utils, dictInstance.getDictionary(), total, done);
            done = genResult.getFirst();
            StringBuffer html = genResult.getSecond();
            updateGenDictProgress(done, total);
            try {
                FileOutputStream outStream = new FileOutputStream(outFile);
                StringReader reader = new StringReader(html.toString());
                IOUtils.copy(reader, outStream, Charset.defaultCharset());
                Logger.trace("Ready build accordance file: " + outFile.getAbsolutePath());
                setLabelWriteSuccessText(done, total);
            } catch (IOException ex) {
                setLabelWriteFailedText(done, total);
                Logger.trace(ex);
                Logger.trace("Write: \""
                        + outFile.getAbsolutePath()
                        + "\" failed with: "
                        + ex.getMessage());
            }
            return done;

        }

        private void updateGenDictProgress(long done, long total) {
            if (updateCounter >= UPDATE_INTERVALL) {
                updateProgress(done, total);
                setLabelText(done, total);
                updateCounter = 0;
            } else {
                updateCounter++;
            }
        }

        private void setLabelText(long done, long total) {
            String text = format("Progress (done / total) -> (%d / %d)\n" +
                    "generating: %s", done,total, actDictionary);
            updateMessage(text);
        }

        private void setLabelWriteSuccessText(long done, long total) {
            String text = format("Progress (done / total) -> (%d / %d)\n" +
                    "writing: %s (success)", done,total, actDictionary);
            updateMessage(text);
        }

        private void setLabelWriteFailedText(long done, long total) {
            String text = format("Progress (done / total) -> (%d / %d)\n" +
                    "writing: %s (failed)", done,total, actDictionary);
            updateMessage(text);
        }
    }

}
