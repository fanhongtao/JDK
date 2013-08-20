/*
 * @(#)JavaCodebaseComponentImpl.java	1.20 04/06/21
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.ior.iiop;

import org.omg.IOP.TAG_JAVA_CODEBASE ;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.se.spi.ior.TaggedComponentBase ;

import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent ;

/**
 * @author 
 */
public class JavaCodebaseComponentImpl extends TaggedComponentBase 
    implements JavaCodebaseComponent
{
    private String URLs ;

    public boolean equals( Object obj )
    {
	if (obj == null)
	    return false ;

	if (!(obj instanceof JavaCodebaseComponentImpl))
	    return false ;

	JavaCodebaseComponentImpl other = (JavaCodebaseComponentImpl)obj ;

	return URLs.equals( other.getURLs() ) ;
    }

    public int hashCode()
    {
	return URLs.hashCode() ;
    }

    public String toString()
    {
	return "JavaCodebaseComponentImpl[URLs=" + URLs + "]" ;
    }

    public String getURLs() 
    {
	return URLs ;
    }

    public JavaCodebaseComponentImpl( String URLs ) 
    {
	this.URLs = URLs ;
    }
    
    public void writeContents(OutputStream os) 
    {
	os.write_string( URLs ) ;
    }
    
    public int getId() 
    {
	return TAG_JAVA_CODEBASE.value ; // 25 in CORBA 2.3.1 13.6.3
    }
}
