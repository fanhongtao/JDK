/*
 * @(#)TextAreaDocument.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text.html;

import javax.swing.text.*;


/**
 * TextAreaDocument extends the capabilities of the PlainDocument 
 * to store the data that is initially set in the Document.
 * This is stored in order to enable an accurate reset of the 
 * state when a reset is requested.
 *
 * @author Sunita Mani
 * @version 1.8 12/19/03
 */
  
class TextAreaDocument extends PlainDocument {

    String initialText;
  

    /**
     * Resets the model by removing all the data,
     * and restoring it to its initial state.
     */
    void reset() {
	try {
	    remove(0, getLength());
	    if (initialText != null) {
		insertString(0, initialText, null);
	    }
	} catch (BadLocationException e) {
	}
    }

    /**
     * Stores the data that the model is initially
     * loaded with.
     */
    void storeInitialText() {
	try {
	    initialText = getText(0, getLength());
	} catch (BadLocationException e) {
	}
    }
}




