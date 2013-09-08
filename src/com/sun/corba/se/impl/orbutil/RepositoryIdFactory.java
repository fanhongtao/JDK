/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORB;

public abstract class RepositoryIdFactory
{
    private static final RepIdDelegator_1_3 legacyDelegator
        = new RepIdDelegator_1_3();

    private static final RepIdDelegator_1_3_1 ladybirdDelegator
        = new RepIdDelegator_1_3_1();

    private static final RepIdDelegator currentDelegator
        = new RepIdDelegator();

    /**
     * Returns the latest version RepositoryIdStrings instance
     */
    public static RepositoryIdStrings getRepIdStringsFactory()
    {
        return currentDelegator;
    }

    /**
     * Checks the version of the ORB and returns the appropriate
     * RepositoryIdStrings instance.
     */
    public static RepositoryIdStrings getRepIdStringsFactory(ORB orb)
    {
        if (orb != null) {
            switch (orb.getORBVersion().getORBType()) {
                case ORBVersion.NEWER:
                case ORBVersion.FOREIGN:
                case ORBVersion.JDK1_3_1_01:
                    return currentDelegator;
                case ORBVersion.OLD:
                    return legacyDelegator;
                case ORBVersion.NEW:
                    return ladybirdDelegator;
                default:
                    return currentDelegator;
            }
        } else
            return currentDelegator;
    }

    /**
     * Returns the latest version RepositoryIdUtility instance
     */
    public static RepositoryIdUtility getRepIdUtility()
    {
        return currentDelegator;
    }

    /**
     * Checks the version of the ORB and returns the appropriate
     * RepositoryIdUtility instance.
     */
    public static RepositoryIdUtility getRepIdUtility(ORB orb)
    {
        if (orb != null) {
            switch (orb.getORBVersion().getORBType()) {
                case ORBVersion.NEWER:
                case ORBVersion.FOREIGN:
                case ORBVersion.JDK1_3_1_01:
                    return currentDelegator;
                case ORBVersion.OLD:
                    return legacyDelegator;
                case ORBVersion.NEW:
                    return ladybirdDelegator;
                default:
                    return currentDelegator;
            }
        } else
            return currentDelegator;
    }
}
