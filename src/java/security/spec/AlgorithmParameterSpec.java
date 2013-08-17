/*
 * @(#)AlgorithmParameterSpec.java	1.7 98/12/03
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.security.spec;

/**
 * A (transparent) specification of cryptographic parameters.
 *
 * <P> This interface contains no methods or constants. Its only purpose
 * is to group (and provide type safety for) all parameter specifications.
 * All parameter specifications must implement this interface.
 * 
 * @author Jan Luehe
 *
 * @version 1.7 98/12/03
 *
 * @see java.security.AlgorithmParameters
 * @see DSAParameterSpec
 *
 * @since JDK1.2
 */

public interface AlgorithmParameterSpec { }
