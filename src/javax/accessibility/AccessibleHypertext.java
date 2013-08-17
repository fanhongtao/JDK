/*
 * @(#)AccessibleHypertext.java	1.4 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.accessibility;


import java.lang.*;
import java.util.*;
import java.awt.*;
import javax.swing.text.*;


/**
 * <P>The AccessibleHypertext class is the base class for all
 * classes that present hypertext information on the display.  This class
 * provides the standard mechanism for an assistive technology to access
 * that text via its content, attributes, and spatial location.
 * It also provides standard mechanisms for manipulating hyperlinks.
 * Applications can determine if an object supports the AccessibleHypertext
 * interface by first obtaining its AccessibleContext (see {@link Accessible})
 * and then calling the {@link AccessibleContext#getAccessibleText}
 * method of AccessibleContext.  If the return value is a class which extends
 * AccessibleHypertext, then that object supports AccessibleHypertext.
 *
 * @see Accessible
 * @see Accessible#getAccessibleContext
 * @see AccessibleContext
 * @see AccessibleText
 * @see AccessibleContext#getAccessibleText
 *
 * @version
 * @author	Peter Korn
 */
public interface AccessibleHypertext extends AccessibleText {

    /**
     * Returns the number of links within this hypertext doc.
     *
     * @return number of links in this hypertext doc.
     */
    public abstract int getLinkCount();

    /**
     * Returns the nth Link of this Hypertext document.
     *
     * @param linkIndex within the links of this Hypertext
     * @return Link object encapsulating the nth link(s)
     */
    public abstract AccessibleHyperlink getLink(int linkIndex);

    /**
     * Returns the index into an array of hyperlinks that
     * is associated with this character index, or -1 if there
     * is no hyperlink associated with this index.
     *
     * @param charIndex index within the text
     * @return index into the set of hyperlinks for this hypertext doc.
     */
    public abstract int getLinkIndex(int charIndex);
}
