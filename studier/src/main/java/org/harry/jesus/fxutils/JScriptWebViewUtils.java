package org.harry.jesus.fxutils;

import com.sun.webkit.WebPage;
import javafx.scene.web.WebEngine;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Field;

public class JScriptWebViewUtils {

    public static void highlight(WebEngine engine, String text) {
        try {
            Field pageField = engine.getClass().getDeclaredField("page");
            pageField.setAccessible(true);


            WebPage page = (com.sun.webkit.WebPage) pageField.get(engine);
            page.find(text, true, true, false);
        } catch(Exception ex) {
            Logger.trace(ex);
            Logger.trace("Highlight failes with: " + ex.getMessage());
        }
    }

    public static  void removeHighlight(WebEngine engine) {
        engine.executeScript("$('body').removeHighlight()");
    }
}
