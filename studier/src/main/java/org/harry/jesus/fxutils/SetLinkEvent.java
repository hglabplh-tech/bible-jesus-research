package org.harry.jesus.fxutils;

import com.sun.javafx.scene.SceneEventDispatcher;
import javafx.event.*;
import javafx.scene.web.WebView;
import org.harry.jesus.BibleStudy;
import org.harry.jesus.jesajautils.BibleTextUtils;

public class SetLinkEvent extends Event {

    private final BibleTextUtils.BookLink link;

    public SetLinkEvent(BibleTextUtils.BookLink link) {
        super(BibleStudy.SET_LINK_EVENT);
        this.link = link;
    }

    public BibleTextUtils.BookLink getLink() {
        return link;
    }
}
