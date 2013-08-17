/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * @(#)Utilities.java	1.1 98/07/31
 *
 * (C) Copyright IBM Corp. 1998 - All Rights Reserved
 */

package javax.swing.text;

/*
 * simple run-length-encoding of byte arrays
 * runs of same value up to 0x7f in length written as 0x80 | count, value
 * runs of mixed values up to 0x7f in length written as count, values[count]
 */

import java.util.Random;

class RLEUtilities {

  static final boolean debug = false;


  static byte[] writeRLE(byte[] src) {
    // break into runs of similar and dissimilar bytes at runs where three bytes are the same
    //
    byte[] result = new byte[src.length + (src.length / 0x7e) + 1];
    int w = 0;

    int p = 0;
    while (p < src.length) {
      int b0 = src[p];
      boolean match = false;

      int q = p + 1;
      int e = Math.min(src.length, p + 0x7e);

      while (q < e) {
	if (src[q] == b0) {
	  ++q;
	  if (q < e) {
	    if (src[q] == b0) {
	      q -= 2;
	      break;
	    }
	  } else {
	    break;
	  }
	}
	b0 = src[q];
	++q;
      }

      if (q > p) {
	int n = q - p;
	if (debug) {
	  System.out.print(p + ": ");
	  for (int x = p; x < q; ++x) {
	    System.out.print(Integer.toHexString(src[x]) + " ");
	  }
	  System.out.println(); 
	  System.out.println(n + " distinct values");
	}
	result[w++] = (byte)n; // different code
	try {
	  System.arraycopy(src, p, result, w, n);
	}
	catch (ArrayIndexOutOfBoundsException ex) {
	  System.out.println("src len: " + src.length + " p: " + p + " res len: " + result.length + " w: " + w + " n: " + n);
	  throw ex;
	}
	w += n;
      }

      if (q >= src.length) break;

      p = q;
      q = p + 1;
      e = Math.min(src.length, p + 0x7e);
      int b = src[p];

      while (q < e && src[q] == b) ++q;
      
      if (q > p + 2) {
	int n = q - p;
	if (debug) {
	  System.out.print(p + ": ");
	  for (int x = p; x < q; ++x) {
	    System.out.print(Integer.toHexString(src[x]) + " ");
	  }
	  System.out.println(); 
	  System.out.println(n + " common values of " + Integer.toHexString(src[p]));
	}

	result[w++] = (byte)(0x80 | n);
	result[w++] = (byte)b;
	p = q;
      }
    }

    // System.out.println("rle in: " + src.length + " out: " + w);
    byte[] temp = new byte[w];
    System.arraycopy(result, 0, temp, 0, w);

    return temp;
  }

  static byte[] readRLE(byte[] src) {
    byte[] result = new byte[src.length * 4];
    int w = 0;

    int p = 0;
    while (p < src.length) {
      byte code = src[p++];
      int count = code & 0x7f;
      if (w + count > result.length) {
	byte[] temp = new byte[Math.max(w + count, result.length * 2)];
	System.arraycopy(result, 0, temp, 0, w);
	result = temp;
      }

      if ((code & 0x80) != 0) { // same
	byte val = src[p++];
	// System.out.println(w + ": reading " + count + " values of " + Integer.toHexString(val));
	while (--count >= 0) {
	  result[w++] = val;
	}
      }
      else { // distinct
	if (debug) {
	  System.out.print(w + ": reading " + count + " distinct values: ");
	  for (int i = 0; i < count; i++) {
	    byte val = src[p+i];
	    System.out.print(Integer.toHexString(val) + " ");
	  }
	  System.out.println();
	}

	System.arraycopy(src, p, result, w, count);
	p += count;
	w += count;
      }
    }
  
    byte[] temp = new byte[w];
    System.arraycopy(result, 0, temp, 0, w);

    return temp;
  }

  /*
  private static void compareArrays(byte[] olda, byte[] newa) {
    if (newa.length != olda.length) {
      System.out.println("length mismatch, old: " + olda.length + ", new: " + newa.length);
    }

    for (int i = 0; i < olda.length; i++) {
      if (newa[i] != olda[i]) {
	System.out.println("mismatch at: " + i);
	break;
      }
    }
  }

  public static void main(String[] args) {
    Random r = new Random(23);

    int inlen = 0;
    int outlen = 0;
    for (int i = 1; i < 300; ++i) {
      System.out.print(".");
      byte[] b = new byte[i];
      for (int j = 0; j < i; ++j) {
	for (int k = 0; k < i;) {
	  boolean run = (r.nextInt() & 0x1) == 0;
	  int n = ((r.nextInt() % 0x3f) % (i-k)) + 1;
	  if (run) {
	    byte v = (byte)(r.nextInt() % 0xff);
	    while (--n >= 0) {
	      b[k++] = v;
	    }
	  } else {
	    while (--n >= 0) {
	      b[k++] = (byte)(r.nextInt() % 0xff);
	    }
	  }
	}

	byte[] b2 = writeRLE(b);
	inlen += b.length;
	outlen += b2.length;
	compareArrays(b, readRLE(b2));
      }
    }

    System.out.println("inlen: " + inlen + " outlen: " + outlen + " ratio: " + (float)((double)inlen / (double)outlen));
  }
  */
}
