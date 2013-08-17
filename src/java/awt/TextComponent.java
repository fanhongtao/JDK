/*
 * @(#)TextComponent.java	1.35 98/12/04
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package java.awt;

import java.awt.peer.TextComponentPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import sun.awt.SunToolkit;


/**
 * The <code>TextComponent</code> class is the superclass of 
 * any component that allows the editing of some text. 
 * <p>
 * A text component embodies a string of text.  The 
 * <code>TextComponent</code> class defines a set of methods 
 * that determine whether or not this text is editable. If the
 * component is editable, it defines another set of methods
 * that supports a text insertion caret. 
 * <p>
 * In addition, the class defines methods that are used 
 * to maintain a current <em>selection</em> from the text. 
 * The text selection, a substring of the component's text, 
 * is the target of editing operations. It is also referred
 * to as the <em>selected text</em>.
 *
 * @version	1.35, 12/04/98
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @since       JDK1.0
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
     * Constructs a new text component initialized with the 
     * specified text. Sets the value of the cursor to 
     * <code>Cursor.TEXT_CURSOR</code>.
     * The insertion caret is initially placed before the first 
     * character and the text is left justified.
     * @param      text the initial text that the component presents.
     * @see        java.awt.Cursor
     * @since      JDK1.0
     */
    TextComponent(String text) {
	this.text = text;
	setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

    boolean areInputMethodsEnabled() {
	try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            return ((SunToolkit) toolkit).enableInputMethodsForTextComponent();
        } catch (Exception e) {
            // if something bad happens, just don't enable input methods
            return false;
	}
    }

    /**
     * Removes the TextComponent's peer.  The peer allows us to modify
     * the appearance of the TextComponent without changing its
     * functionality.
     */
    public void removeNotify() {
      synchronized(getTreeLock()) {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    text = peer.getText();
	    selectionStart = peer.getSelectionStart();
	    selectionEnd = peer.getSelectionEnd();
	}
	super.removeNotify();
      }
    }

    /**
     * Sets the text that is presented by this 
     * text component to be the specified text. 
     * @param       t   the new text.
     * @see         java.awt.TextComponent#getText  
     * @since       JDK1.0
     */
    public synchronized void setText(String t) {
	text = t;
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.setText(t);
	}
    }

    /**
     * Gets the text that is presented by this text component.
     * @see     java.awt.TextComponent#setText
     * @since   JDK1.0
     */
    public synchronized String getText() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    text = peer.getText();
	}
	return text;
    }

    /**
     * Gets the selected text from the text that is
     * presented by this text component.  
     * @return      the selected text of this text component.
     * @see         java.awt.TextComponent#select
     * @since       JDK1.0
     */
    public synchronized String getSelectedText() {
	return getText().substring(getSelectionStart(), getSelectionEnd());
    }

    /**
     * Indicates whether or not this text component is editable.
     * @return     <code>true</code> if this text component is
     *                  editable; <code>false</code> otherwise.
     * @see        java.awt.TextComponent#setEditable
     * @since      JDK1ble
     */
    public boolean isEditable() {
	return editable;
    }

    /**
     * Sets the flag that determines whether or not this
     * text component is editable.
     * <p>
     * If the flag is set to <code>true</code>, this text component 
     * becomes user editable. If the flag is set to <code>false</code>, 
     * the user cannot change the text of this text component. 
     * @param     t   a flag indicating whether this text component 
     *                      should be user editable.
     * @see       java.awt.TextComponent#isEditable
     * @since     JDK1.0
     */
    public synchronized void setEditable(boolean b) {
	editable = b;
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    peer.setEditable(b);
	}
    }

    /**
     * Gets the start position of the selected text in 
     * this text component. 
     * @return      the start position of the selected text. 
     * @see         java.awt.TextComponent#setSelectionStart
     * @see         java.awt.TextComponent#getSelectionEnd
     * @since       JDK1.0
     */
    public synchronized int getSelectionStart() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    selectionStart = peer.getSelectionStart();
	}
	return selectionStart;
    }

    /**
     * Sets the selection start for this text component to  
     * the specified position. The new start point is constrained 
     * to be at or before the current selection end. It also
     * cannot be set to less than zero, the beginning of the 
     * component's text.
     * If the caller supplies a value for <code>selectionStart</code>
     * that is out of bounds, the method enforces these constraints
     * silently, and without failure.
     * @param       selectionStart   the start position of the 
     *                        selected text.
     * @see         java.awt.TextComponent#getSelectionStart
     * @see         java.awt.TextComponent#setSelectionEnd
     * @since       JDK1.1
     */
    public synchronized void setSelectionStart(int selectionStart) {
	/* Route through select method to enforce consistent policy
    	 * between selectionStart and selectionEnd.
    	 */
	select(selectionStart, getSelectionEnd());
    }

    /**
     * Gets the end position of the selected text in 
     * this text component. 
     * @return      the end position of the selected text. 
     * @see         java.awt.TextComponent#setSelectionEnd
     * @see         java.awt.TextComponent#getSelectionStart
     * @since       JDK1.0
     */
    public synchronized int getSelectionEnd() {
	TextComponentPeer peer = (TextComponentPeer)this.peer;
	if (peer != null) {
	    selectionEnd = peer.getSelectionEnd();
	}
	return selectionEnd;
    }

    /**
     * Sets the selection end for this text component to  
     * the specified position. The new end point is constrained 
     * to be at or after the current selection start. It also
     * cannot be set beyond the end of the component's text.
     * If the caller supplies a value for <code>selectionEnd</code>
     * that is out of bounds, the method enforces these constraints
     * silently, and without failure.
     * @param       selectionEnd   the end position of the 
     *                        selected text.
     * @see         java.awt.TextComponent#getSelectionEnd
     * @see         java.awt.TextComponent#setSelectionStart
     * @since       JDK1.1
     */
    public synchronized void setSelectionEnd(int selectionEnd) {
	/* Route through select method to enforce consistent policy
    	 * between selectionStart and selectionEnd.
    	 */
	select(getSelectionStart(), selectionEnd);
    }
    
    /**
     * Selects the text between the specified start and end positions.
     * <p>
     * This method sets the start and end positions of the 
     * selected text, enforcing the restriction that the end 
     * position must be greater than or equal to the start position.
     * The start position must be greater than zero, and the 
     * end position must be less that or equal to the length
     * of the text component's text. If the caller supplies values
     * that are inconsistent or out of bounds, the method enforces 
     * these constraints silently, and without failure.
     * @param        selectionStart the start position of the 
     *                             text to select.
     * @param        selectionEnd the end position of the 
     *                             text to select.
     * @see          java.awt.TextComponent#setSelectionStart
     * @see          java.awt.TextComponent#setSelectionEnd
     * @see          java.awt.TextComponent#selectAll
     * @since        JDK1.0
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
     * Selects all the text in this text component.
     * @see        java.awt.TextComponent@select
     * @since      JDK1.0
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
     * Sets the position of the text insertion caret for 
     * this text component.
     * 
     * @param        position the position of the text insertion caret.
     * @exception    IllegalArgumentException if the value supplied
     *                   for <code>position</code> is less than zero.
     * @since        JDK1.1
     */
    public synchronized void setCaretPosition(int position) {
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
     * Gets the position of the text insertion caret for 
     * this text component.
     * @return       the position of the text insertion caret.
     * @since        JDK1.1
     */
    public synchronized int getCaretPosition() {
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
    public synchronized void removeTextListener(TextListener l) {
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
     * Returns the parameter string representing the state of this text 
     * component. This string is useful for debugging. 
     * @return      the parameter string of this text component.
     * @since       JDK1.0
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
