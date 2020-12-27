package org.harry.jesus.jesajautils.editor;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.web.HTMLEditor;
import org.pmw.tinylog.Logger;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;


public class HTMLToPDF {


public static void convertTo(String html, OutputStream os) {
        try {
            ConverterProperties
                    converterProperties = new ConverterProperties();
            HtmlConverter.convertToPdf(html, os, converterProperties);
            os.close();

        } catch (Exception ex) {
            Logger.trace("Error occured during HTML to PDF" + ex.getMessage());
        }
    }

    public static void printDocument(HTMLEditor htmlEdit) {
    try {

        Printer printer = Printer.getDefaultPrinter();
        System.out.println("Printer - " + printer.getName());
        PrinterJob job = PrinterJob.createPrinterJob(printer);
        job.showPageSetupDialog(htmlEdit.getScene().getWindow());
        htmlEdit.print(job);
        job.endJob();
    } catch (Exception ex) {
        Logger.trace("Print error: " + ex.getMessage());
    }
    }

    static class PrintJobWatcher {
        boolean done = false;

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
