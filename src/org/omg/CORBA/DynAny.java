/*
 * @(#)DynAny.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */


package org.omg.CORBA;


/**
 *  An object that enables the dynamic management of <code>Any</code>
 *  values.  A <code>DynAny</code> object allows a program to use
 *  an <code>Any</code> object
 *  when the program has no static information about the type of the
 *  <code>Any</code> object. The <code>DynAny</code> interface
 *  provides methods for traversing the data value associated with an 
 *  <code>Any</code> object at run time and for extracting the primitive
 *  constituents of the data value.
 *  <p>
 *  A <code>DynAny</code> object is associated with a data value
 *  that may correspond to a copy of the value inserted into an
 *  <code>Any</code> object. The <code>DynAny</code> object may be seen as
 *  owning a pointer to an external buffer that holds some representation
 *  of the data value.
 *  <p>
 *  For data values that are constructed types (IDL
 *  struct, sequence, array, union, and so on), the <code>DynAny</code>
 *  object also can be thought of as holding a pointer to a buffer offset
 *  where the current component of the constructed type is being represented.
 *  The buffer pointer effectively points
 *  to the space used to represent the first component of the data value
 *  when the programmer creates the <code>DynAny</code> object.  Calling
 *  the <code>DynAny</code> method <code>next</code> will move the pointer
 *  to the next component, making it possible to iterate through the
 *  components of a constructed data value.  The buffer pointer is moved
 *  back to the first component each time the method <code>rewind</code>
 *  is called.
 *  <p>
 *  <code>DynAny</code> methods make it possible to do the following:
 *  <ul>
 *  <li> Obtain the type code associated with a <code>DynAny</code> object
 *  <li> Initialize a <code>DynAny</code> object from another 
 *       <code>DynAny</code> object
 *  <li> Initialize a <code>DynAny</code> object from an
 *       <code>Any</code> value
 *  <li> Generate an <code>Any</code> value from a <code>DynAny</code>
 *       object
 *  <li> Destroy a <code>DynAny</code> object
 *  <li> Create a copy of a <code>DynAny</code> object
 *  <li> Access a value of some basic type in a <code>DynAny</code> object
 *  <li> Iterate through components of a <code>DynAny</code> object
 *  </ul>
 * <p>
 *  Inserting a basic data type value into a constructed <code>DynAny</code>
 *  object implies initializing the next component of the constructed data
 *  value associated with the <code>DynAny</code> object.  For example,
 *  invoking the method <code>insert_boolean</code> in a <code>DynStruct</code>
 *  implies inserting a <code>boolean</code> data value as the next member
 *  of the associated struct data value.
 *  <p>
 *  Creating a <code>DynAny</code> object can be done by:
 *  <ul>
 *  <li> invoking a method on an existing <code>DynAny</code> object
 *  <li> invoking an <code>org.omg.CORBA.ORB</code> method (create_dyn_any,
 *       create_dyn_struct, create_dyn_sequence, and so on)
 *  </ul>
 * <p>
 *  Dynamic creation of an <code>Any</code> object containing a value
 *  of a basic data type typically involves the following:
 * <ul>
 * <li> Creating a <code>DynAny</code> object using
 *      <code>ORB.create_basic_dyn_any</code>,
 *      passing it the type code associated with the basic data type value to
 *      be created
 * <li> Initializing the value by means of invoking methods on the resulting
 *      <code>DynAny</code> object (<code>insert_boolean</code> if the
 *      <code>DynAny</code> is of type <code>boolean</code>, for example)
 * <li> Creating the <code>Any</code> object by invoking the method
 *      <code>to_any</code> on the initialized <code>DynAny</code> object
 * </ul>
 * <p>
 *  Dynamic creation of an <code>Any</code> object containing a value
 *  of a constructed data type typically involves the following:
 * <ul>
 * <li> Creating a <code>DynAny</code> object using the appropriate <code>ORB</code>
 *      method (for example, <code>ORB.create_dyn_struct</code> for an IDL struct),
 *      passing it the type code associated with the constructed data type value to
 *      be created
 * <li> Initializing components of the value by means of: 
 *  <ul>
 *    <li>invoking methods on the resulting <code>DynStruct</code>
 *      (or other constructed type) object
 *     <ul>
 *      <li>call the method <code>current_component</code> to get a
 *          <code>DynAny</code> object for that component
 *      <li> call the appropriate <code>insert</code> method 
 *            on the <code>DynAny</code> object returned by 
 *            the method <code>current_component</code> to initialize
 *           the component
 *     <li> call the method <code>next</code> to move to the next component
 *          and continue to get the current component, initialize it, and move
 *          to the next component until there are no more components (the method
 *          <code>next</code> returns <code>false</code>)
 *     </ul>
 *    <li> or invoking methods on the
 *      <code>DynAny</code> objects generated for each member of the 
 *      constructed type
 *  </ul>
 * <li> Creating the <code>Any</code> object by invoking the method
 *      <code>to_any</code> on the initialized <code>DynAny</code> object
 * </ul>
 *  <p>
 *  <code>DynAny</code> objects are intended to be local to the
 *  process in which they are created and used. Any method that
 *  attempts to export references to <code>DynAny</code> objects
 *  to other processes or to externalize them with the method
 *  <code>org.omg.CORBA.ORB.object_to_string</code> will throw a
 * <code>MARSHAL</code> exception.  Also, even though <code>DynAny</code>
 * objects are derived from the interface <code>Object</code>, invoking
 * methods from <code>Object</code> will throw the <code>NO_IMPLEMENT</code>
 * exception.  An attempt to use a <code>DynAny</code> object with the
 * DII (Dynamic Invocation Interface) may throw a <code>NO_IMPLEMENT</code>
 * exception.
 * <p>
 *  The following classes are derived from <code>DynAny</code>
 *  and define additional methods relevant to their particular
 *  IDL type: <code>DynFixed</code>, <code>DynStruct</code>, <code>DynSequence</code>, 
 *  <code>DynArray</code>, <code>DynUnion</code>, <code>DynEnum</code>,
 *  <code>DynAny</code>,  and <code>DynValue</code>.
 */


public interface DynAny extends org.omg.CORBA.Object
{

/**
 * Retrieves the <code>TypeCode</code> object contained in
 * this <code>DynAny</code> object. Note that the type code
 * associated with a <code>DynAny</code> object is initialized
 * at the time <code>DynAny</code> is created and cannot be changed
 * during the lifetime of the <code>DynAny</code> object.
 *
 * @return the <code>TypeCode</code> object describing the 
 *         value contained in this <code>DynAny</code> object
 */
    public org.omg.CORBA.TypeCode type() ;

/**
 * Initializes the value associated with this <code>DynAny</code>
 * object with the value associated with the given <code>DynAny</code>
 * object.
 *
 * @param dyn_any the <code>DynAny</code> object whose value will be
 *                used to initialize this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.Invalid</code> 
 *             if the given <code>DynAny</code> object has a type code
 *             that is not equivalent or has not been assigned a value
 */
    public void assign(org.omg.CORBA.DynAny dyn_any)
        throws org.omg.CORBA.DynAnyPackage.Invalid;

/**
 * Initializes the value associated with this <code>DynAny</code>
 * object with the value associated with the given <code>Any</code>
 * object.
 * @param value the <code>Any</code> object whose value will be
 *                used to initialize this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.Invalid</code> 
 *             if the given <code>Any</code> object has a type code
 *             that is not equivalent or has not been assigned a value
 */
    public void from_any(org.omg.CORBA.Any value)
        throws org.omg.CORBA.DynAnyPackage.Invalid;

/**
 * Creates an <code>Any</code> object from this <code>DynAny</code>
 * object.  The type code and value of this <code>DynAny</code>
 * object are copied into the newly-created <code>Any</code> object.
 *
 * @return the newly-created <code>Any</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.Invalid</code> if
 *            this <code>DynAny</code> object has not been correctly
 *            created or does not contain a meaningful value
 */
    public org.omg.CORBA.Any to_any()
        throws org.omg.CORBA.DynAnyPackage.Invalid;

/**
 * Destroys this <code>DynAny</code> object and frees any resources
 * used to represent the data value associated with it. This method
 * also destroys all <code>DynAny</code> objects obtained from it.
 * <p>
 * Destruction of <code>DynAny</code> objects should be handled with
 * care, taking into account issues dealing with the representation of 
 * data values associated with <code>DynAny</code> objects.  A programmer
 * who wants to destroy a <code>DynAny</code> object but still be able
 * to manipulate some component of the data value associated with it,
 * should first create a <code>DynAny</code> object for the component
 * and then make a copy of the created <code>DynAny</code> object.
 */
    public void destroy() ;

    /**
     * Clone this </code>DnyAny</code>.
     *
     * @return the <code>DynAny</code>.
     */
    public org.omg.CORBA.DynAny copy() ;

/**
 * Inserts the given <code>boolean</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>boolean</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_boolean(boolean value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>byte</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>byte</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_octet(byte value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>char</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>char</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_char(char value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>short</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>short</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_short(short value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>short</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>short</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_ushort(short value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>int</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>int</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_long(int value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>int</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>int</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_ulong(int value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>float</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>float</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_float(float value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>double</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>double</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_double(double value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>String</code> object as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>String</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_string(String value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>org.omg.CORBA.Object</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>org.omg.CORBA.Object</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_reference(org.omg.CORBA.Object value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>org.omg.CORBA.TypeCode</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>org.omg.CORBA.TypeCode</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_typecode(org.omg.CORBA.TypeCode value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>long</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>long</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_longlong(long value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>long</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>long</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_ulonglong(long value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>char</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>char</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_wchar(char value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>String</code> as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>String</code> to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_wstring(String value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Inserts the given <code>org.omg.CORBA.Any</code> object as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>org.omg.CORBA.Any</code> object to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_any(org.omg.CORBA.Any value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    // orbos 98-01-18: Objects By Value -- begin
    
/**
 * Inserts the given <code>java.io.Serializable</code> object as the value for this
 * <code>DynAny</code> object.
 *
 * <p> If this method is called on a constructed <code>DynAny</code>
 * object, it initializes the next component of the constructed data
 * value associated with this <code>DynAny</code> object.
 *
 * @param value the <code>java.io.Serializable</code> object to insert into this
 *              <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.InvalidValue</code>
 *            if the value inserted is not consistent with the type
 *            of the accessed component in this <code>DynAny</code> object
 */
    public void insert_val(java.io.Serializable value)
	throws org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Retrieves the <code>java.io.Serializable</code> object contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>java.io.Serializable</code> object that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>java.io.Serializable</code> object
 */               
    public java.io.Serializable get_val()
	throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
    
    // orbos 98-01-18: Objects By Value -- end

/**
 * Retrieves the <code>boolean</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>boolean</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>boolean</code>
 */               
    public boolean get_boolean()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>byte</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>byte</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>byte</code>
 */               
    public byte get_octet()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

/**
 * Retrieves the <code>char</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>char</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>char</code>
 */               
    public char get_char()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>short</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>short</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>short</code>
 */               
    public short get_short()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>short</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>short</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>short</code>
 */               
    public short get_ushort()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>int</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>int</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>int</code>
 */               
    public int get_long()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>int</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>int</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>int</code>
 */               
    public int get_ulong()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>float</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>float</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>float</code>
 */               
    public float get_float()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>double</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>double</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>double</code>
 */               
    public double get_double()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>String</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>String</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>String</code>
 */               
    public String get_string()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>org.omg.CORBA.Other</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>org.omg.CORBA.Other</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for an <code>org.omg.CORBA.Other</code>
 */               
    public org.omg.CORBA.Object get_reference()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>org.omg.CORBA.TypeCode</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>org.omg.CORBA.TypeCode</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>org.omg.CORBA.TypeCode</code>
 */               
    public org.omg.CORBA.TypeCode get_typecode()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>long</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>long</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>long</code>
 */               
    public long get_longlong()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>long</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>long</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>long</code>
 */               
    public long get_ulonglong()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>char</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>char</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>char</code>
 */               
    public char get_wchar()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>String</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>String</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>String</code>
 */               
    public String get_wstring()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


/**
 * Retrieves the <code>org.omg.CORBA.Any</code> contained
 * in this <code>DynAny</code> object.
 *
 * @return the <code>org.omg.CORBA.Any</code> that is the 
 *         value for this <code>DynAny</code> object
 * @exception <code>org.omg.CORBA.DynAnyPackage.TypeMismatch</code>
 *               if the type code of the accessed component in this
 *               <code>DynAny</code> object is not equivalent to
 *               the type code for a <code>org.omg.CORBA.Any</code>
 */               
    public org.omg.CORBA.Any get_any()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

/**
 * Returns a <code>DynAny</code> object reference that can
 * be used to get/set the value of the component currently accessed.
 * The appropriate or <code>insert</code> method
 * can be called on the resulting <code>DynAny</code> object
 * to initialize the component.
 * The appropriate <code>get</code> method
 * can be called on the resulting <code>DynAny</code> object
 * to extract the value of the component.
 */
    public org.omg.CORBA.DynAny current_component() ;

/**
 * Moves to the next component of this <code>DynAny</code> object.
 * This method is used for iterating through the components of
 * a constructed type, effectively moving a pointer from one
 * component to the next.  The pointer starts out on the first
 * component when a <code>DynAny</code> object is created.
 *
 * @return <code>true</code> if the pointer points to a component;
 * <code>false</code> if there are no more components or this
 * <code>DynAny</code> is associated with a basic type rather than
 * a constructed type
 */ 
    public boolean next() ;

/**
 * Moves the internal pointer to the given index. Logically, this method
 * sets a new offset for this pointer.  
 *
 * @param index an <code>int</code> indicating the position to which
 *              the pointer should move.  The first position is 0.
 * @return <code>true</code> if the pointer points to a component;
 * <code>false</code> if there is no component at the designated
 * index.  If this <code>DynAny</code> object is associated with a
 * basic type, this method returns <code>false</code> for any index
 * other than 0.
 */
    public boolean seek(int index) ;

/**
 * Moves the internal pointer to the first component.
 */
    public void rewind() ;
}
