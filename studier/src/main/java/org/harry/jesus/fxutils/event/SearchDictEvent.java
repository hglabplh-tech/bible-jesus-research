package org.harry.jesus.fxutils.event;

import javafx.event.Event;
import javafx.event.EventType;

public class SearchDictEvent extends Event {

    public static EventType<SearchDictEvent> SEARCH_DICT_EVENT = new EventType<>("SEARCH_DICT_EVENT");
    private final String query;

    public SearchDictEvent(String query) {
        super(SEARCH_DICT_EVENT);
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
