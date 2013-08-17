/*
 * @(#)ExampleApplet.java	1.2 97/01/14 Jeff Dinkins
 *
 * Copyright (c) 1995-1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 */

package actual;

import java.applet.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ExampleApplet: Applet that demonstrates 
 * OpenlookButtons.
 *
 * The applet creates a window that has a pretty background 
 * picture, and adds an OpenlookButton.
 *
 * Notice how the lightweight Openlook Button component has "transparent" 
 * corners and you can see the image behind them! Cool!
 */
public class ExampleApplet extends Applet {

  Image background;

  public void init() {
      setLayout(new FlowLayout());
      loadBackgroundImage();

      // *** Create buttons
      OpenlookButton button1  = new OpenlookButton("Motif sucks");
      add(button1);

      OpenlookButton button2  = new OpenlookButton("I miss Openlook!");
      add(button2);

      OpenlookButton button3  = new OpenlookButton("Java is Cool!");
      add(button3);

      // *** Create button listener
      ExampleActionListener listener = new ExampleActionListener();
      button1.addActionListener(listener);
      button2.addActionListener(listener);
      button3.addActionListener(listener);
  }

  public void loadBackgroundImage() {
      
    //needed because this is running under Switcher
    Applet parentApplet;
    
    /* Get the parent Applet object. */
    try {
      parentApplet = (Applet)getParent();
      background = parentApplet.getImage(parentApplet.getCodeBase(), 
					 "actual/images/scott.jpg");
    } catch (ClassCastException e) {
      System.err.println("Parent isn't an Applet!");
      throw(e);
    }  
  }

  /**
   * override update to *not* erase the background before painting
   */
  public void update(Graphics g) {
      paint(g);
  }

  /**
   * paint the background picture, then call super.paint which
   * will paint all contained components 
   *
   * NOTE: You MUST call super.paint(g) or the lightweight 
   * component(s) won't get painted.
   */
  public void paint(Graphics g) {
      g.drawImage(background, 0, 0, getSize().width, getSize().height,
                  getBackground(), this);
      super.paint(g);
  }

}

class ExampleActionListener implements ActionListener {
 
    public ExampleActionListener() {
    }
 
    public void actionPerformed(ActionEvent e) {
        System.out.println("Button Pressed: " + e.getActionCommand());
    }
}

