/*
 * @(#)CheckboxPanel.java	1.11 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * Checkboxes!
 *
 * @version 1.8 08/26/98
 * @author Jeff Dinkins
 */
public class CheckboxPanel extends JPanel 
{
    // The Frame
    SwingSet swing;

    ImageIcon bulb1 = SwingSet.sharedInstance().loadImageIcon("images/WebSpice/bulb1.gif","dim light bulb");
    ImageIcon bulb2 = SwingSet.sharedInstance().loadImageIcon("images/WebSpice/bulb2.gif","lit light bulb");
    ImageIcon bulb3 = SwingSet.sharedInstance().loadImageIcon("images/WebSpice/bulb3.gif","greyed out light bulb");

    public CheckboxPanel(SwingSet swing) {
	this.swing = swing;

	setBorder(swing.emptyBorder5);
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	// *************** Checkboxes ****************
	// text buttons
	JPanel textButtons = SwingSet.createHorizontalPanel(false);
	textButtons.setAlignmentX(LEFT_ALIGNMENT);
	Border buttonBorder = new TitledBorder(null, "Text Checkboxes", 
					       TitledBorder.LEFT, TitledBorder.TOP,
					       swing.boldFont);

	Border emptyBorder = new EmptyBorder(5,5,5,5);
	Border compoundBorder = new CompoundBorder( buttonBorder, emptyBorder);
	textButtons.setBorder(compoundBorder);

	JCheckBox button;
	button = new JCheckBox("One", true);
	button.setToolTipText("This is a Checkbox with Text");
        button.setMnemonic('o');
	swing.checkboxes.addElement(button);
	textButtons.add(button);
	textButtons.add(Box.createRigidArea(swing.hpad10));
	
	button = new JCheckBox("Two");
	button.setToolTipText("This is a Checkbox with Text");
        button.setMnemonic('t');
	swing.checkboxes.addElement(button);
	textButtons.add(button);
	textButtons.add(Box.createRigidArea(swing.hpad10));

	button = new JCheckBox("Three");
	button.setToolTipText("This is a Checkbox with Text");
        button.setMnemonic('h');
	swing.checkboxes.addElement(button);
	textButtons.add(button);

	
	// image buttons
	JPanel imageButtons = SwingSet.createHorizontalPanel(false);
	imageButtons.setAlignmentX(LEFT_ALIGNMENT);
	buttonBorder = new TitledBorder(null, "Image Checkbox", 
					       TitledBorder.LEFT, TitledBorder.TOP,
					       swing.boldFont);
	compoundBorder = new CompoundBorder(buttonBorder, emptyBorder);
	imageButtons.setBorder(compoundBorder);

	button = new JCheckBox("One", bulb1);
	button.setSelectedIcon(bulb2);
	button.setDisabledIcon(bulb3);
	button.getAccessibleContext().setAccessibleDescription("Image of a lightbulb");
	swing.checkboxes.addElement(button);
	imageButtons.add(button);
	imageButtons.add(Box.createRigidArea(swing.hpad10));

	button = new JCheckBox("Two", bulb1);
	button.setSelectedIcon(bulb2);
	button.setDisabledIcon(bulb3);
	button.getAccessibleContext().setAccessibleDescription("Image of a lightbulb");
	swing.checkboxes.addElement(button);
	imageButtons.add(button);
	imageButtons.add(Box.createRigidArea(swing.hpad10));

	button = new JCheckBox("Three", bulb1);
	button.setSelectedIcon(bulb2);
	button.setDisabledIcon(bulb3);
	button.getAccessibleContext().setAccessibleDescription("Image of a lightbulb");
	swing.checkboxes.addElement(button);
	imageButtons.add(button);

	// Add button panels to buttonPanel
	JPanel buttonPanel = swing.createVerticalPanel(true);
	buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
	buttonPanel.setAlignmentY(TOP_ALIGNMENT);


	buttonPanel.add(textButtons);
	buttonPanel.add(Box.createRigidArea(swing.vpad20));
	buttonPanel.add(imageButtons);

	buttonPanel.add(Box.createGlue());


	// *************** Create the button controls ****************
	JPanel controls = new JPanel() {
	    public Dimension getMaximumSize() {
		return new Dimension(300, super.getMaximumSize().height);
	    }
	};
	controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
	controls.setAlignmentY(TOP_ALIGNMENT);
	controls.setAlignmentX(LEFT_ALIGNMENT);

	JPanel buttonControls = swing.createHorizontalPanel(true);
	buttonControls.setAlignmentY(TOP_ALIGNMENT);
	buttonControls.setAlignmentX(LEFT_ALIGNMENT);

	JPanel leftColumn = swing.createVerticalPanel(false);
	leftColumn.setAlignmentX(LEFT_ALIGNMENT);
	leftColumn.setAlignmentY(TOP_ALIGNMENT);

	JPanel rightColumn = new LayoutControlPanel(swing, swing.checkboxes);

	buttonControls.add(leftColumn);
	buttonControls.add(Box.createRigidArea(swing.hpad20));
	buttonControls.add(rightColumn);
	buttonControls.add(Box.createRigidArea(swing.hpad20));

	controls.add(buttonControls);

	// Display Options
	JLabel l = new JLabel("Display Options:");
	leftColumn.add(l);
	l.setFont(swing.boldFont);

 	JCheckBox bordered = new JCheckBox("Paint Border");
	bordered.setToolTipText("Click here to turn border painting on or off.");
        bordered.setMnemonic('b');
 	bordered.addItemListener(swing.buttonDisplayListener);
 	leftColumn.add(bordered);

 	JCheckBox focused = new JCheckBox("Paint Focus");
	focused.setToolTipText("Click here to turn focus painting on or off.");
        focused.setMnemonic('f');
 	focused.setSelected(true);
 	focused.addItemListener(swing.buttonDisplayListener);
 	leftColumn.add(focused);

	JCheckBox enabled = new JCheckBox("Enabled");
	enabled.setToolTipText("Click here to enable or disable the checkboxes.");
	enabled.setSelected(true);
        enabled.setMnemonic('e');
	enabled.addItemListener(swing.buttonDisplayListener);
	leftColumn.add(enabled);
 
	leftColumn.add(Box.createRigidArea(swing.vpad20));

	
	l = new JLabel("Pad Amount:");
	leftColumn.add(l);
	l.setFont(swing.boldFont);
	
	ButtonGroup group = new ButtonGroup();
	JRadioButton defaultPad = new JRadioButton("Default");
        defaultPad.setMnemonic('d');
	defaultPad.setToolTipText("Uses the default padding between the border and label.");
	group.add(defaultPad);
	defaultPad.setSelected(true);
 	defaultPad.addItemListener(swing.buttonPadListener);
	leftColumn.add(defaultPad);

	JRadioButton zeroPad = new JRadioButton("0");
	zeroPad.setToolTipText("Uses no padding between the border and label.");
        zeroPad.setMnemonic('0');
	group.add(zeroPad);
 	zeroPad.addItemListener(swing.buttonPadListener);
	leftColumn.add(zeroPad);

	JRadioButton tenPad = new JRadioButton("10");
	tenPad.setToolTipText("Uses a 10 pixel pad between the border and label.");
        tenPad.setMnemonic('1');
	group.add(tenPad);
 	tenPad.addItemListener(swing.buttonPadListener);
	leftColumn.add(tenPad);

	leftColumn.add(Box.createRigidArea(swing.vpad20));

	
	add(buttonPanel);
	add(Box.createRigidArea(swing.hpad10));
 	add(controls);
    }

    
}
