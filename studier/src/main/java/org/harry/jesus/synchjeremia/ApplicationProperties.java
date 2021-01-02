package org.harry.jesus.synchjeremia;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.pmw.tinylog.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ApplicationProperties {
    public static final List<String> fontFamilies = Font.getFamilies();
    public static final TextStyle baseShape = TextStyle.backgroundColor(Color.WHITE)
            .updateTextColor(Color.BLACK);
    public static final TextStyle blackShape = TextStyle.backgroundColor(Color.BLACK)
            .updateTextColor(Color.WHITE);
    public static final TextStyle oldDosShape = TextStyle.backgroundColor(Color.BLACK)
            .updateTextColor(Color.LIGHTGREEN);
    public static final TextStyle heikeShape = TextStyle.backgroundColor(Color.RED)
            .updateTextColor(Color.WHITE);
    public static final TextStyle joshiShape = TextStyle.backgroundColor(Color.WHITE)
            .updateTextColor(Color.BLUE);
    public static final TextStyle harryShape = TextStyle.backgroundColor(Color.LIGHTGREY)
            .updateTextColor(Color.BLACK);
    public static final String AUDIO_PATH = "com.harry.jesus.apath";
    public static final String BIBLE_XML_PATH = "com.harry.jesus.biblespath";
    public static final String APP_FONT = "com.harry.jesus.font";
    public static final String APP_FONT_SIZE = "com.harry.jesus.fontsize";
    public static final String APP_SHAPE = "com.harry.jesus.shape";

    private  static Map<String, TextStyle> shapeMap = new LinkedHashMap<>();


    static {
      shapeMap.put("Base Shape",baseShape);
      shapeMap.put("Black Shape",blackShape);
      shapeMap.put("Old Dos Shape",oldDosShape);
      shapeMap.put("Heike's Shape",heikeShape);
      shapeMap.put("Joshi's Shape",joshiShape);
      shapeMap.put("Harry's Shape",harryShape);
    }

    public static void storeApplicationProperties() {
        try {

            getSettings().store(new FileOutputStream(SynchThread.appProps)
                    , "Bible Study Properties");

        } catch (IOException ex) {
            Logger.trace(ex);
            Logger.trace("Error storing properties: " + ex.getMessage());
        }
    }

    public static void loadApplicationProperties() {
        try {
            if (SynchThread.appProps.exists()) {
                getSettings().load(new FileInputStream(SynchThread.appProps));
            }
        } catch (IOException  ex) {
            Logger.trace(ex);
            Logger.trace("Error loading properties: " + ex.getMessage());
        }
    }

    private static Properties getSettings () {
        return BibleThreadPool.getContext().getSettings();
    }

    public static Map<String, TextStyle> getShapeMap() {
        return shapeMap;
    }

    public static String getApplicationBiblesDir() {
        return getSettings().getProperty(BIBLE_XML_PATH,
                System.getProperty("user.home") + "/bibleStudyXmlPath");
    }

    public static String getApplicationMediaDir() {
        return getSettings().getProperty(AUDIO_PATH,
                System.getProperty("user.home") + "/bibleStudyAudio");
    }

    public static void setApplicationBiblesDir(String dir) {
        getSettings().setProperty(BIBLE_XML_PATH, dir);
    }

    public static void setApplicationMediaDir(String dir) {
        getSettings().setProperty(AUDIO_PATH, dir);
    }

    public static Optional<String> getFontFamily() {
        Optional<String> optFont = ApplicationProperties.fontFamilies
                .stream()
                .filter(e -> e.contains("Tempus"))
                .findFirst();
        if (optFont.isPresent()) {
            return Optional.of(getSettings().getProperty(APP_FONT, optFont.get()));
        }
        return Optional.empty();
    }

    public static void setFontFamily(String font) {
        getSettings().setProperty(APP_FONT, font);
    }

    public static void setFontSize(Integer fontSize) {
        getSettings().setProperty(APP_FONT_SIZE, fontSize.toString());
    }

    public static void setShape(String shape) {
        getSettings().setProperty(APP_SHAPE, shape);
    }


    public static Integer getFontSize() {
        return Integer
                .parseInt(getSettings().getProperty(APP_FONT_SIZE, "10"));
    }

    public static TextStyle getShape() {
        String shapeString = getSettings().getProperty(APP_SHAPE, "Base Shape");
        return getShapeMap().get(shapeString);
    }

    public static String getShapeString() {
        String shapeString = getSettings().getProperty(APP_SHAPE, "Base Shape");
        return shapeString;
    }
}
