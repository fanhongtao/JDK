
import java.awt.*;
import java.awt.event.*;


public class ColumnOfBoxes extends Panel
{
  public Insets getInsets()
  {
    return new Insets(100, 0, 100, 0);
  }

  ColumnOfBoxes(Color c, int n)
  {
    setLayout(new GridLayout(0, 1, 2, 2));
    for(int i = 0; i < n; i++) {
      add(new Box(c));
    }
  }
}


