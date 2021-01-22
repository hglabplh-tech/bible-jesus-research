package org.harry.jesus.danielpersistence.xmlpersist;

import generated.XMLBIBLE;
import jesus.harry.org.devotional._1.Devotional;
import org.tinylog.Logger;

import javax.xml.bind.*;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * The type Devotional persistence.
 */
public class DevotionalPersistence {


    /**
     * Load devotional devotional.
     *
     * @param stream the stream
     * @return the devotional
     */
    public static Devotional loadDevotional(InputStream stream) {
        JAXBContext jaxbContext;


        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(Devotional.class);
            Unmarshaller umarshall = jaxbContext.createUnmarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            JAXBElement<Devotional> root = (JAXBElement<Devotional>) umarshall.unmarshal(stream);
            Logger.trace("About to unmarshall ok.....");


            System.out.println(root.getValue());

            return (Devotional) root.getValue();
        } catch (
                JAXBException ex) {
            Logger.trace("trust list not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("trust list not loaded", ex);
        }
    }

    /**
     * Store devotional.
     *
     * @param root the root
     * @param out  the out
     */
    public static void storeDevotional(Devotional root, OutputStream out) {
        JAXBContext jaxbContext;
        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(Devotional.class);
            Marshaller marshall = jaxbContext.createMarshaller();
            marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Logger.trace("About to unmarshall unmarshaller created.....");
            marshall.marshal(root, out);
            Logger.trace("About to unmarshall ok.....");
        } catch (
                JAXBException ex) {
            Logger.trace("trust list not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("trust list not loaded", ex);
        }
    }
}
