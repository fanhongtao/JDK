/*
 * @(#)AniArea.java	1.12 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.Graphics;
import java.util.StringTokenizer;
import java.awt.Image;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * This ImageArea provides for a button that animates when the mouse is
 * over it. The animation is specifed as a base image that contains all
 * of the animation frames and then a series of X,Y coordinate pairs that
 * define the top left corner of each new frame.
 *
 * @author	Chuck McManis
 * @version	1.12, 12/03/01
 */
class AniArea extends ImageMapArea {

    Image sourceImage;
    int	 nFrames;
    int  coords[];
    int	 currentFrame = 0;

    public void handleArg(String s) {
	StringTokenizer st = new StringTokenizer(s, ", ");
	int	i;
        String imgName;

	imgName = st.nextToken();
	try {
	    sourceImage = parent.getImage(new URL(parent.getDocumentBase(),
						  imgName));
	    parent.addImage(sourceImage);
	} catch (MalformedURLException e) {}

	nFrames = 0;
	coords = new int[40];

	while (st.hasMoreTokens()) {
	    coords[nFrames*2]     = Integer.parseInt(st.nextToken());
	    coords[(nFrames*2)+1] = Integer.parseInt(st.nextToken());
	    nFrames++;
	    if (nFrames > 19)
		break;
	}
    }

    public boolean animate() {
	if (entered) {
	    repaint();
	}
	return entered;
    }

    public void enter() {
	currentFrame = 0;
	parent.startAnimation();
    }

    public void highlight(Graphics g) {
	if (entered) {
	    drawImage(g, sourceImage, 
		      X-coords[currentFrame*2], Y-coords[(currentFrame*2)+1],
		      X, Y, W, H);
	    currentFrame++;
	    if (currentFrame >= nFrames)
		currentFrame = 0;
	}
    }
  public String getAppletInfo() {
    return "Title: AniArea \nAuthor: Chuck McManis \nThis ImageMapArea subclass provides for a button that animates when the mouse is over it. The animation is specifed as a base image that contains all of the animation frames and then a series of X,Y coordinate pairs that define the top left corner of each new frame.";
  }
}

