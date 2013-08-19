/*
 * @(#)ORBSingleton.java	1.38 03/01/23
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

package com.sun.corba.se.internal.corba;

import java.util.Collection;
import java.util.Properties;
import java.util.Hashtable;
import java.applet.Applet;
import java.net.URL;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.NVList;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Request;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.OutputStream;

import com.sun.corba.se.connection.ORBSocketFactory;

import com.sun.corba.se.internal.core.ClientGIOP;
import com.sun.corba.se.internal.core.MarshalInputStream;
import com.sun.corba.se.internal.core.MarshalOutputStream;
import com.sun.corba.se.internal.core.ORBVersion;
import com.sun.corba.se.internal.core.ORBVersionFactory;
import com.sun.corba.se.internal.core.ServerGIOP;
import com.sun.corba.se.internal.core.ServiceContextRegistry;
import com.sun.corba.se.internal.core.SubcontractRegistry;
import com.sun.corba.se.internal.core.CodeSetComponentInfo;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.EndPoint;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.iiop.BufferManagerFactory;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.POA.POAORB;

/*
 * The restricted singleton ORB implementation.
 *
 * For now, this class must implement just enough functionality to be
 * used as a factory for immutable TypeCode instances.
 *
 * See ORB.java for the real ORB implementation.
 */
public class ORBSingleton
    // This was changed from extending core.ORB so that it can
    // work for read_Object.
    extends com.sun.corba.se.internal.corba.ORB
    implements TypeCodeFactory
{
    // GIOP Version
    // Default is 1.0.  We will change it to 1.2 later.
    protected GIOPVersion giopVersion = GIOPVersion.DEFAULT_VERSION;

    // This map is needed for resolving recursive type code placeholders
    // based on the unique repository id.
    // _REVISIT_ No garbage collection. For Java2 use WeakHashMap.
    private Hashtable typeCodeMap = null;

    // This is used to support read_Object.
    private static POAORB fullORB;

    protected void set_parameters(Applet app, Properties props) {
    }

    protected void set_parameters (String params[], Properties props) {
    }

    /*
     * We must support these methods to act as a TypeCode factory.
     */

    public OutputStream create_output_stream() {
        return new EncapsOutputStream(this);
    }

    public TypeCode get_primitive_tc(TCKind tcKind) {
	// _REVISIT_ if this returns a null, throw an exception perhaps?
        return TypeCodeImpl.get_primitive_tc(tcKind);
    }

    public TypeCode create_struct_tc(String id,
				     String name,
				     StructMember[] members)
    {
        return new TypeCodeImpl(this, TCKind._tk_struct, id, name, members);
    }
  
    public TypeCode create_union_tc(String id,
				    String name,
				    TypeCode discriminator_type,
				    UnionMember[] members)
    {
        return new TypeCodeImpl(this,
				TCKind._tk_union, 
				id, 
				name, 
				discriminator_type, 
				members);
    }
  
    public TypeCode create_enum_tc(String id,
				   String name,
				   String[] members)
    {
        return new TypeCodeImpl(this, TCKind._tk_enum, id, name, members);
    }
  
    public TypeCode create_alias_tc(String id,
				    String name,
				    TypeCode original_type)
    {
        return new TypeCodeImpl(this, TCKind._tk_alias, id, name, original_type);
    }
  
    public TypeCode create_exception_tc(String id,
					String name,
					StructMember[] members)
    {
        return new TypeCodeImpl(this, TCKind._tk_except, id, name, members);
    }
  
    public TypeCode create_interface_tc(String id,
					String name)
    {
        return new TypeCodeImpl(this, TCKind._tk_objref, id, name);
    }
  
    public TypeCode create_string_tc(int bound) {
        return new TypeCodeImpl(this, TCKind._tk_string, bound);
    }
  
    public TypeCode create_wstring_tc(int bound) {
        return new TypeCodeImpl(this, TCKind._tk_wstring, bound);
    }
  
    public TypeCode create_sequence_tc(int bound,
				       TypeCode element_type)
    {
        return new TypeCodeImpl(this, TCKind._tk_sequence, bound, element_type);
    }
  
    public TypeCode create_recursive_sequence_tc(int bound,
						 int offset)
    {
        return new TypeCodeImpl(this, TCKind._tk_sequence, bound, offset);
    }
  
    public TypeCode create_array_tc(int length,
				    TypeCode element_type)
    {
        return new TypeCodeImpl(this, TCKind._tk_array, length, element_type);
    }

    public org.omg.CORBA.TypeCode create_native_tc(String id,
						   String name)
    {
        return new TypeCodeImpl(this, TCKind._tk_native, id, name);
    }

    public org.omg.CORBA.TypeCode create_abstract_interface_tc(
							       String id,
							       String name)
    {
        return new TypeCodeImpl(this, TCKind._tk_abstract_interface, id, name);
    }

    public org.omg.CORBA.TypeCode create_fixed_tc(short digits, short scale)
    {
        return new TypeCodeImpl(this, TCKind._tk_fixed, digits, scale);
    }

    // orbos 98-01-18: Objects By Value -- begin

    public org.omg.CORBA.TypeCode create_value_tc(String id,
                                                  String name,
                                                  short type_modifier,
                                                  TypeCode concrete_base,
                                                  ValueMember[] members)
    {
        return new TypeCodeImpl(this, TCKind._tk_value, id, name,
                                type_modifier, concrete_base, members);
    }

    public org.omg.CORBA.TypeCode create_recursive_tc(String id) {
        return new TypeCodeImpl(this, id);
    }

    public org.omg.CORBA.TypeCode create_value_box_tc(String id,
						      String name,
						      TypeCode boxed_type)
    {
        return new TypeCodeImpl(this, TCKind._tk_value_box, id, name, boxed_type);
    }


    public Any create_any() {
        return new AnyImpl(this);
    }

    // TypeCodeFactory interface methods.
    // Keeping track of type codes by repository id.

    public void setTypeCode(String id, TypeCodeImpl code) {
        if (typeCodeMap == null)
            typeCodeMap = new Hashtable(64);
        typeCodeMap.put(id, code);
    }

    public TypeCodeImpl getTypeCode(String id) {
        if (typeCodeMap == null)
            return null;
        return (TypeCodeImpl)typeCodeMap.get(id);
    }

    /*
     * Not strictly needed for TypeCode factory duty but these seem
     * harmless enough.
     */

    public NVList create_list(int count) {
        return new NVListImpl(this, count);
    }

    public org.omg.CORBA.NVList
	create_operation_list(org.omg.CORBA.Object oper) {
        throw new NO_IMPLEMENT();
    }

    public org.omg.CORBA.NamedValue
	create_named_value(String s, Any any, int flags) {
        return new NamedValueImpl(this, s, any, flags);
    }

    public org.omg.CORBA.ExceptionList create_exception_list() {
	return new ExceptionListImpl();
    }

    public org.omg.CORBA.ContextList create_context_list() {
        return new ContextListImpl(this);
    }

    public org.omg.CORBA.Context get_default_context() {
        throw new NO_IMPLEMENT();
    }

    public org.omg.CORBA.Environment create_environment() {
        return new EnvironmentImpl();
    }

    public org.omg.CORBA.Current get_current() {
        throw new NO_IMPLEMENT();
    }

    /*
     * Things that aren't allowed.
     */

    public String[] list_initial_services () {
	throw new NO_IMPLEMENT();
    }

    public org.omg.CORBA.Object resolve_initial_references(String identifier)
	throws InvalidName
    {
	throw new NO_IMPLEMENT();
    }

    public void register_initial_reference( 
        String id, org.omg.CORBA.Object obj ) throws InvalidName
    {
	throw new NO_IMPLEMENT();
    }

    public void send_multiple_requests_oneway(Request[] req) {
	throw new SecurityException("ORBSingleton: access denied");
    }
 
    public void send_multiple_requests_deferred(Request[] req) {
	throw new SecurityException("ORBSingleton: access denied");
    }

    public boolean poll_next_response() {
	throw new SecurityException("ORBSingleton: access denied");
    }
 
    public org.omg.CORBA.Request get_next_response() {
	throw new SecurityException("ORBSingleton: access denied");
    }

    public String object_to_string(org.omg.CORBA.Object obj) {
	throw new SecurityException("ORBSingleton: access denied");
    }

    public org.omg.CORBA.Object string_to_object(String s) {
	throw new SecurityException("ORBSingleton: access denied");
    }

    public java.rmi.Remote string_to_remote(String s)
	throws java.rmi.RemoteException
    {
	throw new SecurityException("ORBSingleton: access denied");
    }

    public void connect(org.omg.CORBA.Object servant) {
	throw new SecurityException("ORBSingleton: access denied");
    }

    public void disconnect(org.omg.CORBA.Object obj) {
	throw new SecurityException("ORBSingleton: access denied");
    }

    public void run()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public void shutdown(boolean wait_for_completion)
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    protected void shutdownServants(boolean wait_for_completion) {
        throw new SecurityException("ORBSingleton: access denied");
    }

    protected void destroyConnections() {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public void destroy() {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public boolean work_pending()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public void perform_work()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public org.omg.CORBA.portable.ValueFactory register_value_factory(String repositoryID,
    				org.omg.CORBA.portable.ValueFactory factory)
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public void unregister_value_factory(String repositoryID)
    {
        throw new SecurityException("ORBSingleton: access denied");
    }
    
    public org.omg.CORBA.portable.ValueFactory lookup_value_factory(String repositoryID)
    {
        throw new SecurityException("ORBSingleton: access denied");
    }


/*************************************************************************
    These are methods from com.sun.corba.se.internal.se.core.ORB
 ************************************************************************/

    /**
     * Get an instance of the GIOP client implementation.
     */
    public ClientGIOP getClientGIOP()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    /**
     * Get an instance of the GIOP server implementation.
     */
    public ServerGIOP getServerGIOP()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    /**
     * Return the subcontract registry
     */

    public SubcontractRegistry getSubcontractRegistry()
    {
	// To enable read_Object.

	if (fullORB == null) {
	    Properties props = new Properties();
	    // This needs to be a POAORB so that all subcontracts
	    // are available.
	    props.put("org.omg.CORBA.ORBClass",
		      "com.sun.corba.se.internal.POA.POAORB");
	    fullORB = (POAORB) ORB.init((String[])null, props);
	}
	return fullORB.getSubcontractRegistry();
    }

    /**
     * Return the service context registry
     */
    public ServiceContextRegistry getServiceContextRegistry()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    /**
     * Get a new instance of a GIOP input stream.
     */
    public MarshalInputStream newInputStream()
    {
        return new EncapsInputStream(this);
    }

    /**
     * Get a new instance of a GIOP input stream.
     */
    public MarshalInputStream newInputStream(byte[] buffer, int size)
    {
        return new EncapsInputStream(this, buffer, size);
    }

    public MarshalInputStream newInputStream(byte[] buffer, int size, boolean littleEndian) {
	return new EncapsInputStream(this, buffer, size, littleEndian);
    }

    /**
     * Get a new instance of a GIOP output stream.
     */
    public MarshalOutputStream newOutputStream()
    {
        return new EncapsOutputStream(this);
    }

    /**
     * Get the transient server ID
     */
    public int getTransientServerId()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    /**
     * Return the bootstrap naming port specified in the ORBInitialPort param.
     */
    public int getORBInitialPort()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    /**
     * Return the bootstrap naming host specified in the ORBInitialHost param.
     */
    public String getORBInitialHost()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public String getORBServerHost()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public int getORBServerPort()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public CodeSetComponentInfo getCodeSetComponentInfo() 
    {
	    return new CodeSetComponentInfo();
    }

    public boolean isLocalHost( String host )
    {
	// To enable read_Object.
	return false;
    }

    public boolean isLocalServerId( int subcontractId, int serverId )
    {
	// To enable read_Object.
	return false;
    }

    public boolean isLocalServerPort( int port ) 
    {
        return false;
    }

    public GIOPVersion getGIOPVersion() {
        return giopVersion;
    }

    /*
     * Things from corba.ORB.
     */

    public ORBVersion getORBVersion()
    {
        // Always use our latest ORB version (latest fixes, etc)
        return ORBVersionFactory.getORBVersion();
    }

    public void setORBVersion(ORBVersion verObj)
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public String getAppletHost()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public URL getAppletCodeBase()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public Collection getUserSpecifiedListenPorts ()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public ORBSocketFactory getSocketFactory ()
    {
        throw new SecurityException("ORBSingleton: access denied");
    }

    public int getHighWaterMark(){
        throw new SecurityException("ORBSingleton: access denied");
    }

    public int getLowWaterMark(){
        throw new SecurityException("ORBSingleton: access denied");
    }

    public int getNumberToReclaim(){
        throw new SecurityException("ORBSingleton: access denied");
    }

    public int getGIOPFragmentSize() {
        return ORBConstants.GIOP_DEFAULT_BUFFER_SIZE;
    }

    // getGIOPBufferSize needed by create_output_stream

    public int getGIOPBuffMgrStrategy(GIOPVersion gv) {
        return BufferManagerFactory.GROW;
    }

    // get_current throws NO_IMPLEMENT in parent.

    // PI hooks are empty.

    public IOR getServantIOR(){
        throw new SecurityException("ORBSingleton: access denied");
    }
	    

}
