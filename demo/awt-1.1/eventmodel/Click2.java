/*
 * @(#)Click2.java	1.2 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


import java.awt.*;
import java.awt.event.*;
import java.applet.*;


/** 
 * This version of the Click applet fixes Click2 by extending
 * the nested TargetListener class with methods that redispatch
 * the mouse motion events from the target columns to the applet.
 * Note that we're translating the events coordinates from the
 * source components coordinate system to the applets coordinate
 * system. 
 * 
 * This applet runs correctly in HotJava, it requires JDK 1.1.
 */

public class Click2 extends Applet
{
  Color puckColor = new Color(200, 0, 10);
  Box puck = new Box(puckColor);
  ColumnOfBoxes[] targets = new ColumnOfBoxes[8];

  private final static class TargetListener 
    extends MouseAdapter implements MouseMotionListener
  {
    private Color newBackground;
    private Color oldBackground;

    TargetListener(Color newBackground) {
      this.newBackground = newBackground;
    }

    public void mouseEntered(MouseEvent e) {
      oldBackground = e.getComponent().getBackground();
      e.getComponent().setBackground(newBackground);
    }

    public void mouseExited(MouseEvent e) {
      e.getComponent().setBackground(oldBackground);
    }

    private void redispatch(MouseEvent e) {
      Point origin = e.getComponent().getLocation();
      e.translatePoint(origin.x, origin.y);
      e.getComponent().getParent().dispatchEvent(e);
    }

    public void mouseMoved(MouseEvent e) {  redispatch(e); }
    public void mouseDragged(MouseEvent e) { redispatch(e); }
    public void mouseClicked(MouseEvent e) { redispatch(e); }
  }

  public Click2()
  {
    MouseMotionListener movePuck = new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e)
      {
	int x = e.getX();
	int y = getSize().height - puck.getSize().height;
	puck.setLocation(x, y);
      }
    };

    /* Create a row of targets, i.e. columns of boxes, along
     * the top of the applet.  Each target column contains
     * between one and four boxes.
     */

    for(int i = 0; i < targets.length; i++) {
      int nBoxes = 1 + (int)(Math.random() * 4.0);
      float boxHue = (float)i / (float)targets.length;
      Color boxColor = Color.getHSBColor(boxHue, 0.5f, 0.85f);
      TargetListener tl = new TargetListener(boxColor.brighter());
      targets[i] = new ColumnOfBoxes(boxColor, nBoxes);
      targets[i].addMouseListener(tl);
      targets[i].addMouseMotionListener(tl);
      add(targets[i]);
    }

    add(puck);
    addMouseMotionListener(movePuck);
  }

  public static void main(String[] args)
  {
    WindowListener l = new WindowAdapter()
      {
	public void windowClosing(WindowEvent e) {System.exit(0);}
      };

    Frame f = new Frame("Click");
    f.addWindowListener(l); 
    f.add(new Click2());
    f.setSize(600, 400);
    f.show();
  }
}
