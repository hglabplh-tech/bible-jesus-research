package org.harry.jesus.fxutils;

import com.sun.javafx.webkit.Accessor;
import com.sun.webkit.WebPage;
import javafx.scene.web.WebEngine;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Field;

public class JScriptWebViewUtils {

    public static void findString(WebEngine engine, String text) {
        try {
            WebPage page = Accessor.getPageFor(engine);
            page.find(text, true, true, false);
        } catch(Exception ex) {
            Logger.trace(ex);
            Logger.trace("Highlight failes with: " + ex.getMessage());
        }
    }
}
