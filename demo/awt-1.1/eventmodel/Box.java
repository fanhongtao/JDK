/*
 * @(#)Box.java	1.2 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


import java.awt.*;
import java.awt.event.*;


public class Box extends Component
{
  Box(Color c)
  {
    setForeground(c);
  }

  public void paint(Graphics g)
  {
    super.paint(g);
    g.setColor(getForeground());
    g.fill3DRect(0, 0, getSize().width-1, getSize().height-1, true);
  }
  
  public Dimension getPreferredSize()
  {
    return new Dimension(64, 16);
  }
}

