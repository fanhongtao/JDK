/*
 * @(#)SwingGraphics.java	1.25 98/08/31
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing;

/**
 * A private graphics to access clip bounds without creating a new
 * rectangle
 *
 * @version 1.25 08/31/98
 * @author Arnaud Weber
 */

import java.awt.*;
import java.awt.image.*;
import java.util.Stack;


import java.text.AttributedCharacterIterator;
 

class SwingGraphics extends Graphics implements GraphicsWrapper {
    Graphics graphics;
    Graphics originalGraphics;
    Rectangle clipRect;
    Color currentColor;
    Font currentFont;

    int translateX = 0;    // translation delta since initialization
    int translateY = 0;

    /* The SwingGraphics this object was cloned from, if any. */
    SwingGraphics previous;

    /* Can this context be restored to its original state? */
    private boolean restorable = true;

    /* diagnostic aids -- should be false for production builds. */
    private static final boolean trace = false;   // trace creates and disposes
    private static final boolean verbose = false; // show reuse hits/misses
    private static final boolean debug = false;   // show bad params, misc.

    public Graphics create() {
        return createSwingGraphics(this);
    }

    public Graphics create(int x,int y,int w,int h) {
        if (debug && (w <= 0 || h <= 0)) {
            System.out.println("bad size:  " + w + "x" + h);
            Thread.dumpStack();
        }
        return createSwingGraphics(this, x, y, w, h);
    }

    public Graphics subGraphics() {
        Graphics g = graphics;

        /* We've handed our monitored object out, and can no longer track
         * its state. */
	restorable = false;

        return g;
    }

    SwingGraphics(Graphics g) {
        if(g == null) {
            Thread.currentThread().dumpStack();
        }
        init(g);
    }

    void init(Graphics g) {
        if (g instanceof SwingGraphics) {
            /* Clone an existing SwingGraphics object.  The dispose method
             * must be called to reset the shared sub-graphics object to
             * the same state it was in when cloned. */
            SwingGraphics sg = (SwingGraphics)g;
            originalGraphics = sg.originalGraphics;
            graphics = sg.graphics;
            previous = sg;
            if (clipRect == null) {
                clipRect = new Rectangle(sg.clipRect.x, 
                                         sg.clipRect.y, 
                                         sg.clipRect.width, 
                                         sg.clipRect.height);
            } else {
                clipRect.x = sg.clipRect.x;
                clipRect.y = sg.clipRect.y;
                clipRect.width = sg.clipRect.width;
                clipRect.height = sg.clipRect.height;
            }
            currentColor = sg.currentColor;
            currentFont = sg.currentFont;
            if (verbose) {
                System.out.print('.');    // '.' means "cache" hit
                System.out.flush();
            }
        } else {
            /* Initialize using a non-SwingGraphics Graphics object.  The 
             * original object is cloned to prevent damage, and its initial
             * state recorded. */
            originalGraphics = g;
            graphics = g.create();
            previous = null;
            Rectangle cr = g.getClipBounds();
            if (cr == null) {
                if (clipRect == null) {
                    // Not a recycled SwingGraphics, allocate Rectangle.
                    clipRect = new Rectangle(0, 0, Integer.MAX_VALUE,
                                             Integer.MAX_VALUE);
                } else {
                    // Reuse recycled SwingGraphics' existing Rectangle.
                    clipRect.x = clipRect.y = 0;
                    clipRect.width = clipRect.height = Integer.MAX_VALUE;
                }
            } else {
                // Save returned Rectangle.
                clipRect = cr;
            }
            currentColor = g.getColor();
            currentFont = g.getFont();
            if (verbose) {
                System.out.print('*');    // '.' means "cache" miss
                System.out.flush();
            }
        }
    }

    public static Graphics createSwingGraphics(Graphics g) {
        if (g == null) {
            Thread.currentThread().dumpStack();
            return null;
        }

      
        return g.create();
        
















    }

    /**
     * Create a SwingGraphics from another Graphics object, and set its clip
     * to be the intersection of the first Graphics object's clip rect.
     * Graphics.create() normally does this, but Microsoft's SDK for Java
     * 2.0 doesn't set the clip of the returned object.  Since this method
     * is supposed to emulate what Graphics.create() does, all potential
     * bugs should be first checked with that method before changing the
     * behavior here.
     */
    static Graphics createSwingGraphics(Graphics g, int x, int y,
                                        int width, int height) {
      
        return g.create(x, y, width, height);
        
























    }

    public void translate(int x,int y) {
        graphics.translate(x,y);
        translateX += x;
        translateY += y;
        clipRect.x -= x;
        clipRect.y -= y;
    }

    public Color getColor() {
        return currentColor;
    }

    public void setColor(Color c) {
        graphics.setColor(c);
        currentColor = c;
    }

    public void setPaintMode() {
        graphics.setPaintMode();

        /* There's no way to restore the original paint mode. */
	restorable = false;
    }

    public void setXORMode(Color c1) {
        graphics.setXORMode(c1);

        /* There's no way to restore the original XOR mode. */
	restorable = false;
    }

    public Font getFont() {
        return currentFont;
    }

    public void setFont(Font font) {
        graphics.setFont(font);
        currentFont = font;
    }

    public FontMetrics getFontMetrics() {
	return graphics.getFontMetrics();
    }

    public FontMetrics getFontMetrics(Font f) {
        return graphics.getFontMetrics(f);
    }

    public Rectangle getClipBounds() {
        /* Clone rectangle since we can't return a const and don't want
         * the caller to change this rectangle. */
        return new Rectangle(clipRect);
    }

    public boolean isClipIntersecting(Rectangle r) {
        if (clipRect.x >= r.x + r.width || clipRect.x + clipRect.width <= r.x ||
            clipRect.y >= r.y + r.height || clipRect.y + clipRect.height <= r.y) {
            return false;
        }
        return !(clipRect.width == 0 || clipRect.height == 0 || r.width == 0 ||
                 r.height == 0);
    }

    public int getClipX() {
        return clipRect.x;
    }

    public int getClipY() {
        return clipRect.y;
    }

    public int getClipWidth() {
        return clipRect.width;
    }

    public int getClipHeight() {
        return clipRect.height;
    }

    public void clipRect(int x, int y, int width, int height) {
        graphics.clipRect(x,y,width,height);
        _changeClip(x, y, width, height, false);
    }

    public void setClip(int x, int y, int width, int height) {
        graphics.setClip(x,y,width,height);
        _changeClip(x, y, width, height, true);
    }

    public Shape getClip() {
        return graphics.getClip();
    }

    public void setClip(Shape clip) {
        graphics.setClip(clip);
        if(clip instanceof Rectangle) {
            Rectangle r = (Rectangle) clip;
            _changeClip(r.x,r.y,r.width,r.height,true);
        }
    }

    public void copyArea(int x, int y, int width, int height,
                         int dx, int dy) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        graphics.copyArea(x,y,width,height,dx,dy);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        graphics.drawLine(x1,y1,x2,y2);
    }

    public void fillRect(int x, int y, int width, int height) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        graphics.fillRect(x,y,width,height);
    }

    public void drawRect(int x, int y, int width, int height) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        graphics.drawRect(x, y, width, height);
    }

    public void clearRect(int x, int y, int width, int height) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        graphics.clearRect(x,y,width,height);
    }

    public void drawRoundRect(int x, int y, int width, int height,
                              int arcWidth, int arcHeight) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        graphics.drawRoundRect(x,y,width,height,arcWidth,arcHeight);
    }

    public void fillRoundRect(int x, int y, int width, int height,
                              int arcWidth, int arcHeight) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        graphics.fillRoundRect(x,y,width,height,arcWidth,arcHeight);
    }

    public void draw3DRect(int x, int y, int width, int height,
			   boolean raised) {
	graphics.draw3DRect(x, y, width, height, raised);
    }

    public void fill3DRect(int x, int y, int width, int height,
			   boolean raised) {
	graphics.fill3DRect(x, y, width, height, raised);
    }

    public void drawOval(int x, int y, int width, int height) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        graphics.drawOval(x,y,width,height);
    }

    public void fillOval(int x, int y, int width, int height) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        graphics.fillOval(x,y,width,height);
    }

    public void drawArc(int x, int y, int width, int height,
                        int startAngle, int arcAngle) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        graphics.drawArc(x,y,width,height,startAngle,arcAngle);
    }

    public void fillArc(int x, int y, int width, int height,
                        int startAngle, int arcAngle) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        graphics.fillArc(x,y,width,height,startAngle,arcAngle);
    }

    public void drawPolyline(int xPoints[], int yPoints[],
                             int nPoints) {
        graphics.drawPolyline(xPoints,yPoints,nPoints);
    }

    public void drawPolygon(int xPoints[], int yPoints[],
                            int nPoints) {
        graphics.drawPolygon(xPoints,yPoints,nPoints);
    }

    public void drawPolygon(Polygon p) {
        graphics.drawPolygon(p);
    }

    public void fillPolygon(int xPoints[], int yPoints[],
                            int nPoints) {
        graphics.fillPolygon(xPoints,yPoints,nPoints);
    }

    public void fillPolygon(Polygon p) {
        graphics.fillPolygon(p);
    }

    public void drawString(String str, int x, int y) {
        graphics.drawString(str,x,y);
    }

    
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        graphics.drawString(iterator,x,y);
    }
     

    public void drawChars(char data[], int offset, int length, int x, int y) {
        graphics.drawChars(data, offset, length, x, y);
    }

    public void drawBytes(byte data[], int offset, int length, int x, int y) {
	graphics.drawBytes(data, offset, length, x, y);
    }

    public boolean drawImage(Image img, int x, int y,
                             ImageObserver observer) {
        return graphics.drawImage(img,x,y,observer);
    }

    public boolean drawImage(Image img, int x, int y,
                             int width, int height,
                             ImageObserver observer) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        return graphics.drawImage(img,x,y,width,height,observer);
    }

    public boolean drawImage(Image img, int x, int y,
                             Color bgcolor,
                             ImageObserver observer) {
        return graphics.drawImage(img,x,y,bgcolor,observer);
    }

    public boolean drawImage(Image img, int x, int y,
                             int width, int height,
                             Color bgcolor,
                             ImageObserver observer) {
        if (debug && (width <= 0 || height <= 0)) {
            System.out.println("bad size:  " + width + "x" + height);
            Thread.dumpStack();
        }

        return graphics.drawImage(img,x,y,width,height,bgcolor,observer);
    }

    public boolean drawImage(Image img,
                             int dx1, int dy1, int dx2, int dy2,
                             int sx1, int sy1, int sx2, int sy2,
                             ImageObserver observer) {
        return graphics.drawImage(img,dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2,observer);
    }

    public boolean drawImage(Image img,
                             int dx1, int dy1, int dx2, int dy2,
                             int sx1, int sy1, int sx2, int sy2,
                             Color bgcolor,
                             ImageObserver observer) {
        return graphics.drawImage(img,dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2,bgcolor,observer);
    }

    /* Restore the shared sub-graphics object to its original state. */
    private void resetGraphics() {
        if (trace) {
            System.out.println("resetGraphics: 0x" + 
                               Integer.toHexString(hashCode()));
        }
	if (restorable) {
	    restoreGraphics();
	} else {
	    replaceGraphics();
	}
    }

    /* The shared sub-graphics object can be restored to the previous
     * state of this object, so do so.
     */
    private void restoreGraphics() {
        if (currentFont != previous.currentFont) {
            setFont(previous.currentFont);
        }
        if (currentColor != previous.currentColor) {
            setColor(previous.currentColor);
        }
        if (translateX != 0 || translateY != 0) {
            translate(-translateX, -translateY);
        }
        if (clipRect.x != previous.clipRect.x || 
            clipRect.y != previous.clipRect.y ||
            clipRect.width != previous.clipRect.width || 
            clipRect.height != previous.clipRect.height) {
            setClip(previous.clipRect.x, previous.clipRect.y,
                    previous.clipRect.width, previous.clipRect.height);
        }
    }

    /* The shared sub-graphics object has been modified in an unrestorable
     * way, so re-create one and substitute the new one for the clobbered
     * one in our stack.  This is an expensive operation.
     */
    private void replaceGraphics() {
        if (trace || debug) {
            System.out.println("replaceGraphics: 0x" + 
                               Integer.toHexString(hashCode()));
        }
        graphics = originalGraphics.create();
        redoTranslate();
        graphics.setColor(currentColor);
        graphics.setFont(currentFont);
        graphics.setClip(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
    }

    /* Reapply any translate operation.  This method is recursive so
     * that the multiple translates are applied in in the same order
     * as the SwingGraphics stack.
     */
    private void redoTranslate() {
        if (previous != null) {
            previous.redoTranslate();
        }
        if (translateX != 0 || translateY != 0) {
            graphics.translate(translateX, translateY);
        }
    }

    public void dispose() {
        if (trace) {
            System.out.println(
                "dispose: 0x" + Integer.toHexString(hashCode()) + 
                "(" + (previous == null ? "null" : 
                       Integer.toHexString(previous.hashCode())) + ")");
        }
        if (graphics != null) {
            if (previous != null) {
                // In stack - do a graphics state "pop".
                resetGraphics();
            } else {
                // Bottom of stack, truly dispose of the wrapped object.
                graphics.dispose();
            }
        }
        graphics = null;
        SwingGraphics.recycleSwingGraphics(this);
    }

    public void finalize() {
	graphics.finalize();
    }

    public String toString() {
        String fontString = currentFont.toString();
        fontString = fontString.substring(fontString.indexOf('['));
	return "SwingGraphics(0x" + Integer.toHexString(hashCode()) + 
            ") [subGraphics " + 
            originalGraphics.getClass().getName() + 
            "\n   translate [x=" + translateX + ",y=" + translateY +
            "] clip [x=" + clipRect.x + ",y=" + clipRect.y +
            ",w=" + clipRect.width + ",h=" + clipRect.height +
            "]\n   color [r=" + currentColor.getRed() + 
            ",g=" + currentColor.getGreen() + 
            ",b=" + currentColor.getBlue() + 
            "] font " + fontString + "]";
    }

    public Rectangle getClipRect() {
	return graphics.getClipRect();
    }

    private void _changeClip(int x,int y,int w,int h,boolean set) {
        if(set) {
            clipRect.x = x;
            clipRect.y = y;
            clipRect.width = w;
            clipRect.height = h;
        } else {
            SwingUtilities.computeIntersection(x,y,w,h,clipRect);
        }
    }

    private static Stack pool = new Stack();

    private static synchronized void recycleSwingGraphics(SwingGraphics g) {
        if (debug) {
            if (pool.indexOf(g) != -1) {
                System.out.println("Tried to recycle the same graphics twice!");
                Thread.dumpStack();
            }
        }
        pool.push(g);
    }

    private static synchronized SwingGraphics getRecycledSwingGraphics() {
        SwingGraphics r = null;
        if (pool.size() > 0) {
            r = (SwingGraphics) pool.pop();
        }
        return r;
    }

    static {
        // Warn if running wrong version of this class for this JDK.
        
          if (!SwingUtilities.is1dot2) {
              System.err.println("warning: running 1.2 version of SwingGraphics");
          }
          








    }
}
