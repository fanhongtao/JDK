/*
 * @(#)ExampleApplet.java	1.6 97/06/19 Jeff Dinkins
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
import actual.*;

/**
 * ExampleApplet: Applet that demonstrates 
 * RoundButtons.
 *
 * The applet creates a window that has a pretty background 
 * picture, and adds an RoundButton.
 *
 * Notice how the corners of the lightweight Round Button component are
 * "transparent" and you can see the image behind them! Cool!
 */
public class ExampleApplet extends Applet {

  Image background;

  public void init() {
      setLayout(new FlowLayout());
      loadBackgroundImage();

      // Create some round buttons, listeners, and add them to the panel
      RoundButton button1  = new RoundButton("Button 1");
      add(button1);

      RoundButton button2  = new RoundButton("Button 2");
      add(button2);

      RoundButton button3  = new RoundButton("Java is Cool!");
      add(button3);

      // Create action listener
      ExampleActionListener listener = new ExampleActionListener();
      button1.addActionListener(listener);
      button2.addActionListener(listener);
      button3.addActionListener(listener);
  }

  public void loadBackgroundImage() {
    //needed because ExampleApplet is running under Switcher
    Applet parentApplet;
    
    //Get the parent Applet object. 
    try {
      parentApplet = (Applet)getParent();
      background = parentApplet.getImage(parentApplet.getCodeBase(), "actual/images/scott.jpg");
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
   */
  public void paint(Graphics g) {
      // paint the background image
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

