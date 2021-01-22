package org.harry.jesus.jesajautils.graphicsjaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The type Verse image data.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "verseLink",
        "imagePath"
})
public class VerseImageData {

    /**
     * The Verse link.
     */
    @XmlElement(required = true)
    protected String verseLink;

    /**
     * The Image path.
     */
    @XmlElement(required = true)
    protected String imagePath;

    /**
     * Gets verse link.
     *
     * @return the verse link
     */
    public String getVerseLink() {
        return verseLink;
    }

    /**
     * Gets image path.
     *
     * @return the image path
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Sets verse link.
     *
     * @param verseLink the verse link
     * @return the verse link
     */
    public VerseImageData setVerseLink(String verseLink) {
        this.verseLink = verseLink;
        return this;
    }

    /**
     * Sets image path.
     *
     * @param imagePath the image path
     * @return the image path
     */
    public VerseImageData setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }
}
