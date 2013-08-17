/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text.html;

import javax.swing.text.*;
import javax.swing.event.HyperlinkEvent;
import java.net.URL;

/**
 * HTMLFrameHyperlinkEvent is used to notify interested
 * parties that link was activated in a frame.
 *
 * @author Sunita Mani
 * @version 1.5, 02/06/02
 */

public class HTMLFrameHyperlinkEvent extends HyperlinkEvent {

    /**
     * Creates a new object representing a html frame 
     * hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param targetURL the affected URL
     * @param targetFrame the Frame to display the document in
     */
    public HTMLFrameHyperlinkEvent(Object source, EventType type, URL targetURL, 
				   String targetFrame) {
        super(source, type, targetURL);
	this.targetFrame = targetFrame;
	this.sourceElement = null;
    }


    /**
     * Creates a new object representing a hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param targetURL the affected URL
     * @param desc a description
     * @param targetFrame the Frame to display the document in
     */
    public HTMLFrameHyperlinkEvent(Object source, EventType type, URL targetURL, String desc,  
				   String targetFrame) {
        super(source, type, targetURL, desc);
	this.targetFrame = targetFrame;
	this.sourceElement = null;
    }

    /**
     * Creates a new object representing a hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param targetURL the affected URL
     * @param sourceElement the element that corresponds to the source
     *                      of the event
     * @param targetFrame the Frame to display the document in
     */
    public HTMLFrameHyperlinkEvent(Object source, EventType type, URL targetURL, 
				   Element sourceElement, String targetFrame) {
        super(source, type, targetURL);
	this.targetFrame = targetFrame;
	this.sourceElement = sourceElement;
    }


    /**
     * Creates a new object representing a hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param targetURL the affected URL
     * @param desc a desription
     * @param sourceElement the element that corresponds to the source
     *                      of the event
     * @param targetFrame the Frame to display the document in
     */
    public HTMLFrameHyperlinkEvent(Object source, EventType type, URL targetURL, String desc,  
				   Element sourceElement, String targetFrame) {
        super(source, type, targetURL, desc);
	this.targetFrame = targetFrame;
	this.sourceElement = sourceElement;
    }

    /**
     * returns the element that corresponds to the source of the
     * event.  This would always be the leaf element whose
     * tag is HTML.Tag.FRAME.
     */
    public Element getSourceElement() {
	return sourceElement;
    }

    /**
     * returns the target for the link.
     */
    public String getTarget() {
	return targetFrame;
    }

    private String targetFrame;
    private Element sourceElement;
}
