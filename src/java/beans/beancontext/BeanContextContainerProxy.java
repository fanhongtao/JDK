/*
 * @(#)BeanContextContainerProxy.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * @version 1.1
 * @since JDK1.2
 *
 * @seealso java.beans.beancontext.BeanContext
 * @seealso java.beans.beancontext.BeanContextSupport
 */

public interface BeanContextContainerProxy {

    /**
     * @return the AWT Container associated with this BeanContext
     */

    Container getContainer();
}
