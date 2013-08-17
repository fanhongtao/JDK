/*
 * @(#)ToggleButtonPanel.java	1.12 01/11/29
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
 * ToggleButtons!
 *
 * @version 1.9 08/26/98
 * @author Jeff Dinkins
 */
public class ToggleButtonPanel extends JPanel 
{
    // The Frame
    SwingSet swing;


    public ToggleButtonPanel(SwingSet swing) {
	this.swing = swing;
	ButtonGroup group;

	setBorder(swing.emptyBorder5);
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	// *************** duke ****************
        ImageIcon dukeAnim;
        ImageIcon duke = SwingSet.sharedInstance().loadImageIcon("images/swing.small.gif","Duke waving from a swing");
        ImageIcon dukeWave = SwingSet.sharedInstance().loadImageIcon("images/dukeWave.gif","Duke waving");

	// *************** toggle buttons ****************
	// text buttons
	JPanel textButtons = SwingSet.createHorizontalPanel(false);
	textButtons.setAlignmentX(LEFT_ALIGNMENT);
	Border buttonBorder = new TitledBorder(null, "Text ToggleButton", 
					       TitledBorder.LEFT, TitledBorder.TOP,
					       swing.boldFont);

	Border emptyBorder = new EmptyBorder(5,5,5,5);
	Border compoundBorder = new CompoundBorder( buttonBorder, emptyBorder);

	textButtons.setBorder(compoundBorder);

	group = new ButtonGroup();
	JToggleButton button;
	button = new JToggleButton("One", true);
	button.setToolTipText("This is a ToggleButton with Text");
	group.add(button);
        button.setMnemonic('o');
	swing.toggleButtons.addElement(button);
	textButtons.add(button);
	textButtons.add(Box.createRigidArea(swing.hpad10));
	
	button = new JToggleButton("Two");
	group.add(button);
        button.setMnemonic('t');
	button.setToolTipText("This is a ToggleButton with Text");
	swing.toggleButtons.addElement(button);
	textButtons.add(button);
	textButtons.add(Box.createRigidArea(swing.hpad10));

	button = new JToggleButton("Three");
	group.add(button);
        button.setMnemonic('h');
	button.setToolTipText("This is a ToggleButton with Text");
	swing.toggleButtons.addElement(button);
	textButtons.add(button);


	// image buttons
	group = new ButtonGroup();

	JPanel imageButtons = SwingSet.createHorizontalPanel(false);
	imageButtons.setAlignmentX(LEFT_ALIGNMENT);
	buttonBorder = new TitledBorder(null, "Image ToggleButtons", 
					       TitledBorder.LEFT, TitledBorder.TOP,
					       swing.boldFont);
	compoundBorder = new CompoundBorder(buttonBorder, emptyBorder);
	imageButtons.setBorder(compoundBorder);

	// 1 image
        dukeAnim = SwingSet.sharedInstance().loadImageIcon("images/swing-64.gif","Animated image of Duke swinging on a swing");
	button = new JToggleButton(duke);
        dukeAnim.setImageObserver(button);
	group.add(button);
	button.setRolloverIcon(dukeWave);
	button.setSelectedIcon(dukeAnim);
	button.setSelected(true);
	button.setToolTipText("This is a ToggleButton with a Icon");
	button.getAccessibleContext().setAccessibleName("Swinging Duke toggle button");
	swing.toggleButtons.addElement(button);
	imageButtons.add(button);
	imageButtons.add(Box.createRigidArea(swing.hpad10));
	
	// 2 images
        dukeAnim = SwingSet.sharedInstance().loadImageIcon("images/swing-64.gif","Animated image of Duke swinging on a swing");
	button = new JToggleButton(duke);
	button.setRolloverIcon(dukeWave);
        dukeAnim.setImageObserver(button);
	group.add(button);
	swing.toggleButtons.addElement(button);
	button.setSelectedIcon(dukeAnim);
	button.setToolTipText("This is a ToggleButton with a Icon");
	button.getAccessibleContext().setAccessibleName("Swinging Duke toggle button");
	imageButtons.add(button);
	imageButtons.add(Box.createRigidArea(swing.hpad10));

	// 3 images
        dukeAnim = SwingSet.sharedInstance().loadImageIcon("images/swing-64.gif","Animated image of Duke swinging on a swing");
	button = new JToggleButton(duke);
	button.setRolloverIcon(dukeWave);
        dukeAnim.setImageObserver(button);
	group.add(button);
	button.setSelectedIcon(dukeAnim);
	button.setToolTipText("This is a ToggleButton with a Icon");
	button.getAccessibleContext().setAccessibleName("Swinging Duke toggle button");
	swing.toggleButtons.addElement(button);
	imageButtons.add(button);

	// Add button panels to buttonPanel
	JPanel buttonPanel = SwingSet.createVerticalPanel(true);
	buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
	buttonPanel.setAlignmentY(TOP_ALIGNMENT);

	buttonPanel.add(textButtons);

	buttonPanel.add(Box.createVerticalStrut(10));

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

	JPanel buttonControls = SwingSet.createHorizontalPanel(true);
	buttonControls.setAlignmentY(TOP_ALIGNMENT);
	buttonControls.setAlignmentX(LEFT_ALIGNMENT);

	JPanel leftColumn = SwingSet.createVerticalPanel(false);
	leftColumn.setAlignmentX(LEFT_ALIGNMENT);
	leftColumn.setAlignmentY(TOP_ALIGNMENT);

	JPanel rightColumn = new LayoutControlPanel(swing, swing.toggleButtons);

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
	bordered.setSelected(true);
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
	enabled.setToolTipText("Click here to enable or disable the toggle buttons.");
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
