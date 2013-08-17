/*
 * @(#)SecureRandom.java	1.19 98/08/06
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
 
package java.security;

import java.util.*;

/**
 * <p>This class provides a crytpographically strong pseudo-random number
 * generator based on the SHA-1 hash algorithm.
 *
 * <p>The calls inherited from Random will be implemented in terms of the
 * strengthened functionality.
 *
 * @see java.util.Random
 * 
 * @version 1.2 96/09/15
 * @author Benjamin Renaud
 * @author Josh Bloch 
 * @author Gadi Guy
 */

public class SecureRandom extends Random {

    /**
     * Used by the empty constructor to seed the SecureRandom under
     * construction.
     */
    private static SecureRandom seeder;
    private static final int DIGEST_SIZE = 20;
    private transient MessageDigest digest;
    private byte[] state;
    private byte[] remainder;
    private int remCount;

    /**
     * This empty constructor automatically seeds the generator.  We attempt
     * to provide sufficient seed bytes to completely randomize the internal
     * state of the generator (20 bytes).  Note, however, that our seed
     * generation algorithm has not been thoroughly studied or widely deployed.
     * 
     * <p>The first time this constructor is called in a given Virtual Machine,
     * it may take several seconds of CPU time to seed the generator, depending
     * on the underlying hardware.  Successive calls run quickly because they
     * rely on the same (internal) pseudo-random number generator for their
     * seed bits.
     *
     * <p>The seeding procedure implemented by this constructor ensures that
     * the sequence of pseudo-random bytes produced by each SecureRandom 
     * instance yields no useful information about the byte-sequence produced
     * by any other instance.  If however, the user wishes to produce multiple 
     * instances with truly unrelated seeds, the following code yields
     * the desired result (at substantial CPU cost per instance!):<p>
     *
     * <pre>
     * SecureRandom rnd = new SecureRandom(SecureRandom.getSeed(20));
     * </pre>
     */
    public SecureRandom() {
	this(nextSeed());
    }

    /**
     * This call, used exclusively by the empty constructor, returns 20 seed
     * bytes to seed the SecureRandom instance under construction.  The first
     * time this method is called, creates a class-wide generator-generator.
     * This involves generating 20 "real-random" bytes with getSeed, which is
     * very time consuming!
     */
    private synchronized static byte[] nextSeed() {
	if (seeder == null) {
	    seeder = new SecureRandom(getSeed(20));
	    seeder.setSeed(SeedGenerator.getSystemEntropy());
	}

	byte seed[] = new byte[20];
	seeder.nextBytes(seed);
	return seed;
    }


    /**
     * This constructor uses a user-provided seed in preference to the 
     * self-seeding algorithm referred to in the empty constructor 
     * description. It may be preferable to the empty constructor if the 
     * caller has access to high-quality random bytes from some physical 
     * device (for example, a radiation detector or a noisy diode).
     * 
     * @param seed the seed.
     */
    public SecureRandom(byte seed[]) {
	/*
	 * This call to our superclass constructor will result in a call
	 * to our own setSeed method, which will return immediately when
	 * it is passed zero.
	 */
	super(0);

	try {
	    digest = MessageDigest.getInstance("SHA");
	} catch (NoSuchAlgorithmException e) {
	    throw new InternalError("internal error: SHA-1 not available.");
	}

	setSeed(seed);
    }

    /**
     * Reseeds this random object. The given seed supplements, rather than
     * replaces, the existing seed. Thus, repeated calls are guaranteed
     * never to reduce randomness.
     *
     * @param seed the seed.
     */
    synchronized public void setSeed(byte[] seed) {
	if (state != null) {
	    digest.update(state);
	    for (int i = 0; i < state.length; i++)
		state[i] = 0;
	}
	state = digest.digest(seed);
    }

    /**
     * Reseeds this random object, using the eight bytes contained 
     * in the given <code>long seed</code>. The given seed supplements, 
     * rather than replaces, the existing seed. Thus, repeated calls 
     * are guaranteed never to reduce randomness. 
     * 
     * <p>This method is defined for compatibility with 
     * <code>java.util.Random</code>.
     *
     * @param seed the seed.
     */
    public void setSeed(long seed) {
	/* 
	 * Ignore call from super constructor (as well as any other calls
	 * unfortunate enough to be passing 0).  It's critical that we
	 * ignore call from superclass constructor, as digest has not
	 * yet been initialized at that point.
	 */
	if (seed != 0)
	    setSeed(longToByteArray(seed));
    }

    private static void updateState(byte[] state, byte[] output) {
	int last = 1;
	int v = 0;
	byte t = 0;
	boolean zf = false;
 
	// state(n + 1) = (state(n) + output(n) + 1) % 2^160;
	for (int i = 0; i < state.length; i++) {
	    // Add two bytes
	    v = (int)state[i] + (int)output[i] + last;
	    // Result is lower 8 bits
	    t = (byte)v;
	    // Store result. Check for state collision.
	    zf = zf | (state[i] != t);
	    state[i] = t;
	    // High 8 bits are carry. Store for next iteration.
	    last = v >> 8;
	}
 
	// Make sure at least one bit changes!
	if (!zf)
	   state[0]++;
    }

    /**
     * Generates a user-specified number of random bytes.  This method is
     * used as the basis of all random entities returned by this class
     * (except seed bytes).  Thus, it may be overridden to change the
     * behavior of the class.
     * 
     * @param bytes the array to be filled in with random bytes.
     */

    synchronized public void nextBytes(byte[] result) {
	int index = 0;
	int todo;
	byte[] output = remainder;
 
	// Use remainder from last time
	int r = remCount;
	if (r > 0) {
	    // How many bytes?
	    todo = (result.length - index) < (DIGEST_SIZE - r) ?
			(result.length - index) : (DIGEST_SIZE - r);
	    // Copy the bytes, zero the buffer
	    for (int i = 0; i < todo; i++) {
		result[i] = output[r];
		output[r++] = 0;
	    }
	    remCount += todo;
	    index += todo;
	}
 
	// If we need more bytes, make them.
	while (index < result.length) {
	    // Step the state
	    digest.update(state);
	    output = digest.digest();
	    updateState(state, output);
 
	    // How many bytes?
	    todo = (result.length - index) > DIGEST_SIZE ?
		DIGEST_SIZE : result.length - index;
	    // Copy the bytes, zero the buffer
	    for (int i = 0; i < todo; i++) {
		result[index++] = output[i];
		output[i] = 0;
	    }
	    remCount += todo;
	}
 
	// Store remainder for next time
	remainder = output;
	remCount %= DIGEST_SIZE;
    }

    /**
     * Generates an integer containing the user-specified number of
     * pseudo-random bits (right justified, with leading zeros).  This
     * method overrides a <code>java.util.Random</code> method, and serves 
     * to provide a source of random bits to all of the methods inherited 
     * from that class (for example, <code>nextInt</code>, 
     * <code>nextLong</code>, and <code>nextFloat</code>).
     *
     * @param numBits number of pseudo-random bits to be generated, where 
     * 0 <= <code>numBits</code> <= 32.
     */
    final protected int next(int numBits) {
	int numBytes = (numBits+7)/8;
	byte b[] = new byte[numBytes];
        int next = 0;

	nextBytes(b);
	for (int i=0; i<numBytes; i++)
	    next = (next << 8) + (b[i] & 0xFF);

        return next >>> (numBytes*8 - numBits);
    }

    /**
     * Returns the given number of seed bytes, computed using the seed
     * generation algorithm that this class uses to seed itself.  This
     * call may be used to seed other random number generators.  While
     * we attempt to return a "truly random" sequence of bytes, we do not 
     * know exactly how random the bytes returned by this call are.  (See 
     * the empty constructor <a href = "#SecureRandom">SecureRandom</a>
     * for a brief description of the underlying algorithm.)
     * The prudent user will err on the side of caution and get extra
     * seed bytes, although it should be noted that seed generation is
     * somewhat costly.
     *
     * @param numBytes the number of seed bytes to generate.
     * 
     * @return the seed bytes.
     */
     public static byte[] getSeed(int numBytes) {
	 byte[] retVal = new byte[numBytes];

	 for (int i=0; i<numBytes; i++)
	     retVal[i] = (byte) SeedGenerator.getByte();

	 return retVal;
     }


    /**
     * Helper function to convert a long into a byte array (least significant
     * byte first).
     */
    private static byte[] longToByteArray(long l) {
	byte[] retVal = new byte[8];

	for (int i=0; i<8; i++) {
	    retVal[i] = (byte) l;
	    l >>= 8;
	}

	return retVal;
    }
}
