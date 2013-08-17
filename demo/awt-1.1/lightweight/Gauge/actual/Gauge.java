/*
 * @(#)Gauge.java	1.2 97/01/14 Jeff Dinkins
 *
 * Copyright (c) 1995-1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 */

package actual; 
import java.awt.*;
import java.applet.*;

/*
 * Gauge - a class that implements a lightweight component
 * that can be used, for example, as a performance meter.
 *
 * Lightweight components can have "transparent" areas, meaning that
 * you can see the background of the container behind these areas.
 *
 */
public class Gauge extends Component {
    
  // the current and total amounts that the gauge reperesents
  int current = 0;
  int total = 100;

  // The preferred size of the gauge
  int Height = 18;   // looks good
  int Width  = 250;  // arbitrary 

  /**
   * Constructs a Gauge
   */
  public Gauge() {
      this(Color.lightGray);
  }

  /**
   * Constructs a that will be drawn uses the
   * specified color.
   *
   * @gaugeColor the color of this Gauge
   */
  public Gauge(Color gaugeColor) {
      setBackground(gaugeColor);
  }

  public void paint(Graphics g) {
      int barWidth = (int) (((float)current/(float)total) * getSize().width);
      g.setColor(getBackground());
      g.fill3DRect(0, 0, barWidth, getSize().height-2, true);
  }

  public void setCurrentAmount(int Amount) {
      current = Amount; 

      // make sure we don't go over total
      if(current > 100)
       current = 100;

      repaint();
  }

  public int getCurrentAmount() {
      return current;
  }

  public int getTotalAmount() {
      return total;
  }

  public Dimension getPreferredSize() {
      return new Dimension(Width, Height);
  }

  public Dimension getMinimumSize() {
      return new Dimension(Width, Height);
  }

}

