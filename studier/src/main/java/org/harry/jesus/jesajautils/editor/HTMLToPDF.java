package org.harry.jesus.jesajautils.editor;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

import javafx.print.*;

import javafx.scene.web.HTMLEditor;

import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.harry.jesus.jesajautils.judaerrmsg.ExceptionAlert;
import org.pmw.tinylog.Logger;

import javax.print.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.awt.*;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.UUID;


/**
 * The type Html to pdf.
 */
public class HTMLToPDF {


    /**
     * Convert to.
     *
     * @param html the html
     * @param os   the os
     */
    public static void convertTo(String html, OutputStream os) {
        try {
            ConverterProperties
                    converterProperties = new ConverterProperties();
            HtmlConverter.convertToPdf(html, os, converterProperties);
            os.close();

        } catch (Exception ex) {
            ExceptionAlert alert = new ExceptionAlert(ex);
            alert.showAndWait();
            Logger.trace("Error occured during HTML to PDF" + ex.getMessage());
        }
    }

    /**
     * Print document.
     *
     * @param htmlEdit the html edit
     */
    public static void printDocument(HTMLEditor htmlEdit) {
    try {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        convertTo(htmlEdit.getHtmlText(), os);
        PrinterJob job = PrinterJob.getPrinterJob();
        ByteArrayInputStream pdfIN = new ByteArrayInputStream(os.toByteArray());
        PDDocument doc = PDDocument.load(pdfIN);
        PDFPrintable pdfPrintable = new PDFPrintable(doc);
        pdfPrintable.setSubsamplingAllowed(true);
        if (job.printDialog()) {
            job.setPrintable(pdfPrintable);
            job.print();
        }

    } catch (Exception ex) {
        Logger.trace("Print error: " + ex.getMessage());
    }
    }

    /**
     * The type Print job watcher.
     */
    static class PrintJobWatcher {
        /**
         * The Done.
         */
        boolean done = false;

        /**
         * Instantiates a new Print job watcher.
         *
         * @param job the job
         */
        PrintJobWatcher(DocPrintJob job) {
            job.addPrintJobListener(new PrintJobAdapter() {
                public void printJobCanceled(PrintJobEvent pje) {
                    allDone();
                }
                public void printJobCompleted(PrintJobEvent pje) {
                    allDone();
                }
                public void printJobFailed(PrintJobEvent pje) {
                    allDone();
                }
                public void printJobNoMoreEvents(PrintJobEvent pje) {
                    allDone();
                }
                void allDone() {
                    synchronized (PrintJobWatcher.this) {
                        done = true;
                        System.out.println("Printing done ...");
                        PrintJobWatcher.this.notify();
                    }
                }
            });
        }

        /**
         * Wait for done.
         */
        public synchronized void waitForDone() {
            try {
                while (!done) {
                    wait();
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
