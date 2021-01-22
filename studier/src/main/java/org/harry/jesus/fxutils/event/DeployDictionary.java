package org.harry.jesus.fxutils.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.harry.jesus.jesajautils.configjaxbser.DictionaryRef;

/**
 * The type Deploy dictionary.
 */
public class DeployDictionary extends Event {

    /**
     * The constant DEPLOY_DICT_EVENT.
     */
    public static EventType<DeployDictionary> DEPLOY_DICT_EVENT = new EventType<>("DEPLOY_DICT_EVENT");
    private final DictionaryRef dictRef;

    /**
     * Instantiates a new Deploy dictionary.
     *
     * @param ref the ref
     */
    public DeployDictionary(DictionaryRef ref) {
        super(DEPLOY_DICT_EVENT);
        this.dictRef = ref;
    }

    /**
     * Gets dict ref.
     *
     * @return the dict ref
     */
    public DictionaryRef getDictRef() {
        return this.dictRef;
    }
}
