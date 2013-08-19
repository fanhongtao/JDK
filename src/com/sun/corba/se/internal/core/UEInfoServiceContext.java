/*
 * @(#)UEInfoServiceContext.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import java.io.Serializable ;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.internal.core.ServiceContext ;

public class UEInfoServiceContext extends ServiceContext {
    public UEInfoServiceContext( Throwable ex )
    {
	unknown = ex ;
    }

    public UEInfoServiceContext(InputStream is, GIOPVersion gv)
    {
	super(is, gv) ;

	try { 
	    unknown = (Throwable) in.read_value() ;
	} catch (ThreadDeath d) {
	    throw d ;
	} catch (Throwable e) {
	    unknown = new UNKNOWN( 0, CompletionStatus.COMPLETED_MAYBE ) ;
	}
    }

    // Required SERVICE_CONTEXT_ID and getId definitions
    public static final int SERVICE_CONTEXT_ID = 9 ;
    public int getId() { return SERVICE_CONTEXT_ID ; }

    public void writeData( OutputStream os ) throws SystemException
    {
	os.write_value( (Serializable)unknown ) ;
    }

    public Throwable getUE() { return unknown ; } 

    private Throwable unknown = null ;

    public String toString()
    {
	return "UEInfoServiceContext[ unknown=" + unknown.toString() + " ]" ;
    }
}


