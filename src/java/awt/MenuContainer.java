/*
 * @(#)MenuContainer.java	1.2 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.awt;

/**
 * The super class of all menu related containers.
 *
 * @version 	1.9, 09/21/98
 * @author 	Arthur van Hoff
 */

public interface MenuContainer {
    Font getFont();
    void remove(MenuComponent comp);

    /**
     * @deprecated As of JDK version 1.1
     * replaced by dispatchEvent(AWTEvent).
     */
    boolean postEvent(Event evt);
}
