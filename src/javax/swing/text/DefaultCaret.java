/*
 * @(#)DefaultCaret.java	1.121 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.beans.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.util.EventListener; 

/**
 * A default implementation of Caret.  The caret is rendered as
 * a vertical line in the color specified by the CaretColor property 
 * of the associated JTextComponent.  It can blink at the rate specified
 * by the BlinkRate property.
 * <p>
 * This implementation expects two sources of asynchronous notification.
 * The timer thread fires asynchronously, and causes the caret to simply
 * repaint the most recent bounding box.  The caret also tracks change
 * as the document is modified.  Typically this will happen on the
 * event thread as a result of some mouse or keyboard event.  Updates
 * can also occur from some other thread mutating the document.  There
 * is a property <code>AsynchronousMovement</code> that determines if
 * the caret will move on asynchronous updates.  The default behavior
 * is to <em>not</em> update on asynchronous updates.  If asynchronous
 * updates are allowed, the update thread will fire the caret position 
 * change to listeners asynchronously.  The repaint of the new caret 
 * location will occur on the event thread in any case, as calls to
 * <code>modelToView</code> are only safe on the event thread.
 * <p>
 * The caret acts as a mouse and focus listener on the text component
 * it has been installed in, and defines the caret semantics based upon
 * those events.  The listener methods can be reimplemented to change the 
 * semantics.
 * By default, the first mouse button will be used to set focus and caret
 * position.  Dragging the mouse pointer with the first mouse button will
 * sweep out a selection that is contiguous in the model.  If the associated
 * text component is editable, the caret will become visible when focus
 * is gained, and invisible when focus is lost.
 * <p>
 * The Highlighter bound to the associated text component is used to 
 * render the selection by default.  
 * Selection appearance can be customized by supplying a
 * painter to use for the highlights.  By default a painter is used that
 * will render a solid color as specified in the associated text component
 * in the <code>SelectionColor</code> property.  This can easily be changed
 * by reimplementing the 
 * <a href="#getSelectionHighlighter">getSelectionHighlighter</a>
 * method.
 * <p>
 * A customized caret appearance can be achieved by reimplementing
 * the paint method.  If the paint method is changed, the damage method
 * should also be reimplemented to cause a repaint for the area needed
 * to render the caret.  The caret extends the Rectangle class which
 * is used to hold the bounding box for where the caret was last rendered.
 * This enables the caret to repaint in a thread-safe manner when the
 * caret moves without making a call to modelToView which is unstable
 * between model updates and view repair (i.e. the order of delivery 
 * to DocumentListeners is not guaranteed).
 * <p>
 * The magic caret position is set to null when the caret position changes.
 * A timer is used to determine the new location (after the caret change).
 * When the timer fires, if the magic caret position is still null it is
 * reset to the current caret position. Any actions that change
 * the caret position and want the magic caret position to remain the
 * same, must remember the magic caret position, change the cursor, and
 * then set the magic caret position to its original value. This has the
 * benefit that only actions that want the magic caret position to persist
 * (such as open/down) need to know about it.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author  Timothy Prinzing
 * @version 1.121 01/23/03
 * @see     Caret
 */
public class DefaultCaret extends Rectangle implements Caret, FocusListener, MouseListener, MouseMotionListener {

    /**
     * Constructs a default caret.
     */
    public DefaultCaret() {
	async = false;
    }

    /**
     * Get the flag that determines whether or not 
     * asynchronous updates will move the caret.
     * Normally the caret is moved by events from
     * the event thread such as mouse or keyboard
     * events.  Changes from another thread might
     * be used to load a file, or show changes
     * from another user.  This flag determines
     * whether those changes will move the caret.
     */
    boolean getAsynchronousMovement() {
	return async;
    }

    /**
     * Set the flag that determines whether or not 
     * asynchronous updates will move the caret.
     * Normally the caret is moved by events from
     * the event thread such as mouse or keyboard
     * events.  Changes from another thread might
     * be used to load a file, or show changes
     * from another user.  This flag determines
     * whether those changes will move the caret.
     *
     * @param m move the caret on asynchronous
     *   updates if true.
     */
    void setAsynchronousMovement(boolean m) {
	async = m;
    }

    /**
     * Gets the text editor component that this caret is 
     * is bound to.
     *
     * @return the component
     */
    protected final JTextComponent getComponent() {
	return component;
    }

    /**
     * Cause the caret to be painted.  The repaint
     * area is the bounding box of the caret (i.e.
     * the caret rectangle or <em>this</em>).
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see 
     * <A HREF="http://java.sun.com/products/jfc/swingdoc-archive/threads.html">
     * Threads and Swing</A> for more information.
     */
    protected final synchronized void repaint() {
	if (component != null) {
	    component.repaint(x, y, width, height);
	}
    }

    /**
     * Damages the area surrounding the caret to cause
     * it to be repainted in a new location.  If paint() 
     * is reimplemented, this method should also be 
     * reimplemented.  This method should update the 
     * caret bounds (x, y, width, and height).
     *
     * @param r  the current location of the caret
     * @see #paint
     */
    protected synchronized void damage(Rectangle r) {
	if (r != null) {
	    x = r.x - 4;
	    y = r.y;
	    width = 10;
	    height = r.height;
	    repaint();
	}
    }

    /**
     * Scrolls the associated view (if necessary) to make
     * the caret visible.  Since how this should be done
     * is somewhat of a policy, this method can be 
     * reimplemented to change the behavior.  By default
     * the scrollRectToVisible method is called on the
     * associated component.
     *
     * @param nloc the new position to scroll to
     */
    protected void adjustVisibility(Rectangle nloc) {
        if (SwingUtilities.isEventDispatchThread()) {
            if (component != null) {
                component.scrollRectToVisible(nloc);
            }
        }
        else {
            SwingUtilities.invokeLater(new SafeScroller(nloc));
        }
    }

    /**
     * Gets the painter for the Highlighter.
     *
     * @return the painter
     */
    protected Highlighter.HighlightPainter getSelectionPainter() {
	return DefaultHighlighter.DefaultPainter;
    }

    /**
     * Tries to set the position of the caret from
     * the coordinates of a mouse event, using viewToModel().
     *
     * @param e the mouse event
     */
    protected void positionCaret(MouseEvent e) {
	Point pt = new Point(e.getX(), e.getY());
	Position.Bias[] biasRet = new Position.Bias[1];
	int pos = component.getUI().viewToModel(component, pt, biasRet);
	if(biasRet[0] == null)
	    biasRet[0] = Position.Bias.Forward;
	if (pos >= 0) {
	    setDot(pos, biasRet[0]);
	}
    }

    /**
     * Tries to move the position of the caret from
     * the coordinates of a mouse event, using viewToModel(). 
     * This will cause a selection if the dot and mark
     * are different.
     *
     * @param e the mouse event
     */
    protected void moveCaret(MouseEvent e) {
	Point pt = new Point(e.getX(), e.getY());
	Position.Bias[] biasRet = new Position.Bias[1];
	int pos = component.getUI().viewToModel(component, pt, biasRet);
	if(biasRet[0] == null)
	    biasRet[0] = Position.Bias.Forward;
	if (pos >= 0) {
	    moveDot(pos, biasRet[0]);
	}
    }

    // --- FocusListener methods --------------------------

    /**
     * Called when the component containing the caret gains
     * focus.  This is implemented to set the caret to visible
     * if the component is editable.
     *
     * @param e the focus event
     * @see FocusListener#focusGained
     */
    public void focusGained(FocusEvent e) {
	if (component.isEnabled()) {
	    if (component.isEditable()) {
		setVisible(true);
	    }
	    setSelectionVisible(true);
	}
    }

    /**
     * Called when the component containing the caret loses
     * focus.  This is implemented to set the caret to visibility
     * to false.
     *
     * @param e the focus event
     * @see FocusListener#focusLost
     */
    public void focusLost(FocusEvent e) {
	setVisible(false);
        setSelectionVisible(ownsSelection || e.isTemporary());
    }

    // --- MouseListener methods -----------------------------------
    
    /**
     * Called when the mouse is clicked.  If the click was generated
     * from button1, a double click selects a word,
     * and a triple click the current line.
     *
     * @param e the mouse event
     * @see MouseListener#mouseClicked
     */
    public void mouseClicked(MouseEvent e) {
	if (! e.isConsumed()) {
	    int nclicks = e.getClickCount();
	    if (SwingUtilities.isLeftMouseButton(e)) {
		// mouse 1 behavior
		if(e.getClickCount() == 2) {
		    Action a = new DefaultEditorKit.SelectWordAction();
		    a.actionPerformed(new ActionEvent(getComponent(),
						      ActionEvent.ACTION_PERFORMED, null, e.getWhen(), e.getModifiers()));
		} else if(e.getClickCount() == 3) {
		    Action a = new DefaultEditorKit.SelectLineAction();
		    a.actionPerformed(new ActionEvent(getComponent(),
						      ActionEvent.ACTION_PERFORMED, null, e.getWhen(), e.getModifiers()));
		} 
	    } else if (SwingUtilities.isMiddleMouseButton(e)) {
		// mouse 2 behavior
		if (nclicks == 1 && component.isEditable() && component.isEnabled()) {
		    // paste system selection, if it exists
		    JTextComponent c = (JTextComponent) e.getSource();
		    if (c != null) {
			try {
			    Toolkit tk = c.getToolkit();
			    Clipboard buffer = tk.getSystemSelection();
			    if (buffer != null) {
				// platform supports system selections, update it.
				adjustCaret(e);
				TransferHandler th = c.getTransferHandler();
				if (th != null) {
                                    Transferable trans = buffer.getContents(null);
                                    if (trans != null) {
                                        th.importData(c, trans);
                                    }
				}
                                adjustFocus(true);
			    }
			} catch (HeadlessException he) {
			    // do nothing... there is no system clipboard
			}
		    }
		}
	    }
	}
    }

    /**
     * If button 1 is pressed, this is implemented to
     * request focus on the associated text component, 
     * and to set the caret position. If the shift key is held down,
     * the caret will be moved, potentially resulting in a selection,
     * otherwise the
     * caret position will be set to the new location.  If the component
     * is not enabled, there will be no request for focus.
     *
     * @param e the mouse event
     * @see MouseListener#mousePressed
     */
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (e.isConsumed()) {
                shouldHandleRelease = true;
            } else {
                shouldHandleRelease = false;
                adjustCaretAndFocus(e);
            }
        }
    }

    void adjustCaretAndFocus(MouseEvent e) {
        adjustCaret(e);
        adjustFocus(false);
    }

    /**
     * Adjusts the caret location based on the MouseEvent.
     */
    private void adjustCaret(MouseEvent e) {
	if ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0 &&
	    getDot() != -1) {
	    moveCaret(e);
	} else {
	    positionCaret(e);
	}
    }

    /**
     * Adjusts the focus, if necessary.
     *
     * @param inWindow if true indicates requestFocusInWindow should be used
     */
    private void adjustFocus(boolean inWindow) {
	if ((component != null) && component.isEnabled() &&
                                   component.isRequestFocusEnabled()) {
            if (inWindow) {
                component.requestFocusInWindow();
            }
            else {
                component.requestFocus();
            }
	}
    }

    /**
     * Called when the mouse is released.
     *
     * @param e the mouse event
     * @see MouseListener#mouseReleased
     */
    public void mouseReleased(MouseEvent e) {
        if (shouldHandleRelease && SwingUtilities.isLeftMouseButton(e)) {
            adjustCaretAndFocus(e);
        }
    }

    /**
     * Called when the mouse enters a region.
     *
     * @param e the mouse event
     * @see MouseListener#mouseEntered
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Called when the mouse exits a region.
     *
     * @param e the mouse event
     * @see MouseListener#mouseExited
     */
    public void mouseExited(MouseEvent e) {
    }

    // --- MouseMotionListener methods -------------------------

    /**
     * Moves the caret position 
     * according to the mouse pointer's current
     * location.  This effectively extends the
     * selection.  By default, this is only done
     * for mouse button 1.
     *
     * @param e the mouse event
     * @see MouseMotionListener#mouseDragged
     */
    public void mouseDragged(MouseEvent e) {
	if ((! e.isConsumed()) && SwingUtilities.isLeftMouseButton(e)) {
	    moveCaret(e);
	}
    }

    /**
     * Called when the mouse is moved.
     *
     * @param e the mouse event
     * @see MouseMotionListener#mouseMoved
     */
    public void mouseMoved(MouseEvent e) {
    }

    // ---- Caret methods ---------------------------------

    /**
     * Renders the caret as a vertical line.  If this is reimplemented
     * the damage method should also be reimplemented as it assumes the
     * shape of the caret is a vertical line.  Sets the caret color to
     * the value returned by getCaretColor().
     * <p>
     * If there are multiple text directions present in the associated
     * document, a flag indicating the caret bias will be rendered.
     * This will occur only if the associated document is a subclass
     * of AbstractDocument and there are multiple bidi levels present
     * in the bidi element structure (i.e. the text has multiple
     * directions associated with it).
     *
     * @param g the graphics context
     * @see #damage
     */
    public void paint(Graphics g) {
	if(isVisible()) {
	    try {
		TextUI mapper = component.getUI();
		Rectangle r = mapper.modelToView(component, dot, dotBias);

                if ((r == null) || ((r.width == 0) && (r.height == 0))) {
                    return;
                }
                if (width > 0 && height > 0 &&
                                !this._contains(r.x, r.y, r.width, r.height)) {
                    // We seem to have gotten out of sync and no longer
                    // contain the right location, adjust accordingly.
                    Rectangle clip = g.getClipBounds();

                    if (clip != null && !clip.contains(this)) {
                        // Clip doesn't contain the old location, force it
                        // to be repainted lest we leave a caret around.
                        repaint();
                    }
                    // This will potentially cause a repaint of something
                    // we're already repainting, but without changing the
                    // semantics of damage we can't really get around this.
                    damage(r);
                }
		g.setColor(component.getCaretColor());
		g.drawLine(r.x, r.y, r.x, r.y + r.height - 1);

		// see if we should paint a flag to indicate the bias
		// of the caret.  
		// PENDING(prinz) this should be done through
		// protected methods so that alternative LAF 
		// will show bidi information.
		Document doc = component.getDocument();
		if (doc instanceof AbstractDocument) {
		    Element bidi = ((AbstractDocument)doc).getBidiRootElement();
		    if ((bidi != null) && (bidi.getElementCount() > 1)) {
			// there are multiple directions present.
                        flagXPoints[0] = r.x;
                        flagYPoints[0] = r.y;
                        flagXPoints[1] = r.x;
                        flagYPoints[1] = r.y + 4;
                        flagYPoints[2] = r.y;
                        flagXPoints[2] = (dotLTR) ? r.x + 5 : r.x - 4;
                        g.fillPolygon(flagXPoints, flagYPoints, 3);                      
		    }
		}
	    } catch (BadLocationException e) {
		// can't render I guess
		//System.err.println("Can't render cursor");
	    }
	}
    }
    
    /**
     * Called when the UI is being installed into the
     * interface of a JTextComponent.  This can be used
     * to gain access to the model that is being navigated
     * by the implementation of this interface.  Sets the dot
     * and mark to 0, and establishes document, property change,
     * focus, mouse, and mouse motion listeners.
     *
     * @param c the component
     * @see Caret#install
     */
    public void install(JTextComponent c) {
	component = c;
	Document doc = c.getDocument();
	dot = mark = 0;
	dotLTR = markLTR = true;
	dotBias = markBias = Position.Bias.Forward;
	if (doc != null) {
	    doc.addDocumentListener(updateHandler);
	}
	c.addPropertyChangeListener(updateHandler);
	focusListener = new FocusHandler(this);
	c.addFocusListener(focusListener);
	c.addMouseListener(this);
	c.addMouseMotionListener(this);

	// if the component already has focus, it won't
	// be notified.
	if (component.hasFocus()) {
	    focusGained(null);
	}
    }

    /**
     * Called when the UI is being removed from the
     * interface of a JTextComponent.  This is used to
     * unregister any listeners that were attached.
     *
     * @param c the component
     * @see Caret#deinstall
     */
    public void deinstall(JTextComponent c) {
	c.removeMouseListener(this);
	c.removeMouseMotionListener(this);
	if (focusListener != null) {
	    c.removeFocusListener(focusListener);
	    focusListener = null;
	}
	c.removePropertyChangeListener(updateHandler);
	Document doc = c.getDocument();
	if (doc != null) {
	    doc.removeDocumentListener(updateHandler);
	}
	synchronized(this) {
	    component = null;
	}
	if (flasher != null) {
	    flasher.stop();
	}

	
    }

    /**
     * Adds a listener to track whenever the caret position has
     * been changed.
     *
     * @param l the listener
     * @see Caret#addChangeListener
     */
    public void addChangeListener(ChangeListener l) {
	listenerList.add(ChangeListener.class, l);
    }
	
    /**
     * Removes a listener that was tracking caret position changes.
     *
     * @param l the listener
     * @see Caret#removeChangeListener
     */
    public void removeChangeListener(ChangeListener l) {
	listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Returns an array of all the change listeners
     * registered on this caret.
     *
     * @return all of this caret's <code>ChangeListener</code>s 
     *         or an empty
     *         array if no change listeners are currently registered
     *
     * @see #addChangeListener
     * @see #removeChangeListener
     *
     * @since 1.4
     */
    public ChangeListener[] getChangeListeners() {
        return (ChangeListener[])listenerList.getListeners(
                ChangeListener.class);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.  The listener list is processed last to first.
     *
     * @see EventListenerList
     */
    protected void fireStateChanged() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==ChangeListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
	    }	       
	}
    }	

    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this caret.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     *
     * You can specify the <code>listenerType</code> argument
     * with a class literal,
     * such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * <code>DefaultCaret</code> <code>c</code>
     * for its change listeners with the following code:
     *
     * <pre>ChangeListener[] cls = (ChangeListener[])(c.getListeners(ChangeListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param listenerType the type of listeners requested; this parameter
     *          should specify an interface that descends from
     *          <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s on this component,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code>
     *          doesn't specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getChangeListeners
     *
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	return listenerList.getListeners(listenerType); 
    }

    /**
     * Changes the selection visibility.
     *
     * @param vis the new visibility
     */
    public void setSelectionVisible(boolean vis) {
	if (vis != selectionVisible) {
	    selectionVisible = vis;
	    if (selectionVisible) {
		// show
		Highlighter h = component.getHighlighter();
		if ((dot != mark) && (h != null) && (selectionTag == null)) {
		    int p0 = Math.min(dot, mark);
		    int p1 = Math.max(dot, mark);
		    Highlighter.HighlightPainter p = getSelectionPainter();
		    try {
			selectionTag = h.addHighlight(p0, p1, p);
		    } catch (BadLocationException bl) {
			selectionTag = null;
		    }
		}
	    } else {
		// hide
		if (selectionTag != null) {
		    Highlighter h = component.getHighlighter();
		    h.removeHighlight(selectionTag);
		    selectionTag = null;
		}
	    }
	}
    }

    /**
     * Checks whether the current selection is visible.
     *
     * @return true if the selection is visible
     */
    public boolean isSelectionVisible() {
	return selectionVisible;
    }

    /**
     * Determines if the caret is currently visible.
     *
     * @return true if visible else false
     * @see Caret#isVisible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the caret visibility, and repaints the caret.
     *
     * @param e the visibility specifier
     * @see Caret#setVisible
     */
    public void setVisible(boolean e) {
	// focus lost notification can come in later after the
	// caret has been deinstalled, in which case the component
	// will be null.
	if (component != null) {
            TextUI mapper = component.getUI();
	    if (visible != e) {
                visible = e;
		// repaint the caret
		try {
		    Rectangle loc = mapper.modelToView(component, dot,dotBias);
		    damage(loc);
		} catch (BadLocationException badloc) {
		    // hmm... not legally positioned
		}
	    }
	}
	if (flasher != null) {
	    if (visible) {
		flasher.start();
	    } else {
		flasher.stop();
	    }
	}
    }

    /**
     * Sets the caret blink rate.
     *
     * @param rate the rate in milliseconds, 0 to stop blinking
     * @see Caret#setBlinkRate
     */
    public void setBlinkRate(int rate) {
	if (rate != 0) {
	    if (flasher == null) {
		flasher = new Timer(rate, updateHandler);
	    }
	    flasher.setDelay(rate);
	} else {
	    if (flasher != null) {
		flasher.stop();
		flasher.removeActionListener(updateHandler);
		flasher = null;
	    }
	}
    }

    /**
     * Gets the caret blink rate.
     *
     * @return the delay in milliseconds.  If this is
     *  zero the caret will not blink.
     * @see Caret#getBlinkRate
     */
    public int getBlinkRate() {
	return (flasher == null) ? 0 : flasher.getDelay();
    }

    /**
     * Fetches the current position of the caret.
     *
     * @return the position >= 0
     * @see Caret#getDot
     */
    public int getDot() {
        return dot;
    }

    /**
     * Fetches the current position of the mark.  If there is a selection,
     * the dot and mark will not be the same.
     *
     * @return the position >= 0
     * @see Caret#getMark
     */
    public int getMark() {
        return mark;
    }

    /**
     * Sets the caret position and mark to some position.  This
     * implicitly sets the selection range to zero.
     *
     * @param dot the position >= 0
     * @see Caret#setDot
     */
    public void setDot(int dot) {
	setDot(dot, Position.Bias.Forward);
    }

    /**
     * Moves the caret position to some other position.
     *
     * @param dot the position >= 0
     * @see Caret#moveDot
     */
    public void moveDot(int dot) {
	moveDot(dot, Position.Bias.Forward);
    }

    // ---- Bidi methods (we could put these in a subclass)
    
    void moveDot(int dot, Position.Bias dotBias) {
	if (! component.isEnabled()) {
	    // don't allow selection on disabled components.
	    setDot(dot, dotBias);
	    return;
	}
	if (dot != this.dot) {
            NavigationFilter filter = component.getNavigationFilter();

            if (filter != null) {
                filter.moveDot(getFilterBypass(), dot, dotBias);
            }
            else {
                handleMoveDot(dot, dotBias);
            }
        }
    }

    void handleMoveDot(int dot, Position.Bias dotBias) {
        changeCaretPosition(dot, dotBias);
	    
        if (selectionVisible) {
            Highlighter h = component.getHighlighter();
            if (h != null) {
                int p0 = Math.min(dot, mark);
                int p1 = Math.max(dot, mark);
                
                // if p0 == p1 then there should be no highlight, remove it if necessary
                if (p0 == p1) {
                    if (selectionTag != null) {
                        h.removeHighlight(selectionTag);
                        selectionTag = null;
                    }
                // otherwise, change or add the highlight
                } else {
                    try {
                        if (selectionTag != null) {
                            h.changeHighlight(selectionTag, p0, p1);
                        } else {
                            Highlighter.HighlightPainter p = getSelectionPainter();
                            selectionTag = h.addHighlight(p0, p1, p);
                        }
                    } catch (BadLocationException e) {
                        throw new StateInvariantError("Bad caret position");
                    }
                }
            }
        }
    }

    void setDot(int dot, Position.Bias dotBias) {
        NavigationFilter filter = component.getNavigationFilter();

        if (filter != null) {
            filter.setDot(getFilterBypass(), dot, dotBias);
        }
        else {
            handleSetDot(dot, dotBias);
        }
    }

    void handleSetDot(int dot, Position.Bias dotBias) {
	// move dot, if it changed
	Document doc = component.getDocument();
	if (doc != null) {
	    dot = Math.min(dot, doc.getLength());
	}
	dot = Math.max(dot, 0);

        // The position (0,Backward) is out of range so disallow it.
        if( dot == 0 )
            dotBias = Position.Bias.Forward;
        
	mark = dot;
	if (this.dot != dot || this.dotBias != dotBias ||
	    selectionTag != null || forceCaretPositionChange) {
	    changeCaretPosition(dot, dotBias);
	}
	this.markBias = this.dotBias;
	this.markLTR = dotLTR;
	Highlighter h = component.getHighlighter();
	if ((h != null) && (selectionTag != null)) {
	    h.removeHighlight(selectionTag);
	    selectionTag = null;
	}
    }

    Position.Bias getDotBias() {
	return dotBias;
    }

    Position.Bias getMarkBias() {
	return markBias;
    }

    boolean isDotLeftToRight() {
	return dotLTR;
    }

    boolean isMarkLeftToRight() {
	return markLTR;
    }

    boolean isPositionLTR(int position, Position.Bias bias) {
	Document doc = component.getDocument();
	if(doc instanceof AbstractDocument ) {
	    if(bias == Position.Bias.Backward && --position < 0)
		position = 0;
	    return ((AbstractDocument)doc).isLeftToRight(position, position);
	}
	return true;
    }

    Position.Bias guessBiasForOffset(int offset, Position.Bias lastBias,
				     boolean lastLTR) {
	// There is an abiguous case here. That if your model looks like:
	// abAB with the cursor at abB]A (visual representation of
	// 3 forward) deleting could either become abB] or
	// ab[B. I'ld actually prefer abB]. But, if I implement that
	// a delete at abBA] would result in aBA] vs a[BA which I 
	// think is totally wrong. To get this right we need to know what
	// was deleted. And we could get this from the bidi structure
	// in the change event. So:
	// PENDING: base this off what was deleted.
	if(lastLTR != isPositionLTR(offset, lastBias)) {
	    lastBias = Position.Bias.Backward;
	}
	else if(lastBias != Position.Bias.Backward &&
		lastLTR != isPositionLTR(offset, Position.Bias.Backward)) {
	    lastBias = Position.Bias.Backward;
	}
	if (lastBias == Position.Bias.Backward && offset > 0) {
	    try {
		Segment s = new Segment();
		component.getDocument().getText(offset - 1, 1, s);
		if (s.count > 0 && s.array[s.offset] == '\n') {
		    lastBias = Position.Bias.Forward;
		}
	    }
	    catch (BadLocationException ble) {}
	}
	return lastBias;
    }

    // ---- local methods --------------------------------------------

    /**
     * Sets the caret position (dot) to a new location.  This
     * causes the old and new location to be repainted.  It
     * also makes sure that the caret is within the visible 
     * region of the view, if the view is scrollable.
     */
    void changeCaretPosition(int dot, Position.Bias dotBias) {
	// repaint the old position and set the new value of
	// the dot.
	repaint();


        // Make sure the caret is visible if this window has the focus.
	if (flasher != null && flasher.isRunning()) {
            visible = true;
            flasher.restart();
        }

	// notify listeners at the caret moved
	this.dot = dot;
        this.dotBias = dotBias;
        dotLTR = isPositionLTR(dot, dotBias);
        fireStateChanged();

        updateSystemSelection();

	setMagicCaretPosition(null);

	// We try to repaint the caret later, since things
	// may be unstable at the time this is called 
	// (i.e. we don't want to depend upon notification
	// order or the fact that this might happen on
	// an unsafe thread).
	Runnable callRepaintNewCaret = new Runnable() {
            public void run() {
		repaintNewCaret();
	    }
	};
	SwingUtilities.invokeLater(callRepaintNewCaret);
    }

    /**
     * Repaints the new caret position, with the
     * assumption that this is happening on the
     * event thread so that calling <code>modelToView</code>
     * is safe.
     */
    void repaintNewCaret() {
	if (component != null) {
	    TextUI mapper = component.getUI();
	    Document doc = component.getDocument();
	    if ((mapper != null) && (doc != null)) {
		// determine the new location and scroll if
		// not visible.
		Rectangle newLoc;
		try {
		    newLoc = mapper.modelToView(component, this.dot, this.dotBias);
		} catch (BadLocationException e) {
		    newLoc = null;
		}
		if (newLoc != null) {
		    adjustVisibility(newLoc);
		    // If there is no magic caret position, make one
		    if (getMagicCaretPosition() == null) {
			setMagicCaretPosition(new Point(newLoc.x, newLoc.y));
		    }
		}
		
		// repaint the new position
		damage(newLoc);
	    }
	}
    }
    
    private void updateSystemSelection() {
        if (this.dot != this.mark && component != null) {
            Clipboard clip = getSystemSelection();

            if (clip != null) {
                clip.setContents(new StringSelection(
                            component.getSelectedText()), getClipboardOwner());
                ownsSelection = true;
            }
        }
    }

    private Clipboard getSystemSelection() {
        try {
            return component.getToolkit().getSystemSelection();
        } catch (HeadlessException he) {
            // do nothing... there is no system clipboard
        } catch (SecurityException se) {
            // do nothing... there is no allowed system clipboard
        }
        return null;
    }

    private ClipboardOwner getClipboardOwner() {
        if (clipboardOwner == null) {
            clipboardOwner = new ClipboardHandler();
        }
        return clipboardOwner;
    }

    /**
     * This is invoked after the document changes to verify the current
     * dot/mark is valid. We do this in case the <code>NavigationFilter</code>
     * changed where to position the dot, that resulted in the current location
     * being bogus.
     */
    private void ensureValidPosition() {
        int length = component.getDocument().getLength();
        if (dot > length || mark > length) {
            // Current location is bogus and filter likely vetoed the
            // change, force the reset without giving the filter a
            // chance at changing it.
            handleSetDot(length, Position.Bias.Forward);
        }
    }


    /**
     * Saves the current caret position.  This is used when 
     * caret up/down actions occur, moving between lines
     * that have uneven end positions.
     *
     * @param p the position
     * @see #getMagicCaretPosition
     */
    public void setMagicCaretPosition(Point p) {
	magicCaretPosition = p;
    }
    
    /**
     * Gets the saved caret position.
     *
     * @return the position
     * see #setMagicCaretPosition
     */
    public Point getMagicCaretPosition() {
	return magicCaretPosition;
    }

    /**
     * Compares this object to the specified object.
     * The superclass behavior of comparing rectangles
     * is not desired, so this is changed to the Object
     * behavior.
     *
     * @param     obj   the object to compare this font with
     * @return    <code>true</code> if the objects are equal; 
     *            <code>false</code> otherwise
     */
    public boolean equals(Object obj) {
	return (this == obj);
    }

    public String toString() {
        String s = "Dot=(" + dot + ", " + dotBias + ")";
        s += " Mark=(" + mark + ", " + markBias + ")";
        return s;
    }

    private NavigationFilter.FilterBypass getFilterBypass() {
        if (filterBypass == null) {
            filterBypass = new DefaultFilterBypass();
        }
        return filterBypass;
    }

    // Rectangle.contains returns false if passed a rect with a w or h == 0,
    // this won't (assuming X,Y are contained with this rectangle).
    private boolean _contains(int X, int Y, int W, int H) {
        int w = this.width;
        int h = this.height;
        if ((w | h | W | H) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if any dimension is zero, tests below must return false...
        int x = this.x;
        int y = this.y;
        if (X < x || Y < y) {
            return false;
        }
        if (W > 0) {
            w += x;
            W += X;
            if (W <= X) {
                // X+W overflowed or W was zero, return false if...
                // either original w or W was zero or
                // x+w did not overflow or
                // the overflowed x+w is smaller than the overflowed X+W
                if (w >= x || W > w) return false;
            } else {
                // X+W did not overflow and W was not zero, return false if...
                // original w was zero or
                // x+w did not overflow and x+w is smaller than X+W
                if (w >= x && W > w) return false;
            }
        }
        else if ((x + w) < X) {
            return false;
        }
        if (H > 0) {
            h += y;
            H += Y;
            if (H <= Y) {
                if (h >= y || H > h) return false;
            } else {
                if (h >= y && H > h) return false;
            }
        }
        else if ((y + h) < Y) {
            return false;
        }
        return true;
    }

    // --- serialization ---------------------------------------------

    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
	s.defaultReadObject();
	updateHandler = new UpdateHandler();
	if (!s.readBoolean()) {
	    dotBias = Position.Bias.Forward;
	}
	else {
	    dotBias = Position.Bias.Backward;
	}
	if (!s.readBoolean()) {
	    markBias = Position.Bias.Forward;
	}
	else {
	    markBias = Position.Bias.Backward;
	}
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
	s.writeBoolean((dotBias == Position.Bias.Backward));
	s.writeBoolean((markBias == Position.Bias.Backward));
    }
    
    // ---- member variables ------------------------------------------

    /**
     * The event listener list.
     */
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * The change event for the model.
     * Only one ChangeEvent is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;

    // package-private to avoid inner classes private member
    // access bug
    JTextComponent component;

    /**
     * flag to indicate if async updates should
     * move the caret.
     */
    boolean async;
    boolean visible;
    int dot;
    int mark;
    Object selectionTag;
    boolean selectionVisible;
    Timer flasher;
    Point magicCaretPosition;
    transient Position.Bias dotBias;
    transient Position.Bias markBias;
    boolean dotLTR;
    boolean markLTR;
    transient UpdateHandler updateHandler = new UpdateHandler();
    transient private int[] flagXPoints = new int[3];
    transient private int[] flagYPoints = new int[3];
    transient private FocusListener focusListener;
    private transient NavigationFilter.FilterBypass filterBypass;
    private transient ClipboardOwner clipboardOwner;
    /**
     * This is used to indicate if the caret currently owns the selection.
     * This is always false if the system does not support the system
     * clipboard.
     */
    private boolean ownsSelection;

    /**
     * If this is true, the location of the dot is updated regardless of
     * the current location. This is set in the DocumentListener
     * such that even if the model location of dot hasn't changed (perhaps do
     * to a forward delete) the visual location is updated.
     */
    private boolean forceCaretPositionChange;
    
    /**
     * Whether or not mouseReleased should adjust the caret and focus.
     * This flag is set by mousePressed if it wanted to adjust the caret
     * and focus but couldn't because of a possible DnD operation.
     */
    private transient boolean shouldHandleRelease;

    class SafeScroller implements Runnable {
	
	SafeScroller(Rectangle r) {
	    this.r = r;
	}

	public void run() {
	    if (component != null) {
		component.scrollRectToVisible(r);
	    }
	}

	Rectangle r;
    }


    /**
     * ClipboardOwner that will toggle the visibility of the selection
     * when ownership is lost.
     */
    private class ClipboardHandler implements ClipboardOwner {
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
            if (ownsSelection) {
                ownsSelection = false;
                if (component != null && !component.hasFocus()) {
                    setSelectionVisible(false);
                }
            }
        }
    }


    //
    // DefaultCaret extends Rectangle, which is Serializable. DefaultCaret
    // also implements FocusListener. Swing attempts to remove UI related
    // classes (such as this class) by installing a FocusListener. When
    // that FocusListener is messaged to writeObject it uninstalls the UI
    // (refer to JComponent.EnableSerializationFocusListener). The
    // problem with this is that any other FocusListeners will also get
    // serialized due to how AWT handles listeners. So, for the time being
    // this class is installed as the FocusListener on the JTextComponent,
    // and it will forward the FocusListener methods to the DefaultCaret.
    // Since FocusHandler is not Serializable DefaultCaret will not be 
    // pulled in.
    //
    private static class FocusHandler implements FocusListener {
	private transient FocusListener fl;

	FocusHandler(FocusListener fl) {
	    this.fl = fl;
	}

	/**
	 * Called when the component containing the caret gains
	 * focus.  This is implemented to set the caret to visible
	 * if the component is editable.
	 *
	 * @param e the focus event
	 * @see FocusListener#focusGained
	 */
	public void focusGained(FocusEvent e) {
	    fl.focusGained(e);
	}

	/**
	 * Called when the component containing the caret loses
	 * focus.  This is implemented to set the caret to visibility
	 * to false.
	 *
	 * @param e the focus event
	 * @see FocusListener#focusLost
	 */
	public void focusLost(FocusEvent e) {
	    fl.focusLost(e);
	}
    }


    class UpdateHandler implements PropertyChangeListener, DocumentListener, ActionListener {

	// --- ActionListener methods ----------------------------------
	
	/**
	 * Invoked when the blink timer fires.  This is called
	 * asynchronously.  The simply changes the visibility
	 * and repaints the rectangle that last bounded the caret.
	 *
	 * @param e the action event
	 */
        public void actionPerformed(ActionEvent e) {
	    if (!visible && (width == 0 || height == 0)) {
                // setVisible(true) will cause a scroll, only do this if the
                // new location is really valid.
                if (component != null) {
                    TextUI mapper = component.getUI();
                    try {
                        Rectangle r = mapper.modelToView(component, dot,
                                                         dotBias);
                        if (r != null && r.width != 0 && r.height != 0) {
                            setVisible(true);
                            return;
                        }
                    } catch (BadLocationException ble) {
                    }
                }
	    }
            visible = !visible;
            repaint();
	}
    
	// --- DocumentListener methods --------------------------------

	/**
	 * Updates the dot and mark if they were changed by
	 * the insertion.
	 *
	 * @param e the document event
	 * @see DocumentListener#insertUpdate
	 */
        public void insertUpdate(DocumentEvent e) {
	    if (async || SwingUtilities.isEventDispatchThread()) {
		int adjust = 0;
		int offset = e.getOffset();
		int length = e.getLength();
                int newDot = dot;
                short changed = 0;
		if (newDot >= offset) {
		    newDot += length;
                    changed |= 1;
		}
                int newMark = mark;
		if (newMark >= offset) {
		    newMark += length;
                    changed |= 2;
		}
	    
		if (changed != 0) {
                    Position.Bias dotBias = DefaultCaret.this.dotBias;
		    if(dot == offset) {
		        Document doc = component.getDocument();
		        boolean isNewline;
		        try {
			    Segment s = new Segment();
			    doc.getText(newDot - 1, 1, s);
			    isNewline = (s.count > 0 &&
					 s.array[s.offset] == '\n');
		        } catch (BadLocationException ble) {
			    isNewline = false;
		        }
		        if(isNewline) {
                            dotBias = Position.Bias.Forward;
	                } else {
                            dotBias = Position.Bias.Backward;
		        }
                    }
                    if (newMark == newDot) {
                        setDot(newDot, dotBias);
                        ensureValidPosition();
                    }
                    else {
                        setDot(newMark, markBias);
                        if (getDot() == newMark) {
                            // Due this test in case the filter vetoed the
                            // change in which case this probably won't be
                            // valid either.
                            moveDot(newDot, dotBias);
                        }
                        ensureValidPosition();
                    }
                }
	    }
	}

	/**
	 * Updates the dot and mark if they were changed
	 * by the removal.
	 *
	 * @param e the document event
	 * @see DocumentListener#removeUpdate
	 */
        public void removeUpdate(DocumentEvent e) {
	    if (async || SwingUtilities.isEventDispatchThread()) {
		int adjust = 0;
		int offs0 = e.getOffset();
		int offs1 = offs0 + e.getLength();
                int newDot = dot;
                boolean adjustDotBias = false;
		if (newDot >= offs1) {
                    newDot -= (offs1 - offs0);
		    if(newDot == offs1) {
		        adjustDotBias = true;
		    }
	        } else if (newDot >= offs0) {
	            newDot = offs0;
		    adjustDotBias = true;
	        }
                int newMark = mark;
	        boolean adjustMarkBias = false;
	        if (newMark >= offs1) {
	   	    newMark -= (offs1 - offs0);
		    if(newMark == offs1) {
		        adjustMarkBias = true;
		    }
	        } else if (newMark >= offs0) {
		    newMark = offs0;
		    adjustMarkBias = true;
	        }
	        if (newMark == newDot) {
                    forceCaretPositionChange = true;
                    try {
                        setDot(newDot, guessBiasForOffset(newDot, dotBias,
                                                          dotLTR));
                    } finally {
                        forceCaretPositionChange = false;
                    }
                    ensureValidPosition();
	        } else {
                    Position.Bias dotBias = DefaultCaret.this.dotBias;
                    Position.Bias markBias = DefaultCaret.this.markBias;
		    if(adjustDotBias) {
		        dotBias = guessBiasForOffset(newDot, dotBias, dotLTR);
		    }
		    if(adjustMarkBias) {
		        markBias = guessBiasForOffset(mark, markBias, markLTR);
		    }
		    setDot(newMark, markBias);
                    if (getDot() == newMark) {
                        // Due this test in case the filter vetoed the change
                        // in which case this probably won't be valid either.
                        moveDot(newDot, dotBias);
                    }
                    ensureValidPosition();
                }
	    }
	}

	/**
	 * Gives notification that an attribute or set of attributes changed.
	 *
	 * @param e the document event
	 * @see DocumentListener#changedUpdate
	 */
        public void changedUpdate(DocumentEvent e) {
	}

	// --- PropertyChangeListener methods -----------------------

	/**
	 * This method gets called when a bound property is changed.
	 * We are looking for document changes on the editor.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
	    Object oldValue = evt.getOldValue();
	    Object newValue = evt.getNewValue();
	    if ((oldValue instanceof Document) || (newValue instanceof Document)) {
                setDot(0);
		if (oldValue != null) {
		    ((Document)oldValue).removeDocumentListener(this);
		}
		if (newValue != null) {
		    ((Document)newValue).addDocumentListener(this);
		}
	    }
	}

    }


    private class DefaultFilterBypass extends NavigationFilter.FilterBypass {
        public Caret getCaret() {
            return DefaultCaret.this;
        }

        public void setDot(int dot, Position.Bias bias) {
            handleSetDot(dot, bias);
        }

        public void moveDot(int dot, Position.Bias bias) {
            handleMoveDot(dot, bias);
        }
    }
}

