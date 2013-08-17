/*
 * @(#)PictureFrame.java	1.2 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package alternative;

import java.awt.*;

public class PictureFrame extends Frame {
    boolean inAnApplet = true;
    Image image;

    PictureFrame(Image image, int width, int height) {
	super("PictureFrame");

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	setLayout(gridbag);

	//Create the image and start downloading it.
	MediaTracker tracker = new MediaTracker(this);
	this.image = image;
	tracker.addImage(image, 0);
	tracker.checkAll(true);

	c.gridwidth = GridBagConstraints.REMAINDER;
	c.weightx = 1.0;
	Label label1 = new Label("Here's what you'd see if you were using "
		                 + "a 1.1-compatible browser:");
	gridbag.setConstraints(label1, c);
	add(label1);

	c.weighty = 1.0;
	ImageDisplayer imageDisplayer = new ImageDisplayer(image,
							   width,
							   height);
	gridbag.setConstraints(imageDisplayer, c);
	add(imageDisplayer);

	c.weighty = 0.0;
	Label label2 = new Label("Remember, this is just a picture!");
	label2.setForeground(Color.red);
	gridbag.setConstraints(label2, c);
	add(label2);
    }

    public boolean handleEvent(Event e) {
	if (e.id == Event.WINDOW_DESTROY) {
	    if (inAnApplet) {
		dispose();
	    } else {
		System.exit(0);
	    }
	}
	return super.handleEvent(e);
    }

    public static void main(String[] args) {
	PictureFrame frame = 
	    new PictureFrame(Toolkit.getDefaultToolkit().getImage("Beeper.gif"), 200, 200);
	frame.inAnApplet = false;
        frame.pack();
        frame.show();
    }
}
