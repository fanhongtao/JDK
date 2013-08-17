/*
 * @(#)TexturePaintContext.java	1.21 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import sun.awt.image.IntegerInterleavedRaster;
import sun.awt.image.ByteInterleavedRaster;

abstract class TexturePaintContext implements PaintContext {
    ColorModel colorModel;
    int bWidth;
    int bHeight;
    int maxWidth;

    WritableRaster outRas;

    double xOrg;
    double yOrg;
    double incXAcross;
    double incYAcross;
    double incXDown;
    double incYDown;

    int colincx;
    int colincy;
    int colincxerr;
    int colincyerr;
    int rowincx;
    int rowincy;
    int rowincxerr;
    int rowincyerr;

    public static PaintContext getContext(BufferedImage bufImg,
					  AffineTransform xform,
					  RenderingHints hints,
					  Rectangle devBounds) {
        WritableRaster raster = bufImg.getRaster();
	ColorModel cm = bufImg.getColorModel();
	int maxw = devBounds.width;
	if (raster instanceof IntegerInterleavedRaster) {
	    IntegerInterleavedRaster iir = (IntegerInterleavedRaster) raster;
	    if (iir.getNumDataElements() == 1 && iir.getPixelStride() == 1) {
		return new Int(iir, cm, xform, maxw);
	    }
	} else if (raster instanceof ByteInterleavedRaster) {
	    ByteInterleavedRaster bir = (ByteInterleavedRaster) raster;
	    if (bir.getNumDataElements() == 1 && bir.getPixelStride() == 1) {
		return new Byte(bir, cm, xform, maxw);
	    }
	}
	return new Any(raster, cm, xform, maxw);
    }

    TexturePaintContext(ColorModel cm, AffineTransform xform,
			int bWidth, int bHeight, int maxw) {
	this.colorModel = cm;
	this.bWidth = bWidth;
	this.bHeight = bHeight;
	this.maxWidth = maxw;

	try {
	    xform = xform.createInverse();
        } catch (NoninvertibleTransformException e) {
	    xform.setToScale(0, 0);
	}
	this.incXAcross = mod(xform.getScaleX(), bWidth);
	this.incYAcross = mod(xform.getShearY(), bHeight);
	this.incXDown = mod(xform.getShearX(), bWidth);
	this.incYDown = mod(xform.getScaleY(), bHeight);
        this.xOrg = xform.getTranslateX();
        this.yOrg = xform.getTranslateY();
	this.colincx = (int) incXAcross;
	this.colincy = (int) incYAcross;
	this.colincxerr = fractAsInt(incXAcross);
	this.colincyerr = fractAsInt(incYAcross);
	this.rowincx = (int) incXDown;
	this.rowincy = (int) incYDown;
	this.rowincxerr = fractAsInt(incXDown);
	this.rowincyerr = fractAsInt(incYDown);

    }

    static int fractAsInt(double d) {
	return (int) ((d % 1.0) * Integer.MAX_VALUE);
    }

    static double mod(double num, double den) {
	num = num % den;
	if (num < 0) {
	    num += den;
	    if (num >= den) {
		// For very small negative numerators, the answer might
		// be such a tiny bit less than den that the difference
		// is smaller than the mantissa of a double allows and
		// the result would then be rounded to den.  If that is
		// the case then we map that number to 0 as the nearest
		// modulus representation.
		num = 0;
	    }
	}
	return num;
    }
    
    /**
     * Release the resources allocated for the operation.
     */
    public void dispose() {
        // Nothing to dispose
    }

    /**
     * Return the ColorModel of the output.
     */
    public ColorModel getColorModel() {
        return colorModel;
    }

    /**
     * Return a Raster containing the colors generated for the graphics
     * operation.
     * @param x,y,w,h The area in device space for which colors are
     * generated.
     */
    public Raster getRaster(int x, int y, int w, int h) {
	if (outRas == null ||
	    outRas.getWidth() < w ||
	    outRas.getHeight() < h)
	{
	    // If h==1, we will probably get lots of "scanline" rects
	    outRas = makeRaster((h == 1 ? Math.max(w, maxWidth) : w), h);
	}
	double X = mod(xOrg + x * incXAcross + y * incXDown, bWidth);
	double Y = mod(yOrg + x * incYAcross + y * incYDown, bHeight);

	setRaster((int) X, (int) Y,
		  (int) ((X % 1.0) * Integer.MAX_VALUE),
		  (int) ((Y % 1.0) * Integer.MAX_VALUE),
		  w, h, bWidth, bHeight,
		  colincx, colincxerr,
		  colincy, colincyerr,
		  rowincx, rowincxerr,
		  rowincy, rowincyerr);

	return outRas;
    }

    public abstract WritableRaster makeRaster(int w, int h);
    public abstract void setRaster(int x, int y, int xerr, int yerr,
				   int w, int h, int bWidth, int bHeight,
				   int colincx, int colincxerr,
				   int colincy, int colincyerr,
				   int rowincx, int rowincxerr,
				   int rowincy, int rowincyerr);

    static class Int extends TexturePaintContext {
	IntegerInterleavedRaster srcRas;
	int inData[];
	int inOff;
	int inSpan;
	int outData[];
	int outOff;
	int outSpan;

	public Int(IntegerInterleavedRaster srcRas,
		   ColorModel cm, AffineTransform xform, int maxw) {
	    super(cm, xform, srcRas.getWidth(), srcRas.getHeight(), maxw);
	    this.srcRas = srcRas;
	    this.inData = srcRas.getDataStorage();
	    this.inSpan = srcRas.getScanlineStride();
	    this.inOff = srcRas.getDataOffset(0);
	}

	public WritableRaster makeRaster(int w, int h) {
	    WritableRaster ras = srcRas.createCompatibleWritableRaster(w, h);
	    IntegerInterleavedRaster iiRas = (IntegerInterleavedRaster) ras;
	    outData = iiRas.getDataStorage();
	    outSpan = iiRas.getScanlineStride();
	    outOff = iiRas.getDataOffset(0);
	    return ras;
	}

	public void setRaster(int x, int y, int xerr, int yerr,
			      int w, int h, int bWidth, int bHeight,
			      int colincx, int colincxerr,
			      int colincy, int colincyerr,
			      int rowincx, int rowincxerr,
			      int rowincy, int rowincyerr) {
	    int[] inData = this.inData;
	    int[] outData = this.outData;
	    int out = outOff;
	    int inSpan = this.inSpan;
	    int inOff = this.inOff;
	    int outSpan = this.outSpan;
	    boolean normalx = (colincx == 1 && colincxerr == 0 &&
			       colincy == 0 && colincyerr == 0);
	    int rowx = x;
	    int rowy = y;
	    int rowxerr = xerr;
	    int rowyerr = yerr;
	    if (normalx) {
		outSpan -= w;
	    }
	    for (int j = 0; j < h; j++) {
		if (normalx) {
		    int in = inOff + rowy * inSpan + bWidth;
		    x = bWidth - rowx;
		    out += w;
		    if (bWidth >= 32) {
			int i = w;
			while (i > 0) {
			    int copyw = (i < x) ? i : x;
			    System.arraycopy(inData, in - x,
					     outData, out - i,
					     copyw);
			    i -= copyw;
			    if ((x -= copyw) == 0) {
				x = bWidth;
			    }
			}
		    } else {
			for (int i = w; i > 0; i--) {
			    outData[out - i] = inData[in - x];
			    if (--x == 0) {
				x = bWidth;
			    }
			}
		    }
		} else {
		    x = rowx;
		    y = rowy;
		    xerr = rowxerr;
		    yerr = rowyerr;
		    for (int i = 0; i < w; i++) {
			outData[out + i] = inData[inOff + y * inSpan + x];
			if ((xerr += colincxerr) < 0) {
			    xerr &= Integer.MAX_VALUE;
			    x++;
			}
			if ((x += colincx) >= bWidth) {
			    x -= bWidth;
			}
			if ((yerr += colincyerr) < 0) {
			    yerr &= Integer.MAX_VALUE;
			    y++;
			}
			if ((y += colincy) >= bHeight) {
			    y -= bHeight;
			}
		    }
		}
		if ((rowxerr += rowincxerr) < 0) {
		    rowxerr &= Integer.MAX_VALUE;
		    rowx++;
		}
		if ((rowx += rowincx) >= bWidth) {
		    rowx -= bWidth;
		}
		if ((rowyerr += rowincyerr) < 0) {
		    rowyerr &= Integer.MAX_VALUE;
		    rowy++;
		}
		if ((rowy += rowincy) >= bHeight) {
		    rowy -= bHeight;
		}
		out += outSpan;
	    }
	}
    }

    static class Byte extends TexturePaintContext {
	ByteInterleavedRaster srcRas;
	byte inData[];
	int inOff;
	int inSpan;
	byte outData[];
	int outOff;
	int outSpan;

	public Byte(ByteInterleavedRaster srcRas,
		    ColorModel cm, AffineTransform xform, int maxw) {
	    super(cm, xform, srcRas.getWidth(), srcRas.getHeight(), maxw);
	    this.srcRas = srcRas;
	    this.inData = srcRas.getDataStorage();
	    this.inSpan = srcRas.getScanlineStride();
	    this.inOff = srcRas.getDataOffset(0);
	}

	public WritableRaster makeRaster(int w, int h) {
	    WritableRaster ras = srcRas.createCompatibleWritableRaster(w, h);
	    ByteInterleavedRaster biRas = (ByteInterleavedRaster) ras;
	    outData = biRas.getDataStorage();
	    outSpan = biRas.getScanlineStride();
	    outOff = biRas.getDataOffset(0);
	    return ras;
	}

	public void setRaster(int x, int y, int xerr, int yerr,
			      int w, int h, int bWidth, int bHeight,
			      int colincx, int colincxerr,
			      int colincy, int colincyerr,
			      int rowincx, int rowincxerr,
			      int rowincy, int rowincyerr) {
	    byte[] inData = this.inData;
	    byte[] outData = this.outData;
	    int out = outOff;
	    int inSpan = this.inSpan;
	    int inOff = this.inOff;
	    int outSpan = this.outSpan;
	    boolean normalx = (colincx == 1 && colincxerr == 0 &&
			       colincy == 0 && colincyerr == 0);
	    int rowx = x;
	    int rowy = y;
	    int rowxerr = xerr;
	    int rowyerr = yerr;
	    if (normalx) {
		outSpan -= w;
	    }
	    for (int j = 0; j < h; j++) {
		if (normalx) {
		    int in = inOff + rowy * inSpan + bWidth;
		    x = bWidth - rowx;
		    out += w;
		    if (bWidth >= 32) {
			int i = w;
			while (i > 0) {
			    int copyw = (i < x) ? i : x;
			    System.arraycopy(inData, in - x,
					     outData, out - i,
					     copyw);
			    i -= copyw;
			    if ((x -= copyw) == 0) {
				x = bWidth;
			    }
			}
		    } else {
			for (int i = w; i > 0; i--) {
			    outData[out - i] = inData[in - x];
			    if (--x == 0) {
				x = bWidth;
			    }
			}
		    }
		} else {
		    x = rowx;
		    y = rowy;
		    xerr = rowxerr;
		    yerr = rowyerr;
		    for (int i = 0; i < w; i++) {
			outData[out + i] = inData[inOff + y * inSpan + x];
			if ((xerr += colincxerr) < 0) {
			    xerr &= Integer.MAX_VALUE;
			    x++;
			}
			if ((x += colincx) >= bWidth) {
			    x -= bWidth;
			}
			if ((yerr += colincyerr) < 0) {
			    yerr &= Integer.MAX_VALUE;
			    y++;
			}
			if ((y += colincy) >= bHeight) {
			    y -= bHeight;
			}
		    }
		}
		if ((rowxerr += rowincxerr) < 0) {
		    rowxerr &= Integer.MAX_VALUE;
		    rowx++;
		}
		if ((rowx += rowincx) >= bWidth) {
		    rowx -= bWidth;
		}
		if ((rowyerr += rowincyerr) < 0) {
		    rowyerr &= Integer.MAX_VALUE;
		    rowy++;
		}
		if ((rowy += rowincy) >= bHeight) {
		    rowy -= bHeight;
		}
		out += outSpan;
	    }
	}
    }

    static class Any extends TexturePaintContext {
	WritableRaster srcRas;

	public Any(WritableRaster srcRas,
		   ColorModel cm, AffineTransform xform, int maxw) {
	    super(cm, xform, srcRas.getWidth(), srcRas.getHeight(), maxw);
	    this.srcRas = srcRas;
	}

	public WritableRaster makeRaster(int w, int h) {
	    return srcRas.createCompatibleWritableRaster(w, h);
	}

	public void setRaster(int x, int y, int xerr, int yerr,
			      int w, int h, int bWidth, int bHeight,
			      int colincx, int colincxerr,
			      int colincy, int colincyerr,
			      int rowincx, int rowincxerr,
			      int rowincy, int rowincyerr) {
	    Object data = null;
	    int rowx = x;
	    int rowy = y;
	    int rowxerr = xerr;
	    int rowyerr = yerr;
	    WritableRaster srcRas = this.srcRas;
	    WritableRaster outRas = this.outRas;
	    for (int j = 0; j < h; j++) {
		x = rowx;
		y = rowy;
		xerr = rowxerr;
		yerr = rowyerr;
		for (int i = 0; i < w; i++) {
		    data = srcRas.getDataElements(x, y, data);
		    outRas.setDataElements(i, j, data);
		    if ((xerr += colincxerr) < 0) {
			xerr &= Integer.MAX_VALUE;
			x++;
		    }
		    if ((x += colincx) >= bWidth) {
			x -= bWidth;
		    }
		    if ((yerr += colincyerr) < 0) {
			yerr &= Integer.MAX_VALUE;
			y++;
		    }
		    if ((y += colincy) >= bHeight) {
			y -= bHeight;
		    }
		}
		if ((rowxerr += rowincxerr) < 0) {
		    rowxerr &= Integer.MAX_VALUE;
		    rowx++;
		}
		if ((rowx += rowincx) >= bWidth) {
		    rowx -= bWidth;
		}
		if ((rowyerr += rowincyerr) < 0) {
		    rowyerr &= Integer.MAX_VALUE;
		    rowy++;
		}
		if ((rowy += rowincy) >= bHeight) {
		    rowy -= bHeight;
		}
	    }
	}
    }
}
