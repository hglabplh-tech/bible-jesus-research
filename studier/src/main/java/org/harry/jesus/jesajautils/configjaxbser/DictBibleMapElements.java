package org.harry.jesus.jesajautils.configjaxbser;



import javax.xml.bind.annotation.XmlElement;

class DictBibleMapElements
{
    @XmlElement public DictionaryRef dictRef;
    @XmlElement public BibleRef bibleRef;

    private DictBibleMapElements() {} //Required by JAXB

    public DictBibleMapElements(DictionaryRef dictRef, BibleRef bibleRef)
    {
        this.dictRef = dictRef;
        this.bibleRef = bibleRef;
    }
}
