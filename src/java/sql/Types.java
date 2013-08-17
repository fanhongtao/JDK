/*
 * @(#)Types.java	1.15 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 * <P>The class that defines constants that are used to identify generic
 * SQL types, called JDBC types.
 * The actual type constant values are equivalent to those in XOPEN.
 *
 */
public class Types {

	public final static int BIT 		=  -7;
	public final static int TINYINT 	=  -6;
	public final static int SMALLINT	=   5;
	public final static int INTEGER 	=   4;
	public final static int BIGINT 		=  -5;

	public final static int FLOAT 		=   6;
	public final static int REAL 		=   7;
	public final static int DOUBLE 		=   8;

	public final static int NUMERIC 	=   2;
	public final static int DECIMAL		=   3;

	public final static int CHAR		=   1;
	public final static int VARCHAR 	=  12;
	public final static int LONGVARCHAR 	=  -1;

	public final static int DATE 		=  91;
	public final static int TIME 		=  92;
	public final static int TIMESTAMP 	=  93;

	public final static int BINARY		=  -2;
	public final static int VARBINARY 	=  -3;
	public final static int LONGVARBINARY 	=  -4;

	public final static int NULL		=   0;

    /**
     * <code>OTHER</code> indicates that the SQL type is database-specific and
     * gets mapped to a Java object that can be accessed via
     * the methods <code>getObject</code> and <code>setObject</code>.
     */
	public final static int OTHER		= 1111;

        

    /**
     * JDBC 2.0
     *
     * A type representing a Java Object.
     */
        public final static int JAVA_OBJECT         = 2000;

    /**
     * JDBC 2.0
     *
	 * A type based on a built-in type.
     * One of the two user-defined data types (UDTs).
     */
        public final static int DISTINCT            = 2001;
	
    /**
     * JDBC 2.0
     *
	 * A type consisting of attributes that may be any type.
     * One of the two user-defined data types (UDTs).
     */
        public final static int STRUCT              = 2002;

    /**
     * JDBC 2.0
     *
     * A type representing an SQL ARRAY.
     */
        public final static int ARRAY               = 2003;

    /**
     * JDBC 2.0
     *
     * A type representing an SQL Binary Large Object.
     */
        public final static int BLOB                = 2004;

    /**
     * JDBC 2.0
     *
     * A type representing an SQL Character Large Object.
     */
        public final static int CLOB                = 2005;

    /**
     * JDBC 2.0
     *
     * A type representing an SQL REF<structured type>.
     */
        public final static int REF                 = 2006;
        

    // Prevent instantiation
    private Types() {}
}


