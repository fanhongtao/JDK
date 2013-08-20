/*
 * @(#)DSAKeyPairGenerator.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security.interfaces;

import java.security.*;

/**
 * An interface to an object capable of generating DSA key pairs. 
 *
 * <p>The <code>initialize</code> methods may each be called any number 
 * of times. If no <code>initialize</code> method is called on a 
 * DSAKeyPairGenerator, the default is to generate 1024-bit keys, using 
 * precomputed p, q and g parameters and an instance of SecureRandom as 
 * the random bit source.
 * 
 * <p>Users wishing to indicate DSA-specific parameters, and to generate a key 
 * pair suitable for use with the DSA algorithm typically
 * 
 * <ol>
 * 
 * <li>Get a key pair generator for the DSA algorithm by calling the 
 * KeyPairGenerator <code>getInstance</code> method with "DSA" 
 * as its argument.<p> 
 * 
 * <li>Initialize the generator by casting the result to a DSAKeyPairGenerator
 * and calling one of the 
 * <code>initialize</code> methods from this DSAKeyPairGenerator interface.<p>
 * 
 * <li>Generate a key pair by calling the <code>generateKeyPair</code> 
 * method from the KeyPairGenerator class.
 * 
 * </ol> 
 *
 * <p>Note: it is not always necessary to do do algorithm-specific
 * initialization for a DSA key pair generator. That is, it is not always
 * necessary to call an <code>initialize</code> method in this interface.
 * Algorithm-independent initialization using the <code>initialize</code> method
 * in the KeyPairGenerator
 * interface is all that is needed when you accept defaults for algorithm-specific
 * parameters.
 * 
 * @see java.security.KeyPairGenerator
 */
public interface DSAKeyPairGenerator {

    /**
     * Initializes the key pair generator using p, q and g, the DSA
     * family parameters.
     *
     * @param params the parameters to use to generate the keys.
     *
     * @param random the random bit source to use to generate 
     * key bits.
     *
     * @exception InvalidParameterException if the parameters passed are
     * invalid or null.
     */
   public void initialize(DSAParams params, SecureRandom random)
   throws InvalidParameterException;

    /**
     * Initializes the key pair generator for a given modulus length,
     * without parameters. 
     *
     * <p>If <code>genParams</code> is true, this method will generate new 
     * p, q and g parameters. If it is false, the method will use precomputed
     * parameters for the modulus length requested. If there are no
     * precomputed parameters for that modulus length, an exception will be 
     * thrown. It is guaranteed that there will always be
     * default parameters for modulus lengths of 512 and 1024 bits.
     *
     * @param modlen the modulus length, in bits. Valid values are any
     * multiple of 8 between 512 and 1024, inclusive.
     *
     * @param random the random bit source to use to generate 
     * key bits.
     *
     * @param genParams whether or not to generate new parameters for
     * the modulus length requested.
     *
     * @exception InvalidParameterException if the modulus length is not
     * between 512 and 1024, or if genParams is false and there are
     * not precomputed parameters for the modulus length requested.  
     */
    public void initialize(int modlen, boolean genParams, SecureRandom random)
    throws InvalidParameterException;
}
