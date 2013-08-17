/*
 * @(#)BeanContextChildComponentProxy.java	1.2 00/01/12
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
 * This interface is implemented by BeanContextChildren that have an AWT Component
 * associated with them.
 * </p>
 *
 * @author Laurence P. G. Cable
 * @version 1.1
 * @since JDK1.2
 *
 * @seealso java.beans.beancontext.BeanContext
 * @seealso java.beans.beancontext.BeanContextSupport
 */

public interface BeanContextChildComponentProxy {

    /**
     * @return the AWT Component associated with this BeanContextChild
     */

    Component getComponent();
}
