package org.harry.jesus.jesajautils.graphicsjaxb;



import org.harry.jesus.jesajautils.configjaxbser.BibleRef;
import org.harry.jesus.jesajautils.configjaxbser.DictionaryRef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.UUID;

/**
 * The type Verse image map elements.
 */
class VerseImageMapElements
{
    /**
     * The Identifier.
     */
    @XmlElement public String identifier;
    /**
     * The Image data.
     */
    @XmlElement public VerseImageData imageData;

    private VerseImageMapElements() {} //Required by JAXB

    /**
     * Instantiates a new Verse image map elements.
     *
     * @param identifier the identifier
     * @param imageData  the image data
     */
    public VerseImageMapElements(String identifier, VerseImageData imageData)
    {
        this.identifier = identifier;
        this.imageData = imageData;
    }



}
