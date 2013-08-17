/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.text.html.parser;

import java.io.PrintStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.BitSet;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Properties;
import java.net.URL;

/**
 * The representation of an SGML DTD. This is produced by the DTDParser.
 * The resulting DTD object describes a document syntax and is needed
 * to parser HTML documents using the Parser. It contains a list of
 * elements and their attributes as well as a list of entities defined
 * in the DTD.
 *
 * @see Element
 * @see AttributeList
 * @see ContentModel
 * @see DTDParser
 * @see Parser
 * @author Arthur van Hoff
 * @version 1.11 02/06/02
 */
public
class DTD implements DTDConstants {
    public String name;
    public Vector elements = new Vector();
    public Hashtable elementHash = new Hashtable();
    public Hashtable entityHash = new Hashtable();
    public final Element pcdata = getElement("#pcdata");
    public final Element html = getElement("html");
    public final Element meta = getElement("meta");
    public final Element base = getElement("base");
    public final Element isindex = getElement("isindex");
    public final Element head = getElement("head");
    public final Element body = getElement("body");
    public final Element applet = getElement("applet");
    public final Element param = getElement("param");
    public final Element p = getElement("p");
    public final Element title = getElement("title");
    final Element style = getElement("style");
    final Element link = getElement("link");

    public static int FILE_VERSION = 1;

    /**
     * Create a new DTD.
     */
    protected DTD(String name) {
	this.name = name;
	defEntity("#RE", GENERAL, '\r');
	defEntity("#RS", GENERAL, '\n');
	defEntity("#SPACE", GENERAL, ' ');
	defineElement("unknown", EMPTY, false, true, null, null, null, null);
    }

    /**
     * Get the name of the DTD.
     */
    public String getName() {
	return name;
    }

    /**
     * Get an entity by name.
     */
    public Entity getEntity(String name) {
	return (Entity)entityHash.get(name);
    }

    /**
     * Get a character entity.
     */
    public Entity getEntity(int ch) {
	return (Entity)entityHash.get(new Integer(ch));
    }

    /**
     * Return true if the element is part of the dtd
     * else false.
     */
    boolean elementExists(String name) {
	Element e = (Element)elementHash.get(name);
	return ((e == null) ? false : true);
    }

    /**
     * Get an element by name. A new element is
     * created if the element doesn't exist.
     */
    public Element getElement(String name) {
	Element e = (Element)elementHash.get(name);
	if (e == null) {
	    e = new Element(name, elements.size());
	    elements.addElement(e);
	    elementHash.put(name, e);
	}
	return e;
    }

    /**
     * Get an element by index.
     */
    public Element getElement(int index) {
	return (Element)elements.elementAt(index);
    }

    /**
     * Define an entity.
     */
    public Entity defineEntity(String name, int type, char data[]) {
	Entity ent = (Entity)entityHash.get(name);
	if (ent == null) {
	    ent = new Entity(name, type, data);
	    entityHash.put(name, ent);
	    if (((type & GENERAL) != 0) && (data.length == 1)) {
		switch (type & ~GENERAL) {
		  case CDATA:
		  case SDATA:
		    entityHash.put(new Integer(data[0]), ent);
		    break;
		}
	    }
	}
	return ent;
    }

    /**
     * Define an element.
     */
    public Element defineElement(String name, int type,
		       boolean omitStart, boolean omitEnd, ContentModel content,
		       BitSet exclusions, BitSet inclusions, AttributeList atts) {
	Element e = getElement(name);
	e.type = type;
	e.oStart = omitStart;
	e.oEnd = omitEnd;
	e.content = content;
	e.exclusions = exclusions;
	e.inclusions = inclusions;
	e.atts = atts;
	return e;
    }

    /**
     * Define the attributes of an element.
     */
    public void defineAttributes(String name, AttributeList atts) {
	Element e = getElement(name);
	e.atts = atts;
    }

    /**
     * Define a character entity.
     */
    public Entity defEntity(String name, int type, int ch) {
	char data[] = {(char)ch};
	return defineEntity(name, type, data);
    }

    /**
     * Define an entity.
     */
    protected Entity defEntity(String name, int type, String str) {
	int len = str.length();
	char data[] = new char[len];
	str.getChars(0, len, data, 0);
	return defineEntity(name, type, data);
    }

    /**
     * Define an element.
     */
    protected Element defElement(String name, int type,
		       boolean omitStart, boolean omitEnd, ContentModel content,
		       String[] exclusions, String[] inclusions, AttributeList atts) {
	BitSet excl = null;
	if (exclusions != null && exclusions.length > 0) {
	    excl = new BitSet();
	    for (int i = 0; i < exclusions.length; i++) {
		String str = exclusions[i];
		if (str.length() > 0) {
		    excl.set(getElement(str).getIndex());
		}
	    }
	}
	BitSet incl = null;
	if (inclusions != null && inclusions.length > 0) {
	    incl = new BitSet();
	    for (int i = 0; i < inclusions.length; i++) {
		String str = inclusions[i];
		if (str.length() > 0) {
		    incl.set(getElement(str).getIndex());
		}
	    }
	}
	return defineElement(name, type, omitStart, omitEnd, content, excl, incl, atts);
    }

    /**
     * Define an attribute list
     */
    protected AttributeList defAttributeList(String name, int type, int modifier, String value, String values, AttributeList atts) {
	Vector vals = null;
	if (values != null) {
	    vals = new Vector();
	    for (StringTokenizer s = new StringTokenizer(values, "|") ; s.hasMoreTokens() ;) {
		String str = s.nextToken();
		if (str.length() > 0) {
		    vals.addElement(str);
		}
	    }
	}
	return new AttributeList(name, type, modifier, value, vals, atts);
    }

    /**
     * Define a content model
     */
    protected ContentModel defContentModel(int type, Object obj, ContentModel next) {
	return new ContentModel(type, obj, next);
    }

    /**
     * Return a string representation.
     */
    public String toString() {
	return name;
    }

    /**
     * The hashtable of DTDs.
     */
    static Hashtable dtdHash = new Hashtable();

  public static void putDTDHash(String name, DTD dtd) {
    dtdHash.put(name, dtd);
  }
    /**
     * Get a DTD.
     */
    public static DTD getDTD(String name) throws IOException {
	name = name.toLowerCase();
	DTD dtd = (DTD)dtdHash.get(name);
	if (dtd == null)
	  dtd = new DTD(name);

	return dtd;
    }

    public void read(DataInputStream in) throws IOException {
	if (in.readInt() != FILE_VERSION) {
	}

	//
	// Read the list of names
	//
	String[] names = new String[in.readShort()];
	for (int i = 0; i < names.length; i++) {
	    names[i] = in.readUTF();
	}


	//
	// Read the entities
	//
	int num = in.readShort();
	for (int i = 0; i < num; i++) {
	    short nameId = in.readShort();
	    int type = in.readByte();
	    String name = in.readUTF();
	    defEntity(names[nameId], type | GENERAL, name);
	}

	// Read the elements
	//
	num = in.readShort();
	for (int i = 0; i < num; i++) {
	    short nameId = in.readShort();
	    int type = in.readByte();
	    byte flags = in.readByte();
	    ContentModel m = readContentModel(in, names);
	    String[] exclusions = readNameArray(in, names);
	    String[] inclusions = readNameArray(in, names);
	    AttributeList atts = readAttributeList(in, names);
	    defElement(names[nameId], type,
		       ((flags & 0x01) != 0), ((flags & 0x02) != 0),
		       m, exclusions, inclusions, atts);
	}
    }

    private ContentModel readContentModel(DataInputStream in, String[] names)
		throws IOException {
	byte flag = in.readByte();
	switch(flag) {
	    case 0:		// null
		return null;
	    case 1: {		// content_c
		int type = in.readByte();
		ContentModel m = readContentModel(in, names);
		ContentModel next = readContentModel(in, names);
		return defContentModel(type, m, next);
	    }
	    case 2: {		// content_e
		int type = in.readByte();
		Element el = getElement(names[in.readShort()]);
		ContentModel next = readContentModel(in, names);
		return defContentModel(type, el, next);
	    }
	default:
		throw new IOException("bad bdtd");
	}
    }

    private String[] readNameArray(DataInputStream in, String[] names)
		throws IOException {
	int num = in.readShort();
	if (num == 0) {
	    return null;
	}
	String[] result = new String[num];
	for (int i = 0; i < num; i++) {
	    result[i] = names[in.readShort()];
	}
	return result;
    }


    private AttributeList readAttributeList(DataInputStream in, String[] names)
		throws IOException  {
	AttributeList result = null;
	for (int num = in.readByte(); num > 0; --num) {
	    short nameId = in.readShort();
	    int type = in.readByte();
	    int modifier = in.readByte();
	    short valueId = in.readShort();
	    String value = (valueId == -1) ? null : names[valueId];
	    Vector values = null;
	    short numValues = in.readShort();
	    if (numValues > 0) {
		values = new Vector(numValues);
		for (int i = 0; i < numValues; i++) {
		    values.addElement(names[in.readShort()]);
		}
	    }
result = new AttributeList(names[nameId], type, modifier, value,
				       values, result);
	    // We reverse the order of the linked list by doing this, but
	    // that order isn't important.
	}
	return result;
    }

}
