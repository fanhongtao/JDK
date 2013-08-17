/*
 * @(#)HyperlinkEvent.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.event;

import java.util.EventObject;
import java.net.URL;


/**
 * HyperlinkEvent is used to notify interested parties that 
 * something has happened with respect to a hypertext link.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.9 08/28/98
 * @author  Timothy Prinzing
 */
public class HyperlinkEvent extends EventObject {

    /**
     * Creates a new object representing a hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param u the affected URL
     */
    public HyperlinkEvent(Object source, EventType type, URL u) {
        super(source);
	this.type = type;
	this.u = u;
    }

    /**
     * Creates a new object representing a hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param u the affected URL
     */
    public HyperlinkEvent(Object source, EventType type, URL u, String desc) {
        super(source);
	this.type = type;
	this.u = u;
	this.desc = desc;
    }

    /**
     * Gets the type of event.
     *
     * @return the type
     */
    public EventType getEventType() {
	return type;
    }

    /**
     * Get the description of the link as a string.
     * This may be useful if a URL can't be formed
     * from the description, in which case the associated
     * URL would be null.
     */
    public String getDescription() {
	return desc;
    }
	
    /**
     * Gets the URL that the link refers to.
     *
     * @return the URL
     */
    public URL getURL() {
	return u;
    }

    private EventType type;
    private URL u;
    private String desc;

	
    /**
     * Defines the ENTERED, EXITED, and ACTIVATED event types, along
     * with their string representations, returned by toString().
     */
    public static final class EventType {

        private EventType(String s) {
	    typeString = s;
	}

        /**
         * Entered type.
         */
	public static final EventType ENTERED = new EventType("ENTERED");

        /**
         * Exited type.
         */
	public static final EventType EXITED = new EventType("EXITED");

        /**
         * Activated type.
         */
	public static final EventType ACTIVATED = new EventType("ACTIVATED");

        /**
         * Converts the type to a string.
         *
         * @return the string
         */
        public String toString() {
	    return typeString;
	}

	private String typeString;
    }
}

