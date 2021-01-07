package org.harry.jesus.synchjeremia;

import javafx.application.Platform;
import org.harry.jesus.danielpersistence.PersistenceLayer;
import org.harry.jesus.jesajautils.Tuple;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SynchThread extends TimerTask {

    private static final String APP_DIR = System.getProperty("user.home") + "/.bibleStudy";

    private static final String NOTES_XML = "notes.xml";

    private static final String HIGHLIGHT_XML = "highlight.xml";

    private static final String RENDER_OBJ = "render.obj";

    private static final String HISTORY_OBJ = "history.obj";

    private static final String SETTINGS_PROP = "application.properties";

    private static final File appDir;

    private static final File renderObj;

    private static final File historyObj;

    private static final File notesXML;

    private static final File highlightsXML;

    public  static final File appProps;

    private static Timer timer = new Timer();



    static {
        appDir = new File(APP_DIR);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        renderObj = new File(appDir, RENDER_OBJ);
        historyObj = new File(appDir, HISTORY_OBJ);
        notesXML = new File(appDir, NOTES_XML);
        highlightsXML = new File(appDir, HIGHLIGHT_XML);
        appProps = new File(appDir, SETTINGS_PROP);
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
                ApplicationProperties.storeApplicationProperties();
            }



        });
    }

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

}
