package org.harry.jesus.danielpersistence.xmlpersist;

import jesus.harry.org.devotional._1.Devotional;
import jesus.harry.org.versnotes._1.Versnotes;
import org.tinylog.Logger;

import javax.xml.bind.*;
import java.io.InputStream;
import java.io.OutputStream;

public class VersNotesPersistence {

    public static Versnotes loadNotes(InputStream stream) {
        JAXBContext jaxbContext;


        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(Versnotes.class);
            Unmarshaller umarshall = jaxbContext.createUnmarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            Versnotes root =  (Versnotes)umarshall.unmarshal(stream);
            Logger.trace("About to unmarshall ok.....");


            System.out.println(root);
            stream.close();

            return (Versnotes) root;
        } catch (Exception ex) {
            Logger.trace("notes not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("notes not loaded", ex);
        }
    }

    public static void storeNotes(Versnotes root, OutputStream out) {
        JAXBContext jaxbContext;
        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(Versnotes.class);
            Marshaller marshall = jaxbContext.createMarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            marshall.marshal(root, out);
            Logger.trace("About to unmarshall ok.....");
            out.close();
        } catch (Exception ex) {
            Logger.trace("notes not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("notes not loaded", ex);
        }
    }
}
