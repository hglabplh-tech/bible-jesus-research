package org.harry.jesus.jesajautils.configjaxbser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "bibleID",
        "pathToBook",
        "fuzzyLinkSearch",
        "hashValue"

})
public class BibleRef {

    @XmlElement(name = "pathToBook", required = true)
    private String pathToBook = null;

    @XmlElement(name = "bibleID", required = true)
    private String bibleID = null;

    @XmlElement(name = "hashValue", required = true)
    private String hashValue = null;

    @XmlElement(name = "fuzzyLinkSearch", required = true)
    private Boolean fuzzyLinkSearch = false;

    public static final String FORMAT_XSD = "zefania.xsd";


    public String getPathToBook() {
        return pathToBook;
    }

    public BibleRef setPathToBook(String pathToBook) {
        this.pathToBook = pathToBook;
        return this;
    }

    public String getBibleID() {
        return bibleID;
    }

    public BibleRef setBibleID(String bibleID) {
        this.bibleID = bibleID;
        return this;
    }

    public String getHashValue() {
        return hashValue;
    }

    public BibleRef setHashValue(String hasfValue) {
        this.hashValue = hasfValue;
        return this;
    }

    public Boolean getFuzzyLinkSearch() {
        return fuzzyLinkSearch;
    }

    public BibleRef setFuzzyLinkSearch(Boolean fuzzyLinkSearch) {
        this.fuzzyLinkSearch = fuzzyLinkSearch;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BibleRef)) return false;
        BibleRef bibleRef = (BibleRef) o;
        return  getBibleID().equals(bibleRef.getBibleID());
    }

    public boolean equalsExact(Object o) {
        if (this == o) return true;
        if (!(o instanceof BibleRef)) return false;
        BibleRef bibleRef = (BibleRef) o;
        return getPathToBook().equals(bibleRef.getPathToBook())
                && getBibleID().equals(bibleRef.getBibleID())
                && getHashValue().equals(bibleRef.getHashValue())
                && getFuzzyLinkSearch().equals(bibleRef.getFuzzyLinkSearch());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPathToBook(), getBibleID(), getHashValue(), getFuzzyLinkSearch());
    }

    @Override
    public String toString() {
        return "BibleRef{" +
                "pathToBook='" + pathToBook + '\'' +
                ", bibleName='" + bibleID + '\'' +
                ", hashValue='" + hashValue + '\'' +
                ", fuzzyLinkSearch=" + fuzzyLinkSearch +
                '}';
    }
}
