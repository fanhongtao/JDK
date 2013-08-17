/*
 * @(#)OpenlookButton.java	1.2 97/01/14 Jeff Dinkins
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
 * OpenlookButton - a class that produces a lightweight button.
 *
 * Lightweight components can have "transparent" areas, meaning that
 * you can see the background of the container behind them.
 *
 */
public class OpenlookButton extends Component {

  static int capWidth = 20;          // The width of the Button's endcap
  String label;                      // The Button's text
  protected boolean pressed = false; // true if the button is detented.
  ActionListener actionListener;     // Post action events to listeners
  
  
  /**
   * Constructs an OpenlookButton with no label.
   */
  public OpenlookButton() {
      this("");
  }

  /**
   * Constructs an OpenlookButton with the specified label.
   * @param label the label of the button
   */
  public OpenlookButton(String label) {
      this.label = label;
      enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }

  /**
   * gets the label
   * @see setLabel
   */
  public String getLabel() {
      return label;
  }
  
  /**
   * sets the label
   * @see getLabel
   */
  public void setLabel(String label) {
      this.label = label;
      invalidate();
      repaint();
  }
  
  /**
   * paints the button
   */
  public void paint(Graphics g) {
      int width = getSize().width - 1;
      int height = getSize().height - 1;

      Color interior;
      Color highlight1;
      Color highlight2;
      
      interior = getBackground();

      // ***** determine what colors to use
      if(pressed) {
	  highlight1 = interior.darker();
	  highlight2 = interior.brighter();
      } else {
	  highlight1 = interior.brighter();
	  highlight2 = interior.darker();
      }

      // ***** paint the interior of the button
      g.setColor(interior);
      // left cap
      g.fillArc(0, 0, 			    // start
		capWidth, height,    	    // size
		90, 180);		    // angle

      // right cap
      g.fillArc(width - capWidth, 0,        // start
		capWidth, height,           // size
		270, 180);		    // angle

      // inner rectangle
      g.fillRect(capWidth/2, 0, width - capWidth, height);
      

      // ***** highlight the perimeter of the button
      // draw upper and lower highlight lines
      g.setColor(highlight1);
      g.drawLine(capWidth/2, 0, width - capWidth/2, 0);
      g.setColor(highlight2);
      g.drawLine(capWidth/2, height, width - capWidth/2, height);

      // upper arc left cap
      g.setColor(highlight1);
      g.drawArc(0, 0,                       // start
                capWidth, height,           // size
                90, 180-40                  // angle
      );
 
      // lower arc left cap
      g.setColor(highlight2);
      g.drawArc(0, 0,                       // start
                capWidth, height,           // size
                270-40, 40                  // angle
      );

      // upper arc right cap
      g.setColor(highlight1);
      g.drawArc(width - capWidth, 0,        // start
                capWidth, height,           // size
                90-40, 40                   // angle
      );
 
      // lower arc right cap
      g.setColor(highlight2);
      g.drawArc(width - capWidth, 0,        // start
                capWidth, height,           // size
                270, 180-40                 // angle
      );

      // ***** draw the label centered in the button
      Font f = getFont();
      if(f != null) {
	  FontMetrics fm = getFontMetrics(getFont());
	  g.setColor(getForeground());
	  g.drawString(label,
		       width/2 - fm.stringWidth(label)/2,
		       height/2 + fm.getHeight()/2 - fm.getMaxDescent()
	  );
      }
  }
  
  /**
   * The preferred size of the button. 
   */
  public Dimension getPreferredSize() {
      Font f = getFont();
      if(f != null) {
	  FontMetrics fm = getFontMetrics(getFont());
	  return new Dimension(fm.stringWidth(label) + capWidth*2,
			       fm.getHeight() + 10);
      } else {
	  return new Dimension(100, 50);
      }
  }
  
  /**
   * The minimum size of the button. 
   */
  public Dimension getMinimumSize() {
      return new Dimension(100, 50);
  }

  /**
   * Adds the specified action listener to receive action events
   * from this button.
   * @param listener the action listener
   */
   public void addActionListener(ActionListener listener) {
       actionListener = AWTEventMulticaster.add(actionListener, listener);
       enableEvents(AWTEvent.MOUSE_EVENT_MASK);
   }
 
   /**
    * Removes the specified action listener so it no longer receives
    * action events from this button.
    * @param listener the action listener
    */
   public void removeActionListener(ActionListener listener) {
       actionListener = AWTEventMulticaster.remove(actionListener, listener);
   }
 
 
   /**
    * Paints the button and sends an action event to all listeners.
    */
   public void processMouseEvent(MouseEvent e) {
       Graphics g;
       switch(e.getID()) {
          case MouseEvent.MOUSE_PRESSED:
            // render myself inverted....
            pressed = true;
	
	    // Repaint might flicker a bit. To avoid this, you can use
	    // double buffering (see the Gauge example).
	    repaint();
            break;
          case MouseEvent.MOUSE_RELEASED:
            if(actionListener != null) {
               actionListener.actionPerformed(new ActionEvent(
                   this, ActionEvent.ACTION_PERFORMED, label));
            }
            // render myself normal again
            if(pressed == true) {
                pressed = false;

	        // Repaint might flicker a bit. To avoid this, you can use
	        // double buffering (see the Gauge example).
		repaint();
            }
            break;
          case MouseEvent.MOUSE_ENTERED:
 
            break;
          case MouseEvent.MOUSE_EXITED:
            if(pressed == true) {
                // Cancel! Don't send action event.
                pressed = false;

	        // Repaint might flicker a bit. To avoid this, you can use
	        // double buffering (see the Gauge example).
		repaint();

                // Note: for a more complete button implementation,
                // you wouldn't want to cancel at this point, but
                // rather detect when the mouse re-entered, and
                // re-highlight the button. There are a few state
                // issues that that you need to handle, which we leave
                // this an an excercise for the reader (I always
                // wanted to say that!)
            }
            break;
       }
       super.processMouseEvent(e);
   }


}

