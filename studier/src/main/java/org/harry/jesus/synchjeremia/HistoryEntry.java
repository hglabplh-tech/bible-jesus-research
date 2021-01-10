package org.harry.jesus.synchjeremia;

import org.harry.jesus.jesajautils.BibleTextUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

public class HistoryEntry implements Serializable {

    private final String titel;

    private final Calendar date;

    private final BibleTextUtils.BookLink bookLink;

    public HistoryEntry(String titel, Calendar date, BibleTextUtils.BookLink bookLink) {
        this.titel = titel;
        this.date = date;
        this.bookLink = bookLink;
    }

    public BibleTextUtils.BookLink getBookLink() {
        return bookLink;
    }

    public String getTitel() {
        return titel;
    }

    public Calendar getDate() {
        return date;
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

    public boolean equalsExact(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoryEntry)) return false;
        HistoryEntry that = (HistoryEntry) o;
        return getTitel().equals(that.getTitel()) && getDate().equals(that.getDate()) && getBookLink().equals(that.getBookLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitel(), getDate(), getBookLink());
    }
}
