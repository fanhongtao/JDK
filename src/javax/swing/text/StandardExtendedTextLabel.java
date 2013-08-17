/*
 * @(#)StandardExtendedTextLabel.java	1.9 99/04/22
 *
 * Copyright 1998, 1999 by Sun Microsystems, Inc.,
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
 * @(#)StandardExtendedTextLabel.java	1.9 99/04/22
 *
 * (C) Copyright IBM Corp. 1998, All Rights Reserved
 */

package javax.swing.text;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import sun.awt.font.NativeFontWrapper;
import sun.awt.font.StandardGlyphVector;

/**
 * An implementation of ExtendedTextLabel without using TextSource.
 */

// !!! this implementation does not perform indic reordering

class StandardExtendedTextLabel extends ExtendedTextLabel {
    GlyphVector gv;
    float[] charinfo;
    LineMetrics metrics;
    boolean dataIsLTR;
    boolean lineIsLTR;
    float italicAngle;

    int numchars;
    Rectangle2D lb;
    Rectangle2D ab;
    Rectangle2D vb;

    protected StandardExtendedTextLabel(
                                        GlyphVector gv,
                                        float[] charinfo, 
                                        LineMetrics metrics, 
                                        boolean dataIsLTR, 
                                        boolean lineIsLTR,
                                        float italicAngle) {

        this.gv = gv;
        this.charinfo = charinfo;
        this.metrics = metrics;
        this.dataIsLTR = dataIsLTR;
        this.lineIsLTR = lineIsLTR;
        this.italicAngle = italicAngle;

        this.numchars = charinfo.length / numvals;
    }


  // TextLabel API

  Rectangle2D getLogicalBounds(float x, float y) {
    if (lb == null) {
      lb = createLogicalBounds();
    }
    return new Rectangle2D.Float((float)(lb.getX() + x), 
				 (float)(lb.getY() + y), 
				 (float)lb.getWidth(), 
				 (float)lb.getHeight());
  }

  Rectangle2D getVisualBounds(float x, float y) {
    if (vb == null) {
      vb = createVisualBounds();
    }
    return new Rectangle2D.Float((float)(vb.getX() + x), 
				 (float)(vb.getY() + y), 
				 (float)vb.getWidth(), 
				 (float)vb.getHeight());
  }

  Rectangle2D getAlignBounds(float x, float y) {
    if (ab == null) {
      ab = createAlignBounds();
    }
    return new Rectangle2D.Float((float)(ab.getX() + x), 
				 (float)(ab.getY() + y), 
				 (float)ab.getWidth(),
				 (float)ab.getHeight());

  }

  Shape getOutline(float x, float y) {
    return gv.getOutline(x, y);
  }

  void draw(Graphics2D g, float x, float y) {
    g.drawGlyphVector(gv, x, y);
  }

  protected Rectangle2D createLogicalBounds() {
    // return gv.getLogicalBounds();
    // !!! current gv implementation is slow and incorrect
    // this one is just slow

    float ll = 0f;
    float lt = -metrics.getAscent();
    float lw = 0f;
    float lh = metrics.getAscent() + metrics.getDescent() + metrics.getLeading();
   
    int rn = charinfo.length - numvals;
    while (rn > 0) {
      if (charinfo[rn+advx] != 0) break;
      rn -= numvals;
    }
      
    if (rn >= 0) {
      int ln = 0;
      while (ln < rn) {
	if (charinfo[ln+advx] != 0) break;
	ln += numvals;
      }

      ll = Math.min(0f, charinfo[ln+posx]);
      lw = charinfo[rn+posx] + charinfo[rn+advx] - ll;
    }

    return new Rectangle2D.Float(ll, lt, lw, lh);
  }

  protected Rectangle2D createVisualBounds() {
    return gv.getVisualBounds();
  }

  // like createLogicalBounds except ignore leading and logically trailing white space
  // this assumes logically trailing whitespace is also visually trailing

  protected Rectangle2D createAlignBounds() {
    float al = 0f;
    float at = -metrics.getAscent();
    float aw = 0f;
    float ah = metrics.getAscent() + metrics.getDescent();

    int rn = charinfo.length - numvals;
    while (rn > 0 && ((charinfo[rn+advx] == 0) || (lineIsLTR && charinfo[rn+visw] == 0))) {
      rn -= numvals;
    }
      
    if (rn >= 0) {
      int ln = 0;
      while (ln < rn && ((charinfo[ln+advx] == 0) || (!lineIsLTR && charinfo[ln+visw] == 0))) {
	ln += numvals;
      }

      al = Math.max(0f, charinfo[ln+posx]);
      aw = charinfo[rn+posx] + charinfo[rn+advx] - al;
    }

    return new Rectangle2D.Float(al, at, aw, ah);
  }


    // ExtendedTextLabel API

  
  private static final int posx = 0,
    posy = 1,
    advx = 2, 
    advy = 3,
    visx = 4,
    visy = 5,
    visw = 6,
    vish = 7;
  private static final int numvals = 8;
      
    int getNumCharacters() {
        return numchars;
    }

    float getItalicAngle() {
        return italicAngle;
    }

    LineMetrics getLineMetrics() {
        return metrics;
    }

    float getCharX(int index) {
        validate(index);
        return charinfo[l2v(index) * numvals + posx];
    }
    
    float getCharY(int index) {
        validate(index);
        return charinfo[l2v(index) * numvals + posy];
    }

    float getCharAdvance(int index) {
        validate(index);
        return charinfo[l2v(index) * numvals + advx];
    }

    Rectangle2D getCharVisualBounds(int index, float x, float y) {
        validate(index);
        index = l2v(index) * numvals;
        return new Rectangle2D.Float(
                                     charinfo[index + visx] + x,
                                     charinfo[index + visy] + y,
                                     charinfo[index + visw],
                                     charinfo[index + vish]);
    }
    
  int logicalToVisual(int logicalIndex) {
    validate(logicalIndex);
    return l2v(logicalIndex);
  }

  int visualToLogical(int visualIndex) {
    validate(visualIndex);
    return v2l(visualIndex);
  }

  int getLineBreakIndex(int start, float width) {
    validate(start);
    --start;
    while (width >= 0 && ++start < numchars) {
      width -= charinfo[l2v(start) * numvals + advx];
    }

    return start; 
  }

  /*
    int getCharAdvanceAtWidth(int start, float width) {
        int index = start * numvals + advx;
        if( ltr ) {
            int numchars = getNumCharacters();
            --start;
            while( width >= 0 && ++start < numchars) {
                width -= charinfo[index];
                index += numvals;
            }
        } else {
            ++start;
            while( width >= 0 && --start >= 0 ) {
                width -= charinfo[index];
                index -= numvals;
            }
        }

        return start; 
    }
    */

  
    int getCharIndexAtWidth(float width) {
        int start = 0;
        if( dataIsLTR ) {
            int index = start * numvals + advx;
            int numchars = getNumCharacters();
            --start;
            while( width >= 0 && ++start < numchars) {
                width -= charinfo[index];
                index += numvals;
            }
        } else {
            start = getNumCharacters()-1;
            int index = start * numvals + advx;
            ++start;
            while( width >= 0 && --start >= 0 ) {
                width -= charinfo[index];
                index -= numvals;
            }
        }

        //if( start >= getNumCharacters() )
        //    System.out.println("getCharIndexAtWidth: " + start+", width "+width);
        return start; 
    }
    

    float getCharAdvanceBetween(int start, int limit) {
        float a = 0f;
	--start;
	while (++start < limit) {
	  a += charinfo[l2v(start) * numvals + advx];
	}

        return a;
    }

  private void validate(int index) {
    if (index < 0) {
      throw new IllegalArgumentException("index " + index + " < 0");
    } else if (index >= numchars) {
      throw new IllegalArgumentException("index " + index + " >= " + numchars);
    }
  }

  protected int l2v(int index) {
    return dataIsLTR ? index : numchars - 1 - index;
  }

  protected int v2l(int index) {
    return dataIsLTR ? index : numchars - 1 - index;
  }

    public String toString() {
        String result = null;
        for(int i=0; i<charinfo.length; i+=numvals) {
            result += "\tadvx = " + charinfo[i+advx];
            result += " advy = "  + charinfo[i+advy];
            result += " posx = "  + charinfo[i+posx];
            result += " posy = "  + charinfo[i+posy];
            result += "\n";
        }
        return result;
    }

  static ExtendedTextLabel create(
				  char[] context, 
				  int contextStart,
				  int contextLength,
				  int start,
				  int length,
				  boolean ltr,
				  Font font,
				  FontRenderContext frc) {

    char[] glyphs = context;
    int gStart = start;

    // Only do arabic processing on rtl data
    if (ltr) {
      for (int i = start, e = start + length; i < e; ++i) {
	if (isFormatMark(context[i])) {
	  glyphs = new char[length];
	  gStart = 0;
	  System.arraycopy(context, start, glyphs, 0, length);

	  glyphs[i-start] = '\uffff';
	  for (int j = i - start + 1; j < length; ++j) {
	    if (isFormatMark(glyphs[j])) {
	      glyphs[j] = '\uffff';
	    }
	  }
          break;
	}
      }
    } else {
      // get shape type of char on visual right
      // !!! assume no indic for now
      int rightType = NewArabicShaping.VALUE_NOSHAPE_NONE;
      for (int i = start - 1; i >= contextStart; --i) {
	rightType = NewArabicShaping.getShapeType(context[i]);
	if (rightType != NewArabicShaping.VALUE_TRANSPARENT) {
	  break;
	}
      }

      // get shape type of char on visual left
      int leftType = NewArabicShaping.VALUE_NOSHAPE_NONE;
      for (int i = start + length, e = contextStart + contextLength; i < e; ++i) {
	leftType = NewArabicShaping.getShapeType(context[i]);
	if (leftType != NewArabicShaping.VALUE_TRANSPARENT) {
	  break;
	}
      }

      gStart = 0;
      glyphs = new char[length];
      for (int i = length, j = start - 1; i > 0;) {
	glyphs[--i] = context[++j];
      }

      NewArabicShaping.shape(glyphs, leftType, rightType);

      ArabicLigaturizer.getLamAlefInstance().ligaturize(glyphs, 0, glyphs.length);

      // hack mirroring, replace format marks
      for (int i = 0; i < length; ++i) {
	char c = glyphs[i];
	if (isFormatMark(c)) {
	  glyphs[i] = '\uffff';
	} else {
	  glyphs[i] = getMirroredChar(c);
	}
      }
    }

    StandardGlyphVector gv = new StandardGlyphVector(font, glyphs, gStart, length, frc);

    // create the charinfo, assume 1-1 chars to glyphs

    float[] charinfo = gv.getGlyphInfo();

    // create the metrics, ignore dropped chars, !!! linemetrics length is bogus

    LineMetrics metrics = font.getLineMetrics(glyphs, gStart, length, frc);

    // default lineIsLTR to dataIsLTR

    boolean lineIsLTR = ltr;
    boolean dataIsLTR = ltr;

    float italicAngle = font.getItalicAngle();

    return new StandardExtendedTextLabel(gv, charinfo, metrics, dataIsLTR, lineIsLTR, italicAngle);
  }

  private static boolean isFormatMark(char c) {
    // this includes not only the format marks, but also tab, cr, lf, ps, and ls.
    // the compiler may not help us out here, this might be worth optimizing, if
    // everything else gets cleaned up.

    switch (c) {
    case '\u0009':
    case '\012': // aka u+000a, but compiler complains about premature eol
    case '\015': // aka u+000d, same problem
    case '\u200c':
    case '\u200d':
    case '\u200e':
    case '\u200f':
    case '\u2028':
    case '\u2029':
    case '\u202a':
    case '\u202b':
    case '\u202c':
    case '\u202d':
    case '\u202e':
    case '\u206a':
    case '\u206b':
    case '\u206c':
    case '\u206d':
    case '\u206e':
    case '\u206f':
    case '\ufeff':
      return true;

    default:
      return false;
    }
  }

  // Not the same as mirroring, which is a glyph feature.  The best we can do is swap
  // with a paired character when there is one.  This won't position the character
  // properly, but it is a reasonable approximation.
  private static final char[] mirrorPairs = {
    '\u0028', '\u0029', // ascii paired punctuation
    '\u003c', '\u003e',
    '\u005b', '\u005d',
    '\u007b', '\u007d',
    '\u2045', '\u2046', // math symbols (not complete)
    '\u207d', '\u207e',
    '\u208d', '\u208e',
    '\u2264', '\u2265',
    '\u3008', '\u3009', // chinese paired punctuation
    '\u300a', '\u300b',
    '\u300c', '\u300d',
    '\u300e', '\u300f',
    '\u3010', '\u3011',
    '\u3014', '\u3015',
    '\u3016', '\u3017',
    '\u3018', '\u3019',
    '\u301a', '\u301b',
  };

  private static char getMirroredChar(char c) {
    if (c <= '\u007d' || (c >= '\u2045' && c <= '\u301b')) {
      for (int i = 0; i < mirrorPairs.length; i++) {
	char mc = mirrorPairs[i];
	if (mc == c) {
	  return mirrorPairs[i + (((i & 0x1) == 0) ? 1 : -1)];
	} else if (mc > c) {
	  break;
	}
      }
    }

    return c;
  }

  /*
    static ExtendedTextLabel create(
                                    char[] context, 
                                    int contextStart,
                                    int contextLength,
                                    int start,
                                    int length,
                                    boolean ltr,
                                    Font font,
                                    FontRenderContext frc) {

        //System.out.println("ExtendedTextLabel: context["+new String(context,contextStart,contextLength)+"]");
        //System.out.println("\tcontextStart = " + contextStart +
        //                 " contextLength = " + contextLength);
        //System.out.println("\tstart = " + start + " length = " + length +
        //                   " ltr = " + ltr);
    
        int leftType = ArabicShaping.NEITHER;
        for (int i = start - 1; i >= contextStart; --i) {
            leftType = ArabicShaping.getShapeType(context[i]);
            if (leftType != ArabicShaping.TRANSPARENT) {
                break;
            }
        }

        int rightType = ArabicShaping.NEITHER;
        for (int i = start + length, e = contextStart + contextLength; i < e; ++i) {
            rightType = ArabicShaping.getShapeType(context[i]);
            if (rightType != ArabicShaping.TRANSPARENT) {
                break;
            }
        }

        if (!ltr) {
            int tempType = leftType;
            leftType = rightType;
            rightType = tempType;
        }

        char[] chars = new char[length];
        System.arraycopy(context, start, chars, 0, length);

        LineMetrics lm = font.getLineMetrics(context, start, length, frc);

        frc = new FontRenderContext(null,false,false);
        char[] glyphs = new char[length];
        if (ltr) {
            System.arraycopy(chars, 0, glyphs, 0, length);
        } else {
            for (int i = length, j = start - 1; i > 0;) {
                glyphs[--i] = ArabicShaping.getMirroredChar(context[++j]); // hack mirrors
            }
        }

        ArabicShaping.shapeArabic(glyphs, leftType, rightType, '\uffff');
    
        boolean[] ligmap = new boolean[length];

        int glength = -1;
        for (int i = 0; i < length; i++) {
            char c = glyphs[i];
            if (c == '\uffff' || c < '\u0020') { // hide all C0 control chars by making them behave like ligatures
                ligmap[i] = true;
            }
            else {
                glyphs[++glength] = c;
            }
        }
        ++glength;

        char[] nglyphs = new char[glength];
        System.arraycopy(glyphs, 0, nglyphs, 0, glength);
        glyphs = nglyphs;

        //    for (int i = 0; i < glyphs.length; i++) {
        //      System.out.print(Integer.toHexString(glyphs[i]) + " ");
        //    }
        //    System.out.println();

        GlyphVector gv = font.createGlyphVector(frc, glyphs);
        gv.performDefaultLayout();
        //System.out.println(frc.getTransform());
        //System.out.println(font.getTransform());

        float[] info = new float[length * numvals];

        float[] xy = gv.getGlyphPositions(0, glength, null);

        if (ltr) {
            float x = 0f;
            float y = 0f;
            float a = 0f;
            for (int i = 0, n = 0, gi = 0, gn = 0; i < length; i++, n += numvals) {
                if (ligmap[i]) {
                    info[n+posx] = x + a;
                } else {
                    GlyphMetrics gm = gv.getGlyphMetrics(gi++);
                    x = xy[gn++];
                    y = xy[gn++];
                    a = gm.getAdvance();

                    info[n+advx] = a;
                    info[n+advy] = 0;

                    info[n+posx] = x;
                    info[n+posy] = y;

                    Rectangle2D r = gm.getBounds2D();
                    //      System.out.println("gm " + i + " bounds: " + r);

                    info[n+visx] = (float)(x + r.getX());
                    info[n+visy] = (float)(y + r.getY());
                    info[n+visw] = (float)r.getWidth();
                    info[n+vish] = (float)r.getHeight();
                }
            }
        } else {
            float x = glength > 0 ? xy[xy.length - 2] + gv.getGlyphMetrics(glength - 1).getAdvance() : 0f;
            float y = 0f;
            float a = 0f;

            for (int i = length, n = 0, gi = glength, gn = xy.length; --i >= 0; n += numvals) {
                if (ligmap[i]) {
                    info[n+posx] = x;
                } else {
                    GlyphMetrics gm = gv.getGlyphMetrics(--gi);
                    y = xy[--gn];
                    x = xy[--gn];
                    a = gm.getAdvance();

                    info[n+advx] = a;
                    info[n+advy] = 0;

                    info[n+posx] = x;
                    info[n+posy] = y;

                    Rectangle2D r = gm.getBounds2D();
                    //      System.out.println("gm " + i + " bounds: " + r);

                    info[n+visx] = (float)(x + r.getX());
                    info[n+visy] = (float)(y + r.getY());
                    info[n+visw] = (float)r.getWidth();
                    info[n+vish] = (float)r.getHeight();
                }
            }
        }

        float al = 0f;
        float at = -metrics.getAscent();
        float aw = 0f;
        float ah = metrics.getAscent() + metrics.getDescent();

        float ll = al;
        float lt = at;
        float lw = aw;
        float lh = ah + metrics.getLeading();

        // System.out.println(metrics.getAscent() + ", " + metrics.getDescent() + ", " + metrics.getLeading());

        boolean foundlb = false;

        int n = length * numvals;
        while (n > 0) {
            n -= numvals;
            // System.out.println("info " + (n / numvals) + " adv: " + info[n+advx] + " visw: " + info[n+visw]);
            if (info[n+advx] != 0) {
                if (!foundlb) {
                    foundlb = true;
                    if (ltr) {
                        ll = Math.min(0f, info[posx]);
                        lw = info[n+posx] + info[n+advx] - ll;
                    } else {
                        ll = Math.min(0f, info[n+posx]);
                        lw = info[posx] + info[advx] - ll;
                    }
                }

                if (info[n+visw] != 0) { // replaces Character.isSpaceChar
                    if (ltr) {
                        al = Math.max(0f, info[posx]);
                        aw = info[n+posx]+info[n+advx] - al;
                    } else {
                        al = Math.max(0f, info[n+posx]);
                        aw = info[posx]+info[advx] - al;
                    }
                    break;
                }
            }
        }

        Rectangle2D ab = new Rectangle2D.Float(al, at, aw, ah);
        Rectangle2D lb = new Rectangle2D.Float(ll, lt, lw, lh);

        TextLabel label = new StandardTextLabel(gv, lb, ab);
        ExtendedTextLabel extLabel = new StandardExtendedTextLabel(label, lm, info, chars, ltr, font, frc);

        return extLabel;
    }
    */
}
