/*
 * @(#)BeanContextChildComponentProxy.java	1.2 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.2
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
