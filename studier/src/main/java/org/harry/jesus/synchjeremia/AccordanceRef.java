package org.harry.jesus.synchjeremia;

import java.util.Objects;

public class AccordanceRef {


    private String pathToBook = null;

    private String filename = null;

    private String hashValue = null;

    private String dictionaryID = null;

    public static final String FORMAT_XSD = "zefaniaDict.xsd";

    public String getPathToBook() {
        return pathToBook;
    }

    public AccordanceRef setPathToBook(String pathToBook) {
        this.pathToBook = pathToBook;
        return this;
    }





    public String getHashValue() {
        return hashValue;
    }

    public AccordanceRef setHashValue(String hashValue) {
        this.hashValue = hashValue;
        return this;
    }

    public static String getFormatXsd() {
        return FORMAT_XSD;
    }

    public String getFilename() {
        return filename;
    }

    public AccordanceRef setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getDictionaryID() {
        return dictionaryID;
    }

    public AccordanceRef setDictionaryID(String dictionaryID) {
        this.dictionaryID = dictionaryID;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccordanceRef)) return false;
        AccordanceRef that = (AccordanceRef) o;
        return getPathToBook().equals(that.getPathToBook()) && Objects.equals(getFilename(), that.getFilename()) && Objects.equals(getHashValue(), that.getHashValue()) && getDictionaryID().equals(that.getDictionaryID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPathToBook(), getFilename(), getHashValue(), getDictionaryID());
    }
}
