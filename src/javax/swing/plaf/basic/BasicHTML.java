/*
 * @(#)BasicHTML.java	1.2 98/11/10
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
package javax.swing.plaf.basic;

import java.io.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

/**
 * Support for providing html views for the swing components.
 * This translates a simple html string to a javax.swing.text.View
 * implementation that can render the html and provide the necessary
 * layout semantics.
 *
 * @author  Timothy Prinzing
 * @version 1.2 11/10/98
 */
/*public*/ class BasicHTML {

    /**
     * Create an html renderer for the given component and
     * string of html.
     */
    public static View createHTMLView(JComponent c, String html) {
	HTMLEditorKit kit = getFactory();
	Document doc = kit.createDefaultDocument();
	Reader r = new StringReader(html);
	try {
	    kit.read(r, doc, 0);
	} catch (Throwable e) {
	}
	ViewFactory f = kit.getViewFactory();
	View hview = f.create(doc.getDefaultRootElement());
	View v = new Renderer(c, f, hview);
	return v;
    }

    /**
     * Check the given string to see if it should trigger the
     * html rendering logic.
     */
    public static boolean isHTMLString(String s) {
	if (s != null) {
	    return s.startsWith("<html>");
	}
	return false;
    }

    /**
     * Stash the HTML render for the given text into the client
     * properties of the given JComponent. If the given text is 
     * <em>NOT HTML</em> the property will be cleared of any
     * renderer.
     * <p>
     * This method is useful for ComponentUI implementations
     * that are static (i.e. shared) and get their state
     * entirely from the JComponent.
     */
    public static void updateRenderer(JComponent c, String text) {
	View value = null;
	String key = null;
	if (BasicHTML.isHTMLString(text)) {
	    value = BasicHTML.createHTMLView(c, text);
	}
	c.putClientProperty(BasicHTML.propertyKey, value);
    }

    /**
     * Key to use for the html renderer when stored as a 
     * client property of a JComponent.
     */
    public static final String propertyKey = "html";

    static HTMLEditorKit getFactory() {
	if (basicHTMLFactory == null) {
	    basicHTMLFactory = new BasicEditorKit();

	    // Ideally you would create a new StyleSheet and call
	    // newSS.addStyleSheet(basicHTMLFactory.getStyleSheet()),
	    // but addStyleSheet is package private:(
	    // Also, we can't use the basic editor kit because it uses
	    // the sharedStyles static we are trying to initialize!!
	    // It might be better to create a StyleSheet directly, but
	    // currently we still leverage the defaults.
	    EditorKit tmpKit = new HTMLEditorKit();
	    HTMLDocument doc = (HTMLDocument) tmpKit.createDefaultDocument();
	    sharedStyles = doc.getStyleSheet();
	    try {
		StringReader r = new StringReader(styleChanges);
		sharedStyles.loadRules(r, null);
	    } catch (Throwable e) {
		// don't want to die in static initialization... 
		// just display things wrong.
	    }
	}
	return basicHTMLFactory;
    }

    /**
     * The source of the html renderers
     */
    private static HTMLEditorKit basicHTMLFactory;

    private static StyleSheet sharedStyles;

    /**
     * Overrides to the default stylesheet.  Should consider
     * just creating a completely fresh stylesheet.
     */
    private static final String styleChanges = 
    "p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }" +
    "body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }";

    /**
     * The views produced for the ComponentUI implementations aren't
     * going to be edited and don't need full html support.  This kit
     * alters the HTMLEditorKit to try and trim things down a bit.  
     * It does the following:
     * <ul>
     * <li>It doesn't produce elements for things like comments, 
     * head, title, unknown tags, etc.  
     * <li>It shares the StyleSheet across all the documents.
     * <li>It installs a different set of css settings from the default
     * provided by HTMLEditorKit.
     * </ul>
     */
    static class BasicEditorKit extends HTMLEditorKit {

        public Document createDefaultDocument() {
	    Document doc = new BasicDocument(sharedStyles);
	    return doc;
	}
	
    }

    /**
     * This must be subclassed to provide a different HTMLReader 
     * implementation.
     */
    static class BasicDocument extends HTMLDocument {

	BasicDocument(StyleSheet s) {
	    super(s);
	}

        public HTMLEditorKit.ParserCallback getReader(int pos) {
	    HTMLReader reader = new BasicReader(pos);
	    return reader;
	}

	class BasicReader extends HTMLReader {

	    public BasicReader(int offset) {
		super(offset);
		TagAction ignore = new TagAction();
		registerTag(HTML.Tag.HEAD, ignore);
	    }
	}

    }

    /**
     * Root text view that acts as an HTML renderer.
     */
    static class Renderer extends View {

        Renderer(JComponent c, ViewFactory f, View v) {
            super(null);
	    host = c;
	    factory = f;
	    view = v;
	    view.setParent(this);
	    // initially layout to the preferred size
	    setSize(view.getPreferredSpan(X_AXIS), view.getPreferredSpan(Y_AXIS));
        }

	/**
	 * Fetches the attributes to use when rendering.  At the root
	 * level there are no attributes.  If an attribute is resolved
	 * up the view hierarchy this is the end of the line.
	 */
        public AttributeSet getAttributes() {
	    return null;
	}

        /**
         * Determines the preferred span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getPreferredSpan(int axis) {
	    if (axis == X_AXIS) {
		// width currently laid out to
		return width;
	    }
	    return view.getPreferredSpan(axis);
        }

        /**
         * Determines the minimum span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getMinimumSpan(int axis) {
	    return view.getMinimumSpan(axis);
        }

        /**
         * Determines the maximum span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getMaximumSpan(int axis) {
	    return Integer.MAX_VALUE;
        }

        /**
         * Specifies that a preference has changed.
         * Child views can call this on the parent to indicate that
         * the preference has changed.  The root view routes this to
         * invalidate on the hosting component.
         * <p>
         * This can be called on a different thread from the
         * event dispatching thread and is basically unsafe to
         * propagate into the component.  To make this safe,
         * the operation is transferred over to the event dispatching 
         * thread for completion.  It is a design goal that all view
         * methods be safe to call without concern for concurrency,
         * and this behavior helps make that true.
         *
         * @param child the child view
         * @param width true if the width preference has changed
         * @param height true if the height preference has changed
         */ 
        public void preferenceChanged(View child, boolean width, boolean height) {
            host.revalidate();
        }

        /**
         * Determines the desired alignment for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the desired alignment, where 0.0 indicates the origin
         *     and 1.0 the full span away from the origin
         */
        public float getAlignment(int axis) {
	    return view.getAlignment(axis);
        }

        /**
         * Renders the view.
         *
         * @param g the graphics context
         * @param allocation the region to render into
         */
        public void paint(Graphics g, Shape allocation) {
	    Rectangle alloc = allocation.getBounds();
	    view.setSize(alloc.width, alloc.height);
	    view.paint(g, allocation);
        }
        
        /**
         * Sets the view parent.
         *
         * @param parent the parent view
         */
        public void setParent(View parent) {
            throw new Error("Can't set parent on root view");
        }

        /** 
         * Returns the number of views in this view.  Since
         * this view simply wraps the root of the view hierarchy
         * it has exactly one child.
         *
         * @return the number of views
         * @see #getView
         */
        public int getViewCount() {
            return 1;
        }

        /** 
         * Gets the n-th view in this container.
         *
         * @param n the number of the view to get
         * @return the view
         */
        public View getView(int n) {
            return view;
        }

        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.
         *
         * @param pos the position to convert
         * @param a the allocated region to render into
         * @return the bounding box of the given position
         */
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	    return view.modelToView(pos, a, b);
        }

	/**
	 * Provides a mapping from the document model coordinate space
	 * to the coordinate space of the view mapped to it.
	 *
	 * @param p0 the position to convert >= 0
	 * @param b0 the bias toward the previous character or the
	 *  next character represented by p0, in case the 
	 *  position is a boundary of two views. 
	 * @param p1 the position to convert >= 0
	 * @param b1 the bias toward the previous character or the
	 *  next character represented by p1, in case the 
	 *  position is a boundary of two views. 
	 * @param a the allocated region to render into
	 * @return the bounding box of the given position is returned
	 * @exception BadLocationException  if the given position does
	 *   not represent a valid location in the associated document
	 * @exception IllegalArgumentException for an invalid bias argument
	 * @see View#viewToModel
	 */
	public Shape modelToView(int p0, Position.Bias b0, int p1, 
				 Position.Bias b1, Shape a) throws BadLocationException {
	    return view.modelToView(p0, b0, p1, b1, a);
	}

        /**
         * Provides a mapping from the view coordinate space to the logical
         * coordinate space of the model.
         *
         * @param x x coordinate of the view location to convert
         * @param y y coordinate of the view location to convert
         * @param a the allocated region to render into
         * @return the location within the model that best represents the
         *    given point in the view
         */
        public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	    return view.viewToModel(x, y, a, bias);
        }

        /**
         * Returns the document model underlying the view.
         *
         * @return the model
         */
        public Document getDocument() {
            return view.getDocument();
        }
        
        /**
         * Returns the starting offset into the model for this view.
         *
         * @return the starting offset
         */
        public int getStartOffset() {
	    return view.getStartOffset();
        }

        /**
         * Returns the ending offset into the model for this view.
         *
         * @return the ending offset
         */
        public int getEndOffset() {
	    return view.getEndOffset();
        }

        /**
         * Gets the element that this view is mapped to.
         *
         * @return the view
         */
        public Element getElement() {
	    return view.getElement();
        }

        /**
         * Sets the view size.
         *
         * @param width the width
         * @param height the height
         */
        public void setSize(float width, float height) {
	    this.width = (int) width;
	    view.setSize(width, height);
        }

        /**
         * Fetches the container hosting the view.  This is useful for
         * things like scheduling a repaint, finding out the host 
         * components font, etc.  The default implementation
         * of this is to forward the query to the parent view.
         *
         * @return the container
         */
        public Container getContainer() {
            return host;
        }
        
        /**
         * Fetches the factory to be used for building the
         * various view fragments that make up the view that
         * represents the model.  This is what determines
         * how the model will be represented.  This is implemented
         * to fetch the factory provided by the associated
         * EditorKit.
         *
         * @return the factory
         */
        public ViewFactory getViewFactory() {
	    return factory;
        }

	private int width;
        private View view;
	private ViewFactory factory;
	private JComponent host;

    }
}
