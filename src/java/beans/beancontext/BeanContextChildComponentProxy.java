/*
 * @(#)BeanContextChildComponentProxy.java	1.1 98/07/21
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
