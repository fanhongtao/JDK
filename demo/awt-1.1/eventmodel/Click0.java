
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

/** 
 * Here's a simple example of an anonymous nested class that implements
 * the MouseMotionListener interface.  It does so by extending 
 * MouseMotionAdapter, a utility class that provides no-op implementations
 * for all of the methods in MouseMotionListener.  In this case we're
 * just handling mouseMoved() events by moving the "puck" along the
 * bottom edge of the applet.
 * 
 * Note that the listener implementation can refer to fields defined
 * in enclosing scopes, e.g. the Box field called puck, directly.  
 * 
 * This applet runs correctly in HotJava, it requires JDK 1.1.
 */

public class Click0 extends Applet
{
  Color puckColor = new Color(200, 0, 10);
  Box puck = new Box(puckColor);

  public Click0()
  {
    MouseMotionListener movePuck = new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e)
      {
	int x = e.getX();
	int y = getSize().height - puck.getSize().height;
	puck.setLocation(x, y);
      }
    };

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
    f.add(new Click0());
    f.setSize(400, 400);
    f.show();
  }
}
