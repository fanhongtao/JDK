/*
 * @(#)InputStream.java	1.21 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.omg.CORBA.portable;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;

/**
 * InputStream is the Java API for reading IDL types
 * from CDR marshal streams. These methods are used by the ORB to
 * unmarshal IDL types as well as to extract IDL types out of Anys.
 * The <code>_array</code> versions of the methods can be directly
 * used to read sequences and arrays of IDL types.
 *
 * @version 1.12, 04/22/98
 * @since   JDK1.2
 */

public abstract class InputStream extends java.io.InputStream
{
    public abstract boolean	read_boolean();
    public abstract char	read_char();
    public abstract char	read_wchar();
    public abstract byte	read_octet();
    public abstract short	read_short();
    public abstract short	read_ushort();
    public abstract int		read_long();
    public abstract int		read_ulong();
    public abstract long	read_longlong();
    public abstract long	read_ulonglong();
    public abstract float	read_float();
    public abstract double	read_double();
    public abstract String	read_string();
    public abstract String	read_wstring();

    public abstract void	read_boolean_array(boolean[] value, int offset, int length);
    public abstract void	read_char_array(char[] value, int offset, int length);
    public abstract void	read_wchar_array(char[] value, int offset, int length);
    public abstract void	read_octet_array(byte[] value, int offset, int length);
    public abstract void	read_short_array(short[] value, int offset, int length);
    public abstract void	read_ushort_array(short[] value, int offset, int length);
    public abstract void	read_long_array(int[] value, int offset, int length);
    public abstract void	read_ulong_array(int[] value, int offset, int length);
    public abstract void	read_longlong_array(long[] value, int offset, int length);
    public abstract void	read_ulonglong_array(long[] value, int offset, int length);
    public abstract void	read_float_array(float[] value, int offset, int length);
    public abstract void	read_double_array(double[] value, int offset, int length);

    public abstract org.omg.CORBA.Object read_Object();
    public abstract TypeCode	read_TypeCode();
    public abstract Any		read_any();

    /**
     * @deprecated Deprecated by CORBA 2.2.
     */
    public abstract Principal	read_Principal();


	/**
	 * @see <a href="package-summary.html#unimpl"><code>portable</code>
	 * package comments for unimplemented features</a>
	 */
    public int read() throws java.io.IOException {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

	/**
	 * @see <a href="package-summary.html#unimpl"><code>portable</code>
	 * package comments for unimplemented features</a>
	 */
    public java.math.BigDecimal read_fixed() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

	/**
	 * @see <a href="package-summary.html#unimpl"><code>portable</code>
	 * package comments for unimplemented features</a>
	 */
    public org.omg.CORBA.Context read_Context() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
    /*
     * The following methods were added by orbos/98-04-03: Java to IDL
     * Mapping. These are used by RMI over IIOP.
     */

    /**
     * read_Object unmarshals an object and returns a CORBA Object
     * which is an instance of the class passed as its argument.
     * This class is the stub class of the expected type.
	 *
	 * @see <a href="package-summary.html#unimpl"><code>portable</code>
	 * package comments for unimplemented features</a>
     */
    public  org.omg.CORBA.Object read_Object(java.lang.Class
						     clz) {
	throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Return the ORB that created this InputStream
	 *
	 * @see <a href="package-summary.html#unimpl"><code>portable</code>
	 * package comments for unimplemented features</a>
     */
    public org.omg.CORBA.ORB orb() {
	throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}
