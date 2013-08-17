/*
 * @(#)Remote.java	1.3 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.rmi;

/** 
 * The Remote interface serves to identify all remote objects.
 * Any object that is a remote object must directly or indirectly implement
 * this interface.  Only those methods specified in a remote interface are 
 * available remotely. <p>
 * Implementation classes can implement any number of remote interfaces
 * and can extend other remote implementation classes.
 */

public interface Remote {}
