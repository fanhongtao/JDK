/*
 * @(#)DynAny.java	1.9 98/09/10
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


package org.omg.CORBA;


/** org.omg.CORBA.Any values can be dynamically interpreted (traversed) and
 *  constructed
 *  through DynAny objects. A DynAny object is associated with a data value
 *  which may correspond to a copy of the value inserted into an Any.
 *  The DynAny APIs enable traversal of the data value associated with an
 *  Any at runtime and extraction of the primitive constituents of the
 *  data value.
 */


public interface DynAny extends org.omg.CORBA.Object
{
    /**
     * Returns the <code>TypeCode</code> of the object inserted into
     * this <code>DynAny</code>.
     *
     * @return the <code>TypeCode</code> object.
     */
    public org.omg.CORBA.TypeCode type() ;
    
    /**
     * Copy the contents from one Dynamic Any into another.
     *
     * @param dyn_any the <code>DynAny</code> object whose contents
     *                are assigned to this <code>DynAny</code>. 
     * @exception Invalid if the source <code>DynAny</code> is
     *            invalid. 
     */
    public void assign(org.omg.CORBA.DynAny dyn_any)
        throws org.omg.CORBA.DynAnyPackage.Invalid;

    /**
     * Make a <code>DynAny</code> object from an <code>Any</code>
     * object.
     *
     * @param value the <code>Any</code> object.
     * @exception Invalid if the source <code>Any</code> object is
     *                    empty or bad.
     */
    public void from_any(org.omg.CORBA.Any value)
        throws org.omg.CORBA.DynAnyPackage.Invalid;

    /**
     * Convert a <code>DynAny</code> object to an <code>Any</code>
     * object.
     *
     * @return the <code>Any</code> object.
     * @exception Invalid if this <code>DynAny</code> is empty or
     *                    bad.
     */
    public org.omg.CORBA.Any to_any()
        throws org.omg.CORBA.DynAnyPackage.Invalid;

    /**
     * Destroy this <code>DynAny</code>.
     */
    public void destroy() ;

    /**
     * Clone this </code>DnyAny</code>.
     *
     * @return the <code>DynAny</code>.
     */
    public org.omg.CORBA.DynAny copy() ;

    public void insert_boolean(boolean value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_octet(byte value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_char(char value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_short(short value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_ushort(short value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_long(int value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_ulong(int value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_float(float value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_double(double value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_string(String value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_reference(org.omg.CORBA.Object value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_typecode(org.omg.CORBA.TypeCode value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_longlong(long value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_ulonglong(long value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_wchar(char value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_wstring(String value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_any(org.omg.CORBA.Any value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    // orbos 98-01-18: Objects By Value -- begin
    
    public void insert_val(java.io.Serializable value)
	throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public java.io.Serializable get_val()
	throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
    
    // orbos 98-01-18: Objects By Value -- end

    public boolean get_boolean()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public byte get_octet()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public char get_char()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public short get_short()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public short get_ushort()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public int get_long()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public int get_ulong()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public float get_float()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public double get_double()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public String get_string()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public org.omg.CORBA.Object get_reference()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public org.omg.CORBA.TypeCode get_typecode()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public long get_longlong()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public long get_ulonglong()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public char get_wchar()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public String get_wstring()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public org.omg.CORBA.Any get_any()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    /**
     * During traversal or iteration of a <code>DynAny</code>, inspect
     * the current component in the <code>DynAny</code>.
     *
     * @return the <code>DynAny</code>
     */
    public org.omg.CORBA.DynAny current_component() ;

    /**
     * Logically advance the pointer to the next component.
     *
     * @return true if there is a next component, false otherwise.
     */
    public boolean next() ;

    /**
     * Advance to the component at a specified index.
     *
     * @param index the index of the component to advance to.
     * @return true if there is a component at <code>index</code>,
     *              false otherwise.
     */
    public boolean seek(int index) ;

    /**
     * Rewind the internal pointer to point to the first component.
     */
    public void rewind() ;
}
