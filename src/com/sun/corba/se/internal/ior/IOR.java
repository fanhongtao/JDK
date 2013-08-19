/*
 * @(#)IOR.java	1.28 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/IOR.java

package com.sun.corba.se.internal.ior;

import java.util.LinkedList ;
import java.util.ListIterator ;
import java.util.Iterator ;
import java.io.*;

import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA.BAD_PARAM ;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;

import org.omg.IOP.TAG_INTERNET_IOP ;

import com.sun.corba.se.internal.ior.Writeable ;
import com.sun.corba.se.internal.ior.IdEncapsulationContainerBase ;
import com.sun.corba.se.internal.ior.IIOPProfileTemplate ;
import com.sun.corba.se.internal.ior.IIOPProfile ;
import com.sun.corba.se.internal.ior.ObjectIds ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.IdEncapsulationFactory ;
import com.sun.corba.se.internal.ior.TaggedProfileFactoryFinder ;

import com.sun.corba.se.internal.core.MarshalOutputStream;

import com.sun.corba.se.internal.corba.EncapsOutputStream;

import com.sun.corba.se.internal.orbutil.HexOutputStream;
import com.sun.corba.se.internal.orbutil.MinorCodes;

/** An IOR is represented as a linked list of profiles.
* Only objects that extend Profile should be added to an IOR.
* However, enforcing this restriction requires overriding all
* of the addXXX methods inherited from LinkedList, so no check
* is included here.
* @author Ken Cavanaugh
*/
public class IOR extends IdEncapsulationContainerBase implements Writeable 
{

    // Constants used in getIORFromString
    public static final String STRINGIFY_PREFIX = "IOR:" ;
    protected static final int PREFIX_LENGTH = STRINGIFY_PREFIX.length() ;

    private String typeId;
   
    public boolean equals( Object obj )
    {
	if (obj == null)
	    return false ;

	if (!(obj instanceof IOR))
	    return false ;

	IOR other = (IOR)obj ;

	return super.equals( obj ) && typeId.equals( other.typeId ) ;
    }

    /** Construct an empty IOR.  This is needed for null object references.
    */
    public IOR()
    {
	this( "" ) ;
    }

    public IOR( String typeId ) 
    {
	this.typeId = typeId ;
    }

    /** Construct an IOR from a template for a single IIOP profile and id 
    * info.  This is the common case today.
    */
    public IOR( String typeId, IIOPProfileTemplate template, 
	ObjectId id )
    {
	this.typeId = typeId ;
	IIOPProfile profile = new IIOPProfile( id, template ) ;
	add( profile ) ;
    }

    /** Construct an IOR from template and id info.  Note that this
     * IOR will only contain IIOP profiles, since that is all that we currently
     * support.
     * @param template
     * @param ids
     * @return 
     * @exception 
     * @author 
     * The template and ids arguments to the IOR constructor must both have the 
     * same number of elements.
     * @roseuid 3910984E01EF
     */
    public IOR( String typeId, IORTemplate template, ObjectIds ids) 
    {
	this.typeId = typeId ;

	Iterator idIterator = ids.iterator() ;
	Iterator templateIterator = template.iterator() ;

	while (templateIterator.hasNext()) {
	    TaggedProfileTemplate ptemp = (TaggedProfileTemplate)
		(templateIterator.next()) ;
	    ObjectId oid = null ;

	    if (idIterator.hasNext())
		oid = (ObjectId)(idIterator.next()) ;
	    else
		throw new BAD_PARAM( "Too few ObjectIds in IOR constructor" ) ;

	    TaggedProfile profile = ptemp.create( oid ) ;
	    add( profile ) ;
	}	

	if (idIterator.hasNext())
	    throw new BAD_PARAM( "Too many ObjectIds in IOR constructor" ) ;
    }

    /** Construct an IOR from an IORTemplate by applying the same
    * object id to each IIOPProfileTemplate in the IORTemplate.
    */
    public IOR( String typeId, IORTemplate template, ObjectId id) 
    {
	this.typeId = typeId ;

	Iterator templateIterator = template.iterator() ;

	while (templateIterator.hasNext()) {
	    TaggedProfileTemplate ptemp = 
		(TaggedProfileTemplate)(templateIterator.next()) ;

	    TaggedProfile profile = ptemp.create( id ) ;

	    add( profile ) ;
	}	
    }
    
    /**
     * @param is
     * @return 
     * @exception 
     * @author 
     * @roseuid 3910984E01FA
     */
    public IOR(InputStream is) 
    {
    	IdEncapsulationFactoryFinder finder = 
	    TaggedProfileFactoryFinder.getFinder() ;
	this.typeId = is.read_string() ;
	readIdEncapsulationSequence( finder, is ) ;
    }
    
    /**
     * @return String
     * @exception 
     * @author 
     * @roseuid 3910984E0204
     */
    public String getTypeId() 
    {
	return typeId ;
    }
    
    /**
     * @param arg0
     * @return void
     * @exception 
     * @author 
     * @roseuid 3910984E0205
     */
    public void write(OutputStream os) 
    {
	os.write_string( typeId ) ;
	writeIdEncapsulationSequence( os ) ;
    }

    public String stringify(com.sun.corba.se.internal.core.ORB orb)
    {
        StringWriter bs;

        MarshalOutputStream s = orb.newOutputStream();
        s.putEndian();
        write( (OutputStream)s );
        bs = new StringWriter();
        try {
            s.writeTo(new HexOutputStream(bs));
        } catch (IOException ex) {
            throw new INTERNAL( MinorCodes.STRINGIFY_WRITE_ERROR,
                                CompletionStatus.COMPLETED_NO );
        }

        return com.sun.corba.se.internal.core.IOR.STRINGIFY_PREFIX + bs;
    }

    public void makeImmutable()
    {
	Iterator iter = iteratorById( TAG_INTERNET_IOP.value ) ;
	while (iter.hasNext()) {
	    Object obj = iter.next() ;
	    if (obj instanceof IIOPProfile) {
		IIOPProfile profile = (IIOPProfile)obj ;
		IIOPProfileTemplate temp = profile.getTemplate() ;
		temp.makeImmutable() ;
	    }
	}
	super.makeImmutable() ;
    }
    
    public org.omg.IOP.IOR getIOPIOR(ORB orb) {    
	EncapsOutputStream os = new EncapsOutputStream(orb);
	write(os);
	InputStream is = (InputStream) (os.create_input_stream());
	return org.omg.IOP.IORHelper.read(is);
    }
}
