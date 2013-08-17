/*
 * @(#)Spinner.java	1.2 97/01/14 Jeff Dinkins
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
 * Spinner - a class that creates a lightweight component that
 * shows a spinning wheel.
 *
 * Lightweight components can have "transparent" areas, meaning that
 * you can see the background of the container behind these areas.
 *
 */
public class Spinner extends Component {

  float percentDone = 0;
  int totalTicks    = 60;
  int currentTick   = 0;
  
  SpinnerThread spinnerThread;
  
  /**
   * Constructs a Spinner
   */
  public Spinner() {
      setForeground(Color.gray);
      setForeground(Color.lightGray);
  }
  
  /**
   * paints the Spinner
   */
  public void paint(Graphics g) {
      int start_angle = 90;
      int done_angle = (int) (percentDone * 360);
      
      g.setColor(getBackground());
      g.fillArc(3, 3, getSize().width-8, getSize().height-8, 0, 360);
      
      g.setColor(getForeground());
      g.fillArc(3, 3, getSize().width-8, getSize().height-8, start_angle, done_angle);

      g.setColor(Color.black);
      g.drawArc(3, 3, getSize().width-8, getSize().height-8, 0, 360);
  }

  public void setCurrentTick(int tick) {
      currentTick = tick;

      if(currentTick > totalTicks) {
	  percentDone = 1;
      } else if(currentTick == 0) {
	  percentDone = 0;
      } else {
	  percentDone = (float) currentTick / (float) totalTicks;
      }
      
      // Repaint might flicker a bit. To avoid this, you can use
      // double buffering (see the Gauge example).
      repaint();
  }

  public void startSpinning() {
      spinnerThread = new SpinnerThread(this);
      spinnerThread.start();
  }

  public void stopSpinning() {
      spinnerThread.stop();
      spinnerThread = null;
  }

  public void setTotalTicks(int tick) {
      totalTicks = tick;
  }

  public int getTotalTicks() {
      return totalTicks;
  }

  public int getCurrentTick() {
      return currentTick;
  }


}



/**
 * SpinnerThread: spins the wheel
 */
class SpinnerThread extends Thread {

  Spinner spinner;

  SpinnerThread(Spinner spinner) {
      super("Spinner Thread");
      this.spinner = spinner;
  }

  public void run () {
      int i = spinner.getCurrentTick();
      while(true) {
	  try {
	      while (i-- > 0) {
		  spinner.setCurrentTick(i);
		  sleep(100);
	      }
	  } catch (java.lang.InterruptedException e) {
	      // don't care if we are interrupted
	  }
	  i = spinner.getTotalTicks();
      }
  }
}
 
