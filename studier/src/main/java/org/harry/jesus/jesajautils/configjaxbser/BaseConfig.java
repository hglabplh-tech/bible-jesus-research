package org.harry.jesus.jesajautils.configjaxbser;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.harry.jesus.jesajautils.browse.TextStyle;

import javax.xml.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Base config.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "biblesDir",
        "dictionariesDir",
        "mediaPath",
        "fontFamily",
        "fontSize",
        "readerShape"
})
public class BaseConfig {
    /**
     * The constant fontFamilies.
     */
    public static final List<String> fontFamilies = Font.getFamilies();
    /**
     * The constant baseShape.
     */
    public static final TextStyle baseShape = TextStyle.backgroundColor(Color.WHITE)
            .updateTextColor(Color.BLACK);
    /**
     * The constant blackShape.
     */
    public static final TextStyle blackShape = TextStyle.backgroundColor(Color.BLACK)
            .updateTextColor(Color.WHITE);
    /**
     * The constant oldDosShape.
     */
    public static final TextStyle oldDosShape = TextStyle.backgroundColor(Color.BLACK)
            .updateTextColor(Color.LIGHTGREEN);
    /**
     * The constant heikeShape.
     */
    public static final TextStyle heikeShape = TextStyle.backgroundColor(Color.RED)
            .updateTextColor(Color.WHITE);
    /**
     * The constant joshiShape.
     */
    public static final TextStyle joshiShape = TextStyle.backgroundColor(Color.WHITE)
            .updateTextColor(Color.DARKBLUE);
    /**
     * The constant harryShape.
     */
    public static final TextStyle harryShape = TextStyle.backgroundColor(Color.LIGHTGREY)
            .updateTextColor(Color.BLACK);

    private  static Map<ShapeEnum, TextStyle> shapeMap = new LinkedHashMap<>();

    static {
        shapeMap.put(ShapeEnum.BASE_SHAPE, baseShape);
        shapeMap.put(ShapeEnum.BLACK_SHAPE,blackShape);
        shapeMap.put(ShapeEnum.OLD_DOS_SHAPE,oldDosShape);
        shapeMap.put(ShapeEnum.HEIKES_SHAPE,heikeShape);
        shapeMap.put(ShapeEnum.JOSHIS_SHAPE, joshiShape);
        shapeMap.put(ShapeEnum.HARRYS_SHAPE,harryShape);
    }

    /**
     * Gets shape map.
     *
     * @return the shape map
     */
    public static Map<ShapeEnum, TextStyle> getShapeMap() {
        return shapeMap;
    }

    /**
     * The Bibles dir.
     */
    @XmlElement(name = "biblesDir", required = true)
    protected String biblesDir;

    /**
     * The Dictionaries dir.
     */
    @XmlElement(name = "dictionariesDir", required = true)
    protected String dictionariesDir;

    /**
     * The Media path.
     */
    @XmlElement(name = "mediaPath", required = true)
    protected String mediaPath;

    /**
     * The Font family.
     */
    @XmlElement(name = "fontFamily", required = true)
    protected String fontFamily;

    /**
     * The Font size.
     */
    @XmlElement(name = "fontSize", required = true)
    protected  Integer fontSize;

    /**
     * The Reader shape.
     */
    @XmlElement(name = "readerShape", required = true)
    protected ShapeEnum readerShape;

    /**
     * Gets bibles dir.
     *
     * @return the bibles dir
     */
    public String getBiblesDir() {
        return biblesDir;
    }

    /**
     * Sets bibles dir.
     *
     * @param biblesDir the bibles dir
     * @return the bibles dir
     */
    public BaseConfig setBiblesDir(String biblesDir) {
        this.biblesDir = biblesDir;
        return this;
    }

    /**
     * Gets dictionaries dir.
     *
     * @return the dictionaries dir
     */
    public String getDictionariesDir() {
        return dictionariesDir;
    }

    /**
     * Sets dictionaries dir.
     *
     * @param dictionariesDir the dictionaries dir
     * @return the dictionaries dir
     */
    public BaseConfig setDictionariesDir(String dictionariesDir) {
        this.dictionariesDir = dictionariesDir;
        return this;
    }

    /**
     * Gets font family.
     *
     * @return the font family
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * Sets font family.
     *
     * @param fontFamily the font family
     * @return the font family
     */
    public BaseConfig setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    /**
     * Gets font size.
     *
     * @return the font size
     */
    public Integer getFontSize() {
        return fontSize;
    }

    /**
     * Sets font size.
     *
     * @param fontSize the font size
     * @return the font size
     */
    public BaseConfig setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    /**
     * Gets media path.
     *
     * @return the media path
     */
    public String getMediaPath() {
        return mediaPath;
    }

    /**
     * Sets media path.
     *
     * @param mediaPath the media path
     * @return the media path
     */
    public BaseConfig setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
        return this;
    }

    /**
     * Gets reader shape.
     *
     * @return the reader shape
     */
    public ShapeEnum getReaderShape() {
        return readerShape;
    }

    /**
     * Sets reader shape.
     *
     * @param readerShape the reader shape
     * @return the reader shape
     */
    public BaseConfig setReaderShape(ShapeEnum readerShape) {
        this.readerShape = readerShape;
        return this;
    }

    /**
     * Gets shape.
     *
     * @return the shape
     */
    public TextStyle getShape() {
        ShapeEnum shapeEnum = getReaderShape();
        return getShapeMap().get(shapeEnum);
    }

    /**
     * The enum Shape enum.
     */
    @XmlType(name = "shapeEnum")
    @XmlEnum
    public enum ShapeEnum {

        /**
         * The Base shape.
         */
        BASE_SHAPE("Base Shape"),
        /**
         * The Black shape.
         */
        BLACK_SHAPE("Black Shape"),
        /**
         * The Old dos shape.
         */
        OLD_DOS_SHAPE("Old Dos Shape"),
        /**
         * The Heikes shape.
         */
        HEIKES_SHAPE("Heike's Shape"),
        /**
         * The Joshis shape.
         */
        JOSHIS_SHAPE("Joshi's Shape"),
        /**
         * The Harrys shape.
         */
        HARRYS_SHAPE("Harry's Shape");

        private final String shapeValue;

        ShapeEnum(String shapeValue) {
            this.shapeValue = shapeValue;
        }

        /**
         * Gets shape value.
         *
         * @return the shape value
         */
        public String getShapeValue() {
            return shapeValue;
        }

        /**
         * Value string.
         *
         * @return the string
         */
        public String value() {
            return name();
        }

        /**
         * From value shape enum.
         *
         * @param v the v
         * @return the shape enum
         */
        public static ShapeEnum fromValue(String v) {
            for (ShapeEnum actEnum: values()) {
                if (actEnum.getShapeValue().equals(v)) {
                    return actEnum;
                }
            }
            return ShapeEnum.BASE_SHAPE;
        }

        @Override
        public String toString() {
            return this.getShapeValue();
        }

    }
}
