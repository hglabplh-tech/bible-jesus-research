package org.harry.jesus.fxutils.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.harry.jesus.jesajautils.configjaxbser.DictionaryRef;

public class DeployDictionary extends Event {

    public static EventType<DeployDictionary> DEPLOY_DICT_EVENT = new EventType<>("DEPLOY_DICT_EVENT");
    private final DictionaryRef dictRef;

    public DeployDictionary(DictionaryRef ref) {
        super(DEPLOY_DICT_EVENT);
        this.dictRef = ref;
    }

    public DictionaryRef getDictRef() {
        return this.dictRef;
    }
}
