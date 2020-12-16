package org.harry.jesus.jesajautils;

import generated.XMLBIBLE;
import org.tinylog.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class BibleReader {
    public static XMLBIBLE loadBible(InputStream trustList) throws Exception {
        JAXBContext jaxbContext;


        try {
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(XMLBIBLE.class);
            Unmarshaller umarshall  = jaxbContext.createUnmarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            JAXBElement root = (JAXBElement) umarshall.unmarshal(trustList);
            Logger.trace("About to unmarshall ok.....");


            System.out.println(root.getValue());

            return (XMLBIBLE)root.getValue();
        }
        catch (JAXBException ex) {
            Logger.trace("trust list not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("trust list not loaded", ex);
        }
    }

}
