/*
 * @(#)file      PermissionImpl.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   4.11
 * @(#)date      06/11/29
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */


package com.sun.jmx.snmp.IPAcl;



import java.io.Serializable;


/**
 * Permission is represented as a String.
 *
 * @see java.security.acl.Permission
 * @version     4.11     11/17/05
 * @author      Sun Microsystems, Inc
 */

class PermissionImpl implements java.security.acl.Permission, Serializable {
  private String perm = null;
  
  /**
   * Constructs a permission.
   *
   * @param s the string representing the permission.
   */
  public PermissionImpl(String s) {
	perm = s;
  }
  
  public int hashCode() {
	return super.hashCode();	
  }
  
  /**
   * Returns true if the object passed matches the permission represented in. 
   *
   * @param p the Permission object to compare with. 
   * @return true if the Permission objects are equal, false otherwise.
   */
  public boolean equals(Object p){
	if (p instanceof PermissionImpl){
	  return perm.equals(((PermissionImpl)p).getString());
	} else {
	  return false;
	}
  }
  
  /**
   * Prints a string representation of this permission. 
   *
   * @return a string representation of this permission. 
   */
  public String toString(){
	return perm.toString();
  }
  
  /**
   * Prints the permission. 
   *
   * @return a string representation of this permission. 
   */
  public String getString(){
	return perm;
  }
}

