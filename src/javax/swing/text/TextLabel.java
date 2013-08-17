/*
 * @(#)TextLabel.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
/*
 * @(#)TextLabel.java	1.5 98/09/21
 *
 * (C) Copyright IBM Corp. 1998, All Rights Reserved
 */

package javax.swing.text;

import java.awt.Graphics2D;
import java.awt.Shape;

import java.awt.geom.Rectangle2D;

/**
 * A label.  
 * Visual bounds is a rect that encompasses the entire rendered area.
 * Logical bounds is a rect that defines how to position this next
 * to other objects.
 * Align bounds is a rect that defines how to align this to margins.
 * it generally allows some overhang that logical bounds would prevent.
 */
abstract class TextLabel {

  /** 
   * Return a rectangle that surrounds all the pixels when this label is rendered at x, y.
   */
  abstract Rectangle2D getVisualBounds(float x, float y);

  /**
   * Return a rectangle that corresponds to the logical bounds of the text 
   * when this label is rendered at x, y. 
   * This rectangle is used when positioning text next to other text.
   */
  abstract Rectangle2D getLogicalBounds(float x, float y);

  /**
   * Return a rectangle that corresponds to the alignment bounds of the text
   * when this label is rendered at x, y. This rectangle is used when positioning text next
   * to a margin.  It differs from the logical bounds in that it does not include leading or
   * trailing whitespace.
   */
  abstract Rectangle2D getAlignBounds(float x, float y);

  /**
   * Return an outline of the characters in the label when rendered at x, y.
   */
  abstract Shape getOutline(float x, float y);

  /**
   * Render the label at x, y in the graphics.
   */
  abstract void draw(Graphics2D g, float x, float y);

  /**
   * A convenience method that returns the visual bounds when rendered at 0, 0.
   */
  Rectangle2D getVisualBounds() {
    return getVisualBounds(0f, 0f);
  }

  /**
   * A convenience method that returns the logical bounds when rendered at 0, 0.
   */
  Rectangle2D getLogicalBounds() {
    return getLogicalBounds(0f, 0f);
  }

  /**
   * A convenience method that returns the align bounds when rendered at 0, 0.
   */
  Rectangle2D getAlignBounds() {
    return getAlignBounds(0f, 0f);
  }

  /**
   * A convenience method that returns the outline when rendered at 0, 0.
   */
  Shape getOutline() {
    return getOutline(0f, 0f);
  }

  /**
   * A convenience method that renders the label at 0, 0.
   */
  void draw(Graphics2D g) {
    draw(g, 0f, 0f);
  }
}
