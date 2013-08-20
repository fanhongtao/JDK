/*
 * @(#)CertPathParameters.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.cert;

/**
 * A specification of certification path algorithm parameters.
 * The purpose of this interface is to group (and provide type safety for) 
 * all <code>CertPath</code> parameter specifications. All 
 * <code>CertPath</code> parameter specifications must implement this 
 * interface.  
 *
 * @version 	1.5 12/19/03
 * @author	Yassir Elley
 * @see 	CertPathValidator#validate(CertPath, CertPathParameters)
 * @see 	CertPathBuilder#build(CertPathParameters)
 * @since	1.4
 */
public interface CertPathParameters extends Cloneable {

  /**
   * Makes a copy of this <code>CertPathParameters</code>. Changes to the
   * copy will not affect the original and vice versa.
   *
   * @return a copy of this <code>CertPathParameters</code>
   */
  Object clone();
}
