/*
 * @(#)AlgorithmParameterGeneratorSpi.java	1.5 98/09/04
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

package java.security;

import java.security.spec.AlgorithmParameterSpec;

/**
 * This class defines the <i>Service Provider Interface</i> (<b>SPI</b>)
 * for the <code>AlgorithmParameterGenerator</code> class, which 
 * is used to generate a set of parameters to be used with a certain algorithm.
 *
 * <p> All the abstract methods in this class must be implemented by each 
 * cryptographic service provider who wishes to supply the implementation
 * of a parameter generator for a particular algorithm.
 *
 * <p> In case the client does not explicitly initialize the
 * AlgorithmParameterGenerator (via a call to an <code>engineInit</code>
 * method), each provider must supply (and document) a default initialization.
 * For example, the Sun provider uses a default modulus prime size of 1024
 * bits for the generation of DSA parameters.
 *
 * @author Jan Luehe
 *
 * @version 1.5, 00/05/10
 *
 * @see AlgorithmParameterGenerator
 * @see AlgorithmParameters
 * @see java.security.spec.AlgorithmParameterSpec
 *
 * @since JDK1.2
 */

public abstract class AlgorithmParameterGeneratorSpi {

    /**
     * Initializes this parameter generator for a certain size
     * and source of randomness.
     *
     * @param size the size (number of bits).
     * @param random the source of randomness.
     */
    protected abstract void engineInit(int size, SecureRandom random);

    /**
     * Initializes this parameter generator with a set of
     * algorithm-specific parameter generation values.
     *
     * @param params the set of algorithm-specific parameter generation values.
     * @param random the source of randomness.
     *
     * @exception InvalidAlgorithmParameterException if the given parameter
     * generation values are inappropriate for this parameter generator.
     */
    protected abstract void engineInit(AlgorithmParameterSpec genParamSpec,
				       SecureRandom random)
	throws InvalidAlgorithmParameterException;

    /**
     * Generates the parameters.
     *
     * @return the new AlgorithmParameters object.
     */
    protected abstract AlgorithmParameters engineGenerateParameters();
}
