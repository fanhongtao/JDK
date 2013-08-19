/*
 * @(#)DelegateImpl.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.POA;

import org.omg.PortableServer.*;



public class DelegateImpl implements org.omg.PortableServer.portable.Delegate{

    private POAORB _orb;

    public DelegateImpl(POAORB orb){
        _orb     = orb;
    }

    /*                org.omg.PortableServer.portable.Delegate methods.           */
    //==============================================================================
    //==============================================================================
    public org.omg.CORBA.ORB orb(Servant self){
        return _orb;
    }

    public org.omg.CORBA.Object this_object(Servant self){
        //REVISIT Temporary implementation
        byte[] oid;
        POAImpl poa;
        try{
            oid = _orb.poaCurrent.get_object_id();
            poa = (POAImpl)_orb.poaCurrent.get_POA();
            return poa.createReference(self._all_interfaces(poa,oid)[0],oid); 
        } catch (org.omg.PortableServer.CurrentPackage.NoContext notInInvocationE) { 
            //Not within an invocation context
            POAImpl defaultPOA = null;
            try{
                defaultPOA = (POAImpl)self._default_POA();
            } catch (ClassCastException exception){
                throw new org.omg.CORBA.OBJ_ADAPTER(
                    "Servant's _default_POA must be an instance of POAImpl.");
            }
            try{
                if ( defaultPOA.getPolicies().isImplicitlyActivated() )
                    return defaultPOA.servant_to_reference(self);
                else if ( defaultPOA.getPolicies().isUniqueIds() 
                     && defaultPOA.getPolicies().retainServants())
                    return defaultPOA.servant_to_reference(self);
                else throw new org.omg.CORBA.OBJ_ADAPTER(
                        "Don't have the right POA policies for _this_object " +
                        "called from outside of an invocation thread.");
            } catch ( org.omg.PortableServer.POAPackage.ServantNotActive e) {
                throw new org.omg.CORBA.OBJ_ADAPTER(
                    "ServantNotActive: servant_to_reference failed.");
            } catch ( org.omg.PortableServer.POAPackage.WrongPolicy e) {
                throw new org.omg.CORBA.OBJ_ADAPTER(
                    "WrongPolicy: servant_to_reference failed.");
            }
        } catch (ClassCastException e) {
            throw new org.omg.CORBA.OBJ_ADAPTER(
                "Servant's _default_POA must be an instance of POAImpl.");
        }
    }

    public POA poa(Servant self){
        try{
            return _orb.poaCurrent.get_POA();
        } catch (org.omg.PortableServer.CurrentPackage.NoContext exception){
            if (ShutdownUtilDelegate.instance != null) {
                POA returnValue = ShutdownUtilDelegate.instance.lookupPOA(self);
                if (returnValue != null) {
                    return returnValue;
                }
            }
            throw new org.omg.CORBA.OBJ_ADAPTER(
                "NoContext: outside of an invocation context.");
        }
    }

    public byte[] object_id(Servant self){
        try{
            return _orb.poaCurrent.get_object_id();
        } catch (org.omg.PortableServer.CurrentPackage.NoContext exception){
            throw new org.omg.CORBA.OBJ_ADAPTER(
                "NoContext: outside of an invocation context.");
        }
    }

    public POA default_POA(Servant self){
        return _orb.getRootPOA();
    }

    public boolean is_a(Servant self, String repId){
        String[] repositoryIds = self._all_interfaces(poa(self),object_id(self));
	for ( int i=0; i<repositoryIds.length; i++ )
	    if ( repId.equals(repositoryIds[i]) )
		return true;
        return false;
    }

    public boolean non_existent(Servant self){
        //REVISIT
        try{
            byte[] oid = _orb.poaCurrent.get_object_id();
            if( oid == null) return true;
            else return false;
        } catch (org.omg.PortableServer.CurrentPackage.NoContext exception){
            throw new org.omg.CORBA.OBJ_ADAPTER(
                "NoContext: outside of an invocation context.");
        }
        
    }

    //REVISIT
    //Simon And Ken Will Ask About Editorial Changes
    //In Idl To Java For The Following Signature.
    /*
    public org.omg.CORBA.Object get_interface(Servant Self){
        //Not currently implemented.
        throw new org.omg.CORBA.NO_IMPLEMENT("This method is not implemented.");
    }
    */

    // The get_interface() method has been replaced by get_interface_def()

    public org.omg.CORBA.Object get_interface_def(Servant Self){
        //Not currently implemented.
        throw new org.omg.CORBA.NO_IMPLEMENT("This method is not implemented.");
    }
}



