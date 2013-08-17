/*
 * @(#)Ligaturizer.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
/*
 * @(#)Ligaturizer.java	1.2 98/07/31
 *
 * (C) Copyright IBM Corp. 1998 - All Rights Reserved
 */

package javax.swing.text;

/*
 * An object that ligaturizes a character array.
 */

abstract class Ligaturizer {
  /**
   * Convert text between start and limit to incorporate ligatures.
   * Text is in visual order.  Shaping, if required, has already been
   * performed on the text. The leftmost character of a matching
   * string is replaced by the ligature character, other characters
   * of the string are replaced by U+ffff.  Transparent characters
   * not defined as part of the ligature remain unchanged, and are
   * ignored by the matching process.
   */
  abstract void ligaturize(char[] text, int start, int length);

  /*
   * Return a ligaturizer that only generates those ligatures from the
   * original ligaturizer that the filter accepts.  Note that it is
   * not possible to 'unrestrict' a ligaturizer.
   */
  abstract Ligaturizer restrict(Ligaturizer.Filter f);

  /*
   * A functor used to restrict a Ligaturizer to only those ligatures
   * for which 'accepts' returns true.
   */
  static abstract class Filter {
    abstract boolean accepts(char c);
  }
}
