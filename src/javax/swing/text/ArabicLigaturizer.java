/*
 * @(#)ArabicLigaturizer.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
/*
 * @(#)ArabicLigaturizer.java	1.1 98/07/31
 *
 * (C) Copyright IBM Corp. 1998 - All Rights Reserved
 */

package javax.swing.text;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * A factory for two arabic ligaturizers.  One generates all the 
 * compatibility arabic ligatures, the other only lam-alef ligatures.
 */

/*
 * Source data is in resource arabiclig.data as an unsigned short count of 
 * the number of chars, followed by the chars.  The fallback ligaturizer
 * generates only the lam-alef ligatures from fef5-fefc.
 */

class ArabicLigaturizer {
  private static Ligaturizer singleton;
  private static Ligaturizer lasingleton;

  static Ligaturizer getInstance() {
    if (singleton == null) {
      singleton = new CharBasedLigaturizer(getData());
    }
    return singleton;
  }

  static Ligaturizer getLamAlefInstance() {
    if (lasingleton == null) {
      lasingleton = new CharBasedLigaturizer(getLamAlefData());
    }
    return lasingleton;
  }

  private ArabicLigaturizer() {
  }

  private static char[] data = null;

  protected static char[] getData() {
    if (data == null) {
      InputStream in = ArabicLigaturizer.class.getResourceAsStream("/arabiclig.data");
      try {
	BufferedInputStream inbuf = new BufferedInputStream(in);
	DataInputStream indat = new DataInputStream(inbuf);
	int len = indat.readInt();
	char[] temp = new char[len];
	for (int i = 0; i < len; i++) {
	  temp[i] = indat.readChar();
	}
	data = temp;
      }
      catch (IOException e) {
	data = getLamAlefData();
      }
    }
    return data;
  }

  private static char[] lamalef = null;

  protected static char[] getLamAlefData() {
    // note "\\u0022" performs the \\ before the unicode expansion
    // (at least in 1.1.6), expanding this to 5 chars
    // so we use \" instead

    if (lamalef == null) {
      lamalef = (
	"\uffff\uffff\u0004\u0007\u0012\u001d\u0028\ufe82" + 
	"\uffff\u0002\u000c\u000f\ufedf\ufef5\u0000\ufee0" + 
	"\ufef6\u0000\ufe84\uffff\u0002\u0017\u001a\ufedf" + 
	"\ufef7\u0000\ufee0\ufef8\u0000\ufe88\uffff\u0002" + 
	"\"\u0025\ufedf\ufef9\u0000\ufee0\ufefa\u0000" +
	"\ufe8e\uffff\u0002\u002d\u0030\ufedf\ufefb\u0000" + 
	"\ufee0\ufefc\u0000").toCharArray();
    }
    return lamalef;
  }

  /*
  static void main(String[] args) {
    char[] src = " \u0644\0627 ".toCharArray();
    
    Ligaturizer al = ArabicLigaturizer.getInstance();
    al = al.restrict(new Ligaturizer.Filter() {
      boolean accepts(char c) {
	return c >= '\ufef5' && c <= '\ufefc';
      }
    });
    System.out.println("restricted ligaturizer");
    System.out.println(al);

    System.out.println("lam alef ligaturizer");
    System.out.println(getLamAlefInstance());
  }
  */
}
