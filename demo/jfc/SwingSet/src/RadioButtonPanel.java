/*
 * @(#)RadioButtonPanel.java	1.12 01/11/29
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
 * RadioButtons!
 *
 * @version 1.9 08/26/98
 * @author Jeff Dinkins
 */
public class RadioButtonPanel extends JPanel 
{
    // The Frame
    SwingSet swing;

    ImageIcon radio = SwingSet.sharedInstance().loadImageIcon("images/WebSpice/radio.gif","Grey circle with blue triangle inside");
    ImageIcon radioSelected = SwingSet.sharedInstance().loadImageIcon("images/WebSpice/radioSelected.gif","Grey circle with green triangle inside");
    ImageIcon radioPressed = SwingSet.sharedInstance().loadImageIcon("images/WebSpice/radioPressed.gif","Grey circle with purple triangle inside");

    public RadioButtonPanel(SwingSet swing) {
	this.swing = swing;

	ButtonGroup group;

	setBorder(swing.emptyBorder5);
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	// *************** radio buttons ****************
	// text buttons
	JPanel textButtons = SwingSet.createHorizontalPanel(false);
	textButtons.setAlignmentX(LEFT_ALIGNMENT);
	Border buttonBorder = new TitledBorder(null, "Text RadioButtons", 
					       TitledBorder.LEFT, TitledBorder.TOP,
					       swing.boldFont);

	Border emptyBorder = new EmptyBorder(5,5,5,5);
	Border compoundBorder = new CompoundBorder( buttonBorder, emptyBorder);
	textButtons.setBorder(compoundBorder);

	group = new ButtonGroup();
	JRadioButton button;
	button = new JRadioButton("One", true);
	button.setToolTipText("This is a RadioButton with Text");
	group.add(button);
        button.setMnemonic('o');
	swing.radioButtons.addElement(button);
	textButtons.add(button);
	textButtons.add(Box.createRigidArea(swing.hpad10));
	
	button = new JRadioButton("Two");
	group.add(button);
        button.setMnemonic('t');
	button.setToolTipText("This is a RadioButton with Text");
	swing.radioButtons.addElement(button);
	textButtons.add(button);
	textButtons.add(Box.createRigidArea(swing.hpad10));

	button = new JRadioButton("Three");
	group.add(button);
        button.setMnemonic('h');
	button.setToolTipText("This is a RadioButton with Text");
	swing.radioButtons.addElement(button);
	textButtons.add(button);


	// image buttons
	group = new ButtonGroup();

	JPanel imageButtons = SwingSet.createHorizontalPanel(false);
	imageButtons.setAlignmentX(LEFT_ALIGNMENT);
	buttonBorder = new TitledBorder(null, "Image RadioButtons", 
					       TitledBorder.LEFT, TitledBorder.TOP,
					       swing.boldFont);
	compoundBorder = new CompoundBorder(buttonBorder, emptyBorder);
	imageButtons.setBorder(compoundBorder);

	// 1 image
	button = new JRadioButton(swing.duke2);
	group.add(button);
	button.setSelectedIcon(swing.dukeWave);
	button.setPressedIcon(swing.dukeWaveRed);
	button.setRolloverIcon(swing.dukeWaveRed);
	button.setSelected(true);
	button.setToolTipText("This is a RadioButton with a Icon");
	button.getAccessibleContext().setAccessibleName("Duke as a radio button");
	swing.radioButtons.addElement(button);
	imageButtons.add(button);
	imageButtons.add(Box.createRigidArea(swing.hpad10));
	
	// 2 images
	button = new JRadioButton(swing.duke2);
	group.add(button);
	swing.radioButtons.addElement(button);
	button.setSelectedIcon(swing.dukeWave);
	button.setPressedIcon(swing.dukeWaveRed);
	button.setRolloverIcon(swing.dukeWaveRed);
	button.setToolTipText("This is a RadioButton with a Icon");
	button.getAccessibleContext().setAccessibleName("Duke as a radio button");
	imageButtons.add(button);
	imageButtons.add(Box.createRigidArea(swing.hpad10));

	// 3 images
	button = new JRadioButton(swing.duke2);
	group.add(button);
	button.setSelectedIcon(swing.dukeWave);
	button.setPressedIcon(swing.dukeWaveRed);
	button.setRolloverIcon(swing.dukeWaveRed);
	button.setToolTipText("This is a RadioButton with a Icon");
	button.getAccessibleContext().setAccessibleName("Duke as a radio button");
	swing.radioButtons.addElement(button);
	imageButtons.add(button);

	// text&image buttons
	group = new ButtonGroup();

	JPanel tiButtons = SwingSet.createHorizontalPanel(false);
	tiButtons.setAlignmentX(LEFT_ALIGNMENT);
	buttonBorder = new TitledBorder(null, "Image & Text RadioButtons", 
					       TitledBorder.LEFT, TitledBorder.TOP,
					       swing.boldFont);
	compoundBorder = new CompoundBorder(buttonBorder, emptyBorder);
	tiButtons.setBorder(compoundBorder);

	button = new JRadioButton("Left", radio);
	group.add(button);
	button.setToolTipText("This is a RadioButton with a Icon and Text");
	button.setSelected(true);
	button.setSelectedIcon(radioSelected);
	button.setPressedIcon(radioPressed);
	swing.radioButtons.addElement(button);
	tiButtons.add(button);
	tiButtons.add(Box.createRigidArea(swing.hpad10));

	button = new JRadioButton("Center", radio);
	group.add(button);
	swing.radioButtons.addElement(button);
	button.setToolTipText("This is a RadioButton with a Icon and Text");
	button.setSelectedIcon(radioSelected);
	button.setPressedIcon(radioPressed);
	tiButtons.add(button);
	tiButtons.add(Box.createRigidArea(swing.hpad10));

	button = new JRadioButton("Right", radio);
	group.add(button);
	swing.radioButtons.addElement(button);
	button.setToolTipText("This is a RadioButton with a Icon and Text");
	button.setSelectedIcon(radioSelected);
	button.setPressedIcon(radioPressed);
	tiButtons.add(button);
	tiButtons.add(Box.createHorizontalBox());

	// Add button panels to buttonPanel
	JPanel buttonPanel = SwingSet.createVerticalPanel(true);
	buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
	buttonPanel.setAlignmentY(TOP_ALIGNMENT);

	buttonPanel.add(textButtons);

	buttonPanel.add(Box.createVerticalStrut(10));


	buttonPanel.add(imageButtons);

	buttonPanel.add(Box.createVerticalStrut(10));

	buttonPanel.add(tiButtons);
	buttonPanel.add(tiButtons);
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

	JPanel buttonControls = SwingSet.createHorizontalPanel(true);
	buttonControls.setAlignmentY(TOP_ALIGNMENT);
	buttonControls.setAlignmentX(LEFT_ALIGNMENT);

	JPanel leftColumn = SwingSet.createVerticalPanel(false);
	leftColumn.setAlignmentX(LEFT_ALIGNMENT);
	leftColumn.setAlignmentY(TOP_ALIGNMENT);

	JPanel rightColumn = new LayoutControlPanel(swing, swing.radioButtons);

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
	enabled.setSelected(true);
	enabled.setToolTipText("Click here to enable or disable the radio buttons.");
        enabled.setMnemonic('e');
	enabled.addItemListener(swing.buttonDisplayListener);
	leftColumn.add(enabled);
 

	leftColumn.add(Box.createRigidArea(swing.vpad20));

	
	l = new JLabel("Pad Amount:");
	leftColumn.add(l);
	l.setFont(swing.boldFont);
	
	group = new ButtonGroup();
	JRadioButton defaultPad = new JRadioButton("Default");
        defaultPad.setMnemonic('d');
	defaultPad.setToolTipText("Uses the default padding between the border and label.");
	group.add(defaultPad);
	defaultPad.setSelected(true);
 	defaultPad.addItemListener(swing.buttonPadListener);
	leftColumn.add(defaultPad);

	JRadioButton zeroPad = new JRadioButton("0");
        zeroPad.setMnemonic('0');
	group.add(zeroPad);
	zeroPad.setToolTipText("Uses no padding between the border and label.");
 	zeroPad.addItemListener(swing.buttonPadListener);
	leftColumn.add(zeroPad);

	JRadioButton tenPad = new JRadioButton("10");
        tenPad.setMnemonic('1');
	tenPad.setToolTipText("Uses a 10 pixel pad between the border and label.");
	group.add(tenPad);
 	tenPad.addItemListener(swing.buttonPadListener);
	leftColumn.add(tenPad);
	
	leftColumn.add(Box.createRigidArea(swing.vpad20));

	add(buttonPanel);
	add(Box.createRigidArea(swing.hpad10));
 	add(controls);
    }

    
}
