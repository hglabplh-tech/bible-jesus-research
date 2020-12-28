package org.harry.jesus.synchjeremia;

import javafx.application.Platform;
import javafx.scene.paint.Color;
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

    private static final File appDir;

    private static final File renderObj;

    private static Timer timer = new Timer();



    static {
        appDir = new File(APP_DIR);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        renderObj = new File(appDir, RENDER_OBJ);
        timer.schedule (new SynchThread()
           , (long)(1000L * 120L), (long)(1000L * 60L));
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            public void run() {
                BibleThreadPool.ThreadBean context = BibleThreadPool.getContext();
                storeRendering(context);
            }



        });
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
                    context.setRenderMap((Map<Tuple<Integer, Integer>, List<Tuple<Integer, String>>>) objStream.readObject());

                    objStream.close();
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.trace("Error storing rendering" + ex.getMessage());
            }
        }
    }
}
