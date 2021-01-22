package org.harry.jesus.jesajautils.configjaxbser;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Bible app config.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "base",
        "dictConf"
})
@XmlRootElement
public class BibleAppConfig {

    /**
     * The Base.
     */
    @XmlElement( required = true)
    BaseConfig base;

    /**
     * The Dict conf.
     */
    @XmlElement( required = true)
    BiblesDictConfig dictConf;

    /**
     * Gets base config.
     *
     * @return the base config
     */
    public BaseConfig getBaseConfig() {
        if (base == null) {
            base = new BaseConfig();
        }
        return base;
    }

    /**
     * Sets base config.
     *
     * @param baseConfig the base config
     * @return the base config
     */
    public BibleAppConfig setBaseConfig(BaseConfig baseConfig) {
        this.base = baseConfig;
        return this;
    }

    /**
     * Gets dict config.
     *
     * @return the dict config
     */
    public BiblesDictConfig getDictConfig() {
        if (dictConf == null) {
            dictConf = new BiblesDictConfig();
        }
        return dictConf;
    }

    /**
     * Sets dict config.
     *
     * @param dictConfig the dict config
     * @return the dict config
     */
    public BibleAppConfig setDictConfig(BiblesDictConfig dictConfig) {
        this.dictConf = dictConfig;
        return this;
    }
}
