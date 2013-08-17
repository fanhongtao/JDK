/*
 * @(#)DoubleBufferPanel.java	1.2 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package actual;
 
import java.awt.*;
import java.applet.*;

public class DoubleBufferPanel extends Panel {
    
  Image offscreen;

  /**
   * null out the offscreen buffer as part of invalidation
   */
  public void invalidate() {
      super.invalidate();
      offscreen = null;
  }

  /**
   * override update to *not* erase the background before painting
   */
  public void update(Graphics g) {
      paint(g);
  }

  /**
   * paint children into an offscreen buffer, then blast entire image
   * at once.
   */
  public void paint(Graphics g) {
      if(offscreen == null) {
         offscreen = createImage(getSize().width, getSize().height);
      }

      Graphics og = offscreen.getGraphics();
      og.setClip(0,0,getSize().width, getSize().height);
      super.paint(og);
      g.drawImage(offscreen, 0, 0, null);
      og.dispose();
  }

}

