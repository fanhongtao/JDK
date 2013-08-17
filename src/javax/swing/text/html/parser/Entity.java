/*
 * @(#)Entity.java	1.4 98/08/26
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.CharArrayReader;
import java.net.URL;

/**
 * An entity in as described in a DTD using the ENTITY construct.
 * It defines the type and value of the the entity.
 *
 * @see DTD
 * @version 1.4, 08/26/98
 * @author Arthur van Hoff
 */
public final
class Entity implements DTDConstants {
    public String name;
    public int type;
    public char data[];

    /**
     * Create an entity.
     */
    public Entity(String name, int type, char data[]) {
	this.name = name;
	this.type = type;
	this.data = data;
    }

    /**
     * Get the name of the entity.
     */
    public String getName() {
	return name;
    }

    /**
     * Get the type of the entity.
     */
    public int getType() {
	return type & 0xFFFF;
    }

    /**
     * Return true if it is a parameter entity.
     */
    public boolean isParameter() {
	return (type & PARAMETER) != 0;
    }

    /**
     * Return true if it is a parameter entity.
     */
    public boolean isGeneral() {
	return (type & GENERAL) != 0;
    }

    /**
     * Return the data.
     */
    public char getData()[] {
	return data;
    }

    /**
     * Return the data as a string.
     */
    public String getString() {
	return new String(data, 0, data.length);
    }


    static Hashtable entityTypes = new Hashtable();

    static {
	entityTypes.put("PUBLIC", new Integer(PUBLIC));
	entityTypes.put("CDATA", new Integer(CDATA));
	entityTypes.put("SDATA", new Integer(SDATA));
	entityTypes.put("PI", new Integer(PI));
	entityTypes.put("STARTTAG", new Integer(STARTTAG));
	entityTypes.put("ENDTAG", new Integer(ENDTAG));
	entityTypes.put("MS", new Integer(MS));
	entityTypes.put("MD", new Integer(MD));
	entityTypes.put("SYSTEM", new Integer(SYSTEM));
    }

    public static int name2type(String nm) {
	Integer i = (Integer)entityTypes.get(nm);
	return (i == null) ? CDATA : i.intValue();
    }
}

