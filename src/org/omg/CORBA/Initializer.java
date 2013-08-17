/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.omg.CORBA;



/**
 *
 * This is the mapping for an IDL structure which specifies
 * an initializer for a value type. A value can contain all the 
 * elements that an interface can as well as the definition of 
 * state members, and initializers for that state. In order to 
 * ensure portability of value implementations, designers may 
 * also define the signatures of initializers (or constructors) for 
 * non abstract value types. Syntactically these look like local 
 * operations except that they use the keyword init for the "name" of 
 * the operation, have no return type, and must use only in parameters. 
 * There may be any number of init declarations, as long as the signatures 
 * of all the initializers declared within the same scope are unique. 
 * Using the same signature as one found in a less-derived type is allowed.
 * The mapping of initializers is language specific and may not always 
 * result in a one to one correspondence between initializer signatures 
 * and the programming language constructs into which they map. This is 
 * because the mapping from IDL types into programming language types is 
 * not isomorphic; several different IDL types may map to the same 
 * programming language type. Hence defining initializers with the same 
 * number of parameters with types that are "similar" (e.g. char and wchar, 
 * signed and unsigned integers, etc.) should be done with care. Value types 
 * may also be abstract. They are called abstract because an abstract value 
 * type may not be instantiated. No <state_members> or <initializers> may 
 * be specified. However, local operations may be specified. 
 * Essentially they are a bundle of operation signatures with a purely 
 * local implementation. Note that a concrete value type with an 
 * empty state is not an abstract value type. 
 * The following shows an example of defining a value type with an 
 * initializer. 
 * Example:
 *
 *       valuetype EmployeeRecord {
 *            
 *            private string name;
 *            private string email;
 *            private string SSN;
 * 
 *            init(in string name, in string SSN);
 *       };
 *
 * Value types do not map to org.omg.CORBA.Object's.
 *
 */

public final class Initializer implements org.omg.CORBA.portable.IDLEntity
{
    /**
     * members contains the reference to all the Initializers of non abstract
     * value types.
     */
    public org.omg.CORBA.StructMember members[] = null;

    /**
     * Constructs an Initializer object with the members attribute null.
     */
    public Initializer ()
    {
    } // ctor

    /**
     * Constructs an Initializer object and assigns the members attribute 
     * with the given parameter.
     *
     * @param _members an array of StructMember Objects.
     */
    public Initializer (org.omg.CORBA.StructMember[] _members)
    {
	members = _members;
    } // ctor

} // class Initializer
