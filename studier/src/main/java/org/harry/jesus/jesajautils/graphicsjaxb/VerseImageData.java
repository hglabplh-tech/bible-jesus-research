package org.harry.jesus.jesajautils.graphicsjaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "verseLink",
        "imagePath"
})
public class VerseImageData {

    @XmlElement(required = true)
    protected String verseLink;

    @XmlElement(required = true)
    protected String imagePath;

    public String getVerseLink() {
        return verseLink;
    }

    public String getImagePath() {
        return imagePath;
    }

    public VerseImageData setVerseLink(String verseLink) {
        this.verseLink = verseLink;
        return this;
    }

    public VerseImageData setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }
}
