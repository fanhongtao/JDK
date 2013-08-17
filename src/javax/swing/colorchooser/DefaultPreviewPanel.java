/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.colorchooser;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;


/**
 * The standard preview panel for the color chooser.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.8 02/06/02
 * @author Steve Wilson
 * @see JColorChooser
 */
class DefaultPreviewPanel extends JPanel {

    private int squareSize = 25;
    private int squareGap = 5;
    private int innerGap = 5;
  

    private int textGap = 5;
    private Font font = new Font("Dialog", Font.PLAIN, 12);
    private String sampleText = UIManager.getString("ColorChooser.sampleText");

    private int swatchWidth = 50;

    private Color oldColor = null;


    public Dimension getPreferredSize() {

	FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(getFont());

	int ascent = fm.getAscent();
	int height = fm.getHeight();
	int width = fm.stringWidth(sampleText);

        int y = height*3 + textGap*3;
	int x = squareSize * 3 + squareGap*4 + swatchWidth + width;
        return new Dimension( x,y );
    }

    public void paintComponent(Graphics g) {
        if (oldColor == null)
	    oldColor = getForeground();

        g.setColor(getBackground());
	g.fillRect(0,0,getWidth(),getHeight());

        int squareWidth = paintSquares(g);

	int textWidth = paintText(g, squareWidth);

        paintSwatch(g, squareWidth + textWidth);


    }

    private void paintSwatch(Graphics g, int offsetX) {
        int swatchX = offsetX + squareGap;
	g.setColor(oldColor);
	g.fillRect(swatchX, 0, swatchWidth, (squareSize) + (squareGap/2));
	g.setColor(getForeground());
	g.fillRect(swatchX, (squareSize) + (squareGap/2), swatchWidth, (squareSize) + (squareGap/2) );
    }

    private int paintText(Graphics g, int offsetX) {
	g.setFont(getFont());
	FontMetrics fm = g.getFontMetrics();

	int ascent = fm.getAscent();
	int height = fm.getHeight();
	int width = fm.stringWidth(sampleText);

	int textXOffset = offsetX + textGap;

        Color color = getForeground();

	g.setColor(color);

	g.drawString(sampleText, textXOffset, ascent+2);

	g.fillRect(textXOffset,
		   ( height) + textGap, 
		   width + (textGap),
		   height +2);

	g.setColor(Color.black);
	g.drawString(sampleText, 
		     textXOffset+(textGap/2), 
		     height+ascent+textGap+2);


	g.setColor(Color.white);

	g.fillRect(textXOffset,
		   ( height + textGap) * 2, 
		   width + (textGap),
		   height +2);

	g.setColor(color);
	g.drawString(sampleText,
		     textXOffset+(textGap/2), 
		     ((height+textGap) * 2)+ascent+2);

	return width + textGap + 4;
	
    }

    private int paintSquares(Graphics g) {

        Color color = getForeground();

        g.setColor(Color.white);
	g.fillRect(0,0,squareSize,squareSize);
	g.setColor(color);
	g.fillRect(innerGap, 
		   innerGap, 
		   squareSize - (innerGap*2), 
		   squareSize - (innerGap*2));
	g.setColor(Color.white);
	g.fillRect(innerGap*2,
		   innerGap*2,
		   squareSize - (innerGap*4),
		   squareSize - (innerGap*4));

        g.setColor(color);
	g.fillRect(0,squareSize+squareGap,squareSize,squareSize);

	g.translate(squareSize+squareGap, 0);
        g.setColor(Color.black);
	g.fillRect(0,0,squareSize,squareSize);
	g.setColor(color);
	g.fillRect(innerGap, 
		   innerGap, 
		   squareSize - (innerGap*2), 
		   squareSize - (innerGap*2));
	g.setColor(Color.white);
	g.fillRect(innerGap*2,
		   innerGap*2,
		   squareSize - (innerGap*4),
		   squareSize - (innerGap*4));
	g.translate(-(squareSize+squareGap), 0);

	g.translate(squareSize+squareGap, squareSize+squareGap);
        g.setColor(Color.white);
	g.fillRect(0,0,squareSize,squareSize);
	g.setColor(color);
	g.fillRect(innerGap, 
		   innerGap, 
		   squareSize - (innerGap*2), 
		   squareSize - (innerGap*2));
	g.translate(-(squareSize+squareGap), -(squareSize+squareGap));
	


	g.translate((squareSize+squareGap)*2, 0);
        g.setColor(Color.white);
	g.fillRect(0,0,squareSize,squareSize);
	g.setColor(color);
	g.fillRect(innerGap, 
		   innerGap, 
		   squareSize - (innerGap*2), 
		   squareSize - (innerGap*2));
	g.setColor(Color.black);
	g.fillRect(innerGap*2,
		   innerGap*2,
		   squareSize - (innerGap*4),
		   squareSize - (innerGap*4));
	g.translate(-((squareSize+squareGap)*2), 0);

	g.translate((squareSize+squareGap)*2, (squareSize+squareGap));
        g.setColor(Color.black);
	g.fillRect(0,0,squareSize,squareSize);
	g.setColor(color);
	g.fillRect(innerGap, 
		   innerGap, 
		   squareSize - (innerGap*2), 
		   squareSize - (innerGap*2));
	g.translate(-((squareSize+squareGap)*2), -(squareSize+squareGap));

	return ((squareSize+squareGap) *3);

    }

}
