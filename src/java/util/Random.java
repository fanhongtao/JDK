/*
 * @(#)Random.java	1.17 98/07/01
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

package java.util;

/**
 * An instance of this class is used to generate a stream of 
 * pseudorandom numbers. The class uses a 48-bit seed, which is 
 * modified using a linear congruential formula. (See Donald Knuth, 
 * <i>The Art of Computer Programming, Volume 2</i>, Section 3.2.1.) 
 * <p>
 * If two instances of <code>Random</code> are created with the same 
 * seed, and the same sequence of method calls is made for each, they 
 * will generate and return identical sequences of numbers. 
 * <p>
 * Many applications will find the <code>random</code> method in 
 * class <code>Math</code> simpler to use.
 *
 * @author  Frank Yellin
 * @version 1.17, 07/01/98
 * @see     java.lang.Math#random()
 * @since   JDK1.0
 */
public
class Random implements java.io.Serializable {
    private long seed;
    private final static long multiplier = 0x5DEECE66DL;
    private final static long addend = 0xBL;
    private final static long mask = (1L << 48) - 1;

    /** 
     * Creates a new random number generator. Its seed is initialized to 
     * a value based on the current time. 
     *
     * @see     java.lang.System#currentTimeMillis()
     * @since   JDK1.0
     */
    public Random() { this(System.currentTimeMillis()); }

    /** 
     * Creates a new random number generator using a single 
     * <code>long</code> seed. 
     *
     * @param   seed   the initial seed.
     * @see     java.util.Random#setSeed(long)
     * @since   JDK1.0
     */
    public Random(long seed) {
        setSeed(seed);
    }

    /**
     * Sets the seed of this random number generator using a single 
     * <code>long</code> seed. 
     *
     * @param   seed   the initial seed.
     * @since   JDK1.0
     */
    synchronized public void setSeed(long seed) {
        this.seed = (seed ^ multiplier) & mask;
    	haveNextNextGaussian = false;
    }

    /**
     * Generates the next pseudorandom number. Subclass should
     * override this, as this is used by all other methods.
     *
     * @param   bits random bits
     * @return  the next pseudorandom value from this random number generator's sequence.
     * @since   JDK1.1
     */
    synchronized protected int next(int bits) {
        long nextseed = (seed * multiplier + addend) & mask;
        seed = nextseed;
        return (int)(nextseed >>> (48 - bits));
    }

    private static final int BITS_PER_BYTE = 8;
    private static final int BYTES_PER_INT = 4;

    /**
     * Generates a user specified number of random bytes.
     *
     * @since   JDK1.1
     */
    public void nextBytes(byte[] bytes) {
	int numRequested = bytes.length;

	int numGot = 0, rnd = 0;

	while (true) {
	    for (int i = 0; i < BYTES_PER_INT; i++) {
		if (numGot == numRequested)
		    return;

		rnd = (i==0 ? next(BITS_PER_BYTE * BYTES_PER_INT)
		            : rnd >> BITS_PER_BYTE);
		bytes[numGot++] = (byte)rnd;
	    }
	}
    }

    /**
     * Returns the next pseudorandom, uniformly distributed <code>int</code>
     * value from this random number generator's sequence.
     *
     * @return  the next pseudorandom, uniformly distributed <code>int</code>
     *          value from this random number generator's sequence.
     * @since   JDK1.0
     */
    public int nextInt() {  return next(32); }

    /**
     * Returns the next pseudorandom, uniformly distributed <code>long</code>
     * value from this random number generator's sequence.
     *
     * @return  the next pseudorandom, uniformly distributed <code>long</code>
     *          value from this random number generator's sequence.
     * @since   JDK1.0
     */
    public long nextLong() {
        // it's okay that the bottom word remains signed.
        return ((long)(next(32)) << 32) + next(32);
    }

    /**
     * Returns the next pseudorandom, uniformly distributed <code>float</code>
     * value between <code>0.0</code> and <code>1.0</code> from this random
     * number generator's sequence.
     *
     * @return  the next pseudorandom, uniformly distributed <code>float</code>
     *          value between <code>0.0</code> and <code>1.0</code> from this
     *          random number generator's sequence.
     * @since   JDK1.0
     */
    public float nextFloat() {
        int i = next(24);
        return i / ((float)(1 << 24));
    }

    /**
     * Returns the next pseudorandom, uniformly distributed 
     * <code>double</code> value between <code>0.0</code> and
     * <code>1.0</code> from this random number generator's sequence.
     *
     * @return  the next pseudorandom, uniformly distributed 
     *          <code>double</code> value between <code>0.0</code> and
     *          <code>1.0</code> from this random number generator's sequence.
     * @since   JDK1.0
     */
    public double nextDouble() {
        long l = ((long)(next(26)) << 27) + next(27);
        return l / (double)(1L << 53);
    }

    private double nextNextGaussian;
    private boolean haveNextNextGaussian = false;

    /**
     * Returns the next pseudorandom, Gaussian ("normally") distributed
     * <code>double</code> value with mean <code>0.0</code> and standard
     * deviation <code>1.0</code> from this random number generator's sequence.
     *
     * @return  the next pseudorandom, Gaussian ("normally") distributed
     *          <code>double</code> value with mean <code>0.0</code> and
     *          standard deviation <code>1.0</code> from this random number
     *          generator's sequence.
     * @since   JDK1.0
     */
    synchronized public double nextGaussian() {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if (haveNextNextGaussian) {
    	    haveNextNextGaussian = false;
    	    return nextNextGaussian;
    	} else {
            double v1, v2, s;
    	    do { 
                v1 = 2 * nextDouble() - 1; // between -1 and 1
            	v2 = 2 * nextDouble() - 1; // between -1 and 1 
                s = v1 * v1 + v2 * v2;
    	    } while (s >= 1);
    	    double multiplier = Math.sqrt(-2 * Math.log(s)/s);
    	    nextNextGaussian = v2 * multiplier;
    	    haveNextNextGaussian = true;
    	    return v1 * multiplier;
        }
    }
}     
