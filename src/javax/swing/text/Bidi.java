/*
 * @(#)Bidi.java	1.2 98/09/21
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
 * @(#)Bidi.java	1.2 98/09/21
 *
 * (C) Copyright Taligent, Inc. 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1997, 1998 - All Rights Reserved
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 */

package javax.swing.text;

import java.io.*;

/**
 * Bidi implementation.
 */
final class Bidi {
  private boolean ltr;
  private byte[] dirs;
  private byte[] levels;
  private int[] l2vMap;
  private int[] v2lMap;

  /**
   * Return the number of elements the bidi represents.
   */
  int getLength() {
    return levels.length;
  }

  /**
   * Return true if the base run direction defined for this bidi is
   * left to right, false if it is right to left.
   */
  boolean isDirectionLTR() {
    return ltr;
  }

  /**
   * Return a mapping of the bidirectional information from
   * logical to visual position.  If mapping is canonical, return
   * null;
   */
  int[] getLogicalToVisualMap() {
    if (l2vMap == null) {
      l2vMap = getInverseOrder(getVisualToLogicalMap());
    }
    return l2vMap;
  }

  /**
   * Return a mapping of the bidirectional information from
   * visual to logical position.  If mapping is canonical, return
   * null;
   */
  int[] getVisualToLogicalMap() {
    if (v2lMap == null) {
      v2lMap = createVisualToLogicalMap(levels);
    }
    return v2lMap;
  }

  /**
   * Return the resolved level array.
   */
  byte[] getLevels() {
    return levels;
  }

  /**
   * Return a bidi representing the given subrange of this bidi, with
   * line reordering applied to counterdirectional trailing whitespace.
   */
  Bidi createLineBidi(int start, int limit) {
    byte[] newLevels = new byte[limit - start];
    System.arraycopy(levels, start, newLevels, 0, newLevels.length);

    if (dirs != null) {
      byte x = (byte)(ltr ? 0 : 1);
      for (int i = newLevels.length; --i >= 0;) {
	if (newLevels[i] == x || dirs[start + i] != WS) {
	  break;
	}
	newLevels[i] = x;
      }
    }

    return new Bidi(newLevels, ltr);
  }

  /**
   * Return the level of the element at pos.
   */
  int getLevelAt(int pos) {
    return levels[pos];
  }

  /**
   * Get the limit of the level run starting at start.
   */
  int getLevelLimit(int start) {
    byte l = levels[start];
    while (++start < levels.length && levels[start] == l) {}
    return start;
  }

  /**
   * Create bidi information about the provided paragraph of text.
   * Use bidi rules to default line direction.
   */
  Bidi(char[] text) {
    this(text, defaultIsLTR(text, 0, text.length));
  }

  /**
   * Create bidi information about the provided paragraph of text.
   * Embedding codes in the text will be processed.
   * @param text the characters in the paragraph.
   * @param ltr true if the paragraph run direction is LTR,
   * false if it is RTL.
   */
  Bidi(char[] text, boolean ltr) {
    this(text, getEmbeddingArray(text, ltr), ltr);
  }

  /**
   * Create bidi information about the provided paragraph of text, using the
   * provided embedding information.  Explicit embedding codes in the text, if
   * any, are not processed.
   * @param text the characters in the paragraph.
   * @param embs the embedding data.  Values from 0-15 are embeddings at the
   * indicated level.  Values from 16-31 are overrides at the corresponding levels.
   * This may modify the data in embs.
   * @param ltr true if the run direction for the paragraph is LTR,
   * false if it is RTL.
   */
  Bidi(char[] text, byte[] embs, boolean ltr) {
    byte[] dirs = getDirectionCodeArray(text);
    for (int i = 0; i < embs.length; i++) {
      if ((embs[i] & 0x10) != 0) {
	embs[i] &= 0x0f;
	dirs[i] = (byte)(embs[i] & 0x01); // L, R
      }
    }

    applyBidiRules((byte[])dirs.clone(), embs, ltr); // save the input dirs

    this.ltr = ltr;
    this.dirs = dirs;
    this.levels = embs;
  }

  /**
   * Create bidi information, using the provided data on the direction 
   * codes and levels of the text in the paragraph. These dirs and levels
   * arrays should not be subsequently modified by the caller.
   *
   * @param dirs an array of bidi direction codes.  Arabic rtl text is
   * encoded using AR.  Embedding overrides have already been
   * processed into this array.
   * @param levels an array of embedding levels, 0-15 inclusive.
   * @param ltr true if the run direction for the paragraph is LTR,
   * false if it is RTL.
   */
  Bidi(byte[] dirs, byte[] levels, boolean ltr) {
    applyBidiRules((byte[])dirs.clone(), levels, ltr); // save the input dirs
    this.ltr = ltr;
    this.dirs = dirs;
    this.levels = levels;
  }

  protected Bidi(byte[] levels, boolean ltr) {
    this.ltr = ltr;
    this.dirs = null;
    this.levels = levels;
  }

  /* Public so clients can construct any direction array. */

  static final byte L  = 0;    /* left to right (strong) */
  static final byte R  = 1;    /* right to left (strong) */
  static final byte EN = 2;    /* european number (weak) */
  static final byte ES = 3;    /* european number separator (weak) */
  static final byte ET = 4;    /* european number terminator (weak) */
  static final byte AN = 5;    /* arabic number (weak) */
  static final byte CS = 6;    /* common number separator (weak) */
  static final byte B  = 7;    /* block separator */
  static final byte S  = 8;    /* segment separator */
  static final byte WS = 9;    /* whitespace */
  static final byte ON = 10;   /* other neutral */
  static final byte AR = 11;   /* arabic strong rtl character, not in 2.1 spec */
        
  static final char LRM = 0x200E; /* left to right mark */
  static final char RLM = 0x200F; /* right to left mark */

  static final char LRE = 0x202A; /* left to right embedding */
  static final char RLE = 0x202B; /* right to left embedding */
  static final char PDF = 0x202C; /* pop directional formatting */
  static final char LRO = 0x202D; /* left to right override */
  static final char RLO = 0x202E; /* right to left override */

  /* The embedding/override level stack limit */

  static final char NUMLEVELS = 16;

  /**
   * Modify the dir and levels arrays by applying the bidi algorithm to the arrays, changing
   * them in place.  On output, the direction array contains only L, R, or AR codes, and
   * the levels array contains the resolved levels.
   */
  static void applyBidiRules(byte[] dirs, byte[] levels, boolean ltr) {
    resolveWeakTypes(dirs, levels, ltr);
    resolveNeutralTypes(dirs, levels, ltr);
    resolveImplicitLevels(dirs, levels, ltr);
  }

  /*
   * This resolves serially in order from left to right, with the results
   * of previous changes taken into account for later characters.  So, for
   * example, a series of ET's after an EN will all change to EN, since
   * once the first ET changes to EN, it is then treated as EN for
   * transforming the following ET, and so on.  It will also process ETs
   * before EN by scanning forward across runs of ET and checking the
   * following character.
   *
   * !!! This does not take embedded levels into account. Should it?
   */
  private static void resolveWeakTypes(byte[] dirs, byte[] levels, boolean ltr) {
    int i = 0;
    int limit = dirs.length;
    byte prev = -1;
    byte cur = dirs[i];
    boolean lastStrongWasArabic = cur == AR;

    while (i < limit) {
      int ii = i + 1;
      byte next = (ii == limit) ? -1 : dirs[ii];
      if (next == EN && lastStrongWasArabic) {
        next = AN;
      }

      byte ncur = cur;

      switch (cur) {
      case L:
      case R:
	lastStrongWasArabic = false;
	break;

      case AR:
        lastStrongWasArabic = true;
        break;

      case ES:
        if (prev == EN && next == EN)
          ncur = EN;
        else
          ncur = ON;
        break;

      case CS:
        if (prev == EN && next == EN)
          ncur = EN;
        else if (prev == AN && next == AN)
          ncur = AN;
        else
          ncur = ON;
        break;

      case ET:
        if (prev == EN || next == EN) {
          ncur = EN;
        } else if (next == ET && !lastStrongWasArabic) {
          // forward scan to handle ET ET EN
          for (int j = ii + 1; j < dirs.length; ++j) {
            byte dir = dirs[j];
            if (dir == ET) {
              continue;
            }

            if (dir == EN) {
              while (ii < j) {
                dirs[ii++] = EN;
              }
              ncur = EN;
              next = dir;
            }
            break;
          }
        } else {
          ncur = ON;
        }
        break;

      default:
        break;
      }

      dirs[i] = ncur;
      i = ii;
      prev = ncur;
      cur = next;
    }
  }

  /**
   * Table indicating which codes represent a neutral
   */
  static final boolean neutral[] = {
    false, // L
    false, // R
    false, // EN
    false, // ES
    false, // ET
    false, // AN
    false, // CS
    true,  // B
    true,  // S
    true,  // WS
    true,  // ON
    false, // AR
  };
    
  /*
   * According to Mark, this operation should never span a level boundary.
   * The start and end of the level should be treated like sot and eot,
   * with the base direction the direction of the level.
   */
  // new version fixes #4173251
  private static void resolveNeutralTypes(byte[] dirs, byte[] levels, boolean ltr) {
    int i = 0;
    int limit = dirs.length;
    while (i < limit) {
      byte tempBaseLevel = levels[i];
      byte tempBaseDir = ((tempBaseLevel & 0x1) == 0) ? L : R;

      int eot = i + 1;
      while (eot < limit && levels[eot] == tempBaseLevel) {
        eot++;
      }

      byte last = tempBaseDir;
      byte nval = tempBaseDir;

      int  nexti = i - 1;

      while (i < eot) {
        byte dir = dirs[i];
	switch (dir) {
	case L:
	  last = L; break;

	case R:
	case AR:
	  last = R; break;

	case EN:
	case ES:
	case ET:
	case AN:
	case CS:
	  break;

	case B:
	case S:
	case WS:
	case ON:
	  if (i > nexti) {
	    nval = tempBaseDir;
	    nexti = i + 1;
	  loop:
	    while (nexti < eot) {
	      byte ndir = dirs[nexti];
	      switch (ndir) {
	      case L:
		nval = last == L ? L : tempBaseDir;
		break loop;
	      case R:
	      case AR:
		nval = last == L ? tempBaseDir : R;
		break loop;
	      }
	      ++nexti;
	    }
	  }
	  dirs[i] = nval;
	}
	++i;
      }
    }
  }

  /*
   * This does not use "global direction" but instead uses the
   * resolved level. EN processing is influenced by level boundaries.
   * This also processes segment and paragraph separator directions
   *
   * This interprets 'end of line' in L1 to mean end of segment.
   * Runs of whitespace at the end of the paragraph, and before a
   * block or segment separator, are set to the base level.
   */
  private static void resolveImplicitLevels(byte[] dirs, byte[] levels, boolean ltr) {
    byte baselevel = (byte)(ltr ? 0 : 1);
    int limit = dirs.length;

    for (int i = 0; i < limit; i++) {
      byte level = levels[i];
      byte nlevel = level;

      switch (dirs[i]) {
      case L: nlevel = (byte)((level + 1) & 0xe); break;
      case AR:
      case R: nlevel = (byte)(level | 0x1); break;
      case AN: nlevel = (byte)((level + 2) & 0xe); break;
      case EN: if ((level & 0x1) != 0) {
        nlevel += 1;
      } else if (i == 0 || levels[i-1] != level) {
        nlevel += 2;
      } else {
        byte dir = dirs[i-1];
        if (dir == EN) {
          nlevel = levels[i-1];
        } else if (dir != L) {
          nlevel += 2;
        }
      }
      break;
      case B:
      case S: nlevel = baselevel;
        // set preceeding whitespace to baselevel too
        for (int j = i - 1; j >= 0 && dirs[j] == WS; --j) {
          levels[j] = baselevel;
        }
        break;
      }

      if (nlevel < NUMLEVELS && nlevel != level) {
        levels[i] = nlevel;
      }
    }

    for (int j = limit - 1; j >= 0 && dirs[j] == WS; --j) {
      levels[j] = baselevel;
    }
  }

  Bidi(Bidi paragraphBidi, int start, int limit) {
    byte[] indirs = paragraphBidi.dirs;
    byte[] newLevels = createLineLevels(indirs, paragraphBidi.levels, paragraphBidi.ltr, start, limit);

    this.ltr = paragraphBidi.ltr;
    this.dirs = null;
    this.levels = newLevels;
  }

  /**
   * Return a level array representing the levels between lineStart and lineLimit using
   * the direction information to identify trailing whitespace that might need to switch
   * levels.
   */
  static byte[] createLineLevels(byte[] dirs, byte[] levels, boolean ltr, int lineStart, int lineLimit) {
    byte[] lineLevels = new byte[lineLimit - lineStart];
    System.arraycopy(levels, lineStart, lineLevels, 0, lineLevels.length);

    byte x = (byte)(ltr ? 0 : 1);
    for (int i = lineLimit - lineStart - 1; i >= 0; --i) {
      if (lineLevels[i] == x || dirs[lineStart + i] != WS) {
	break;
      }
      lineLevels[i] = x;
    }
    
    return lineLevels;
  }

  /**
   * Table indicating which codes represent a strong direction.
   */
  static final boolean strongDirection[] = {
    true, // L
    true, // R
    false, // EN
    false, // ES
    false, // ET
    false, // AN
    false, // CS
    false, // B
    false, // S
    false, // WS
    false, // ON
    true, // AR
  };
    
  /**
   * Return true if the default bidi rules indicate a run direction of LTR for the
   * provided range of the char array.
   */
  static boolean defaultIsLTR(char[] chars, int start, int limit) {
    while (start < limit) {
      byte dir = getDirectionCode(chars[start++]);
      if (strongDirection[dir]) {
	return dir == L;
      }
    }

    return true;
  }

  /**
   * Return true if the character suggests a need for bidi processing.  
   * Characters in the arabic extended blocks return false by default.
   * Other rtl characters and rtl explicit codes return true.
   */
  static boolean requiresBidi(char c) {
    if (c < '\u0591') return false;
    if (c > '\u202e') return false; // if contains arabic extended data, presume already ordered
    if (c > '\u2019') return true; // formatting codes require bidi
    byte dc = getDirectionCode(c);
    return dc == R || dc == AR;
  }

  /**
   * Return the bidirectional direction code of the provided character.
   * Includes AR direction code.
   */
  static byte getDirectionCode(char c) {
    return dirValues[(dirIndices[c >> 7] << 7) + (c & 0x7f)];
  }

  static final boolean strongDirectionOrB[] = {
    true, // L
    true, // R
    false, // EN
    false, // ES
    false, // ET
    false, // AN
    false, // CS
    true, // B
    false, // S
    false, // WS
    false, // ON
    true, // AR
  };
    
  /**
   * Return an array of the bidirectional direction codes for the
   * provided characters.  Includes the AR direction code.  Combining
   * characters take the direction code of the previous strong
   * directional character, if present, otherwise they take the
   * direction code ON.
   */
  static byte[] getDirectionCodeArray(char[] chars) {
    byte sc = ON;
    byte[] dirs = new byte[chars.length];
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      byte dc = getDirectionCode(c);
      if (strongDirectionOrB[dc]) {
	sc = dc;
      } else if ((dc == ON) && isCombiningMark(c)) {
	dc = sc;
      }
      dirs[i] = dc;
    }

    return dirs;
  }

  private static boolean isCombiningMark(char ch) {
    return ((((1 << Character.NON_SPACING_MARK) |  
	      (1 << Character.ENCLOSING_MARK) | 
	      (1 << Character.COMBINING_SPACING_MARK)) >> Character.getType(ch)) & 1) != 0;
  }

  /**
   * Process the text for embedding codes and return the embedding array.
   * The text represents a single paragraph using the provided run direction.  
   * The embedding array represents simple embeddings as values from 0-15 and 
   * overrides as values from 16-31.
   */
  static byte[] getEmbeddingArray(char[] chars, boolean ltr) {
    byte[] embeddings = new byte[chars.length];

    byte baseLevel = (byte)(ltr ? 0 : 1);
    byte level = baseLevel;
    int s = 0; // stack counter
    int skip = 0; // skip counter when codes don't affect the stack
    byte levelStack[] = new byte[NUMLEVELS]; // stack of levels

    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      switch (c) {
      case LRE:
      case LRO: {
	if (skip > 0) {
	  ++skip;
	} else {
	  byte newlevel = (byte)((level & 0x0e) + 2);
	  if (newlevel >= NUMLEVELS) {
	    ++skip;
	  } else {
	    levelStack[s++] = level;
	    embeddings[i] = level;

	    if (c == LRO) {
	      level = (byte)(newlevel + 0x10);
	    } else {
	      level = newlevel;
	    }

	    continue;
	  }
	}
      } break;

      case RLE:
      case RLO: {
	if (skip > 0) {
	  ++skip;
	} else {
	  byte newlevel = (byte)(((level & 0xf) + 1) | 0x01);
	  if (newlevel >= NUMLEVELS) {
	    ++skip;
	  } else {
	    levelStack[s++] = level;
	    embeddings[i] = level;

	    if (c == RLO) {
	      level = (byte)(newlevel + 0x10);
	    } else {
	      level = newlevel;
	    }

	    continue;
	  }
	}
      } break;

      case PDF:
	if (skip > 0) {
	  --skip;
	} else if (s > 0) {
	  level = levelStack[--s];
	}
	break;

      default:
	break;
      }

      embeddings[i] = level;
    }

    return embeddings;
  }

  /**
   * Given a level array, compute a a visual to logical ordering.
   */
  static int[] createVisualToLogicalMap(byte[] levels) {
    int len = levels.length;
    int[] mapping = new int[len];

    byte lowestOddLevel = (byte)(NUMLEVELS + 1);
    byte highestLevel = 0;

    // initialize mapping and levels

    for (int i = 0; i < len; i++) {
      mapping[i] = i;

      byte level = levels[i];
      if (level > highestLevel) {
        highestLevel = level;
      }

      if ((level & 0x01) != 0 && level < lowestOddLevel) {
        lowestOddLevel = level;
      }
    }

    while (highestLevel >= lowestOddLevel) {
      int i = 0;
      for (;;) {
        while (i < len && levels[i] < highestLevel) {
          i++;
        }
        int begin = i++;

        if (begin == levels.length) {
          break; // no more runs at this level
        }

        while (i < len && levels[i] >= highestLevel) {
          i++;
        }
        int end = i - 1;

        while (begin < end) {
          int temp = mapping[begin];
          mapping[begin] = mapping[end];
          mapping[end] = temp;
          ++begin;
          --end;
        }
      }

      --highestLevel;
    }

    return mapping;
  }

  /**
   * Reorder the objects in the array into visual order based on their levels.
   */
  static void reorderVisually(byte[] levels, Object[] objects) {
    int len = levels.length;

    byte lowestOddLevel = (byte)(NUMLEVELS + 1);
    byte highestLevel = 0;

    // initialize mapping and levels

    for (int i = 0; i < len; i++) {
      byte level = levels[i];
      if (level > highestLevel) {
        highestLevel = level;
      }

      if ((level & 0x01) != 0 && level < lowestOddLevel) {
        lowestOddLevel = level;
      }
    }

    while (highestLevel >= lowestOddLevel) {
      int i = 0;
      for (;;) {
        while (i < len && levels[i] < highestLevel) {
          i++;
        }
        int begin = i++;

        if (begin == levels.length) {
          break; // no more runs at this level
        }

        while (i < len && levels[i] >= highestLevel) {
          i++;
        }
        int end = i - 1;

        while (begin < end) {
          Object temp = objects[begin];
          objects[begin] = objects[end];
          objects[end] = temp;
          ++begin;
          --end;
        }
      }

      --highestLevel;
    }
  }

  /**
   * Return the inverse array, source array must map 1-1
   *
   * i.e. if values[i] = j, then inverse[j] = i.
   */
  static int[] getInverseOrder(int[] values) {
    if (values == null) {
      return null;
    }

    int[] result = new int[values.length];
    for (int i = 0; i < values.length; i++) {
      result[values[i]] = i;
    }

    return result;
  }

  /**
   * Compute a contiguous order for the range start, limit.
   */
  private static int[] computeContiguousOrder(int[] values, int start, 
                                              int limit) {

    int[] result = new int[limit-start];
    for (int i=0; i < result.length; i++) {
      result[i] = i + start;
    }

    // now we'll sort result[], with the following comparison:
    // result[i] lessthan result[j] iff values[result[i]] < values[result[j]]

    // selection sort for now;  use more elaborate sorts if desired
    for (int i=0; i < result.length-1; i++) {
      int minIndex = i;
      int currentValue = values[result[minIndex]];
      for (int j=i; j < result.length; j++) {
        if (values[result[j]] < currentValue) {
          minIndex = j;
          currentValue = values[result[minIndex]];
        }
      }
      int temp = result[i];
      result[i] = result[minIndex];
      result[minIndex] = temp;
    }

    // shift result by start:
    if (start != 0) {
      for (int i=0; i < result.length; i++) {
        result[i] -= start;
      }
    }

    // next, check for canonical order:
    int k;
    for (k=0; k < result.length; k++) {
      if (result[k] != k) {
        break;
      }
    }

    if (k == result.length) {
      return null;
    }

    // now return inverse of result:
    return getInverseOrder(result);
  }

  /**
   * Return an array containing contiguous values from 0 to length 
   * having the same ordering as the source array. If this would be
   * a canonical ltr ordering, return null.  values[] is NOT
   * required to be a permutation.
   */
  static int[] getContiguousOrder(int[] values) {
    if (values != null) {
      return computeContiguousOrder(values, 0, values.length);
    }

    return null;
  }

  /**
   * Return an array containing the values from start up to limit, 
   * normalized to fall within the range from 0 up to limit - start.  
   * If this would be a canonical ltr ordering, return null.
   * NOTE: This method assumes that values[] is a permutation 
   * generated from levels[].
   */
  static int[] getNormalizedOrder(int[] values, byte[] levels, 
                                         int start, int limit) {

    if (values != null) {
      if (start != 0 || limit != values.length) {
        // levels optimization
        boolean copyRange, canonical;
        byte primaryLevel;

        if (levels == null) {
          primaryLevel = (byte) 0x0;
          copyRange = true;
          canonical = true;
        }
        else {
          if (levels[start] == levels[limit-1]) {
            primaryLevel = levels[start];
            canonical = (primaryLevel & (byte)0x1) == 0;

            // scan for levels below primary
            int i;
            for (i=start; i < limit; i++) {
              if (levels[i] < primaryLevel) {
                break;
              }
              if (canonical) {
                canonical = levels[i] == primaryLevel;
              }
            }

            copyRange = (i == limit);
          }
          else {
            copyRange = false;

            // these don't matter;  but the compiler cares:
            primaryLevel = (byte) 0x0;
            canonical = false;
          }
        }

        if (copyRange) {
          if (canonical) {
            return null;
          }

          int[] result = new int[limit-start];
          int baseValue;

          if ((primaryLevel & (byte)0x1) != 0) {
            baseValue = values[limit-1];
          } else {
            baseValue = values[start];
          }

          if (baseValue == 0) {
            System.arraycopy(values, start, result, 0, limit-start);
          }
          else {
            for (int j=0; j < result.length; j++) {
              result[j] = values[j+start] - baseValue;
            }
          }

          return result;
        }
        else {
          return computeContiguousOrder(values, start, limit);
        }
      }
      else {
        return values;
      }
    }

    return null;
  }

  // convenience method for compatibility with old tests
  static Bidi createBidi(char[] text) {
    return new Bidi(text);
  }

  /*
  private static byte[] dirIndices;
  private static byte[] dirValues;
  private static final String dataName = "bidi.data";
  
  private static byte[] xdirIndices;
  private static byte[] xdirValues;

  static {
    try {
      InputStream is = Bidi.class.getResourceAsStream(dataName);
      BufferedInputStream bsf = new BufferedInputStream(is);
      DataInputStream dbsf = new DataInputStream(bsf);

      int indicesCount = dbsf.readInt();
      xdirIndices = new byte[indicesCount];
      dbsf.readFully(xdirIndices);

      int valuesCount = dbsf.readInt();
      xdirValues = new byte[valuesCount];
      dbsf.readFully(xdirValues);

      dirIndices = RLEUtilities.readRLE(xdirIndices);
      dirValues = RLEUtilities.readRLE(xdirValues);
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println(e);
    }
  }

  static void main(String[] args) {
    System.out.print("\n  private static byte[] dirIndices = {");
    for (int i = 0; i < xdirIndices.length; ++i) {
      if (i % 16 == 0) {
	System.out.print("\n    ");
      }
      System.out.print(xdirIndices[i] + ", ");
    }
    System.out.println("\n  };");

    System.out.print("\n  private static final byte[] dirValues = {");
    for (int i = 0; i < xdirValues.length; ++i) {
      if (i % 16 == 0) {
	System.out.print("\n    ");
      }
      System.out.print(xdirValues[i] + ", ");
    }
    System.out.println("\n  };");
  }
  */

  private static byte[] dirIndices = {
    14, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, -124, 
    14, 18, 15, 16, 17, 18, 19, 20, 21, 22, 23, 14, 24, 25, 26, 27, 
    14, 28, 29, 30, -104, 14, 11, 2, 31, 32, 33, 34, 35, 36, 37, 38, 
    14, 39, -103, 14, 8, 40, 41, 42, 43, 44, 45, 46, 47, -76, 14, -2, 
    2, -91, 2, 1, 48, -104, 14, -41, 2, 1, 49, -60, 2, 12, 50, 14, 
    51, 52, 53, 53, 54, 55, 56, 57, 58, 59, 
  };

  private static byte[] dirValues = {
    -119, 10, 1, 8, -106, 10, 3, 9, 10, 10, -125, 4, -123, 10, 5, 4, 
    6, 4, 6, 3, -118, 2, 1, 6, -122, 10, -102, 0, -122, 10, -102, 0, 
    -91, 10, 2, 9, 10, -124, 4, -118, 10, 4, 4, 4, 2, 2, -123, 10, 
    1, 2, -122, 10, -105, 0, 1, 10, -97, 0, 1, 10, -2, 0, -2, 0, 
    2, 0, 0, -124, 10, -98, 0, -72, 10, -39, 0, -121, 10, -81, 0, 1, 
    10, -118, 0, -106, 10, -58, 0, -102, 10, 2, 0, 0, -110, 10, 2, 0, 
    0, -124, 10, 1, 0, -125, 10, 1, 0, -123, 10, -121, 0, 3, 10, 0, 
    10, -108, 0, 1, 10, -84, 0, 1, 10, -121, 0, -125, 10, 8, 0, 10, 
    0, 10, 0, 10, 0, 10, -110, 0, -115, 10, -116, 0, 1, 10, -62, 0, 
    1, 10, -116, 0, 1, 10, -87, 0, -119, 10, -75, 0, 8, 10, 10, 0, 
    0, 10, 10, 0, 0, -125, 10, -100, 0, 2, 10, 10, -120, 0, 4, 10, 
    10, 0, 0, -73, 10, -90, 0, 2, 10, 10, -121, 0, 1, 10, -89, 0, 
    2, 10, 0, -121, 10, -111, 1, 1, 10, -105, 1, 1, 10, -118, 1, -117, 
    10, -101, 1, -123, 10, -123, 1, -105, 10, 1, 11, -114, 10, 1, 11, -125, 
    10, 2, 11, 10, -102, 11, -123, 10, -109, 11, -115, 10, -118, 5, 6, 4, 
    5, 5, 11, 10, 10, -56, 11, 2, 10, 10, -123, 11, 1, 10, -113, 11, 
    1, 10, -98, 11, 2, 10, 10, -118, 2, -2, 10, -119, 10, -125, 0, 1, 
    10, -75, 0, 2, 10, 10, -110, 0, 2, 10, 10, -123, 0, -125, 10, -103, 
    0, -112, 10, -125, 0, 1, 10, -120, 0, 6, 10, 10, 0, 0, 10, 10, 
    -106, 0, 1, 10, -121, 0, 2, 10, 0, -125, 10, -124, 0, 4, 10, 10, 
    0, 10, -121, 0, 6, 10, 10, 0, 0, 10, 10, -125, 0, -119, 10, 1, 
    0, -124, 10, 3, 0, 0, 10, -123, 0, 2, 10, 10, -107, 0, -121, 10, 
    3, 0, 10, 10, -122, 0, -124, 10, 4, 0, 0, 10, 10, -106, 0, 1, 
    10, -121, 0, 13, 10, 0, 0, 10, 0, 0, 10, 0, 0, 10, 10, 0, 
    10, -123, 0, -124, 10, 4, 0, 0, 10, 10, -125, 0, -117, 10, -124, 0, 
    2, 10, 0, -121, 10, -113, 0, -116, 10, -125, 0, 1, 10, -121, 0, 3, 
    10, 0, 10, -125, 0, 1, 10, -106, 0, 1, 10, -121, 0, 4, 10, 0, 
    0, 10, -123, 0, 2, 10, 10, -118, 0, 1, 10, -125, 0, 1, 10, -125, 
    0, 3, 10, 10, 0, -113, 10, 1, 0, -123, 10, -118, 0, -111, 10, -125, 
    0, 1, 10, -120, 0, 6, 10, 10, 0, 0, 10, 10, -106, 0, 1, 10, 
    -121, 0, 5, 10, 0, 0, 10, 10, -124, 0, 2, 10, 10, -120, 0, -125, 
    10, 4, 0, 0, 10, 10, -125, 0, -120, 10, 2, 0, 0, -124, 10, 3, 
    0, 0, 10, -125, 0, -124, 10, -117, 0, -111, 10, 3, 0, 0, 10, -122, 
    0, -125, 10, -125, 0, 1, 10, -124, 0, -125, 10, 7, 0, 0, 10, 0, 
    10, 0, 0, -125, 10, 2, 0, 0, -125, 10, -125, 0, -125, 10, -120, 0, 
    1, 10, -125, 0, -124, 10, -123, 0, -125, 10, -125, 0, 1, 10, -124, 0, 
    -119, 10, 1, 0, -113, 10, -116, 0, -114, 10, -125, 0, 1, 10, -120, 0, 
    1, 10, -125, 0, 1, 10, -105, 0, 1, 10, -118, 0, 1, 10, -123, 0, 
    -124, 10, -121, 0, 1, 10, -125, 0, 1, 10, -124, 0, -121, 10, 2, 0, 
    0, -119, 10, 2, 0, 0, -124, 10, -118, 0, -110, 10, 3, 0, 0, 10, 
    -120, 0, 1, 10, -125, 0, 1, 10, -105, 0, 1, 10, -118, 0, 1, 10, 
    -123, 0, -124, 10, -121, 0, 1, 10, -125, 0, 1, 10, -124, 0, -121, 10, 
    2, 0, 0, -121, 10, 4, 0, 10, 0, 0, -124, 10, -118, 0, -110, 10, 
    3, 0, 0, 10, -120, 0, 1, 10, -125, 0, 1, 10, -105, 0, 1, 10, 
    -112, 0, -124, 10, -122, 0, 2, 10, 10, -125, 0, 1, 10, -124, 0, -119, 
    10, 1, 0, -120, 10, 2, 0, 0, -124, 10, -118, 0, -111, 10, -70, 0, 
    -124, 10, -99, 0, -91, 10, 13, 0, 0, 10, 0, 10, 10, 0, 0, 10, 
    0, 10, 10, 0, -122, 10, -124, 0, 1, 10, -121, 0, 1, 10, -125, 0, 
    9, 10, 0, 10, 0, 10, 10, 0, 0, 10, -115, 0, 1, 10, -125, 0, 
    2, 10, 10, -123, 0, 3, 10, 0, 10, -122, 0, 2, 10, 10, -118, 0, 
    4, 10, 10, 0, 0, -94, 10, -56, 0, 1, 10, -95, 0, -121, 10, -101, 
    0, -124, 10, -122, 0, 3, 10, 0, 10, -107, 0, -125, 10, -121, 0, 2, 
    10, 0, -26, 10, -90, 0, -118, 10, -89, 0, -124, 10, 1, 0, -124, 10, 
    -38, 0, -123, 10, -60, 0, -123, 10, -46, 0, -122, 10, -100, 0, -124, 10, 
    -38, 0, -122, 10, -106, 0, 2, 10, 10, -122, 0, 2, 10, 10, -90, 0, 
    2, 10, 10, -122, 0, 2, 10, 10, -120, 0, 7, 10, 0, 10, 0, 10, 
    0, 10, -97, 0, 2, 10, 10, -75, 0, 1, 10, -113, 0, 1, 10, -114, 
    0, 2, 10, 10, -122, 0, 1, 10, -109, 0, 2, 10, 10, -125, 0, 1, 
    10, -119, 0, 1, 10, -121, 9, 1, 6, -124, 9, 4, 10, 10, 0, 1, 
    -104, 10, 2, 7, 7, -122, 10, -123, 4, -69, 10, 1, 2, -125, 10, -122, 
    2, 2, 4, 4, -124, 10, -118, 2, 2, 4, 4, -108, 10, -115, 4, -93, 
    10, -110, 0, -2, 10, -93, 0, -2, 10, -111, 10, 2, 4, 4, -2, 10, 
    -92, 10, -59, 0, -123, 10, 1, 9, -96, 10, -113, 0, -111, 10, -44, 0, 
    -124, 10, -122, 0, 2, 10, 10, -34, 0, -122, 10, -88, 0, -124, 10, -34, 
    0, 1, 10, -112, 0, -32, 10, -99, 0, -125, 10, -92, 0, -100, 10, -100, 
    0, -125, 10, -78, 0, -113, 10, -116, 0, -124, 10, -81, 0, 1, 10, -9, 
    0, -124, 10, -29, 0, 2, 10, 10, -97, 0, 1, 10, -90, 0, -38, 10, 
    -92, 0, -36, 10, -82, 0, -46, 10, -121, 0, -116, 10, -123, 0, -122, 10, 
    -103, 1, 1, 10, -123, 1, 9, 10, 1, 10, 1, 1, 10, 1, 1, 10, 
    -20, 1, -95, 10, -2, 1, -19, 1, -110, 10, -64, 1, 2, 10, 10, -74, 
    1, -88, 10, -116, 1, -12, 10, -125, 1, 3, 10, 1, 10, -2, 1, -119, 
    1, -109, 10, -118, 2, -121, 10, -102, 0, -122, 10, -102, 0, -118, 10, -38, 
    0, -125, 10, -122, 0, 2, 10, 10, -122, 0, 2, 10, 10, -122, 0, 2, 
    10, 10, -125, 0, -93, 10, 
  };

  static {
    dirIndices = RLEUtilities.readRLE(dirIndices);
    dirValues = RLEUtilities.readRLE(dirValues);
  }
}
