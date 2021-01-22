package org.harry.jesus.jesajautils.configjaxbser;

import javax.xml.bind.annotation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;


/**
 * The type Dictionary ref.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "dictionaryID",
        "dictionaryName",
        "pathToBook",
        "filename",
        "hashValue"

})
public class DictionaryRef {


    @XmlElement(name = "pathToBook", required = true)
    private String pathToBook = null;

    @XmlElement(name = "filename", required = true)
    private String filename = null;

    @XmlElement(name = "hashValue", required = true)
    private String hashValue = null;

    @XmlElement(name = "dictionaryID", required = true)
    private String dictionaryID = null;

    @XmlElement(name = "dictionaryName", required = false)
    private String dictionaryName = null;

    /**
     * The constant FORMAT_XSD.
     */
    public static final String FORMAT_XSD = "zefaniaDict.xsd";

    /**
     * Gets path to book.
     *
     * @return the path to book
     */
    public String getPathToBook() {
        return pathToBook;
    }

    /**
     * Sets path to book.
     *
     * @param pathToBook the path to book
     * @return the path to book
     */
    public DictionaryRef setPathToBook(String pathToBook) {
        this.pathToBook = pathToBook;
        return this;
    }

    /**
     * Gets hash value.
     *
     * @return the hash value
     */
    public String getHashValue() {
        return hashValue;
    }

    /**
     * Sets hash value.
     *
     * @param hashValue the hash value
     * @return the hash value
     */
    public DictionaryRef setHashValue(String hashValue) {
        this.hashValue = hashValue;
        return this;
    }

    /**
     * Gets format xsd.
     *
     * @return the format xsd
     */
    public static String getFormatXsd() {
        return FORMAT_XSD;
    }

    /**
     * Gets filename.
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets filename.
     *
     * @param filename the filename
     * @return the filename
     */
    public DictionaryRef setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    /**
     * Gets dictionary id.
     *
     * @return the dictionary id
     */
    public String getDictionaryID() {
        return dictionaryID;
    }

    /**
     * Sets dictionary id.
     *
     * @param dictionaryID the dictionary id
     * @return the dictionary id
     */
    public DictionaryRef setDictionaryID(String dictionaryID) {
        this.dictionaryID = dictionaryID;
        return this;
    }

    /**
     * Gets dictionary name.
     *
     * @return the dictionary name
     */
    public String getDictionaryName() {
        return dictionaryName;
    }

    /**
     * Sets dictionary name.
     *
     * @param dictionaryName the dictionary name
     * @return the dictionary name
     */
    public DictionaryRef setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof  DictionaryRef) {
            DictionaryRef that = (DictionaryRef) o;
            return getDictionaryID().equals(that.getDictionaryID());
        } else {
            return false;
        }

    }


    /**
     * Equals exact boolean.
     *
     * @param o the o
     * @return the boolean
     */
    public boolean equalsExact(Object o) {
        if (this == o) return true;
        if (!(o instanceof DictionaryRef)) return false;
        DictionaryRef that = (DictionaryRef) o;
        return getPathToBook().equals(that.getPathToBook()) && getFilename().equals(that.getFilename()) && getHashValue().equals(that.getHashValue()) && getDictionaryID().equals(that.getDictionaryID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPathToBook(), getFilename(), getHashValue(), getDictionaryID());
    }

    @Override
    public String toString() {
        return  dictionaryID
                + ", " + dictionaryName
                + ", " + hashValue;

    }
}
