package org.harry.jesus.jesajautils.httpSrv;

import generated.Dictionary;
import generated.XMLBIBLE;
import jesus.harry.org.verseofday._1.LinkContent;
import jesus.harry.org.verseofday._1.LinkType;
import jesus.harry.org.verseofday._1.VersesOfDay;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.harry.jesus.synchjeremia.SynchThread;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DayVerses {

    /**
     * The singleton instance variable
     */
    private static final DayVerses dayVerses = new DayVerses();

    /**
     * Make private constructor
     */
    private DayVerses() {

    }

    public  List<BibleTextUtils.BookLink> verses = new ArrayList<>();

    /**
     * load verse of days XML
     */
    public void loadVerseOfDays() {


        try {
            if (SynchThread.verseOfDaysXML.exists()) {
                BufferedInputStream stream = new BufferedInputStream(new FileInputStream(SynchThread.verseOfDaysXML));
                JAXBContext jaxbContext;
                Logger.trace("About to unmarshall.....");
                jaxbContext = JAXBContext.newInstance(VersesOfDay.class);
                Unmarshaller umarshall = jaxbContext.createUnmarshaller();
                Logger.trace("About to unmarshall unmarshaller created.....");
                VersesOfDay versesOfDays =  (VersesOfDay)umarshall.unmarshal(stream);
                Logger.trace("About to unmarshall ok.....");
                stream.close();
                fillVersesList(versesOfDays);

            }
        }
        catch(Exception ex){
                Logger.trace("verse of day  not loaded error ->: " + ex.getMessage());
                Logger.trace(ex);
                throw new IllegalStateException("verse of day not loaded", ex);
        }
    }


    /**
     * Store verses of days.
     */
    public void storeVersesOfDays() {
        JAXBContext jaxbContext;

        try {
            OutputStream out = new FileOutputStream(SynchThread.verseOfDaysXML);
            Logger.trace("Fill object for serialization");
            VersesOfDay root = convertToVersesOfDay();
            Logger.trace("About to unmarshall.....");
            jaxbContext = JAXBContext.newInstance(VersesOfDay.class);
            Marshaller marshall = jaxbContext.createMarshaller();
            marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Logger.trace("About to unmarshall unmarshaller created.....");
            marshall.marshal(root, out);
            Logger.trace("About to unmarshall ok.....");
            out.close();
        } catch (Exception ex) {
            Logger.trace("day of verses not stored error ->: " + ex.getMessage());
            Logger.trace(ex);
        }
    }

    /**
     * Build the JAXB Objects for serialization
     * @return the object
     */
    private VersesOfDay convertToVersesOfDay() {
        VersesOfDay root = new VersesOfDay();
        for (BibleTextUtils.BookLink verseLink: this.verses) {
            LinkType link = new LinkType();
            LinkContent content = new LinkContent();
            content.setBookNo(verseLink.getBook());
            content.setChapterNo(verseLink.getChapter());
            for (Integer verseNo: verseLink.getVerses()) {
                content.getVerseNo().add(verseNo);
            }
            link.setVerseContent(content);
            root.getVerses().add(link);
        }
        return root;
    }

    /**
     * Fill the internal list when loading
     * @param versesOfDays the XML object for the verses of the days
     */
    private void fillVersesList(VersesOfDay versesOfDays) {
        System.out.println(versesOfDays);
        List<LinkType> links = versesOfDays.getVerses();
        for (LinkType link: links) {
            LinkContent content = link.getVerseContent();
            if (content != null) {
                Integer bookNo = content.getBookNo();
                Integer chapterNo = content.getChapterNo();
                BibleTextUtils.BookLink bookLink =
                        new BibleTextUtils.BookLink(bookNo, chapterNo, content.getVerseNo());
                this.verses.add(bookLink);
            }
        }
    }

    /**
     * return the verses list
     * @return the verses list
     */
    public List<BibleTextUtils.BookLink> getVerses() {
        return verses;
    }

    /**
     * Return the singleton of this class
     * @return the singleton instance
     */
    public  List<BibleTextUtils.BookLink> loadVerses() {
        this.getVerses().clear();
        this.loadVerseOfDays();
        return this.getVerses();
    }

    /**
     * Return the singleton of this class
     * @return the singleton instance
     */
    public static DayVerses getInstance() {
        return dayVerses;
    }
}
