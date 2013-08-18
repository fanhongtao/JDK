/*
 * @(#)ServiceContext.java	1.26 02/07/30
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.iiop.CDRInputStream ;
import com.sun.corba.se.internal.corba.EncapsInputStream ;
import com.sun.corba.se.internal.corba.EncapsOutputStream ;
import com.sun.corba.se.internal.orbutil.ORBUtility ;

/** Base class for all ServiceContext classes.
* There is a derived ServiceContext class for each service context that
* the ORB supports.  Each subclass encapsulates the representation of
* the service context and provides any needed methods for manipulating
* the service context.  Each subclass must provide the following 
* members:
* <p>
* <ul>
* </li>a public static final int SERVICE_CONTEXT_ID that gives the OMG
* (or other) defined id for the service context.  This is needed for the
* registration mechanism defined in ServiceContexts. OMG defined 
* service context ids are taken from section 13.6.7 of ptc/98-12-04.</li>
* <li>a public constructor that takes an InputStream as its argument.</li>
* <li>Appropriate definitions of getId() and writeData().  getId() must
* return SERVICE_CONTEXT_ID.</li>
* </ul>
* <p>
* The subclass can be constructed either directly from the service context
* representation, or by reading the representation from an input stream.
* These cases are needed when the service context is created and written to
* the request or reply, and when the service context is read from the
* received request or reply.
*/
public abstract class ServiceContext {
    /** Simple default constructor used when subclass is constructed 
     * from its representation.
     */
    protected ServiceContext() { }

    private void dprint( String msg ) 
    {
	ORBUtility.dprint( this, msg ) ;
    }
    
    /** Stream constructor used when subclass is constructed from an
     * InputStream.  This constructor must be called by super( stream )
     * in the subclass.  After this constructor completes, the service
     * context representation can be read from in.
     * Note that the service context id has been consumed from the input
     * stream before this object is constructed.
     */
    protected ServiceContext(InputStream s, GIOPVersion gv) throws SystemException
    {
	com.sun.corba.se.internal.corba.ORB orb
            = (com.sun.corba.se.internal.corba.ORB)(((com.sun.corba.se.internal.iiop.CDRInputStream)s).orb()) ;

	if (orb.serviceContextDebugFlag)
	    dprint( "Reading service context from stream" ) ;

	// Read the encapsulated service context from the input stream
	int len = s.read_long();

	if (orb.serviceContextDebugFlag)
	    dprint( "Service context length = " + len ) ;

	byte[] data = new byte[len];
	s.read_octet_array(data,0,len);

	// Prepare for subclass to unmarshal value from the encapsulation
        //
        // Note:  As of Jan 2001, no standard OMG or Sun service contexts
        // ship wchar data or are defined as using anything but GIOP 1.0 CDR.
        // However, our current implementation will use the GIOP 1.2 CDR wchar
        // with UTF-16BE/LE when forced.  See CORBA formal 99-10-07 15.3.1.6.
    	EncapsInputStream cis = new EncapsInputStream(s.orb(), data, data.length);

	cis.consumeEndian() ;
	in = (InputStream)cis ;
    }

    /** Returns Service context id.  Must be overloaded in subclass.
     */
    public abstract int getId() ;

    /** Write the service context to an output stream.  This method 
     * must be used for writing the service context to a request or reply
     * header.
     */
    public void write(OutputStream s, GIOPVersion gv) throws SystemException
    {
        EncapsOutputStream os = new EncapsOutputStream(s.orb(), gv) ;
        os.putEndian() ;
        writeData( os ) ;
        byte[] data = os.toByteArray() ;

        s.write_long(getId());
        s.write_long(data.length);
        s.write_octet_array(data, 0, data.length);
    }


    /** Writes the data used to represent the subclasses service context
     * into an encapsulation stream.  Must be overloaded in subclass.
     */
    protected abstract void writeData( OutputStream os ) ;

    /** in is the stream containing the service context representation.
     * It is constructed by the stream constructor, and available for use
     * in the subclass stream constructor.
     */
    protected InputStream in = null ;

    public String toString() 
    {
	return "ServiceContext[ id=" + getId() + " ]" ;
    } 
}
