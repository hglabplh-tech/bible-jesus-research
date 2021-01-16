package org.harry.jesus.jesajautils.configjaxbser;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "bibleRefs",
        "dictionaryRefs",
        "dictBibleMap",
})
public class BiblesDictConfig {


    @XmlElement( required = true)
    protected List<BibleRef> bibleRefs;

    @XmlElement( required = true)
    protected List<DictionaryRef> dictionaryRefs;

    @XmlJavaTypeAdapter(DictBibleMapAdapter.class)
    protected Map<DictionaryRef, BibleRef> dictBibleMap = new HashMap<>();



    public Map<DictionaryRef, BibleRef> getDictBibleMapping() {
        return dictBibleMap;
    }

    public List<BibleRef> getBibleRefs() {
        if (bibleRefs == null) {
            bibleRefs = new ArrayList<>();
        }
        return bibleRefs;
    }

    public List<DictionaryRef> getDictionaryRefs() {
        if (dictionaryRefs == null) {
            dictionaryRefs = new ArrayList<>();
        }
        return dictionaryRefs;
    }
}
