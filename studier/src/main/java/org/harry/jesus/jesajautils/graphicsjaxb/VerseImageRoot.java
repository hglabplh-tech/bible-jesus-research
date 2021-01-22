package org.harry.jesus.jesajautils.graphicsjaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Verse image root.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "verseImageMap"

})
@XmlRootElement
public class VerseImageRoot {

    /**
     * The Verse image map.
     */
    @XmlJavaTypeAdapter(VerseImagesMapAdapter.class)
    protected Map<String, VerseImageData> verseImageMap = new HashMap<>();


    /**
     * Gets verse image mapping.
     *
     * @return the verse image mapping
     */
    public Map<String, VerseImageData> getVerseImageMapping() {
        return verseImageMap;
    }
}
