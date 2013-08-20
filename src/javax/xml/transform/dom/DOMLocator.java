/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)DOMLocator.java	1.10 03/12/19
 */
package javax.xml.transform.dom;

import javax.xml.transform.SourceLocator;

import org.w3c.dom.Node;


/**
 * Indicates the position of a node in a source DOM, intended
 * primarily for error reporting.  To use a DOMLocator, the receiver of an
 * error must downcast the {@link javax.xml.transform.SourceLocator}
 * object returned by an exception. A {@link javax.xml.transform.Transformer}
 * may use this object for purposes other than error reporting, for instance,
 * to indicate the source node that originated a result node.
 */
public interface DOMLocator extends SourceLocator {

    /**
     * Return the node where the event occurred.
     *
     * @return The node that is the location for the event.
     */
    public Node getOriginatingNode();
}

