package org.harry.jesus.fxutils.event;

import javafx.event.*;
import org.harry.jesus.jesajautils.BibleTextUtils;

/**
 * The type Set link event.
 */
public class SetLinkEvent extends Event {

    /**
     * The constant SET_LINK_EVENT.
     */
    public static EventType<SetLinkEvent> SET_LINK_EVENT = new EventType<>("SET_LINK_EVENT");
    private final BibleTextUtils.BookLink link;

    /**
     * Instantiates a new Set link event.
     *
     * @param link the link
     */
    public SetLinkEvent(BibleTextUtils.BookLink link) {
        super(SET_LINK_EVENT);
        this.link = link;
    }

    /**
     * Gets link.
     *
     * @return the link
     */
    public BibleTextUtils.BookLink getLink() {
        return link;
    }
}
