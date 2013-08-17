/*
 * @(#)TextAreaDocument.java	1.5 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * @version 1.5 02/02/00
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




