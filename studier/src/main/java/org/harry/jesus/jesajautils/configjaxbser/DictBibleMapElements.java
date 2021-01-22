package org.harry.jesus.jesajautils.configjaxbser;



import javax.xml.bind.annotation.XmlElement;

/**
 * The type Dict bible map elements.
 */
class DictBibleMapElements
{
    /**
     * The Dict ref.
     */
    @XmlElement public DictionaryRef dictRef;
    /**
     * The Bible ref.
     */
    @XmlElement public BibleRef bibleRef;

    private DictBibleMapElements() {} //Required by JAXB

    /**
     * Instantiates a new Dict bible map elements.
     *
     * @param dictRef  the dict ref
     * @param bibleRef the bible ref
     */
    public DictBibleMapElements(DictionaryRef dictRef, BibleRef bibleRef)
    {
        this.dictRef = dictRef;
        this.bibleRef = bibleRef;
    }
}
