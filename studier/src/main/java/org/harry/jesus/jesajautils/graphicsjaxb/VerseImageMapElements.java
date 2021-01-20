package org.harry.jesus.jesajautils.graphicsjaxb;



import org.harry.jesus.jesajautils.configjaxbser.BibleRef;
import org.harry.jesus.jesajautils.configjaxbser.DictionaryRef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.UUID;

class VerseImageMapElements
{
    @XmlElement public String identifier;
    @XmlElement public VerseImageData imageData;

    private VerseImageMapElements() {} //Required by JAXB

    public VerseImageMapElements(String identifier, VerseImageData imageData)
    {
        this.identifier = identifier;
        this.imageData = imageData;
    }



}
