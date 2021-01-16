package org.harry.jesus.jesajautils.configjaxbser;

import javax.xml.bind.annotation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;


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

    public static final String FORMAT_XSD = "zefaniaDict.xsd";

    public String getPathToBook() {
        return pathToBook;
    }

    public DictionaryRef setPathToBook(String pathToBook) {
        this.pathToBook = pathToBook;
        return this;
    }

    public String getHashValue() {
        return hashValue;
    }

    public DictionaryRef setHashValue(String hashValue) {
        this.hashValue = hashValue;
        return this;
    }

    public static String getFormatXsd() {
        return FORMAT_XSD;
    }

    public String getFilename() {
        return filename;
    }

    public DictionaryRef setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getDictionaryID() {
        return dictionaryID;
    }

    public DictionaryRef setDictionaryID(String dictionaryID) {
        this.dictionaryID = dictionaryID;
        return this;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

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
