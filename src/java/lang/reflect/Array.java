/*
 * @(#)Array.java	1.4 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang.reflect;

/**
 * The Array class provides static methods to dynamically create and
 * access Java arrays.
 *
 * <p>Array permits widening conversions to occur during a get or set
 * operation, but throws an IllegalArgumentException if a narrowing
 * conversion would occur.
 *
 * @author Nakul Saraiya
 */
public final
class Array {

    /**
     * Constructor.  Class Array is not instantiable.
     */
    private Array() {}

    /**
     * Creates a new array with the specified component type and
     * length.

     * The effect is that of the equivalent array creation expression:
     * <pre>
     *    new componentType[length]
     * </pre>
     *
     * @param componentType the Class object representing the
     * component type of the new array
     * @param length the length of the new array
     * @return the new array
     * @exception NullPointerException if the specified
     * componentType parameter is null
     * @exception NegativeArraySizeException if the specified length
     * is negative
     */
    public static Object newInstance(Class componentType, int length)
	throws NegativeArraySizeException {
	return newArray(componentType, length);
    }

    /**
     * Creates a new array with the specified component type and
     * dimensions.
     *
     * <p>The effect is that of the equivalent array creation expression:
     * <pre>
     *    new componentType[dimensions[0]][dimensions[1]]...
     * </pre>
     *
     * @param componentType the Class object representing the component
     * type of the new array
     * @param dimensions an array of ints representing the dimensions of
     * the new array
     * @return the new array
     * @exception NullPointerException if the specified 
     * componentType argument is null
     * @exception IllegalArgumentException if the specified dimensions
     * argument is a zero-dimensional array, or if the number of
     * requested dimensions exceeds the limit on the number of array dimensions 
     * supported by the implementation (typically 255).
     * @exception NegativeArraySizeException if any of the components in
     * the specified dimension argument is negative.
     */
    public static Object newInstance(Class componentType, int[] dimensions)
	throws IllegalArgumentException, NegativeArraySizeException {
	return multiNewArray(componentType, dimensions);
    }

    /**
     * Returns the length of the specified array object, as an int.
     *
     * @param array the array
     * @return the length of the array
     * @exception IllegalArgumentException if the object argument is not
     * an array
     */
    public static native int getLength(Object array)
	throws IllegalArgumentException;

    /**
     * Returns the value of the indexed component in the specified
     * array object.  The value is automatically wrapped in an object
     * if it has a primitive type.
     *
     * @param array the array
     * @param index the index
     * @return the (possibly wrapped) value of the indexed component in
     * the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     */
    public static native Object get(Object array, int index)
 	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a boolean.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native boolean getBoolean(Object array, int index)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a byte.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native byte getByte(Object array, int index)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a char.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native char getChar(Object array, int index)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a short.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native short getShort(Object array, int index)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as an int.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native int getInt(Object array, int index)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a long.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native long getLong(Object array, int index)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a float.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native float getFloat(Object array, int index)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a double.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native double getDouble(Object array, int index)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified new value.  The new value is first
     * automatically unwrapped if the array has a primitive component
     * type.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null, or if the array component type is primitive and the specified
     * value is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the array component type is primitive and
     * the specified value cannot be converted to the primitive type by
     * a combination of unwrapping and identity or widening conversions
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     */
    public static native void set(Object array, int index, Object value)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified boolean value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setBoolean(Object array, int index, boolean z)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified boolean value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
      * is not an array, or if the the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setByte(Object array, int index, byte b)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified byte value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setChar(Object array, int index, char c)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified short value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setShort(Object array, int index, short s)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified int value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setInt(Object array, int index, int i)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified long value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setLong(Object array, int index, long l)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified float value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setFloat(Object array, int index, float f)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified double value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified index
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setDouble(Object array, int index, double d)
	throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /*
     * Private
     */

    private static native Object newArray(Class componentType, int length)
	throws NegativeArraySizeException;

    private static native Object multiNewArray(Class componentType,
	int[] dimensions)
	throws IllegalArgumentException, NegativeArraySizeException;


}
