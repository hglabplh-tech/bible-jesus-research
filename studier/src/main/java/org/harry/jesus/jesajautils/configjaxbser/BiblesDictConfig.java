package org.harry.jesus.jesajautils.configjaxbser;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Bibles dict config.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "bibleRefs",
        "dictionaryRefs",
        "dictBibleMap",
        "selectBible",
        "selectDictionary",
        "verseOfDayBibleId",
        "verseOfDayRandom"
})
public class BiblesDictConfig {


    /**
     * The Bible refs.
     */
    @XmlElement( required = true)
    protected List<BibleRef> bibleRefs;

    /**
     * The Bible refs.
     */
    @XmlElement( required = false)
    protected String verseOfDayBibleId;

    /**
     * The Dictionary refs.
     */
    @XmlElement( required = true)
    protected List<DictionaryRef> dictionaryRefs;

    /**
     * The Dict bible map.
     */
    @XmlJavaTypeAdapter(DictBibleMapAdapter.class)
    protected Map<DictionaryRef, BibleRef> dictBibleMap = new HashMap<>();

    /**
     * The Select dictionary.
     */
    @XmlElement( required = false)
    protected Boolean selectDictionary = Boolean.FALSE;

    /**
     * The Select bible.
     */
    @XmlElement( required = false)
    protected Boolean selectBible = Boolean.FALSE;

    /**
     * The Verse of day random.
     */
    @XmlElement( required = false)
    protected Boolean verseOfDayRandom = Boolean.TRUE;


    /**
     * Gets dict bible mapping.
     *
     * @return the dict bible mapping
     */
    public Map<DictionaryRef, BibleRef> getDictBibleMapping() {
        return dictBibleMap;
    }

    /**
     * Gets verse of day bible id.
     *
     * @return the verse of day bible id
     */
    public String getVerseOfDayBibleId() {
        return verseOfDayBibleId;
    }

    /**
     * Sets verse of day bible id.
     *
     * @param verseOfDayBibleId the verse of day bible id
     * @return the verse of day bible id
     */
    public BiblesDictConfig setVerseOfDayBibleId(String verseOfDayBibleId) {
        this.verseOfDayBibleId = verseOfDayBibleId;
        return this;
    }

    /**
     * Gets bible refs.
     *
     * @return the bible refs
     */
    public List<BibleRef> getBibleRefs() {
        if (bibleRefs == null) {
            bibleRefs = new ArrayList<>();
        }
        return bibleRefs;
    }

    /**
     * Gets dictionary refs.
     *
     * @return the dictionary refs
     */
    public List<DictionaryRef> getDictionaryRefs() {
        if (dictionaryRefs == null) {
            dictionaryRefs = new ArrayList<>();
        }
        return dictionaryRefs;
    }

    /**
     * Gets select dictionary.
     *
     * @return the select dictionary
     */
    public Boolean getSelectDictionary() {
        return selectDictionary;
    }

    /**
     * Gets select bible.
     *
     * @return the select bible
     */
    public Boolean getSelectBible() {
        return selectBible;
    }

    /**
     * If verse of day is random
     *
     * @return get the true/false
     */
    public Boolean getVerseOfDayRandom() {
        return verseOfDayRandom;
    }

    /**
     * Set if the verse of the day is selected random
     *
     * @param verseOfDayRandom set true /false
     * @return the instance of this class
     */
    public BiblesDictConfig setVerseOfDayRandom(Boolean verseOfDayRandom) {
        this.verseOfDayRandom = verseOfDayRandom;
        return this;
    }

    /**
     * Sets select dictionary.
     *
     * @param selectDictionary the select dictionary
     * @return the select dictionary
     */
    public BiblesDictConfig setSelectDictionary(Boolean selectDictionary) {
        this.selectDictionary = selectDictionary;
        return this;
    }

    /**
     * Sets select bible.
     *
     * @param selectBible the select bible
     * @return the select bible
     */
    public BiblesDictConfig setSelectBible(Boolean selectBible) {
        this.selectBible = selectBible;
        return this;
    }
}
