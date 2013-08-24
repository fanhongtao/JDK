/*
 * $Id: Text.java,v 1.2 2004/04/02 01:24:19 ofung Exp $
 * $Revision: 1.2 $
 * $Date: 2004/04/02 01:24:19 $
 */

/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.xml.soap;

/**
 * A representation of a node whose value is text.  A <code>Text</code> object
 * may represent text that is content or text that is a comment.
 *
 */
public interface Text extends Node, org.w3c.dom.Text {

    /**
     * Retrieves whether this <code>Text</code> object represents a comment.
     *
     * @return <code>true</code> if this <code>Text</code> object is a
     *         comment; <code>false</code> otherwise
     */
    public boolean isComment();
}
