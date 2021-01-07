package org.harry.jesus.synchjeremia;

import org.harry.jesus.jesajautils.BibleTextUtils;

import java.io.Serializable;
import java.util.Objects;

public class HistoryEntry implements Serializable {

    private final BibleTextUtils.BookLink bookLink;

    public HistoryEntry(BibleTextUtils.BookLink bookLink) {
        this.bookLink = bookLink;
    }

    public BibleTextUtils.BookLink getBookLink() {
        return bookLink;
    }

    @Override
    public String toString() {
        return getBookLink().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoryEntry)) return false;
        HistoryEntry that = (HistoryEntry) o;
        return getBookLink().equals(that.getBookLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBookLink());
    }
}
