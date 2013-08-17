/*
 * @(#)SecureRandomSpi.java	1.3 98/12/03
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
 
package java.security;

/**
 * This class defines the <i>Service Provider Interface</i> (<b>SPI</b>)
 * for the <code>SecureRandom</code> class.
 * All the abstract methods in this class must be implemented by each
 * service provider who wishes to supply the implementation
 * of a cryptographically strong pseudo-random number generator.
 *
 * @version 1.3 98/12/03
 *
 * @see SecureRandom
 * @since JDK1.2
 */

public abstract class SecureRandomSpi implements java.io.Serializable {

    /**
     * Reseeds this random object. The given seed supplements, rather than
     * replaces, the existing seed. Thus, repeated calls are guaranteed
     * never to reduce randomness.
     *
     * @param seed the seed.
     */
    protected abstract void engineSetSeed(byte[] seed);

    /**
     * Generates a user-specified number of random bytes.
     * 
     * @param bytes the array to be filled in with random bytes.
     */
    protected abstract void engineNextBytes(byte[] bytes);

    /**
     * Returns the given number of seed bytes.  This call may be used to
     * seed other random number generators.
     *
     * @param numBytes the number of seed bytes to generate.
     * 
     * @return the seed bytes.
     */
     protected abstract byte[] engineGenerateSeed(int numBytes);
}
