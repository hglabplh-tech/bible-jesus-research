package org.harry.jesus.fxutils.event;

import javafx.event.*;
import org.harry.jesus.jesajautils.BibleTextUtils;

public class SetLinkEvent extends Event {

    public static EventType<SetLinkEvent> SET_LINK_EVENT = new EventType<>("SET_LINK_EVENT");
    private final BibleTextUtils.BookLink link;

    public SetLinkEvent(BibleTextUtils.BookLink link) {
        super(SET_LINK_EVENT);
        this.link = link;
    }

    public BibleTextUtils.BookLink getLink() {
        return link;
    }
}
