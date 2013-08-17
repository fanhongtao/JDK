/*
 * @(#)DnDConstants.java	1.4 98/06/19
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.dnd;

/**
 * @version 1.4
 * @since JDK1.2
 *
 */

public final class DnDConstants {

    private DnDConstants() {} // define null private constructor.

    /**
     * DnD operation actions/verbs ...
     */

    public static final int ACTION_NONE		= 0x0;
    public static final int ACTION_COPY		= 0x1;
    public static final int ACTION_MOVE		= 0x2;
    public static final int ACTION_COPY_OR_MOVE	= ACTION_COPY | ACTION_MOVE;

    /**
     * DnD operation action/verb
     *
     * The link verb is found in many, if not all native DnD platforms, the
     * actual interpretation of LINK semantics is not only highly platform
     * dependent but is also application dependent. Broadly speaking the
     * semantic is "do not copy, or move the operand, but create a reference
     * to it". Defining the meaning of "reference" is where ambiguity is
     * introduced.
     *
     * The vern is provided for completness. but its use is not recommended
     * for DnD operations between logically distinct applications where 
     * misinterpretation of the operations semantics could lead to confusing
     * results for the user.
     */

    public static final int ACTION_LINK	        = 0x40000000;
    public static final int ACTION_REFERENCE    = ACTION_LINK;

}
