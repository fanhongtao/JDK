/*
 * @(#)AniArea.java	1.16 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)AniArea.java	1.16 04/07/26
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
 * @version	1.16, 07/26/04
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

