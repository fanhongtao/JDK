/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.io.Serializable;

/**
 * This class encapsulates a single tab stop (basically as tab stops  
 * are thought of by RTF). A tab stop is at a specified distance from the
 * left margin, aligns text in a specified way, and has a specified leader.
 * TabStops are immutable, and usually contained in TabSets.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.15 02/06/02
 */
public class TabStop implements Serializable {

    /** Character following tab is positioned at location. */
    public static final int ALIGN_LEFT    = 0;
    /** Characters following tab are positioned such that all following
     * characters up to next tab/newline end at location. */
    public static final int ALIGN_RIGHT   = 1;
    /** Characters following tab are positioned such that all following
     * characters up to next tab/newline are centered around the tabs
     * location. */
    public static final int ALIGN_CENTER  = 2;
    /** Characters following tab are aligned such that next
     * decimal/tab/newline is at the tab location, very similiar to
     * RIGHT_TAB, just includes decimal as additional character to look for.
     */
    public static final int ALIGN_DECIMAL = 4;
    public static final int ALIGN_BAR     = 5;

    /* Bar tabs (whatever they are) are actually a separate kind of tab
       in the RTF spec. However, being a bar tab and having alignment
       properties are mutually exclusive, so the reader treats barness
       as being a kind of alignment. */

    public static final int LEAD_NONE      = 0;
    public static final int LEAD_DOTS      = 1;
    public static final int LEAD_HYPHENS   = 2;
    public static final int LEAD_UNDERLINE = 3;
    public static final int LEAD_THICKLINE = 4;
    public static final int LEAD_EQUALS    = 5;

    /** Tab type. */
    private int alignment;
    /** Location, from the left margin, that tab is at. */
    private float position;
    private int leader;

    /**
     * Creates a tab at position <code>pos</code> with a default alignment
     * and default leader.
     */
    public TabStop(float pos) {
        this(pos, ALIGN_LEFT, LEAD_NONE);
    }

    /**
     * Creates a tab with the specified position <code>pos</code>,
     * alignment <code>align</code> and leader <code>leader</code>.
     */
    public TabStop(float pos, int align, int leader) {
	alignment = align;
	this.leader = leader;
	position = pos;
    }

    /**
     * @return the postion of the tab.
     */
    public float getPosition() {
	return position;
    }

    /**
     * @return the alignment of the tab.
     */
    public int getAlignment() {
	return alignment;
    }

    /**
     * @return the leader of the tab.
     */
    public int getLeader() {
	return leader;
    }

    /**
     * Returns true if other equals the receiver.
     */
    public boolean equals(Object other)
    {
	if (other == this) {
	    return true;
	}
	if (other instanceof TabStop) {
	    TabStop o = (TabStop)other;
	    return ( (alignment == o.alignment) &&
		     (leader == o.leader) &&
		     (position == o.position) );  /* TODO: epsilon */
	}
	return false;
    }

    /**
     * Returns the hashCode for the object.  This must be defined
     * here to ensure 100% pure.
     *
     * @return the hashCode for the object
     */
    public int hashCode() { 
	return super.hashCode();
    }

    /* This is for debugging; perhaps it should be removed before release */
    public String toString() {
	String buf;
	switch(alignment) {
	  default:
	  case ALIGN_LEFT:
	    buf = "";
	    break;
	  case ALIGN_RIGHT:
	    buf = "right ";
	    break;
	  case ALIGN_CENTER:
	    buf = "center ";
	    break;
	  case ALIGN_DECIMAL:
	    buf = "decimal ";
	    break;
	  case ALIGN_BAR:
	    buf = "bar ";
	    break;
	}
	buf = buf + "tab @" + String.valueOf(position);
	if (leader != LEAD_NONE)
	    buf = buf + " (w/leaders)";
	return buf;
    }
}

