/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.peer.ScrollbarPeer;
import java.awt.event.*;
import java.util.EventListener;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import javax.accessibility.*;


/**
 * The <code>Scrollbar</code> class embodies a scroll bar, a
 * familiar user-interface object. A scroll bar provides a
 * convenient means for allowing a user to select from a
 * range of values. The following three vertical
 * scroll bars could be used as slider controls to pick
 * the red, green, and blue components of a color:
 * <p>
 * <img src="doc-files/Scrollbar-1.gif"
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * Each scroll bar in this example could be created with
 * code similar to the following:
 * <p>
 * <hr><blockquote><pre>
 * redSlider=new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 255);
 * add(redSlider);
 * </pre></blockquote><hr>
 * <p>
 * Alternatively, a scroll bar can represent a range of values. For
 * example, if a scroll bar is used for scrolling through text, the
 * width of the "bubble" or "thumb" can represent the amount of text
 * that is visible. Here is an example of a scroll bar that
 * represents a range:
 * <p>
 * <img src="doc-files/Scrollbar-2.gif"
 * ALIGN=center HSPACE=10 VSPACE=7>
 * <p>
 * The value range represented by the bubble is the <em>visible</em>
 * range of the scroll bar. The horizontal scroll bar in this
 * example could be created with code like the following:
 * <p>
 * <hr><blockquote><pre>
 * ranger = new Scrollbar(Scrollbar.HORIZONTAL, 0, 60, 0, 300);
 * add(ranger);
 * </pre></blockquote><hr>
 * <p>
 * Note that the actual maximum value of the scroll bar is the
 * <code>maximum</code> minus the <code>visible</code>.
 * In the previous example, because the <code>maximum</code> is
 * 300 and the <code>visible</code> is 60, the actual maximum
 * value is 240.  The range of the scrollbar track is 0 - 300.
 * The left side of the bubble indicates the value of the
 * scroll bar.
 * <p>
 * Normally, the user changes the value of the scroll bar by
 * making a gesture with the mouse. For example, the user can
 * drag the scroll bar's bubble up and down, or click in the
 * scroll bar's unit increment or block increment areas. Keyboard
 * gestures can also be mapped to the scroll bar. By convention,
 * the <b>Page&nbsp;Up</b> and <b>Page&nbsp;Down</b>
 * keys are equivalent to clicking in the scroll bar's block
 * increment and block decrement areas.
 * <p>
 * When the user changes the value of the scroll bar, the scroll bar
 * receives an instance of <code>AdjustmentEvent</code>.
 * The scroll bar processes this event, passing it along to
 * any registered listeners.
 * <p>
 * Any object that wishes to be notified of changes to the
 * scroll bar's value should implement
 * <code>AdjustmentListener</code>, an interface defined in
 * the package <code>java.awt.event</code>.
 * Listeners can be added and removed dynamically by calling
 * the methods <code>addAdjustmentListener</code> and
 * <code>removeAdjustmentListener</code>.
 * <p>
 * The <code>AdjustmentEvent</code> class defines five types
 * of adjustment event, listed here:
 * <p>
 * <ul>
 * <li><code>AdjustmentEvent.TRACK</code> is sent out when the
 * user drags the scroll bar's bubble.
 * <li><code>AdjustmentEvent.UNIT_INCREMENT</code> is sent out
 * when the user clicks in the left arrow of a horizontal scroll
 * bar, or the top arrow of a vertical scroll bar, or makes the
 * equivalent gesture from the keyboard.
 * <li><code>AdjustmentEvent.UNIT_DECREMENT</code> is sent out
 * when the user clicks in the right arrow of a horizontal scroll
 * bar, or the bottom arrow of a vertical scroll bar, or makes the
 * equivalent gesture from the keyboard.
 * <li><code>AdjustmentEvent.BLOCK_INCREMENT</code> is sent out
 * when the user clicks in the track, to the left of the bubble
 * on a horizontal scroll bar, or above the bubble on a vertical
 * scroll bar. By convention, the <b>Page&nbsp;Up</b>
 * key is equivalent, if the user is using a keyboard that
 * defines a <b>Page&nbsp;Up</b> key.
 * <li><code>AdjustmentEvent.BLOCK_DECREMENT</code> is sent out
 * when the user clicks in the track, to the right of the bubble
 * on a horizontal scroll bar, or below the bubble on a vertical
 * scroll bar. By convention, the <b>Page&nbsp;Down</b>
 * key is equivalent, if the user is using a keyboard that
 * defines a <b>Page&nbsp;Down</b> key.
 * </ul>
 * <p>
 * The JDK&nbsp;1.0 event system is supported for backwards
 * compatibility, but its use with newer versions of the platform is
 * discouraged. The fives types of adjustment event introduced
 * with JDK&nbsp;1.1 correspond to the five event types
 * that are associated with scroll bars in previous platform versions.
 * The following list gives the adjustment event type,
 * and the corresponding JDK&nbsp;1.0 event type it replaces.
 * <p>
 * <ul>
 * <li><code>AdjustmentEvent.TRACK</code> replaces
 * <code>Event.SCROLL_ABSOLUTE</code>
 * <li><code>AdjustmentEvent.UNIT_INCREMENT</code> replaces
 * <code>Event.SCROLL_LINE_UP</code>
 * <li><code>AdjustmentEvent.UNIT_DECREMENT</code> replaces
 * <code>Event.SCROLL_LINE_DOWN</code>
 * <li><code>AdjustmentEvent.BLOCK_INCREMENT</code> replaces
 * <code>Event.SCROLL_PAGE_UP</code>
 * <li><code>AdjustmentEvent.BLOCK_DECREMENT</code> replaces
 * <code>Event.SCROLL_PAGE_DOWN</code>
 * </ul>
 * <p>
 *
 * @version 	1.78, 02/06/02
 * @author 	Sami Shaio
 * @see         java.awt.event.AdjustmentEvent
 * @see         java.awt.event.AdjustmentListener
 * @since       JDK1.0
 */
public class Scrollbar extends Component implements Adjustable, Accessible {

    /**
     * A constant that indicates a horizontal scroll bar.
     */
    public static final int	HORIZONTAL = 0;

    /**
     * A constant that indicates a vertical scroll bar.
     */
    public static final int	VERTICAL   = 1;

    /**
     * The value of the Scrollbar.
     * value should be either greater than <code>minimum</code>
     * or less that <code>maximum</code>
     *
     * @serial
     * @see getValue()
     * @see setValue()
     */
    int	value;

    /**
     * The maximum value of the Scrollbar.
     * This value must be greater than the <code>minimum</code>
     * value.<br>
     * This integer can be either positive or negative, and
     * it's range can be altered at any time.
     *
     * @serial
     * @see getMaximum()
     * @see setMaximum()
     */
    int	maximum;

    /**
     * The minimum value of the Scrollbar.
     * This value must be greater than the <code>minimum</code>
     * value.<br>
     * This integer can be either positive or negative.
     *
     * @serial
     * @see getMinimum()
     * @see setMinimum()
     */
    int	minimum;

    /**
     * The size of the visible portion of the Scrollbar.
     * The size of the scrollbox is normally used to indicate
     * the visibleAmount.
     *
     * @serial
     * @see getVisibleAmount()
     * @see setVisibleAmount()
     */
    int	visibleAmount;

    /**
     * The Scrollbar's orientation--being either horizontal or vertical.
     * This value should be specified when the scrollbar is being
     * created.<BR>
     * orientation can be either : <code>VERTICAL</code> or
     * <code>HORIZONTAL</code> only.
     *
     * @serial
     * @see getOrientation()
     * @see setOrientation()
     */
    int	orientation;

    /**
     * The amount by which the scrollbar value will change when going
     * up or down by a line.
     * This value should be a non negative integer.
     *
     * @serial
     * @see setLineIncrement()
     * @see getLineIncrement()
     */
    int lineIncrement = 1;

    /**
     * The amount by which the scrollbar value will change when going
     * up or down by a page.
     * This value should be a non negative integer.
     *
     * @serial
     * @see setPageIncrement()
     * @see getPageIncrement()
     */
    int pageIncrement = 10;

    transient AdjustmentListener adjustmentListener;

    private static final String base = "scrollbar";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = 8451667562882310543L;

    /**
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();

    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
	initIDs();
    }

    /**
     * Constructs a new vertical scroll bar.
     * The default properties of the scroll bar are listed in
     * the following table:
     * <p> </p>
     * <table border>
     * <tr>
     *   <th>Property</th>
     *   <th>Description</th>
     *   <th>Default Value</th>
     * </tr>
     * <tr>
     *   <td>orientation</td>
     *   <td>indicates if the scroll bar is vertical or horizontal</td>
     *   <td><code>Scrollbar.VERTICAL</code></td>
     * </tr>
     * <tr>
     *   <td>value</td>
     *   <td>value which controls the location
     *   <br>of the scroll bar bubble</td>
     *   <td>0</td>
     * </tr>
     * <tr>
     *   <td>minimum</td>
     *   <td>minimum value of the scroll bar</td>
     *   <td>0</td>
     * </tr>
     * <tr>
     *   <td>maximum</td>
     *   <td>maximum value of the scroll bar</td>
     *   <td>100</td>
     * </tr>
     * <tr>
     *   <td>unit increment</td>
     *   <td>amount the value changes when the
     *   <br>Line Up or Line Down key is pressed,
     *   <br>or when the end arrows of the scrollbar
     *   <br>are clicked </td>
     *   <td>1</td>
     * </tr>
     * <tr>
     *   <td>block increment</td>
     *   <td>amount the value changes when the
     *   <br>Page Up or Page Down key is pressed,
     *   <br>or when the scrollbar track is clicked 
     *   <br>on either side of the bubble </td>
     *   <td>10</td>
     * </tr>
     * </table>
     *
     */
    public Scrollbar() {
	this(VERTICAL, 0, 10, 0, 100);
    }

    /**
     * Constructs a new scroll bar with the specified orientation.
     * <p>
     * The <code>orientation</code> argument must take one of the two
     * values <code>Scrollbar.HORIZONTAL</code>,
     * or <code>Scrollbar.VERTICAL</code>,
     * indicating a horizontal or vertical scroll bar, respectively.
     * @param       orientation   indicates the orientation of the scroll bar.
     * @exception   IllegalArgumentException    when an illegal value for
     *                    the <code>orientation</code> argument is supplied.
     */
    public Scrollbar(int orientation) {
        this(orientation, 0, 10, 0, 100);
    }

    /**
     * Constructs a new scroll bar with the specified orientation,
     * initial value, page size, and minimum and maximum values.
     * <p>
     * The <code>orientation</code> argument must take one of the two
     * values <code>Scrollbar.HORIZONTAL</code>,
     * or <code>Scrollbar.VERTICAL</code>,
     * indicating a horizontal or vertical scroll bar, respectively.
     * <p>
     * If the specified maximum value is less than the minimum value, it
     * is changed to be the same as the minimum value. If the initial
     * value is lower than the minimum value, it is changed to be the
     * minimum value; if it is greater than the maximum value, it is
     * changed to be the maximum value.
     * @param     orientation   indicates the orientation of the scroll bar.
     * @param     value     the initial value of the scroll bar.
     * @param     visible   the size of the scroll bar's bubble, representing
     *                      the visible portion; the scroll bar uses this
                            value when paging up or down by a page.
     * @param     minimum   the minimum value of the scroll bar.
     * @param     maximum   the maximum value of the scroll bar.
     */
    public Scrollbar(int orientation, int value, int visible, int minimum, int maximum) {
	switch (orientation) {
	  case HORIZONTAL:
	  case VERTICAL:
	    this.orientation = orientation;
	    break;
	  default:
	    throw new IllegalArgumentException("illegal scrollbar orientation");
	}
	setValues(value, visible, minimum, maximum);
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
     * Creates the Scrollbar's peer.  The peer allows you to modify
     * the appearance of the Scrollbar without changing any of its
     * functionality.
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
	    if (peer == null) 
	        peer = getToolkit().createScrollbar(this);
	    super.addNotify();
	}
    }

    /**
     * Determines the orientation of this scroll bar.
     * @return    the orientation of this scroll bar, either
     *               <code>Scrollbar.HORIZONTAL</code> or
     *               <code>Scrollbar.VERTICAL</code>.
     * @see       java.awt.Scrollbar#setOrientation
     */
    public int getOrientation() {
	return orientation;
    }

    /**
     * Sets the orientation for this scroll bar.
     * @param     the orientation of this scroll bar, either
     *               <code>Scrollbar.HORIZONTAL</code> or
     *               <code>Scrollbar.VERTICAL</code>.
     * @see       java.awt.Scrollbar#getOrientation
     * @exception   IllegalArgumentException  if the value supplied
     *                   for <code>orientation</code> is not a
     *                   legal value.
     * @since     JDK1.1
     */
    public void setOrientation(int orientation) {
        synchronized (getTreeLock()) {
	    if (orientation == this.orientation) {
	        return;
	    }
	    switch (orientation) {
	        case HORIZONTAL:
	        case VERTICAL:
		    this.orientation = orientation;
		    break;
	        default:
		    throw new IllegalArgumentException("illegal scrollbar orientation");
	    }
	    /* Create a new peer with the specified orientation. */
	    if (peer != null) {
		removeNotify();
		addNotify();
		invalidate();
	    }
	}
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                    ((orientation == VERTICAL)
                     ? AccessibleState.HORIZONTAL : AccessibleState.VERTICAL),
                    ((orientation == VERTICAL) 
                     ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL));
        }
    }

    /**
     * Gets the current value of this scroll bar.
     * @return      the current value of this scroll bar.
     * @see         java.awt.Scrollbar#getMinimum
     * @see         java.awt.Scrollbar#getMaximum
     */
    public int getValue() {
	return value;
    }

    /**
     * Sets the value of this scroll bar to the specified value.
     * <p>
     * If the value supplied is less than the current minimum or
     * greater than the current maximum, then one of those values
     * is substituted, as appropriate.
     * <p>
     * Normally, a program should change a scroll bar's
     * value only by calling <code>setValues</code>.
     * The <code>setValues</code> method simultaneously
     * and synchronously sets the minimum, maximum, visible amount,
     * and value properties of a scroll bar, so that they are
     * mutually consistent.
     * @param       newValue   the new value of the scroll bar.
     * @see         java.awt.Scrollbar#setValues
     * @see         java.awt.Scrollbar#getValue
     * @see         java.awt.Scrollbar#getMinimum
     * @see         java.awt.Scrollbar#getMaximum
     */
    public void setValue(int newValue) {
	/* Use setValues so that a consistent policy
    	 * relating minimum, maximum, and value is enforced.
    	 */
    	setValues(newValue, visibleAmount, minimum, maximum);
    }

    /**
     * Gets the minimum value of this scroll bar.
     * @return      the minimum value of this scroll bar.
     * @see         java.awt.Scrollbar#getValue
     * @see         java.awt.Scrollbar#getMaximum
     */
    public int getMinimum() {
	return minimum;
    }

    /**
     * Sets the minimum value of this scroll bar.
     * <p>
     * Normally, a program should change a scroll bar's minimum
     * value only by calling <code>setValues</code>.
     * The <code>setValues</code> method simultaneously
     * and synchronously sets the minimum, maximum, visible amount,
     * and value properties of a scroll bar, so that they are
     * mutually consistent.
     * @param       newMinimum   the new minimum value
     *                     for this scroll bar.
     * @see         java.awt.Scrollbar#setValues
     * @see         java.awt.Scrollbar#setMaximum
     * @since       JDK1.1
     */
    public void setMinimum(int newMinimum) {
	/* Use setValues so that a consistent policy
    	 * relating minimum, maximum, and value is enforced.
    	 */
	setValues(value, visibleAmount, newMinimum, maximum);
    }

    /**
     * Gets the maximum value of this scroll bar.
     * @return      the maximum value of this scroll bar.
     * @see         java.awt.Scrollbar#getValue
     * @see         java.awt.Scrollbar#getMinimum
     */
    public int getMaximum() {
	return maximum;
    }

    /**
     * Sets the maximum value of this scroll bar.
     * <p>
     * Normally, a program should change a scroll bar's maximum
     * value only by calling <code>setValues</code>.
     * The <code>setValues</code> method simultaneously
     * and synchronously sets the minimum, maximum, visible amount,
     * and value properties of a scroll bar, so that they are
     * mutually consistent.
     * @param       newMaximum   the new maximum value
     *                     for this scroll bar.
     * @see         java.awt.Scrollbar#setValues
     * @see         java.awt.Scrollbar#setMinimum
     * @since       JDK1.1
     */
    public void setMaximum(int newMaximum) {
	/* Use setValues so that a consistent policy
    	 * relating minimum, maximum, and value is enforced.
    	 */
    	setValues(value, visibleAmount, minimum, newMaximum);
    }

    /**
     * Gets the visible amount of this scroll bar.
     * <p>
     * The visible amount of a scroll bar is the range of
     * values represented by the width of the scroll bar's
     * bubble. It is used to determine the scroll bar's
     * block increment.
     * @return      the visible amount of this scroll bar.
     * @see         java.awt.Scrollbar#setVisibleAmount
     * @since       JDK1.1
     */
    public int getVisibleAmount() {
	return getVisible();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getVisibleAmount()</code>.
     */
    public int getVisible() {
	return visibleAmount;
    }

    /**
     * Sets the visible amount of this scroll bar.
     * <p>
     * The visible amount of a scroll bar is the range of
     * values represented by the width of the scroll bar's
     * bubble. It is used to determine the scroll bar's
     * block increment.
     * <p>
     * Normally, a program should change a scroll bar's
     * value only by calling <code>setValues</code>.
     * The <code>setValues</code> method simultaneously
     * and synchronously sets the minimum, maximum, visible amount,
     * and value properties of a scroll bar, so that they are
     * mutually consistent.
     * @param       newAmount the amount visible per page.
     * @see         java.awt.Scrollbar#getVisibleAmount
     * @see         java.awt.Scrollbar#setValues
     * @since       JDK1.1
     */
    public void setVisibleAmount(int newAmount) {
    	setValues(value, newAmount, minimum, maximum);
    }

    /**
     * Sets the unit increment for this scroll bar.
     * <p>
     * The unit increment is the value that is added (subtracted)
     * when the user activates the unit increment area of the
     * scroll bar, generally through a mouse or keyboard gesture
     * that the scroll bar receives as an adjustment event.
     * @param        v  the amount by which to increment or decrement
     *                         the scroll bar's value.
     * @see          java.awt.Scrollbar#getUnitIncrement
     * @since        JDK1.1
     */
    public void setUnitIncrement(int v) {
	setLineIncrement(v);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setUnitIncrement(int)</code>.
     */
    public synchronized void setLineIncrement(int v) {
	lineIncrement = v;
	ScrollbarPeer peer = (ScrollbarPeer)this.peer;
	if (peer != null) {
	    peer.setLineIncrement(v);
	}
    }

    /**
     * Gets the unit increment for this scrollbar.
     * <p>
     * The unit increment is the value that is added (subtracted)
     * when the user activates the unit increment area of the
     * scroll bar, generally through a mouse or keyboard gesture
     * that the scroll bar receives as an adjustment event.
     * @return      the unit increment of this scroll bar.
     * @see         java.awt.Scrollbar#setUnitIncrement
     * @since       JDK1.1
     */
    public int getUnitIncrement() {
	return getLineIncrement();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getUnitIncrement()</code>.
     */
    public int getLineIncrement() {
	return lineIncrement;
    }

    /**
     * Sets the block increment for this scroll bar.
     * <p>
     * The block increment is the value that is added (subtracted)
     * when the user activates the block increment area of the
     * scroll bar, generally through a mouse or keyboard gesture
     * that the scroll bar receives as an adjustment event.
     * @param        v  the amount by which to increment or decrement
     *                         the scroll bar's value.
     * @see          java.awt.Scrollbar#getBlockIncrement
     * @since        JDK1.1
     */
    public void setBlockIncrement(int v) {
	setPageIncrement(v);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setBlockIncrement()</code>.
     */
    public synchronized void setPageIncrement(int v) {
	pageIncrement = v;
	ScrollbarPeer peer = (ScrollbarPeer)this.peer;
	if (peer != null) {
	    peer.setPageIncrement(v);
	}
    }

    /**
     * Gets the block increment of this scroll bar.
     * <p>
     * The block increment is the value that is added (subtracted)
     * when the user activates the block increment area of the
     * scroll bar, generally through a mouse or keyboard gesture
     * that the scroll bar receives as an adjustment event.
     * @return      the block increment of this scroll bar.
     * @see         java.awt.Scrollbar#setBlockIncrement
     * @since       JDK1.1
     */
    public int getBlockIncrement() {
	return getPageIncrement();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getBlockIncrement()</code>.
     */
    public int getPageIncrement() {
	return pageIncrement;
    }

    /**
     * Sets the values of four properties for this scroll bar.
     * <p>
     * This method simultaneously and synchronously sets the values
     * of four scroll bar properties, assuring that the values of
     * these properties are mutually consistent. It enforces the
     * constraints that maximum cannot be less than minimum, and that
     * value cannot be less than the minimum or greater than the maximum.
     * @param      value is the position in the current window.
     * @param      visible is the amount visible per page.
     * @param      minimum is the minimum value of the scroll bar.
     * @param      maximum is the maximum value of the scroll bar.
     */
    public void setValues(int value, int visible, int minimum, int maximum) {
        int oldValue;
        synchronized (this) {
            if (maximum <= minimum) {
                maximum = minimum + 1;
            }
            if (visible > maximum - minimum) {
                visible = maximum - minimum;
            }
            if (visible < 1) {
                visible = 1;
            }
            if (value < minimum) {
                value = minimum;
            }
            if (value > maximum - visible) {
                value = maximum - visible;
            }

            oldValue = this.value;
            this.value = value;
            this.visibleAmount = visible;
            this.minimum = minimum;
            this.maximum = maximum;
            ScrollbarPeer peer = (ScrollbarPeer)this.peer;
            if (peer != null) {
                peer.setValues(value, visibleAmount, minimum, maximum);
            }
        }

        if ((oldValue != value) && (accessibleContext != null))  {
            accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
                    new Integer(oldValue),
                    new Integer(value));
        }
    }

    /**
     * Adds the specified adjustment listener to receive instances of
     * <code>AdjustmentEvent</code> from this scroll bar.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param        l the adjustment listener.
     * @see          java.awt.event.AdjustmentEvent
     * @see          java.awt.event.AdjustmentListener
     * @see          java.awt.Scrollbar#removeAdjustmentListener
     * @since        JDK1.1
     */
    public synchronized void addAdjustmentListener(AdjustmentListener l) {
	if (l == null) {
	    return;
	}
	adjustmentListener = AWTEventMulticaster.add(adjustmentListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified adjustment listener so that it no longer
     * receives instances of <code>AdjustmentEvent</code> from this scroll bar.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param        	l    the adjustment listener.
     * @see          	java.awt.event.AdjustmentEvent
     * @see          	java.awt.event.AdjustmentListener
     * @see          	java.awt.Scrollbar#addAdjustmentListener
     * @since        	JDK1.1
     */
    public synchronized void removeAdjustmentListener(AdjustmentListener l) {
	if (l == null) {
	    return;
	}
	adjustmentListener = AWTEventMulticaster.remove(adjustmentListener, l);
    }

    /**
     * Return an array of all the listeners that were added to the Scrollbar
     * with addXXXListener(), where XXX is the name of the <code>listenerType</code>
     * argument.  For example, to get all of the AdjustmentListener(s) for the
     * given Scrollbar <code>s</code>, one would write:
     * <pre>
     * AdjustmentListener[] als = (AdjustmentListener[])(s.getListeners(AdjustmentListener.class))
     * </pre>
     * If no such listener list exists, then an empty array is returned.
     * 
     * @param    listenerType   Type of listeners requested
     * @return   all of the listeners of the specified type supported by this scrollbar
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	EventListener l = null; 
	if  (listenerType == AdjustmentListener.class) { 
	    l = adjustmentListener;
	} else {
	    return super.getListeners(listenerType);
	}
	return AWTEventMulticaster.getListeners(l, listenerType);
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        if (e.id == AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED) {
            if ((eventMask & AWTEvent.ADJUSTMENT_EVENT_MASK) != 0 ||
                adjustmentListener != null) {
                return true;
            }
            return false;
        }
        return super.eventEnabled(e);
    }

    /**
     * Processes events on this scroll bar. If the event is an
     * instance of <code>AdjustmentEvent</code>, it invokes the
     * <code>processAdjustmentEvent</code> method.
     * Otherwise, it invokes its superclass's
     * <code>processEvent</code> method.
     * @param        e the event.
     * @see          java.awt.event.AdjustmentEvent
     * @see          java.awt.Scrollbar#processAdjustmentEvent
     * @since        JDK1.1
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof AdjustmentEvent) {
            processAdjustmentEvent((AdjustmentEvent)e);
            return;
        }
	super.processEvent(e);
    }

    /**
     * Processes adjustment events occurring on this
     * scrollbar by dispatching them to any registered
     * <code>AdjustmentListener</code> objects.
     * <p>
     * This method is not called unless adjustment events are
     * enabled for this component. Adjustment events are enabled
     * when one of the following occurs:
     * <p><ul>
     * <li>An <code>AdjustmentListener</code> object is registered
     * via <code>addAdjustmentListener</code>.
     * <li>Adjustment events are enabled via <code>enableEvents</code>.
     * </ul><p>
     * @param       e the adjustment event.
     * @see         java.awt.event.AdjustmentEvent
     * @see         java.awt.event.AdjustmentListener
     * @see         java.awt.Scrollbar#addAdjustmentListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */
    protected void processAdjustmentEvent(AdjustmentEvent e) {
        if (adjustmentListener != null) {
            adjustmentListener.adjustmentValueChanged(e);
        }
    }

    /**
     * Returns the parameter string representing the state of
     * this scroll bar. This string is useful for debugging.
     * @return      the parameter string of this scroll bar.
     */
    protected String paramString() {
	return super.paramString() +
	    ",val=" + value +
	    ",vis=" + visibleAmount +
	    ",min=" + minimum +
	    ",max=" + maximum +
	    ((orientation == VERTICAL) ? ",vert" : ",horz");
    }


    /* Serialization support.
     */
    /**
     * The scrollbars serialized Data Version.
     *
     * @serial
     */
    private int scrollbarSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable ItemListener(s) as optional data.
     * The non-serializable ItemListner(s) are detected and
     * no attempt is made to serialize them.
     *
     * @serialData Null terminated sequence of 0 or more pairs.
     *             The pair consists of a String and Object.
     *             The String indicates the type of object and
     *             is one of the following :
     *             itemListenerK indicating and ItemListener object.
     *
     * @see AWTEventMulticaster.save(ObjectOutputStream, String, EventListener)
     * @see java.awt.Component.itemListenerK
     */
    private void writeObject(ObjectOutputStream s)
      throws IOException
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, adjustmentListenerK, adjustmentListener);
      s.writeObject(null);
    }

    /**
     * Read the ObjectInputStream and if it isnt null
     * add a listener to receive item events fired
     * by the Scrollbar.
     * Unrecognised keys or values will be Ignored.
     * 
     * @see removeActionListener()
     * @see addActionListener()
     */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException
    {
      s.defaultReadObject();

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
	String key = ((String)keyOrNull).intern();

	if (adjustmentListenerK == key)
	  addAdjustmentListener((AdjustmentListener)(s.readObject()));

	else // skip value for unrecognized key
	  s.readObject();
      }
    }


/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this Scrollbar. 
     * For scrollbars, the AccessibleContext takes the form of an 
     * AccessibleAWTScrollBar. 
     * A new AccessibleAWTScrollBar instance is created if necessary.
     *
     * @return an AccessibleAWTScrollBar that serves as the 
     *         AccessibleContext of this ScrollBar
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTScrollBar();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>Scrollbar</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to scrollbar user-interface elements.
     */
    protected class AccessibleAWTScrollBar extends AccessibleAWTComponent
        implements AccessibleValue {

        /**
         * Get the state set of this object.
         *
         * @return an instance of AccessibleState containing the current state 
         * of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            if (getOrientation() == VERTICAL) {
                states.add(AccessibleState.VERTICAL);
            } else {
                states.add(AccessibleState.HORIZONTAL);
            }
            return states;
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SCROLL_BAR;
        }

        /**
         * Get the AccessibleValue associated with this object.  In the
         * implementation of the Java Accessibility API for this class, 
	 * return this object, which is responsible for implementing the
         * AccessibleValue interface on behalf of itself.
	 * 
	 * @return this object
         */
        public AccessibleValue getAccessibleValue() {
            return this;
        }

        /**
         * Get the accessible value of this object.
         *
         * @return The current value of this object.
         */
        public Number getCurrentAccessibleValue() {
            return new Integer(getValue());
        }

        /**
         * Set the value of this object as a Number.
         *
         * @return True if the value was set.
         */
        public boolean setCurrentAccessibleValue(Number n) {
            if (n instanceof Integer) {
                setValue(n.intValue());
                return true;
            } else {
                return false;
            }
        }

        /**
         * Get the minimum accessible value of this object.
         *
         * @return The minimum value of this object.
         */
        public Number getMinimumAccessibleValue() {
            return new Integer(getMinimum());
        }

        /**
         * Get the maximum accessible value of this object.
         *
         * @return The maximum value of this object.
         */
        public Number getMaximumAccessibleValue() {
            return new Integer(getMaximum());
        }

    } // AccessibleAWTScrollBar

}
