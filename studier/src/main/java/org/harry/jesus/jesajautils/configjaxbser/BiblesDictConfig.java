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
        "selectDictionary"
})
public class BiblesDictConfig {


    /**
     * The Bible refs.
     */
    @XmlElement( required = true)
    protected List<BibleRef> bibleRefs;

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
     * Gets dict bible mapping.
     *
     * @return the dict bible mapping
     */
    public Map<DictionaryRef, BibleRef> getDictBibleMapping() {
        return dictBibleMap;
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
