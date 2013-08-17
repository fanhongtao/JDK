/*
 * @(#)TextField.java	1.38 97/03/03
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

import java.awt.peer.TextFieldPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * TextField is a component that allows the editing of a single line of text.
 *
 * @version	1.38, 03/03/97
 * @author 	Sami Shaio
 */
public class TextField extends TextComponent {

    /**
     * The number of columns in the TextField.
     */
    int columns;

    /**
     * The echo character.
     */
    char echoChar;

    transient ActionListener actionListener;

    private static final String base = "textfield";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -2966288784432217853L;

    /**
     * Constructs a new TextField.
     */
    public TextField() {
	this("", 0);
    }

    /**
     * Constructs a new TextField initialized with the specified text.
     * @param text the text to be displayed
     */
    public TextField(String text) {
	this(text, text.length());
    }

    /**
     * Constructs a new empty TextField with the specified number of columns.
     * @param columns the number of columns
     */ 
    public TextField(int columns) {
	this("", columns);
    }

    /**
     * Constructs a new TextField initialized with the specified text
     * and columns.
     * @param text the text to be displayed
     * @param columns the number of columns
     */
    public TextField(String text, int columns) {
	super(text);
	this.name = base + nameCounter++;
	this.columns = columns;
    }

    /**
     * Creates the TextField's peer.  The peer allows us to modify the
     * appearance of the TextField without changing its functionality.
     */
    public void addNotify() {
	peer = getToolkit().createTextField(this);
	super.addNotify();
    }

    /**
     * Returns the character to be used for echoing.
     * @see #setEchoChar
     * @see #echoCharIsSet
     */
    public char getEchoChar() {
	return echoChar;
    }

    /**
     * Sets the echo character for this TextField. This is useful for
     * fields where the user input shouldn't be echoed to the screen,
     * as in the case of a TextField that represents a password.
     * @param c the echo character for this TextField
     * @see #echoCharIsSet
     * @see #getEchoChar
     */
    public void setEchoChar(char c) {
	setEchoCharacter(c);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setEchoChar(char).
     */
    public void setEchoCharacter(char c) {
	echoChar = c;
	TextFieldPeer peer = (TextFieldPeer)this.peer;
	if (peer != null) {
	    peer.setEchoCharacter(c);
	}
    }

    /**
     * Returns true if this TextField has a character set for
     * echoing.
     * @see #setEchoChar
     * @see #getEchoChar
     */
    public boolean echoCharIsSet() {
	return echoChar != 0;
    }

    /**
     * Returns the number of columns in this TextField.
     */
    public int getColumns() {
	return columns;
    }

    /**
     * Sets the number of columns in this TextField.
     * @param columns the number of columns
     * @exception IllegalArgumentException If columns is less than 0.
     */
    public void setColumns(int columns) {
	int oldVal = this.columns;
	if (columns < 0) {
	    throw new IllegalArgumentException("columns less than zero.");
	}
	if (columns != oldVal) {
	    this.columns = columns;
	    invalidate();
	}
    }

    /**
     * Returns the preferred size Dimensions needed for this TextField
     * with the specified amount of columns.
     * @param columns the number of columns in this TextField
     */
    public Dimension getPreferredSize(int columns) {
    	return preferredSize(columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getPreferredSize(int).
     */
    public Dimension preferredSize(int columns) {
	synchronized (Component.LOCK) {
	    TextFieldPeer peer = (TextFieldPeer)this.peer;
	    return (peer != null) ?
		       peer.preferredSize(columns) :
		       super.preferredSize();
	}
    }

    /**
     * Returns the preferred size Dimensions needed for this TextField.
     */
    public Dimension getPreferredSize() {
    	return preferredSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getPreferredSize().
     */
    public Dimension preferredSize() {
	synchronized (Component.LOCK) {
	    return (columns > 0) ?
		       preferredSize(columns) :
		       super.preferredSize();
	}
    }

    /**
     * Returns the minimum size Dimensions needed for this TextField
     * with the specified amount of columns.
     * @param columns the number of columns in this TextField
     */
    public Dimension getMinimumSize(int columns) {
    	return minimumSize(columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getMinimumSize(int).
     */
    public Dimension minimumSize(int columns) {
	synchronized (Component.LOCK) {
	    TextFieldPeer peer = (TextFieldPeer)this.peer;
	    return (peer != null) ?
		       peer.minimumSize(columns) :
		       super.minimumSize();
	}
    }

    /**
     * Returns the minimum size Dimensions needed for this TextField.
     */
    public Dimension getMinimumSize() {
    	return minimumSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getMinimumSize().
     */
    public Dimension minimumSize() {
	synchronized (Component.LOCK) {
	    return (columns > 0) ?
		       minimumSize(columns) :
		       super.minimumSize();
	}
    }

    /**
     * Adds the specified action listener to recieve 
     * action events from this textfield.
     * @param l the action listener
     */ 
    public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
        newEventsOnly = true;	
    }

    /**
     * Removes the specified action listener so that it no longer
     * receives action events from this textfield.
     * @param l the action listener 
     */ 
    public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        if (e.id == ActionEvent.ACTION_PERFORMED) {
            if ((eventMask & AWTEvent.ACTION_EVENT_MASK) != 0 ||
                actionListener != null) {
                return true;
            } 
            return false;
        }
        return super.eventEnabled(e);
    }          

    /**
     * Processes events on this textfield. If the event is an ActionEvent,
     * it invokes the processActionEvent method, else it invokes its
     * superclass's processEvent.
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ActionEvent) {
            processActionEvent((ActionEvent)e);     
            return;
        }
	super.processEvent(e);
    }

    /** 
     * Processes action events occurring on this textfield by
     * dispatching them to any registered ActionListener objects.
     * NOTE: This method will not be called unless action events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) An ActionListener object is registered via addActionListener()
     * b) Action events are enabled via enableEvents()
     * @see Component#enableEvents
     * @param e the action event
     */  
    protected void processActionEvent(ActionEvent e) {
        if (actionListener != null) {
            actionListener.actionPerformed(e);
        }
    }

    /**
     * Returns the String of parameters for this TextField.
     */
    protected String paramString() {
	String str = super.paramString();
	if (echoChar != 0) {
	    str += ",echo=" + echoChar;
	}
	return str;
    }


    /* Serialization support. 
     */

    private int textFieldSerializedDataVersion = 1;


    private void writeObject(ObjectOutputStream s)
      throws IOException 
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, actionListenerK, actionListener);
      s.writeObject(null);
    }


    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
      s.defaultReadObject();

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
	String key = ((String)keyOrNull).intern();

	if (actionListenerK == key) 
	  addActionListener((ActionListener)(s.readObject()));

	else // skip value for unrecognized key
	  s.readObject();
      }
    }

}
