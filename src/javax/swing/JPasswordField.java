/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * JPasswordField is a lightweight component that allows the editing 
 * of a single line of text where the view indicates something was
 * typed, but does not show the original characters. 
 * You can find further information and examples in
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/textfield.html">How to Use Text Fields</a>,
 * a section in <em>The Java Tutorial.</em>
 * <p>
 * JPasswordField is intended 
 * to be source-compatible with java.awt.TextField used with echoChar 
 * set.  It is provided seperately to make it easier to safely change 
 * the ui for the JTextField without affecting password entries.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JPasswordField">JPasswordField</a>
 * key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *  attribute: isContainer false
 * description: Allows the editing of a line of text but doesn't show the characters.
 *
 * @author  Timothy Prinzing
 * @version 1.43 02/06/02
 */
public class JPasswordField extends JTextField {

    /**
     * Constructs a new JPasswordField, with a default document, null starting
     * text string, and 0 column width.
     */
    public JPasswordField() {
        this(null,null,0);
    }

    /**
     * Constructs a new JPasswordField initialized with the specified text.
     * The document model is set to the default, and the number of columns to 0.
     *
     * @param text the text to be displayed, null if none
     */
    public JPasswordField(String text) {
        this(null, text, 0);
    }

    /**
     * Constructs a new empty JPasswordField with the specified
     * number of columns.  A default model is created, and the initial string
     * is set to null.
     *
     * @param columns the number of columns >= 0
     */ 
    public JPasswordField(int columns) {
        this(null, null, columns);
    }

    /**
     * Constructs a new JPasswordField initialized with the specified text
     * and columns.  The document model is set to the default.
     *
     * @param text the text to be displayed, null if none
     * @param columns the number of columns >= 0
     */
    public JPasswordField(String text, int columns) {
        this(null, text, columns);
    }

    /**
     * Constructs a new JPasswordField that uses the given text storage
     * model and the given number of columns.  This is the constructor
     * through which the other constructors feed.  The echo character is
     * set to '*'.  If the document model is null, a default one will be
     * created.
     *
     * @param doc  the text storage to use
     * @param txt the text to be displayed, null if none
     * @param columns  the number of columns to use to calculate 
     *   the preferred width >= 0.  If columns is set to zero, the
     *   preferred width will be whatever naturally results from
     *   the component implementation.
     */
    public JPasswordField(Document doc, String txt, int columns) {
        super(doc, txt, columns);
        echoChar = '*';
    }

    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "PasswordFieldUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Returns the character to be used for echoing.  The default is '*'.
     *
     * @return the echo character, 0 if unset
     * @see #setEchoChar
     * @see #echoCharIsSet
     */
    public char getEchoChar() {
        return echoChar;
    }

    /**
     * Sets the echo character for this JPasswordField.  Note 
     * that this is largely a suggestion to the view as the
     * view that gets installed can use whatever graphic techniques
     * it desires to represent the field.  Setting a value of 0 unsets
     * the echo character.
     *
     * @param c the echo character to display
     * @see #echoCharIsSet
     * @see #getEchoChar
     * @beaninfo
     * description: character to display in place of the real characters
     */
    public void setEchoChar(char c) {
        echoChar = c;
    }

    /**
     * Returns true if this JPasswordField has a character set for
     * echoing.  A character is considered to be set if the echo character
     * is not 0.
     *
     * @return true if a character is set for echoing
     * @see #setEchoChar
     * @see #getEchoChar
     */
    public boolean echoCharIsSet() {
        return echoChar != 0;
    }

    // --- JTextComponent methods ----------------------------------

    /**
     * Normally transfers the currently selected range in the associated
     * text model to the system clipboard, removing the contents
     * from the model.  This is not a good thing for a password field
     * and is reimplemented to simply beep.
     */
    public void cut() {
	getToolkit().beep();
    }

    /**
     * Normally transfers the currently selected range in the associated
     * text model to the system clipboard, leaving the contents
     * in the text model.  This is not a good thing for a password field
     * and is reimplemented to simply beep.
     */
    public void copy() {
	getToolkit().beep();
    }

    /**
     * Returns the text contained in this TextComponent.  If the underlying
     * document is null, will give a NullPointerException.  
     * <p>
     * For security reasons, this method is deprecated.  Use the
     * getPassword method instead.
     * @deprecated As of Java 2 platform v1.2,
     * replaced by <code>getPassword()</code>.
     * @return the text
     */
    public String getText() {
	return super.getText();
    }

    /**
     * Fetches a portion of the text represented by the
     * component.  Returns an empty string if length is 0.
     * <p>
     * For security reasons, this method is deprecated.  Use the
     * getPassword method instead.
     * @deprecated As of Java 2 platform v1.2,
     * replaced by <code>getPassword()</code>.
     * @param offs the offset >= 0
     * @param len the length >= 0
     * @return the text
     * @exception BadLocationException if the offset or length are invalid
     */
    public String getText(int offs, int len) throws BadLocationException {
        return super.getText(offs, len);
    }

    /**
     * Returns the text contained in this TextComponent.  If the underlying
     * document is null, will give a NullPointerException.  For stronger
     * security, it is recommended that the returned character array be
     * cleared after use by setting each character to zero.
     *
     * @return the text
     */
    public char[] getPassword() {
        Document doc = getDocument();
	Segment txt = new Segment();
        try {
            doc.getText(0, doc.getLength(), txt); // use the non-String API
        } catch (BadLocationException e) {
	    return null;
        }
	char[] retValue = new char[txt.count];
	System.arraycopy(txt.array, txt.offset, retValue, 0, txt.count);
        return retValue;
    }

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

    // --- variables -----------------------------------------------

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "PasswordFieldUI";

    private char echoChar;


    /**
     * Returns a string representation of this JPasswordField. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JPasswordField.
     */
    protected String paramString() {
	return super.paramString() +
	",echoChar=" + echoChar;
    }

/////////////////
// Accessibility support
////////////////


    /**
     * Gets the AccessibleContext associated with this JPasswordField. 
     * For password fields, the AccessibleContext takes the form of an 
     * AccessibleJPasswordField. 
     * A new AccessibleJPasswordField instance is created if necessary.
     *
     * @return an AccessibleJPasswordField that serves as the 
     *         AccessibleContext of this JPasswordField
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJPasswordField();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JPasswordField</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to password field user-interface 
     * elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJPasswordField extends AccessibleJTextField {

        /**
         * Gets the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         *   object (AccessibleRole.PASSWORD_TEXT)
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PASSWORD_TEXT;
        }
    }
}
