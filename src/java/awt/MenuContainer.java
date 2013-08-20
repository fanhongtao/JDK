/*
 * @(#)MenuContainer.java	1.16 04/05/18
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

/**
 * The super class of all menu related containers.
 *
 * @version 	1.16, 05/18/04
 * @author 	Arthur van Hoff
 */

public interface MenuContainer {
    Font getFont();
    void remove(MenuComponent comp);

    /**
     * @deprecated As of JDK version 1.1
     * replaced by dispatchEvent(AWTEvent).
     */
    @Deprecated
    boolean postEvent(Event evt);
}
