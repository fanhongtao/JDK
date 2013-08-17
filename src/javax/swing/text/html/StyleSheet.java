/*
 * @(#)StyleSheet.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.text.html;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.border.*;
import javax.swing.event.ChangeListener;
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
 * &nbsp; 
 * &nbsp; import java.util.*;
 * &nbsp; import javax.swing.text.*;
 * &nbsp; import javax.swing.text.html.*;
 * &nbsp; 
 * &nbsp; public class ShowStyles {
 * &nbsp; 
 * &nbsp;     public static void main(String[] args) {
 * &nbsp; 	HTMLEditorKit kit = new HTMLEditorKit();
 * &nbsp; 	HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
 * &nbsp; 	StyleSheet styles = doc.getStyleSheet();
 * &nbsp; 	
 * &nbsp; 	Enumeration rules = styles.getStyleNames();
 * &nbsp; 	while (rules.hasMoreElements()) {
 * &nbsp; 	    String name = (String) rules.nextElement();
 * &nbsp; 	    Style rule = styles.getStyle(name);
 * &nbsp; 	    System.out.println(rule.toString());
 * &nbsp; 	}
 * &nbsp; 	System.exit(0);
 * &nbsp;     }
 * &nbsp; }
 * &nbsp; 
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
 * @version 1.53 04/22/99
 */
public class StyleSheet extends StyleContext {

    /**
     * Construct a StyleSheet
     */
    public StyleSheet() {
	super();
	selectorMapping = new Hashtable();
	resolvedStyles = new Hashtable();
	if (css == null) {
	    css = new CSS();
	}
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
        SearchBuffer sb = SearchBuffer.obtainSearchBuffer();

        try {
            // Build an array of all the parent elements.
            Vector searchContext = sb.getVector();

            for (Element p = e; p != null; p = p.getParentElement()) {
                searchContext.addElement(p);
            }

            // Build a fully qualified selector.
            int              n = searchContext.size();
            StringBuffer     cacheLookup = sb.getStringBuffer();
            AttributeSet     attr;
            String           eName;
            Object           name;

            // >= 1 as the HTML.Tag for the 0th element is passed in.
            for (int counter = n - 1; counter >= 1; counter--) {
                e = (Element)searchContext.elementAt(counter);
                attr = e.getAttributes();
                name = attr.getAttribute(StyleConstants.NameAttribute);
                if (name instanceof HTML.Tag) {
                    if (name == HTML.Tag.IMPLIED) {
                        eName = HTML.Tag.P.toString();
                    }
                    else {
                        eName = name.toString();
                    }
                }
                else {
                    eName = name.toString();
                }
                cacheLookup.append(eName);
                if (attr != null) {
                    if (attr.isDefined(HTML.Attribute.ID)) {
                        cacheLookup.append('#');
                        cacheLookup.append(attr.getAttribute
					   (HTML.Attribute.ID));
                    }
                    else if (attr.isDefined(HTML.Attribute.CLASS)) {
                        cacheLookup.append('.');
                        cacheLookup.append(attr.getAttribute
					   (HTML.Attribute.CLASS));
                    }
                }
                cacheLookup.append(' ');
            }
	    if (t == HTML.Tag.IMPLIED) {
		t = HTML.Tag.P;
	    }
            cacheLookup.append(t.toString());
	    e = (Element)searchContext.elementAt(0);
            attr = e.getAttributes();
            if (attr != null) {
                if (attr.isDefined(HTML.Attribute.ID)) {
                    cacheLookup.append('#');
                    cacheLookup.append(attr.getAttribute(HTML.Attribute.ID));
                }
                else if (attr.isDefined(HTML.Attribute.CLASS)) {
                    cacheLookup.append('.');
                    cacheLookup.append(attr.getAttribute
				       (HTML.Attribute.CLASS));
                }
            }

            Style style = getResolvedStyle(cacheLookup.toString(),
					   searchContext, t);
	    return style;
        }
        finally {
            SearchBuffer.releaseSearchBuffer(sb);
        }
    }

    /**
     * Fetch the rule that best matches the selector given
     * in string form. Where <code>selector</code> is a space separated
     * String of the element names. For example, <code>selector</code>
     * might be 'html body tr td''<p>
     * The attributes of the returned Style will change
     * as rules are added and removed. That is if you to ask for a rule
     * with a selector "table p" and a new rule was added with a selector
     * of "p" the returned Style would include the new attributes from
     * the rule "p".
     */
    public Style getRule(String selector) {
	selector = cleanSelectorString(selector);
	if (selector != null) {
	    Style style = getResolvedStyle(selector);
	    return style;
	}
	return null;
    }

    /**
     * Add a set of rules to the sheet.  The rules are expected to
     * be in valid CSS format.  Typically this would be called as
     * a result of parsing a &lt;style&gt; tag.
     */
    public void addRule(String rule) {
	if (rule != null) {
	    CssParser parser = new CssParser();
	    try {
		parser.parse(getBase(), new StringReader(rule), false, false);
	    } catch (IOException ioe) { }
	}
    }

    /**
     * Translate a CSS declaration to an AttributeSet that represents
     * the CSS declaration.  Typically this would be called as a
     * result of encountering an HTML style attribute.
     */
    public AttributeSet getDeclaration(String decl) {
	if (decl == null) {
	    return SimpleAttributeSet.EMPTY;
	}
	CssParser parser = new CssParser();
	return parser.parseDeclaration(decl);
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
	CssParser parser = new CssParser();
	parser.parse(ref, in, false, false);
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
     * Removes a named style previously added to the document.
     *
     * @param nm  the name of the style to remove
     */
    public void removeStyle(String nm) {
	Style       aStyle = getStyle(nm);

	if (aStyle != null) {
	    String selector = cleanSelectorString(nm);
	    String[] selectors = getSimpleSelectors(selector);
	    synchronized(this) {
		Object mapping = getRootSelectorMapping();
		for (int i = selectors.length - 1; i >= 0; i--) {
		    mapping = getSelectorMapping(mapping, selectors[i], true);
		}
		Style rule = getMappingStyle(mapping);
		if (rule != null) {
		    removeMappingStyle(mapping);
		    if (resolvedStyles.size() > 0) {
			Enumeration values = resolvedStyles.elements();
			while (values.hasMoreElements()) {
			    ResolvedStyle style = (ResolvedStyle)values.
				                    nextElement();
			    style.removeStyle(rule);
			}
		    }
		}
	    }
	}
	super.removeStyle(nm);
    }

    /**
     * Adds the rules from the StyleSheet <code>ss</code> to those of
     * the receiver. <code>ss's</code> rules will override the rules of
     * any previously added style sheets. An added StyleSheet will never
     * override the rules of the receiving style sheet.
     */
    /* public */
    synchronized void addStyleSheet(StyleSheet ss) {
	if (linkedStyleSheets == null) {
	    linkedStyleSheets = new Vector();
	}
	if (!linkedStyleSheets.contains(ss)) {
	    linkedStyleSheets.insertElementAt(ss, 0);
	    linkStyleSheetAt(ss, 0);
	}
    }

    /**
     * Removes the StyleSheet <code>ss</code> from those of the receiver.
     */
    /* public */
    synchronized void removeStyleSheet(StyleSheet ss) {
	if (linkedStyleSheets != null) {
	    int index = linkedStyleSheets.indexOf(ss);
	    if (index != -1) {
		linkedStyleSheets.removeElementAt(index);
		unlinkStyleSheet(ss, index);
		if (index == 0 && linkedStyleSheets.size() == 0) {
		    linkedStyleSheets = null;
		}
	    }
	}
    }

    //
    // The following is used to import style sheets.
    //

    /**
     * Returns an enumeration of the linked StyleSheets. Will return null
     * if there are no linked StyleSheets.
     */
    /* public */
    synchronized Enumeration getStyleSheets() {
	if (linkedStyleSheets == null) {
	    return null;
	}
	return linkedStyleSheets.elements();
    }

    /**
     * Imports a style sheet from <code>url</code>. The resulting rules
     * are directly added to the receiver. If you do not want the rules
     * to become part of the receiver, create a new StyleSheet and use
     * addStyleSheet to link it in.
     */
    void importStyleSheet(URL url) {
	try {
	    InputStream is;

	    is = url.openStream();
	    Reader r = new BufferedReader(new InputStreamReader(is));
	    CssParser parser = new CssParser();
	    parser.parse(url, r, false, true);
	    r.close();
	    is.close();
	} catch (Throwable e) {
	    // on error we simply have no styles... the html
	    // will look mighty wrong but still function.
	}
    }

    /**
     * Sets the base. All import statements that are relative, will be
     * relative to <code>base</code>.
     */
    /* public */
    void setBase(URL base) {
	this.base = base;
    }

    /**
     * Returns the base.
     */
    /* public */
    URL getBase() {
	return base;
    }

    /**
     * Add a CSS attribute to the given set.
     */
    boolean addCSSAttribute(MutableAttributeSet attr, CSS.Attribute key,
			    String value) {
	Object iValue = css.getInternalCSSValue(key, value);
	if (iValue != null) {
	    attr.addAttribute(key, iValue);
	    return true;
	}
	return false;
    }

    /**
     * Add a CSS attribute to the given set.
     */
    boolean addCSSAttributeFromHTML(MutableAttributeSet attr,
				    CSS.Attribute key, String value) {
	Object iValue = css.getCssValue(key, value);
	if (iValue != null) {
	    attr.addAttribute(key, iValue);
	    return true;
	}
	return false;
    }

    // ---- Conversion functionality ---------------------------------

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
	AttributeSet cssAttrSet = css.translateHTMLToCSS(htmlAttrSet);

	MutableAttributeSet cssStyleSet = addStyle(null, null);
	cssStyleSet.addAttributes(cssAttrSet);

	return cssStyleSet;
    }

    /**
     * Adds an attribute to the given set, and returns
     * the new representative set.  This is reimplemented to
     * convert StyleConstant attributes to CSS prior to forwarding
     * to the superclass behavior.  The the StyleConstants attribute
     * has no corresponding CSS entry, the StyleConstants attribute
     * is stored (but will likely be unused).
     *
     * @param old the old attribute set
     * @param key the non-null attribute key
     * @param value the attribute value
     * @return the updated attribute set
     * @see MutableAttributeSet#addAttribute
     */
    public AttributeSet addAttribute(AttributeSet old, Object key,
				     Object value) {
	if (css == null) {
	    // supers constructor will call this before returning,
	    // and we need to make sure CSS is non null.
	    css = new CSS();
	}
	if (key instanceof StyleConstants) {
	    Object cssValue = css.styleConstantsValueToCSSValue
		              ((StyleConstants)key, value);
	    if (cssValue != null) {
		Object cssKey = css.styleConstantsKeyToCSSKey
		                    ((StyleConstants)key);
		if (cssKey != null) {
		    return super.addAttribute(old, cssKey, cssValue);
		}
	    }
	}
	return super.addAttribute(old, key, value);
    }

    /**
     * Adds a set of attributes to the element.  If any of these attributes
     * are StyleConstants attributes, they will be converted to CSS prior
     * to forwarding to the superclass behavior.
     *
     * @param old the old attribute set
     * @param attr the attributes to add
     * @return the updated attribute set
     * @see MutableAttributeSet#addAttribute
     */
    public AttributeSet addAttributes(AttributeSet old, AttributeSet attr) {
        return super.addAttributes(old, convertAttributeSet(attr));
    }

    /**
     * Removes an attribute from the set.  If the attribute is a StyleConstants
     * attribute, the request will be converted to a CSS attribute prior to 
     * forwarding to the superclass behavior.
     *
     * @param old the old set of attributes
     * @param key the non-null attribute name
     * @return the updated attribute set
     * @see MutableAttributeSet#removeAttribute
     */
    public AttributeSet removeAttribute(AttributeSet old, Object key) {
	if (key instanceof StyleConstants) {
	    Object cssKey = css.styleConstantsKeyToCSSKey((StyleConstants)key);
	    if (cssKey != null) {
		return super.removeAttribute(old, cssKey);
	    }
	}
        return super.removeAttribute(old, key);
    }

    /**
     * Removes a set of attributes for the element.  If any of the attributes
     * is a StyleConstants attribute, the request will be converted to a CSS 
     * attribute prior to forwarding to the superclass behavior.
     *
     * @param old the old attribute set
     * @param names the attribute names
     * @return the updated attribute set
     * @see MutableAttributeSet#removeAttributes
     */
    public AttributeSet removeAttributes(AttributeSet old, Enumeration names) {
        return super.removeAttributes(old, names);
    }

    /**
     * Removes a set of attributes. If any of the attributes
     * is a StyleConstants attribute, the request will be converted to a CSS 
     * attribute prior to forwarding to the superclass behavior.
     *
     * @param old the old attribute set
     * @param attrs the attributes
     * @return the updated attribute set
     * @see MutableAttributeSet#removeAttributes
     */
    public AttributeSet removeAttributes(AttributeSet old, AttributeSet attrs) {
	return super.removeAttributes(old, convertAttributeSet(attrs));
    }

    /**
     * Create a compact set of attributes that might be shared.
     * This is a hook for subclasses that want to alter the 
     * behavior of SmallAttributeSet.  This can be reimplemented
     * to return an AttributeSet that provides some sort of
     * attribute conversion.
     *
     * @param a The set of attributes to be represented in the
     *  the compact form.
     */
    protected SmallAttributeSet createSmallAttributeSet(AttributeSet a) {
	return new SmallConversionSet(a);
    }

    /**
     * Create a large set of attributes that should trade off
     * space for time.  This set will not be shared.  This is
     * a hook for subclasses that want to alter the behavior
     * of the larger attribute storage format (which is 
     * SimpleAttributeSet by default).   This can be reimplemented
     * to return a MutableAttributeSet that provides some sort of
     * attribute conversion.
     *
     * @param a The set of attributes to be represented in the
     *  the larger form.
     */
    protected MutableAttributeSet createLargeAttributeSet(AttributeSet a) {
        return new LargeConversionSet(a);
    }

    /**
     * Convert a set of attributes (if necessary) so that
     * any attributes that were specified as StyleConstants
     * attributes and have a CSS mapping, will be converted
     * to CSS attributes.
     */
    AttributeSet convertAttributeSet(AttributeSet a) {
	if ((a instanceof LargeConversionSet) || 
	    (a instanceof SmallConversionSet)) {
	    // known to be converted.
	    return a;
	}
	// in most cases, there are no StyleConstants attributes
	// so we iterate the collection of keys to avoid creating
	// a new set.
	Enumeration names = a.getAttributeNames();
	while (names.hasMoreElements()) {
	    Object name = names.nextElement();
	    if (name instanceof StyleConstants) {
		// we really need to do a conversion, iterate again
		// building a new set.
		MutableAttributeSet converted = new LargeConversionSet();
		Enumeration keys = a.getAttributeNames();
		while (keys.hasMoreElements()) {
		    Object key = keys.nextElement();
		    Object cssValue = null;
		    if (key instanceof StyleConstants) {
			// convert the StyleConstants attribute if possible
			Object cssKey = css.styleConstantsKeyToCSSKey
			                    ((StyleConstants)key);
			if (cssKey != null) {
			    Object value = a.getAttribute(key);
			    cssValue = css.styleConstantsValueToCSSValue
				           ((StyleConstants)key, value);
			    if (cssValue != null) {
				converted.addAttribute(cssKey, cssValue);
			    }
			}
		    }
		    if (cssValue == null) {
			converted.addAttribute(key, a.getAttribute(key));
		    }
		}
		return converted;
            }
	}
	return a;
    }

    /**
     * Large set of attributes that does conversion of requests
     * for attributes of type StyleConstants.
     */
    class LargeConversionSet extends SimpleAttributeSet {

	/**
	 * Creates a new attribute set based on a supplied set of attributes.
	 *
	 * @param source the set of attributes
	 */
        public LargeConversionSet(AttributeSet source) {
	    super(source);
	}

        public LargeConversionSet() {
	    super();
	}

        /**
         * Checks whether a given attribute is defined.
         *
         * @param key the attribute key
         * @return true if the attribute is defined
         * @see AttributeSet#isDefined
         */
        public boolean isDefined(Object key) {
	    if (key instanceof StyleConstants) {
		Object cssKey = css.styleConstantsKeyToCSSKey
		                    ((StyleConstants)key);
		if (cssKey != null) {
		    return super.isDefined(cssKey);
		}
	    }
	    return super.isDefined(key);
	}

        /**
         * Gets the value of an attribute.
         *
         * @param key the attribute name
         * @return the attribute value
         * @see AttributeSet#getAttribute
         */
        public Object getAttribute(Object key) {
	    if (key instanceof StyleConstants) {
		Object cssKey = css.styleConstantsKeyToCSSKey
		                    ((StyleConstants)key);
		if (cssKey != null) {
		    Object value = super.getAttribute(cssKey);
		    if (value != null) {
			return css.cssValueToStyleConstantsValue
			                   ((StyleConstants)key, value);
		    }
		}
	    }
	    return super.getAttribute(key);
	}
    }

    /**
     * small set of attributes that does conversion of requests
     * for attributes of type StyleConstants.
     */
    class SmallConversionSet extends SmallAttributeSet {

	/**
	 * Creates a new attribute set based on a supplied set of attributes.
	 *
	 * @param source the set of attributes
	 */
        public SmallConversionSet(AttributeSet attrs) {
	    super(attrs);
	}
 	
        /**
         * Checks whether a given attribute is defined.
         *
         * @param key the attribute key
         * @return true if the attribute is defined
         * @see AttributeSet#isDefined
         */
        public boolean isDefined(Object key) {
	    if (key instanceof StyleConstants) {
		Object cssKey = css.styleConstantsKeyToCSSKey
		                    ((StyleConstants)key);
		if (cssKey != null) {
		    return super.isDefined(cssKey);
		}
	    }
	    return super.isDefined(key);
	}

        /**
         * Gets the value of an attribute.
         *
         * @param key the attribute name
         * @return the attribute value
         * @see AttributeSet#getAttribute
         */
        public Object getAttribute(Object key) {
	    if (key instanceof StyleConstants) {
		Object cssKey = css.styleConstantsKeyToCSSKey
		                    ((StyleConstants)key);
		if (cssKey != null) {
		    Object value = super.getAttribute(cssKey);
		    if (value != null) {
			return css.cssValueToStyleConstantsValue
			                   ((StyleConstants)key, value);
		    }
		}
	    }
	    return super.getAttribute(key);
	}
    }

    // ---- Resource handling ----------------------------------------

    /**
     * Fetch the font to use for the given set of attributes.
     */
    public Font getFont(AttributeSet a) {
	return css.getFont(this, a, 12);
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
	Color c = css.getColor(a, CSS.Attribute.COLOR);
	if (c == null) {
	    return Color.black;
	}
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
	return css.getColor(a, CSS.Attribute.BACKGROUND_COLOR);
    }

    /**
     * Fetch the box formatter to use for the given set
     * of css attributes.
     */
    public BoxPainter getBoxPainter(AttributeSet a) {
	return new BoxPainter(a, css, this);
    }

    /**
     * Fetch the list formatter to use for the given set
     * of css attributes.
     */
    public ListPainter getListPainter(AttributeSet a) {
	return new ListPainter(a);
    }

    public void setBaseFontSize(int sz) {
	css.setBaseFontSize(sz);
    }

    public void setBaseFontSize(String size) {
	css.setBaseFontSize(size);
    }

    public static int getIndexOfSize(float pt) {
	return CSS.getIndexOfSize(pt);
    }

    /**
     * Return the point size, given a size index.
     */
    public float getPointSize(int index) {
	return css.getPointSize(index);
    }

    /**
     *  Given a string "+2", "-2", "2".
     *  returns a point size value
     */
    public float getPointSize(String size) {
	return css.getPointSize(size);
    }

    /**
     * Convert a color string "RED" or "#NNNNNN" to a Color.
     * Note: This will only convert the HTML3.2 colors strings
     *       or string of length 7
     *       otherwise, it will return null.
     */
    public Color stringToColor(String string) {
	return CSS.stringToColor(string);
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
    void addRule(String[] selector, AttributeSet declaration,
		 boolean isLinked) {
	int n = selector.length;
	StringBuffer sb = new StringBuffer();
	sb.append(selector[0]);
	for (int counter = 1; counter < n; counter++) {
	    sb.append(' ');
	    sb.append(selector[counter]);
	}
	String selectorName = sb.toString();
	Style rule = getStyle(selectorName);
	if (rule == null) {
	    // Notice how the rule is first created, and it not part of
	    // the synchronized block. It is done like this as creating
	    // a new rule will fire a ChangeEvent. We do not want to be
	    // holding the lock when calling to other objects, it can
	    // result in deadlock.
	    Style altRule = addStyle(selectorName, null);
	    synchronized(this) {
		Object mapping = getRootSelectorMapping();
		for (int i = n - 1; i >= 0; i--) {
		    mapping = getSelectorMapping(mapping, selector[i], true);
		}
		rule = getMappingStyle(mapping);
		if (rule == null) {
		    rule = createStyleForSelector(selectorName, mapping,
						  altRule);
		    refreshResolvedRules(selectorName, selector, rule,
					 getSpecificity(mapping));
		}
	    }
	}
	if (isLinked) {
	    rule = getLinkedStyle(rule);
	}
	rule.addAttributes(declaration);
    }

    //
    // The following gaggle of methods is used in maintaing the rules from
    // the sheet.
    //

    /**
     * Updates the attributes of the rules to reference any related
     * rules in <code>ss</code>.
     */
    private synchronized void linkStyleSheetAt(StyleSheet ss, int index) {
	if (resolvedStyles.size() > 0) {
	    Enumeration values = resolvedStyles.elements();
	    while (values.hasMoreElements()) {
		ResolvedStyle rule = (ResolvedStyle)values.nextElement();
		rule.insertExtendedStyleAt(ss.getRule(rule.getName()),
					   index);
	    }
	}
    }

    /**
     * Removes references to the rules in <code>ss</code>.
     * <code>index</code> gives the index the StyleSheet was at, that is
     * how many StyleSheets had been added before it.
     */
    private synchronized void unlinkStyleSheet(StyleSheet ss, int index) {
	if (resolvedStyles.size() > 0) {
	    Enumeration values = resolvedStyles.elements();
	    while (values.hasMoreElements()) {
		ResolvedStyle rule = (ResolvedStyle)values.nextElement();
		rule.removeExtendedStyleAt(index);
	    }
	}
    }

    /**
     * @return simple selectors that comprise selector.
     */
    /* protected */
    String[] getSimpleSelectors(String selector) {
	selector = cleanSelectorString(selector);
	SearchBuffer sb = SearchBuffer.obtainSearchBuffer();
	Vector selectors = sb.getVector();
	int lastIndex = 0;
	int length = selector.length();
	while (lastIndex != -1) {
	    int newIndex = selector.indexOf(' ', lastIndex);
	    if (newIndex != -1) {
		selectors.addElement(selector.substring(lastIndex, newIndex));
		if (++newIndex == length) {
		    lastIndex = -1;
		}
		else {
		    lastIndex = newIndex;
		}
	    }
	    else {
		selectors.addElement(selector.substring(lastIndex));
		lastIndex = -1;
	    }
	}
	String[] retValue = new String[selectors.size()];
	selectors.copyInto(retValue);
	SearchBuffer.releaseSearchBuffer(sb);
	return retValue;
    }

    /**
     * Returns a string that only has one space between simple selectors,
     * which may be the passed in String.
     */
    /*protected*/ String cleanSelectorString(String selector) {
	boolean lastWasSpace = true;
	for (int counter = 0, maxCounter = selector.length();
	     counter < maxCounter; counter++) {
	    switch(selector.charAt(counter)) {
	    case ' ':
		if (lastWasSpace) {
		    return _cleanSelectorString(selector);
		}
		lastWasSpace = true;
		break;
	    case '\n':
	    case '\r':
	    case '\t':
		return _cleanSelectorString(selector);
	    default:
		lastWasSpace = false;
	    }
	}
	if (lastWasSpace) {
	    return _cleanSelectorString(selector);
	}
	// It was fine.
	return selector;
    }

    /**
     * Returns a new String that contains only one space between non
     * white space characters.
     */
    private String _cleanSelectorString(String selector) {
	SearchBuffer sb = SearchBuffer.obtainSearchBuffer();
	StringBuffer buff = sb.getStringBuffer();
	boolean lastWasSpace = true;
	int lastIndex = 0;
	char[] chars = selector.toCharArray();
	int numChars = chars.length;
	String retValue = null;
	try {
	    for (int counter = 0; counter < numChars; counter++) {
		switch(chars[counter]) {
		case ' ':
		    if (!lastWasSpace) {
			lastWasSpace = true;
			if (lastIndex < counter) {
			    buff.append(chars, lastIndex,
					1 + counter - lastIndex);
			}
		    }
		    lastIndex = counter + 1;
		    break;
		case '\n':
		case '\r':
		case '\t':
		    if (!lastWasSpace) {
			lastWasSpace = true;
			if (lastIndex < counter) {
			    buff.append(chars, lastIndex,
					counter - lastIndex);
			    buff.append(' ');
			}
		    }
		    lastIndex = counter + 1;
		    break;
		default:
		    lastWasSpace = false;
		    break;
		}
	    }
	    if (lastWasSpace && buff.length() > 0) {
		// Remove last space.
		buff.setLength(buff.length() - 1);
	    }
	    else if (lastIndex < numChars) {
		buff.append(chars, lastIndex, numChars - lastIndex);
	    }
	    retValue = buff.toString();
	}
	finally {
	    SearchBuffer.releaseSearchBuffer(sb);
	}
	return retValue;
    }

    /**
     * Returns the root selector mapping that all selectors are relative
     * too. This is an inverted graph of the selectors.
     */
    private Object getRootSelectorMapping() {
	return selectorMapping;
    }

    /**
     * Returns the child mapping of <code>parent</code> for
     * <code>selector</code>. If there is no mapping for <code>selector</code>
     * and <code>create</code> is false, this will return null.
     */
    private synchronized Object getSelectorMapping(Object parent,
						   String selector,
						   boolean create) {
	Hashtable retValue = (Hashtable)((Hashtable)parent).get(selector);
	if (retValue == null && create) {
	    retValue = new Hashtable(7);
	    ((Hashtable)parent).put(selector, retValue);

	    // Update specificity for child.
	    int    specificity = 0;

	    if (parent != null) {
		Object     pSpec = ((Hashtable)parent).get(SPECIFICITY);

		if (pSpec != null) {
		    specificity = ((Integer)pSpec).intValue();
		}
	    }
	    // class (.) 100
	    // id (#)    10000
	    char    firstChar = selector.charAt(0);
	    if (firstChar == '.') {
		specificity += 100;
	    }
	    else if (firstChar == '#') {
		specificity += 10000;
	    }
	    else {
		specificity += 1;
		if (selector.indexOf('.') != -1) {
		    specificity += 100;
		}
		if (selector.indexOf('#') != -1) {
		    specificity += 10000;
		}
	    }
	    retValue.put(SPECIFICITY, new Integer(specificity));
	}
	return retValue;
    }

    /**
     * Returns the specificity of the passed in String. It assumes the
     * passed in string doesn't contain junk, that is each selector is
     * separated by a space and each selector at most contains one . or one
     * #. A simple selector has a weight of 1, an id selector has a weight
     * of 100, and a class selector has a weight of 10000.
     */
    /*protected*/ static int getSpecificity(String selector) {
        int specificity = 0;
        boolean lastWasSpace = true;

        for (int counter = 0, maxCounter = selector.length();
             counter < maxCounter; counter++) {
            switch(selector.charAt(counter)) {
            case '.':
                specificity += 100;
                break;
            case '#':
                specificity += 10000;
                break;
            case ' ':
                lastWasSpace = true;
                break;
            default:
                if (lastWasSpace) {
                    lastWasSpace = false;
                    specificity += 1;
                }
            }
        }
        return specificity;
    }

    /**
     * Returns the specificity of the passed in mapping.
     */
    private int getSpecificity(Object mapping) {
	Object     pSpec = ((Hashtable)mapping).get(SPECIFICITY);

	if (pSpec != null) {
	    return ((Integer)pSpec).intValue();
	}
	return 0;
    }

    /**
     * Returns the style for the passed in mapping.
     */
    private Style getMappingStyle(Object mapping) {
	return (Style)((Hashtable)mapping).get(RULE);
    }

    /**
     * Removes the previously added mapping style.
     */
    private void removeMappingStyle(Object mapping) {
	((Hashtable)mapping).remove(RULE);
    }

    /**
     * Returns the style that linked attributes should be added to. This
     * will create the style if necessary.
     */
    private Style getLinkedStyle(Style localStyle) {
	// NOTE: This is not synchronized, and the caller of this does
	// not synchronize. There is the chance for one of the callers to
	// overwrite the existing resolved parent, but it is quite rare.
	// The reason this is left like this is because setResolveParent
	// will fire a ChangeEvent. It is really, REALLY bad for us to
	// hold a lock when calling outside of us, it may cause a deadlock.
	Style retStyle = (Style)localStyle.getResolveParent();
	if (retStyle == null) {
	    retStyle = addStyle(null, null);
	    localStyle.setResolveParent(retStyle);
	}
	return retStyle;
    }

    /**
     * Returns the Style appropriate for <code>selector</code> and
     * <code>mapping</code>. If a Style does not currently exist,
     * <code>altStyle</code> will be used.
     */
    private synchronized Style createStyleForSelector(String selector,
						      Object mapping,
						      Style altStyle) {
	Style style = (Style)((Hashtable)mapping).get(RULE);
	if (style == null) {
	    style = altStyle;
	    ((Hashtable)mapping).put(RULE, altStyle);
	}
	return style;
    }

    /**
     * Returns the resolved style for <code>selector</code>. This will
     * create the resolved style, if necessary.
     */
    private synchronized Style getResolvedStyle(String selector,
						Vector elements,
						HTML.Tag t) {
	Style retStyle = (Style)resolvedStyles.get(selector);
	if (retStyle == null) {
	    retStyle = createResolvedStyle(selector, elements, t);
	}
	return retStyle;
    }

    /**
     * Returns the resolved style for <code>selector</code>. This will
     * create the resolved style, if necessary.
     */
    private synchronized Style getResolvedStyle(String selector) {
	Style retStyle = (Style)resolvedStyles.get(selector);
	if (retStyle == null) {
	    retStyle = createResolvedStyle(selector);
	}
	return retStyle;
    }

    /**
     * Adds <code>mapping</code> to <code>elements</code>. It is added
     * such that <code>elements</code> will remain ordered by 
     * specificity.
     */
    private void addSortedStyle(Object mapping, Vector elements) {
	int       size = elements.size();

	if (size > 0) {
	    int     specificity = getSpecificity(mapping);

	    for (int counter = 0; counter < size; counter++) {
		if (specificity >= getSpecificity(elements.
						  elementAt(counter))) {
		    elements.insertElementAt(mapping, counter);
		    return;
		}
	    }
	}
	elements.addElement(mapping);
    }

    /**
     * Adds <code>parentMapping</code> to <code>styles</code>, and
     * recursively calls this method if <code>parentMapping</code> has
     * any child mappings for any of the Elements in <code>elements</code>.
     */
    private synchronized void getStyles(Object parentMapping,
                           Vector styles,
                           String[] tags, String[] ids, String[] classes,
                           int index, int numElements,
                           Hashtable alreadyChecked) {
	// Avoid desending the same mapping twice.
	if (alreadyChecked.contains(parentMapping)) {
	    return;
	}
	alreadyChecked.put(parentMapping, parentMapping);
	Style style = getMappingStyle(parentMapping);
	if (style != null) {
	    addSortedStyle(parentMapping, styles);
	}
	for (int counter = index; counter < numElements; counter++) {
            String tagString = tags[counter];
            if (tagString != null) {
		Object childMapping = getSelectorMapping(parentMapping,
							 tagString, false);
		if (childMapping != null) {
		    getStyles(childMapping, styles, tags, ids, classes,
                              counter + 1, numElements, alreadyChecked);
		}
		if (classes[counter] != null) {
		    String className = classes[counter];
		    childMapping = getSelectorMapping(parentMapping,
				       tagString + "." + className, false);
		    if (childMapping != null) {
			getStyles(childMapping, styles, tags, ids, classes,
                                  counter + 1, numElements, alreadyChecked);
		    }
		    childMapping = getSelectorMapping(parentMapping, "." +
						      className, false);
		    if (childMapping != null) {
			getStyles(childMapping, styles, tags, ids, classes,
                                  counter + 1, numElements, alreadyChecked);
		    }
		}
		if (ids[counter] != null) {
		    String idName = ids[counter];
		    childMapping = getSelectorMapping(parentMapping,
				       tagString + "#" + idName, false);
		    if (childMapping != null) {
			getStyles(childMapping, styles, tags, ids, classes,
                                  counter + 1, numElements, alreadyChecked);
		    }
		    childMapping = getSelectorMapping(parentMapping, "#" +
						      idName, false);
		    if (childMapping != null) {
			getStyles(childMapping, styles, tags, ids, classes,
                                  counter + 1, numElements, alreadyChecked);
		    }
		}
	    }
	}
    }

    /**
     * Creates a returns Style containing all the rules that match
     *  <code>selector</code>.
     */
    private synchronized Style createResolvedStyle(String selector,
				      String[] tags,
				      String[] ids, String[] classes) {
	SearchBuffer sb = SearchBuffer.obtainSearchBuffer();
	Vector tempVector = sb.getVector();
	Hashtable tempHashtable = sb.getHashtable();
	// Determine all the Styles that are appropriate, placing them
	// in tempVector
	try {
	    Object mapping = getRootSelectorMapping();
	    int numElements = tags.length;
	    String tagString = tags[0];
	    Object childMapping = getSelectorMapping(mapping, tagString,
						     false);
	    if (childMapping != null) {
		getStyles(childMapping, tempVector, tags, ids, classes, 1,
			  numElements, tempHashtable);
	    }
	    if (classes[0] != null) {
		String className = classes[0];
		childMapping = getSelectorMapping(mapping, tagString + "." +
						  className, false);
		if (childMapping != null) {
		    getStyles(childMapping, tempVector, tags, ids, classes, 1,
			      numElements, tempHashtable);
		}
		childMapping = getSelectorMapping(mapping, "." + className,
						  false);
		if (childMapping != null) {
		    getStyles(childMapping, tempVector, tags, ids, classes,
			      1, numElements, tempHashtable);
		}
	    }
	    if (ids[0] != null) {
		String idName = ids[0];
		childMapping = getSelectorMapping(mapping, tagString + "#" +
						  idName, false);
		if (childMapping != null) {
		    getStyles(childMapping, tempVector, tags, ids, classes,
			      1, numElements, tempHashtable);
		}
		childMapping = getSelectorMapping(mapping, "#" + idName,
						  false);
		if (childMapping != null) {
		    getStyles(childMapping, tempVector, tags, ids, classes,
			      1, numElements, tempHashtable);
		}
	    }
	    // Create a new Style that will delegate to all the matching
	    // Styles.
	    int numLinkedSS = (linkedStyleSheets != null) ?
		              linkedStyleSheets.size() : 0;
	    int numStyles = tempVector.size();
	    AttributeSet[] attrs = new AttributeSet[numStyles + numLinkedSS];
	    for (int counter = 0; counter < numStyles; counter++) {
		attrs[counter] = getMappingStyle(tempVector.
						 elementAt(counter));
	    }
	    // Get the AttributeSet from linked style sheets.
	    for (int counter = 0; counter < numLinkedSS; counter++) {
		AttributeSet attr = ((StyleSheet)linkedStyleSheets.
				 elementAt(counter)).getRule(selector);
		if (attr == null) {
		    attrs[counter + numStyles] = SimpleAttributeSet.EMPTY;
		}
		else {
		    attrs[counter + numStyles] = attr;
		}
	    }
	    ResolvedStyle retStyle = new ResolvedStyle(selector, attrs,
						       numStyles);
	    resolvedStyles.put(selector, retStyle);
	    return retStyle;
	}
	finally {
	    SearchBuffer.releaseSearchBuffer(sb);
	}
    }

    /**
     * Creates and returns Style containing all the rules that
     * matches <code>selector</code>. Elements is a
     * Vector of all the Elements the style is being asked for. The
     * first Element is the deepest Element, with the last Element
     * representing the root. <code>t</code> gives the Tag to use for
     * the first Element in <code>elements</code>.
     */
    private Style createResolvedStyle(String selector, Vector elements,
				      HTML.Tag t) {
	int numElements = elements.size();
	if (t == HTML.Tag.IMPLIED) {
	    t = HTML.Tag.P;
	}
        // Build three arrays, one for tags, one for class's, and one for
        // id's
        String tags[] = new String[numElements];
        String ids[] = new String[numElements];
        String classes[] = new String[numElements];
        for (int counter = 0; counter < numElements; counter++) {
            Element e = (Element)elements.elementAt(counter);
            AttributeSet attr = e.getAttributes();
            if (attr != null) {
		HTML.Tag tag = (HTML.Tag)attr.getAttribute(StyleConstants.
							   NameAttribute);
		if (tag == HTML.Tag.IMPLIED) {
		    tag = HTML.Tag.P;
		}
                if (tag != null) {
                    tags[counter] = tag.toString();
                }
                else {
                    tags[counter] = null;
                }
		if (attr.isDefined(HTML.Attribute.CLASS)) {
                    classes[counter] = attr.getAttribute
			              (HTML.Attribute.CLASS).toString();
		}
                else {
                    classes[counter] = null;
                }
		if (attr.isDefined(HTML.Attribute.ID)) {
                    ids[counter] = attr.getAttribute(HTML.Attribute.ID).
			                toString();
                }
                else {
                    ids[counter] = null;
                }
            }
            else {
                tags[counter] = ids[counter] = classes[counter] = null;
            }
        }
        tags[0] = t.toString();
	return createResolvedStyle(selector, tags, ids, classes);
    }

    /**
     * Creates a returns a Style containing all the rules that match
     *  <code>selector</code>. It is assumed that each simple selector
     * in <code>selector</code> is separated by a space.
     */
    private Style createResolvedStyle(String selector) {
	SearchBuffer sb = SearchBuffer.obtainSearchBuffer();
	// Will contain the tags, ids, and classes, in that order.
	Vector elements = sb.getVector();
	try {
	    boolean done;
	    int dotIndex = 0;
	    int spaceIndex = 0;
	    int poundIndex = 0;
	    int lastIndex = 0;
	    int length = selector.length();
	    while (lastIndex < length) {
		if (dotIndex == lastIndex) {
		    dotIndex = selector.indexOf('.', lastIndex);
		}
		if (poundIndex == lastIndex) {
		    poundIndex = selector.indexOf('#', lastIndex);
		}
		spaceIndex = selector.indexOf(' ', lastIndex);
		if (spaceIndex == -1) {
		    spaceIndex = length;
		}
		if (dotIndex != -1 && poundIndex != -1 &&
		    dotIndex < spaceIndex && poundIndex < spaceIndex) {
		    if (poundIndex < dotIndex) {
			// #.
			if (lastIndex == poundIndex) {
			    elements.addElement("");
			}
			else {
			    elements.addElement(selector.substring(lastIndex,
								  poundIndex));
			}
			if ((dotIndex + 1) < spaceIndex) {
			    elements.addElement(selector.substring
						(dotIndex + 1, spaceIndex));
			}
			else {
			    elements.addElement(null);
			}
			if ((poundIndex + 1) == dotIndex) {
			    elements.addElement(null);
			}
			else {
			    elements.addElement(selector.substring
						(poundIndex + 1, dotIndex));
			}
		    }
		    else if(poundIndex < spaceIndex) {
			// .#
			if (lastIndex == dotIndex) {
			    elements.addElement("");
			}
			else {
			    elements.addElement(selector.substring(lastIndex,
								  dotIndex));
			}
			if ((dotIndex + 1) < poundIndex) {
			    elements.addElement(selector.substring
						(dotIndex + 1, poundIndex));
			}
			else {
			    elements.addElement(null);
			}
			if ((poundIndex + 1) == spaceIndex) {
			    elements.addElement(null);
			}
			else {
			    elements.addElement(selector.substring
						(poundIndex + 1, spaceIndex));
			}
		    }
		    dotIndex = poundIndex = spaceIndex + 1;
		}
		else if (dotIndex != -1 && dotIndex < spaceIndex) {
		    // .
		    if (dotIndex == lastIndex) {
			elements.addElement("");
		    }
		    else {
			elements.addElement(selector.substring(lastIndex,
							       dotIndex));
		    }
		    if ((dotIndex + 1) == spaceIndex) {
			elements.addElement(null);
		    }
		    else {
			elements.addElement(selector.substring(dotIndex + 1,
							       spaceIndex));
		    }
		    elements.addElement(null);
		    dotIndex = spaceIndex + 1;
		}
		else if (poundIndex != -1 && poundIndex < spaceIndex) {
		    // #
		    if (poundIndex == lastIndex) {
			elements.addElement("");
		    }
		    else {
			elements.addElement(selector.substring(lastIndex,
							       poundIndex));
		    }
		    elements.addElement(null);
		    if ((poundIndex + 1) == spaceIndex) {
			elements.addElement(null);
		    }
		    else {
			elements.addElement(selector.substring(poundIndex + 1,
							       spaceIndex));
		    }
		    poundIndex = spaceIndex + 1;
		}
		else {
		    // id
		    elements.addElement(selector.substring(lastIndex,
							   spaceIndex));
		    elements.addElement(null);
		    elements.addElement(null);
		}
		lastIndex = spaceIndex + 1;
	    }
	    // Create the tag, id, and class arrays.
	    int total = elements.size();
	    int numTags = total / 3;
	    String[] tags = new String[numTags];
	    String[] ids = new String[numTags];
	    String[] classes = new String[numTags];
	    for (int index = 0, eIndex = total - 3; index < numTags;
		 index++, eIndex -= 3) {
		tags[index] = (String)elements.elementAt(eIndex);
		ids[index] = (String)elements.elementAt(eIndex + 1);
		classes[index] = (String)elements.elementAt(eIndex + 2);
	    }
	    return createResolvedStyle(selector, tags, ids, classes);
	}
	finally {
	    SearchBuffer.releaseSearchBuffer(sb);
	}
    }

    /**
     * Should be invoked when a new rule is added that did not previously
     * exist. Will go through and refresh the necessary resovled
     * rules.
     */
    private synchronized void refreshResolvedRules(String selectorName,
						   String[] selector,
						   Style newStyle,
						   int specificity) {
	if (resolvedStyles.size() > 0) {
	    Enumeration values = resolvedStyles.elements();
	    while (values.hasMoreElements()) {
		ResolvedStyle style = (ResolvedStyle)values.nextElement();
		if (style.matches(selectorName)) {
		    style.insertStyle(newStyle, specificity);
		}
	    }
	}
    }

    /**
     * A temporary class used to hold a Vector, a StringBuffer and a
     * Hashtable. This is used to avoid allocing a lot of garbage when
     * searching for rules. Use the static method obtainSearchBuffer and
     * releaseSearchBuffer to get a SearchBuffer, and release it when
     * done.
     */
    private static class SearchBuffer {
	/** A stack containing instances of SearchBuffer. Used in getting
	 * rules. */
	static Stack searchBuffers = new Stack();
	// A set of temporary variables that can be used in whatever way.
        Vector vector = null;
        StringBuffer stringBuffer = null;
        Hashtable hashtable = null;

	/**
	 * Returns an instance of SearchBuffer. Be sure and issue
	 * a releaseSearchBuffer when done with it.
	 */
	static SearchBuffer obtainSearchBuffer() {
	    SearchBuffer sb;
	    try {
		sb = (SearchBuffer)searchBuffers.pop();
	    } catch (EmptyStackException ese) {
		sb = new SearchBuffer();
	    }
	    return sb;
	}

	/**
	 * Adds <code>sb</code> to the stack of SearchBuffers that can
	 * be used.
	 */
	static void releaseSearchBuffer(SearchBuffer sb) {
	    sb.empty();
	    searchBuffers.push(sb);
	}

	StringBuffer getStringBuffer() {
	    if (stringBuffer == null) {
		stringBuffer = new StringBuffer();
	    }
	    return stringBuffer;
	}

	Vector getVector() {
	    if (vector == null) {
		vector = new Vector();
	    }
	    return vector;
	}

	Hashtable getHashtable() {
	    if (hashtable == null) {
		hashtable = new Hashtable();
	    }
	    return hashtable;
	}

        void empty() {
	    if (stringBuffer != null) {
		stringBuffer.setLength(0);
	    }
            if (vector != null) {
                vector.removeAllElements();
            }
            if (hashtable != null) {
                hashtable.clear();
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

	BoxPainter(AttributeSet a, CSS css, StyleSheet ss) {
	    this.ss = ss;
	    this.css = css;
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
	    Color color = css.getColor(a, CSS.Attribute.BORDER_COLOR);
	    if (color == null) {
		color = css.getColor(a, CSS.Attribute.COLOR);
		if (color == null) {
		    return Color.black;
		}
	    }
	    return color;
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
	    StyleSheet sheet = ss;

	    // remove margin
	    float top = getLength(CSS.Attribute.MARGIN_TOP, a);
	    float left = getLength(CSS.Attribute.MARGIN_LEFT, a);
	    x += left;
	    y += top;
	    w -= left + getLength(CSS.Attribute.MARGIN_RIGHT, a);
	    h -= top + getLength(CSS.Attribute.MARGIN_BOTTOM, a);

	    Color bg;
	    if (sheet != null) {
		bg = sheet.getBackground(a);
	    }
	    else {
		bg = null;
	    }
	    if (bg != null) {

		g.setColor(bg);
		g.fillRect((int) x, (int) y, (int) w, (int) h);
	    }
	    border.paintBorder(null, g, (int) x, (int) y, (int) w, (int) h);
	}

	float getLength(CSS.Attribute key, AttributeSet a) {
	    return css.getLength(a, key);
	}

	Border border;
	Insets binsets;
	CSS css;
	StyleSheet ss;
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
	    String imgstr = (String)attr.getAttribute(CSS.Attribute.
						      LIST_STYLE_IMAGE);
	    type = null;
	    if (imgstr != null && !imgstr.equals("none")) {
		try {
		    String tmpstr = null;
		    StringTokenizer st = new StringTokenizer(imgstr, "()");
		    if (st.hasMoreTokens())
			tmpstr = st.nextToken();
		    if (st.hasMoreTokens())
			tmpstr = st.nextToken();
		    URL u = new URL(tmpstr);
		    img = new ImageIcon(u);
		} catch (MalformedURLException e) {
		    type = null;
		    img = null;
		}
	    }

	    /* Get the type of bullet to use in the list */
	    if (img == null) {
		type = (CSS.Value)attr.getAttribute(CSS.Attribute.
						    LIST_STYLE_TYPE);
	    }
            // PENDING(sky): Resolve this, there is no way to get the
            // start value. start is an HTML attribute, and not visible
            // from attr.
            start = 1;
	}

	/**
	 * Returns a string that represents the value
	 * of the HTML.Attribute.TYPE attribute.
	 * If this attributes is not defined, then
	 * then the type defaults to "disc" unless
	 * the tag is on Ordered list.  In the case
	 * of the latter, the default type is "decimal".
	 */
	private CSS.Value getChildType(View childView) {
	    CSS.Value childtype = (CSS.Value)childView.getAttributes().
                                  getAttribute(CSS.Attribute.LIST_STYLE_TYPE);

	    if (childtype == null) {
		if (type == null) {
                    // Parent view.
                    View v = childView.getParent();
		    HTMLDocument doc = (HTMLDocument)v.getDocument();
		    if (doc.matchNameAttribute(v.getElement().getAttributes(),
                                               HTML.Tag.OL)) {
			childtype = CSS.Value.DECIMAL;
		    } else {
			childtype = CSS.Value.DISC;
		    }
		} else {
		    childtype = type;
		}
	    }
	    return childtype;
	}

        /**
         * Returns an integer that should be used to render the child at
         * <code>childIndex</code> with. The retValue will usually be
         * <code>childIndex</code> + 1, unless <code>parentView</code>
         * has some Views that do not represent LI's, or one of the views
         * has a HTML.Attribute.START specified.
         */
        private int getRenderIndex(View parentView, int childIndex) {
            int retIndex = childIndex;
            for (int counter = childIndex; counter >= 0; counter--) {
                AttributeSet as = parentView.getElement().getElement(counter).
                                  getAttributes();
                if (as.getAttribute(StyleConstants.NameAttribute) !=
                    HTML.Tag.LI) {
                    retIndex--;
                }
                else {
                    Object value = as.getAttribute(HTML.Attribute.VALUE);
                    if (value != null &&
                        (value instanceof String)) {
                        try {
                            int iValue = Integer.parseInt((String)value);
                            return retIndex - counter + iValue;
                        }
                        catch (NumberFormatException nfe) {}
                    }
                }
            }
            return retIndex + start;
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
            Object name = cv.getElement().getAttributes().getAttribute
                         (StyleConstants.NameAttribute);
            // Only draw something if the View is a list item. This won't
            // be the case for comments.
            if (!(name instanceof HTML.Tag) ||
                name != HTML.Tag.LI) {
                return;
            }
	    CSS.Value childtype = getChildType(cv);
	    float align = cv.getAlignment(View.Y_AXIS);
	    Font font = ((StyledDocument)cv.getDocument()).
		                         getFont(cv.getAttributes());
	    if (font != null) {
		g.setFont(font);
	    }
	    if (childtype == CSS.Value.SQUARE || childtype == CSS.Value.CIRCLE
                || childtype == CSS.Value.DISC) {
    		drawShape(g, childtype, (int) x, (int) y, (int) h, align);
	    } else if (childtype == CSS.Value.CIRCLE) {
    		drawShape(g, childtype, (int) x, (int) y, (int) h, align);
	    } else if (childtype == CSS.Value.DECIMAL) {
		drawLetter(g, '1', (int) x, (int) y, (int) h,
                           getRenderIndex(v, item));
	    } else if (childtype == CSS.Value.LOWER_ALPHA) {
		drawLetter(g, 'a', (int) x, (int) y, (int) h,
                           getRenderIndex(v, item));
	    } else if (childtype == CSS.Value.UPPER_ALPHA) {
		drawLetter(g, 'A', (int) x, (int) y, (int) h,
                           getRenderIndex(v, item));
	    } else if (childtype == CSS.Value.LOWER_ROMAN) {
		drawLetter(g, 'i', (int) x, (int) y, (int) h,
                           getRenderIndex(v, item));
	    } else if (childtype == CSS.Value.UPPER_ROMAN) {
		drawLetter(g, 'I', (int) x, (int) y, (int) h,
                           getRenderIndex(v, item));
	    } else if (childtype == null && img != null) {
    		drawIcon(g, (int) x, (int) y, (int) h, align,
			 v.getContainer());
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
	void drawShape(Graphics g, CSS.Value type, int ax, int ay, int ah,
		       float align) {
	    g.setColor(Color.black);
	    int x = ax - bulletgap - 7;
	    int y = ay + (int)(ah * align) - 3;

	    if (type == CSS.Value.SQUARE) {
		g.drawRect(x, y, 7, 7);
	    } else if (type == CSS.Value.CIRCLE) {
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
	    String str = formatItemNum(index, letter) + ".";
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

        private int start;
        private CSS.Value type;
        URL imageurl;
        Icon img = null;
        private int bulletgap = 5;
    }

    /**
     * An implementation of AttributeSet that can multiplex
     * across a set of AttributeSets.
     */
    static class MuxingAttributeSet implements AttributeSet {
	MuxingAttributeSet(AttributeSet[] attrs) {
	    this.attrs = attrs;
	}

	MuxingAttributeSet() {
	}

	protected synchronized void setAttributes(AttributeSet[] attrs) {
	    this.attrs = attrs;
	}

	/**
	 * Returns the AttributeSets multiplexing too. When the AttributeSets
	 * need to be referenced, this should be called.
	 */
	protected synchronized AttributeSet[] getAttributes() {
	    return attrs;
	}

	/**
	 * Inserts <code>as</code> at <code>index</code>. This assumes
	 * <code>index</code> is >= 0 && <= attrs.length.
	 */
	protected synchronized void insertAttributeSetAt(AttributeSet as,
							 int index) {
	    int numAttrs = attrs.length;
	    AttributeSet newAttrs[] = new AttributeSet[numAttrs + 1];
	    if (index < numAttrs) {
		if (index > 0) {
		    System.arraycopy(attrs, 0, newAttrs, 0, index);
		    System.arraycopy(attrs, index, newAttrs, index + 1,
				     numAttrs - index);
		}
		else {
		    System.arraycopy(attrs, 0, newAttrs, 1, numAttrs);
		}
	    }
	    else {
		System.arraycopy(attrs, 0, newAttrs, 0, numAttrs);
	    }
	    newAttrs[index] = as;
            attrs = newAttrs;
        }

	/**
	 * Removes the AttributeSet at <code>index</code>. This assumes
	 * <code>index</code> is >= 0 && < attrs.length
	 */
	protected synchronized void removeAttributeSetAt(int index) {
	    int numAttrs = attrs.length;
	    AttributeSet[] newAttrs = new AttributeSet[numAttrs - 1];
	    if (numAttrs > 0) {
		if (index == 0) {
		    // FIRST
		    System.arraycopy(attrs, 1, newAttrs, 0, numAttrs - 1);
		}
		else if (index < (numAttrs - 1)) {
		    // MIDDLE
		    System.arraycopy(attrs, 0, newAttrs, 0, index);
		    System.arraycopy(attrs, index + 1, newAttrs, index,
				     numAttrs - index - 1);
		}
		else {
		    // END
		    System.arraycopy(attrs, 0, newAttrs, 0, numAttrs - 1);
		}
	    }
	    attrs = newAttrs;
	}

	//  --- AttributeSet methods ----------------------------

	/**
	 * Gets the number of attributes that are defined.
	 *
	 * @return the number of attributes
	 * @see AttributeSet#getAttributeCount
	 */
        public int getAttributeCount() {
	    AttributeSet[] as = getAttributes();
	    int n = 0;
	    for (int i = 0; i < as.length; i++) {
		n += as[i].getAttributeCount();
	    }
	    return n;
	}

	/**
	 * Checks whether a given attribute is defined.
	 * This will convert the key over to CSS if the
	 * key is a StyleConstants key that has a CSS
	 * mapping.
	 *
	 * @param key the attribute key
	 * @return true if the attribute is defined
	 * @see AttributeSet#isDefined
	 */
        public boolean isDefined(Object key) {
	    AttributeSet[] as = getAttributes();
	    for (int i = 0; i < as.length; i++) {
		if (as[i].isDefined(key)) {
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
	    AttributeSet[] as = getAttributes();
	    MutableAttributeSet a = new SimpleAttributeSet();
	    int n = 0;
	    for (int i = as.length - 1; i >= 0; i--) {
		a.addAttributes(as[i]);
	    }
	    return a;
	}

	/**
	 * Gets the value of an attribute.  If the requested
	 * attribute is a StyleConstants attribute that has
	 * a CSS mapping, the request will be converted.
	 *
	 * @param key the attribute name
	 * @return the attribute value
	 * @see AttributeSet#getAttribute
	 */
        public Object getAttribute(Object key) {
	    AttributeSet[] as = getAttributes();
	    int n = as.length;
	    for (int i = 0; i < n; i++) {
		Object o = as[i].getAttribute(key);
		if (o != null) {
		    return o;
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
	    return new MuxingAttributeNameEnumeration();
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
	 * Returns null, subclasses may wish to do something more
	 * intelligent with this.
	 */
        public AttributeSet getResolveParent() {
	    return null;
	}

	AttributeSet[] attrs;


	/**
	 * An Enumeration of the Attribute names in a MuxingAttributeSet.
	 * This may return the same name more than once.
	 */
	class MuxingAttributeNameEnumeration implements Enumeration {

	    MuxingAttributeNameEnumeration() {
		updateEnum();
	    }

	    public boolean hasMoreElements() {
		if (currentEnum == null) {
		    return false;
		}
		return currentEnum.hasMoreElements();
	    }

	    public Object nextElement() {
		if (currentEnum == null) {
		    throw new NoSuchElementException("No more names");
		}
		Object retObject = currentEnum.nextElement();
		if (!currentEnum.hasMoreElements()) {
		    updateEnum();
		}
		return retObject;
	    }

	    void updateEnum() {
		AttributeSet[] as = getAttributes();
		currentEnum = null;
		while (currentEnum == null && attrIndex < as.length) {
		    currentEnum = as[attrIndex++].getAttributeNames();
		    if (!currentEnum.hasMoreElements()) {
			currentEnum = null;
		    }
		}
	    }


	    /** Index into attrs the current Enumeration came from. */
	    int attrIndex;
	    /** Enumeration from attrs. */
	    Enumeration currentEnum;
	}
    }


    /**
     * A subclass of MuxingAttributeSet that translates between
     * CSS and HTML and StyleConstants. The AttributeSets used are
     * the CSS rules that match the Views Elements.
     */
    class ViewAttributeSet extends MuxingAttributeSet {
	ViewAttributeSet(View v) {
	    host = v;

	    // PENDING(prinz) fix this up to be a more realistic
	    // implementation.
	    Document doc = v.getDocument();
	    SearchBuffer sb = SearchBuffer.obtainSearchBuffer();
	    Vector muxList = sb.getVector();
	    try {
		if (doc instanceof HTMLDocument) {
		    StyleSheet styles = StyleSheet.this;
		    Element elem = v.getElement();
		    AttributeSet a = elem.getAttributes();
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
				   it will be possible to specificity this
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
			HTML.Tag t = (HTML.Tag) a.getAttribute
			             (StyleConstants.NameAttribute);
			if (t == HTML.Tag.IMPLIED) {
			    t = HTML.Tag.P;
			}
			AttributeSet cssRule = styles.getRule(t, elem);
			if (cssRule != null) {
			    muxList.addElement(cssRule);
			}
		    }
		}
		this.attrs = new AttributeSet[muxList.size()];
		muxList.copyInto(this.attrs);
	    }
	    finally {
		SearchBuffer.releaseSearchBuffer(sb);
	    }
	}

	//  --- AttributeSet methods ----------------------------

	/**
	 * Checks whether a given attribute is defined.
	 * This will convert the key over to CSS if the
	 * key is a StyleConstants key that has a CSS
	 * mapping.
	 *
	 * @param key the attribute key
	 * @return true if the attribute is defined
	 * @see AttributeSet#isDefined
	 */
        public boolean isDefined(Object key) {
	    if (key instanceof StyleConstants) {
		Object cssKey = css.styleConstantsKeyToCSSKey
		                    ((StyleConstants)key);
		if (cssKey != null) {
		    key = cssKey;
		}
	    }
	    return super.isDefined(key);
	}

	/**
	 * Gets the value of an attribute.  If the requested
	 * attribute is a StyleConstants attribute that has
	 * a CSS mapping, the request will be converted.
	 *
	 * @param key the attribute name
	 * @return the attribute value
	 * @see AttributeSet#getAttribute
	 */
        public Object getAttribute(Object key) {
	    if (key instanceof StyleConstants) {
		Object cssKey = css.styleConstantsKeyToCSSKey
		               ((StyleConstants)key);
		if (cssKey != null) {
		    Object value = doGetAttribute(cssKey);
		    if (value instanceof CSS.CssValue) {
			return ((CSS.CssValue)value).toStyleConstants
			             ((StyleConstants)key);
		    }
		}
	    }
	    return doGetAttribute(key);
	}

        Object doGetAttribute(Object key) {
	    Object retValue = super.getAttribute(key);
	    if (retValue != null) {
		return retValue;
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
	 * If not overriden, the resolving parent defaults to
	 * the parent element.
	 *
	 * @return the attributes from the parent
	 * @see AttributeSet#getResolveParent
	 */
        public AttributeSet getResolveParent() {
	    if (host == null) {
		return null;
	    }
	    View parent = host.getParent();
	    return (parent != null) ? parent.getAttributes() : null;
	}

	/** View created for. */
	View host;
    }


    /**
     * A subclass of MuxingAttributeSet that implements Style. Currently
     * the MutableAttributeSet methods are unimplemented, that is they
     * do nothing.
     */
    // PENDING(sky): Decide what to do with this. Either make it
    // contain a SimpleAttributeSet that modify methods are delegated to,
    // or change getRule to return an AttributeSet and then don't make this
    // implement Style.
    static class ResolvedStyle extends MuxingAttributeSet implements
                  Style {
	ResolvedStyle(String name, AttributeSet[] attrs, int extendedIndex) {
	    this.attrs = attrs;
	    this.name = name;
	    this.extendedIndex = extendedIndex;
	}

        /**
         * Inserts a Style into the receiver so that the styles the
         * receiver represents are still ordered by specificity.
	 * <code>style</code> will be added before any extended styles, that
	 * is before extendedIndex.
         */
        synchronized void insertStyle(Style style, int specificity) {
            int maxCounter = attrs.length;
	    int counter = 0;
            for (;counter < extendedIndex; counter++) {
		if (specificity > getSpecificity(((Style)attrs[counter]).
						 getName())) {
		    break;
                }
            }
	    insertAttributeSetAt(style, counter);
	    extendedIndex++;
	}

	/**
	 * Removes a previously added style. This will do nothing if
	 * <code>style</code> is not referenced by the receiver.
	 */
	synchronized void removeStyle(Style style) {
	    for (int counter = attrs.length - 1; counter >= 0; counter--) {
		if (attrs[counter] == style) {
		    removeAttributeSetAt(counter);
		    if (counter < extendedIndex) {
			extendedIndex--;
		    }
		    break;
		}
	    }
	}

	/**
	 * Adds <code>s</code> as one of the Attributesets to look up
	 * attributes in.
	 */
	synchronized void insertExtendedStyleAt(Style attr, int index) {
	    insertAttributeSetAt(attr, extendedIndex + index);
	}

	/**
	 * Adds <code>s</code> as one of the Attributesets to look up
	 * attributes in. It will be the AttributeSet last checked.
	 */
	synchronized void addExtendedStyle(Style attr) {
	    insertAttributeSetAt(attr, attrs.length);
	}

	/**
	 * Removes the style at <code>index</code> +
	 * <code>extendedIndex</code>.
	 */
	synchronized void removeExtendedStyleAt(int index) {
	    removeAttributeSetAt(extendedIndex + index);
	}

        /**
         * Returns true if the receiver matches <code>selector</code>, where
         * a match is defined by the CSS rule matching.
	 * Each simple selector must be separated by a single space.
         */
        protected boolean matches(String selector) {
            int sLast = selector.length();

            if (sLast == 0) {
                return false;
            }
            int thisLast = name.length();
            int sCurrent = selector.lastIndexOf(' ');
            int thisCurrent = name.lastIndexOf(' ');
	    if (sCurrent >= 0) {
		sCurrent++;
	    }
	    if (thisCurrent >= 0) {
		thisCurrent++;
	    }
            if (!matches(selector, sCurrent, sLast, thisCurrent, thisLast)) {
                return false;
            }
            while (sCurrent != -1) {
                sLast = sCurrent - 1;
                sCurrent = selector.lastIndexOf(' ', sLast - 1);
		if (sCurrent >= 0) {
		    sCurrent++;
		}
                boolean match = false;
                while (!match && thisCurrent != -1) {
                    thisLast = thisCurrent - 1;
                    thisCurrent = name.lastIndexOf(' ', thisLast - 1);
		    if (thisCurrent >= 0) {
			thisCurrent++;
		    }
                    match = matches(selector, sCurrent, sLast, thisCurrent,
				    thisLast);
                }
                if (!match) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns true if the substring of the receiver, in the range
         * thisCurrent, thisLast matches the substring of selector in
         * the ranme sCurrent to sLast based on CSS selector matching.
         */
        boolean matches(String selector, int sCurrent, int sLast,
                       int thisCurrent, int thisLast) {
            sCurrent = Math.max(sCurrent, 0);
            thisCurrent = Math.max(thisCurrent, 0);
            int thisDotIndex = boundedIndexOf(name, '.', thisCurrent,
					      thisLast);
            int thisPoundIndex = boundedIndexOf(name, '#', thisCurrent,
						thisLast);
            int sDotIndex = boundedIndexOf(selector, '.', sCurrent, sLast);
            int sPoundIndex = boundedIndexOf(selector, '#', sCurrent, sLast);
            if (sDotIndex != -1) {
                // Selector has a '.', which indicates name must match it,
                // or if the '.' starts the selector than name must have
                // the same class (doesn't matter what element name).
                if (thisDotIndex == -1) {
                    return false;
                }
                if (sCurrent == sDotIndex) {
                    if ((thisLast - thisDotIndex) != (sLast - sDotIndex) || 
                        !selector.regionMatches(sCurrent, name, thisDotIndex,
                                                (thisLast - thisDotIndex))) {
                        return false;
                    }
                }
                else {
                    // Has to fully match.
                    if ((sLast - sCurrent) != (thisLast - thisCurrent) ||
                        !selector.regionMatches(sCurrent, name, thisCurrent,
                                                (thisLast - thisCurrent))) {
                        return false;
                    }
                }
                return true;
            }
            if (sPoundIndex != -1) {
                // Selector has a '#', which indicates name must match it,
                // or if the '#' starts the selector than name must have
                // the same id (doesn't matter what element name).
                if (thisPoundIndex == -1) {
                    return false;
                }
                if (sCurrent == sPoundIndex) {
                    if ((thisLast - thisPoundIndex) !=(sLast - sPoundIndex) || 
                        !selector.regionMatches(sCurrent, name, thisPoundIndex,
                                                (thisLast - thisPoundIndex))) {
                        return false;
                    }
                }
                else {
                    // Has to fully match.
                    if ((sLast - sCurrent) != (thisLast - thisCurrent) ||
                        !selector.regionMatches(sCurrent, name, thisCurrent,
                                               (thisLast - thisCurrent))) {
                        return false;
                    }
                }
                return true;
            }
            if (thisDotIndex != -1) {
                // Reciever references a class, just check element name.
                return selector.regionMatches(sCurrent, name, thisCurrent,
                                              thisDotIndex - thisCurrent);
            }
            if (thisPoundIndex != -1) {
                // Reciever references an id, just check element name.
                return selector.regionMatches(sCurrent, name, thisCurrent,
                                              thisPoundIndex - thisCurrent);
            }
            // Fail through, no classes or ides, just check string.
            return selector.regionMatches(sCurrent, name, thisCurrent,
                                              thisLast - thisCurrent);
        }

        /**
         * Similiar to String.indexOf, but allows an upper bound
         * (this is slower in that it will still check string starting at
         * start.
         */
        int boundedIndexOf(String string, char search, int start,
                           int end) {
            int retValue = string.indexOf(search, start);
            if (retValue >= end) {
                return -1;
            }
            return retValue;
        }

	public void addAttribute(Object name, Object value) {}
	public void addAttributes(AttributeSet attributes) {}
	public void removeAttribute(Object name) {}
	public void removeAttributes(Enumeration names) {}
	public void removeAttributes(AttributeSet attributes) {}
	public void setResolveParent(AttributeSet parent) {}
	public String getName() {return name;}
	public void addChangeListener(ChangeListener l) {}
	public void removeChangeListener(ChangeListener l) {}

	/** The name of the Style, which is the selector.
	 * This will NEVER change!
	 */
	String name;
	/** Start index of styles coming from other StyleSheets. */
	private int extendedIndex;
    }

    // ---- Variables ---------------------------------------------

    final static int DEFAULT_FONT_SIZE = 3;

    private CSS css;

    /** An inverted graph of the selectors. Each key will be a String tag,
     * and each value will be a Hashtable. Further the key RULE is used
     * for the styles attached to the particular location.
     */
    private Hashtable selectorMapping;

    /** Maps from selector (as a string) to Style that includes all
     * relevant styles. */
    private Hashtable resolvedStyles;

    /** Vector of StyleSheets that the rules are to reference.
     */
    private Vector linkedStyleSheets;

    /** Where the style sheet was found. Used for relative imports. */
    private URL base;


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

    static final SheetAttribute SPECIFICITY = new SheetAttribute("specificity", false);
    static final SheetAttribute RULE = new SheetAttribute("rule", false);
    static final SheetAttribute WEIGHT = new SheetAttribute("rule-weight", false);

    static final SheetAttribute BOX_PAINTER = new SheetAttribute("box-painter", true);
    static final SheetAttribute LIST_PAINTER = new SheetAttribute("list-painter", true);


    static {
	StyleContext.registerStaticAttributeKey(RULE);
	StyleContext.registerStaticAttributeKey(WEIGHT);
	StyleContext.registerStaticAttributeKey(BOX_PAINTER);
	StyleContext.registerStaticAttributeKey(LIST_PAINTER);
    }


    /**
     * Default parser for css specifications that get loaded into
     * the StyleSheet.<p>
     * This class is not thread safe, do not ask it to parse while it is
     * in the middle of parsing.
     */
    class CssParser implements Serializable {

	/**
	 * Parses the passed in CSS declaration into an AttributeSet.
	 */
	AttributeSet parseDeclaration(String string) {
	    try {
		return parseDeclaration(new StringReader(string));
	    } catch (IOException ioe) {}
	    return null;
	}

	/**
	 * Parses the passed in CSS declaration into an AttributeSet.
	 */
	AttributeSet parseDeclaration(Reader r) throws IOException {
	    parse(base, r, true, false);
	    return declaration.copyAttributes();
	}

	/**
	 * Parse the given css stream
	 */
	void parse(URL base, Reader r, boolean parseDeclaration,
				boolean isLink) throws IOException {
	    this.base = base;
	    this.isLink = isLink;
	    declaration.removeAttributes(declaration);
	    selectorTokens.removeAllElements();
	    selectors.removeAllElements();
	    ruleTokens.removeAllElements();
	    this.parsingDeclaration = parseDeclaration;
	    StreamTokenizer st = new StreamTokenizer(r);
	    st.ordinaryChar('/');
	    st.slashStarComments(true);
	    // Treat # as a word.
	    st.wordChars('#', '#');
	    boolean inRule = parseDeclaration;
	    // Set to true when an '@' is encountered.
	    boolean inAt = false;
	    // Set to true if import is reached.
	    boolean inImport = false;
	    // Set to true when first selector/rule is encountered.
	    boolean encounteredRule = false;
	    // The import is added here.
	    StringBuffer importBuffer = null;
	    // Set to true if the last character was a dot ('.')
	    boolean lastWasDot = false;
	    char[] tempChar = new char[1];
	    for (int token = st.nextToken(); token != StreamTokenizer.TT_EOF;
		 token = st.nextToken()) {

		// process tokens
		switch (token) {
		case StreamTokenizer.TT_WORD:
		    if (inAt) {
			if (inImport) {
			    importBuffer.append(st.sval);
			}
			else if (st.sval.equals("import")) {
			    inImport = true;
			    if (importBuffer == null) {
				importBuffer = new StringBuffer();
			    }
			    else {
				importBuffer.setLength(0);
			    }
			}
		    }
		    else if (inRule) {
			ruleTokens.addElement(st.sval);
		    } else {
			if (lastWasDot) {
			    selectorTokens.addElement('.' + st.sval);
			}
			else {
			    selectorTokens.addElement(st.sval);
			}
			encounteredRule = true;
		    }
		    lastWasDot = false;
		    break;
		case StreamTokenizer.TT_NUMBER:
		    lastWasDot = false;
		    if (inAt) {
			if (inImport) {
			    importBuffer.append(st.nval);
			}
		    }
		    else if (inRule) {
			ruleTokens.addElement(new Integer((int) st.nval));
		    } else {
			if (st.nval == 0) {
			    // Most likely indicates a selector is starting
			    // with a . (eg .foo)
			    lastWasDot = true;
			}
			else {
			    String s = (String) selectorTokens.lastElement();
			    s = s + ((int) st.nval);
			    selectorTokens.setElementAt(s, selectorTokens.
							size()-1);
			    encounteredRule = true;
			}
		    }
		    break;
		case StreamTokenizer.TT_EOL:
		    break;
		default:
		    lastWasDot = false;
		    if (inAt) {
			if (st.ttype == ';') {
			    inAt = false;
			    if (inImport) {
				if (!encounteredRule) {
				    addImport(importBuffer.toString());
				}
				inImport = false;
			    }
			}
			else if (inImport) {
			    if (st.ttype == '\'' ||
				st.ttype == '"') {
				importBuffer.append(st.sval);
			    }
			    else {
				importBuffer.append((char)st.ttype);
			    }
			}
		    }
		    else {
			switch(st.ttype) {
			case '{':
			    encounteredRule = true;
			    addSelector();
			    inRule = true;
			    break;
			case '}':
			    if (ruleTokens.size() > 0) {
				// Won't happen if just encountered a ;
				addKeyValueToDeclaration();
			    }
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
			case '@':
			    inAt = true;
			    inImport = false;
			    break;
			default:
			    if (inRule) {
				ruleTokens.addElement(new Character(','));
			    } else {
				if (selectorTokens.size() == 0) {
				    tempChar[0] = (char)st.ttype;
				    selectorTokens.addElement(new String
							      (tempChar));
				}
				else {
				    String s = (String) selectorTokens.
				                    lastElement();
				    s = s + ((char) st.ttype);
				    selectorTokens.setElementAt(s,
					       selectorTokens.size()-1);
				}
			    }
			}
		    }
		}
	    }
	    if (parsingDeclaration && ruleTokens.size() > 0) {
		addKeyValueToDeclaration();
	    }
	}

	void addImport(String importString) {
	    if (importString.startsWith("url(") &&
		importString.endsWith(")")) {
		try {
		    URL url = new URL(importString.substring
				      (4, importString.length() - 1));
		    if (url != null) {
			importStyleSheet(url);
		    }
		} catch (MalformedURLException mue) {
		}
	    }
	    else if (base != null) {
		// Relative URL, try from base
		try {
		    URL url = new URL(base, importString);
		    if (url != null) {
			importStyleSheet(url);
		    }
		}
		catch (MalformedURLException muee) {
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
	    return css.getInternalCSSValue(key, getDeclarationString());
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
	    if (parsingDeclaration) {
		throw new RuntimeException("Declaration should not contain rules");
	    }
	    int n = selectors.size();
	    for (int i = 0; i < n; i++) {
		String[] selector = (String[]) selectors.elementAt(i);
		if (selector.length > 0) {
		    StyleSheet.this.addRule(selector, declaration, isLink);
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
	/** True if parsing a declaration, that is the Reader will not
	 * contain a selector. */
	boolean parsingDeclaration;
	/** True if the attributes are coming from a linked/imported style. */
	boolean isLink;
	URL base;
    }
}
