/*
 * @(#)Scrollbar.java	1.49 97/03/03
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

import java.awt.peer.ScrollbarPeer;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A Scrollbar component which implements the Adjustable interface.
 *
 * @version	1.49 03/03/97
 * @author 	Sami Shaio
 */
public class Scrollbar extends Component implements Adjustable {
  
    /**
     * The horizontal Scrollbar variable.
     */
    public static final int	HORIZONTAL = 0;

    /**
     * The vertical Scrollbar variable.
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
     * Constructs a new vertical Scrollbar.
     */
    public Scrollbar() {
	this(VERTICAL, 0, 10, 0, 100);
    }

    /**
     * Constructs a new Scrollbar with the specified orientation.
     * @param orientation either Scrollbar.HORIZONTAL or Scrollbar.VERTICAL
     * @exception IllegalArgumentException When an illegal scrollbar orientation is given.
     */
    public Scrollbar(int orientation) {
        this(orientation, 0, 10, 0, 100);
    }

    /**
     * Constructs a new Scrollbar with the specified orientation,
     * value, page size,  and minumum and maximum values.
     * @param orientation either Scrollbar.HORIZONTAL or Scrollbar.VERTICAL
     * @param value the scrollbar's value
     * @param visible the size of the visible portion of the
     * scrollable area. The scrollbar will use this value when paging up
     * or down by a page.
     * @param minimum the minimum value of the scrollbar
     * @param maximum the maximum value of the scrollbar
     */
    public Scrollbar(int orientation, int value, int visible, int minimum, int maximum) {
	this.name = base + nameCounter++;
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
     * Creates the Scrollbar's peer.  The peer allows you to modify
     * the appearance of the Scrollbar without changing any of its
     * functionality.
     */

    public void addNotify() {
	peer = getToolkit().createScrollbar(this);
	super.addNotify();
    }

    /**
     * Returns the orientation for this Scrollbar.
     */
    public int getOrientation() {
	return orientation;
    }

    /**
     * Sets the orientation for this Scrollbar.
     * @param orientation  the orientation (HORIZONTAL or VERTICAL) of
     * this scrollbar.
     */
    public synchronized void setOrientation(int orientation) {
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
	synchronized (Component.LOCK) {
	    if (peer != null) {
		removeNotify();
		addNotify();
		invalidate();
	    }
	}
    }

    /**
     * Returns the current value of this Scrollbar.
     * @see #getMinimum
     * @see #getMaximum
     */
    public int getValue() {
	return value;
    }

    /**
     * Sets the value of this Scrollbar to the specified value.
     * @param value the new value of the Scrollbar. If this value is
     * below the current minimum or above the current maximum minus 
     * the visible amount, it becomes the new one of those values, 
     * respectively.
     * @see #getValue
     */
    public synchronized void setValue(int newValue) {
	/* Use setValues so that a consistent policy
    	 * relating minimum, maximum, and value is enforced.
    	 */
    	setValues(newValue, visibleAmount, minimum, maximum);
    }

    /**
     * Returns the minimum value of this Scrollbar.
     * @see #getMaximum
     * @see #getValue
     */
    public int getMinimum() {
	return minimum;
    }

    /**
     * Sets the minimum value for this Scrollbar.
     * @param minimum the minimum value of the scrollbar
     */
    public synchronized void setMinimum(int newMinimum) {
	/* Use setValues so that a consistent policy
    	 * relating minimum, maximum, and value is enforced.
    	 */
	setValues(value, visibleAmount, newMinimum, maximum);
    }

    /**
     * Returns the maximum value of this Scrollbar.
     * @see #getMinimum
     * @see #getValue
     */
    public int getMaximum() {
	return maximum;
    }

    /**
     * Sets the maximum value for this Scrollbar.
     * @param maximum the maximum value of the scrollbar
     */
    public synchronized void setMaximum(int newMaximum) {
	/* Use setValues so that a consistent policy
    	 * relating minimum, maximum, and value is enforced.
    	 */
    	setValues(value, visibleAmount, minimum, newMaximum);
    }

    /**
     * Returns the visible amount of this Scrollbar.
     */
    public int getVisibleAmount() {
	return getVisible();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getVisibleAmount().
     */
    public int getVisible() {
	return visibleAmount;
    }

    /**
     * Sets the visible amount of this Scrollbar, which is the range
     * of values represented by the width of the scroll bar's bubble.
     * @param visible the amount visible per page
     */
    public synchronized void setVisibleAmount(int newAmount) {
    	setValues(value, newAmount, minimum, maximum);
    }

    /**
     * Sets the unit increment for this scrollbar. This is the value
     * that will be added (subtracted) when the user hits the unit down
     * (up) gadgets.
     */
    public synchronized void setUnitIncrement(int v) {
	setLineIncrement(v);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setUnitIncrement(int).
     */
    public void setLineIncrement(int v) {
	lineIncrement = v;
	ScrollbarPeer peer = (ScrollbarPeer)this.peer;
	if (peer != null) {
	    peer.setLineIncrement(v);
	}
    }

    /**
     * Gets the unit increment for this scrollbar.
     */
    public int getUnitIncrement() {
	return getLineIncrement();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getUnitIncrement().
     */
    public int getLineIncrement() {
	return lineIncrement;
    }

    /**
     * Sets the block increment for this scrollbar. This is the value
     * that will be added (subtracted) when the user hits the block down
     * (up) gadgets.
     */
    public synchronized void setBlockIncrement(int v) {
	setPageIncrement(v);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setBlockIncrement().
     */
    public void setPageIncrement(int v) {
	pageIncrement = v;
	ScrollbarPeer peer = (ScrollbarPeer)this.peer;
	if (peer != null) {
	    peer.setPageIncrement(v);
	}
    }

    /**
     * Gets the block increment for this scrollbar.
     */
    public int getBlockIncrement() {
	return getPageIncrement();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getBlockIncrement().
     */
    public int getPageIncrement() {
	return pageIncrement;
    }

    /**
     * Sets the values for this Scrollbar.
     * This method enforces the following constraints:
     * <UL>
     * <LI> The maximum must be greater than the minimum </LI>
     * <LI> The value must be greater than or equal to the minumum 
     *      and less than or equal to the maximum minus the 
     *      visible amount </LI>  
     * <LI> The visible amount must be greater than 1 and less than or equal
     *      to the difference between the maximum and minimum values. </LI>
     * </UL>
     * Values which do not meet these criteria are quietly coerced to the
     * appropriate boundary value.
     * @param value is the position in the current window.
     * @param visible is the amount visible per page
     * @param minimum is the minimum value of the scrollbar
     * @param maximum is the maximum value of the scrollbar
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
     * Adds the specified adjustment listener  to recieve adjustment events 
     * from this scrollbar.
     * @param l the adjustment listener
     */ 
    public synchronized void addAdjustmentListener(AdjustmentListener l) {
	adjustmentListener = AWTEventMulticaster.add(adjustmentListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified adjustment listener so that it no longer
     * receives adjustment events from this scrollbar..
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
     * Processes events on this scrollbar. If the event is an 
     * AdjustmentEvent, it invokes the processAdjustmentEvent method,
     * else it invokes its superclass's processEvent.
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof AdjustmentEvent) {
            processAdjustmentEvent((AdjustmentEvent)e);
            return;
        }
	super.processEvent(e);
    }

    /** 
     * Processes adjustment events occurring on this scrollbar by
     * dispatching them to any registered AdjustmentListener objects.
     * NOTE: This method will not be called unless adjustment events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) An AdjustmentListener object is registered via addAdjustmentListener()
     * b) Adjustment events are enabled via enableEvents()
     * @see Component#enableEvents
     * @param e the adjustment event
     */ 
    protected void processAdjustmentEvent(AdjustmentEvent e) {
        if (adjustmentListener != null) {
            adjustmentListener.adjustmentValueChanged(e);
        }
    }

    /**
     * Returns the String parameters for this Scrollbar.
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
