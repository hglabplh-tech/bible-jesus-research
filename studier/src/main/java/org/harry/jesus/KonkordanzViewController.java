package org.harry.jesus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.codefx.libfx.control.webview.WebViews;
import org.harry.jesus.fxutils.KonkordanzViewHyperListener;
import org.harry.jesus.fxutils.WebViewHyperListener;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.browse.FoldableStyledArea;

import javax.swing.event.HyperlinkEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class KonkordanzViewController {

    @FXML
    WebView konkordanzViewer;

    @FXML
    public void initialize() {


        try {
            InputStream stream = this.getClass().getResourceAsStream("/hreftest/href-test.html");
            if (stream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int read = stream.read(buffer);
                while (read > -1) {
                    outStream.write(buffer, 0, read);
                    read = stream.read(buffer);
                }
                String htmlString = new String(outStream.toByteArray(), Charset.defaultCharset());
                outStream.close();
                stream.close();
                System.out.println(htmlString);
                WebEngine engine = konkordanzViewer.getEngine();
                engine.setJavaScriptEnabled(true);
                engine.loadContent(htmlString);


            }

        } catch (IOException ex ) {

        }
    }

    public void setWebViewListener(BibleTextUtils utils, FoldableStyledArea area) {
        WebViews.addHyperlinkListener(konkordanzViewer,
                new KonkordanzViewHyperListener(utils, area, konkordanzViewer), HyperlinkEvent.EventType.ACTIVATED);
    }

    public void closeWin(ActionEvent event) {

    }

    public static class Controller {

    }
}
