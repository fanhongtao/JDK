
import java.awt.*;
import java.awt.event.*;
import java.applet.*;


/** 
 * This version of the Click applet extends Click1 by adding a row
 * of "targets" along the top of the applet panel that highlight
 * when the mouse passes over them.  The listener that handles
 * enter/exit events is an instance of the private static class 
 * TargetListener.   A static class can't refer to fields or variables 
 * in enclosing scopes like an ordinary nested class (TargetListener
 * doesn't need to - it's self contained) however it's name is
 * scoped to the outer class.  For example, if TargetClass was 
 * public it could be used as Click1.TargetListener.  We've made
 * it private because it's not intended to be used outside of the
 * implementation of Click1.
 * 
 * Click1 has a bug: the puck no longer tracks the mouse correctly.
 * The fix is in Click2. 
 * 
 * This applet runs correctly in HotJava, it requires JDK 1.1.
 */

public class Click1 extends Applet
{
  Color puckColor = new Color(200, 0, 10);
  Box puck = new Box(puckColor);
  ColumnOfBoxes[] targets = new ColumnOfBoxes[8];

  private final static class TargetListener extends MouseAdapter
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
  }

  public Click1()
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
      int nBoxes = 1 + (int)(Math.random() * 3.0);
      float boxHue = (float)i / (float)targets.length;
      Color boxColor = Color.getHSBColor(boxHue, 0.5f, 0.85f);
      MouseListener targetListener = new TargetListener(boxColor.brighter());
      targets[i] = new ColumnOfBoxes(boxColor, nBoxes);
      targets[i].addMouseListener(targetListener);
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
    f.add(new Click1());
    f.setSize(600, 400);
    f.show();
  }
}
