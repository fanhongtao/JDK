/*
 * @(#)TextComponent.java	1.29 97/04/21
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

import java.awt.peer.TextComponentPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A TextComponent is a component that allows the editing of some text.
 *
 * @version	1.29, 04/21/97
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class TextComponent extends Component {

    /**
     * The value of the text.
     */
    String text;

    /**
     * A boolean indicating whether or not this TextComponent is editable.
     */
    boolean editable = true;

    /**
     * The selection start.
     */
    int selectionStart;

    /**
     * The selection end.
     */
    int selectionEnd;

    transient protected TextListener textListener;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -2214773872412987419L;

    /**
     * Constructs a new TextComponent initialized with the specified text.
     * Sets the cursor to Cursor.TEXT_CURSOR.
     * @param text the initial text of the field.
     */
    TextComponent(String text) {
	this.text = text;
	setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

    /**
     * Removes the TextComponent's peer.  The peer allows us to modify
     * the appearance of the TextComponent without changing its
     * functionality.
     */
    public void removeNotify() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    text = peer.getText();
	    selectionStart = peer.getSelectionStart();
	    selectionEnd = peer.getSelectionEnd();
	}
	super.removeNotify();
    }

    /**
     * Sets the text of this TextComponent to the specified text.
     * @param t the new text to be set
     * @see #getText
     */
    public synchronized void setText(String t) {
	text = t;
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.setText(t);
	}
    }

    /**
     * Returns the text contained in this TextComponent.
     * @see #setText
     */
    public synchronized String getText() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    text = peer.getText();
	}
	return text;
    }

    /**
     * Returns the selected text contained in this TextComponent.
     * @see #setText
     */
    public synchronized String getSelectedText() {
	return getText().substring(getSelectionStart(), getSelectionEnd());
    }

    /**
     * Returns the boolean indicating whether this TextComponent is
     * editable or not.
     * @see #setEditable
     */
    public boolean isEditable() {
	return editable;
    }

    /**
     * Sets the specified boolean to indicate whether or not this
     * TextComponent should be editable.
     * @param b the boolean to be set
     * @see #isEditable
     */
    public synchronized void setEditable(boolean b) {
	editable = b;
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.setEditable(b);
	}
    }

    /**
     * Returns the selected text's start position.
     */
    public synchronized int getSelectionStart() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    selectionStart = peer.getSelectionStart();
	}
	return selectionStart;
    }

    /**
     * Sets the selection start to the specified position.  The new
     * starting point is constrained to be before or at the current
     * selection end.
     * @param selectionStart the start position of the text
     */
    public synchronized void setSelectionStart(int selectionStart) {
	/* Route through select method to enforce consistent policy
    	 * between selectionStart and selectionEnd.
    	 */
	select(selectionStart, getSelectionEnd());
    }

    /**
     * Returns the selected text's end position.
     */
    public synchronized int getSelectionEnd() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    selectionEnd = peer.getSelectionEnd();
	}
	return selectionEnd;
    }

    /**
     * Sets the selection end to the specified position.  The new
     * end point is constrained to be at or after the current
     * selection start.
     * @param selectionEnd the start position of the text
     */
    public synchronized void setSelectionEnd(int selectionEnd) {
	/* Route through select method to enforce consistent policy
    	 * between selectionStart and selectionEnd.
    	 */
	select(getSelectionStart(), selectionEnd);
    }
    
    /**
     * Selects the text found between the specified start and end locations.
     * @param selectionStart the start position of the text
     * @param selectionEnd the end position of the text
     */
    public synchronized void select(int selectionStart, int selectionEnd) {
	String text = getText();
	if (selectionStart < 0) {
	    selectionStart = 0;
	}
	if (selectionEnd > text.length()) {
	    selectionEnd = text.length();
	}
	if (selectionEnd < selectionStart) {
	    selectionEnd = selectionStart;
	}
	if (selectionStart > selectionEnd) {
	    selectionStart = selectionEnd;
	}

	this.selectionStart = selectionStart;
	this.selectionEnd = selectionEnd;

	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.select(selectionStart, selectionEnd);
	}
    }

    /**
     * Selects all the text in the TextComponent.
     */
    public synchronized void selectAll() {
	String text = getText();
	this.selectionStart = 0;
	this.selectionEnd = getText().length();

	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.select(selectionStart, selectionEnd);
	}
    }

    /**
     * Sets the position of the text insertion caret for the TextComponent
     * @param position the position
     * @exception IllegalArgumentException If position is less than 0.
     */
    public void setCaretPosition(int position) {
	if (position < 0) {
	    throw new IllegalArgumentException("position less than zero.");
	}

	int maxposition = getText().length();
	if (position > maxposition) {
	    position = maxposition;
	}

	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.setCaretPosition(position);
	} else {
	    throw new IllegalComponentStateException("Cannot set caret position until after the peer has been created");
	}
    }

    /**
     * Returns the position of the text insertion caret for the 
     * text component.
     * @return the position of the text insertion caret for the text component.
     */
    public int getCaretPosition() {
        TextComponentPeer peer = (TextComponentPeer)this.peer;
	int position = 0;

	if (peer != null) {
	    position = peer.getCaretPosition();
	} 
	return position;
    }

    /**
     * Adds the specified text event listener to recieve text events 
     * from this textcomponent.
     * @param l the text event listener
     */ 
    public synchronized void addTextListener(TextListener l) {
	textListener = AWTEventMulticaster.add(textListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified text event listener so that it no longer
     * receives text events from this textcomponent
     */ 
    public void removeTextListener(TextListener l) {
	textListener = AWTEventMulticaster.remove(textListener, l);
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        if (e.id == TextEvent.TEXT_VALUE_CHANGED) {
            if ((eventMask & AWTEvent.TEXT_EVENT_MASK) != 0 ||
                textListener != null) {
                return true;
            } 
            return false;
        }
        return super.eventEnabled(e);
    }     

    /**
     * Processes events on this textcomponent. If the event is a
     * TextEvent, it invokes the processTextEvent method,
     * else it invokes its superclass's processEvent.
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof TextEvent) {
            processTextEvent((TextEvent)e);
            return;
        }
	super.processEvent(e);
    }

    /** 
     * Processes text events occurring on this text component by
     * dispatching them to any registered TextListener objects.
     * NOTE: This method will not be called unless text events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A TextListener object is registered via addTextListener()
     * b) Text events are enabled via enableEvents()
     * @see Component#enableEvents
     * @param e the text event
     */ 
    protected void processTextEvent(TextEvent e) {
        if (textListener != null) {
            int id = e.getID();
	    switch (id) {
	    case TextEvent.TEXT_VALUE_CHANGED:
		textListener.textValueChanged(e);
		break;
	    }
        }
    }

    /**
     * Returns the String of parameters for this TextComponent.
     */
    protected String paramString() {
	String str = super.paramString() + ",text=" + getText();
	if (editable) {
	    str += ",editable";
	}
	return str + ",selection=" + getSelectionStart() + "-" + getSelectionEnd();
    }


    /* 
     * Serialization support.  Since the value of the fields
     * selectionStart, and selectionEnd, and text aren't neccessarily
     * up to date we sync them up with the peer before serializing.
     */

    private int textComponentSerializedDataVersion = 1;


    private void writeObject(java.io.ObjectOutputStream s)
      throws java.io.IOException 
    {
      TextComponentPeer peer = (TextComponentPeer)this.peer;
      if (peer != null) {
	text = peer.getText();
	selectionStart = peer.getSelectionStart();
	selectionEnd = peer.getSelectionEnd();
      }
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, textListenerK, textListener);
      s.writeObject(null);
    }


    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
      s.defaultReadObject();

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
	String key = ((String)keyOrNull).intern();

	if (textListenerK == key) 
	  addTextListener((TextListener)(s.readObject()));

	else // skip value for unrecognized key
	  s.readObject();
      }
    }
}
