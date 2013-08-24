/*
 * @(#)BeanContextContainerProxy.java	1.11 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans.beancontext;

import java.awt.Container;

/**
 * <p>
 * This interface is implemented by BeanContexts' that have an AWT Container
 * associated with them.
 * </p>
 *
 * @author Laurence P. G. Cable
 * @version 1.11, 11/17/05
 * @since 1.2
 *
 * @see java.beans.beancontext.BeanContext
 * @see java.beans.beancontext.BeanContextSupport
 */

public interface BeanContextContainerProxy {

    /**
     * Gets the <code>java.awt.Container</code> associated 
     * with this <code>BeanContext</code>.
     * @return the <code>java.awt.Container</code> associated 
     * with this <code>BeanContext</code>.
     */
    Container getContainer();
}
