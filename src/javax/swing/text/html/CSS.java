/*
 * @(#)CSS.java	1.13 99/04/22
 *
 * Copyright 1998, 1999 by Sun Microsystems, Inc.,
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

import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.text.*;

/**
 * Defines a set of
 * <a href="http://www.w3.org/TR/REC-CSS1">CSS attributes</a>
 * as a typesafe enumeration.  The html View implementations use
 * css attributes to determine how they will render. This also defines
 * methods to map between CSS<->HTML<->StyleConstants.
 *
 * @author  Timothy Prinzing
 * @version 1.13 04/22/99
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
	// Lists.
	static final Value BLANK_LIST_ITEM = new Value("none");
	static final Value DISC = new Value("disc");
	static final Value CIRCLE = new Value("circle");
	static final Value SQUARE = new Value("square");
	static final Value DECIMAL = new Value("decimal");
	static final Value LOWER_ROMAN = new Value("lower-roman");
	static final Value UPPER_ROMAN = new Value("upper-roman");
	static final Value LOWER_ALPHA = new Value("lower-alpha");
	static final Value UPPER_ALPHA = new Value("upper-alpha");

	private String name;

	static final Value[] allValues = {
	    INHERITED, NONE, DOTTED, DASHED, SOLID, DOUBLE, GROOVE,
	    RIDGE, INSET, OUTSET, DISC, CIRCLE, SQUARE, DECIMAL,
	    LOWER_ROMAN, UPPER_ROMAN, LOWER_ALPHA, UPPER_ALPHA,
	    BLANK_LIST_ITEM
	};
    }

    public CSS() {
	baseFontSize = 3;
	// setup the css conversion table
	valueConvertor = new Hashtable();
	valueConvertor.put(CSS.Attribute.FONT_SIZE, new FontSize());
	valueConvertor.put(CSS.Attribute.FONT_FAMILY, new FontFamily());
	valueConvertor.put(CSS.Attribute.FONT_WEIGHT, new FontWeight());
	valueConvertor.put(CSS.Attribute.BORDER_STYLE, new BorderStyle());
	Object cv = new ColorValue();
	valueConvertor.put(CSS.Attribute.COLOR, cv);
	valueConvertor.put(CSS.Attribute.BACKGROUND_COLOR, cv);
	valueConvertor.put(CSS.Attribute.BORDER_COLOR, cv);
	Object lv = new LengthValue();
	valueConvertor.put(CSS.Attribute.MARGIN_TOP, lv);
	valueConvertor.put(CSS.Attribute.MARGIN_BOTTOM, lv);
	valueConvertor.put(CSS.Attribute.MARGIN_LEFT, lv);
	valueConvertor.put(CSS.Attribute.MARGIN_RIGHT, lv);
	valueConvertor.put(CSS.Attribute.PADDING_TOP, lv);
	valueConvertor.put(CSS.Attribute.PADDING_BOTTOM, lv);
	valueConvertor.put(CSS.Attribute.PADDING_LEFT, lv);
	valueConvertor.put(CSS.Attribute.PADDING_RIGHT, lv);
	valueConvertor.put(CSS.Attribute.BORDER_WIDTH, lv);
	valueConvertor.put(CSS.Attribute.BORDER_TOP_WIDTH, lv);
	valueConvertor.put(CSS.Attribute.BORDER_BOTTOM_WIDTH, lv);
	valueConvertor.put(CSS.Attribute.BORDER_LEFT_WIDTH, lv);
	valueConvertor.put(CSS.Attribute.BORDER_RIGHT_WIDTH, lv);
	valueConvertor.put(CSS.Attribute.TEXT_INDENT, lv);
	valueConvertor.put(CSS.Attribute.WIDTH, lv);
	valueConvertor.put(CSS.Attribute.HEIGHT, lv);
	Object sv = new StringValue();
	valueConvertor.put(CSS.Attribute.FONT_STYLE, sv);
	valueConvertor.put(CSS.Attribute.TEXT_DECORATION, sv);
	valueConvertor.put(CSS.Attribute.TEXT_ALIGN, sv);
	valueConvertor.put(CSS.Attribute.VERTICAL_ALIGN, sv);
	valueConvertor.put(CSS.Attribute.LIST_STYLE_TYPE, new ListType());
	Object generic = new CssValue();
	int n = CSS.Attribute.allAttributes.length;
	for (int i = 0; i < n; i++) {
	    CSS.Attribute key = CSS.Attribute.allAttributes[i];
	    if (valueConvertor.get(key) == null) {
		valueConvertor.put(key, generic);
	    }
	}
    }

    /**
     * Sets the base font size. <code>sz</code> is a CSS value, and is
     * not necessarily the point size. Use getPointSize to determine the
     * point size corresponding to <code>sz</code>.
     */
    void setBaseFontSize(int sz) {
	if (sz < 1)
	  baseFontSize = 0;
	else if (sz > 7)
	  baseFontSize = 7;
	else
	  baseFontSize = sz;
    }

    /**
     * Sets the base font size from the passed in string.
     */
    void setBaseFontSize(String size) {
	int relSize, absSize, diff;

	if (size != null) {
	    if (size.startsWith("+")) {
		relSize = Integer.valueOf(size.substring(1)).intValue();
		setBaseFontSize(baseFontSize + relSize);
	    } else if (size.startsWith("-")) {
		relSize = -Integer.valueOf(size.substring(1)).intValue();
		setBaseFontSize(baseFontSize + relSize);
	    } else {
		setBaseFontSize(Integer.valueOf(size).intValue());
	    }
	}
    }

    /**
     * Returns the base font size.
     */
    int getBaseFontSize() {
	return baseFontSize;
    }

    /**
     * Gets the internal CSS representation of <code>value</code> which is
     * a CSS value of the CSS attribute named <code>key</code>.
     */
    Object getInternalCSSValue(CSS.Attribute key, String value) {
	CssValue conv = (CssValue) valueConvertor.get(key);
	return conv.parseCssValue(value);
    }

    /**
     * Maps from a StyleConstants to a CSS Attribute.
     */
    Attribute styleConstantsKeyToCSSKey(StyleConstants sc) {
	return (Attribute)styleConstantToCssMap.get(sc);
    }

    /**
     * Maps from a StyleConstants value to a CSS value.
     */
    Object styleConstantsValueToCSSValue(StyleConstants sc,
					 Object styleValue) {
	Object cssKey = styleConstantsKeyToCSSKey(sc);
	if (cssKey != null) {
	    CssValue conv = (CssValue)valueConvertor.get(cssKey);
	    return conv.fromStyleConstants(sc, styleValue);
	}
	return null;
    }

    /**
     * Converts the passed in CSS value to a StyleConstants value.
     * <code>key</code> identifies the CSS attribute being mapped.
     */
    Object cssValueToStyleConstantsValue(StyleConstants key, Object value) {
	if (value instanceof CssValue) {
	    return ((CssValue)value).toStyleConstants((StyleConstants)key);
	}
	return null;
    }

    /**
     * Returns the font for the values in the passed in AttributeSet.
     * It is assumed the keys will be CSS.Attribute keys.
     * <code>sc</code> is the StyleContext that will be messaged to get
     * the font once the size, name and style have been determined.
     */
    Font getFont(StyleContext sc, AttributeSet a, int defaultSize) {
	// PENDING(prinz) this is a 1.1 based implementation, need to also
	// have a 1.2 version.
	FontSize sizeValue = (FontSize)a.getAttribute(CSS.Attribute.FONT_SIZE);

	int size = (sizeValue != null) ? (int) sizeValue.getValue(a) :
	                                 defaultSize;

	/*
	 * If the vertical alignment is set to either superscirpt or
	 * subscript we reduce the font size by 2 points.
	 */
	StringValue vAlignV = (StringValue)a.getAttribute
	                      (CSS.Attribute.VERTICAL_ALIGN);
	if ((vAlignV != null)) {
	    String vAlign = vAlignV.toString();
	    if ((vAlign.indexOf("sup") >= 0) ||
		(vAlign.indexOf("sub") >= 0)) {
		size -= 2;
	    }
	}
	
	FontFamily familyValue = (FontFamily)a.getAttribute
	                                    (CSS.Attribute.FONT_FAMILY);
	String family = (familyValue != null) ? familyValue.getValue() :
	                          "SansSerif";
	int style = Font.PLAIN;
	FontWeight weightValue = (FontWeight) a.getAttribute
	                          (CSS.Attribute.FONT_WEIGHT);
	if ((weightValue != null) && (weightValue.getValue() > 400)) {
	    style |= Font.BOLD;
	}
	Object fs = a.getAttribute(CSS.Attribute.FONT_STYLE);
	if ((fs != null) && (fs.toString().indexOf("italic") >= 0)) {
	    style |= Font.ITALIC;
	}
	Font f = sc.getFont(family, style, size);
	return f;
    }

    /**
     * Takes a set of attributes and turn it into a color
     * specification.  This might be used to specify things
     * like brighter, more hue, etc.
     * This will return null if there is no value for <code>key</code>.
     *
     * @parram key CSS.Attribute identifying where color is stored.
     * @param a the set of attributes
     * @return the color
     */
    Color getColor(AttributeSet a, CSS.Attribute key) {
	ColorValue cv = (ColorValue) a.getAttribute(key);
	if (cv != null) {
	    return cv.getValue();
	}
	return null;
    }

    /**
     * Returns the size of a font from the passed in string.
     *
     * @param size CSS string describing font size
     * @param baseFontSize size to use for relative units.
     */
    float getPointSize(String size) {
	int relSize, absSize, diff, index;
	if (size != null) {
	    if (size.startsWith("+")) {
		relSize = Integer.valueOf(size.substring(1)).intValue();
		return getPointSize(baseFontSize + relSize);
	    } else if (size.startsWith("-")) {
		relSize = -Integer.valueOf(size.substring(1)).intValue();
		return getPointSize(baseFontSize + relSize);
	    } else {
		absSize = Integer.valueOf(size).intValue();
		return getPointSize(absSize);
	    }
	}
	return 0;
    }

    /**
     * Returns the length of the attribute in <code>a</code> with
     * key <code>key</code>.
     */
    float getLength(AttributeSet a, CSS.Attribute key) {
	LengthValue lv = (LengthValue) a.getAttribute(key);
	float len = (lv != null) ? lv.getValue() : 0;
	return len;
    }

    /**
     * Convert a set of html attributes to an equivalent
     * set of css attributes.
     *
     * @param AttributeSet containing the HTML attributes.
     * @return AttributeSet containing the corresponding CSS attributes.
     *        The AttributeSet will be empty if there are no mapping
     *        CSS attributes.
     */
    AttributeSet translateHTMLToCSS(AttributeSet htmlAttrSet) {
	MutableAttributeSet cssAttrSet = new SimpleAttributeSet();
	Element elem = (Element)htmlAttrSet;
	HTML.Tag tag = getHTMLTag(htmlAttrSet);
	if ((tag == HTML.Tag.TD) || (tag == HTML.Tag.TH)) {
	    // translate border width into the cells
	    AttributeSet tableAttr = elem.getParentElement().
		                     getParentElement().getAttributes();
	    translateAttribute(HTML.Attribute.BORDER, tableAttr, cssAttrSet);
	    String pad = (String)tableAttr.getAttribute(HTML.Attribute.CELLPADDING);
	    if (pad != null) {
		Object v = getInternalCSSValue(CSS.Attribute.PADDING_TOP, pad);
		cssAttrSet.addAttribute(CSS.Attribute.PADDING_TOP, v);
		cssAttrSet.addAttribute(CSS.Attribute.PADDING_BOTTOM, v);
		cssAttrSet.addAttribute(CSS.Attribute.PADDING_LEFT, v);
		cssAttrSet.addAttribute(CSS.Attribute.PADDING_RIGHT, v);
	    }
	}
	if (elem.isLeaf()) {
	    translateEmbeddedAttributes(htmlAttrSet, cssAttrSet);
	} else {
	    translateAttributes(tag, htmlAttrSet, cssAttrSet);
	}
	return cssAttrSet;
    }


    private static final Hashtable attributeMap = new Hashtable();
    private static final Hashtable valueMap = new Hashtable();
    /**
     * The html/css size model has seven slots
     * that one can assign sizes to.
     */
    static int sizeMap[] = { 8, 10, 12, 14, 18, 24, 36 };

    /**
     * The hashtable and the static initalization block below,
     * set up a mapping from well-known HTML attributes to
     * CSS attributes.  For the most part, there is a 1-1 mapping
     * between the two.  However in the case of certain HTML
     * attributes for example HTML.Attribute.VSPACE or
     * HTML.Attribute.HSPACE, end up mapping to two CSS.Attribute's.
     * Therefore, the value associated with each HTML.Attribute.
     * key ends up being an array of CSS.Attribute.* objects.
     */
    private static final Hashtable htmlAttrToCssAttrMap = new Hashtable(20);

    /**
     * The hashtable and static initialization that follows sets
     * up a translation from StyleConstants (i.e. the <em>well known</em>
     * attributes) to the associated CSS attributes.
     */
    private static final Hashtable styleConstantToCssMap = new Hashtable(17);
    /** Maps from HTML value to a CSS value. Used in internal mapping. */
    private static final Hashtable htmlValueToCssValueMap = new Hashtable(8);
    /** Maps from CSS value (string) to internal value. */
    private static final Hashtable cssValueToInternalValueMap = new Hashtable(8);

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

	htmlAttrToCssAttrMap.put(HTML.Attribute.COLOR,
				 new CSS.Attribute[]{CSS.Attribute.COLOR});
	htmlAttrToCssAttrMap.put(HTML.Attribute.TEXT,
				 new CSS.Attribute[]{CSS.Attribute.COLOR});
	htmlAttrToCssAttrMap.put(HTML.Attribute.CLEAR,
				 new CSS.Attribute[]{CSS.Attribute.CLEAR});
	htmlAttrToCssAttrMap.put(HTML.Attribute.BACKGROUND,
				 new CSS.Attribute[]{CSS.Attribute.BACKGROUND_IMAGE});
	htmlAttrToCssAttrMap.put(HTML.Attribute.BGCOLOR,
				 new CSS.Attribute[]{CSS.Attribute.BACKGROUND_COLOR});
	htmlAttrToCssAttrMap.put(HTML.Attribute.WIDTH,
				 new CSS.Attribute[]{CSS.Attribute.WIDTH});
	htmlAttrToCssAttrMap.put(HTML.Attribute.HEIGHT,
				 new CSS.Attribute[]{CSS.Attribute.HEIGHT});
	htmlAttrToCssAttrMap.put(HTML.Attribute.BORDER,
				 new CSS.Attribute[]{CSS.Attribute.BORDER_WIDTH});
	htmlAttrToCssAttrMap.put(HTML.Attribute.CELLPADDING,
				 new CSS.Attribute[]{CSS.Attribute.PADDING});
	htmlAttrToCssAttrMap.put(HTML.Attribute.CELLSPACING,
				 new CSS.Attribute[]{CSS.Attribute.MARGIN});
	htmlAttrToCssAttrMap.put(HTML.Attribute.MARGINWIDTH,
				 new CSS.Attribute[]{CSS.Attribute.MARGIN_LEFT,
						     CSS.Attribute.MARGIN_RIGHT});
	htmlAttrToCssAttrMap.put(HTML.Attribute.MARGINHEIGHT,
				 new CSS.Attribute[]{CSS.Attribute.MARGIN_TOP,
						     CSS.Attribute.MARGIN_BOTTOM});
	htmlAttrToCssAttrMap.put(HTML.Attribute.HSPACE,
				 new CSS.Attribute[]{CSS.Attribute.PADDING_LEFT,
						     CSS.Attribute.PADDING_RIGHT});
	htmlAttrToCssAttrMap.put(HTML.Attribute.VSPACE,
				 new CSS.Attribute[]{CSS.Attribute.PADDING_BOTTOM,
						     CSS.Attribute.PADDING_TOP});
	htmlAttrToCssAttrMap.put(HTML.Attribute.FACE,
				 new CSS.Attribute[]{CSS.Attribute.FONT_FAMILY});
	htmlAttrToCssAttrMap.put(HTML.Attribute.SIZE,
				 new CSS.Attribute[]{CSS.Attribute.FONT_SIZE});
	htmlAttrToCssAttrMap.put(HTML.Attribute.VALIGN,
				 new CSS.Attribute[]{CSS.Attribute.VERTICAL_ALIGN});
	htmlAttrToCssAttrMap.put(HTML.Attribute.ALIGN,
				 new CSS.Attribute[]{CSS.Attribute.VERTICAL_ALIGN,
						     CSS.Attribute.TEXT_ALIGN,
						     CSS.Attribute.FLOAT});
	htmlAttrToCssAttrMap.put(HTML.Attribute.TYPE,
				 new CSS.Attribute[]{CSS.Attribute.LIST_STYLE_TYPE});

	// initialize StyleConstants mapping 
	styleConstantToCssMap.put(StyleConstants.FontFamily, 
				  CSS.Attribute.FONT_FAMILY);
	styleConstantToCssMap.put(StyleConstants.FontSize, 
				  CSS.Attribute.FONT_SIZE);
	styleConstantToCssMap.put(StyleConstants.Bold, 
				  CSS.Attribute.FONT_WEIGHT);
	styleConstantToCssMap.put(StyleConstants.Italic, 
				  CSS.Attribute.FONT_STYLE);
	styleConstantToCssMap.put(StyleConstants.Underline, 
				  CSS.Attribute.TEXT_DECORATION);
	styleConstantToCssMap.put(StyleConstants.StrikeThrough, 
				  CSS.Attribute.TEXT_DECORATION);
	styleConstantToCssMap.put(StyleConstants.Superscript, 
				  CSS.Attribute.VERTICAL_ALIGN);
	styleConstantToCssMap.put(StyleConstants.Subscript, 
				  CSS.Attribute.VERTICAL_ALIGN);
	styleConstantToCssMap.put(StyleConstants.Foreground, 
				  CSS.Attribute.COLOR);
	styleConstantToCssMap.put(StyleConstants.Background, 
				  CSS.Attribute.BACKGROUND_COLOR);
	styleConstantToCssMap.put(StyleConstants.FirstLineIndent, 
				  CSS.Attribute.TEXT_INDENT);
	styleConstantToCssMap.put(StyleConstants.LeftIndent, 
				  CSS.Attribute.MARGIN_LEFT);
	styleConstantToCssMap.put(StyleConstants.RightIndent, 
				  CSS.Attribute.MARGIN_RIGHT);
	styleConstantToCssMap.put(StyleConstants.SpaceAbove, 
				  CSS.Attribute.MARGIN_TOP);
	styleConstantToCssMap.put(StyleConstants.SpaceBelow, 
				  CSS.Attribute.MARGIN_BOTTOM);
	styleConstantToCssMap.put(StyleConstants.Alignment, 
				  CSS.Attribute.TEXT_ALIGN);

	// HTML->CSS
	htmlValueToCssValueMap.put("disc", CSS.Value.DISC);
	htmlValueToCssValueMap.put("square", CSS.Value.SQUARE);
	htmlValueToCssValueMap.put("circle", CSS.Value.CIRCLE);
	htmlValueToCssValueMap.put("1", CSS.Value.DECIMAL);
	htmlValueToCssValueMap.put("a", CSS.Value.LOWER_ALPHA);
	htmlValueToCssValueMap.put("A", CSS.Value.UPPER_ALPHA);
	htmlValueToCssValueMap.put("i", CSS.Value.LOWER_ROMAN);
	htmlValueToCssValueMap.put("I", CSS.Value.UPPER_ROMAN);

	// CSS-> internal CSS
	cssValueToInternalValueMap.put("disc", CSS.Value.DISC);
	cssValueToInternalValueMap.put("square", CSS.Value.SQUARE);
	cssValueToInternalValueMap.put("circle", CSS.Value.CIRCLE);
	cssValueToInternalValueMap.put("decimal", CSS.Value.DECIMAL);
	cssValueToInternalValueMap.put("lower-roman", CSS.Value.LOWER_ROMAN);
	cssValueToInternalValueMap.put("upper-roman", CSS.Value.UPPER_ROMAN);
	cssValueToInternalValueMap.put("lower-alpha", CSS.Value.LOWER_ALPHA);
	cssValueToInternalValueMap.put("upper-alpha", CSS.Value.UPPER_ALPHA);

        // Register all the CSS attribute keys for archival/unarchival
	Object[] keys = CSS.Attribute.allAttributes;
	try {
	    for (int i = 0; i < keys.length; i++) {
		StyleContext.registerStaticAttributeKey(keys[i]);
	    }
	} catch (Throwable e) {
	    e.printStackTrace();
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


    //
    // Conversion related methods/classes
    //

    /**
     * Converts a type Color to a hex string
     * in the format "#RRGGBB"
     */
    static String colorToHex(Color color) {

      String colorstr = new String("#");

      // Red
      String str = Integer.toHexString(color.getRed());
      if (str.length() > 2)
	str = str.substring(0, 2);
      else if (str.length() < 2)
	colorstr += "0" + str;
      else
	colorstr += str;

      // Green
      str = Integer.toHexString(color.getGreen());
      if (str.length() > 2)
	str = str.substring(0, 2);
      else if (str.length() < 2)
	colorstr += "0" + str;
      else
	colorstr += str;

      // Blue
      str = Integer.toHexString(color.getBlue());
      if (str.length() > 2)
	str = str.substring(0, 2);
      else if (str.length() < 2)
	colorstr += "0" + str;
      else
	colorstr += str;

      return colorstr;
    }

     /**
      * Convert a "#FFFFFF" hex string to a Color.
      * If the color specification is bad, an attempt
      * will be made to fix it up.
      */
    static final Color hexToColor(String value) {
	 if (value.startsWith("#")) {
	     String digits = value.substring(1, Math.min(value.length(), 7));
	     String hstr = "0x" + digits;
	     Color c = Color.decode(hstr);
	     return c;
	 }
	 return null;
     }

    /**
     * Convert a color string "RED" or "#NNNNNN" to a Color.
     * Note: This will only convert the HTML3.2 colors strings
     *       or string of length 7
     *       otherwise, it will return null.
     */
    static Color stringToColor(String str) {
      Color color = null;

      if (str.charAt(0) == '#')
        color = hexToColor(str);
      else if (str.equalsIgnoreCase("Black"))
        color = hexToColor("#000000");
      else if(str.equalsIgnoreCase("Silver"))
        color = hexToColor("#C0C0C0");
      else if(str.equalsIgnoreCase("Gray"))
        color = hexToColor("#808080");
      else if(str.equalsIgnoreCase("White"))
        color = hexToColor("#FFFFFF");
      else if(str.equalsIgnoreCase("Maroon"))
        color = hexToColor("#800000");
      else if(str.equalsIgnoreCase("Red"))
        color = hexToColor("#FF0000");
      else if(str.equalsIgnoreCase("Purple"))
        color = hexToColor("#800080");
      else if(str.equalsIgnoreCase("Fuchsia"))
        color = hexToColor("#FF00FF");
      else if(str.equalsIgnoreCase("Green"))
        color = hexToColor("#008000");
      else if(str.equalsIgnoreCase("Lime"))
        color = hexToColor("#00FF00");
      else if(str.equalsIgnoreCase("Olive"))
        color = hexToColor("#808000");
      else if(str.equalsIgnoreCase("Yellow"))
        color = hexToColor("#FFFF00");
      else if(str.equalsIgnoreCase("Navy"))
        color = hexToColor("#000080");
      else if(str.equalsIgnoreCase("Blue"))
        color = hexToColor("#0000FF");
      else if(str.equalsIgnoreCase("Teal"))
        color = hexToColor("#008080");
      else if(str.equalsIgnoreCase("Aqua"))
        color = hexToColor("#00FFFF");
      return color;
    }

    static int getIndexOfSize(float pt) {
        for (int i = 0; i < sizeMap.length; i ++ )
                if (pt <= sizeMap[i])
                        return i;
        return sizeMap.length - 1;
    }

    /**
     * Return the point size, given a size index.
     */
    float getPointSize(int index) {
	if (index < 0)
	  return sizeMap[0];
	else if (index > sizeMap.length - 1)
	  return sizeMap[sizeMap.length - 1];
	else
	  return sizeMap[index];
    }


    private void translateEmbeddedAttributes(AttributeSet htmlAttrSet,
					     MutableAttributeSet cssAttrSet) {
	Enumeration keys = htmlAttrSet.getAttributeNames();
	while (keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    if (key instanceof HTML.Tag) {
		HTML.Tag tag = (HTML.Tag)key;
		Object o = htmlAttrSet.getAttribute(tag);
		if (o != null && o instanceof AttributeSet) {
		    translateAttributes(tag, (AttributeSet)o, cssAttrSet);
		}
	    } else if (key instanceof CSS.Attribute) {
		cssAttrSet.addAttribute(key, htmlAttrSet.getAttribute(key));
	    }
	}
    }

    private void translateAttributes(HTML.Tag tag,
					    AttributeSet htmlAttrSet,
					    MutableAttributeSet cssAttrSet) {
	Enumeration names = htmlAttrSet.getAttributeNames();
	while (names.hasMoreElements()) {
	    Object name = names.nextElement();

	    if (name instanceof HTML.Attribute) {
		HTML.Attribute key = (HTML.Attribute)name;

		/*
		 * HTML.Attribute.ALIGN needs special processing.
		 * It can map to to 1 of many(3) possible CSS attributes
		 * depending on the nature of the tag the attribute is
		 * part off and depending on the value of the attribute.
		 */
		if (key == HTML.Attribute.ALIGN) {
		    String htmlAttrValue = (String)htmlAttrSet.getAttribute(HTML.Attribute.ALIGN);
		    if (htmlAttrValue != null) {
			CSS.Attribute cssAttr = getCssAlignAttribute(tag, htmlAttrSet);
			if (cssAttr != null) {
			    Object o = getCssValue(cssAttr, htmlAttrValue);
			    if (o != null) {
				cssAttrSet.addAttribute(cssAttr, o);
			    }
			}
		    }
		} else {

		    /*
		     * The html size attribute has a mapping in the CSS world only
		     * if it is par of a font or base font tag.
		     */

		    if (key == HTML.Attribute.SIZE && !isHTMLFontTag(tag)) {
			continue;
		    }

		    translateAttribute(key, htmlAttrSet, cssAttrSet);
		}
	    } else if (name instanceof CSS.Attribute) {
		cssAttrSet.addAttribute(name, htmlAttrSet.getAttribute(name));
	    }
	}
    }

    private void translateAttribute(HTML.Attribute key,
					   AttributeSet htmlAttrSet,
					   MutableAttributeSet cssAttrSet) {
	/*
	 * In the case of all remaining HTML.Attribute's they
	 * map to 1 or more CCS.Attribute.
	 */
	CSS.Attribute[] cssAttrList = getCssAttribute(key);
	
	String htmlAttrValue = (String)htmlAttrSet.getAttribute(key);
	
	if (cssAttrList == null || htmlAttrValue == null) {
	    return;
	}
	for (int i = 0; i < cssAttrList.length; i++) {
	    Object o = getCssValue(cssAttrList[i], htmlAttrValue);
	    if (o != null) {
		cssAttrSet.addAttribute(cssAttrList[i], o);
	    }
	}
    }

    /**
     * Given a CSS.Attribute object and its corresponding HTML.Attribute's
     * value, this method returns a CssValue object to associate with the
     * CSS attribute.
     *
     * @param the CSS.Attribute
     * @param a String containing the value associated HTML.Attribtue.
     */
    Object getCssValue(CSS.Attribute cssAttr, String htmlAttrValue) {
	CssValue value = (CssValue)valueConvertor.get(cssAttr);
	Object o = value.parseHtmlValue(htmlAttrValue);
	return o;
    }

    /**
     * Maps an HTML.Attribute object to its appropriate CSS.Attributes.
     *
     * @param HTML.Attribute
     * @return CSS.Attribute[]
     */
    private CSS.Attribute[] getCssAttribute(HTML.Attribute hAttr) {
	return (CSS.Attribute[])htmlAttrToCssAttrMap.get(hAttr);
    }

    /**
     * Maps HTML.Attribute.ALIGN to either:
     *     CSS.Attribute.TEXT_ALIGN
     *     CSS.Attribute.FLOAT
     *     CSS.Attribute.VERTICAL_ALIGN
     * based on the tag associated with the attribute and the
     * value of the attribute.
     *
     * @param AttributeSet containing html attributes.
     * @return CSS.Attribute mapping for HTML.Attribute.ALIGN.
     */
    private CSS.Attribute getCssAlignAttribute(HTML.Tag tag,
						   AttributeSet htmlAttrSet) {
	return CSS.Attribute.TEXT_ALIGN;
/*
	String htmlAttrValue = (String)htmlAttrSet.getAttribute(HTML.Attribute.ALIGN);
	CSS.Attribute cssAttr = CSS.Attribute.TEXT_ALIGN;
	if (htmlAttrValue != null && htmlAttrSet instanceof Element) {
	    Element elem = (Element)htmlAttrSet;
	    if (!elem.isLeaf() && tag.isBlock() && validTextAlignValue(htmlAttrValue)) {
		return CSS.Attribute.TEXT_ALIGN;
	    } else if (isFloater(htmlAttrValue)) {
		return CSS.Attribute.FLOAT;
	    } else if (elem.isLeaf()) {
		return CSS.Attribute.VERTICAL_ALIGN;
	    }
 	}
	return null;
	*/
    }

    /**
     * Fetches the tag associated with the HTML AttributeSet.
     *
     * @param  AttributeSet containing the html attributes.
     * @return HTML.Tag
     */
    private HTML.Tag getHTMLTag(AttributeSet htmlAttrSet) {
	Object o = htmlAttrSet.getAttribute(StyleConstants.NameAttribute);
	if (o instanceof HTML.Tag) {
	    HTML.Tag tag = (HTML.Tag) o;
	    return tag;
	}
	return null;
    }


    private boolean isHTMLFontTag(HTML.Tag tag) {
	return (tag != null && ((tag == HTML.Tag.FONT) || (tag == HTML.Tag.BASEFONT)));
    }


    private boolean isFloater(String alignValue) {
	return (alignValue.equals("left") || alignValue.equals("right"));
    }

    private boolean validTextAlignValue(String alignValue) {
	return (isFloater(alignValue) || alignValue.equals("center"));
    }

    /**
     * Base class to CSS values in the attribute sets.  This
     * is intended to act as a convertor to/from other attribute
     * formats.
     * <p>
     * The css parser uses the parseCssValue method to convert
     * a string to whatever format is appropriate a given key
     * (i.e. these convertors are stored in a map using the
     * CSS.Attribute as a key and the CssValue as the value).
     * <p>
     * The html to css conversion process first converts the
     * HTML.Attribute to a CSS.Attribute, and then calls
     * the parseHtmlValue method on the value of the html
     * attribute to produce the corresponding css value.
     * <p>
     * The StyleConstants to CSS conversion process first 
     * converts the StyleConstants attribute to a 
     * CSS.Attribute, and then calls the fromStyleConstants
     * method to convert the StyleConstants value to a 
     * CSS value.
     * <p>
     * The CSS to StyleConstants conversion process first
     * converts the StyleConstants attribute to a 
     * CSS.Attribute, and then calls the toStyleConstants
     * method to convert the CSS value to a StyleConstants
     * value.
     */
    static class CssValue implements Serializable {

	/**
	 * Convert a css value string to the internal format
	 * (for fast processing) used in the attribute sets.
	 * The fallback storage for any value that we don't
	 * have a special binary format for is a String.
	 */
	Object parseCssValue(String value) {
	    return value;
	}

	/**
	 * Convert an html attribute value to a css attribute
	 * value.  If there is no conversion, return null.
	 * This is implemented to simply forward to the css
	 * parsing by default (since some of the attribute
	 * values are the same).  If the attribute value
	 * isn't recognized as a css value it is generally
	 * returned as null.
	 */
	Object parseHtmlValue(String value) {
	    return parseCssValue(value);
	}

	/**
	 * Convert a StyleConstants attribute value to
	 * a css attribute value.  If there is no conversion
	 * return null.  By default, there is no conversion.
	 * 
	 * @param key the StyleConstants attribute.
	 * @param value the value of a StyleConstants attribute,
	 *   to be converted.
	 * @returns the CSS value that represents the StyleConstants
	 *   value.
	 */
	Object fromStyleConstants(StyleConstants key, Object value) {
	    return null;
	}

	/**
	 * Convert a CSS attribute value to a StyleConstants
	 * value.  If there is no conversion, return null.
	 * By default, there is no conversion.
	 *
	 * @param key the StyleConstants attribute.
	 * @returns the StyleConstants attribute value that 
	 *   represents the CSS attribute value.
	 */
	Object toStyleConstants(StyleConstants key) {
	    return null;
	}

	/**
	 * Return the css format of the value
	 */
        public String toString() {
	    return svalue;
	}

	/**
	 * The value as a string... before conversion to a
	 * binary format.
	 */
	String svalue;
    }

    /**
     * By default CSS attributes are represented as simple
     * strings.  They also have no conversion to/from
     * StyleConstants by default. This class represents the 
     * value as a string (via the superclass), but 
     * provides StyleConstants conversion support for the 
     * CSS attributes that are held as strings.
     */
    static class StringValue extends CssValue {

	/**
	 * Convert a css value string to the internal format
	 * (for fast processing) used in the attribute sets.
	 * This produces a StringValue, so that it can be
	 * used to convert from CSS to StyleConstants values.
	 */
	Object parseCssValue(String value) {
	    StringValue sv = new StringValue();
	    sv.svalue = value;
	    return sv;
	}

	/**
	 * Convert a StyleConstants attribute value to
	 * a css attribute value.  If there is no conversion
	 * return null. 
	 * 
	 * @param key the StyleConstants attribute.
	 * @param value the value of a StyleConstants attribute,
	 *   to be converted.
	 * @returns the CSS value that represents the StyleConstants
	 *   value.
	 */
	Object fromStyleConstants(StyleConstants key, Object value) {
	    if (key == StyleConstants.Italic) {
		if (value.equals(Boolean.TRUE)) {
		    return parseCssValue("italic");
		}
		return parseCssValue("");
	    } else if (key == StyleConstants.Underline) {
		if (value.equals(Boolean.TRUE)) {
		    return parseCssValue("underline");
		} 
		return parseCssValue("");
	    } else if (key == StyleConstants.Alignment) {
		int align = ((Integer)value).intValue();
		String ta;
		switch(align) {
		case StyleConstants.ALIGN_LEFT:
		    ta = "left";
		    break;
		case StyleConstants.ALIGN_RIGHT:
		    ta = "right";
		    break;
		case StyleConstants.ALIGN_CENTER:
		    ta = "center";
		    break;
		case StyleConstants.ALIGN_JUSTIFIED:
		    ta = "justify";
		    break;
		default:
		    ta = "left";
		}
		return parseCssValue(ta);
	    } else if (key == StyleConstants.StrikeThrough) {
		if (value.equals(Boolean.TRUE)) {
		    return parseCssValue("line-through");
		} 
		return parseCssValue("");
	    } else if (key == StyleConstants.Superscript) {
		if (value.equals(Boolean.TRUE)) {
		    return parseCssValue("super");
		}
		return parseCssValue("");
	    } else if (key == StyleConstants.Subscript) {
		if (value.equals(Boolean.TRUE)) {
		    return parseCssValue("sub");
		}
		return parseCssValue("");
	    }
	    return null;
	}

	/**
	 * Convert a CSS attribute value to a StyleConstants
	 * value.  If there is no conversion, return null.
	 * By default, there is no conversion.
	 *
	 * @param key the StyleConstants attribute.
	 * @returns the StyleConstants attribute value that 
	 *   represents the CSS attribute value.
	 */
	Object toStyleConstants(StyleConstants key) {
	    if (key == StyleConstants.Italic) {
		if (svalue.indexOf("italic") >= 0) {
		    return Boolean.TRUE;
		}
		return Boolean.FALSE;
	    } else if (key == StyleConstants.Underline) {
		if (svalue.indexOf("underline") >= 0) {
		    return Boolean.TRUE;
		} 
		return Boolean.FALSE;
	    } else if (key == StyleConstants.Alignment) {
		if (svalue.equals("right")) {
		    return new Integer(StyleConstants.ALIGN_RIGHT);
		} else if (svalue.equals("center")) {
		    return new Integer(StyleConstants.ALIGN_CENTER);
		} else if  (svalue.equals("justify")) {
		    return new Integer(StyleConstants.ALIGN_JUSTIFIED);
		}
		return new Integer(StyleConstants.ALIGN_LEFT);
	    } else if (key == StyleConstants.StrikeThrough) {
		if (svalue.indexOf("line-through") >= 0) {
		    return Boolean.TRUE;
		} 
		return Boolean.FALSE;
	    } else if (key == StyleConstants.Superscript) {
		if (svalue.indexOf("super") >= 0) {
		    return Boolean.TRUE;
		} 
		return Boolean.FALSE;
	    } else if (key == StyleConstants.Subscript) {
		if (svalue.indexOf("sub") >= 0) {
		    return Boolean.TRUE;
		} 
		return Boolean.FALSE;
	    }
	    return null;
	}

    }

    /**
     * Represents a value for the CSS.FONT_SIZE attribute.
     * The binary format of the value can be one of several
     * types.  If the type is Float,
     * the value is specified in terms of point or
     * percentage, depending upon the ending of the
     * associated string.
     * If the type is Integer, the value is specified
     * in terms of a size index.
     */
    class FontSize extends CssValue {

	/**
	 * Returns the size in points.  This is ultimately
	 * what we need for the purpose of creating/fetching
	 * a Font object.
	 *
	 * @param a the attribute set the value is being
	 *  requested from.  We may need to walk up the
	 *  resolve hierarchy if it's relative.
	 */
	float getValue(AttributeSet a) {
	    // PENDING(prinz) need to add support for relative
	    // and percentage.
	    if (index) {
		// it's an index, translate from size table
		return getPointSize((int) value);
	    } else {
		return value;
	    }
	}

	boolean isRelative() {
	    return relative;
	}

	Object parseCssValue(String value) {
	    FontSize fs = new FontSize();
	    fs.svalue = value;
	    try {
		if (value.equals("xx-small")) {
		    fs.value = 0;
		    fs.index = true;
		} else if (value.equals("x-small")) {
		    fs.value = 1;
		    fs.index = true;
		} else if (value.equals("small")) {
		    fs.value = 2;
		    fs.index = true;
		} else if (value.equals("medium")) {
		    fs.value = 3;
		    fs.index = true;
		} else if (value.equals("large")) {
		    fs.value = 4;
		    fs.index = true;
		} else if (value.equals("x-large")) {
		    fs.value = 5;
		    fs.index = true;
		} else if (value.equals("xx-large")) {
		    fs.value = 6;
		    fs.index = true;
		} else if (value.equals("bigger")) {
		    fs.value = 1;
		    fs.index = true;
		    fs.relative = true;
		} else if (value.equals("smaller")) {
		    fs.value = -1;
		    fs.index = true;
		    fs.relative = true;
		} else if (value.endsWith("pt")) {
		    String sz = value.substring(0, value.length() - 2);
		    fs.value = Float.valueOf(sz).floatValue();
		} else {
		    // TBD - further processing
		    fs.value = Float.valueOf(value).floatValue();
		}
	    } catch (NumberFormatException nfe) {
		fs = null;
	    }
	    return fs;
	}

	Object parseHtmlValue(String value) {
	    FontSize fs = new FontSize();
	    fs.svalue = value;

	    try {
		/*
		 * relative sizes in the size attribute are relative
		 * to the <basefont>'s size.
		 */
		int baseFontSize = getBaseFontSize();
		if ((value != null) && (value.charAt(0) == '+')) {
		    int relSize = Integer.valueOf(value.substring(1)).intValue();
		    fs.value = baseFontSize + relSize;
		    fs.index = true;
		} else if ((value != null) && (value.charAt(0) == '-')) {
		    int relSize = -Integer.valueOf(value.substring(1)).intValue();
		    fs.value = baseFontSize + relSize;
		    fs.index = true;
		} else {
		    fs.value = Integer.parseInt(value);
		    if (fs.value > 6) {
			fs.value = 6;
		    } else if (fs.value < 0) {
			fs.value = 0;
		    }
		    fs.index = true;
		}

	    } catch (NumberFormatException nfe) {
		fs = null;
	    }
	    return fs;
	}

	/**
	 * Convert a StyleConstants attribute value to
	 * a css attribute value.  If there is no conversion
	 * return null.  By default, there is no conversion.
	 * 
	 * @param key the StyleConstants attribute.
	 * @param value the value of a StyleConstants attribute,
	 *   to be converted.
	 * @returns the CSS value that represents the StyleConstants
	 *   value.
	 */
	Object fromStyleConstants(StyleConstants key, Object value) {
	    return parseCssValue(value.toString());
	}

	/**
	 * Convert a CSS attribute value to a StyleConstants
	 * value.  If there is no conversion, return null.
	 * By default, there is no conversion.
	 *
	 * @param key the StyleConstants attribute.
	 * @returns the StyleConstants attribute value that 
	 *   represents the CSS attribute value.
	 */
	Object toStyleConstants(StyleConstants key) {
	    return new Integer((int) getValue(null));
	}

	float value;
	boolean relative;
	boolean index;
	boolean percentage;
    }

    static class FontFamily extends CssValue {

	/**
	 * Returns the font family to use.  This is expected
	 * to be a legal font for this platform.
	 */
	String getValue() {
	    return family;
	}

	Object parseCssValue(String value) {
	    FontFamily ff = new FontFamily();
	    // TBD - a real implementation
	    if (value.equals("monospace")) {
		ff.family = "Monospaced";
	    }
	    else {
		ff.family = value;
	    }
	    ff.svalue = value;
	    return ff;
	}

	Object parseHtmlValue(String value) {
	    // TBD
	    return parseCssValue(value);
	}

	/**
	 * Convert a StyleConstants attribute value to
	 * a css attribute value.  If there is no conversion
	 * return null.  By default, there is no conversion.
	 * 
	 * @param key the StyleConstants attribute.
	 * @param value the value of a StyleConstants attribute,
	 *   to be converted.
	 * @returns the CSS value that represents the StyleConstants
	 *   value.
	 */
	Object fromStyleConstants(StyleConstants key, Object value) {
	    return parseCssValue(value.toString());
	}

	/**
	 * Convert a CSS attribute value to a StyleConstants
	 * value.  If there is no conversion, return null.
	 * By default, there is no conversion.
	 *
	 * @param key the StyleConstants attribute.
	 * @returns the StyleConstants attribute value that 
	 *   represents the CSS attribute value.
	 */
	Object toStyleConstants(StyleConstants key) {
	    return family;
	}

	String family;
    }

    static class FontWeight extends CssValue {

	int getValue() {
	    return weight;
	}

	Object parseCssValue(String value) {
	    FontWeight fw = new FontWeight();
	    fw.svalue = value;
	    if (value.equals("bold")) {
		fw.weight = 700;
	    } else if (value.equals("normal")) {
		fw.weight = 400;
	    } else {
		// PENDING(prinz) add support for relative values
		try {
		    fw.weight = Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
		    fw = null;
		}
	    }
	    return fw;
	}

	/**
	 * Convert a StyleConstants attribute value to
	 * a css attribute value.  If there is no conversion
	 * return null.  By default, there is no conversion.
	 * 
	 * @param key the StyleConstants attribute.
	 * @param value the value of a StyleConstants attribute,
	 *   to be converted.
	 * @returns the CSS value that represents the StyleConstants
	 *   value.
	 */
	Object fromStyleConstants(StyleConstants key, Object value) {
	    if (value.equals(Boolean.TRUE)) {
		return parseCssValue("bold");
	    }
	    return parseCssValue("normal");
	}

	/**
	 * Convert a CSS attribute value to a StyleConstants
	 * value.  If there is no conversion, return null.
	 * By default, there is no conversion.
	 *
	 * @param key the StyleConstants attribute.
	 * @returns the StyleConstants attribute value that 
	 *   represents the CSS attribute value.
	 */
	Object toStyleConstants(StyleConstants key) {
	    return (weight > 500) ? Boolean.TRUE : Boolean.FALSE;
	}

	int weight;
    }

    static class ColorValue extends CssValue {

	/**
	 * Returns the color to use.
	 */
	Color getValue() {
	    return c;
	}

	Object parseCssValue(String value) {

	    Color c = stringToColor(value);
	    if (c != null) {
		ColorValue cv = new ColorValue();
		cv.svalue = value;
		cv.c = c;
		return cv;
	    }
	    return null;
	}

	Object parseHtmlValue(String value) {
	    return parseCssValue(value);
	}

	/**
	 * Convert a StyleConstants attribute value to
	 * a css attribute value.  If there is no conversion
	 * return null.  By default, there is no conversion.
	 * 
	 * @param key the StyleConstants attribute.
	 * @param value the value of a StyleConstants attribute,
	 *   to be converted.
	 * @returns the CSS value that represents the StyleConstants
	 *   value.
	 */
	Object fromStyleConstants(StyleConstants key, Object value) {
	    return parseCssValue(colorToHex((Color) value));
	}

	/**
	 * Convert a CSS attribute value to a StyleConstants
	 * value.  If there is no conversion, return null.
	 * By default, there is no conversion.
	 *
	 * @param key the StyleConstants attribute.
	 * @returns the StyleConstants attribute value that 
	 *   represents the CSS attribute value.
	 */
	Object toStyleConstants(StyleConstants key) {
	    return c;
	}

	Color c;
    }

    static class BorderStyle extends CssValue {

	CSS.Value getValue() {
	    return style;
	}

	Object parseCssValue(String value) {
	    CSS.Value cssv = CSS.getValue(value);
	    if (cssv != null) {
		if ((cssv == CSS.Value.INSET) ||
		    (cssv == CSS.Value.OUTSET) ||
		    (cssv == CSS.Value.NONE) ||
		    (cssv == CSS.Value.DOTTED) ||
		    (cssv == CSS.Value.DASHED) ||
		    (cssv == CSS.Value.SOLID) ||
		    (cssv == CSS.Value.DOUBLE) ||
		    (cssv == CSS.Value.GROOVE) ||
		    (cssv == CSS.Value.RIDGE)) {

		    BorderStyle bs = new BorderStyle();
		    bs.svalue = value;
		    bs.style = cssv;
		    return bs;
		}
	    }
	    return null;
	}

	private void writeObject(java.io.ObjectOutputStream s)
	             throws IOException {
	    s.defaultWriteObject();
	    if (style == null) {
		s.writeObject(null);
	    }
	    else {
		s.writeObject(style.toString());
	    }
	}

	private void readObject(ObjectInputStream s)
		throws ClassNotFoundException, IOException {
	    s.defaultReadObject();
	    Object value = s.readObject();
	    if (value != null) {
		style = CSS.getValue((String)value);
	    }
	}

	// CSS.Values are static, don't archive it.
	transient private CSS.Value style;
    }

    static class LengthValue extends CssValue {

	/**
	 * Returns the length (span) to use.
	 */
	float getValue() {
	    return span;
	}

	Object parseCssValue(String value) {
	    LengthValue lv = new LengthValue();
	    lv.svalue = value;
	    try {
		if (value.endsWith("pt")) {
		    String sz = value.substring(0, value.length() - 2);
		    lv.span = Float.valueOf(sz).floatValue();
		} else {
		    // TBD - further processing
		    lv.span = Float.valueOf(value).floatValue();
		}
	    } catch (NumberFormatException nfe) {
		lv = null;
	    }
	    return lv;
	}

	Object parseHtmlValue(String value) {
	    if (value.equals(HTML.NULL_ATTRIBUTE_VALUE)) {
		value = "1";
	    }
	    return parseCssValue(value);
	}
	/**
	 * Convert a StyleConstants attribute value to
	 * a css attribute value.  If there is no conversion
	 * return null.  By default, there is no conversion.
	 * 
	 * @param key the StyleConstants attribute.
	 * @param value the value of a StyleConstants attribute,
	 *   to be converted.
	 * @returns the CSS value that represents the StyleConstants
	 *   value.
	 */
	Object fromStyleConstants(StyleConstants key, Object value) {
	    
	    LengthValue v = new LengthValue();
	    v.svalue = value.toString();
	    v.span = ((Float)value).floatValue();
	    return v;
	}

	/**
	 * Convert a CSS attribute value to a StyleConstants
	 * value.  If there is no conversion, return null.
	 * By default, there is no conversion.
	 *
	 * @param key the StyleConstants attribute.
	 * @returns the StyleConstants attribute value that 
	 *   represents the CSS attribute value.
	 */
	Object toStyleConstants(StyleConstants key) {
	    return new Float(span);
	}

	float span;
    }


    /**
     * Handles conversion of list types.
     */
    static class ListType extends CssValue {
	Object parseCssValue(String value) {
	    Object retValue = cssValueToInternalValueMap.get(value);
	    if (retValue == null) {
		retValue = cssValueToInternalValueMap.get(value.toLowerCase());
	    }
	    return retValue;
	}


	Object parseHtmlValue(String value) {
	    Object retValue = htmlValueToCssValueMap.get(value);
	    if (retValue == null) {
		retValue = htmlValueToCssValueMap.get(value.toLowerCase());
	    }
	    return retValue;
	}
    }


    //
    // Serialization support
    //

    private void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
        s.defaultWriteObject();

	// Determine what values in valueConvertor need to be written out.
	Enumeration keys = valueConvertor.keys();
	s.writeInt(valueConvertor.size());
	if (keys != null) {
	    while (keys.hasMoreElements()) {
		Object key = keys.nextElement();
		Object value = valueConvertor.get(key);
		if (!(key instanceof Serializable) &&
		    (key = StyleContext.getStaticAttributeKey(key)) == null) {
		    // Should we throw an exception here?
		    key = null;
		    value = null;
		}
		else if (!(value instanceof Serializable) &&
		    (value = StyleContext.getStaticAttributeKey(value)) == null){
		    // Should we throw an exception here?
		    key = null;
		    value = null;
		}
		s.writeObject(key);
		s.writeObject(value);
	    }
	}
    }

    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
        s.defaultReadObject();
	// Reconstruct the hashtable.
	int numValues = s.readInt();
	valueConvertor = new Hashtable(Math.max(1, numValues));
	while (numValues-- > 0) {
	    Object key = s.readObject();
	    Object value = s.readObject();
	    Object staticKey = StyleContext.getStaticAttribute(key);
	    if (staticKey != null) {
		key = staticKey;
	    }
	    Object staticValue = StyleContext.getStaticAttribute(value);
	    if (staticValue != null) {
		value = staticValue;
	    }
	    if (key != null && value != null) {
		valueConvertor.put(key, value);
	    }
	}
    }

    //
    // Instance variables
    //

    /** Maps from CSS key to CssValue. */
    private transient Hashtable valueConvertor;

    /** Size used for relative units. */
    private int baseFontSize;
}
