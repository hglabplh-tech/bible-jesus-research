package org.harry.jesus.danielpersistence.xmlpersist;
/**
 * Class Information
 * @version $Revision$
 * @author $Author$
 *
 * $Id$
 */

import jesus.harry.org.highlights._1.Highlights;
import jesus.harry.org.versnotes._1.Versnotes;
import org.tinylog.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for storing and loading the highlits of vedrses
 *
 * @author $Author$ $Id$
 * @version $Revision$
 */
public class HighlightsPersistence {

    /**
     * Load highlights highlights.
     *
     * @param stream the stream
     * @return the highlights
     */
    public static Highlights loadHighlights(InputStream stream) {
        JAXBContext jaxbContext;


        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(Highlights.class);
            Unmarshaller umarshall = jaxbContext.createUnmarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            Highlights root =  (Highlights)umarshall.unmarshal(stream);
            Logger.trace("About to unmarshall ok.....");


            System.out.println(root);
            stream.close();

            return (Highlights) root;
        } catch (Exception ex) {
            Logger.trace("notes not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("notes not loaded", ex);
        }
    }

    /**
     * Store highlights.
     *
     * @param root the root
     * @param out  the out
     */
    public static void storeHighlights(Highlights root, OutputStream out) {
        JAXBContext jaxbContext;
        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(Highlights.class);
            Marshaller marshall = jaxbContext.createMarshaller();
            marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
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
