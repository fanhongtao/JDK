/*
 * @(#)MinimalHTMLWriter.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.text.html;

import java.io.Writer;
import java.io.IOException;
import java.util.Enumeration;
import java.awt.Color;
import javax.swing.text.*;

/**
 * MinimalHTMLWriter is a fallback writer used by the
 * HTMLEditorKit to write out HTML for a document that
 * is a not produced by the EditorKit.
 *
 * The format for the document is:
 * <html>
 *   <head>
 *     <style>
 *        <!-- list of named styles
 *         p.normal {
 *            font-family: SansSerif;
 *	      margin-height: 0;
 *	      font-size: 14
 *	   }
 *        -->
 *      </style>
 *   </head>
 *   <body>
 *    <p style=normal>
 *     <b>bold, italic and underline attributes
 *        of the run are emitted as html tags.
 *        The remaining attributes are emitted as
 *        part  style attribute of a <font> tag.
 *        The syntax is similar to inline styles.</b>
 *    </p>
 *   </body>
 * </html>
 *
 * @author Sunita Mani
 * @version 1.9, 11/29/01
 */

public class MinimalHTMLWriter extends AbstractWriter {

    /**
     * These static finals are used to
     * tweak & query the fontMask about which
     * of these tags need to be generated, or
     * terminated.
     */
    private static final int BOLD = 0x01;
    private static final int ITALIC = 0x02;
    private static final int UNDERLINE = 0x04;

    // Used to map StyleConstants to CSS.
    private static final CSS css = new CSS();

    private int fontMask = 0;

    int startOffset = 0;
    int endOffset = 0;

    /**
     * stores the attributes of the previous run.
     * Used to compare with the current run's
     * attributeset.  If identical, then a
     * <font> tag is not emitted.
     */
    private AttributeSet fontAttributes;

    /**
     * Creates a new MinimalHTMLWriter.
     *
     * @param a  Writer
     * @param an StyledDocument
     *
     */
    public MinimalHTMLWriter(Writer w, StyledDocument doc) {
	super(w, doc);
    }

    /**
     * Creates a new MinimalHTMLWriter.
     *
     * @param a  Writer
     * @param an StyledDocument
     * @param pos The location in the document to fetch the
     *   content.
     * @param len The amount to write out.
     *
     */
    public MinimalHTMLWriter(Writer w, StyledDocument doc, int pos, int len) {
	super(w, doc, pos, len);
    }

    /**
     * This method is responsible for generating html output
     * from a StyledDocument.
     *
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *            location within the document.
     *
     */
    public void write() throws IOException, BadLocationException {
	writeStartTag("<html>");
	writeHeader();
	writeBody();
	writeEndTag("</html>");
    }


    /**
     * This method writes out all the attributes that are for the
     * following types:
     *  StyleConstants.ParagraphConstants
     *  StyleConstants.CharacterConstants
     *  StyleConstants.FontConstants
     *  StyleConstants.ColorConstants.
     * The attribute name and value are separated by a colon
     * And each pair is separatd by a semicolon.
     *
     * @exception IOException on any I/O error
     */
    protected void writeAttributes(AttributeSet attr) throws IOException {
	Enumeration attributeNames = attr.getAttributeNames();
	while (attributeNames.hasMoreElements()) {
	    Object name = attributeNames.nextElement();
	    if ((name instanceof StyleConstants.ParagraphConstants) ||
		(name instanceof StyleConstants.CharacterConstants) ||
		(name instanceof StyleConstants.FontConstants) ||
		(name instanceof StyleConstants.ColorConstants)) {
		indent();
		write(name.toString());
		write(':');
		write(css.styleConstantsValueToCSSValue
		      ((StyleConstants)name, attr.getAttribute(name)).
		      toString());
		write(';');
		write(NEWLINE);
	    }
	}
    }


    /**
     * This method is responsible for writing text out.
     *
     * @exception IOException on any I/O error
     */
    protected void text(Element elem) throws IOException, BadLocationException {
	String contentStr = getText(elem);
	if ((contentStr.length() > 0) && 
	    (contentStr.charAt(contentStr.length()-1) == NEWLINE)) {
	    contentStr = contentStr.substring(0, contentStr.length()-1);
	}
	if (contentStr.length() > 0) {
	    write(contentStr);
	}
    }

    /**
     * This method writes out a start tag approrirately
     * indented.  It also increments the indent level.
     *
     * @exception IOException on any I/O error
     */
    protected void writeStartTag(String tag) throws IOException {
	indent();
	write(tag);
	write(NEWLINE);
	incrIndent();
    }


    /**
     * This method writes out a end tag approrirately
     * indented.  It also decrements the indent level.
     *
     * @exception IOException on any I/O error
     */
    protected void writeEndTag(String endTag) throws IOException {
	decrIndent();
	indent();
	write(endTag);
	write(NEWLINE);
    }


    /**
     * This method writes out the <head> and <style>
     * tags.  It Then invokes writeStyles() to write
     * out all the named styles as the content of the
     * <style> tag.  The content is surrounded by
     * valid html comment markers to ensure that the
     * document is viewable in applications/browsers
     * that do not support the tag.
     *
     * @exception IOException on any I/O error
     */
    protected void writeHeader() throws IOException {
	writeStartTag("<head>");
	writeStartTag("<style>");
	writeStartTag("<!--");
	writeStyles();
	writeEndTag("-->");
	writeEndTag("</style>");
	writeEndTag("</head>");
    }



    /**
     * This method writes out all the named styles as the
     * content of the <style> tag.
     *
     * @exception IOException on any I/O error
     */
    protected void writeStyles() throws IOException {
	/*
	 *  Access to DefaultStyledDocument done to workaround
	 *  a missing API in styled document to access the
	 *  stylenames.
	 */
	DefaultStyledDocument styledDoc =  ((DefaultStyledDocument)getDocument());
	Enumeration styleNames = styledDoc.getStyleNames();

	while (styleNames.hasMoreElements()) {
	    Style s = styledDoc.getStyle((String)styleNames.nextElement());

	    /** PENDING: Once the name attribute is removed
		from the list we check check for 0. **/
	    if (s.getAttributeCount() == 1 &&
		s.isDefined(StyleConstants.NameAttribute)) {
		continue;
	    }
	    indent();
	    write("p." + s.getName());
	    write(" {\n");
	    incrIndent();
	    writeAttributes(s);
	    decrIndent();
	    indent();
	    write("}\n");
	}
    }


    /**
     * This method iterates over the elements in the document
     * and processes elements based on whether they are
     * branch elements or leaf elements.  It specially handles
     * leaf elements that are text.
     *
     * @exception IOException on any I/O error
     */
    protected void writeBody() throws IOException, BadLocationException {
	ElementIterator it = getElementIterator();

	/*
	  This will be a section element for a styled document.
	  We represent this element in html as the body tags.
	  Therefore we ignore it.
	 */
	it.current();

	Element next = null;

	writeStartTag("<body>");

	boolean inContent = false;

	while((next = it.next()) != null) {
	    if (!inRange(next)) {
		continue;
	    }
	    if (next instanceof AbstractDocument.BranchElement) {
		if (inContent) {
		    writeEndParagraph();
		    inContent = false;
		    fontMask = 0;
		}
		writeStartParagraph(next);
	    } else if (isText(next)) {
		writeContent(next, !inContent);
		inContent = true;
	    } else {
		writeLeaf(next);
		inContent = true;
	    }
	}
	if (inContent) {
	    writeEndParagraph();
	}
	writeEndTag("</body>");
    }


    /**
     * This method handles emiting an end tag for a <p>
     * tag.  Prior to writing out the tag, it ensures
     * that all other tags that have been opened are
     * appropriately closed off.
     *
     * @exception IOException on any I/O error
     */
    protected void writeEndParagraph() throws IOException {
	writeEndMask(fontMask);
	if (inFontTag()) {
	    endFontTag();
	} else {
	    write(NEWLINE);
	}
	writeEndTag("</p>");
    }


    /**
     * This method emits the start tag for a paragraph. If
     * the paragraph has a named style associated with it,
     * then it also generates a class attribute for the
     * <p> tag and set's its value to be the name of the
     * style.
     *
     * @exception IOException on any I/O error
     */
    protected void writeStartParagraph(Element elem) throws IOException {
	AttributeSet attr = elem.getAttributes();
	Object resolveAttr = attr.getAttribute(StyleConstants.ResolveAttribute);
	if (resolveAttr instanceof StyleContext.NamedStyle) {
	    writeStartTag("<p class=" + ((StyleContext.NamedStyle)resolveAttr).getName() + ">");
	} else {
	    writeStartTag("<p>");
	}
    }


    /**
     * Responsible for writing out other non text leaf
     * elements.
     *
     * @exception IOException on any I/O error
     */
    protected void writeLeaf(Element elem) throws IOException {
	indent();
	if (elem.getName() == StyleConstants.IconElementName) {
	    writeImage(elem);
	} else if (elem.getName() == StyleConstants.ComponentElementName) {
	    writeComponent(elem);
	}
    }


    /**
     * Responsible for handling Icon Elements.  This method is
     * deliberatly unimplemented.  How to implement it is more
     * an issue of policy -- for example, if one was to generate
     * an <img> tag, the question does arise about how one would
     * represent the src attribute, i.e location of the image.
     * In certain cases it could be a url, in others it could
     * be read from a stream.
     *
     * @param an element fo type StyleConstants.IconElementName
     */
    protected void writeImage(Element elem) throws IOException {
    }


    /**
     * Responsible for handling Component Elements.  How this
     * method is implemented is a matter of policy.  Hence left
     * unimplemented.
     */
    protected void writeComponent(Element elem) throws IOException {
    }


    /**
     * Returns true if the element is a text element.
     *
     */
    protected boolean isText(Element elem) {
	return (elem.getName() == AbstractDocument.ContentElementName);
    }


    /**
     * This method handles writing out text. It invokes methods
     * that are responsible for writing out its attribute set
     * in a manner that is html compliant.
     *
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *            location within the document.
     */
    protected void writeContent(Element elem,  boolean needsIndenting)
	throws IOException, BadLocationException {

	AttributeSet attr = elem.getAttributes();
	writeNonHTMLAttributes(attr);
	if (needsIndenting) {
	    indent();
	}
	writeHTMLTags(attr);
	text(elem);
    }


    /**
     * This method is responsible for generating
     * bold <b>, italics <i> and <u> tags for the
     * text based on its attribute settings.
     *
     * @exception IOException on any I/O error
     */

    protected void writeHTMLTags(AttributeSet attr) throws IOException {

	int oldMask = fontMask;
	setFontMask(attr);

	int endMask = 0;
	int startMask = 0;
	if ((oldMask & BOLD) != 0) {
	    if ((fontMask & BOLD) == 0) {
		endMask |= BOLD;
	    }
	} else if ((fontMask & BOLD) != 0) {
	    startMask |= BOLD;
	}

	if ((oldMask & ITALIC) != 0) {
	    if ((fontMask & ITALIC) == 0) {
		endMask |= ITALIC;
	    }
	} else if ((fontMask & ITALIC) != 0) {
	    startMask |= ITALIC;
	}

	if ((oldMask & UNDERLINE) != 0) {
	    if ((fontMask & UNDERLINE) == 0) {
		endMask |= UNDERLINE;
	    }
	} else if ((fontMask & UNDERLINE) != 0) {
	    startMask |= UNDERLINE;
	}
	writeEndMask(endMask);
	writeStartMask(startMask);
    }


    /**
     * This method tweaks the appropriate bits of fontMask
     * to reflect whether the text is to be displayed in
     * bold, italics and/or with an underline.
     *
     */
    private void setFontMask(AttributeSet attr) {
	if (StyleConstants.isBold(attr)) {
	    fontMask |= BOLD;
	}

	if (StyleConstants.isItalic(attr)) {
	    fontMask |= ITALIC;
	}

	if (StyleConstants.isUnderline(attr)) {
	    fontMask |= UNDERLINE;
	}
    }




    /**
     * Writes out start tags <u>, <i> and <b> based on
     * the mask settings.
     *
     * @exception IOException on any I/O error
     */
    private void writeStartMask(int mask) throws IOException  {
	if (mask != 0) {
	    if ((mask & UNDERLINE) != 0) {
		write("<u>");
	    }
	    if ((mask & ITALIC) != 0) {
		write("<i>");
	    }
	    if ((mask & BOLD) != 0) {
		write("<b>");
	    }
	}
    }

    /**
     * Writes out end tags for <u>, <i> and <b> based on
     * the mask settings.
     *
     * @exception IOException on any I/O error
     */
    private void writeEndMask(int mask) throws IOException {
	if (mask != 0) {
	    if ((mask & BOLD) != 0) {
		write("</b>");
	    }
	    if ((mask & ITALIC) != 0) {
		write("</i>");
	    }
	    if ((mask & UNDERLINE) != 0) {
		write("</u>");
	    }
	}
    }


    /**
     * This method is responsible for writing out the remaining
     * character level attributes (i,e attributes other than bold
     * italics and underlie) in an html compliant way.  Given that
     * attributes like font family, font size etc.. have no direct
     * mapping to html tags, a <font> tag is generated and its
     * style attribute is set to contain the list of remaining
     * attributes just like inline styles.
     *
     * @exception IOException on any I/O error
     */
    protected void writeNonHTMLAttributes(AttributeSet attr) throws IOException {

	String style = "";
	String separator = "; ";

	if (inFontTag() && fontAttributes.isEqual(attr)) {
	    return;
	}

	Color color = (Color)attr.getAttribute(StyleConstants.Foreground);
	if (color != null) {
	    style += "color: " + css.styleConstantsValueToCSSValue
		                    ((StyleConstants)StyleConstants.Foreground,
				     color) + separator;
	}
	Integer size = (Integer)attr.getAttribute(StyleConstants.FontSize);
	if (size != null) {
	    style += "font-size: " + size.intValue() + separator;
	}

	String family = (String)attr.getAttribute(StyleConstants.FontFamily);
	if (family != null) {
	    style += "font-family: " + family + separator;
	}

	if (style.length() > 0) {
	    startFontTag(style);
	    fontAttributes = attr;
	}
    }


    /**
     * Returns true if we are currently in a <font> tag.
     */
    protected boolean inFontTag() {
	return (fontAttributes != null);
    }

    /**
     * Writes out an end tag for the <font> tag.
     * @exception IOException on any I/O error
     */
    protected void endFontTag() throws IOException {
	write(NEWLINE);
	writeEndTag("</font>");
	fontAttributes = null;
    }


    /**
     * Writes out a start tag for the <font> tag.
     * Given that font tags cannot be nested, if
     * we are already in a font tag, it closes out
     * the enclosing tag, before writing out a
     * new start tag.
     *
     * @exception IOException on any I/O error
     */
    protected void startFontTag(String style) throws IOException {
	boolean callIndent = false;
	if (inFontTag()) {
	    endFontTag();
	    callIndent = true;
	}
	writeStartTag("<font style=\"" + style + "\">");
	if (callIndent) {
	    indent();
	}
    }
}
