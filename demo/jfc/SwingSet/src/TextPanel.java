/*
 * @(#)TextPanel.java	1.10 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;


/**
 * Text!
 *
 * @version 1.10 11/29/01
 * @author Jeff Dinkins
 * @author Peter Korn (accessibility support)
 */
public class TextPanel extends JPanel 
{
    // The Frame
    SwingSet swing;

    public TextPanel(SwingSet swing) {
	super(true);
	this.swing = swing;
	setBorder(new CompoundBorder(swing.loweredBorder, swing.emptyBorder10));

	JPanel textFields = SwingSet.createVerticalPanel(false);

	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	JTextField field1 = new FixedTextField("George Washington", 15);
	field1.getAccessibleContext().setAccessibleName("First text field");

	JTextField field2 = new FixedTextField("Thomas Jefferson", 15);
	field2.setForeground(Color.red);
	field2.getAccessibleContext().setAccessibleName("Second text field");

	JTextField field3 = new FixedTextField("Benjamin Franklin", 15);
	field3.setBackground(new Color(200, 200, 255)); // cornflower blue
	field3.getAccessibleContext().setAccessibleName("Third text field");

	// JTextField field4 = new FixedTextField("Thomas Payne", 15); 
        // Thanks to Chris Paine for pointing out that I misspelled "Paine"). (-:
	JTextField field4 = new FixedTextField("Thomas Paine", 15); 
	field4.setForeground(Color.yellow);
	field4.setBackground(new Color(200, 140, 80)); // pumpkin
	field4.getAccessibleContext().setAccessibleName("Fourth text field");

	JTextField field5 = new FixedTextField("Abraham Lincoln", 15);
	field5.setForeground(Color.green.brighter());
	field5.setBackground(Color.black);
	field5.getAccessibleContext().setAccessibleName("Fifth text field");

	JLabel label = (JLabel) textFields.add(new JLabel("Text Fields:"));
	label.setFont(swing.boldFont);
	label.setLabelFor(field1);
	textFields.add(Box.createRigidArea(swing.vpad10));
	textFields.add(field1);
	textFields.add(Box.createRigidArea(swing.vpad5));
	textFields.add(field2);
	textFields.add(Box.createRigidArea(swing.vpad5));
	textFields.add(field3);
	textFields.add(Box.createRigidArea(swing.vpad5));
	textFields.add(field4);
	textFields.add(Box.createRigidArea(swing.vpad5));
	textFields.add(field5);
	textFields.add(Box.createHorizontalStrut(5));

	String text = LoadFile("Constitution.txt");

	JPanel textAreaPanel = SwingSet.createVerticalPanel(false);
	label = (JLabel) textAreaPanel.add(new JLabel("Text Area:"));
	label.setFont(swing.boldFont);
	textAreaPanel.add(Box.createRigidArea(swing.vpad10));

	JPanel textWrapper = new JPanel(new BorderLayout());
	textWrapper.setAlignmentX(LEFT_ALIGNMENT);
 	textWrapper.setBorder(swing.loweredBorder);

	textAreaPanel.add(textWrapper);

	JTextArea textArea = new JTextArea(text);
	JScrollPane scroller = new JScrollPane() {
            public Dimension getPreferredSize() {
		return new Dimension(300,100);
	    }
	    public float getAlignmentX() {
		return LEFT_ALIGNMENT;
	    }
	};
	scroller.getViewport().add(textArea);
	textArea.setFont(new Font("Dialog", Font.PLAIN, 12));
	textArea.getAccessibleContext().setAccessibleName("Editable text area");
	label.setLabelFor(textArea);
	textWrapper.add(scroller, BorderLayout.CENTER);

	add(Box.createRigidArea(swing.hpad10));
	add(textFields);
	add(Box.createRigidArea(swing.hpad10));
	add(textAreaPanel);
    }


    class FixedTextField extends JTextField {
	public FixedTextField(String text, int columns) {
	    super(text, columns);
	}
	public Dimension getMaximumSize() {
	    return getPreferredSize();
	}
	public float getAlignmentX() {
	    return LEFT_ALIGNMENT;
	}
    }

    public String LoadFile(String filename) {
      return SwingSet.contentsOfFile(filename);
    }
    
    
}
