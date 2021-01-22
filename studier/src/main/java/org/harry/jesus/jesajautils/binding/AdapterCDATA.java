package org.harry.jesus.jesajautils.binding;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * The type Adapter cdata.
 */
public class AdapterCDATA extends XmlAdapter<String, String> {

    @Override
    public String marshal(String arg0) throws Exception {
        return "![CDATA[" + arg0 + "]]";
    }
    @Override
    public String unmarshal(String arg0) throws Exception {
        return arg0;
    }
}
