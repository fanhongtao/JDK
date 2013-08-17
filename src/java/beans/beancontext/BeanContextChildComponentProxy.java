/*
 * @(#)BeanContextChildComponentProxy.java	1.6 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.beans.beancontext;

import java.awt.Component;

/**
 * <p>
 * This interface is implemented by 
 * <code>BeanContextChildren</code> that have an AWT <code>Component</code>
 * associated with them.
 * </p>
 *
 * @author Laurence P. G. Cable
 * @version 1.6, 02/02/00
 * @since 1.2
 *
 * @seealso java.beans.beancontext.BeanContext
 * @seealso java.beans.beancontext.BeanContextSupport
 */

public interface BeanContextChildComponentProxy {

    /**
     * Gets the <code>java.awt.Component</code> associated with 
     * this <code>BeanContextChild</code>.
     * @return the AWT <code>Component</code> associated with 
     * this <code>BeanContextChild</code>
     */

    Component getComponent();
}
