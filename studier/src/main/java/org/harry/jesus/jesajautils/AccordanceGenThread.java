package org.harry.jesus.jesajautils;

import generated.Dictionary;
import org.harry.jesus.synchjeremia.ApplicationProperties;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AccordanceGenThread extends Thread {

    private final Dictionary accordance;

    private final File accordenceAppDir;

    private final String accordenceFileName;

    private final BibleTextUtils utils;

    public AccordanceGenThread(BibleTextUtils utils, Dictionary accordence,
                               String accordenceFileName,
                               File accordenceAppDir) {
        this.accordance = accordence;
        this.accordenceFileName = accordenceFileName;
        this.utils = utils;
        this.accordenceAppDir = accordenceAppDir;
    }

    @Override
    public void run() {
        String fileName = this.accordenceFileName + ".html";
        File outFile = new File(this.accordenceAppDir, fileName);
        if (outFile.exists()) {
            return;
        }
        Logger.trace("Start build accordance file: " + outFile.getAbsolutePath());
        StringBuffer html = HTMLRendering.buildAccordance(utils, accordance);
        try {
            FileOutputStream outStream = new FileOutputStream(outFile);
            outStream.write(html.toString().getBytes());
            outStream.close();
            Logger.trace("Ready build accordance file: " + outFile.getAbsolutePath());
        } catch (IOException ex) {
            Logger.trace(ex);
            Logger.trace("Write: \""
                    + outFile.getAbsolutePath()
                    + "\" failed with: "
                    + ex.getMessage());
        }
    }
}
