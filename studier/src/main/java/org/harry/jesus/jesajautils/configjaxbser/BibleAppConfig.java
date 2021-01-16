package org.harry.jesus.jesajautils.configjaxbser;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "base",
        "dictConf"
})
@XmlRootElement
public class BibleAppConfig {

    @XmlElement( required = true)
    BaseConfig base;

    @XmlElement( required = true)
    BiblesDictConfig dictConf;

    public BaseConfig getBaseConfig() {
        if (base == null) {
            base = new BaseConfig();
        }
        return base;
    }

    public BibleAppConfig setBaseConfig(BaseConfig baseConfig) {
        this.base = baseConfig;
        return this;
    }

    public BiblesDictConfig getDictConfig() {
        if (dictConf == null) {
            dictConf = new BiblesDictConfig();
        }
        return dictConf;
    }

    public BibleAppConfig setDictConfig(BiblesDictConfig dictConfig) {
        this.dictConf = dictConfig;
        return this;
    }
}
