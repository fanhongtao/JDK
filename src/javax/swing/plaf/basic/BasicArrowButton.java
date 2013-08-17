/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

import javax.swing.*;

/**
 * JButton object that draws a scaled Arrow in one of the cardinal directions.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.20 02/06/02
 * @author David Kloba
 */
public class BasicArrowButton extends JButton implements SwingConstants
{
        protected int direction;

        public BasicArrowButton(int direction)            {
	    super();
	    setRequestFocusEnabled(false);
            setDirection(direction);
            setBackground(UIManager.getColor("control"));
        }

        public int getDirection() { return direction; }

        public void setDirection(int dir) { direction = dir; }

	public void paint(Graphics g) {
	    Color origColor;
	    boolean isPressed, isEnabled;
	    int w, h, size;

            w = getSize().width;
            h = getSize().height;
	    origColor = g.getColor();
	    isPressed = getModel().isPressed();
	    isEnabled = isEnabled();

            g.setColor(getBackground());
            g.fillRect(1, 1, w-2, h-2);

            /// Draw the proper Border
            if (isPressed) {
                g.setColor(UIManager.getColor("controlShadow"));
                g.drawRect(0, 0, w-1, h-1);
            } else {
                // Using the background color set above
                g.drawLine(0, 0, 0, h-1);
                g.drawLine(1, 0, w-2, 0);

                g.setColor(UIManager.getColor("controlLtHighlight"));    // inner 3D border
                g.drawLine(1, 1, 1, h-3);
                g.drawLine(2, 1, w-3, 1);

                g.setColor(UIManager.getColor("controlShadow"));       // inner 3D border
                g.drawLine(1, h-2, w-2, h-2);
                g.drawLine(w-2, 1, w-2, h-3);

                g.setColor(UIManager.getColor("controlDkShadow"));     // black drop shadow  __|
                g.drawLine(0, h-1, w-1, h-1);
                g.drawLine(w-1, h-1, w-1, 0);
            }

            // If there's no room to draw arrow, bail
            if(h < 5 || w < 5)      {
                g.setColor(origColor);
                return;
            }

            if (isPressed) {
                g.translate(1, 1);
            }

            // Draw the arrow
            size = Math.min((h - 4) / 3, (w - 4) / 3);
            size = Math.max(size, 2);
	    paintTriangle(g, (w - size) / 2, (h - size) / 2,
				size, direction, isEnabled);

            // Reset the Graphics back to it's original settings
            if (isPressed) {
                g.translate(-1, -1);
	    }
	    g.setColor(origColor);

        }

        public Dimension getPreferredSize() {
            return new Dimension(16, 16);
        }

        public Dimension getMinimumSize() {
            return new Dimension(5, 5);
        }

        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    
    	public boolean isFocusTraversable() {
	  return false;
	}

	public void paintTriangle(Graphics g, int x, int y, int size, 
					int direction, boolean isEnabled) {
	    Color oldColor = g.getColor();
	    int mid, i, j;

	    j = 0;
            size = Math.max(size, 2);
	    mid = size / 2;
	
	    g.translate(x, y);
	    if(isEnabled)
		g.setColor(UIManager.getColor("controlDkShadow"));
	    else
		g.setColor(UIManager.getColor("controlShadow"));

            switch(direction)       {
            case NORTH:
                for(i = 0; i < size; i++)      {
                    g.drawLine(mid-i, i, mid+i, i);
                }
                if(!isEnabled)  {
                    g.setColor(UIManager.getColor("controlLtHighlight"));
                    g.drawLine(mid-i+2, i, mid+i, i);
                }
                break;
            case SOUTH:
                if(!isEnabled)  {
                    g.translate(1, 1);
                    g.setColor(UIManager.getColor("controlLtHighlight"));
                    for(i = size-1; i >= 0; i--)   {
                        g.drawLine(mid-i, j, mid+i, j);
                        j++;
                    }
		    g.translate(-1, -1);
		    g.setColor(UIManager.getColor("controlShadow"));
		}
		
		j = 0;
                for(i = size-1; i >= 0; i--)   {
                    g.drawLine(mid-i, j, mid+i, j);
                    j++;
                }
                break;
            case WEST:
                for(i = 0; i < size; i++)      {
                    g.drawLine(i, mid-i, i, mid+i);
                }
                if(!isEnabled)  {
                    g.setColor(UIManager.getColor("controlLtHighlight"));
                    g.drawLine(i, mid-i+2, i, mid+i);
                }
                break;
            case EAST:
                if(!isEnabled)  {
                    g.translate(1, 1);
                    g.setColor(UIManager.getColor("controlLtHighlight"));
                    for(i = size-1; i >= 0; i--)   {
                        g.drawLine(j, mid-i, j, mid+i);
                        j++;
                    }
		    g.translate(-1, -1);
		    g.setColor(UIManager.getColor("controlShadow"));
                }

		j = 0;
                for(i = size-1; i >= 0; i--)   {
                    g.drawLine(j, mid-i, j, mid+i);
                    j++;
                }
		break;
            }
	    g.translate(-x, -y);	
	    g.setColor(oldColor);
	}
	
}

