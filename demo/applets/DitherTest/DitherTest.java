/*
 * @(#)DitherTest.java	1.7 99/08/04
 *
 * Copyright (c) 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.applet.Applet;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.lang.InterruptedException;

public class DitherTest extends Applet implements Runnable {
    final static int NOOP = 0;
    final static int RED = 1;
    final static int GREEN = 2;
    final static int BLUE = 3;
    final static int ALPHA = 4;
    final static int SATURATION = 5;

    ThreadGroup appletThreadGroup;
    Thread runner;

    DitherControls XControls;
    DitherControls YControls;
    DitherCanvas canvas;

    public void init() {
	String xspec, yspec;
	int xvals[] = new int[2];
	int yvals[] = new int[2];

	try {
	    xspec = getParameter("xaxis");
	} catch (Exception e) {
	    xspec = null;
	}
	try {
	    yspec = getParameter("yaxis");
	} catch (Exception e) {
	    yspec = null;
	}
	if (xspec == null) xspec = "red";
	if (yspec == null) yspec = "blue";
	int xmethod = colormethod(xspec, xvals);
	int ymethod = colormethod(yspec, yvals);

	setLayout(new BorderLayout());
	XControls = new DitherControls(this, xvals[0], xvals[1],
				       xmethod, false);
	YControls = new DitherControls(this, yvals[0], yvals[1],
				       ymethod, true);
	YControls.addRenderButton();
	add("North", XControls);
	add("South", YControls);
	add("Center", canvas = new DitherCanvas());

	appletThreadGroup = Thread.currentThread().getThreadGroup();
    }

    public void destroy() {
        remove(XControls);
        remove(YControls);
        remove(canvas);
    }

    public void start() {
        runner = new Thread(this);
	runner.start();
    }

    public void stop() {
	runner = null;
    }

    public static void main(String args[]) {
	Frame f = new Frame("DitherTest");
	DitherTest	ditherTest = new DitherTest();

	ditherTest.init();

	f.add("Center", ditherTest);
	f.pack();
	f.show();

	ditherTest.start();
    }

    int colormethod(String s, int vals[]) {
	int method = NOOP;

	if (s == null)
	    s = "";

	String lower = s.toLowerCase();
	int len = 0;
	if (lower.startsWith("red")) {
	    method = RED;
	    lower = lower.substring(3);
	} else if (lower.startsWith("green")) {
	    method = GREEN;
	    lower = lower.substring(5);
	} else if (lower.startsWith("blue")) {
	    method = BLUE;
	    lower = lower.substring(4);
	} else if (lower.startsWith("alpha")) {
	    method = ALPHA;
	    lower = lower.substring(4);
	} else if (lower.startsWith("saturation")) {
	    method = SATURATION;
	    lower = lower.substring(10);
	}

	if (method == NOOP) {
	    vals[0] = 0;
	    vals[1] = 0;
	    return method;
	}

	int begval = 0;
	int endval = 255;

	try {
	    int dash = lower.indexOf('-');
	    if (dash < 0) {
		begval = endval = Integer.parseInt(lower);
	    } else {
		begval = Integer.parseInt(lower.substring(0, dash));
		endval = Integer.parseInt(lower.substring(dash+1));
	    }
	} catch (Exception e) {
	}

	if (begval < 0) begval = 0;
	if (endval < 0) endval = 0;
	if (begval > 255) begval = 255;
	if (endval > 255) endval = 255;

	vals[0] = begval;
	vals[1] = endval;

	return method;
    }

    void applymethod(int c[], int method, int step, int total, int vals[]) {
	if (method == NOOP)
	    return;
	int val = ((total < 2)
		   ? vals[0]
		   : vals[0] + ((vals[1] - vals[0]) * step / (total - 1)));
	switch (method) {
	case RED:
	    c[0] = val;
	    break;
	case GREEN:
	    c[1] = val;
	    break;
	case BLUE:
	    c[2] = val;
	    break;
	case ALPHA:
	    c[3] = val;
	    break;
	case SATURATION:
	    int max = Math.max(Math.max(c[0], c[1]), c[2]);
	    int min = max * (255 - val) / 255;
	    if (c[0] == 0) c[0] = min;
	    if (c[1] == 0) c[1] = min;
	    if (c[2] == 0) c[2] = min;
	    break;
	}
    }

    public void run() {
        canvas.setImage(null);	// Wipe previous image
        Image img = calculateImage();
        synchronized(this) {
            if (img != null && runner == Thread.currentThread())
                canvas.setImage(img);
        }
    }

    /**
     * Calculates and returns the image.  Halts the calculation and returns
     * null if the Applet is stopped during the calculation.
     */
    Image calculateImage() {
        Thread me = Thread.currentThread();

        int width = canvas.getSize().width;
	int height = canvas.getSize().height;
	int xvals[] = new int[2];
	int yvals[] = new int[2];
	int xmethod = XControls.getParams(xvals);
	int ymethod = YControls.getParams(yvals);
	int pixels[] = new int[width * height];
	int c[] = new int[4];
	int index = 0;
	for (int j = 0; j < height; j++) {
	    for (int i = 0; i < width; i++) {
		c[0] = c[1] = c[2] = 0;
		c[3] = 255;
		if (xmethod < ymethod) {
		    applymethod(c, xmethod, i, width, xvals);
		    applymethod(c, ymethod, j, height, yvals);
		} else {
		    applymethod(c, ymethod, j, height, yvals);
		    applymethod(c, xmethod, i, width, xvals);
		}
		pixels[index++] = ((c[3] << 24) |
				   (c[0] << 16) |
				   (c[1] << 8) |
				   (c[2] << 0));
	    }

            // Poll once per row to see if we've been told to stop.
            if (runner != me)
                return null;
	}

        return createImage(new MemoryImageSource(width, height,
		      ColorModel.getRGBdefault(), pixels, 0, width));
    }

    public String getAppletInfo() {
        return "An interactive demonstration of dithering.";
    }

    public String[][] getParameterInfo() {
        String[][] info = {
            {"xaxis", "{RED, GREEN, BLUE, PINK, ORANGE, MAGENTA, CYAN, WHITE, YELLOW, GRAY, DARKGRAY}", "The color of the Y axis.  Default is RED."},
            {"yaxis", "{RED, GREEN, BLUE, PINK, ORANGE, MAGENTA, CYAN, WHITE, YELLOW, GRAY, DARKGRAY}", "The color of the X axis.  Default is BLUE."}
        };
        return info;
    }
}

class DitherCanvas extends Canvas {
    Image img;
    static String calcString = "Calculating...";

    public void paint(Graphics g) {
	int w = getSize().width;
	int h = getSize().height;
	if (img == null) {
	    super.paint(g);
	    g.setColor(Color.black);
	    FontMetrics fm = g.getFontMetrics();
	    int x = (w - fm.stringWidth(calcString))/2;
	    int y = h/2;
	    g.drawString(calcString, x, y);
	} else {
            g.drawImage(img, 0, 0, w, h, this);
        }
    }

    public void update(Graphics g) {
	paint(g);
    }

    public Dimension getMinimumSize() {
	return new Dimension(20, 20);
    }

    public Dimension getPreferredSize() {
	return new Dimension(200, 200);
    }

    public Image getImage() {
	return img;
    }

    public void setImage(Image img) {
	this.img = img;
	Graphics g = getGraphics();
	try {
	    paint(g);
	} finally {
	    g.dispose();
	}
    }
}

class DitherControls extends Panel implements ActionListener {
    TextField start;
    TextField end;
    Button button;
    Choice choice;
    DitherTest applet;

    static LayoutManager dcLayout = new FlowLayout(FlowLayout.CENTER, 10, 5);

    public DitherControls(DitherTest app, int s, int e, int type,
			  boolean vertical) {
	applet = app;
	setLayout(dcLayout);
	add(new Label(vertical ? "Vertical" : "Horizontal"));
	add(choice = new Choice());
	choice.addItem("Noop");
	choice.addItem("Red");
	choice.addItem("Green");
	choice.addItem("Blue");
	choice.addItem("Alpha");
	choice.addItem("Saturation");
	choice.select(type);
	add(start = new TextField(Integer.toString(s), 4));
	add(end = new TextField(Integer.toString(e), 4));
    }

    public void addRenderButton() {
	add(button = new Button("New Image"));
	button.addActionListener(this);
    }

    public int getParams(int vals[]) {
	vals[0] = Integer.parseInt(start.getText());
	vals[1] = Integer.parseInt(end.getText());
	return choice.getSelectedIndex();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            applet.start();
        }
    }
}
