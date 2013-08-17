/*
 * @(#)CharBasedLigaturizer.java	1.2 98/09/21
 *
 * Copyright 1998-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
/*
 * @(#)CharBasedLigaturizer.java	1.1 98/07/31
 *
 * (C) Copyright IBM Corp. 1998 - All Rights Reserved
 */

package javax.swing.text;

import sun.awt.font.Ligaturizer.Filter;

class CharBasedLigaturizer extends Ligaturizer {
  private char[] data;
  private Filter filter;

  /**
   * Constructs a new ligaturizer using a ligature tree encoded as a character array.
   * Clients should not modify the data array.
   */
  CharBasedLigaturizer(char[] data) {
    this.data = data;
    this.filter = null;
  }
  
  /*
   * Constructs a new ligaturizer using a ligature tree encoded as a character array.
   * This applies the filter to restrict the ligatures encoded by this tree.  Clients
   * should not modify the data array.
   */
  CharBasedLigaturizer(char[] data, Filter f) {
    char[] temp = (char[])data.clone();
    int len = recursiveRestrict(0, temp, f);

    if (2 * len < temp.length) {
      temp = compress(temp);
    }

    this.data = temp;
    this.filter = f;
  }

  /*
   * Return a ligaturizer that only generates ligatures accepted by
   * the provided filter.
   */
  Ligaturizer restrict(Filter f) {
    if (!f.equals(filter)) {
      return new CharBasedLigaturizer(data, f);
    }
    return this;
  }

  /**
   * Convert length chars starting at start in text to include ligatures.
   * Text is in visual order.  The leftmost character that forms a
   * ligature is replaced by the character for the ligature.  The remaining
   * characters that form the ligature are replaced by U+FFFF.
   *
   * If a ligature explicitly defines use of a combining mark, that mark
   * will be replaced.  Otherwise, a ligature may still form 'around'
   * combining marks.  The characters that form such a ligature will be
   * replaced, but the combining mark or marks remain unchanged and in
   * their original positions.  This allows combining marks to be 
   * placed intelligently on the ligature.
   */
  void ligaturize(char[] text, int start, int length) {
    char[] ligbuf = new char[50];

    // Note: attempt to rewrite to speed up non-lig to non-lig loop
    // *increased* time by 50%, must have confused the jit.  There
    // must be a way to do this and not fool the jit, but perhaps
    // it's not worth finding out.

    int w = start;
    int limit = start + length;
    while (w < limit) {
      int r = w;
      int lig = 0; // root
      // int matchlig = -1;
      // int matchr = r + 1;
      int matchr = -1;
      //    goback:
      do {
        char c = text[r];
        int nextlig = subnode(lig, c);
	if (nextlig != -1) {
          lig = nextlig;
	  ligbuf[r-w] = '\uffff';
	  c = data[lig + 1];
	  if (c != '\uffff') {
	    ligbuf[0] = c;
	    // matchlig = lig;
	    // matchr = r+1;
	    matchr = r;
          }
        }
	//	else if (matchlig == -1) {
	//	  ++w;
	//	  continue goback;
	// }
        else if (isCombiningMark(c)) {
	  ligbuf[r-w] = c;
        } else {
	  break;
        }
      } while (++r < limit);

      //      if (matchlig != -1) {
      //	System.arraycopy(ligbuf, 0, text, w, matchr - w);
      //      }
      // w = matchr;
      if (matchr != -1) {
	System.arraycopy(ligbuf, 0, text, w, matchr - w + 1);
	w = matchr;
      }
      ++w;
    }
  }

  private static boolean isCombiningMark(char ch) {
    return ((((1 << Character.NON_SPACING_MARK) |  
	      (1 << Character.ENCLOSING_MARK) | 
	      (1 << Character.COMBINING_SPACING_MARK)) >> Character.getType(ch)) & 1) != 0;
  }

  // assume no char combines with more than 255 other chars
  // !!! we'll fail otherwise
  // should build pows value into table instead, 
  // and use only for tables longer than a certain length

  private static final int[] exp2 = {
    1, 2, 4, 8, 16, 32, 64, 128, 256
  };

  private static final int[] pows = new int[256];
  static {
    int pow = 0;
    for (int i = 1; i < pows.length; i++) {
      if (i >= exp2[pow]) {
	++pow;
      }
      pows[i] = pow - 1;
    }
  }
      
  // fast binary search
  private int subnode(int pos, char c) {
    int inx;
    int l = data[pos + 2]; // length of subnode index
    if (l == 0) {
      return -1;
    }

    int p = pos + 3; // base of subnode index
    if (l == 1) {
      inx = p;
    } else {
      int pow = pows[l];
      int aux = l - exp2[pow];
      inx = exp2[pow] - 1 + p;
      if (c >= data[data[p + aux]]) {
	inx += aux;
      }

      switch (pow) {
      case 8: if (c < data[data[inx - 128]]) inx -= 128;
      case 7: if (c < data[data[inx - 64]]) inx -= 64;
      case 6: if (c < data[data[inx - 32]]) inx -= 32;
      case 5: if (c < data[data[inx - 16]]) inx-= 16;
      case 4: if (c < data[data[inx - 8]]) inx -= 8;
      case 3: if (c < data[data[inx - 4]]) inx -= 4;
      case 2: if (c < data[data[inx - 2]]) inx -= 2;
      case 1: if (c < data[data[inx - 1]]) inx -= 1;
      case 0: if (c < data[data[inx]]) inx -= 1;
      }
    }

    int n = data[inx];
    if (c == data[n]) {
      return n;
    }
    return -1;
  }

  /*
  // linear search
  private int oldsubnode(int pos, char c) {
    int p = pos + 3;
    int e = p + data[pos + 2];
    while (p < e) {
      int n = data[p];
      char t = data[n];
      if (t == c) {
	return n;
      }
      if (t > c) {
	break;
      }
      ++p;
    }
    return -1;
  }
  */

  /*
   * Apply filter to data in newData starting from node at pos, modifying
   * data and returning the count of the data elements used by the node at pos
   * and the nodes in its subtree.
   *
   * If the filter does not accept the ligature at this node, replace the
   * ligature value with u+ffff.  Recursively call over subtrees to gather
   * count of subtrees leading to a valid ligature, and their total length.
   * Reset the count, and rewrite the first count elements in the subnode 
   * list to hold the positions of these subnodes.
   */
  private static int recursiveRestrict(int pos, char[] newData, Filter f) {
    int len = 0;

    int ncount = 0;
    int count = newData[pos + 2];
    for (int i = 0; i < count; ++i) {
      int subpos = newData[pos + 3 + i];
      int sublen =  recursiveRestrict(subpos, newData, f);
      if (sublen != 0) {
	newData[pos + 3 + ncount] = (char)subpos;
	++ncount;
	len += sublen; // add size of subnode
      }
    }
    len += ncount; // add size of subnode list

    newData[pos + 2] = (char)ncount; // set new length of subnode list

    char lig = newData[pos + 1]; // this ligature
    if (lig != '\uffff') {
      if (f.accepts(lig)) {
	len += 3; // need space for this node if use this ligature
      }
      else {
	newData[pos + 1] = '\uffff'; // remove this ligature
	if (ncount > 0) {
	  len += 3; // even if no ligature here, need space for this node if subtree is valid
	}
      }
    }

    return len;
  }

  /**
   * Compress the data in the array to remove space used by nodes that have
   * been filtered out.  Return the new array.
   */
  private static char[] compress(char[] data) {
    int len = recursiveCompress(data, 0, 0);

    char[] newData = new char[len];
    System.arraycopy(data, 0, newData, 0, len);
    return newData;
  }

  private static int recursiveCompress(char[] data, int pos, int w) {
    data[w] = data[pos];
    data[++w] = data[++pos];
    int count = data[++pos];
    data[++w] = (char)count;
    int base = w;
    w += count + 1;
    while (--count >= 0) {
      int oldpos = data[++pos];
      data[++base] = (char)w;
      w = recursiveCompress(data, oldpos, w);
    }

    return w;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer(super.toString());

    buf.append("[filter: ");
    buf.append(filter);
    //    buf.append(", data: ");
    //    LigatureDataBuilder.dumpUC(new String(data), buf);
    buf.append("]");

    return buf.toString();
  }
}
