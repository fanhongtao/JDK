/*
 * @(#)IDLEntity.java	1.11 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA.portable;

/**
 * An interface with no members whose only purpose is to serve as a marker 
 * indicating  that an implementing class is a
 * Java value type from IDL that has a corresponding Helper class.
 * RMI IIOP serialization looks for such a marker to perform
 * marshalling/unmarshalling. 
 **/
public interface IDLEntity extends java.io.Serializable {

}
