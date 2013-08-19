/*
 * @(#)StubDelegateImpl.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.internal.javax.rmi.CORBA;

import org.omg.CORBA.ORB;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.SystemException;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA_2_3.portable.ObjectImpl;
import com.sun.corba.se.internal.util.Utility;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;

/**
 * Base class from which all RMI-IIOP stubs must inherit.
 */
public class StubDelegateImpl
    implements javax.rmi.CORBA.StubDelegate {

    private int hashCode = 0;
    
    // IOR components
    private int typeLength;
    private byte[] typeData;
    private int numProfiles = 0;
    private int[] profileTags;
    private byte[][] profileData;

    public StubDelegateImpl() {
    }

    /**
     * Sets the IOR components if not already set.
     */
    private void init (javax.rmi.CORBA.Stub self) {

        // If the Stub is not connected to an ORB, BAD_OPERATION exception
        // will be raised by the code below.
        
        if (numProfiles == 0) {
            // write the IOR to an OutputStream 
            OutputStream ostr = ((org.omg.CORBA.ORB)(self._orb())).create_output_stream();
            ostr.write_Object(self);

            // read the IOR components back from the stream
            InputStream istr = ostr.create_input_stream();
            typeLength = istr.read_long();
            typeData = new byte[typeLength];
            istr.read_octet_array(typeData, 0, typeLength);
            numProfiles = istr.read_long();
            profileTags = new int[numProfiles];
            profileData = new byte[numProfiles][];
            for (int i = 0; i < numProfiles; i++) {
                profileTags[i] = istr.read_long();
                profileData[i] = new byte[istr.read_long()];
                istr.read_octet_array(profileData[i], 0, profileData[i].length);
            }
        }
    }
        
    /**
     * Returns a hash code value for the object which is the same for all stubs
     * that represent the same remote object.
     * @return the hash code value.
     */
    public int hashCode(javax.rmi.CORBA.Stub self) {

        init(self);

	if (hashCode == 0) {

	    // compute the hash code
	    for (int i = 0; i < typeLength; i++) {
		hashCode = hashCode * 37 + typeData[i];
	    }
	    for (int i = 0; i < numProfiles; i++) {
	 	hashCode = hashCode * 37 + profileTags[i];
		for (int j = 0; j < profileData[i].length; j++) {
		    hashCode = hashCode * 37 + profileData[i][j];
		}
            }
	}

        return hashCode;    
    }

    /**
     * Compares two stubs for equality. Returns <code>true</code> when used to compare stubs
     * that represent the same remote object, and <code>false</code> otherwise.
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the <code>obj</code>
     *          argument; <code>false</code> otherwise.
     */
    public boolean equals(javax.rmi.CORBA.Stub self, java.lang.Object obj) {
        
        if (self == obj) {
            return true;    
        }
        
        if (!(obj instanceof javax.rmi.CORBA.Stub)) {
            return false;            
        }
        
	// no need to call init() because of calls to hashCode() below

        javax.rmi.CORBA.Stub other = (javax.rmi.CORBA.Stub) obj;
        if (other.hashCode() != self.hashCode()) {
            return false;
        }

        // hashCodes being the same does not mean equality. The stubs still
        // could be pointing to different IORs. So, do a literal comparison.
        
        if (self.toString().equals(other.toString())) {
            return true;    
        }
        
        return false;        
    }

    /**
     * Returns a string representation of this stub. Returns the same string
     * for all stubs that represent the same remote object.
     * @return a string representation of this stub.
     */
    public String toString(javax.rmi.CORBA.Stub self) {

	try {
	    // _REVISIT_ returning the full IOR seems a bit verbose...
	    return ((org.omg.CORBA.ORB)(self._orb())).object_to_string(self);
        
	} catch (org.omg.CORBA.BAD_OPERATION e) {

	    // No delegate, so unconnected...
            return null;
        }
    }
    
    /**
     * Connects this stub to an ORB. Required after the stub is deserialized
     * but not after it is demarshalled by an ORB stream. If an unconnected
     * stub is passed to an ORB stream for marshalling, it is implicitly 
     * connected to that ORB. Application code should not call this method
     * directly, but should call the portable wrapper method 
     * {@link javax.rmi.PortableRemoteObject#connect}.
     * @param orb the ORB to connect to.
     * @exception RemoteException if the stub is already connected to a different
     * ORB, or if the stub does not represent an exported remote or local object.
     */
    public void connect(javax.rmi.CORBA.Stub self, ORB orb) 
        throws RemoteException 
    {
        
        // Do we already have a delegate set?
        
        boolean error = false;
        
        try {
            Delegate del = self._get_delegate();
            
            // Yes, is it the same orb? If so,
            // ignore it...
            
            if (del.orb(self) != orb) {
                
                // No, so error.
                
            error = true;
            }
            
        } catch (org.omg.CORBA.BAD_OPERATION err) {    
            
            // No. Do we know what the IOR is?
                
            if (numProfiles == 0) {
                    
                // No, can we get a Tie for this stub?
                    
                Tie tie = (javax.rmi.CORBA.Tie) Utility.getAndForgetTie(self);
                if (tie == null) {
                        
                    // No, so there is no exported object
                    // to use to connect this guy, so we
                    // must fail...
                        
                    error = true;
                        
                } else {
                
                    // Yes. Is the tie already connected?
                        
                    try {
                        ORB existingOrb = tie.orb();
                            
                        // Yes. Is it the same orb? If so,
                        // ignore it...
                                
                        if (existingOrb != orb) {
                            // No, so this is an error.
                            error = true;
                        }
                            
                    } catch (SystemException se) { 

                        // Should be BAD_OPERATION or BAD_INV_ORDER
                        if (!(se instanceof BAD_OPERATION) &&
                            !(se instanceof BAD_INV_ORDER))
                            throw se;

                        // Not connected, so connect it...
                        if( tie instanceof ObjectImpl )  {
                            tie.orb(orb);
                                                
                            // Copy the delegate from the tie to
                            // ourself...
                        
                            self._set_delegate(((ObjectImpl)tie)._get_delegate());
                        } else {
                            // Tie must be Servant
                            tie.orb(orb);
                            try {
                                org.omg.CORBA.Object ref =
                                    ((org.omg.PortableServer.Servant)tie)._this_object( );
                                self._set_delegate(((ObjectImpl)ref)._get_delegate());
                            } catch( org.omg.CORBA.BAD_INV_ORDER bad) {
                                error = true;
                            }
                        }
                    }
                                                
                }

            } else {
                    
                // Yes, so convert it to an object, extract
                // the delegate, and set it on ourself...
                    
                try {
		    // write the IOR components to an OutputStream 
		    OutputStream ostr = orb.create_output_stream();
		    ostr.write_long(typeLength);
		    ostr.write_octet_array(typeData, 0, typeLength);
		    ostr.write_long(numProfiles);
		    for (int i = 0; i < numProfiles; i++) {
			ostr.write_long(profileTags[i]);
			ostr.write_long(profileData[i].length);
			ostr.write_octet_array(profileData[i], 0, profileData[i].length);
		    }

		    // read the IOR back from the stream
    		    ObjectImpl obj = (ObjectImpl)((ostr.create_input_stream()).read_Object());


		    // copy the delegate
                    self._set_delegate(obj._get_delegate());

                } catch (Exception e) {
                    error = true;
                }         
            }
        }
    
        if (error) {
            throw new RemoteException("CORBA BAD_OPERATION 0");
        }    
    }

    /**
     * Serialization method to restore the IOR state.
     */
    public void readObject(javax.rmi.CORBA.Stub self, java.io.ObjectInputStream stream)
        throws IOException, ClassNotFoundException {

	// read the IOR from the ObjectInputStream
	typeLength = stream.readInt();
	typeData = new byte[typeLength];
	stream.readFully(typeData);
	numProfiles = stream.readInt();
	profileTags = new int[numProfiles];
	profileData = new byte[numProfiles][];
	for (int i = 0; i < numProfiles; i++) {
	    profileTags[i] = stream.readInt();
	    profileData[i] = new byte[stream.readInt()];
	    stream.readFully(profileData[i]);
        }
    }

    /**
     * Serialization method to save the IOR state.
     * @serialData The length of the IOR type ID (int), followed by the IOR type ID
     * (byte array encoded using ISO8859-1), followed by the number of IOR profiles
     * (int), followed by the IOR profiles.  Each IOR profile is written as a 
     * profile tag (int), followed by the length of the profile data (int), followed
     * by the profile data (byte array).
     */
    public void writeObject(javax.rmi.CORBA.Stub self, java.io.ObjectOutputStream stream) throws IOException {

        init(self);
    
	// write the IOR to the ObjectOutputStream
	stream.writeInt(typeLength);
	stream.write(typeData);
	stream.writeInt(numProfiles);
	for (int i = 0; i < numProfiles; i++) {
	    stream.writeInt(profileTags[i]);
	    stream.writeInt(profileData[i].length);
	    stream.write(profileData[i]);
        }

    }

}
