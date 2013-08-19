/*
 * @(#)IdEncapsulationContainerBase.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/ContainerBase.java

package com.sun.corba.se.internal.ior;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import com.sun.corba.se.internal.ior.FreezableList ;
import com.sun.corba.se.internal.ior.IdentifiableContainerBase ;
import com.sun.corba.se.internal.ior.TaggedComponent ;
import com.sun.corba.se.internal.ior.IdEncapsulationFactoryFinder ;
import com.sun.corba.se.internal.ior.IdEncapsulation ;
import org.omg.IOP.TAG_INTERNET_IOP ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;

/**
 * @author 
 */
public class IdEncapsulationContainerBase extends IdentifiableContainerBase
{
    /** Read the count from is, then read count IdEncapsulations from
     * is using the factory.  Add each constructed IdEncapsulation to list.
     * @param arg0
     * @param arg1
     * @return void
     * @exception 
     * @author 
     * @roseuid 3910984C0306
     */
    public void readIdEncapsulationSequence(
	IdEncapsulationFactoryFinder finder, InputStream istr) 
    {
	int count = istr.read_long() ;
	for (int ctr = 0; ctr<count; ctr++) {
	    int id = istr.read_long() ;
	    IdEncapsulation obj = finder.create( id, istr ) ;
	    add( obj ) ;
	}
    }

    /** Write all IdEncapsulations that we contain to os.  The total
     * length must be written before this method is called.
     * @param arg0
     * @return void
     * @exception 
     * @author 
     * @roseuid 3910984C030E
     */
    public void writeIdEncapsulationSequence( OutputStream os) 
    {
	os.write_long( size() ) ;
	Iterator iter = iterator() ;
	while (iter.hasNext()) {
	    IdEncapsulation obj = (IdEncapsulation)( iter.next() ) ;
	    os.write_long( obj.getId() ) ;
	    obj.write( os ) ;
	}
    }
}
