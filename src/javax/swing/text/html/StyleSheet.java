/*
 * @(#)StyleSheet.java	1.35 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
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

import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.border.*;
import javax.swing.text.*;

/**
 * Support for defining the visual characteristics of
 * html views being rendered.  The StyleSheet is used to
 * translate the html model into visual characteristics.
 * This enables views to be customized by a look-and-feel,
 * multiple views over the same model can be rendered
 * differently, etc.  This can be thought of as a CSS
 * rule repository.  The key for CSS attributes is an
 * object of type CSS.Attribute.  The type of the value
 * is up to the StyleSheet implementation, but the
 * <code>toString</code> method is required
 * to return a string representation of CSS value.
 * <p>
 * The primary entry point for HTML View implementations
 * to get their attributes is the 
 * <a href="#getViewAttributes">getViewAttributes</a>
 * method.  This should be implemented to establish the
 * desired policy used to associate attributes with the view.
 * Each HTMLEditorKit (i.e. and therefore each associated
 * JEditorPane) can have its own StyleSheet, but by default one
 * sheet will be shared by all of the HTMLEditorKit instances.
 * HTMLDocument instance can also have a StyleSheet, which
 * holds the document-specific CSS specifications.
 * <p>
 * In order for Views to store less state and therefore be
 * more lightweight, the StyleSheet can act as a factory for
 * painters that handle some of the rendering tasks.  This allows
 * implementations to determine what they want to cache
 * and have the sharing potentially at the level that a
 * selector is common to multiple views.  Since the StyleSheet
 * may be used by views over multiple documents and typically
 * the html attributes don't effect the selector being used,
 * the potential for sharing is significant.
 * <p>
 * The rules are stored as named styles, and other information
 * is stored to translate the context of an element to a 
 * rule quickly.  The following code fragment will display
 * the named styles, and therefore the CSS rules contained.
 * <code><pre>

import java.util.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

public class ShowStyles {

    public static void main(String[] args) {
	HTMLEditorKit kit = new HTMLEditorKit();
	HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
	StyleSheet styles = doc.getStyleSheet();
	
	Enumeration rules = styles.getStyleNames();
	while (rules.hasMoreElements()) {
	    String name = (String) rules.nextElement();
	    Style rule = styles.getStyle(name);
	    System.out.println(rule.toString());
	}
	System.exit(0);
    }
}

 * </pre></code>
 * <p>
 * <font color="red">Note: This implementation is currently
 * incomplete.  It can be replaced with alternative implementations
 * that are complete.  Future versions of this class will provide
 * better CSS support.</font>
 *
 * @author  Timothy Prinzing
 * @author  Sunita Mani
 * @author  Sara Swanson
 * @author  Jill Nakata
 * @version 1.35 08/26/98
 */
public class StyleSheet extends StyleContext {

    /**
     * Construct a StyleSheet
     */
    public StyleSheet() {
	super();
	selectors = addStyle(null, null);
	selectors.addAttribute(StyleConstants.NameAttribute, ":");
	searchContext = new Vector();

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
	Object generic = new CssValue();
	int n = CSS.Attribute.allAttributes.length;
	for (int i = 0; i < n; i++) {
	    CSS.Attribute key = CSS.Attribute.allAttributes[i];
	    if (valueConvertor.get(key) == null) {
		valueConvertor.put(key, generic);
	    }
	}

	baseFontSize = 3;
    }

    /**
     * Fetch the style to use to render the given type
     * of html tag.  The element given is representing
     * the tag and can be used to determine the nesting
     * for situations where the attributes will differ
     * if nesting inside of elements.
     *
     * @param t the type to translate to visual attributes.
     * @param e the element representing the tag. The element
     *  can be used to determine the nesting for situations where
     *  the attributes will differ if nested inside of other
     *  elements.
     * @returns the set of css attributes to use to render
     *  the tag.
     */
    public Style getRule(HTML.Tag t, Element e) {
	// System.err.println("getRule: " + t);
	searchContext.removeAllElements();
	for (Element p = e.getParentElement(); p != null; p = p.getParentElement()) {
	    AttributeSet a = p.getAttributes();
	    searchContext.addElement(a.getAttribute(StyleConstants.NameAttribute));
	}

	Style selector = selectors;
	int n = searchContext.size();
	for (int i = searchContext.size() - 1; i >= 0; i--) {
	    Style candidate = (Style) selector.getAttribute(searchContext.elementAt(i));
	    if (candidate != null) {
		// System.err.println("  candidate for: " + searchContext.elementAt(i));
		selector = candidate;
	    }
	}
	Style s = (Style) selector.getAttribute(t);
	if (s != null) {
	    //System.err.println("  at: " + s.getAttribute(StyleConstants.NameAttribute) +
	    //	     " found: " + s.getAttribute(RULE));
	    return (Style) s.getAttribute(RULE);
	}
	return null;
    }

    /**
     * Fetch the rule that best matches the selector given
     * in string form.
     *
     */
    public Style getRule(String selector) {
	// TBD
	return null;
    }

    /**
     * Add a set of rules to the sheet.  The rules are expected to
     * be in valid CSS format.  Typically this would be called as
     * a result of parsing a &lt;style&gt; tag.
     */
    public void addRule(String rule) {
	// TBD
    }

    /**
     * Translate a CSS declaration to an AttributeSet that represents
     * the CSS declaration.  Typically this would be called as a
     * result of encountering an HTML style attribute.
     */
    public AttributeSet getDeclaration(String decl) {
	// TBD
	return null;
    }

    /**
     * Load a set of rules that have been specified in terms of
     * CSS1 grammar.  If there are collisions with existing rules,
     * the newly specified rule will win.
     *
     * @param in the stream to read the css grammar from.
     * @param ref the reference url.  This value represents the
     *  location of the stream and may be null.  All relative
     *  urls specified in the stream will be based upon this
     *  parameter.
     */
    public void loadRules(Reader in, URL ref) throws IOException {
	if (parser == null) {
	    parser = new CssParser();
	}
	parser.parse(in);
    }

    /**
     * Fetch a set of attributes to use in the view for
     * displaying.  This is basically a set of attributes that
     * can be used for View.getAttributes.
     */
    public AttributeSet getViewAttributes(View v) {
	return new ViewAttributeSet(v);
    }

    /**
     * Fetch the font to use for the given set of attributes.
     */
    public Font getFont(AttributeSet a) {
	// PENDING(prinz) this is a 1.1 based implementation, need to also
	// have a 1.2 version.
	FontSize sizeValue = (FontSize) a.getAttribute(CSS.Attribute.FONT_SIZE);

	int size = (sizeValue != null) ? (int) sizeValue.getValue(a) : 12;

	/*
	 * If the vertical alignment is set to either superscirpt or
	 * subscript we reduce the font size by 2 points.
	 */
	String vAlign = (String) a.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
	if ((vAlign != null) &&
	    ((vAlign.indexOf("sup") >= 0) ||
	     (vAlign.indexOf("sub") >= 0))) {
	    size -= 2;
	}
	
	FontFamily familyValue = (FontFamily) a.getAttribute(CSS.Attribute.FONT_FAMILY);
	String family = (familyValue != null) ? familyValue.getValue() : "SansSerif";
	int style = Font.PLAIN;
	FontWeight weightValue = (FontWeight) a.getAttribute(CSS.Attribute.FONT_WEIGHT);
	if ((weightValue != null) && (weightValue.getValue() > 400)) {
	    style |= Font.BOLD;
	}
	String fs = (String) a.getAttribute(CSS.Attribute.FONT_STYLE);
	if ((fs != null) && fs.equals("italic")) {
	    style |= Font.ITALIC;
	}
	Font f = super.getFont(family, style, size);
	return f;
    }

    /**
     * Takes a set of attributes and turn it into a foreground color
     * specification.  This might be used to specify things
     * like brighter, more hue, etc.
     *
     * @param a the set of attributes
     * @return the color
     */
    public Color getForeground(AttributeSet a) {

	ColorValue cv = (ColorValue) a.getAttribute(CSS.Attribute.COLOR);
	Color c = (cv != null) ? cv.getValue() : Color.black;
	return c;
    }

    /**
     * Takes a set of attributes and turn it into a background color
     * specification.  This might be used to specify things
     * like brighter, more hue, etc.
     *
     * @param attr the set of attributes
     * @return the color
     */
    public Color getBackground(AttributeSet a) {
	ColorValue cv = (ColorValue) a.getAttribute(CSS.Attribute.BACKGROUND_COLOR);
	Color c = (cv != null) ? cv.getValue() : null;
	return c;
    }

    /**
     * Fetch the box formatter to use for the given set
     * of css attributes.
     */
    public BoxPainter getBoxPainter(AttributeSet a) {
	return new BoxPainter(a);
    }

    /**
     * Fetch the list formatter to use for the given set
     * of css attributes.
     */
    public ListPainter getListPainter(AttributeSet a) {
	return new ListPainter(a);
    }

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
    public Color stringToColor(String str) {
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

    public void setBaseFontSize(int sz) {
	if (sz < 1)
	  baseFontSize = 0;
	else if (sz > 7)
	  baseFontSize = 7;
	else
	  baseFontSize = sz;
    }

    public void setBaseFontSize(String size) {
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

    public static int  getIndexOfSize(float pt) {
        for (int i = 0; i < sizeMap.length; i ++ )
                if (pt <= sizeMap[i])
                        return i;
        return sizeMap.length - 1;
    }

    /**
     * Return the point size, given a size index.
     */
    public float getPointSize(int index) {
	if (index < 0)
	  return sizeMap[0];
	else if (index > sizeMap.length - 1)
	  return sizeMap[sizeMap.length - 1];
	else
	  return sizeMap[index];
    }

    /**
     *  Given a string "+2", "-2", "2".
     *  returns a point size value
     */
    public float getPointSize(String size) {
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
     * Add a simple selector to the given selector
     * context.  If the simple selector can be converted
     * to an HTML.Tag it will so that future access if
     * faster.  If the simple selector already exits, the
     * existing selector will be returned.
     */
    Style addSelector(Style context, String simple) {
	HTML.Tag t = HTML.getTag(simple);
	if (t != null) {
	    Style selector;
	    if (! context.isDefined(t)) {
		selector = addStyle(null, context);
		String nm = context.getAttribute(StyleConstants.NameAttribute) +
		  simple + ":";
		selector.addAttribute(StyleConstants.NameAttribute, nm);
		context.addAttribute(t, selector);
	    } else {
		selector = (Style) context.getAttribute(t);
	    }
	    return selector;
	} else {
	    Style selector;
	    if (! context.isDefined(simple)) {
		selector = addStyle(null, context);
		context.addAttribute(simple, selector);
	    } else {
		selector = (Style) context.getAttribute(simple);
	    }
	    return selector;
	}
    }

    /**
     * This method adds a rule into the StyleSheet.
     *
     * @param selector the selector to use for the rule.
     *  This will be a set of simple selectors, and must
     *  be a length of 1 or greater.
     * @param declaration the set of css attributes that
     *  make up the rule.
     */
    void addRule(String[] selector, AttributeSet declaration) {
	String name = "";
	int n = selector.length - 1;
	for (int i = 0; i < n; i++) {
	    name += selector[i] + " ";
	}
	name += selector[n];

	Style rule = getStyle(name);
	if (rule == null) {
	    rule = addStyle(name, null);
	    Style context = StyleSheet.this.selectors;
	    for (int i = 0; i <= n; i++) {
		context = addSelector(context, selector[i]);
	    }
	    context.addAttribute(RULE, rule);
	}
	rule.addAttributes(declaration);
    }

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
	searchContext = new Vector();
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

    static final Border noBorder = new EmptyBorder(0,0,0,0);

    /**
     * Class to carry out some of the duties of
     * css formatting.  Implementations of this
     * class enable views to present the css formatting
     * while not knowing anything about how the css values
     * are being cached.
     * <p>
     * As a delegate of Views, this object is responsible for
     * the insets of a View and making sure the background
     * is maintained according to the css attributes.
     */
    public static class BoxPainter implements Serializable {

	BoxPainter(AttributeSet a) {
	    border = getBorder(a);
	    binsets = border.getBorderInsets(null);
	}

	/**
	 * Fetch a border to render for the given attributes.
	 * PENDING(prinz) This is pretty badly hacked at the 
	 * moment.
	 */
	Border getBorder(AttributeSet a) {
	    Border b = noBorder;
	    Object o = a.getAttribute(CSS.Attribute.BORDER_STYLE);
	    if (o != null) {
		String bstyle = o.toString();
		int bw = (int) getLength(CSS.Attribute.BORDER_WIDTH, a);
		if (bw > 0) {
		    if (bstyle.equals("inset")) {
			Color c = getBorderColor(a);
			b = new BevelBorder(BevelBorder.LOWERED, c.brighter(), c.darker());
		    } else if (bstyle.equals("outset")) {
			Color c = getBorderColor(a);
			b = new BevelBorder(BevelBorder.RAISED, c.brighter(), c.darker());
		    } else if (bstyle.equals("solid")) {
			Color c = getBorderColor(a);
			b = new LineBorder(c);
		    }
		}
	    }
	    return b;
	}

	/**
	 * Fetch the color to use for borders.  This will either be
	 * the value specified by the border-color attribute (which
	 * is not inherited), or it will default to the color attribute
	 * (which is inherited).
	 */
	Color getBorderColor(AttributeSet a) {
	    Object o = a.getAttribute(CSS.Attribute.BORDER_COLOR);
	    if (o == null) {
		o = a.getAttribute(CSS.Attribute.COLOR);
	    }
	    if (o != null) {
		ColorValue cv = (ColorValue) o;
		return cv.getValue();
	    }
	    return Color.black;
	}

	/**
	 * Fetchs the inset needed on a given side to
	 * account for the margin, border, and padding.
	 *
	 * @param side The size of the box to fetch the
	 *  inset for.  This can be View.TOP,
	 *  View.LEFT, View.BOTTOM, or View.RIGHT.
	 * @param v the view making the request.  This is
	 *  used to get the AttributeSet, and may be used to
	 *  resolve percentage arguments.
	 * @exception IllegalArgumentException for an invalid direction
	 */
        public float getInset(int side, View v) {
	    AttributeSet a = v.getAttributes();
	    float inset = 0;
	    switch(side) {
	    case View.LEFT:
		inset += getLength(CSS.Attribute.MARGIN_LEFT, a);
		inset += binsets.left;
		inset += getLength(CSS.Attribute.PADDING_LEFT, a);
		break;
	    case View.RIGHT:
		inset += getLength(CSS.Attribute.MARGIN_RIGHT, a);
		inset += binsets.right;
		inset += getLength(CSS.Attribute.PADDING_RIGHT, a);
		break;
	    case View.TOP:
		inset += getLength(CSS.Attribute.MARGIN_TOP, a);
		inset += binsets.top;
		inset += getLength(CSS.Attribute.PADDING_TOP, a);
		break;
	    case View.BOTTOM:
		inset += getLength(CSS.Attribute.MARGIN_BOTTOM, a);
		inset += binsets.bottom;
		inset += getLength(CSS.Attribute.PADDING_BOTTOM, a);
		break;
	    default:
		throw new IllegalArgumentException("Invalid side: " + side);
	    }
	    return inset;
	}

	/**
	 * Paints the css box according to the attributes
	 * given.  This should paint the border, padding,
	 * and background.
	 *
	 * @param g the rendering surface.
	 * @param x the x coordinate of the allocated area to
	 *  render into.
	 * @param y the y coordinate of the allocated area to
	 *  render into.
	 * @param w the width of the allocated area to render into.
	 * @param h the height of the allocated area to render into.
	 * @param v the view making the request.  This is
	 *  used to get the AttributeSet, and may be used to
	 *  resolve percentage arguments.
	 */
        public void paint(Graphics g, float x, float y, float w, float h, View v) {
	    AttributeSet a = v.getAttributes();

	    // PENDING(prinz) implement real rendering... which would
	    // do full set of border and background capabilities.
	    StyleSheet sheet = ((HTMLDocument) v.getDocument()).getStyleSheet();

	    // remove margin
	    float top = getLength(CSS.Attribute.MARGIN_TOP, a);
	    float left = getLength(CSS.Attribute.MARGIN_LEFT, a);
	    x += left;
	    y += top;
	    w -= left + getLength(CSS.Attribute.MARGIN_RIGHT, a);
	    h -= top + getLength(CSS.Attribute.MARGIN_BOTTOM, a);

	    Color bg = sheet.getBackground(a);
	    if (bg != null) {

		g.setColor(bg);
		g.fillRect((int) x, (int) y, (int) w, (int) h);
	    }
	    border.paintBorder(null, g, (int) x, (int) y, (int) w, (int) h);
	}

	float getLength(CSS.Attribute key, AttributeSet a) {
	    LengthValue lv = (LengthValue) a.getAttribute(key);
	    float len = (lv != null) ? lv.getValue() : 0;
	    return len;
	}

	Border border;
	Insets binsets;
    }

    /**
     * class to carry out some of the duties of css list
     * formatting.  Implementations of this
     * class enable views to present the css formatting
     * while not knowing anything about how the css values
     * are being cached.
     */
    public static class ListPainter implements Serializable {

	ListPainter(AttributeSet attr) {
	    /* Get the image to use as a list bullet */
	    String imgstr = (String)attr.getAttribute(CSS.Attribute.LIST_STYLE_IMAGE);
	    if (imgstr == null) {
		imgstr = (String) attr.getAttribute(CSS.Attribute.LIST_STYLE);
	    }
	    if (imgstr == null) {
		type = null;
	    } else if (imgstr.equalsIgnoreCase("none")) {
		type = new String("none");
	    } else {
		try {
		    String tmpstr = null;
		    StringTokenizer st = new StringTokenizer(imgstr, "()");
		    if (st.hasMoreTokens())
			tmpstr = st.nextToken();
		    if (st.hasMoreTokens())
			tmpstr = st.nextToken();
		    URL u = new URL(tmpstr);
		    img = new ImageIcon(u);
		    type = new String("html-image");
		} catch (MalformedURLException e) {
		    type = null;
		}
	    }

	    /* Get the type of bullet to use in the list */
	    String type = (String) attr.getAttribute(CSS.Attribute.LIST_STYLE_TYPE);
	    if (type == null) {
		type = new String("disc");
	    }

	    /* Get the number to start the list with */
	    /*
	    String startstr = (String)attr.getAttribute("start");
	    if (startstr != null) {
		try {
		    start = (Integer.valueOf(startstr)).intValue();
		} catch (NumberFormatException e) {
		}
	    }
	    */
	}

	/**
	 * Returns a string that represents the value
	 * of the HTML.Attribute.TYPE attribute.
	 * If this attributes is not defined, then
	 * then the type defaults to "disc" unless
	 * the tag is on Ordered list.  In the case
	 * of the latter, the default type is "decimal".
	 */
	private String getChildType(View childView) {
	    
	    // HTML.Attribute.TYPE information is stored in the parent
	    // of the view.
	    View v = childView.getParent();

	    // Given that we are interested in the value of an HTML.Attribute.TYPE
	    // we need to look at the attributes associated with the element,
	    // and not the attributes associated with the view.
	    //
	    AttributeSet a = v.getElement().getAttributes();
	    String childtype = (String) a.getAttribute(HTML.Attribute.TYPE);

	    if (childtype == null) {
		if (type == null) {
		    HTMLDocument doc = (HTMLDocument)v.getDocument();
		    if (doc.matchNameAttribute(v.getElement().getAttributes(), HTML.Tag.OL)) {
			childtype = "decimal";
		    } else {
			childtype = "disc";
		    }
		} else {
		    childtype = type;
		}
	    }
	    return childtype;
	}

	/**
	 * Paints the css list decoration according to the
	 * attributes given.
	 *
	 * @param g the rendering surface.
	 * @param x the x coordinate of the list item allocation
	 * @param y the y coordinate of the list item allocation
	 * @param w the width of the list item allocation
	 * @param h the height of the list item allocation
	 * @param s the allocated area to paint into.
	 * @param item which list item is being painted.  This
	 *  is a number >= 0.
	 */
        public void paint(Graphics g, float x, float y, float w, float h, View v, int item) {
	    View cv = v.getView(item);
	    String childtype = getChildType(cv);
	    float align = cv.getAlignment(View.Y_AXIS);
	    if (childtype.equalsIgnoreCase("square")) {
    		drawShape(g, childtype, (int) x, (int) y, (int) h, align);
	    } else if (childtype.equalsIgnoreCase("circle")) {
    		drawShape(g, childtype, (int) x, (int) y, (int) h, align);
	    } else if (childtype.equalsIgnoreCase("1")
		       || childtype.equalsIgnoreCase("decimal")) {
		drawLetter(g, '1', (int) x, (int) y, (int) h, item);
	    } else if (childtype.equals("a")
		       || childtype.equalsIgnoreCase("lower-alpha")) {
		drawLetter(g, 'a', (int) x, (int) y, (int) h, item);
	    } else if (childtype.equals("A")
		       || childtype.equalsIgnoreCase("upper-alpha")) {
		drawLetter(g, 'A', (int) x, (int) y, (int) h, item);
	    } else if (childtype.equals("i")
		       || childtype.equalsIgnoreCase("lower-roman")) {
		drawLetter(g, 'i', (int) x, (int) y, (int) h, item);
	    } else if (childtype.equals("I")
		       || childtype.equalsIgnoreCase("upper-roman")) {
		drawLetter(g, 'I', (int) x, (int) y, (int) h, item);
	    } else if (childtype.equalsIgnoreCase("html-image")) {
    		drawIcon(g, (int) x, (int) y, (int) h, align, v.getContainer());
	    } else if (childtype.equals("none")) {
		;
	    } else {
    		drawShape(g, childtype, (int) x, (int) y, (int) h, align);
	    }
	}

	/**
	 * Draws the bullet icon specified by the list-style-image argument.
	 *
	 * @param g     the graphics context
	 * @param ax    x coordinate to place the bullet
	 * @param ay    y coordinate to place the bullet
	 * @param ah    height of the container the bullet is placed in
	 * @param align preferred alignment factor for the child view
	 */
	void drawIcon(Graphics g, int ax, int ay, int ah,
		      float align, Component c) {
	    g.setColor(Color.black);
	    int x = ax - img.getIconWidth() - bulletgap;
	    int y = ay + (int)(ah * align) - 3;

	    img.paintIcon(c, g, x, y);
	}

	/**
	 * Draws the graphical bullet item specified by the type argument.
	 *
	 * @param g     the graphics context
	 * @param type  type of bullet to draw (circle, square, disc)
	 * @param ax    x coordinate to place the bullet
	 * @param ay    y coordinate to place the bullet
	 * @param ah    height of the container the bullet is placed in
	 * @param align preferred alignment factor for the child view
	 */
	void drawShape(Graphics g, String type, int ax, int ay, int ah,
		       float align) {
	    g.setColor(Color.black);
	    int x = ax - bulletgap - 7;
	    int y = ay + (int)(ah * align) - 3;

	    if (type.equalsIgnoreCase("square")) {
		g.drawRect(x, y, 7, 7);
	    } else if (type.equalsIgnoreCase("circle")) {
		g.drawOval(x, y, 7, 7);
	    } else {
		g.fillOval(x, y, 7, 7);
	    }
	}

	/**
	 * Draws the letter or number for an ordered list.
	 *
	 * @param g     the graphics context
	 * @param letter type of ordered list to draw
	 * @param ax    x coordinate to place the bullet
	 * @param ay    y coordinate to place the bullet
	 * @param ah    height of the container the bullet is placed in
	 * @param index position of the list item in the list
	 */
	void drawLetter(Graphics g, char letter, int ax, int ay, int ah,
			int index) {
	    g.setColor(Color.black);
	    String str = formatItemNum(index + start, letter) + ".";
	    FontMetrics fm = g.getFontMetrics();
	    int stringwidth = fm.stringWidth(str);
	    int x = ax - stringwidth - bulletgap;
	    int y = ay + fm.getAscent() + fm.getLeading();
	    g.drawString(str, x, y);
	}

	/**
	 * Converts the item number into the ordered list number
	 * (i.e.  1 2 3, i ii iii, a b c, etc.
	 *
	 * @param itemNum number to format
	 * @param type    type of ordered list
	 */
	String formatItemNum(int itemNum, char type) {
	    String numStyle = "1";

	    boolean uppercase = false;

	    String formattedNum;

	    switch (type) {
	    case '1':
	    default:
		formattedNum = String.valueOf(itemNum);
		break;

	    case 'A':
		uppercase = true;
		// fall through
	    case 'a':
		formattedNum = formatAlphaNumerals(itemNum);
		break;

	    case 'I':
		uppercase = true;
		// fall through
	    case 'i':
		formattedNum = formatRomanNumerals(itemNum);
	    }

	    if (uppercase) {
		formattedNum = formattedNum.toUpperCase();
	    }

	    return formattedNum;
	}

	/**
	 * Converts the item number into an alphabetic character
	 *
	 * @param itemNum number to format
	 */
	String formatAlphaNumerals(int itemNum) {
	    String result = "";

	    if (itemNum > 26) {
		result = formatAlphaNumerals(itemNum / 26) +
		    formatAlphaNumerals(itemNum % 26);
	    } else {
		// -1 because item is 1 based.
		result = String.valueOf((char)('a' + itemNum - 1));
	    }

	    return result;
	}

	/* list of roman numerals */
	static final char romanChars[][] = {
	    {'i', 'v'},
	    {'x', 'l' },
	    {'c', 'd' },
	    {'m', '?' },
        };

	/**
	 * Converts the item number into a roman numeral
	 *
	 * @param num  number to format
	 */
	String formatRomanNumerals(int num) {
	    return formatRomanNumerals(0, num);
	}

	/**
	 * Converts the item number into a roman numeral
	 *
	 * @param num  number to format
	 */
	String formatRomanNumerals(int level, int num) {
	    if (num < 10) {
		return formatRomanDigit(level, num);
	    } else {
		return formatRomanNumerals(level + 1, num / 10) +
		    formatRomanDigit(level, num % 10);
	    }
	}


	/**
	 * Converts the item number into a roman numeral
	 *
	 * @param level position
	 * @param num   digit to format
	 */
	String formatRomanDigit(int level, int digit) {
	    String result = "";
	    if (digit == 9) {
		result = result + romanChars[level][0];
		result = result + romanChars[level + 1][0];
		return result;
	    } else if (digit == 4) {
		result = result + romanChars[level][0];
		result = result + romanChars[level][1];
		return result;
	    } else if (digit >= 5) {
		result = result + romanChars[level][1];
		digit -= 5;
	    }

	    for (int i = 0; i < digit; i++) {
		result = result + romanChars[level][0];
	    }

	    return result;
	}

        private String type;
        private int start = 1;
        URL imageurl;
        Icon img = null;
        private int bulletgap = 5;
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
     */
    class CssValue implements Serializable {

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

	float value;
	boolean relative;
	boolean index;
	boolean percentage;
    }

    class FontFamily extends CssValue {

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
	    ff.svalue = value;
	    ff.family = value;
	    return ff;
	}

	Object parseHtmlValue(String value) {
	    // TBD
	    return parseCssValue(value);
	}

	String family;
    }

    class FontWeight extends CssValue {

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

	int weight;
    }

    class ColorValue extends CssValue {

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

	Color c;
    }

    class BorderStyle extends CssValue {

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

    class LengthValue extends CssValue {

	/**
	 * Returns the length (span) to use.
	 */
	float getValue() {
	    return span;
	}

	Object parseCssValue(String value) {
	    LengthValue lv = new LengthValue();
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
	float span;
    }

    /**
     * An implementation of AttributeSet that can multiplex
     * across a set of css attributes from multiple sources.
     */
    static class ViewAttributeSet implements AttributeSet {

	ViewAttributeSet(View v) {
	    host = v;

	    // PENDING(prinz) fix this up to be a more realistic
	    // implementation.
	    Document doc = v.getDocument();
	    if (doc instanceof HTMLDocument) {
		StyleSheet styles = ((HTMLDocument) doc).getStyleSheet();
		Element elem = v.getElement();
		AttributeSet a = elem.getAttributes();
		muxList.removeAllElements();

		AttributeSet htmlAttr = styles.translateHTMLToCSS(a);

		if (htmlAttr.getAttributeCount() != 0) {
		    muxList.addElement(htmlAttr);
		}
		if (elem.isLeaf()) {
		    Enumeration keys = a.getAttributeNames();
		    while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (key instanceof HTML.Tag) {
			    if ((HTML.Tag)key  == HTML.Tag.A) {
				Object o = a.getAttribute((HTML.Tag)key);
				/**
				   In the case of an A tag, the css rules
				   apply only for tags that have their
				   href attribute defined and not for
				   anchors that only have their name attributes
				   defined, i.e anchors that function as
				   destinations.  Hence we do not add the
				   attributes for that latter kind of 
				   anchors.  When CSS2 support is added,
				   it will be possible to specifiy this
				   kind of conditional behaviour in the 
				   stylesheet.
				 **/
				if (o != null && o instanceof AttributeSet) {
				    AttributeSet attr = (AttributeSet)o;
				    if (attr.getAttribute(HTML.Attribute.HREF) == null) {
					continue;
				    }
				}
			    }
			    AttributeSet cssRule = styles.getRule((HTML.Tag) key, elem);
			    if (cssRule != null) {
				muxList.addElement(cssRule);
			    }
			}
		    }
		} else {
		    HTML.Tag t = (HTML.Tag) a.getAttribute(StyleConstants.NameAttribute);
		    if (t == HTML.Tag.IMPLIED) {
			t = HTML.Tag.P;
		    }
		    AttributeSet cssRule = styles.getRule(t, elem);
		    if (cssRule != null) {
			muxList.addElement(cssRule);
		    }
		}
	    }
	    attrs = new AttributeSet[muxList.size()];
	    muxList.copyInto(attrs);
	}

	//  --- AttributeSet methods ----------------------------

	/**
	 * Gets the number of attributes that are defined.
	 *
	 * @return the number of attributes
	 * @see AttributeSet#getAttributeCount
	 */
        public int getAttributeCount() {
	    int n = 0;
	    for (int i = 0; i < attrs.length; i++) {
		n += attrs[i].getAttributeCount();
	    }
	    return n;
	}

	/**
	 * Checks whether a given attribute is defined.
	 *
	 * @param key the attribute key
	 * @return true if the attribute is defined
	 * @see AttributeSet#isDefined
	 */
        public boolean isDefined(Object key) {
	    int n = 0;
	    for (int i = 0; i < attrs.length; i++) {
		if (attrs[i].isDefined(key)) {
		    return true;
		}
	    }
	    return false;
	}

	/**
	 * Checks whether two attribute sets are equal.
	 *
	 * @param attr the attribute set to check against
	 * @return true if the same
	 * @see AttributeSet#isEqual
	 */
        public boolean isEqual(AttributeSet attr) {
	    return ((getAttributeCount() == attr.getAttributeCount()) &&
		    containsAttributes(attr));
	}

	/**
	 * Copies a set of attributes.
	 *
	 * @return the copy
	 * @see AttributeSet#copyAttributes
	 */
        public AttributeSet copyAttributes() {
	    MutableAttributeSet a = new SimpleAttributeSet();
	    int n = 0;
	    for (int i = 0; i < attrs.length; i++) {
		a.addAttributes(attrs[i]);
	    }
	    return a;
	}

	/**
	 * Gets the value of an attribute.
	 *
	 * @param key the attribute name
	 * @return the attribute value
	 * @see AttributeSet#getAttribute
	 */
        public Object getAttribute(Object key) {
	    int n = attrs.length;
	    for (int i = 0; i < n; i++) {
		Object o = attrs[i].getAttribute(key);
		if (o != null) {
		    return o;
		}
	    }
	    // didn't find it... try parent if it's a css attribute
	    // that is inherited.
	    if (key instanceof CSS.Attribute) {
		CSS.Attribute css = (CSS.Attribute) key;
		if (css.isInherited()) {
		    AttributeSet parent = getResolveParent();
		    if (parent != null)
			return parent.getAttribute(key);
		}
	    }
	    return null;
	}

	/**
	 * Gets the names of all attributes.
	 *
	 * @return the attribute names
	 * @see AttributeSet#getAttributeNames
	 */
        public Enumeration getAttributeNames() {
	    // PENDING(prinz) implement an enumeration
	    // that merges the children.
	    return null;
	}

	/**
	 * Checks whether a given attribute name/value is defined.
	 *
	 * @param name the attribute name
	 * @param value the attribute value
	 * @return true if the name/value is defined
	 * @see AttributeSet#containsAttribute
	 */
        public boolean containsAttribute(Object name, Object value) {
	    return value.equals(getAttribute(name));
	}

	/**
	 * Checks whether the attribute set contains all of
	 * the given attributes.
	 *
	 * @param attrs the attributes to check
	 * @return true if the element contains all the attributes
	 * @see AttributeSet#containsAttributes
	 */
        public boolean containsAttributes(AttributeSet attrs) {
	    boolean result = true;

	    Enumeration names = attrs.getAttributeNames();
	    while (result && names.hasMoreElements()) {
		Object name = names.nextElement();
		result = attrs.getAttribute(name).equals(getAttribute(name));
	    }

	    return result;
	}

	/**
	 * If not overriden, the resolving parent defaults to
	 * the parent element.
	 *
	 * @return the attributes from the parent
	 * @see AttributeSet#getResolveParent
	 */
        public AttributeSet getResolveParent() {
	    View parent = host.getParent();
	    String name = (parent != null) ? parent.getElement().getName() : "*none*";
	    return (parent != null) ? parent.getAttributes() : null;
	}

	static Vector muxList = new Vector();
	AttributeSet[] attrs;
	View host;
    }

    // ---- Variables ---------------------------------------------

    final static int DEFAULT_FONT_SIZE = 3;

    private Style selectors;
    private CssParser parser;
    private transient Hashtable valueConvertor;
    private transient Vector searchContext;

    private int baseFontIndex;
    private int baseFontSize;

    /**
     * The html/css size model has seven slots
     * that one can assign sizes to.
     */
    static int sizeMap[] = { 8, 10, 12, 14, 18, 24, 36 };

    /**
     * Internal StyleSheet attribute.  This is used for
     * caches, weights, rule storage, etc.
     */
    static class SheetAttribute {

	SheetAttribute(String s, boolean cache) {
	    name = s;
	    this.cache = cache;
	}

        public String toString() {
	    return name;
	}

        public boolean isCache() {
	    return cache;
	}

	private boolean cache;
	private String name;
    }


    static final SheetAttribute RULE = new SheetAttribute("rule", false);
    static final SheetAttribute WEIGHT = new SheetAttribute("rule-weight", false);

    static final SheetAttribute BOX_PAINTER = new SheetAttribute("box-painter", true);
    static final SheetAttribute LIST_PAINTER = new SheetAttribute("list-painter", true);

    /**
     * Default parser for css specifications that get loaded into
     * the StyleSheet.
     */
    class CssParser implements Serializable {

	/**
	 * Parse the given css stream
	 */
	void parse(Reader r) throws IOException {
	    StreamTokenizer st = new StreamTokenizer(r);
	    st.ordinaryChar('/');
	    st.slashStarComments(true);
	    boolean inRule = false;
	    for (int token = st.nextToken(); token != StreamTokenizer.TT_EOF;
		 token = st.nextToken()) {

		// process tokens
		switch (token) {
		case StreamTokenizer.TT_WORD:
		    if (inRule) {
			ruleTokens.addElement(st.sval);
		    } else {
			selectorTokens.addElement(st.sval);
		    }
		    break;
		case StreamTokenizer.TT_NUMBER:
		    if (inRule) {
			ruleTokens.addElement(new Integer((int) st.nval));
		    } else {
			String s = (String) selectorTokens.lastElement();
			s = s + ((int) st.nval);
			selectorTokens.setElementAt(s, selectorTokens.size()-1);
		    }
		    break;
		case StreamTokenizer.TT_EOL:
		    break;
		default:
		    switch(st.ttype) {
		    case '{':
			addSelector();
			inRule = true;
			break;
		    case '}':
			addKeyValueToDeclaration();
			addRule();
			inRule = false;
			break;
		    case ';':
			addKeyValueToDeclaration();
			break;
		    case ':':
			key = getDeclarationString();
			break;
		    case ',':
			if (inRule) {
			    ruleTokens.addElement(new Character(','));
			} else {
			    addSelector();
			}
			break;
		    default:
			if (inRule) {
			    ruleTokens.addElement(new Character(','));
			} else {
			    String s = (String) selectorTokens.lastElement();
			    s = s + ((char) st.ttype);
			    selectorTokens.setElementAt(s, selectorTokens.size()-1);
			}
		    }
		}
	    }
	}

	void addSelector() {
	    String[] selector = new String[selectorTokens.size()];
	    selectorTokens.copyInto(selector);
	    selectors.addElement(selector);
	    selectorTokens.removeAllElements();
	}

	void addKeyValueToDeclaration() {
	    CSS.Attribute cssKey = CSS.getAttribute(key);
	    if (cssKey != null) {
		Object o = getValue(cssKey);
		if (o != null) {
		    declaration.addAttribute(cssKey, o);
		}
	    }
	    ruleTokens.removeAllElements();
	}

	/**
	 * This fetches the value for the current key using the
	 * current contents of the ruleTokens collection.  The
	 * value should be converted from tokens into however
	 * we choose to store it in the StyleSheet.
	 */
	Object getValue(CSS.Attribute key) {
	    CssValue conv = (CssValue) valueConvertor.get(key);
	    String vstr = getDeclarationString();
	    return conv.parseCssValue(vstr);
	}

	String getDeclarationString() {
	    String s = "";
	    int n = ruleTokens.size();
	    for (int i = 0; i < n; i++) {
		s += ruleTokens.elementAt(i);
	    }
	    ruleTokens.removeAllElements();
	    return s;
	}

	void addRule() {
	    int n = selectors.size();
	    for (int i = 0; i < n; i++) {
		String[] selector = (String[]) selectors.elementAt(i);
		if (selector.length > 0) {
		    StyleSheet.this.addRule(selector, declaration);
		}
	    }
	    declaration.removeAttributes(declaration);
	    selectors.removeAllElements();
	}

	Vector selectors = new Vector();
	Vector selectorTokens = new Vector();
	String key;
	Vector ruleTokens = new Vector();
	MutableAttributeSet declaration = new SimpleAttributeSet();
    }

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

    static {
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

	// Register all the CSS attribute keys for archival/unarchival
	Object[] keys = CSS.Attribute.allAttributes;
	try {
	    for (int i = 0; i < keys.length; i++) {
		StyleContext.registerStaticAttributeKey(keys[i]);
	    }
	} catch (Throwable e) {
	    e.printStackTrace();
	}

	StyleContext.registerStaticAttributeKey(RULE);
	StyleContext.registerStaticAttributeKey(WEIGHT);
	StyleContext.registerStaticAttributeKey(BOX_PAINTER);
	StyleContext.registerStaticAttributeKey(LIST_PAINTER);
    }


    /**
     * Convert a set of html attributes to an equivalent
     * set of css attributes.
     *
     * @param AttributeSet containing the HTML attributes.
     * @param AttributeSet containing the corresponding CSS attributes.
     *        The AttributeSet will be empty if there are no mapping
     *        CSS attributes.
     */
    public AttributeSet translateHTMLToCSS(AttributeSet htmlAttrSet) {

	//MutableAttributeSet cssAttrSet = addStyle(null, null);
	MutableAttributeSet cssAttrSet = new SimpleAttributeSet();

	Element elem = (Element)htmlAttrSet;
	HTML.Tag tag = getHTMLTag(htmlAttrSet);
	if ((tag == HTML.Tag.TD) || (tag == HTML.Tag.TH)) {
	    // translate border width into the cells
	    AttributeSet tableAttr = elem.getParentElement().getParentElement().getAttributes();
	    translateAttribute(HTML.Attribute.BORDER, tableAttr, cssAttrSet);
	}
	if (elem.isLeaf()) {
	    translateEmbeddedAttributes(htmlAttrSet, cssAttrSet);
	} else {
	    translateAttributes(tag, htmlAttrSet, cssAttrSet);
	}

	MutableAttributeSet cssStyleSet = addStyle(null, null);
	cssStyleSet.addAttributes(cssAttrSet);

	return cssStyleSet;
    }


    private void translateEmbeddedAttributes(AttributeSet htmlAttrSet,
					     MutableAttributeSet cssAttrSet) {

	Enumeration names = htmlAttrSet.getAttributeNames();
	while (names.hasMoreElements()) {
	    Object name = names.nextElement();
	    if (name instanceof HTML.Tag) {
		HTML.Tag tag = (HTML.Tag)name;
		Object o = htmlAttrSet.getAttribute(tag);
		if (o != null && o instanceof AttributeSet) {
		    translateAttributes(tag, (AttributeSet)o, cssAttrSet);
		}
	    }
	}
    }



    private void translateAttributes(HTML.Tag tag, AttributeSet htmlAttrSet,
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
	    }
	}
    }

    void translateAttribute(HTML.Attribute key, AttributeSet htmlAttrSet,
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
    private Object getCssValue(CSS.Attribute cssAttr, String htmlAttrValue) {
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
    private CSS.Attribute getCssAlignAttribute(HTML.Tag tag, AttributeSet htmlAttrSet) {
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
}

