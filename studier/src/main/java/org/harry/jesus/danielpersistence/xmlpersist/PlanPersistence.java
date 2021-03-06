package org.harry.jesus.danielpersistence.xmlpersist;

import jesus.harry.org.plan._1.Plan;
import jesus.harry.org.versnotes._1.Versnotes;
import org.tinylog.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The type Plan persistence.
 */
public class PlanPersistence {

    /**
     * Load plan plan.
     *
     * @param stream the stream
     * @return the plan
     */
    public static Plan loadPlan(InputStream stream) {
        JAXBContext jaxbContext;


        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(Plan.class);
            Unmarshaller umarshall = jaxbContext.createUnmarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            Plan root =  (Plan)umarshall.unmarshal(stream);
            Logger.trace("About to unmarshall ok.....");


            System.out.println(root);
            stream.close();

            return (Plan) root;
        } catch (Exception ex) {
            Logger.trace("notes not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("notes not loaded", ex);
        }
    }

    /**
     * Store plan.
     *
     * @param root the root
     * @param out  the out
     */
    public static void storePlan(Plan root, OutputStream out) {
        JAXBContext jaxbContext;
        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(Plan.class);
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
