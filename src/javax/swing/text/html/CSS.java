/*
 * @(#)CSS.java	1.6 98/08/26
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
package javax.swing.text.html;

import java.util.Hashtable;

/**
 * Defines a set of
 * <a href="http://www.w3.org/TR/REC-CSS1">CSS attributes</a>
 * as a typesafe enumeration.  The html View implementations use
 * css attributes to determine how they will render.
 *
 * @author  Timothy Prinzing
 * @version 1.6 08/26/98
 * @see StyleSheet
 */
public class CSS {

    /**
     * Definitions to be used as a key on AttributeSet's
     * that might hold css attributes.  Since this is a
     * closed set (i.e. defined exactly by the specification),
     * it is final and cannot be extended.
     */
    public static final class Attribute {

	private Attribute(String name, String defaultValue, boolean inherited) {
	    this.name = name;
	    this.defaultValue = defaultValue;
	    this.inherited = inherited;
	}

	/**
	 * The string representation of the attribute.  This
	 * should exactly match the string specified in the
	 * css specification.
	 */
	public String toString() {
	    return name;
	}

	/**
	 * Fetch the default value for the attribute.
	 * If there is no default value (such as for
	 * composite attributes), null will be returned.
	 */
	public String getDefaultValue() {
	    return defaultValue;
	}

	/**
	 * Indicates if the attribute should be inherited
	 * from the parent or not.
	 */
	public boolean isInherited() {
	    return inherited;
	}

	private String name;
	private String defaultValue;
	private boolean inherited;


	public static final Attribute BACKGROUND =
	    new Attribute("background", null, false);

	public static final Attribute BACKGROUND_ATTACHMENT =
	    new Attribute("background-attachment", "scroll", false);

	public static final Attribute BACKGROUND_COLOR =
	    new Attribute("background-color", "transparent", false);

	public static final Attribute BACKGROUND_IMAGE =
	    new Attribute("background-image", "none", false);

	public static final Attribute BACKGROUND_POSITION =
	    new Attribute("background-position", null, false);

	public static final Attribute BACKGROUND_REPEAT =
	    new Attribute("background-repeat", "repeat", false);

	public static final Attribute BORDER =
	    new Attribute("border", null, false);

	public static final Attribute BORDER_BOTTOM =
	    new Attribute("border-bottom", null, false);

	public static final Attribute BORDER_BOTTOM_WIDTH =
	    new Attribute("border-bottom-width", "medium", false);

	public static final Attribute BORDER_COLOR =
	    new Attribute("border-color", null, false);

	public static final Attribute BORDER_LEFT =
	    new Attribute("border-left", null, false);

	public static final Attribute BORDER_LEFT_WIDTH =
	    new Attribute("border-left-width", "medium", false);

	public static final Attribute BORDER_RIGHT =
	    new Attribute("border-right", null, false);

	public static final Attribute BORDER_RIGHT_WIDTH =
	    new Attribute("border-right-width", "medium", false);

	public static final Attribute BORDER_STYLE =
	    new Attribute("border-style", "none", false);

	public static final Attribute BORDER_TOP =
	    new Attribute("border-top", null, false);

	public static final Attribute BORDER_TOP_WIDTH =
	    new Attribute("border-top-width", "medium", false);

	public static final Attribute BORDER_WIDTH =
	    new Attribute("border-width", "medium", false);

	public static final Attribute CLEAR =
	    new Attribute("clear", "none", false);

	public static final Attribute COLOR =
	    new Attribute("color", null, true);

	public static final Attribute DISPLAY =
	    new Attribute("display", "block", false);

	public static final Attribute FLOAT =
	    new Attribute("float", "none", false);

	public static final Attribute FONT =
	    new Attribute("font", null, true);

	public static final Attribute FONT_FAMILY =
	    new Attribute("font-family", null, true);

	public static final Attribute FONT_SIZE =
	    new Attribute("font-size", "medium", true);

	public static final Attribute FONT_STYLE =
	    new Attribute("font-style", "normal", true);

	public static final Attribute FONT_VARIANT =
	    new Attribute("font-variant", "normal", true);

	public static final Attribute FONT_WEIGHT =
	    new Attribute("font-weight", "normal", true);

	public static final Attribute HEIGHT =
	    new Attribute("height", "auto", false);

	public static final Attribute LETTER_SPACING =
	    new Attribute("letter-spacing", "normal", true);

	public static final Attribute LINE_HEIGHT =
	    new Attribute("line-height", "normal", true);

	public static final Attribute LIST_STYLE =
	    new Attribute("list-style", null, true);

	public static final Attribute LIST_STYLE_IMAGE =
	    new Attribute("list-style-image", "none", true);

	public static final Attribute LIST_STYLE_POSITION =
	    new Attribute("list-style-position", "outside", true);

	public static final Attribute LIST_STYLE_TYPE =
	    new Attribute("list-style-type", "disc", true);

	public static final Attribute MARGIN =
	    new Attribute("margin", null, false);

	public static final Attribute MARGIN_BOTTOM =
	    new Attribute("margin-bottom", "0", false);

	public static final Attribute MARGIN_LEFT =
	    new Attribute("margin-left", "0", false);

	public static final Attribute MARGIN_RIGHT =
	    new Attribute("margin-right", "0", false);

	public static final Attribute MARGIN_TOP =
	    new Attribute("margin-top", "0", false);

	public static final Attribute PADDING =
	    new Attribute("padding", null, false);

	public static final Attribute PADDING_BOTTOM =
	    new Attribute("padding-bottom", "0", false);

	public static final Attribute PADDING_LEFT =
	    new Attribute("padding-left", "0", false);

	public static final Attribute PADDING_RIGHT =
	    new Attribute("padding-right", "0", false);

	public static final Attribute PADDING_TOP =
	    new Attribute("padding-top", "0", false);

	public static final Attribute TEXT_ALIGN =
	    new Attribute("text-align", null, true);

	public static final Attribute TEXT_DECORATION =
	    new Attribute("text-decoration", "none", true);

	public static final Attribute TEXT_INDENT =
	    new Attribute("text-indent", "0", true);

	public static final Attribute TEXT_TRANSFORM =
	    new Attribute("text-transform", "none", true);

	public static final Attribute VERTICAL_ALIGN =
	    new Attribute("vertical-align", "baseline", false);

	public static final Attribute WORD_SPACING =
	    new Attribute("word-spacing", "normal", true);

	public static final Attribute WHITE_SPACE =
	    new Attribute("whitespace", "normal", true);

	public static final Attribute WIDTH =
	    new Attribute("width", "auto", false);

	// All possible css attribute keys.
	static final Attribute[] allAttributes = {
	    BACKGROUND, BACKGROUND_ATTACHMENT, BACKGROUND_COLOR,
	    BACKGROUND_IMAGE, BACKGROUND_POSITION, BACKGROUND_REPEAT,
	    BORDER, BORDER_BOTTOM, BORDER_BOTTOM_WIDTH, BORDER_COLOR,
	    BORDER_LEFT, BORDER_LEFT_WIDTH, BORDER_RIGHT, BORDER_RIGHT_WIDTH,
	    BORDER_STYLE, BORDER_TOP, BORDER_TOP_WIDTH, BORDER_WIDTH,
	    CLEAR, COLOR, DISPLAY, FLOAT, FONT, FONT_FAMILY, FONT_SIZE,
	    FONT_STYLE, FONT_VARIANT, FONT_WEIGHT, HEIGHT, LETTER_SPACING,
	    LINE_HEIGHT, LIST_STYLE, LIST_STYLE_IMAGE, LIST_STYLE_POSITION,
	    LIST_STYLE_TYPE, MARGIN, MARGIN_BOTTOM, MARGIN_LEFT, MARGIN_RIGHT,
	    MARGIN_TOP, PADDING, PADDING_BOTTOM, PADDING_LEFT, PADDING_RIGHT,
	    PADDING_TOP, TEXT_ALIGN, TEXT_DECORATION, TEXT_INDENT, TEXT_TRANSFORM,
	    VERTICAL_ALIGN, WORD_SPACING, WHITE_SPACE, WIDTH
	};

    }

    static final class Value {

	private Value(String name) {
	    this.name = name;
	}

	/**
	 * The string representation of the attribute.  This
	 * should exactly match the string specified in the
	 * css specification.
	 */
	public String toString() {
	    return name;
	}

	static final Value INHERITED = new Value("inherited");
	static final Value NONE = new Value("none");
	static final Value DOTTED = new Value("dotted");
	static final Value DASHED = new Value("dashed");
	static final Value SOLID = new Value("solid");
	static final Value DOUBLE = new Value("double");
	static final Value GROOVE = new Value("groove");
	static final Value RIDGE = new Value("ridge");
	static final Value INSET = new Value("inset");
	static final Value OUTSET = new Value("outset");

	private String name;

	static final Value[] allValues = {
	    INHERITED, NONE, DOTTED, DASHED, SOLID, DOUBLE, GROOVE,
	    RIDGE, INSET, OUTSET
	};
    }

    private static final Hashtable attributeMap = new Hashtable();
    private static final Hashtable valueMap = new Hashtable();

    static {
	// load the attribute map
	for (int i = 0; i < Attribute.allAttributes.length; i++ ) {
	    attributeMap.put(Attribute.allAttributes[i].toString(),
			     Attribute.allAttributes[i]);
	}
	// load the value map
	for (int i = 0; i < Value.allValues.length; i++ ) {
	    valueMap.put(Value.allValues[i].toString(), 
			     Value.allValues[i]);
	}
    }

    /**
     * Return the set of all possible CSS attribute keys.
     */
    public static Attribute[] getAllAttributeKeys() {
	Attribute[] keys = new Attribute[Attribute.allAttributes.length];
	System.arraycopy(Attribute.allAttributes, 0, keys, 0, Attribute.allAttributes.length);
	return keys;
    }

    /**
     * Translate a string to a CSS.Attribute object.  This
     * will return null if there is no attribute by the given
     * name.
     * @param name the name of the css attribute to fetch the
     *  typesafe enumeration for.
     * @returns the CSS.Attribute object, or null if the string
     *  doesn't represent a valid attribute key.
     */
    public static final Attribute getAttribute(String name) {
	return (Attribute) attributeMap.get(name);
    }

    /**
     * Translate a string to a CSS.Value object.  This
     * will return null if there is no value by the given
     * name.
     * @param name the name of the css value to fetch the
     *  typesafe enumeration for.
     * @returns the CSS.Value object, or null if the string
     *  doesn't represent a valid css value name.  This does
     *  not mean it doesn't represent a valid css value.
     */
    static final Value getValue(String name) {
	return (Value) valueMap.get(name);
    }

}
