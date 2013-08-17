/*
 * @(#)Scrollbar.java	1.55 98/07/01
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

import java.awt.peer.ScrollbarPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * The <code>Scrollbar</code> class embodies a scroll bar, a 
 * familiar user-interface object. A scroll bar provides a 
 * convenient means for allowing a user to select from a 
 * range of values. The following three vertical
 * scroll bars could be used as slider controls to pick 
 * the red, green, and blue components of a color:
 * <p>
 * <img src="images-awt/Scrollbar-1.gif"
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
 * <img src="images-awt/Scrollbar-2.gif" 
 * ALIGN=center HSPACE=10 VSPACE=7> 
 * <p>
 * The value range represented by the bubble is the <em>visible</em> 
 * range of the scroll bar. The horizontal scroll bar in this 
 * example could be created with code like the following: 
 * <p>
 * <hr><blockquote><pre>
 * ranger = new Scrollbar(Scrollbar.HORIZONTAL, 0, 64, 0, 255);
 * add(ranger);
 * </pre></blockquote><hr>
 * <p>
 * Note that the maximum value above, 255, is the maximum value for 
 * the scroll bar's bubble. The actual width of the 
 * scroll bar's track is 255&nbsp;+&nbsp;64. When the scroll bar
 * is set to its maximum value, the left side of the bubble
 * is at 255, and the right side is at 255&nbsp;+&nbsp;64.
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
 * compatibility, but its use with newer versions of JDK is 
 * discouraged. The fives types of adjustment event introduced
 * with JDK&nbsp;1.1 correspond to the five event types 
 * that are associated with scroll bars in previous JDK versions.
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
 * @version	1.55 07/01/98
 * @author 	Sami Shaio
 * @see         java.awt.event.AdjustmentEvent
 * @see         java.awt.event.AdjustmentListener
 * @since       JDK1.0
 */
public class Scrollbar extends Component implements Adjustable {
  
    /**
     * A constant that indicates a horizontal scroll bar.
     * @since     JDK1.0
     */
    public static final int	HORIZONTAL = 0;

    /**
     * A constant that indicates a vertical scroll bar.
     * @since     JDK1.0
     */
    public static final int	VERTICAL   = 1;

    /**
     * The value of the Scrollbar.
     */
    int	value;

    /**
     * The maximum value of the Scrollbar.
     */
    int	maximum;

    /**
     * The minimum value of the Scrollbar.
     */
    int	minimum;

    /**
     * The size of the visible portion of the Scrollbar.
     */
    int	visibleAmount;

    /**
     * The Scrollbar's orientation--being either horizontal or vertical.
     */
    int	orientation;

    /**
     * The amount by which the scrollbar value will change when going
     * up or down by a line.
     */
    int lineIncrement = 1;

    /**
     * The amount by which the scrollbar value will change when going
     * up or down by a page.
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
     * Constructs a new vertical scroll bar.
     * @since   JDK1.0
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
     * @since       JDK1.0
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
     * @since     JDK1.0
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
        return base + nameCounter++;
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
     * @since     JDK1.0
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
    }

    /**
     * Gets the current value of this scroll bar.
     * @return      the current value of this scroll bar.
     * @see         java.awt.Scrollbar#getMinimum
     * @see         java.awt.Scrollbar#getMaximum
     * @since       JDK1.0
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
     * @since       JDK1.0
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
     * @since       JDK1.0
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
     * @since       JDK1.0
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
     * @see         java.awtScrollbar#setValues
     * @see         java.awtScrollbar#setMinimum
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
     * @since      JDK1.0
     */
    public synchronized void setValues(int value, int visible, int minimum, int maximum) {
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

	this.value = value;
	this.visibleAmount = visible;
	this.minimum = minimum;
	this.maximum = maximum;
	ScrollbarPeer peer = (ScrollbarPeer)this.peer;
	if (peer != null) {
	    peer.setValues(value, visibleAmount, minimum, maximum);
	}
    }

    /**
     * Adds the specified adjustment listener to receive instances of 
     * <code>AdjustmentEvent</code> from this scroll bar.
     * @param        l the adjustment listener.
     * @see          java.awt.event.AdjustmentEvent
     * @see          java.awt.event.AdjustmentListener
     * @see          java.awt.Scrollbar#removeAdjustmentListener
     * @since        JDK1.1
     */ 
    public synchronized void addAdjustmentListener(AdjustmentListener l) {
	adjustmentListener = AWTEventMulticaster.add(adjustmentListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified adjustment listener so that it no longer 
     * receives instances of <code>AdjustmentEvent</code> from this scroll bar.
     * @param        l    the adjustment listener.
     * @see          java.awt.event.AdjustmentEvent
     * @see          java.awt.event.AdjustmentListener
     * @see          java.awt.Scrollbar#addAdjustmentListener
     * @since        JDK1.1
     */ 
    public synchronized void removeAdjustmentListener(AdjustmentListener l) {
	adjustmentListener = AWTEventMulticaster.remove(adjustmentListener, l);
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
     * @since       JDK1.0
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

    private int scrollbarSerializedDataVersion = 1;

    private void writeObject(ObjectOutputStream s)
      throws IOException 
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, adjustmentListenerK, adjustmentListener);
      s.writeObject(null);
    }

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

}
