/*
 * @(#)Dialog.java	1.90 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.peer.DialogPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import javax.accessibility.*;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.PeerEvent;

/**
 * A Dialog is a top-level window with a title and a border
 * that is typically used to take some form of input from the user.
 *
 * The size of the dialog includes any area designated for the
 * border.  The dimensions of the border area can be obtained 
 * using the <code>getInsets</code> method, however, since 
 * these dimensions are platform-dependent, a valid insets
 * value cannot be obtained until the dialog is made displayable
 * by either calling <code>pack</code> or <code>show</code>. 
 * Since the border area is included in the overall size of the
 * dialog, the border effectively obscures a portion of the dialog,
 * constraining the area available for rendering and/or displaying
 * subcomponents to the rectangle which has an upper-left corner
 * location of <code>(insets.left, insets.top)</code>, and has a size of
 * <code>width - (insets.left + insets.right)</code> by 
 * <code>height - (insets.top + insets.bottom)</code>. 
 * <p>
 * The default layout for a dialog is <code>BorderLayout</code>.
 * <p>
 * A dialog may have its native decorations (i.e. Frame & Titlebar) turned off
 * with <code>setUndecorated</code>.  This can only be done while the dialog
 * is not {@link Component#isDisplayable() displayable}.
 * <p>
 * A dialog must have either a frame or another dialog defined as its
 * owner when it's constructed.  When the owner window of a visible dialog
 * is minimized, the dialog will automatically be hidden
 * from the user. When the owner window is subsequently restored,
 * the dialog is made visible to the user again.
 * <p>
 * In a multi-screen environment, you can create a <code>Dialog</code>
 * on a different screen device than its owner.  See {@link java.awt.Frame} for
 * more information.
 * <p>
 * A dialog can be either modeless (the default) or modal.  A modal
 * dialog is one which blocks input to all other toplevel windows
 * in the application, except for any windows created with the dialog
 * as their owner. 
 * <p>
 * Dialogs are capable of generating the following
 * <code>WindowEvents</code>:
 * <code>WindowOpened</code>, <code>WindowClosing</code>,
 * <code>WindowClosed</code>, <code>WindowActivated</code>,
 * <code>WindowDeactivated</code>, <code>WindowGainedFocus</code>,
 * <code>WindowLostFocus</code>.
 *
 * @see WindowEvent
 * @see Window#addWindowListener
 *
 * @version 	1.90, 01/23/03
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @since       JDK1.0
 */
public class Dialog extends Window {

    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }

    /**
     * A dialog's resizable property. Will be true
     * if the Dialog is to be resizable, otherwise
     * it will be false.
     *
     * @serial
     * @see #setResizable(boolean)
     */
    boolean resizable = true;


    /**
     * This field indicates whether the dialog is undecorated.
     * This property can only be changed while the dialog is not displayable.
     * <code>undecorated</code> will be true if the dialog is
     * undecorated, otherwise it will be false.
     *
     * @serial
     * @see #setUndecorated(boolean)
     * @see #isUndecorated()
     * @see Component#isDisplayable()
     * @since 1.4
     */
    boolean undecorated = false;

    /**
     * Will be true if the Dialog is modal,
     * otherwise the dialog will be modeless.
     * A modal Dialog grabs all the input to
     * the owner frame from the user.
     *
     * @serial
     * @see #isModal()
     * @see #setModal(boolean)
     */
    boolean modal;

    /**
     * Specifies the title of the Dialog.
     * This field can be null.
     *
     * @serial
     * @see #getTitle()
     * @see #setTitle(String)
     */
    String title;

    private transient boolean keepBlocking = false;
    
    private static final String base = "dialog";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = 5920926903803293709L;

    /**
     * Constructs an initially invisible, non-modal <code>Dialog</code> with 
     * an empty title and the specified owner frame.
     *
     * @param owner the owner of the dialog
     * @exception IllegalArgumentException if the <code>owner</code>'s
     *    <code>GraphicsConfiguration</code> is not from a screen device
     * @exception java.lang.IllegalArgumentException if <code>owner</code>
     *    is <code>null</code>; this exception is always thrown
     *    when <code>GraphicsEnvironment.isHeadless</code>
     *    returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see Component#setSize
     * @see Component#setVisible
     */
    public Dialog(Frame owner) {
	this(owner, "", false);
    }

    /**
     * Constructs an initially invisible <code>Dialog</code> with an empty title,
     * the specified owner frame and modality.
     *
     * @param owner the owner of the dialog
     * @param modal if <code>true</code>, dialog blocks input to other
     *     app windows when shown
     * @exception IllegalArgumentException if the <code>owner</code>'s
     *    <code>GraphicsConfiguration</code> is not from a screen device
     * @exception java.lang.IllegalArgumentException if <code>owner</code>
     *     is <code>null</code>; this exception is always thrown
     *     when <code>GraphicsEnvironment.isHeadless</code>
     *     returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public Dialog(Frame owner, boolean modal) {
	this(owner, "", modal);
    }

    /**
     * Constructs an initially invisible, non-modal <code>Dialog</code> with 
     * the specified owner frame and title. 
     *
     * @param owner the owner of the dialog
     * @param title the title of the dialog; a <code>null</code> value
     *        will be accepted without causing a
     *        <code>NullPointerException</code> to be thrown
     * @exception IllegalArgumentException if the <code>owner</code>'s
     *    <code>GraphicsConfiguration</code> is not from a screen device
     * @exception java.lang.IllegalArgumentException if <code>owner</code>
     *        is <code>null</code>; this exception is always thrown
     *        when <code>GraphicsEnvironment.isHeadless</code>
     *        returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see Component#setSize
     * @see Component#setVisible
     */
    public Dialog(Frame owner, String title) {
	this(owner, title, false);
    }

    /**
     * Constructs an initially invisible <code>Dialog</code> with the
     * specified owner frame, title, and modality. 
     *
     * @param owner the owner of the dialog
     * @param title the title of the dialog; a <code>null</code> value
     *     will be accepted without causing a
     *     <code>NullPointerException</code> to be thrown
     * @param modal if true, dialog blocks input to other app windows when shown
     * @exception IllegalArgumentException if the <code>owner</code>'s
     *    <code>GraphicsConfiguration</code> is not from a screen device
     * @exception java.lang.IllegalArgumentException if <code>owner</code>
     *    is <code>null</code>.  This exception is always thrown
     *    when <code>GraphicsEnvironment.isHeadless</code>
     *    returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see Component#setSize
     * @see Component#setVisible
     */
    public Dialog(Frame owner, String title, boolean modal) {
	super(owner);
	 
	this.title = title;
	this.modal = modal;
        setFocusTraversalPolicy(KeyboardFocusManager.
                                getCurrentKeyboardFocusManager().
                                getDefaultFocusTraversalPolicy()
                                );
    }

    /**
     * Constructs an initially invisible Dialog with the
     * specified owner frame, title, modality, and
     * <code>GraphicsConfiguration</code>. 
     * @param owner the owner of the dialog
     * @param title the title of the dialog. A <code>null</code> value
     *        will be accepted without causing a NullPointerException
     *        to be thrown.
     * @param modal if true, dialog blocks input to other app windows when shown
     * @param gc the <code>GraphicsConfiguration</code> 
     * of the target screen device.  If <code>gc</code> is 
     * <code>null</code>, the same
     * <code>GraphicsConfiguration</code> as the owning Frame is used.
     * @exception java.lang.IllegalArgumentException if <code>owner</code>
     *            is <code>null</code>.  This exception is always thrown
     *            when GraphicsEnvironment.isHeadless() returns true
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see Component#setSize
     * @see Component#setVisible
     * @since 1.4
     */
    public Dialog(Frame owner, String title, boolean modal, 
                  GraphicsConfiguration gc) {
        super(owner, gc);
	 
        this.title = title;
        this.modal = modal;
        setFocusTraversalPolicy(KeyboardFocusManager.
                                getCurrentKeyboardFocusManager().
                                getDefaultFocusTraversalPolicy()
                                );
    }

    /**
     * Constructs an initially invisible, non-modal Dialog with 
     * an empty title and the specified owner dialog.
     * @param owner the owner of the dialog
     * @exception java.lang.IllegalArgumentException if <code>owner</code>
     *            is <code>null</code>.  This exception is always thrown
     *            when GraphicsEnvironment.isHeadless() returns true
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @since 1.2
     */
    public Dialog(Dialog owner) {
	this(owner, "", false);
    }

    /**
     * Constructs an initially invisible, non-modal Dialog 
     * with the specified owner dialog and title. 
     * @param owner the owner of the dialog
     * @param title the title of the dialog. A <code>null</code> value
     *        will be accepted without causing a NullPointerException
     *        to be thrown.
     * @exception java.lang.IllegalArgumentException if <code>owner</code>
     *            is <code>null</code>.  This exception is always thrown
     *            when GraphicsEnvironment.isHeadless() returns true
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @since 1.2
     */
    public Dialog(Dialog owner, String title) {
	this(owner, title, false);
    }

    /**
     * Constructs an initially invisible <code>Dialog</code> with the
     * specified owner dialog, title, and modality. 
     *
     * @param owner the owner of the dialog
     * @exception IllegalArgumentException if the <code>owner</code>'s
     *    <code>GraphicsConfiguration</code> is not from a screen device
     * @param title the title of the dialog; a <code>null</code> value
     *    will be accepted without causing a
     *    <code>NullPointerException</code> to be thrown
     * @param modal if true, dialog blocks input to other app windows when shown
     * @exception java.lang.IllegalArgumentException if <code>owner</code>
     *    is <code>null</code>; this exception is always thrown
     *    when <code>GraphicsEnvironment.isHeadless</code>
     *    returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @since 1.2
     */
    public Dialog(Dialog owner, String title, boolean modal) {
	super(owner);
	 
	this.title = title;
	this.modal = modal;
        setFocusTraversalPolicy(KeyboardFocusManager.
                                getCurrentKeyboardFocusManager().
                                getDefaultFocusTraversalPolicy()
                                );
    }

    /**
     * Constructs an initially invisible <code>Dialog</code> with the
     * specified owner dialog, title, modality, and
     * <code>GraphicsConfiguration</code>. 
     *
     * @param owner the owner of the dialog
     * @param title the title of the dialog; a <code>null</code> value
     *     will be accepted without causing a
     *     <code>NullPointerException</code> to be thrown
     * @param modal if true, dialog blocks input to other app windows when shown
     * @param gc the <code>GraphicsConfiguration</code> 
     *     of the target screen device; if <code>gc</code> is 
     *     <code>null</code>, the same
     *     <code>GraphicsConfiguration</code> as the owning Dialog is used
     * @exception IllegalArgumentException if the <code>owner</code>'s
     *    <code>GraphicsConfiguration</code> is not from a screen device
     * @exception java.lang.IllegalArgumentException if <code>owner</code>
     *    is <code>null</code>; this exception is always thrown
     *    when <code>GraphicsEnvironment.isHeadless</code>
     *    returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see Component#setSize
     * @see Component#setVisible
     * @since 1.4
     */
    public Dialog(Dialog owner, String title, boolean modal, 
                  GraphicsConfiguration gc) {
	super(owner, gc);
	 
	this.title = title;
	this.modal = modal;

        setFocusTraversalPolicy(KeyboardFocusManager.
                                getCurrentKeyboardFocusManager().
                                getDefaultFocusTraversalPolicy()
                                );
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        synchronized (getClass()) {
	    return base + nameCounter++;
	}
    }

    /**
     * Makes this Dialog displayable by connecting it to
     * a native screen resource.  Making a dialog displayable will
     * cause any of its children to be made displayable.
     * This method is called internally by the toolkit and should
     * not be called directly by programs.
     * @see Component#isDisplayable
     * @see #removeNotify
     */
    public void addNotify() {
	synchronized (getTreeLock()) {
	    if (parent != null && parent.getPeer() == null) {
                parent.addNotify();
	    }

	    if (peer == null) {
	        peer = getToolkit().createDialog(this);
	    }
	    super.addNotify();
	}
    }

    /**
     * Indicates whether the dialog is modal.
     * When a modal Dialog is made visible, user input will be
     * blocked to the other windows in the application, except for
     * any windows created with this dialog as their owner.
     *   
     * @return    <code>true</code> if this dialog window is modal;
     *            <code>false</code> otherwise.
     * @see       java.awt.Dialog#setModal
     */
    public boolean isModal() {
	return modal;
    }

    /**
     * Specifies whether this dialog should be modal.  
     * @see       java.awt.Dialog#isModal
     * @since     JDK1.1
     */
    public void setModal(boolean b) {
	this.modal = b;
    }

    /**
     * Gets the title of the dialog. The title is displayed in the
     * dialog's border.
     * @return    the title of this dialog window. The title may be
     *            <code>null</code>.
     * @see       java.awt.Dialog#setTitle
     */
    public String getTitle() {
	return title;
    }

    /**
     * Sets the title of the Dialog.
     * @param title the title displayed in the dialog's border;
	 * a null value results in an empty title
     * @see #getTitle
     */
    public void setTitle(String title) {
        String oldTitle = this.title;

        synchronized(this) {
            this.title = title;
            DialogPeer peer = (DialogPeer)this.peer;
            if (peer != null) {
                peer.setTitle(title);
            }
        }
        firePropertyChange("title", oldTitle, title);
    }

    /**
     * @return true if we actually showed, false if we just called toFront()
     */
    private boolean conditionalShow() {
        boolean retval;

        synchronized (getTreeLock()) {
            if (peer == null) {
                addNotify();
            }
            validate();
            if (visible) {
                toFront();
                retval = false;
            } else {
                visible = retval = true;
                peer.show(); // now guaranteed never to block
		createHierarchyEvents(HierarchyEvent.HIERARCHY_CHANGED,
				      this, parent,
				      HierarchyEvent.SHOWING_CHANGED,
                                      Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_EVENT_MASK));
            }
	    if (retval && (componentListener != null ||
			   (eventMask & AWTEvent.COMPONENT_EVENT_MASK) != 0 ||
                           Toolkit.enabledOnToolkit(AWTEvent.COMPONENT_EVENT_MASK))) {
	        ComponentEvent e =
		    new ComponentEvent(this, ComponentEvent.COMPONENT_SHOWN);
		Toolkit.getEventQueue().postEvent(e);
	    }
        }

        if (retval && (state & OPENED) == 0) {
            postWindowEvent(WindowEvent.WINDOW_OPENED);
            state |= OPENED;
        }

        return retval;
    }
    
    /**
    * Stores the app context on which event dispatch thread the dialog
    * is being shown. Initialized in show(), used in hideAndDisposeHandler()
    */
    transient private AppContext showAppContext;

   /**
     * Makes the Dialog visible. If the dialog and/or its owner
     * are not yet displayable, both are made displayable.  The 
     * dialog will be validated prior to being made visible.  
     * If the dialog is already visible, this will bring the dialog 
     * to the front.
     * <p>
     * If the dialog is modal and is not already visible, this call will
     * not return until the dialog is hidden by calling <code>hide</code> or
     * <code>dispose</code>. It is permissible to show modal dialogs from
     * the event dispatching thread because the toolkit will ensure that
     * another event pump runs while the one which invoked this method
     * is blocked. 
     * @see Component#hide
     * @see Component#isDisplayable
     * @see Component#validate
     * @see java.awt.Dialog#isModal
     */
    public void show() {
        beforeFirstShow = false;
        if (!isModal()) {
            conditionalShow();
        } else {
            // Set this variable before calling conditionalShow(). That
            // way, if the Dialog is hidden right after being shown, we
            // won't mistakenly block this thread.
            keepBlocking = true;

            // Store the app context on which this dialog is being shown.
            // Event dispatch thread of this app context will be sleeping until
            // we wake it by any event from hideAndDisposeHandler().
            showAppContext = AppContext.getAppContext();

            if (conditionalShow()) {
                // We have two mechanisms for blocking: 1. If we're on the
                // EventDispatchThread, start a new event pump. 2. If we're
                // on any other thread, call wait() on the treelock.

                // keep the KeyEvents from being dispatched
                // until the focus has been transfered
                long time = Toolkit.getEventQueue().getMostRecentEventTime();
                Component predictedFocusOwner = getMostRecentFocusOwner();
                KeyboardFocusManager.getCurrentKeyboardFocusManager().
                     enqueueKeyEvents(time, predictedFocusOwner); 

                Runnable pumpEventsForHierarchy = new Runnable() {
                        public void run() {
                            EventDispatchThread dispatchThread =
                                (EventDispatchThread)Thread.currentThread();
                            dispatchThread.pumpEventsForHierarchy(new Conditional() {
                                    public boolean evaluate() {
                                        return keepBlocking && windowClosingException == null;
                                    }
                                }, Dialog.this);
                        }
                    };

                if (EventQueue.isDispatchThread()) {
                    /*
                     * dispose SequencedEvent we are dispatching on current
                     * AppContext, to prevent us from hang.
                     *
                     * BugId 4531693 (son@sparc.spb.su)
                     */
                    SequencedEvent currentSequencedEvent = KeyboardFocusManager.
                        getCurrentKeyboardFocusManager().getCurrentSequencedEvent();
                    if (currentSequencedEvent != null) {
                        currentSequencedEvent.dispose();
                    }

                    pumpEventsForHierarchy.run();
                } else {
                    synchronized (getTreeLock()) {
                        Toolkit.getEventQueue().
                            postEvent(new PeerEvent(this,
                                                    pumpEventsForHierarchy,
                                                    PeerEvent.PRIORITY_EVENT));
                        while (keepBlocking && windowClosingException == null) {
                            try {
                                getTreeLock().wait();
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                }
                KeyboardFocusManager.getCurrentKeyboardFocusManager().
                    dequeueKeyEvents(time, predictedFocusOwner);
                if (windowClosingException != null) {
                    windowClosingException.fillInStackTrace();
                    throw windowClosingException;
                }
            }
        }
    }
    
    void interruptBlocking() {
        if (modal) {
            disposeImpl();
        } else if (windowClosingException != null) {
            windowClosingException.fillInStackTrace();
            windowClosingException.printStackTrace();
            windowClosingException = null;
        }
    }
    final static class WakingRunnable implements Runnable {
        public void run() {
        }
    }
    private void hideAndDisposeHandler() {
        if (keepBlocking) {
            synchronized (getTreeLock()) {
                keepBlocking = false;

                if (showAppContext != null) {
                    // Wake up event dispatch thread on which the dialog was 
                    // initially shown
                    SunToolkit.postEvent(showAppContext, 
                                         new PeerEvent(this, 
                                                       new WakingRunnable(), 
                                                       PeerEvent.PRIORITY_EVENT));
                    showAppContext = null;
                }
                EventQueue.invokeLater(new WakingRunnable());
                getTreeLock().notifyAll();
            }
        }
    }   

    /**
     * Hides the Dialog and then causes show() to return if it is currently
     * blocked.
     */
    public void hide() {
        super.hide();
        hideAndDisposeHandler();
    }

    /**
     * Disposes the Dialog and then causes show() to return if it is currently
     * blocked.
     */
    public void dispose() {
        disposeImpl();
    }
    
    private void disposeImpl() {
        super.dispose();
        hideAndDisposeHandler();
    }
    
    /**
     * Indicates whether this dialog is resizable by the user.
     * @return    <code>true</code> if the user can resize the dialog;
     *            <code>false</code> otherwise.
     * @see       java.awt.Dialog#setResizable
     */
    public boolean isResizable() {
	return resizable;
    }

    /**
     * Sets whether this dialog is resizable by the user.
     * @param     resizable <code>true</code> if the user can
     *                 resize this dialog; <code>false</code> otherwise.
     * @see       java.awt.Dialog#isResizable
     */
    public void setResizable(boolean resizable) {
        boolean testvalid = false;

        synchronized (this) {
            this.resizable = resizable;
            DialogPeer peer = (DialogPeer)this.peer;
            if (peer != null) {
                peer.setResizable(resizable);
                testvalid = true;
            }
        }

        // On some platforms, changing the resizable state affects
        // the insets of the Dialog. If we could, we'd call invalidate()
        // from the peer, but we need to guarantee that we're not holding
        // the Dialog lock when we call invalidate().
        if (testvalid && valid) {
            invalidate();
        }
    }


    /**
     * Disables or enables decorations for this dialog.
     * This method can only be called while the dialog is not displayable.
     * @param  undecorated <code>true</code> if no dialog decorations are 
     *         to be enabled;
     *         <code>false</code> if dialog decorations are to be enabled.
     * @throws <code>IllegalComponentStateException</code> if the dialog
     *         is displayable.
     * @see    #isUndecorated
     * @see    Component#isDisplayable	 
     * @since 1.4
     */
    public void setUndecorated(boolean undecorated) {
        /* Make sure we don't run in the middle of peer creation.*/
        synchronized (getTreeLock()) {
            if (isDisplayable()) {
                throw new IllegalComponentStateException("The dialog is displayable.");
            }
            this.undecorated = undecorated;
        }
    }

    /**
     * Indicates whether this dialog is undecorated.  
     * By default, all dialogs are initially decorated. 
     * @return    <code>true</code> if dialog is undecorated; 
     *                        <code>false</code> otherwise.
     * @see       java.awt.Dialog#setUndecorated
     * @since 1.4
     */
    public boolean isUndecorated() {
	return undecorated;
    }

    /**
     * Returns a string representing the state of this dialog. This 
     * method is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between 
     * implementations. The returned string may be empty but may not be 
     * <code>null</code>.
     * 
     * @return    the parameter string of this dialog window.
     */
    protected String paramString() {
	String str = super.paramString() + (modal ? ",modal" : ",modeless");
	if (title != null) {
	    str += ",title=" + title;
	}
	return str;
    }

    /**
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();

    /*
     * --- Accessibility Support ---
     *
     */

    /**
     * Gets the AccessibleContext associated with this Dialog. 
     * For dialogs, the AccessibleContext takes the form of an 
     * AccessibleAWTDialog. 
     * A new AccessibleAWTDialog instance is created if necessary.
     *
     * @return an AccessibleAWTDialog that serves as the 
     *         AccessibleContext of this Dialog
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTDialog();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>Dialog</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to dialog user-interface elements.
     */
    protected class AccessibleAWTDialog extends AccessibleAWTWindow {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.DIALOG;
        }

        /**
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current 
         * state set of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            if (getFocusOwner() != null) {
                states.add(AccessibleState.ACTIVE);
            }
	    if (isModal()) {
                states.add(AccessibleState.MODAL);
	    }
	    if (isResizable()) {
                states.add(AccessibleState.RESIZABLE);
	    }
            return states;
        }
    
    } // inner class AccessibleAWTDialog
}
