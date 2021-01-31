package org.harry.jesus.synchjeremia;

import javafx.application.Platform;
import org.harry.jesus.danielpersistence.PersistenceLayer;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.configjaxbser.AppSettingsPersistence;
import org.harry.jesus.jesajautils.configjaxbser.BaseConfig;
import org.harry.jesus.jesajautils.configjaxbser.BibleAppConfig;
import org.harry.jesus.jesajautils.graphicsjaxb.VerseImagePersistence;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.util.*;

/**
 * The type Synch thread.
 */
public class SynchThread extends TimerTask {

    /**
     * The constant APP_DIR.
     */
    public static final String APP_DIR = System.getProperty("user.home") + "/.bibleStudy";

    /**
     * The constant APP_TEST_DIR.
     */
    public static final String APP_TEST_DIR = System.getProperty("user.home") + "/.bibleStudyDevelop";

    private static final String VERSE_IMG_SUB = "verseimages";
    private static final String NOTES_XML = "notes.xml";

    private static final String HIGHLIGHT_XML = "highlight.xml";

    private static final String RENDER_OBJ = "render.obj";

    private static final String HISTORY_OBJ = "history.obj";

    private static final String SETTINGS_PROP = "application.properties";

    private static final String APP_SETTINGS_XML = "appSettings.xml";

    private static final String VERSE_IMAGES_XML = "verseImages.xml";

    private static final String VERSE_OFDAYS_XML = "verseOfDays.xml";

    /**
     * The constant appDir.
     */
    public  static final File appDir;

    /**
     * The constant verseImageDir.
     */
    public  static final File verseImageDir;

    private static final File renderObj;

    private static final File historyObj;

    private static final File notesXML;

    private static final File highlightsXML;

    /**
     * The constant appProps.
     */
    public  static final File appProps;

    /**
     * The constant appSettings.
     */
    public  static final File appSettings;

    /**
     * The constant verseImageXML.
     */
    public static final File verseImageXML;

    /**
     * Verse of Days XML storage
     */
    public static final File verseOfDaysXML;

    private static Timer timer = new Timer();



    static {
        String value = System.getenv("bibleStudyTest");
        if (value!= null) {
            appDir = new File(APP_TEST_DIR);
        } else {
            appDir = new File(APP_DIR);
        }
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        verseImageDir = new File(appDir, VERSE_IMG_SUB);
        if (!verseImageDir.exists()) {
            verseImageDir.mkdirs();
        }
        renderObj = new File(appDir, RENDER_OBJ);
        historyObj = new File(appDir, HISTORY_OBJ);
        notesXML = new File(appDir, NOTES_XML);
        highlightsXML = new File(appDir, HIGHLIGHT_XML);
        appProps = new File(appDir, SETTINGS_PROP);
        appSettings = new File(appDir, APP_SETTINGS_XML);
        verseImageXML = new File(appDir, VERSE_IMAGES_XML);
        verseOfDaysXML = new File(appDir, VERSE_OFDAYS_XML);
        timer.schedule (new SynchThread()
           , (long)(1000L * 120L), (long)(1000L * 60L));
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            public void run() {
                BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
                storeRendering(context);
                storeNotes(context);
                storeHighlights(context);
                //ApplicationProperties.storeApplicationProperties();
                storeApplicationSettings(context);
                try {
                    VerseImagePersistence.storeAppSettings(context.getVerseImages(),
                            new FileOutputStream(verseImageXML));
                } catch (FileNotFoundException ex) {
                    Logger.trace(ex);
                    Logger.trace("Verse Images definition not stored: " + ex.getMessage());
                }
            }



        });
    }

    /**
     * Store notes.
     *
     * @param context the context
     */
    public static void storeNotes(BibleThreadPool.ThreadBean context) {
        synchronized (SynchThread.class) {
            try {
                FileOutputStream stream = new FileOutputStream(notesXML);
                PersistenceLayer.storeNotes(context.getNoteList(), stream);

            } catch (IOException ex) {
                Logger.trace("Error storing rendering" + ex.getMessage());
            }
        }
    }

    /**
     * Load notes.
     *
     * @param context the context
     */
    public static void loadNotes(BibleThreadPool.ThreadBean context) {
        synchronized (SynchThread.class) {
            try {
                FileInputStream stream = new FileInputStream(notesXML);
                context.getNoteList().getVersenote().addAll(PersistenceLayer.loadNotes(stream).getVersenote());
            } catch (IOException ex) {
                Logger.trace("Error storing rendering" + ex.getMessage());
            }
        }
    }

    /**
     * Store highlights.
     *
     * @param context the context
     */
    public static void storeHighlights(BibleThreadPool.ThreadBean context) {
        synchronized (SynchThread.class) {
            try {
                FileOutputStream stream = new FileOutputStream(highlightsXML);
                PersistenceLayer.storeHighligts(context.getHighlights(), stream);
            } catch (IOException ex) {
                Logger.trace("Error storing rendering" + ex.getMessage());
            }
        }
    }

    /**
     * Load highlights.
     *
     * @param context the context
     */
    public static void loadHighlights(BibleThreadPool.ThreadBean context) {
        synchronized (SynchThread.class) {
            try {
                FileInputStream stream = new FileInputStream(highlightsXML);
                context.getHighlights().getHighlight().addAll(PersistenceLayer.loadHighlights(stream).getHighlight());
            } catch (IOException ex) {
                Logger.trace("Error storing rendering" + ex.getMessage());
            }
        }
    }


    /**
     * Store rendering.
     *
     * @param context the context
     */
    public static void storeRendering(BibleThreadPool.ThreadBean context) {
        synchronized(SynchThread.class)  {
            try {
                FileOutputStream stream = new FileOutputStream(renderObj);
                ObjectOutputStream objStream = new ObjectOutputStream(stream);
                objStream.writeObject(context.getRenderMap());
                objStream.close();
            } catch (IOException ex) {
                Logger.trace("Error storing rendering" + ex.getMessage());
            }
        }
    }

    /**
     * Load rendering.
     *
     * @param context the context
     */
    public static void loadRendering(BibleThreadPool.ThreadBean context) {
        synchronized(SynchThread.class)  {
            try {
                if (renderObj.exists()) {
                    FileInputStream stream = new FileInputStream(renderObj);
                    ObjectInputStream objStream = new ObjectInputStream(stream);
                    context.setRenderMap((Map<Tuple<Integer, Integer>, Map<Integer, String>>) objStream.readObject());
                    objStream.close();
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.trace("Error storing rendering" + ex.getMessage());
            }
        }
    }

    /**
     * Store history.
     *
     * @param context the context
     */
    public static void storeHistory(BibleThreadPool.ThreadBean context) {
        synchronized(SynchThread.class)  {
            try {
                FileOutputStream stream = new FileOutputStream(historyObj);
                ObjectOutputStream objStream = new ObjectOutputStream(stream);
                objStream.writeObject(context.getHistory());
                objStream.close();
            } catch (IOException ex) {
                Logger.trace("Error storing history" + ex.getMessage());
            }
        }
    }

    /**
     * Load history.
     *
     * @param context the context
     */
    public static void loadHistory(BibleThreadPool.ThreadBean context) {
        synchronized(SynchThread.class)  {
            try {
                if (historyObj.exists()) {
                    FileInputStream stream = new FileInputStream(historyObj);
                    ObjectInputStream objStream = new ObjectInputStream(stream);
                    context.getHistory().addAll((List<HistoryEntry>) objStream.readObject());
                    objStream.close();
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.trace("Error storing history" + ex.getMessage());
            }
        }
    }

    /**
     * Store application settings.
     *
     * @param context the context
     */
    public static void storeApplicationSettings(BibleThreadPool.ThreadBean context) {
        synchronized (SynchThread.class) {
            try {
                FileOutputStream stream = new FileOutputStream(appSettings);
                AppSettingsPersistence.storeAppSettings(context.getAppSettings(), stream);

            } catch (IOException ex) {
                Logger.trace("Error storing rendering" + ex.getMessage());
            }
        }
    }

    private static void convertFromOldProps(BibleThreadPool.ThreadBean context) {
        BaseConfig base = new BaseConfig();

        if (appProps.exists()) {
                    base.setBiblesDir(ApplicationProperties.getApplicationBiblesDir())
                    .setDictionariesDir(ApplicationProperties.getApplicationAccordanceDir())
                    .setFontFamily(ApplicationProperties.getFontFamily().get())
                    .setMediaPath(ApplicationProperties.getApplicationMediaDir())
                    .setReaderShape(BaseConfig.ShapeEnum.fromValue(ApplicationProperties.getShapeString()))
                    .setFontSize(ApplicationProperties.getFontSize());
            appProps.delete();
        } else {
            String basePath = System.getProperty("user.home") + "/bibleStudyApp";
            String biblePath = basePath + "/" + "installedBibles";
            String dictPath = basePath + "/" + "installedBibleDicts";
            Optional<String> optFont = ApplicationProperties.fontFamilies
                    .stream()
                    .filter(e -> e.contains("Tempus"))
                    .findFirst();
            base.setReaderShape(BaseConfig.ShapeEnum.BASE_SHAPE)
                    .setBiblesDir(biblePath)
                    .setDictionariesDir(dictPath)
                    .setMediaPath(basePath)
                    .setFontFamily(optFont.get())
                    .setFontSize(10);
        }
        BibleAppConfig config = new BibleAppConfig().setBaseConfig(base);
        context.setAppSettings(config);

    }

    /**
     * Load application settings.
     *
     * @param context the context
     */
    public static void loadApplicationSettings(BibleThreadPool.ThreadBean context) {
        synchronized (SynchThread.class) {
            try {
                convertFromOldProps(context);
                if (appSettings.exists()) {
                    FileInputStream stream = new FileInputStream(appSettings);
                    BibleAppConfig bibleAppConfig = AppSettingsPersistence.loadAppSettings(stream);
                    context.setAppSettings(bibleAppConfig);
                }
            } catch (IOException ex) {
                Logger.trace("Error storing rendering" + ex.getMessage());
            }
        }
    }

}
