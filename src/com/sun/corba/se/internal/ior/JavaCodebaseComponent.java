/*
 * @(#)JavaCodebaseComponent.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/JavaCodebaseComponent.java

package com.sun.corba.se.internal.ior;

import org.omg.IOP.TAG_JAVA_CODEBASE ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.ior.TaggedComponentBase ;

/**
 * @author 
 */
public class JavaCodebaseComponent extends TaggedComponentBase 
{
    private String URLs ;

    public boolean equals( Object obj )
    {
	if (obj == null)
	    return false ;

	if (!(obj instanceof JavaCodebaseComponent))
	    return false ;

	JavaCodebaseComponent other = (JavaCodebaseComponent)obj ;

	return URLs.equals( other.getURLs() ) ;
    }

    public String toString()
    {
	return "JavaCodebaseComponent[URLs=" + URLs + "]" ;
    }

    public String getURLs() 
    {
	return URLs ;
    }

    /**
     * @return 
     * @exception 
     * @author 
     * @roseuid 3910984E02E9
     */
    public JavaCodebaseComponent( String URLs ) 
    {
	this.URLs = URLs ;
    }
    
    /**
     * @param arg0
     * @return void
     * @exception 
     * @author 
     * @roseuid 3913261302A3
     */
    public void writeContents(OutputStream os) 
    {
	os.write_string( URLs ) ;
    }
    
    /**
     * @return int
     * @exception 
     * @author 
     * @roseuid 391326140150
     */
    public int getId() 
    {
	return TAG_JAVA_CODEBASE.value ; // 25 in CORBA 2.3.1 13.6.3
    }
}
