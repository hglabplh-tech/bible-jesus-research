package org.harry.jesus.synchjeremia;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.harry.jesus.jesajautils.browse.TextStyle;
import org.pmw.tinylog.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * The type Application properties.
 */
public class ApplicationProperties {
    /**
     * The constant fontFamilies.
     */
    public static final List<String> fontFamilies = Font.getFamilies();
    /**
     * The constant baseShape.
     */
    public static final TextStyle baseShape = TextStyle.backgroundColor(Color.WHITE)
            .updateTextColor(Color.BLACK);
    /**
     * The constant blackShape.
     */
    public static final TextStyle blackShape = TextStyle.backgroundColor(Color.BLACK)
            .updateTextColor(Color.WHITE);
    /**
     * The constant oldDosShape.
     */
    public static final TextStyle oldDosShape = TextStyle.backgroundColor(Color.BLACK)
            .updateTextColor(Color.LIGHTGREEN);
    /**
     * The constant heikeShape.
     */
    public static final TextStyle heikeShape = TextStyle.backgroundColor(Color.RED)
            .updateTextColor(Color.WHITE);
    /**
     * The constant joshiShape.
     */
    public static final TextStyle joshiShape = TextStyle.backgroundColor(Color.WHITE)
            .updateTextColor(Color.DARKBLUE);
    /**
     * The constant harryShape.
     */
    public static final TextStyle harryShape = TextStyle.backgroundColor(Color.LIGHTGREY)
            .updateTextColor(Color.BLACK);
    /**
     * The constant AUDIO_PATH.
     */
    public static final String AUDIO_PATH = "com.harry.jesus.apath";
    /**
     * The constant BIBLE_XML_PATH.
     */
    public static final String BIBLE_XML_PATH = "com.harry.jesus.biblespath";
    /**
     * The constant ACCORDANCE_XML_PATH.
     */
    public static final String ACCORDANCE_XML_PATH = "com.harry.jesus.accordancepath";
    /**
     * The constant APP_FONT.
     */
    public static final String APP_FONT = "com.harry.jesus.font";
    /**
     * The constant APP_FONT_SIZE.
     */
    public static final String APP_FONT_SIZE = "com.harry.jesus.fontsize";
    /**
     * The constant APP_SHAPE.
     */
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

    /**
     * Store application properties.
     */
    public static void storeApplicationProperties() {
        try {

            getSettings().store(new FileOutputStream(SynchThread.appProps)
                    , "Bible Study Properties");

        } catch (IOException ex) {
            Logger.trace(ex);
            Logger.trace("Error storing properties: " + ex.getMessage());
        }
    }

    /**
     * Load application properties.
     */
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

    /**
     * Gets shape map.
     *
     * @return the shape map
     */
    public static Map<String, TextStyle> getShapeMap() {
        return shapeMap;
    }

    /**
     * Gets application bibles dir.
     *
     * @return the application bibles dir
     */
    public static String getApplicationBiblesDir() {
        return getSettings().getProperty(BIBLE_XML_PATH,
                System.getProperty("user.home") + "/bibleStudyXmlPath");
    }

    /**
     * Gets application accordance dir.
     *
     * @return the application accordance dir
     */
    public static String getApplicationAccordanceDir() {
        return getSettings().getProperty(ACCORDANCE_XML_PATH,
                System.getProperty("user.home") + "/bibleStudyXmlPath/accordance");
    }

    /**
     * Gets application media dir.
     *
     * @return the application media dir
     */
    public static String getApplicationMediaDir() {
        return getSettings().getProperty(AUDIO_PATH,
                System.getProperty("user.home") + "/bibleStudyAudio");
    }

    /**
     * Sets application bibles dir.
     *
     * @param dir the dir
     */
    public static void setApplicationBiblesDir(String dir) {
        getSettings().setProperty(BIBLE_XML_PATH, dir);
    }

    /**
     * Sets application accordance dir.
     *
     * @param dir the dir
     */
    public static void setApplicationAccordanceDir(String dir) {
        getSettings().setProperty(ACCORDANCE_XML_PATH, dir);
    }


    /**
     * Sets application media dir.
     *
     * @param dir the dir
     */
    public static void setApplicationMediaDir(String dir) {
        getSettings().setProperty(AUDIO_PATH, dir);
    }

    /**
     * Gets font family.
     *
     * @return the font family
     */
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

    /**
     * Sets font family.
     *
     * @param font the font
     */
    public static void setFontFamily(String font) {
        getSettings().setProperty(APP_FONT, font);
    }

    /**
     * Sets font size.
     *
     * @param fontSize the font size
     */
    public static void setFontSize(Integer fontSize) {
        getSettings().setProperty(APP_FONT_SIZE, fontSize.toString());
    }

    /**
     * Sets shape.
     *
     * @param shape the shape
     */
    public static void setShape(String shape) {
        getSettings().setProperty(APP_SHAPE, shape);
    }


    /**
     * Gets font size.
     *
     * @return the font size
     */
    public static Integer getFontSize() {
        return Integer
                .parseInt(getSettings().getProperty(APP_FONT_SIZE, "10"));
    }

    /**
     * Gets shape.
     *
     * @return the shape
     */
    public static TextStyle getShape() {
        String shapeString = getSettings().getProperty(APP_SHAPE, "Base Shape");
        return getShapeMap().get(shapeString);
    }

    /**
     * Gets shape string.
     *
     * @return the shape string
     */
    public static String getShapeString() {
        String shapeString = getSettings().getProperty(APP_SHAPE, "Base Shape");
        return shapeString;
    }
}
