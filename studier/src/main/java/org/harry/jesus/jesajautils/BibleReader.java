package org.harry.jesus.jesajautils;


import generated.Dictionary;
import generated.XMLBIBLE;

import org.tinylog.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;

import javax.xml.bind.Unmarshaller;
import java.io.File;

import java.io.FileInputStream;

import java.security.MessageDigest;

public class BibleReader {
    public static Tuple<XMLBIBLE, String> loadBible(File inFile)  {
        JAXBContext jaxbContext;


        try {
            FileInputStream stream = new FileInputStream(inFile);
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(XMLBIBLE.class);
            Unmarshaller umarshall  = jaxbContext.createUnmarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            JAXBElement root = (JAXBElement) umarshall.unmarshal(stream);
            Logger.trace("About to unmarshall ok.....");


            System.out.println(root.getValue());
            stream.close();
            String digest = buildHash(inFile);

            return new Tuple<>((XMLBIBLE)root.getValue(), digest);
        }
        catch (Exception ex) {
            Logger.trace("bible not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("bible not loaded", ex);
        }
    }

    public static Tuple<Dictionary, String> loadBibleAccordance(File inFile)  {
        JAXBContext jaxbContext;


        try {
            FileInputStream stream = new FileInputStream(inFile);
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(Dictionary.class);
            Unmarshaller umarshall  = jaxbContext.createUnmarshaller();
            Logger.trace("About to unmarshall unmarshaller created.....");
            Dictionary root = (Dictionary) umarshall.unmarshal(stream);
            Logger.trace("About to unmarshall ok.....");


            System.out.println(root.getINFORMATION().getTitleOrCreatorOrDescription());
            stream.close();
            String digest = buildHash(inFile);

            return new Tuple<>(root, digest);

        }
        catch (Exception ex) {
            Logger.trace("bible not loaded error ->: " + ex.getMessage());
            Logger.trace(ex);
            throw new IllegalStateException("bible not loaded", ex);
        }
    }

    private static String buildHash(File inFile) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream stream = new FileInputStream(inFile);
            byte[] buffer = new byte[4096];
            int read = stream.read(buffer);
            while (read != -1) {
                digest.update(buffer, 0 , read);
                read = stream.read(buffer);

            }
            byte [] digestBytes = digest.digest();
            stream.close();
            return bytesToHex(digestBytes);
        } catch ( Exception ex) {
            Logger.trace(ex);
            Logger.trace(" Cannot calculate hash of : "
                    + inFile.getAbsolutePath()
                    + " reason: "
                    + ex.getMessage());
            return "FF";
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
