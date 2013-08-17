/*
 * @(#)Button.java	1.37 97/03/13
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

import java.awt.peer.ButtonPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A class that produces a labeled button component.
 *
 * @version 	1.37 03/13/97
 * @author 	Sami Shaio
 */
public class Button extends Component {

    String label;
    String actionCommand;

    transient ActionListener actionListener;
    
    private static final String base = "button";
    private static int nameCounter = 0;
    
    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -8774683716313001058L;

    /**
     * Constructs a Button with no label.
     */
    public Button() {
	this("");
    }

    /**
     * Constructs a Button with the specified label.
     * @param label the label of the button
     */
    public Button(String label) {
	this.name = base + nameCounter++;
	this.label = label;
    }
    
    /**
     * Creates the peer of the button.  This peer allows us to
     * change the look of the button without changing its functionality.
     */
    public void addNotify() {
	peer = getToolkit().createButton(this);
	super.addNotify();
    }

    /**
     * Gets the label of the button.
     * @see #setLabel
     */
    public String getLabel() {
	return label;
    }

    /**
     * Sets the button with the specified label.
     * @param label the label to set the button with
     * @see #getLabel
     */
    public synchronized void setLabel(String label) {
	this.label = label;
	ButtonPeer peer = (ButtonPeer)this.peer;
	if (peer != null) {
	    peer.setLabel(label);
	}
    }

    /**
     * Sets the command name of the action event fired by this button.
     * By default this will be set to the label of the button.
     */
    public void setActionCommand(String command) {
        actionCommand = command;
    }

    /**
     * Returns the command name of the action event fired by this button.
     */
    public String getActionCommand() {
        return (actionCommand == null? label : actionCommand);
    }

    /**
     * Adds the specified action listener to receive action events
     * from this button.
     * @param l the action listener
     */ 
    public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
        newEventsOnly = true;	
    }

    /**
     * Removes the specified action listener so it no longer receives
     * action events from this button.
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
     * Processes events on this button. If the event is an ActionEvent,
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
     * Processes action events occurring on this button by
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
     * Returns the parameter String of this button.
     */
    protected String paramString() {
	return super.paramString() + ",label=" + label;
    }


    /* Serialization support. 
     */

    private int buttonSerializedDataVersion = 1;


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
