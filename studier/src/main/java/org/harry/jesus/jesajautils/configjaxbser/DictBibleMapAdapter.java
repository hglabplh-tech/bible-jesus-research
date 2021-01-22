package org.harry.jesus.jesajautils.configjaxbser;


import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * The type Dict bible map adapter.
 */
class DictBibleMapAdapter extends XmlAdapter<DictBibleMapElements[], Map<DictionaryRef, BibleRef>> {
    public DictBibleMapElements[] marshal(Map<DictionaryRef, BibleRef> arg0) throws Exception {
        DictBibleMapElements[] dictBibleMapElements = new DictBibleMapElements[arg0.size()];
        int i = 0;
        for (Map.Entry<DictionaryRef, BibleRef> entry : arg0.entrySet())
            dictBibleMapElements[i++] = new DictBibleMapElements(entry.getKey(), entry.getValue());

        return dictBibleMapElements;
    }

    public Map<DictionaryRef, BibleRef> unmarshal(DictBibleMapElements[] arg0) throws Exception {
        Map<DictionaryRef, BibleRef> r = new HashMap<>();
        for (DictBibleMapElements mapelement : arg0)
            r.put(mapelement.dictRef, mapelement.bibleRef);
        return r;
    }
}

