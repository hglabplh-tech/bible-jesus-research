package org.harry.jesus.jesajautils.editor;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

import org.pmw.tinylog.Logger;
import java.io.OutputStream;


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
}
