/*
 * @(#)Permuter.java	1.4 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */



import java.util.Random;

/**
 * An object that implements a cheesy pseudorandom permutation of the integers
 * from zero to some user-specified value. (The permutation is a linear
 * function.) 
 *
 * @version 1.4 12/03/01
 * @author Josh Bloch
 */
class Permuter {
    /**
     * The size of the permutation.
     */
    private int modulus;

    /**
     * Nonnegative integer less than n that is relatively prime to m.
     */
    private int multiplier;

    /**
     * Pseudorandom nonnegative integer less than n.
     */
    private int addend = 22;

    public Permuter(int n) {
        if (n<0) {
            throw new IllegalArgumentException();
	}
        modulus = n;
        if (n==1) {
            return;
	}

        // Initialize the multiplier and offset
        multiplier = (int) Math.sqrt(n);
        while (gcd(multiplier, n) != 1) {
            if (++multiplier == n) {
                multiplier = 1;
	    }
	}
    }

    /**
     * Returns the integer to which this permuter maps the specified integer.
     * The specified integer must be between 0 and n-1, and the returned
     * integer will be as well.
     */
    public int map(int i) {
        return (multiplier * i + addend) % modulus;
    }

    /**
     * Calculate GCD of a and b, which are assumed to be non-negative.
     */
    private static int gcd(int a, int b) {
        while(b != 0) {
            int tmp = a % b;
            a = b;
            b = tmp;
        }
        return a;
    }

    /**
     * Simple test.  Takes modulus on command line and prints out permutation.
     */
    public static void main(String[] args) {
        int modulus = Integer.parseInt(args[0]);
        Permuter p = new Permuter(modulus);
        for (int i=0; i<modulus; i++) {
            System.out.print(p.map(i)+" ");
	}
        System.out.println();
    }
}

