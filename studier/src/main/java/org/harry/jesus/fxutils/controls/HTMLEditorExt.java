package org.harry.jesus.fxutils.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import org.harry.jesus.fxutils.LinkHandler;
import org.harry.jesus.fxutils.graphics.ImageMaker;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.LinkDetector;

import javax.swing.*;
import java.util.Base64;
import java.util.List;

/**
 * The type Html editor ext.
 */
public class HTMLEditorExt  {

    /**
     * Instantiates a new Html editor ext.
     *
     * @param htmlEditor the html editor
     */
    public HTMLEditorExt(HTMLEditor htmlEditor) {
        Node node = htmlEditor.lookup(".top-toolbar");
        if (node instanceof ToolBar) {
            ToolBar bar = (ToolBar) node;
            Button bibleButton = new Button("Bible Link");
            bibleButton.setTooltip(new javafx.scene.control.Tooltip("Bible link"));
            bibleButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    WebView view = (WebView) htmlEditor.lookup("WebView");
                    String selected = (String) view.getEngine().executeScript("window.getSelection().toString();");
                    StringBuffer buffer = new StringBuffer();
                    List<BibleTextUtils.BookLink> links = LinkDetector
                            .parseLinks(BibleTextUtils.getInstance(), selected.trim());
                    String hyperlinkHtml = LinkHandler.generateHyperLink(buffer, links.get(0).getBookLabelClass().getBookNumber(),
                            links.get(0).getChapter(),
                            links.get(0).getVerses().get(0));
                    view.getEngine().executeScript(getInsertHtmlAtCursorJS(hyperlinkHtml));
                }
            });
            Button hyperButton = new Button("Hyperlink");
            hyperButton.setTooltip(new javafx.scene.control.Tooltip("Hyperlink"));
            hyperButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    String url = JOptionPane.showInputDialog("Enter Bible Link");
                    WebView view = (WebView) htmlEditor.lookup("WebView");
                    String selected = (String) view.getEngine().executeScript("window.getSelection().toString();");
                    StringBuffer buffer = new StringBuffer();
                    String hyperlinkHtml =  "<a href=\"" + url.trim() +
                            "\" title=\"" + selected +
                            "\" target=\"_blank\">" +
                            selected + "</a>";
                    view.getEngine().executeScript(getInsertHtmlAtCursorJS(hyperlinkHtml));
                }
            });
            bar.getItems().add(bar.getItems().size(), bibleButton);
            bar.getItems().add(bar.getItems().size(), hyperButton);
            addInsertImage(htmlEditor);

        }
    }



    private String getInsertHtmlAtCursorJS(String html){
        return "insertHtmlAtCursor('" + html + "');"
                + "function insertHtmlAtCursor(html) {\n"
                + " var range, node;\n"
                + " if (window.getSelection && window.getSelection().getRangeAt) {\n"
                + " window.getSelection().deleteFromDocument();\n"
                + " range = window.getSelection().getRangeAt(0);\n"
                + " node = range.createContextualFragment(html);\n"
                + " range.insertNode(node);\n"
                + " } else if (document.selection && document.selection.createRange) {\n"
                + " document.selection.createRange().pasteHTML(html);\n"
                + " document.selection.clear();"
                + " }\n"
                + "}";
    }

    private void addInsertImage (HTMLEditor htmlEditor) {
        Node toolNode = htmlEditor.lookup(".top-toolbar");
        Node webNode = htmlEditor.lookup(".web-view");
        if (toolNode instanceof ToolBar && webNode instanceof WebView) {
        ToolBar bar = (ToolBar) toolNode;
        WebView webView = (WebView) webNode;
        WebEngine engine = webView.getEngine();

        Button btnCaretAddImage = new Button("add an image");
        btnCaretAddImage.setMinSize(100.0, 24.0);
        btnCaretAddImage.setMaxSize(100.0, 24.0);

        bar.getItems().addAll(btnCaretAddImage);

        //data uri image

        //http://stackoverflow.com/questions/2213376/how-to-find-cursor-position-in-a-contenteditable-div

        btnCaretAddImage.setOnAction((ActionEvent event) -> {
            try {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasImage()) {
                    pasteImage(engine, clipboard);
                } else if (clipboard.hasHtml()) {
                    String htmlString = clipboard.getHtml().toString();
                    engine.executeScript(getInsertHtmlAtCursorJS(htmlString));
                } else if (clipboard.hasString()) {
                    String text = "<br>" + clipboard.getString() + "<br>";
                    engine.executeScript(getInsertHtmlAtCursorJS(text));
                }
            } catch (JSException e) {
                // A JavaScript Exception Occured
            }
        });
    }


    }

    private void pasteImage(WebEngine engine, Clipboard clipboard) {
        Image image = (Image) clipboard.getContent(DataFormat.IMAGE);

        byte[] imgBytes = ImageMaker.getBytesFromImage(image);
        if (imgBytes != null) {
            String imgBase64 = Base64.getEncoder().encodeToString(imgBytes);
            String img =
                    "<img alt=\"Embedded Image\" src=\"data:image/png;base64," + imgBase64 + "\" />";
            engine.executeScript(getInsertHtmlAtCursorJS("####html####").
                    replace("####html####",
                            escapeJavaStyleString(img, true, true)));
        }
    }

    private static String hex(int i) {
        return Integer.toHexString(i);
    }

    //a method to convert to a javas/js style string
    //https://commons.apache.org/proper/commons-lang/javadocs/api-2.6/src-html/org/apache/commons/lang/StringEscapeUtils.html

    private static String escapeJavaStyleString(String str,
                                                boolean escapeSingleQuote, boolean escapeForwardSlash) {
        StringBuilder out = new StringBuilder("");
        if (str == null) {
            return null;
        }
        int sz;
        sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);

            // handle unicode
            if (ch > 0xfff) {
                out.append("\\u").append(hex(ch));
            } else if (ch > 0xff) {
                out.append("\\u0").append(hex(ch));
            } else if (ch > 0x7f) {
                out.append("\\u00").append(hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                    case '\b':
                        out.append('\\');
                        out.append('b');
                        break;
                    case '\n':
                        out.append('\\');
                        out.append('n');
                        break;
                    case '\t':
                        out.append('\\');
                        out.append('t');
                        break;
                    case '\f':
                        out.append('\\');
                        out.append('f');
                        break;
                    case '\r':
                        out.append('\\');
                        out.append('r');
                        break;
                    default:
                        if (ch > 0xf) {
                            out.append("\\u00").append(hex(ch));
                        } else {
                            out.append("\\u000").append(hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'':
                        if (escapeSingleQuote) {
                            out.append('\\');
                        }
                        out.append('\'');
                        break;
                    case '"':
                        out.append('\\');
                        out.append('"');
                        break;
                    case '\\':
                        out.append('\\');
                        out.append('\\');
                        break;
                    case '/':
                        if (escapeForwardSlash) {
                            out.append('\\');
                        }
                        out.append('/');
                        break;
                    default:
                        out.append(ch);
                        break;
                }
            }
        }
        return out.toString();
    }



}
