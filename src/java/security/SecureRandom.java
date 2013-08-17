/*
 * @(#)SecureRandom.java	1.16 97/01/30
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
 */

public class SecureRandom extends Random {
    private byte[] state;
    private MessageDigest digest;

    /**
     * Used by the empty constructor to seed the SecureRandom under construction.
     */
    private static SecureRandom generatorGenerator;

    /**
     * This empty constructor automatically seeds the generator.  We attempt
     * to provide sufficient seed bytes to completely randomize the internal
     * state of the generator (20 bytes).  Note, however, that our seed
     * generation algorithm has not been thoroughly studied or widely deployed.
     * It relies on counting the number of times that the calling thread
     * can yield while waiting for another thread to sleep for a specified
     * interval. 
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
	if (generatorGenerator == null)
	    generatorGenerator = new SecureRandom(getSeed(20));

	byte seed[] = new byte[20];
	generatorGenerator.nextBytes(seed);
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
	if (state != null)
	    digest.update(state);
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

    private byte[] randomBytes = null;
    private int randomBytesUsed = 0;
    private long counter = 0;	/* # of times we've generated randomBytes */

    /**
     * Generates a user-specified number of random bytes.  This method is
     * used as the basis of all random entities returned by this class
     * (except seed bytes).  Thus, it may be overridden to change the
     * behavior of the class.
     * 
     * @param bytes the array to be filled in with random bytes.
     */

    synchronized public void nextBytes(byte[] bytes) {
	int numRequested = bytes.length;
	int numGot = 0;

	while (numGot < numRequested) {
	    /* If no more random bytes, make some more */
	    if (randomBytes == null || randomBytesUsed == randomBytes.length) {
		digest.update(state);
		randomBytes = digest.digest(longToByteArray(counter++));
		randomBytesUsed = 0;
	    }

	    bytes[numGot++] = randomBytes[randomBytesUsed++];
	}
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
	     retVal[i] = (byte) SeedGenerator.genSeed();

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
