/*
 * @(#)PUBLIC_MEMBER.java	1.11 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
/*
 * File: ./org/omg/CORBA/PUBLIC_MEMBER.java
 * From: ./ir.idl
 * Date: Fri Aug 28 16:03:31 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CORBA;

/**
 * <code>PUBLIC_MEMBER</code> is one of the two constants of typedef
 * <code>Visibility</code> used in the interface repository
 * to identify visibility of a <code>ValueMember</code> type.
 * The other constant is <code>PRIVATE_MEMBER</code>.
 *
 * @author unattributed
 * @version 1.11 02/02/00
 */
public interface PUBLIC_MEMBER {
    /** Constant to define a public member in the <code>ValueMember</code> class */
    final short value = (short) (1L);
};
