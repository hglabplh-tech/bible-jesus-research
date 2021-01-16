package org.harry.jesus.jesajautils.configjaxbser;

import jesus.harry.org.plan._1.Plan;
import org.tinylog.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;

public class AppSettingsPersistence {

    public static BibleAppConfig loadAppSettings(InputStream stream) {
        JAXBContext jaxbContext;


        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(BibleAppConfig.class);
            Unmarshaller umarshall = jaxbContext.createUnmarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            BibleAppConfig root =  (BibleAppConfig)umarshall.unmarshal(stream);
            Logger.trace("About to unmarshall ok.....");


            System.out.println(root);
            stream.close();

            return root;
        } catch (Exception ex) {
            Logger.trace("notes not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("application settings not loaded", ex);
        }
    }

    public static void storeAppSettings(BibleAppConfig root, OutputStream out) {
        JAXBContext jaxbContext;
        try {
            Logger.trace("About to marshall.....");
            jaxbContext = JAXBContext.newInstance(BibleAppConfig.class);
            Marshaller marshall = jaxbContext.createMarshaller();
            marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Logger.trace("About to marshall unmarshaller created.....");
            marshall.marshal(root, out);
            Logger.trace("About to marshall ok.....");
            out.close();
        } catch (Exception ex) {
            Logger.trace("application settings not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("application settings not stored", ex);
        }
    }
}
