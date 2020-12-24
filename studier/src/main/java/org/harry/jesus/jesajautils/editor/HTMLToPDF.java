package org.harry.jesus.jesajautils.editor;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.pmw.tinylog.Logger;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.awt.print.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class HTMLToPDF {


public static void convertTo(String html, OutputStream os) {
        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(os);
            os.close();
        } catch (Exception ex) {
            Logger.trace("Error occured during HTML to PDF");
        }
    }
}
