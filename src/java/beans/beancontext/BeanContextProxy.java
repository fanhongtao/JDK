/*
 * @(#)BeanContextProxy.java	1.2 98/09/29
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

/**
 * <p>
 * This interface is implemented by a JavaBean that does not directly have 
 * a BeanContext(Child) associated with it (via implementing that interface or a subinterface thereof), but has a public BeanContext(Child) delegated from it.
 * For example, a subclass of java.awt.Container may have a BeanContext 
 * associated with it that all Component children of that Container shall
 * be contained within.
 * </p>
 * <p>
 * An Object may not implement this interface and the BeanContextChild interface
 * (or any subinterfaces thereof) they are mutually exclusive.
 * </p>
 * <p>
 * Callers of this interface shall examine the return type in order to 
 * obtain a particular subinterface of BeanContextChild as follows:
 * <code>
 * BeanContextChild bcc = o.getBeanContextProxy();
 *
 * if (bcc instanceof BeanContext) {
 * 	// ...
 * }
 * </code>
 * or
 * <code>
 * BeanContextChild bcc = o.getBeanContextProxy();
 * BeanContext      bc  = null;
 *
 * try {
 *     bc = (BeanContext)bcc; 
 * } catch (ClassCastException cce) {
 *     // cast failed, bcc is not an instanceof BeanContext 
 * }
 * </code>
 * </p>
 * <p>
 * The return value is a constant for the lifetime of the implementing
 * instance
 * </p>
 * @author Laurence P. G. Cable
 * @version 1.1
 * @since JDK1.2
 *
 * @seealso java.beans.beancontext.BeanContextChild
 * @seealso java.beans.beancontext.BeanContextChildSupport
 */

public interface BeanContextProxy {

    /**
     * @return the BeanContextChild (or subinterface) associated with this Object
     */

    BeanContextChild getBeanContextProxy();
}
