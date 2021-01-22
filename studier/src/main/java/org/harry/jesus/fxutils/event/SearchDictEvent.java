package org.harry.jesus.fxutils.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * The type Search dict event.
 */
public class SearchDictEvent extends Event {

    /**
     * The constant SEARCH_DICT_EVENT.
     */
    public static EventType<SearchDictEvent> SEARCH_DICT_EVENT = new EventType<>("SEARCH_DICT_EVENT");
    private final String query;

    /**
     * Instantiates a new Search dict event.
     *
     * @param query the query
     */
    public SearchDictEvent(String query) {
        super(SEARCH_DICT_EVENT);
        this.query = query;
    }

    /**
     * Gets query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }
}
