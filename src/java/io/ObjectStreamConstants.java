/*
 * @(#)ObjectStreamConstants.java	1.12 98/07/01
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

package java.io;

/**
 *
 * @author  unascribed
 * @version 1.12, 07/01/98
 */
interface ObjectStreamConstants {
    final static short STREAM_MAGIC = (short)0xaced;
    final static short STREAM_VERSION = 5;

    /* Each item in the stream is preceded by a tag
     */
    final static byte TC_BASE = 0x70;
    final static byte TC_NULL = 	(byte)0x70; // Null object reference
    final static byte TC_REFERENCE =	(byte)0x71; // Reference to prev object
    final static byte TC_CLASSDESC = 	(byte)0x72; // Class Descriptor
    final static byte TC_OBJECT = 	(byte)0x73; // new object
    final static byte TC_STRING = 	(byte)0x74; // new String
    final static byte TC_ARRAY = 	(byte)0x75; // new Array
    final static byte TC_CLASS = 	(byte)0x76; // Reference to Class 
    final static byte TC_BLOCKDATA = 	(byte)0x77; // Block of optional data
    final static byte TC_ENDBLOCKDATA =	(byte)0x78; // End of optional data
    final static byte TC_RESET = 	(byte)0x79; // Reset stream context
    final static byte TC_BLOCKDATALONG= (byte)0x7A; // long block data
    final static byte TC_EXCEPTION = 	(byte)0x7B; // exception during write
    final static byte TC_MAX = 		(byte)0x7B;

    /* First wire handle to be assigned. */
    final static int baseWireHandle = 0x7e0000;

    /* Flag bits for ObjectStreamClasses in Stream. */
    final static byte SC_WRITE_METHOD = 0x01;

    final static byte SC_SERIALIZABLE = 0x02;
    final static byte SC_EXTERNALIZABLE = 0x04;
}
