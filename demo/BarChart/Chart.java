/*
 * @(#)Chart.java	1.4 97/07/30
 *
 * Copyright (c) 2070, 1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.*;
import java.lang.*;
import java.net.URL;

public class Chart extends java.applet.Applet {
    static final int	VERTICAL = 0;
    static final int 	HORIZONTAL = 1;

    static final int	SOLID = 0;
    static final int	STRIPED = 1;

    int			orientation;
    String		title;
    Font		titleFont;
    FontMetrics		titleFontMetrics;
    int			titleHeight = 15;
    int			columns;
    int			values[];
    Object		colors[];
    Object		labels[];
    int			styles[];
    int			scale = 10;
    int			maxLabelWidth = 0;
    int			barWidth;
    int			barSpacing = 10;
    int			max = 0;

    public synchronized void init() {
	String rs;
	
	titleFont = new java.awt.Font("Courier", Font.BOLD, 12);
	titleFontMetrics = getFontMetrics(titleFont);
	title = getParameter("title");

	if (title == null) {
	    title = "Chart";
	}
	rs = getParameter("columns");
	if (rs == null) {
	    columns = 5;
	} else {
	    columns = Integer.parseInt(rs);
	}
	rs = getParameter("scale");
	if (rs == null) {
	    scale = 10;
	} else {
	    scale = Integer.parseInt(rs);
	}

	rs = getParameter("orientation");
	if (rs == null) {
	    orientation = VERTICAL;
	} else if (rs.toLowerCase().equals("vertical")) {
	    orientation = VERTICAL;
	} else if (rs.toLowerCase().equals("horizontal")) {
	    orientation = HORIZONTAL;
	} else {
	    orientation = VERTICAL;
	}
	values = new int[columns];
	colors = new Color[columns];
	labels = new String[columns];
	styles = new int[columns];
	for (int i=0; i < columns; i++) {
	    // parse the value for this column
	    rs = getParameter("C" + (i+1));
	    if (rs != null) {
		try {
		    values[i] = Integer.parseInt(rs);
		} catch (NumberFormatException e) {
		    values[i] = 0;
		}
	    }
	    if (values[i] > max) {
		max = values[i];
	    }

	    // parse the label for this column
	    rs = getParameter("C" + (i+1) + "_label");
	    labels[i] = (rs == null) ? "" : rs;
	    maxLabelWidth = Math.max(titleFontMetrics.stringWidth((String)(labels[i])),
				     maxLabelWidth);

	    // parse the bar style
	    rs = getParameter("C" + (i+1) + "_style");
	    if (rs == null || rs.toLowerCase().equals("solid")) {
		styles[i] = SOLID;
	    } else if (rs.toLowerCase().equals("striped")) {
		styles[i] = STRIPED;
	    } else {
		styles[i] = SOLID;
	    }
	    // parse the color attribute for this column
	    rs = getParameter("C" + (i+1) + "_color");
	    if (rs != null) {
		if (rs.equals("red")) {
		    colors[i] = Color.red;
		} else if (rs.equals("green")) {
		    colors[i] = Color.green;
		} else if (rs.equals("blue")) {
		    colors[i] = Color.blue;
		} else if (rs.equals("pink")) {
		    colors[i] = Color.pink;
		} else if (rs.equals("orange")) {
		    colors[i] = Color.orange;
		} else if (rs.equals("magenta")) {
		    colors[i] = Color.magenta;
		} else if (rs.equals("cyan")) {
		    colors[i] = Color.cyan;
		} else if (rs.equals("white")) {
		    colors[i] = Color.white;
		} else if (rs.equals("yellow")) {
		    colors[i] = Color.yellow;
		} else if (rs.equals("gray")) {
		    colors[i] = Color.gray;
		} else if (rs.equals("darkGray")) {
		    colors[i] = Color.darkGray;
		} else {
		    colors[i] = Color.gray;
		}
	    } else {
		colors[i] = Color.gray;
	    }
	}
	switch (orientation) {
	  case VERTICAL:
	  default:
	    barWidth = maxLabelWidth;
	    resize(Math.max(columns * (barWidth + barSpacing),
			    titleFontMetrics.stringWidth(title)) +
		   titleFont.getSize() + 5,
		   (max * scale) + (2 * titleFont.getSize()) + 5 + titleFont.getSize());
	    break;
	  case HORIZONTAL:
	    barWidth = titleFont.getSize();
	    resize(Math.max((max * scale) + titleFontMetrics.stringWidth("" + max),
			    titleFontMetrics.stringWidth(title)) + maxLabelWidth + 5,
		   (columns * (barWidth + barSpacing)) + titleFont.getSize() + 10);
	    break;
	}
    }

    public synchronized void paint(Graphics g) {
	int i, j;
	int cx, cy;
	char l[] = new char[1];


	// draw the title centered at the bottom of the bar graph
	g.setColor(Color.black);
	i = titleFontMetrics.stringWidth(title);
	g.setFont(titleFont);
	g.drawString(title, Math.max((getSize().width - i)/2, 0),
		     getSize().height - titleFontMetrics.getDescent()); 
	for (i=0; i < columns; i++) {
	    switch (orientation) {
	      case VERTICAL:
	      default:
		// set the next X coordinate to account for the label
		// being wider than the bar getSize().width.
		cx = (Math.max((barWidth + barSpacing),maxLabelWidth) * i) +
		    barSpacing;

		// center the bar chart
		cx += Math.max((getSize().width - (columns *
					 (barWidth + (2 * barSpacing))))/2,0);
		
		// set the next Y coordinate to account for the getSize().height
		// of the bar as well as the title and labels painted
		// at the bottom of the chart.
		cy = getSize().height - (values[i] * scale) - 1 - (2 * titleFont.getSize());

		// draw the label
		g.setColor(Color.black);		
		g.drawString((String)labels[i], cx,
			     getSize().height - titleFont.getSize() - titleFontMetrics.getDescent());

		// draw the shadow bar
		if (colors[i] == Color.black) {
		    g.setColor(Color.gray);
		}
		g.fillRect(cx + 5, cy - 3, barWidth,  (values[i] * scale));
		// draw the bar with the specified color
		g.setColor((Color)(colors[i]));
		switch (styles[i]) {
		  case SOLID:
		  default:
		    g.fillRect(cx, cy, barWidth, (values[i] * scale));
		    break;
		  case STRIPED:
		    {
			int steps = (values[i] * scale) / 2;
			int ys;

			for (j=0; j < steps; j++) {
			    ys = cy + (2 * j);
			    g.drawLine(cx, ys, cx + barWidth, ys);
			}
		    }
		    break;
		}
		g.drawString("" + values[i],
			     cx,
			     cy - titleFontMetrics.getDescent());
		break;
	      case HORIZONTAL:
		// set the Y coordinate
		cy = ((barWidth + barSpacing) * i) + barSpacing;
			       
		// set the X coordinate to be the getSize().width of the widest
		// label 
		cx = maxLabelWidth + 1;

		cx += Math.max((getSize().width - (maxLabelWidth + 1 +
					 titleFontMetrics.stringWidth("" +
							       max) +
					 (max * scale))) / 2, 0);
		// draw the labels and the shadow
		g.setColor(Color.black);		
		g.drawString((String)labels[i], cx - maxLabelWidth - 1,
			     cy + titleFontMetrics.getAscent());
		if (colors[i] == Color.black) {
		    g.setColor(Color.gray);
		}
		g.fillRect(cx + 3,
			   cy + 5,
			   (values[i] * scale),
			   barWidth);

		// draw the bar in the current color
		g.setColor((Color)(colors[i]));
		switch (styles[i]) {
		  case SOLID:
		  default:
		    g.fillRect(cx,
			       cy,
			       (values[i] * scale),
			       barWidth);
		    break;
		  case STRIPED:
		    {
			int steps = (values[i] * scale) / 2;
			int ys;

			for (j=0; j < steps; j++) {
			    ys = cx + (2 * j);
			    g.drawLine(ys, cy, ys, cy + barWidth);
			}
		    }
		    break;
		}
		g.drawString("" + values[i],
			     cx + (values[i] * scale) + 3,
			     cy + titleFontMetrics.getAscent());
			     
		break;
	    }
	}
    }
    public String getAppletInfo() {
    return "Title: Bar Chart \nAuthor: Sami Shaio \nA simple bar chart demo.";
  }
  
  public String[][] getParameterInfo() {
    String[][] info = {
      {"title", "string", "The title of bar graph.  Default is 'Chart'"},
      {"scale", "int", "The scale of the bar graph.  Default is 10."},
      {"columns", "int", "The number of columns/rows.  Default is 5."},
      {"orientation", "{VERTICLE, HORIZONTAL}", "The orienation of the bar graph.  Default is VERTICLE."},
      {"c#", "int", "Subsitute a number for #.  The value/size of bar #.  Default is 0."},
      {"c#_label", "string", "The label for bar #.  Default is no label."},
      {"c#_style", "{SOLID, STRIPED}", "The style of bar #.  Default is SOLID."},
      {"c#_color", "{RED, GREEN, BLUE, PINK, ORANGE, MAGENTA, CYAN, WHITE, YELLOW, GRAY, DARKGRAY}", "The color of bar #.  Default is GRAY."}
    };
    return info;
  }
}
