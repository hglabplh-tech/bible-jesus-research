package org.harry.jesus.synchjeremia;

import java.util.Objects;

public class BibleRef {

    private String pathToBook = null;

    private String bibleID = null;

    private String hashValue = null;

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
