/*
 * @(#)IdEncapsulationBase.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.ior.Writeable ;
import com.sun.corba.se.internal.core.GIOPVersion ;
import com.sun.corba.se.internal.corba.EncapsOutputStream ;
import com.sun.corba.se.internal.corba.EncapsInputStream ;
import com.sun.corba.se.internal.iiop.CDROutputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;

/** Provide support for properly reading and writing IdEncapsulation objects
* (tagged profiles and components).
*/
public abstract class IdEncapsulationBase implements Writeable 
{
    /** Write the data for this object as a CDR encapsulation.
    * This is used for writing tagged components and profiles.
    * These data types must be written out as encapsulations,
    * which means that we need to first write the data out to
    * an encapsulation stream, then extract the data and write
    * it to os as an array of octets.
    */
    final public void write( OutputStream os )
    {
	EncapsOutputStream out = new EncapsOutputStream( os.orb() ) ;

	out.putEndian() ;

	writeContents( out ) ;

	writeOutputStream( out, os ) ;
    }

    /** This method actually writes the real data to the encapsulation
    * stream.  It must be defined in a subclass.
    */
    abstract public void writeContents( OutputStream os ) ;

    /** Helper method that is used to extract data from an output
    * stream and write the data to another output stream.  Defined
    * as static so that it can be used in another class.
    */
    static public void writeOutputStream( CDROutputStream dataStream,
	OutputStream os ) 
    {
	byte[] data = dataStream.toByteArray() ;
	os.write_long( data.length ) ;
	os.write_octet_array( data, 0, data.length ) ;
    }

    /** Helper method to read the octet array from is, deencapsulate it, 
    * and return
    * as another InputStream.  This must be called inside the
    * constructor of a derived class to obtain the correct stream
    * for unmarshalling data.
    */
    static public InputStream getEncapsulationStream( InputStream is )
    {
	byte[] data = readOctets( is ) ;
	EncapsInputStream result = new EncapsInputStream( is.orb(), data, 
	    data.length ) ;
	result.consumeEndian() ;
	return result ;
    } 

    /** Helper method that reads an octet array from an input stream.
    * Defined as static here so that it can be used in another class.
    */
    static public byte[] readOctets( InputStream is ) 
    {
	int len = is.read_ulong() ;
	byte[] data = new byte[len] ;
	is.read_octet_array( data, 0, len ) ;
	return data ;
    }
}
