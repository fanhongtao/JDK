/*
 * @(#)BorderedPanePanel.java	1.7 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.7 11/29/01
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
