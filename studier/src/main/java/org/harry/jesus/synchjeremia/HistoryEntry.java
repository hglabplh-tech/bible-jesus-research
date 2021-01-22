package org.harry.jesus.synchjeremia;

import org.harry.jesus.jesajautils.BibleTextUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

/**
 * The type History entry.
 */
public class HistoryEntry implements Serializable {

    private final String titel;

    private final Calendar date;

    private final BibleTextUtils.BookLink bookLink;

    /**
     * Instantiates a new History entry.
     *
     * @param titel    the titel
     * @param date     the date
     * @param bookLink the book link
     */
    public HistoryEntry(String titel, Calendar date, BibleTextUtils.BookLink bookLink) {
        this.titel = titel;
        this.date = date;
        this.bookLink = bookLink;
    }

    /**
     * Gets book link.
     *
     * @return the book link
     */
    public BibleTextUtils.BookLink getBookLink() {
        return bookLink;
    }

    /**
     * Gets titel.
     *
     * @return the titel
     */
    public String getTitel() {
        return titel;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
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

    /**
     * Equals exact boolean.
     *
     * @param o the o
     * @return the boolean
     */
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
