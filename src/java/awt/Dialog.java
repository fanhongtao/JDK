/*
 * @(#)Dialog.java	1.35 97/03/05
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

import java.awt.peer.DialogPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A class that produces a dialog - a window that takes input from the user.
 * The default layout for a dialog is BorderLayout.
 *
 * Dialogs are capable of generating the following window events:
 * WindowOpened, WindowClosing, WindowClosed, WindowActivated, WindowDeactivated.
 * @see WindowEvent
 * @see Window#addWindowListener
 *
 * @version 	1.35, 03/05/97
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class Dialog extends Window {
    boolean	resizable = true;

    /**
     * Sets to true if the Dialog is modal.  A modal
     * Dialog grabs all the input to the parent frame from the user.
     */
    boolean modal;

    /**
     * The title of the Dialog.
     */
    String title;

    private static final String base = "dialog";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 5920926903803293709L;

    /**
     * Constructs an initially invisible Dialog with an empty title.
     * @param parent the owner of the dialog
     * @see Component#setSize
     * @see Component#setVisible
     */
    public Dialog(Frame parent) {
	this(parent, "", false);
    }

    /**
     * Constructs an initially invisible Dialog with an empty title.
     * A modal Dialog grabs all the input to the parent frame from the user.
     * @param parent the owner of the dialog
     * @param modal if true, dialog blocks input to the parent window when shown
     */
    public Dialog(Frame parent, boolean modal) {
	this(parent, "", modal);
    }

    /**
     * Constructs an initially invisible Dialog with a title. 
     * @param parent the owner of the dialog
     * @param title the title of the dialog
     * @see Component#setSize
     * @see Component#setVisible
     */
    public Dialog(Frame parent, String title) {
	this(parent, title, false);
    }

    /**
     * Constructs an initially invisible Dialog with a title. 
     * A modal Dialog grabs all the input to the parent frame from the user.
     * @param parent the owner of the dialog
     * @param title the title of the dialog
     * @param modal if true, dialog blocks input to the parent window when shown
     * @see Component#setSize
     * @see Component#setVisible
     */
    public Dialog(Frame parent, String title, boolean modal) {
	super(parent);
	if (parent == null) {
	    throw new IllegalArgumentException("null parent frame");
	}
	this.name = base + nameCounter++;
	this.title = title;
	this.modal = modal;
    }

    /**
     * Creates the frame's peer.  The peer allows us to change the appearance
     * of the frame without changing its functionality.
     */
    public void addNotify() {
	if (peer == null) {
	    peer = getToolkit().createDialog(this);
	}
	super.addNotify();
    }

    /**
     * Returns true if the Dialog is modal.  A modal
     * Dialog grabs all the input to the parent frame from the user.
     */
    public boolean isModal() {
	return modal;
    }

    /**
     * Specifies whether this Dialog is modal.  A modal
     * Dialog grabs all the input to the parent frame from the user.
     */
    public void setModal(boolean b) {
	this.modal = b;
    }

    /**
     * Gets the title of the Dialog.
     * @see #setTitle
     */
    public String getTitle() {
	return title;
    }

    /**
     * Sets the title of the Dialog.
     * @param title the new title being given to the Dialog
     * @see #getTitle
     */
    public synchronized void setTitle(String title) {
	this.title = title;
	DialogPeer peer = (DialogPeer)this.peer;
	if (peer != null) {
	    peer.setTitle(title);
	}
    }

   /**
     * Shows the dialog. This will bring the dialog to the
     * front if the dialog is already visible.  If the dialog is
     * modal, this call will block input to the parent window
     * until the dialog is taken down
     * by calling hide or dispose. It is permissible to show modal
     * dialogs from the event dispatching thread because the toolkit
     * will ensure that another dispatching thread will run while
     * the one which invoked show is blocked. 
     * @see Component#hide
     */
    public void show() {
	synchronized(Component.LOCK) {
            if (parent != null && parent.getPeer() == null) {
            	parent.addNotify();
            }
	    if (peer == null) {
		addNotify();
	    }
	    validate();	    
	}
	if (visible) {
	    toFront();
	} else {
	    synchronized(Component.LOCK) {
	        visible = true;
	    }
	    if (isModal()) {
                EventDispatchThread dt = null;
                if (Thread.currentThread() instanceof EventDispatchThread) {
	            dt = new EventDispatchThread("AWT-Dispatch-Proxy", 
                                                 Toolkit.getEventQueue());
                    dt.start();
                }
                // For modal case, we most post this event before calling
                // show, since calling peer.show() will block; this is not
                // ideal, as the window isn't yet visible on the screen...
                if ((state & OPENED) == 0) {
                    postWindowEvent(WindowEvent.WINDOW_OPENED);
                    state |= OPENED;
                }
		peer.show(); // blocks until dialog brought down
                if (dt != null) {
                    dt.stopDispatching();
                }                
	    } else {
		synchronized(Component.LOCK) {
		    peer.show();
		}
                if ((state & OPENED) == 0) {
                    postWindowEvent(WindowEvent.WINDOW_OPENED);
                    state |= OPENED;
                }
	    }
	}
    }

    /**
     * Returns true if the user can resize the dialog.
     */
    public boolean isResizable() {
	return resizable;
    }

    /**
     * Sets the resizable flag.
     * @param resizable true if resizable; false otherwise
     */
    public synchronized void setResizable(boolean resizable) {
	this.resizable = resizable;
	DialogPeer peer = (DialogPeer)this.peer;
	if (peer != null) {
	    peer.setResizable(resizable);
	}
    }

    /**
     * Returns the parameter String of this Dialog.
     */
    protected String paramString() {
	String str = super.paramString() + (modal ? ",modal" : ",modeless");
	if (title != null) {
	    str += ",title=" + title;
	}
	return str;
    }
}
