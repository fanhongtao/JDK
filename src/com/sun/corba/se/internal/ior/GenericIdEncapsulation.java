/*
 * @(#)GenericIdEncapsulation.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/GenericIdEncapsulation.java

package com.sun.corba.se.internal.ior;

import java.util.Arrays ;

import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

/**
 * @author 
 * This is used for unknown components and profiles.  A TAG_MULTICOMPONENT_PROFILE will be represented this way.
 */
public abstract class GenericIdEncapsulation implements IdEncapsulation 
{
    private int id;
    private byte data[];
    
    /**
     * @param arg0
     * @param arg1
     * @return 
     * @exception 
     * @author
     * @roseuid 3910984C00C0
     */
    public GenericIdEncapsulation(int id, InputStream is) 
    {
	this.id = id ;
	data = IdEncapsulationBase.readOctets( is ) ;
    }
    
    /**
     * @return int
     * @exception 
     * @author
     * @roseuid 3910984C00C5
     */
    public int getId() 
    {
	return id ;
    }
    
    /**
     * @param arg0
     * @return void
     * @exception 
     * @author
     * @roseuid 3910984C00C6
     */
    public void write(OutputStream os) 
    {
	os.write_ulong( data.length ) ;
	os.write_octet_array( data, 0, data.length ) ;
    }
    
    /**
     * @return String
     * @exception 
     * @author 
     * @roseuid 3980750400B1
     */
    public String toString() 
    {
	return "GenericIdEncapsulation[id=" + getId() + "]" ;
    }
    
    /**
     * @param obj
     * @return boolean
     * @exception 
     * @author 
     * @roseuid 3980750400BB
     */
    public boolean equals(Object obj) 
    {
	if (obj == null)
	    return false ;

	if (!(obj instanceof GenericIdEncapsulation))
	    return false ;

	GenericIdEncapsulation encaps = (GenericIdEncapsulation)obj ;

	return (getId() == encaps.getId()) && 
	    Arrays.equals( getData(), encaps.getData() ) ;
    }
    
    /**
     * @param id
     * @param data
     * @return 
     * @exception 
     * @author 
     * @roseuid 3980750400CF
     */
    public GenericIdEncapsulation(int id, byte[] data) 
    {
	this.id = id ;
	this.data = (byte[])(data.clone()) ;
    }
    
    /**
     * @return byte[]
     * @exception 
     * @author 
     * @roseuid 39807504011F
     */
    public byte[] getData() 
    {
	return data ;
    }
}
