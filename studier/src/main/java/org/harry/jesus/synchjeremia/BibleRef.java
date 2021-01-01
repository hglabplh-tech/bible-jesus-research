package org.harry.jesus.synchjeremia;

import java.util.Objects;

public class BibleRef {

    private String pathToBook = null;

    private String bibleName = null;

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

    public String getBibleName() {
        return bibleName;
    }

    public BibleRef setBibleName(String bibleName) {
        this.bibleName = bibleName;
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
                && getBibleName().equals(bibleRef.getBibleName())
                && getHashValue().equals(bibleRef.getHashValue())
                && getFuzzyLinkSearch().equals(bibleRef.getFuzzyLinkSearch());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPathToBook(), getBibleName(), getHashValue(), getFuzzyLinkSearch());
    }

    @Override
    public String toString() {
        return "BibleRef{" +
                "pathToBook='" + pathToBook + '\'' +
                ", bibleName='" + bibleName + '\'' +
                ", hashValue='" + hashValue + '\'' +
                ", fuzzyLinkSearch=" + fuzzyLinkSearch +
                '}';
    }
}
