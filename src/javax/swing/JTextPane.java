/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.awt.event.ActionEvent;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.plaf.*;

/**
 * A text component that can be marked up with attributes that are
 * represented graphically. 
 * You can find how-to information and examples of using text panes in
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/text.html">Using Text Components</a>,
 * a section in <em>The Java Tutorial.</em>
 *
 * <p>
 * This component models paragraphs
 * that are composed of runs of character level attributes.  Each
 * paragraph may have a logical style attached to it which contains
 * the default attributes to use if no overriden by attributes set
 * on the paragraph or character run.  Components and images may
 * be embedded in the flow of text.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JTextPane">JTextPane</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *   attribute: isContainer true
 * description: A text component that can be marked up with attributes that are graphically represented.
 *
 * @author  Timothy Prinzing
 * @version 1.73 02/06/02
 * @see javax.swing.text.StyledEditorKit
 */
public class JTextPane extends JEditorPane {

    /**
     * Constructs a new JTextPane.  A new instance of StyledEditorKit is
     * created and set, and the document model set to null.
     */
    public JTextPane() {
        super();
        setEditorKit(new StyledEditorKit());
    }

    /**
     * Constructs a new JTextPane, with a specified document model.
     * A new instance of javax.swing.text.StyledEditorKit is created and set.
     *
     * @param doc the document model
     */
    public JTextPane(StyledDocument doc) {
        this();
        setStyledDocument(doc);
    }

    /**
     * Returns the class ID for the UI.
     *
     * @return the ID ("TextPaneUI")
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Associates the editor with a text document.  This
     * must be a StyledDocument.
     *
     * @param doc  the document to display/edit
     * @exception IllegalArgumentException  if doc can't
     *   be narrowed to a StyledDocument which is the
     *   required type of model for this text component
     */
    public void setDocument(Document doc) {
        if (doc instanceof StyledDocument) {
            super.setDocument(doc);
        } else {
            throw new IllegalArgumentException("Model must be StyledDocument");
        }
    }

    /**
     * Associates the editor with a text document.
     * The currently registered factory is used to build a view for
     * the document, which gets displayed by the editor.
     *
     * @param doc  the document to display/edit
     */
    public void setStyledDocument(StyledDocument doc) {
        super.setDocument(doc);
    }

    /**
     * Fetches the model associated with the editor.  
     *
     * @return the model
     */
    public StyledDocument getStyledDocument() {
        return (StyledDocument) getDocument();
    } 

    /**
     * Replaces the currently selected content with new content
     * represented by the given string.  If there is no selection
     * this amounts to an insert of the given text.  If there
     * is no replacement text this amounts to a removal of the
     * current selection.  The replacement text will have the
     * attributes currently defined for input.  If the document is not
     * editable, beep and return.  Then if the document is null, do nothing.
     * If the content to insert is null or empty, ignore it.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.     
     *
     * @param content  the content to replace the selection with
     */
    public void replaceSelection(String content) {
        if (! isEditable()) {
            getToolkit().beep();
            return;
        }
        Document doc = getStyledDocument();
        if (doc != null) {
            try {
                Caret caret = getCaret();
                int p0 = Math.min(caret.getDot(), caret.getMark());
                int p1 = Math.max(caret.getDot(), caret.getMark());
                if (p0 != p1) {
                    doc.remove(p0, p1 - p0);
                }
                if (content != null && content.length() > 0) {
                    doc.insertString(p0, content, getInputAttributes());
                }
            } catch (BadLocationException e) {
                getToolkit().beep();
            }
        }
    }

    /**
     * Inserts a component into the document as a replacement
     * for the currently selected content.  If there is no
     * selection the component is effectively inserted at the 
     * current position of the caret.  This is represented in
     * the associated document as an attribute of one character 
     * of content.  
     *
     * @param c    the component to insert
     */
    public void insertComponent(Component c) {
        MutableAttributeSet inputAttributes = getInputAttributes();
        inputAttributes.removeAttributes(inputAttributes);
        StyleConstants.setComponent(inputAttributes, c);
        replaceSelection(" ");
        inputAttributes.removeAttributes(inputAttributes);
    }

    /**
     * Inserts an icon into the document as a replacement
     * for the currently selected content.  If there is no
     * selection the icon is effectively inserted at the 
     * current position of the caret.  This is represented in
     * the associated document as an attribute of one character 
     * of content.  
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.     
     *
     * @param g    the icon to insert
     * @see Icon
     */
    public void insertIcon(Icon g) {
        MutableAttributeSet inputAttributes = getInputAttributes();
        inputAttributes.removeAttributes(inputAttributes);
        StyleConstants.setIcon(inputAttributes, g);
        replaceSelection(" ");
        inputAttributes.removeAttributes(inputAttributes);
    }

    /**
     * Adds a new style into the logical style hierarchy.  Style attributes
     * resolve from bottom up so an attribute specified in a child
     * will override an attribute specified in the parent.
     *
     * @param nm   the name of the style (must be unique within the
     *   collection of named styles).  The name may be null if the style 
     *   is unnamed, but the caller is responsible
     *   for managing the reference returned as an unnamed style can't
     *   be fetched by name.  An unnamed style may be useful for things
     *   like character attribute overrides such as found in a style 
     *   run.
     * @param parent the parent style.  This may be null if unspecified
     *   attributes need not be resolved in some other style.
     * @return the new Style 
     */
    public Style addStyle(String nm, Style parent) {
        StyledDocument doc = getStyledDocument();
        return doc.addStyle(nm, parent);
    }

    /**
     * Removes a named non-null style previously added to the document.  
     *
     * @param nm  the name of the style to remove
     */
    public void removeStyle(String nm) {
        StyledDocument doc = getStyledDocument();
        doc.removeStyle(nm);
    }

    /**
     * Fetches a named non-null style previously added.
     *
     * @param nm  the name of the style
     * @return the style
     */
    public Style getStyle(String nm) {
        StyledDocument doc = getStyledDocument();
        return doc.getStyle(nm);
    }

    /**
     * Sets the logical style to use for the paragraph at the
     * current caret position.  If attributes aren't explicitly set 
     * for character and paragraph attributes they will resolve 
     * through the logical style assigned to the paragraph, which
     * in term may resolve through some hierarchy completely 
     * independant of the element hierarchy in the document.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.     
     *
     * @param s  the logical style to assign to the paragraph, or null for
     *  no style
     */
    public void setLogicalStyle(Style s) {
        StyledDocument doc = getStyledDocument();
        doc.setLogicalStyle(getCaretPosition(), s);
    }

    /** 
     * Fetches the logical style assigned to the paragraph 
     * represented by the current position of the caret, or null.
     *
     * @return the style
     */
    public Style getLogicalStyle() {
        StyledDocument doc = getStyledDocument();
        return doc.getLogicalStyle(getCaretPosition());
    }

    /**
     * Fetches the character attributes in effect at the 
     * current location of the caret, or null.  
     *
     * @return the attributes, or null
     */
    public AttributeSet getCharacterAttributes() {
        StyledDocument doc = getStyledDocument();
        Element run = doc.getCharacterElement(getCaretPosition());
        if (run != null) {
            return run.getAttributes();
        }
        return null;
    }

    /**
     * Applies the given attributes to character 
     * content.  If there is a selection, the attributes
     * are applied to the selection range.  If there
     * is no selection, the attributes are applied to
     * the input attribute set which defines the attributes
     * for any new text that gets inserted.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.     
     *
     * @param attr the attributes
     * @param replace if true, then replace the existing attributes first
     */
    public void setCharacterAttributes(AttributeSet attr, boolean replace) {
        int p0 = getSelectionStart();
        int p1 = getSelectionEnd();
        if (p0 != p1) {
            StyledDocument doc = getStyledDocument();
            doc.setCharacterAttributes(p0, p1 - p0, attr, replace);
        } else {
            MutableAttributeSet inputAttributes = getInputAttributes();
            if (replace) {
                inputAttributes.removeAttributes(inputAttributes);
            }
            inputAttributes.addAttributes(attr);
        }
    }

    /**
     * Fetches the current paragraph attributes in effect
     * at the location of the caret, or null if none.
     *
     * @return the attributes
     */
    public AttributeSet getParagraphAttributes() {
        StyledDocument doc = getStyledDocument();
        Element paragraph = doc.getParagraphElement(getCaretPosition());
        if (paragraph != null) {
            return paragraph.getAttributes();
        }
        return null;
    }

    /**
     * Applies the given attributes to paragraphs.  If
     * there is a selection, the attributes are applied
     * to the paragraphs that intersect the selection.
     * if there is no selection, the attributes are applied
     * to the paragraph at the current caret position.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">Threads
     * and Swing</A> for more information.     
     *
     * @param attr the non-null attributes
     * @param replace if true, replace the existing attributes first
     */
    public void setParagraphAttributes(AttributeSet attr, boolean replace) {
        int p0 = getSelectionStart();
        int p1 = getSelectionEnd();
        StyledDocument doc = getStyledDocument();
        doc.setParagraphAttributes(p0, p1 - p0, attr, replace);
    }

    /**
     * Gets the input attributes for the pane.
     *
     * @return the attributes
     */
    public MutableAttributeSet getInputAttributes() {
        return getStyledEditorKit().getInputAttributes();
    }

    /**
     * Gets the editor kit.
     *
     * @return the editor kit.
     */
    protected final StyledEditorKit getStyledEditorKit() {
        return (StyledEditorKit) getEditorKit();
    }

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "TextPaneUI";


    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    // --- JEditorPane ------------------------------------

    /**
     * Creates the EditorKit to use by default.  This
     * is implemented to return javax.swing.text.StyledEditorKit.
     *
     * @return the editor kit
     */
    protected EditorKit createDefaultEditorKit() {
        return new StyledEditorKit();
    }

    /**
     * Sets the currently installed kit for handling
     * content.  This is the bound property that
     * establishes the content type of the editor.
     * 
     * @param kit the desired editor behavior.
     * @exception IllegalArgumentException if kit is not a StyledEditorKit
     */
    public final void setEditorKit(EditorKit kit) {
        if (kit instanceof StyledEditorKit) {
            super.setEditorKit(kit);
        } else {
            throw new IllegalArgumentException("Must be StyledEditorKit");
        }
    }

    /**
     * Returns a string representation of this JTextPane. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JTextPane.
     */
    protected String paramString() {
        return super.paramString();
    }

}
