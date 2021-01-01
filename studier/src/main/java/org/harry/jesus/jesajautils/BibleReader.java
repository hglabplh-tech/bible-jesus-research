package org.harry.jesus.jesajautils;

import generated.XMLBIBLE;
import org.harry.jesus.synchjeremia.BibleRef;
import org.tinylog.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class BibleReader {
    public static XMLBIBLE loadBible(InputStream stream)  {
        JAXBContext jaxbContext;


        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(XMLBIBLE.class);
            Unmarshaller umarshall  = jaxbContext.createUnmarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            JAXBElement root = (JAXBElement) umarshall.unmarshal(stream);
            Logger.trace("About to unmarshall ok.....");


            System.out.println(root.getValue());
            stream.close();
            return (XMLBIBLE)root.getValue();
        }
        catch (Exception ex) {
            Logger.trace("bible not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("bible not loaded", ex);
        }
    }

}
