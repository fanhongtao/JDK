/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.colorchooser; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
  * A text field which takes integer values
  *
  * @version 1.10 02/06/02
  * @author Steve Wilson
  */
class JIntegerTextField extends JTextField {

    public JIntegerTextField( int min, int max, int initialValue ) {
        super( new NumericDocument(min, max), initialValue+"", String.valueOf(max).length()+1 );

	installKeyboardActions();
    }

    protected void installKeyboardActions() {
	InputMap keyMap = getInputMap(JComponent.WHEN_FOCUSED);
	ActionMap actionMap = getActionMap();
	if (keyMap != null && actionMap != null) {
	    KeyStroke upKey = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
	    KeyStroke downKey = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
	    KeyStroke numUpKey = KeyStroke.getKeyStroke("KP_UP");
	    KeyStroke numDownKey = KeyStroke.getKeyStroke("KP_DOWN");
	    keyMap.put(upKey, "incrementValue");
	    keyMap.put(downKey, "decrementValue");
	    if (upKey != numUpKey) {
		keyMap.put(numUpKey, "incrementValue");
		keyMap.put(numDownKey, "decrementValue");
	    }
	    actionMap.put("incrementValue", new ValueDelta(1));
	    actionMap.put("decrementValue", new ValueDelta(-1));
	}
    }

    public int getIntegerValue() {
        return ((NumericDocument)getDocument()).getIntegerValue();
    }

    public void setText(String s) {
        NumericDocument doc = (NumericDocument)getDocument();

	int oldValue = doc.currentVal;
	try {
	    doc.currentVal = doc.parse(s);
	} catch (Exception e) {
	    Toolkit.getDefaultToolkit().beep();
	    return;
	}

	if (oldValue != doc.currentVal) {
	    doc.checkingEnabled = false;
	    super.setText(s);
            doc.checkingEnabled = true;
	}
    }


    class ValueDelta extends AbstractAction {

      int delta;

      public ValueDelta(int delta) {
	  this.delta = delta;
      }
      
      public void actionPerformed(ActionEvent e) {
	  NumericDocument doc = (NumericDocument)getDocument();
	  int min = doc.min;
	  int max = doc.max;
	  int value = getIntegerValue();
	  value += delta;

	  if ( value < min) {
	    value = max;
	  } else if (value > max) {
	     value = min;
	  }
	  
	  setText(String.valueOf(value));
      }
    }
}

class NumericDocument extends PlainDocument {

    int min;
    int max;

    int currentVal = 0;
    boolean checkingEnabled = true;

    public NumericDocument(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getIntegerValue() {
        return currentVal;
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

        if (str == null) {
            return;
        }

	if (!checkingEnabled) {
	    super.insertString(offs, str, a);
	    return;
	}

        String proposedResult = null;

        if (getLength() == 0) {
            proposedResult = str;
        } else {
            StringBuffer currentBuffer = new StringBuffer( getText(0, getLength()) );
            currentBuffer.insert(offs, str);
            proposedResult = currentBuffer.toString();
        }
     
	try {
	    currentVal = parse(proposedResult);
	    super.insertString(offs, str, a);
	} catch (Exception e){
	    Toolkit.getDefaultToolkit().beep();
	}
      
    }

    public void remove(int offs, int len) throws BadLocationException {

	if (!checkingEnabled) {
	    super.remove(offs, len);
	    return;
	}

        String currentText = getText(0, getLength());
        String beforeOffset = currentText.substring(0, offs);
        String afterOffset = currentText.substring(len + offs, currentText.length());
        String proposedResult = beforeOffset + afterOffset;

	try {
	    currentVal = parse(proposedResult);
	    super.remove(offs, len);
	} catch (Exception e) {
	    Toolkit.getDefaultToolkit().beep();
	}

    }   

    public int parse(String proposedResult) throws NumberFormatException{

        int value = 0;
	if ( proposedResult.length() != 0) {
	    value =  Integer.parseInt(proposedResult);	    
	}
	if ( value >= min && value <= max) {
	    return value;
	} else {
	    throw new NumberFormatException();
	}
    }
}
