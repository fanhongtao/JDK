/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.bind.annotation.adapters;

import javax.xml.bind.DatatypeConverter;

/**
 * {@link XmlAdapter} for <tt>xs:hexBinary</tt>.
 *
 * <p>
 * This {@link XmlAdapter} binds <tt>byte[]</tt> to the hexBinary representation in XML.
 *
 * @author Kohsuke Kawaguchi
 * @since JAXB 2.0
 */
public final class HexBinaryAdapter extends XmlAdapter<String,byte[]> {
    public byte[] unmarshal(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }

    public String marshal(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }
}
