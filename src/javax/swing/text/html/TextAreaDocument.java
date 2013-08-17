/*
 * @(#)TextAreaDocument.java	1.3 98/08/26
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

import javax.swing.text.*;


/**
 * TextAreaDocument extends the capabilities of the PlainDocument 
 * to store the data that is initially set in the Document.
 * This is stored in order to enable an accurate reset of the 
 * state when a reset is requested.
 *
 * @author Sunita Mani
 * @version 1.3 08/26/98
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




