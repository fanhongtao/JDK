/*
 * @(#)BorderedPanePanel.java	1.7 99/04/23
 *
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * Borders, borders, everywhere....
 *
 * @version 1.7 04/23/99
 * @author Jeff Dinkins
 */
public class BorderedPanePanel extends JPanel
{
    SwingSet swing;

    JPanel borderedPane;

    public BorderedPanePanel(SwingSet swing) {
	this.swing = swing;

	// setBorderStyle(LOWERED);
	setBorder(swing.emptyBorder10);
	setLayout(new BorderLayout());

	borderedPane = new JPanel();
	borderedPane.setLayout(new BorderLayout());
	borderedPane.setBorder(BorderFactory.createTitledBorder("Bordered Pane"));


	// Create title position controls
	JPanel controls = new JPanel();
	controls.setBorder(swing.emptyBorder20);
	controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

	controls.add(Box.createRigidArea(swing.vpad10));

	JRadioButton b;
	ButtonGroup group = new ButtonGroup();
	JLabel label = new JLabel("Title Position:");
	label.setFont(swing.boldFont);
	controls.add(label);

	b = (JRadioButton) controls.add(new JRadioButton("Above Top"));
	b.addActionListener(borderedPaneListener);
	group.add(b);

	b = (JRadioButton) controls.add(new JRadioButton("Top"));
	b.setSelected(true);
	b.addActionListener(borderedPaneListener);
	group.add(b);

	b = (JRadioButton) controls.add(new JRadioButton("Below Top"));
	b.addActionListener(borderedPaneListener);
	group.add(b);

	b = (JRadioButton) controls.add(new JRadioButton("Above Bottom"));
	b.addActionListener(borderedPaneListener);
	group.add(b);

	b = (JRadioButton) controls.add(new JRadioButton("Bottom"));
	b.addActionListener(borderedPaneListener);
	group.add(b);

 	b = (JRadioButton) controls.add(new JRadioButton("Below Bottom"));
	b.addActionListener(borderedPaneListener);
	group.add(b);

	controls.add(Box.createRigidArea(swing.vpad10));
	label = new JLabel("Title Justification:");
	label.setFont(swing.boldFont);
	controls.add(label);

	group = new ButtonGroup();
 	b = (JRadioButton) controls.add(new JRadioButton("Left"));
	b.addActionListener(borderedPaneListener);
	b.setSelected(true);
	group.add(b);

 	b = (JRadioButton) controls.add(new JRadioButton("Center"));
	b.addActionListener(borderedPaneListener);
	group.add(b);

 	b = (JRadioButton) controls.add(new JRadioButton("Right"));
	b.addActionListener(borderedPaneListener);
	group.add(b);

	// Add panels 
	add("Center", borderedPane);
	borderedPane.add("Center", controls);
	// add("East", controls);
    }

    // Title Pane tile position
    ActionListener borderedPaneListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JRadioButton b = (JRadioButton) e.getSource();
	    TitledBorder border = (TitledBorder) borderedPane.getBorder();

            if(b.getText().equals("Above Top")) {
		border.setTitlePosition(TitledBorder.ABOVE_TOP);
	    } else if(b.getText().equals("Top")) {
		border.setTitlePosition(TitledBorder.TOP);
	    } else if(b.getText().equals("Below Top")) {
		border.setTitlePosition(TitledBorder.BELOW_TOP);
	    } else if(b.getText().equals("Above Bottom")) {
		border.setTitlePosition(TitledBorder.ABOVE_BOTTOM);
	    } else if(b.getText().equals("Bottom")) {
		border.setTitlePosition(TitledBorder.BOTTOM);
	    } else if(b.getText().equals("Below Bottom")) {
		border.setTitlePosition(TitledBorder.BELOW_BOTTOM);
	    } else if(b.getText().equals("Left")) {
		border.setTitleJustification(TitledBorder.LEFT);
	    } else if(b.getText().equals("Center")) {
		border.setTitleJustification(TitledBorder.CENTER);
	    } else if(b.getText().equals("Right")) {
		border.setTitleJustification(TitledBorder.RIGHT);
	    }
	    
            borderedPane.invalidate();
            borderedPane.validate();
            borderedPane.repaint();
        }
    };

}
