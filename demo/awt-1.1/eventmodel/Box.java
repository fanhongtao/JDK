
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

