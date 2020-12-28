package org.harry.jesus.jesajautils.editor;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

import javafx.print.*;

import javafx.scene.web.HTMLEditor;

import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.pmw.tinylog.Logger;

import javax.print.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.awt.*;
import java.awt.print.PrinterJob;
import java.io.*;
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
