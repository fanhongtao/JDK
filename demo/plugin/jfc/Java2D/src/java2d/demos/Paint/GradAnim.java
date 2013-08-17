/*
 * @(#)GradAnim.java	1.14 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java2d.demos.Paint;

import java.awt.*;
import java2d.AnimatingSurface;


/**
 * GradientPaint animation.
 */
public class GradAnim extends AnimatingSurface {

    private static final int MAX_HUE = 256 * 6;
    private animval x1, y1, x2, y2;
    private int hue = (int) (Math.random() * MAX_HUE);


    public GradAnim() {
        setBackground(Color.white);
        x1 = new animval(0, 300, 2, 10);
        y1 = new animval(0, 300, 2, 10);
        x2 = new animval(0, 300, 2, 10);
        y2 = new animval(0, 300, 2, 10);
    }


    public void reset(int w, int h) {
        x1.newlimits(0, w);
        y1.newlimits(0, h);
        x2.newlimits(0, w);
        y2.newlimits(0, h);
    }


    public void step(int w, int h) {
        x1.anim(); y1.anim();
        x2.anim(); y2.anim();
        hue = (hue + (int) (Math.random() * 10)) % MAX_HUE;
    }


    public static Color getColor(int hue) {
        int leg = (hue / 256) % 6;
        int step = (hue % 256) * 2;
        int falling = (step < 256) ? 255 : 511 - step;
        int rising = (step < 256) ? step : 255;
        int r, g, b;
        r = g = b = 0;
        switch (leg) {
        case 0:
            r = 255;
            break;
        case 1:
            r = falling;
            g = rising;
            break;
        case 2:
            g = 255;
            break;
        case 3:
            g = falling;
            b = rising;
            break;
        case 4:
            b = 255;
            break;
        case 5:
            b = falling;
            r = rising;
            break;
        }
        return new Color(r, g, b);
    }


    public void render(int w, int h, Graphics2D g2) {
        Color c1 = getColor(hue);
        Color c2 = getColor(hue + 256 * 3);
        GradientPaint gp = new GradientPaint(x1.getFlt(), y1.getFlt(), c1,
                                         x2.getFlt(), y2.getFlt(), c2,
                                         true);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        g2.setColor(Color.yellow);
        g2.drawLine(x1.getInt(), y1.getInt(), x2.getInt(), y2.getInt());
    }


    public class animval {
        float curval;
        float lowval;
        float highval;
        float currate;
        float lowrate;
        float highrate;

        public animval(int lowval, int highval,
                       int lowrate, int highrate) {
            this.lowval = lowval;
            this.highval = highval;
            this.lowrate = lowrate;
            this.highrate = highrate;
            this.curval = randval(lowval, highval);
            this.currate = randval(lowrate, highrate);
        }

        public float randval(float low, float high) {
            return (float) (low + Math.random() * (high - low));
        }

        public float getFlt() {
            return curval;
        }

        public int getInt() {
            return (int) curval;
        }

        public void anim() {
            curval += currate;
            clip();
        }

        public void clip() {
            if (curval > highval) {
                curval = highval - (curval - highval);
                if (curval < lowval) {
                    curval = highval;
                }
                currate = - randval(lowrate, highrate);
            } else if (curval < lowval) {
                curval = lowval + (lowval - curval);
                if (curval > highval) {
                    curval = lowval;
                }
                currate = randval(lowrate, highrate);
            }
        }

        public void newlimits(int lowval, int highval) {
            this.lowval = lowval;
            this.highval = highval;
            clip();
        }
    }


    public static void main(String argv[]) {
        createDemoFrame(new GradAnim());
    }
}
