/*
 * @(#)IDLEntity.java	1.7 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
