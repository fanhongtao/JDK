/*
 * @(#)Element.java	1.4 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.text.html.parser;

import java.util.Hashtable;
import java.util.BitSet;
import java.io.*;

/**
 * An element as described in a DTD using the ELEMENT construct.
 * This is essentiall the description of a tag. It describes the
 * type, content model, attributes, attribute types etc. It is used
 * to correctly parse a document by the Parser.
 *
 * @see DTD
 * @see AttributeList
 * @version 1.4, 08/26/98
 * @author Arthur van Hoff
 */
public final
class Element implements DTDConstants, Serializable {
    public int index;
    public String name;
    public boolean oStart;
    public boolean oEnd;
    public BitSet inclusions;
    public BitSet exclusions;
    public int type = ANY;
    public ContentModel content;
    public AttributeList atts;

    static int maxIndex = 0;

    /**
     * A field to store user data. Mostly used to store
     * style sheets.
     */
    public Object data;

    Element() {
    }

    /**
     * Create a new element.
     */
    Element(String name, int index) {
	this.name = name;
	this.index = index;
	maxIndex = Math.max(maxIndex, index);
    }

    /**
     * Get the name of the element.
     */
    public String getName() {
	return name;
    }

    /**
     * Return true if the start tag can be omitted.
     */
    public boolean omitStart() {
	return oStart;
    }

    /**
     * Return true if the end tag can be omitted.
     */
    public boolean omitEnd() {
	return oEnd;
    }

    /**
     * Get type.
     */
    public int getType() {
	return type;
    }

    /**
     * Get content model
     */
    public ContentModel getContent() {
	return content;
    }

    /**
     * Get the attributes.
     */
    public AttributeList getAttributes() {
	return atts;
    }

    /**
     * Get index.
     */
    public int getIndex() {
	return index;
    }

    /**
     * Check if empty
     */
    public boolean isEmpty() {
	return type == EMPTY;
    }

    /**
     * Convert to a string.
     */
    public String toString() {
	return name;
    }

    /**
     * Get an attribute by name.
     */
    public AttributeList getAttribute(String name) {
	for (AttributeList a = atts ; a != null ; a = a.next) {
	    if (a.name.equals(name)) {
		return a;
	    }
	}
	return null;
    }

    /**
     * Get an attribute by value.
     */
    public AttributeList getAttributeByValue(String name) {
	for (AttributeList a = atts ; a != null ; a = a.next) {
	    if ((a.values != null) && a.values.contains(name)) {
		return a;
	    }
	}
	return null;
    }


    static Hashtable contentTypes = new Hashtable();

    static {
	contentTypes.put("CDATA", new Integer(CDATA));
	contentTypes.put("RCDATA", new Integer(RCDATA));
	contentTypes.put("EMPTY", new Integer(EMPTY));
	contentTypes.put("ANY", new Integer(ANY));
    }

    public static int name2type(String nm) {
	Integer val = (Integer)contentTypes.get(nm);
	return (val != null) ? val.intValue() : 0;
    }
}
