package org.harry.jesus.jesajautils.graphicsjaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

class VerseImagesMapAdapter extends XmlAdapter<VerseImageMapElements[],
        Map<String, VerseImageData>> {
    @Override
    public Map<String, VerseImageData> unmarshal(VerseImageMapElements[] arg0) throws Exception {
        Map<String, VerseImageData> r = new HashMap<>();
        for (VerseImageMapElements mapelement : arg0)
            r.put(mapelement.identifier, mapelement.imageData);
        return r;
    }

    public VerseImageMapElements[] marshal(Map<String, VerseImageData> arg0) throws Exception {
        VerseImageMapElements[] dictBibleMapElements = new VerseImageMapElements[arg0.size()];
        int i = 0;
        for (Map.Entry<String, VerseImageData> entry : arg0.entrySet())
            dictBibleMapElements[i++] = new VerseImageMapElements(entry.getKey(), entry.getValue());

        return dictBibleMapElements;
    }


}

