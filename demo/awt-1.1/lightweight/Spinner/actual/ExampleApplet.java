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

/**
 * ExampleApplet: Applet that demonstrates the
 * Spinner component.
 *
 * The applet creates a window that has a pretty 
 * background picture in it, and throws some lightweight
 * Spinner components in. Notice how the lightweight
 * Spinner component has "transparent" areas that let you see
 * the image behind it! Cool!
 */
public class ExampleApplet extends Applet {

  Image orb;

  GridBagLayout gridbag = new GridBagLayout();
  GridBagConstraints c = new GridBagConstraints();

  public void init() {
      setLayout(gridbag);

      loadBackgroundImage();
      initializeGridbag();

      // ************* Create Spinners
      Spinner  spinner1  = new Spinner();
      Spinner  spinner2  = new Spinner();

      // Set the color of the two spinners
      spinner1.setBackground(Color.yellow);
      spinner1.setForeground(Color.red);

      spinner2.setBackground(new Color(100, 0, 255));
      spinner2.setForeground(new Color(50, 255, 255));

      // ************* add components
      // add spinner1 
      c.gridx = 0;     c.gridy = 0;
      gridbag.setConstraints(spinner1, c);
      add(spinner1);

      // add spinner2
      c.gridx = 1;     c.gridy = 1;
      gridbag.setConstraints(spinner2, c);
      add(spinner2);

      spinner1.startSpinning();
      spinner2.startSpinning();
  }

  public void initializeGridbag() {
      c.gridwidth = 1; c.gridheight = 1;
      c.weightx = 1;   c.weighty = 1;
      c.fill = GridBagConstraints.BOTH;
      c.anchor= GridBagConstraints.NORTHWEST;
  }

  public void loadBackgroundImage() {
    //needed because ExampleApplet is running under Switcher
    Applet parentApplet;
    
    //Get the parent Applet object. 
    try {
      parentApplet = (Applet)getParent();
      orb = parentApplet.getImage(parentApplet.getCodeBase(), 
				  "actual/images/orb.gif"); 
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
      g.drawImage(orb, 0, 0, getSize().width, getSize().height, 
		  getBackground(), this);
      super.paint(g);
  }

}

