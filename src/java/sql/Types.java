/*
 * @(#)Types.java	1.14 98/09/27
 * 
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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


