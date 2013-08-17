/*
 * @(#)Dialog.java	1.44 00/02/10
 *
 * Copyright 1995-1999 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 * 
 */
package java.awt;

import java.awt.peer.DialogPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import sun.awt.AxBridgeHelper;

/**
 * A class that produces a dialog - a window that takes input from the user.
 * The default layout for a dialog is BorderLayout.
 * <p>
 * Dialogs are capable of generating the following window events:
 * WindowOpened, WindowClosing, WindowClosed, WindowActivated, WindowDeactivated.
 * @see WindowEvent
 * @see Window#addWindowListener
 *
 * @version 	1.38, 12/03/97
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @since       JDK1.0
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
     * @since JDK1.0
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
     * @since JDK1.0
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
     * @since JDK1.0
     */
    public Dialog(Frame parent, String title, boolean modal) {
	super(parent);
	if (parent == null) {
	    throw new IllegalArgumentException("null parent frame");
	}
	this.title = title;
	this.modal = modal;
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        return base + nameCounter++;
    }

    /**
     * Creates the dialog's peer.  The peer allows us to change the appearance
     * of the frame without changing its functionality.
     * @since JDK1.0
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
            if (peer == null) {
                peer = getToolkit().createDialog(this);
            }
            super.addNotify();
        }
    }

    /**
     * Indicates whether the dialog is modal.  
     * A modal dialog grabs all input from the user.
     * @return    <code>true</code> if this dialog window is modal; 
     *            <code>false</code> otherwise. 
     * @see       java.awt.Dialog#setModal    
     * @since     JDK1.0
     */
    public boolean isModal() {
	return modal;
    }

    /**
     * Specifies whether this dialog is modal.  A modal
     * Dialog grabs all the input to the parent frame from the user.
     * @see       java.awt.Dialog#isModal 
     * @since     JDK1.1
     */
    public void setModal(boolean b) {
	this.modal = b;
    }

    /**
     * Gets the title of the dialog.
     * @return    the title of this dialog window.
     * @see       java.awt.Dialog#setTitle
     * @since     JDK1.0
     */
    public String getTitle() {
	return title;
    }

    /**
     * Sets the title of the Dialog.
     * @param title the new title being given to the dialog
     * @see #getTitle
     * @since JDK1.0
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
     * @since JDK1.0
     */
    public void show() {
      synchronized(getTreeLock()) {
        if (parent != null && parent.getPeer() == null) {
            parent.addNotify();
        }
	if (peer == null) {
	    addNotify();
	}
      }
	validate();	    
	if (visible) {
	    toFront();
	} else {
	    visible = true;
	    if (isModal()) {
                EventDispatchThread dt = null;
                EventDispatchThread tempEDT = null;
                if (Thread.currentThread() instanceof EventDispatchThread) {
	            dt = new EventDispatchThread("AWT-Dispatch-Proxy", 
                                                 Toolkit.getEventQueue());
                    dt.start();
                } else if (isNewDispatchThreadNeeded()) {
                    /*
                     * 4192193:
                     * this is because our dispatch thread might be blocked in
                     * a call into ActiveX container, and the ActiveX container
                     * then calls into Java again to create a modal dialog.
                     * Our dispatch thread will not wake up until the ActiveX call
                     * is returned. so we need to create a new temporary dispatch
                     * thread here.
                     * also the inside peer.show() the win32 code will
                     * open up a new message loop for the ActiveX container so that
                     * any win32 message that was sent to the ActiveX container during
                     * the modal dialog creation can still be handled.
                     */ 
                    tempEDT = new EventDispatchThread("AWT-Dispatch-Proxy",
                                   Toolkit.getEventQueue());
                    tempEDT.start();
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
                if (tempEDT != null) {
                    /*
                     * 4192193:
                     * we can't call join here, because we have been out of peer.show()
                     * now there is no message loop for the ActiveX container running.
                     * the other dispatch thread might send a message to the ActiveX
                     * container. if we do join here we might deadlock.
                     */
                    tempEDT.stopDispatchingNoJoin();
                }
	    } else {
		peer.show();
                if ((state & OPENED) == 0) {
                    postWindowEvent(WindowEvent.WINDOW_OPENED);
                    state |= OPENED;
                }
	    }
	}
    }

    private boolean isNewDispatchThreadNeeded() {
       Class peerClass = peer.getClass();
       if (!peerClass.getName().equals("sun.awt.windows.WDialogPeer"))
           return false; 
       return sun.awt.AxBridgeHelper.isNewDispatchThreadNeeded(); 
    }
    /**
     * Indicates whether this dialog window is resizable.
     * @return    <code>true</code> if the user can resize the dialog;
     *            <code>false</code> otherwise.
     * @see       java.awt.Dialog#setResizable
     * @since     JDK1.0
     */
    public boolean isResizable() {
	return resizable;
    }

    /**
     * Sets the resizable flag.
     * @param     resizable <code>true</code> if the user can 
     *                 resize this dialog; <code>false</code> otherwise.
     * @see       java.awt.Dialog#isResizable
     * @since     JDK1.0
     */
    public synchronized void setResizable(boolean resizable) {
	this.resizable = resizable;
	DialogPeer peer = (DialogPeer)this.peer;
	if (peer != null) {
	    peer.setResizable(resizable);
	}
    }

    /**
     * Returns the parameter string representing the state of this 
     * dialog window. This string is useful for debugging. 
     * @return    the parameter string of this dialog window.
     * @since     JDK1.0
     */
    protected String paramString() {
	String str = super.paramString() + (modal ? ",modal" : ",modeless");
	if (title != null) {
	    str += ",title=" + title;
	}
	return str;
    }
}
