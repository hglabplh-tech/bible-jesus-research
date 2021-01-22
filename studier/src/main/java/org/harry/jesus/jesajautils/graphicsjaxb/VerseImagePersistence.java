package org.harry.jesus.jesajautils.graphicsjaxb;


import org.tinylog.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The type Verse image persistence.
 */
public class VerseImagePersistence {

    /**
     * Load app settings verse image root.
     *
     * @param stream the stream
     * @return the verse image root
     */
    public static VerseImageRoot loadAppSettings(InputStream stream) {
        JAXBContext jaxbContext;


        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(VerseImageRoot.class);
            Unmarshaller umarshall = jaxbContext.createUnmarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            VerseImageRoot root =  (VerseImageRoot)umarshall.unmarshal(stream);
            Logger.trace("About to unmarshall ok.....");


            System.out.println(root);
            stream.close();

            return root;
        } catch (Exception ex) {
            Logger.trace("Verse Image root not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("Verse Image root not loaded", ex);
        }
    }

    /**
     * Store app settings.
     *
     * @param root the root
     * @param out  the out
     */
    public static void storeAppSettings(VerseImageRoot root, OutputStream out) {
        JAXBContext jaxbContext;
        try {
            Logger.trace("About to marshall.....");
            jaxbContext = JAXBContext.newInstance(VerseImageRoot.class);
            Marshaller marshall = jaxbContext.createMarshaller();
            marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Logger.trace("About to marshall unmarshaller created.....");
            marshall.marshal(root, out);
            Logger.trace("About to marshall ok.....");
            out.close();
        } catch (Exception ex) {
            Logger.trace("Verse Image root not stored error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("Verse Image root  not stored", ex);
        }
    }
}
