/*
 * @(#)ExtendedTextLabel.java	1.6 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * @(#)ExtendedTextLabel.java	1.6 01/11/29
 *
 * (C) Copyright IBM Corp. 1998 - All Rights Reserved
 */

package javax.swing.text;

import java.awt.Font;

import java.awt.font.LineMetrics;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * An extension of TextLabel that maintains information
 * about characters.
 */

abstract class ExtendedTextLabel extends TextLabel {
  /**
   * Return the number of characters represented by this label.
   */
  abstract int getNumCharacters();

  /**
   * Return the italic angle of the text in this label.
   */
  abstract float getItalicAngle();

  /**
   * Return the line metrics for all text in this label.
   */
  abstract LineMetrics getLineMetrics();

  /**
   * Return the x location of the character at the given logical index.
   */
  abstract float getCharX(int logicalIndex);

  /**
   * Return the y location of the character at the given logical index.
   */
  abstract float getCharY(int logicalIndex);

  /**
   * Return the advance of the character at the given logical index.
   */
  abstract float getCharAdvance(int logicalIndex);

  /**
   * Return the visual bounds of the character at the given logical index.
   * This bounds encloses all the pixels of the character when the label is rendered
   * at x, y.
   */
  abstract Rectangle2D getCharVisualBounds(int logicalIndex, float x, float y);

  
  /**
   * Return the visual index of the character at the given logical index.
   */
  abstract int logicalToVisual(int logicalIndex);

  /**
   * Return the logical index of the character at the given visual index.
   */
  abstract int visualToLogical(int visualIndex);

  /**
   * Return the logical index of the character, starting with the character at 
   * logicalStart, whose accumulated advance exceeds width.  If the advances of
   * all characters do not exceed width, return getNumCharacters.  If width is
   * less than zero, return logicalStart - 1.
   */
  abstract int getLineBreakIndex(int logicalStart, float width);

  /**
   * Return the accumulated advances of all characters between logicalStart and
   * logicalLimit.
   */
  abstract float getCharAdvanceBetween(int logicalStart, int logicalLimit);

  /**
   * A convenience overload of getCharVisualBounds that defaults the label origin
   * to 0, 0.
   */
  Rectangle2D getCharVisualBounds(int logicalIndex) {
    return getCharVisualBounds(logicalIndex, 0, 0);
  }

  abstract int getCharIndexAtWidth(float width);
}
