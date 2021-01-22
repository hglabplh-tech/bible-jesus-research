package org.harry.jesus.jesajautils.configjaxbser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * The type Bible ref.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "bibleID",
        "biblename",
        "pathToBook",
        "fuzzyLinkSearch",
        "hashValue"

})
public class BibleRef {

    @XmlElement(name = "pathToBook", required = true)
    private String pathToBook = null;

    @XmlElement(name = "biblename", required = false)
    private String biblename;

    @XmlElement(name = "bibleID", required = true)
    private String bibleID = null;

    @XmlElement(name = "hashValue", required = true)
    private String hashValue = null;

    @XmlElement(name = "fuzzyLinkSearch", required = true)
    private Boolean fuzzyLinkSearch = false;

    /**
     * The constant FORMAT_XSD.
     */
    public static final String FORMAT_XSD = "zefania.xsd";


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
    public BibleRef setPathToBook(String pathToBook) {
        this.pathToBook = pathToBook;
        return this;
    }

    /**
     * Gets bible id.
     *
     * @return the bible id
     */
    public String getBibleID() {
        return bibleID;
    }

    /**
     * Sets bible id.
     *
     * @param bibleID the bible id
     * @return the bible id
     */
    public BibleRef setBibleID(String bibleID) {
        this.bibleID = bibleID;
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
     * @param hasfValue the hasf value
     * @return the hash value
     */
    public BibleRef setHashValue(String hasfValue) {
        this.hashValue = hasfValue;
        return this;
    }

    /**
     * Gets fuzzy link search.
     *
     * @return the fuzzy link search
     */
    public Boolean getFuzzyLinkSearch() {
        return fuzzyLinkSearch;
    }

    /**
     * Sets fuzzy link search.
     *
     * @param fuzzyLinkSearch the fuzzy link search
     * @return the fuzzy link search
     */
    public BibleRef setFuzzyLinkSearch(Boolean fuzzyLinkSearch) {
        this.fuzzyLinkSearch = fuzzyLinkSearch;
        return this;
    }

    /**
     * Gets biblename.
     *
     * @return the biblename
     */
    public String getBiblename() {
        return biblename;
    }

    /**
     * Sets biblename.
     *
     * @param biblename the biblename
     * @return the biblename
     */
    public BibleRef setBiblename(String biblename) {
        this.biblename = biblename;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BibleRef)) return false;
        BibleRef bibleRef = (BibleRef) o;
        return  getBibleID().equals(bibleRef.getBibleID());
    }

    /**
     * Equals exact boolean.
     *
     * @param o the o
     * @return the boolean
     */
    public boolean equalsExact(Object o) {
        if (this == o) return true;
        if (!(o instanceof BibleRef)) return false;
        BibleRef bibleRef = (BibleRef) o;
        return getPathToBook().equals(bibleRef.getPathToBook())
                && getBibleID().equals(bibleRef.getBibleID())
                && getBiblename().equals(bibleRef.getBiblename())
                && getHashValue().equals(bibleRef.getHashValue())
                && getFuzzyLinkSearch().equals(bibleRef.getFuzzyLinkSearch());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPathToBook(), getBibleID(), getHashValue(), getFuzzyLinkSearch(), getBiblename());
    }

    @Override
    public String toString() {
        return  bibleID
                + ", " + biblename
                + ", " + hashValue;
    }
}
