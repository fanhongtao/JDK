/*
 * @(#)NewArabicShaping.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
/*
 * @(#)NewArabicShaping.java	1.1 98/07/31
 *
 * (C) Copyright IBM Corp. 1998 - All Rights Reserved
 */

package javax.swing.text;

/*
 * The new ligature model performs ligatures after shaping.  This is mainly because there
 * don't seem to be a full complement of shapes for all the ligatures.  For example, there
 * are ligatures with only medial forms-- presumably these ligatures don't form in initial
 * or final positions.  Therefore we need to know the position before trying to form the
 * ligature.  We do this by shaping the component parts first, and then matching the shaped
 * versions when trying to fit a ligature.  This makes for more ligature rules, though.
 *
 * Therefore this version does not do ligatures at all, just shaping.
 */

class NewArabicShaping {

  // arabic shaping type code

  // shaping bit masks
  static final int MASK_SHAPE_RIGHT = 1; // if this bit set, shapes to right
  static final int MASK_SHAPE_LEFT = 2; // if this bit set, shapes to left
  static final int MASK_TRANSPARENT = 4; // if this bit set, is transparent (ignore other bits)
  static final int MASK_NOSHAPE = 8; // if this bit set, don't shape this char, i.e. tatweel

  // shaping values
  static final int VALUE_NONE = 0;
  static final int VALUE_RIGHT = MASK_SHAPE_RIGHT;
  static final int VALUE_LEFT = MASK_SHAPE_LEFT;
  static final int VALUE_DUAL = MASK_SHAPE_RIGHT | MASK_SHAPE_LEFT;
  static final int VALUE_TRANSPARENT = MASK_TRANSPARENT;
  static final int VALUE_NOSHAPE_DUAL = MASK_NOSHAPE | VALUE_DUAL;
  static final int VALUE_NOSHAPE_NONE = MASK_NOSHAPE;

  // shape types for 0622 to 06d5 inclusive from unicode std 2.1
  // correction to table in std, 0671 is r, not u.
  // everything below 0622 is non-shaping.
  // 06d6 to 06f9 either n or t based on description in std, but
  // not listed as such.
  // n - non-joining
  // r - right-joining
  // d - dual-joining
  // c - join-causing (tatweel), dual-joining and non-shaping
  // t - transparent
  // . - undefined code point (non-joining and non-shaping)

  static final String shapeTypes = 
  // 2              3               4               5               6
  // 123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0
    "nrrrrdrdrdddddrrrrdddddddd.....cdddddddrrdtttttttt.............n" +

  // 6              7               8               9               a
  // 123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0
    "nnnnnnnnnnnnn..trrrurrrddddddddddddddddrrrrrrrrrrrrrrrrrrddddddd" +

  // a              b               c               d               e
  // 123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0
    "ddddddddddddddddddddddd..ddddd.rdrrrrrrrrrrdrd.ddrr.untttttttttt" +

  // e              f               
  // 123456789abcdef0123456789abcdef
    "ttttnnttntttt..nnnnnnnnnn......";

  static final byte[] shapeVals = new byte[shapeTypes.length()];
  static {
    for (int i = 0; i < shapeVals.length; i++) {
      byte v = VALUE_NOSHAPE_NONE;
      switch (shapeTypes.charAt(i)) {
      case 'c': v = VALUE_NOSHAPE_DUAL; break;
      case 'd': v = VALUE_DUAL; break;
      case 'n': v = VALUE_NONE; break;
      case 'r': v = VALUE_RIGHT; break;
      case 't': v = VALUE_TRANSPARENT; break;
      default: break;
      }
      shapeVals[i] = v;
    }
  }

  static boolean isCombiningOrFormat(char ch) {
    return ((((1 << Character.NON_SPACING_MARK) |  
	      (1 << Character.FORMAT) |
	      (1 << Character.ENCLOSING_MARK) | 
	      (1 << Character.COMBINING_SPACING_MARK)) >> Character.getType(ch)) & 1) != 0;
  }

  static int getShapeType(char c) {
    // shaping array holds types for arabic chars between 0621 and 0700
    // other values are either unshaped, or transparent if a mark or format
    // coce, except for format codes 200c (zero-width non-joiner) and 200d 
    // (dual-width joiner) which are both unshaped and non_joining or
    // dual-joining, respectively.

    if (c >= '\u0621' && c <= '\u200d') {
      if (c < '\u0700') {
	return shapeVals[c - '\u0621'];
      } else if (c == '\u200c') {
	return VALUE_NOSHAPE_NONE;
      } else if (c == '\u200d') {
	return VALUE_NOSHAPE_DUAL;
      }
    }

    return isCombiningOrFormat(c) ? VALUE_TRANSPARENT : VALUE_NOSHAPE_NONE;
  }

  /*
   * Chars in visual order.
   * leftshape is shaping code of char to visual left of range
   * rightshape is shaping code of char to visual right of range
   */
  static void shape(char[] chars, int leftType, int rightType) {

    // iterate in visual order from left to right
    // 
    // the effective left char is the most recently encountered 
    // non-transparent char
    //
    // four boolean states:
    //   the effective left char shapes
    //   the effective left char causes right shaping
    //   the current char shapes
    //   the current char causes left shaping
    // 
    // if both cause shaping, then
    //   left += 1 (isolate to final, or initial to medial)
    //   cur += 2 (isolate to initial)

    // eln is effective left logical index
    int eln = -1;

    boolean leftShapes = false;
    boolean leftCauses = (leftType & MASK_SHAPE_RIGHT) != 0;

    for (int n = 0; n < chars.length; n++) {
      char c = chars[n];
      int t = getShapeType(c);

      if ((t & MASK_TRANSPARENT) != 0) {
	continue;
      }

      boolean curShapes = (t & MASK_NOSHAPE) == 0;
      boolean curCauses = (t & MASK_SHAPE_LEFT) != 0;

      if (leftCauses && curCauses) {
	if (leftShapes) {
	  chars[eln] += 1;
	}
	if (curShapes) {
	  chars[n] = (char)(getToIsolateShape(c) + 2);
	}
      } else {
	if (curShapes) {
	  chars[n] = getToIsolateShape(c);
	}
      }

      leftShapes = curShapes;
      leftCauses = (t & MASK_SHAPE_RIGHT) != 0;
      eln = n;
    }

    if (leftShapes && leftCauses && (rightType & MASK_SHAPE_LEFT) != 0) {
      chars[eln] += 1;
    }
  }

  /*
  static void dumpIsoTable() {
    char[] out = { '\\', 'u', 'x', 'x', 'x', 'x' };

    System.out.print("static String iso = \"");
    for (char c = '\u0621'; c < '\u06d4'; c++) {
      charHex(getToIsolateShape(c), out, 2, 4);
      System.out.print(new String(out));
    }
    System.out.println( "\";");
  }
*/

  static char[] iso = "\ufe80\ufe81\ufe83\ufe85\ufe87\ufe89\ufe8d\ufe8f\ufe93\ufe95\ufe99\ufe9d\ufea1\ufea5\ufea9\ufeab\ufead\ufeaf\ufeb1\ufeb5\ufeb9\ufebd\ufec1\ufec5\ufec9\ufecd\u063b\u063c\u063d\u063e\u063f\u0640\ufed1\ufed5\ufed9\ufedd\ufee1\ufee5\ufee9\ufeed\ufeef\ufef1\u064b\u064c\u064d\u064e\u064f\u0650\u0651\u0652\u0653\u0654\u0655\u0656\u0657\u0658\u0659\u065a\u065b\u065c\u065d\u065e\u065f\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669\u066a\u066b\u066c\u066d\u066e\u066f\u0670\ufb50\u0672\u0673\u0674\u0675\u0676\ufbdd\u0678\ufb66\ufb5e\ufb52\u067c\u067d\ufb56\ufb62\ufb5a\u0681\u0682\ufb76\ufb72\u0685\ufb7a\ufb7e\ufb88\u0689\u068a\u068b\ufb84\ufb82\ufb86\u068f\u0690\ufb8c\u0692\u0693\u0694\u0695\u0696\u0697\ufb8a\u0699\u069a\u069b\u069c\u069d\u069e\u069f\u06a0\u06a1\u06a2\u06a3\ufb6a\u06a5\ufb6e\u06a7\u06a8\ufb8e\u06aa\u06ab\u06ac\ufbd3\u06ae\ufb92\u06b0\ufb9a\u06b2\ufb96\u06b4\u06b5\u06b6\u06b7\u06b8\u06b9\ufb9e\ufba0\u06bc\u06bd\ufbaa\u06bf\ufba4\ufba6\u06c2\u06c3\u06c4\ufbe0\ufbd9\ufbd7\ufbdb\ufbe2\u06ca\ufbde\ufbfc\u06cd\u06ce\u06cf\ufbe4\u06d1\ufbae\ufbb0".toCharArray();

  static char getToIsolateShape(char ch) {
    if (ch < '\u0621' || ch > '\u06d3') {
      return ch;
    }
    return iso[ch - '\u0621'];
  }
}
