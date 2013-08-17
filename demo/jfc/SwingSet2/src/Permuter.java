/*
 * @(#)Permuter.java	1.2 99/07/22
 *
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */



import java.util.Random;

/**
 * An object that implements a cheesy pseudorandom permutation of the integers
 * from zero to some user-specified value. (The permutation is a linear
 * function.) 
 *
 * @version 1.2 07/22/99
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

