package org.harry.jesus.jesajautils.configjaxbser;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.harry.jesus.jesajautils.browse.TextStyle;

import javax.xml.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public static final List<String> fontFamilies = Font.getFamilies();
    public static final TextStyle baseShape = TextStyle.backgroundColor(Color.WHITE)
            .updateTextColor(Color.BLACK);
    public static final TextStyle blackShape = TextStyle.backgroundColor(Color.BLACK)
            .updateTextColor(Color.WHITE);
    public static final TextStyle oldDosShape = TextStyle.backgroundColor(Color.BLACK)
            .updateTextColor(Color.LIGHTGREEN);
    public static final TextStyle heikeShape = TextStyle.backgroundColor(Color.RED)
            .updateTextColor(Color.WHITE);
    public static final TextStyle joshiShape = TextStyle.backgroundColor(Color.WHITE)
            .updateTextColor(Color.DARKBLUE);
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

    public static Map<ShapeEnum, TextStyle> getShapeMap() {
        return shapeMap;
    }

    @XmlElement(name = "biblesDir", required = true)
    protected String biblesDir;

    @XmlElement(name = "dictionariesDir", required = true)
    protected String dictionariesDir;

    @XmlElement(name = "mediaPath", required = true)
    protected String mediaPath;

    @XmlElement(name = "fontFamily", required = true)
    protected String fontFamily;

    @XmlElement(name = "fontSize", required = true)
    protected  Integer fontSize;

    @XmlElement(name = "readerShape", required = true)
    protected ShapeEnum readerShape;

    public String getBiblesDir() {
        return biblesDir;
    }

    public BaseConfig setBiblesDir(String biblesDir) {
        this.biblesDir = biblesDir;
        return this;
    }

    public String getDictionariesDir() {
        return dictionariesDir;
    }

    public BaseConfig setDictionariesDir(String dictionariesDir) {
        this.dictionariesDir = dictionariesDir;
        return this;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public BaseConfig setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public BaseConfig setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public BaseConfig setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
        return this;
    }

    public ShapeEnum getReaderShape() {
        return readerShape;
    }

    public BaseConfig setReaderShape(ShapeEnum readerShape) {
        this.readerShape = readerShape;
        return this;
    }

    public TextStyle getShape() {
        ShapeEnum shapeEnum = getReaderShape();
        return getShapeMap().get(shapeEnum);
    }

    @XmlType(name = "shapeEnum")
    @XmlEnum
    public enum ShapeEnum {

        BASE_SHAPE("Base Shape"),
        BLACK_SHAPE("Black Shape"),
        OLD_DOS_SHAPE("Old Dos Shape"),
        HEIKES_SHAPE("Heike's Shape"),
        JOSHIS_SHAPE("Joshi's Shape"),
        HARRYS_SHAPE("Harry's Shape");

        private final String shapeValue;

        ShapeEnum(String shapeValue) {
            this.shapeValue = shapeValue;
        }

        public String getShapeValue() {
            return shapeValue;
        }

        public String value() {
            return name();
        }

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
