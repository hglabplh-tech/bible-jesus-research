package org.harry.jesus.fxutils.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import org.harry.jesus.jesajautils.HTMLRendering;

import javax.swing.*;

public class HTMLEditorExt  {

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
                    String hyperlinkHtml = HTMLRendering.generateHyperLink(buffer, selected.trim());
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



}
