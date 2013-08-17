/*
 * @(#)DynUnion.java	1.2 98/06/29
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

/** The DynUnion interface represents a DynAny object which is associated
 *  with an IDL union.
 */

public interface DynUnion extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{
    public boolean set_as_default();

    public void set_as_default(boolean arg);

    public org.omg.CORBA.DynAny discriminator();

    public org.omg.CORBA.TCKind discriminator_kind();

    public org.omg.CORBA.DynAny member();

    public String member_name();

    public void member_name(String arg);

    public org.omg.CORBA.TCKind member_kind();
}
