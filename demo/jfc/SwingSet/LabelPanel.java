/*
 * @(#)LabelPanel.java	1.9 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * Labels!
 *
 * @version 1.1 11/14/97
 * @author Jeff Dinkins
 */
public class LabelPanel extends JPanel 
{
    SwingSet swing;

    public LabelPanel(SwingSet swing) {
	this.swing = swing;

	setBorder(swing.emptyBorder5);
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	setOpaque(false);

	JLabel label;

	JPanel labelsPanel = SwingSet.createVerticalPanel(true);
	labelsPanel.setOpaque(false);

	JPanel row1 = SwingSet.createHorizontalPanel(false);
	JPanel row2 = SwingSet.createHorizontalPanel(false);
	JPanel row3 = SwingSet.createHorizontalPanel(false);
	JPanel row4 = SwingSet.createHorizontalPanel(false);
	JPanel row5 = SwingSet.createHorizontalPanel(false);
 
        label = new BorderedSwingSetLabel("Label 1");
	label.setToolTipText("Duke says \"Howdy!\"");
        label.setHorizontalAlignment(label.CENTER);
        label.setIcon(swing.dukeWave);
        label.setVerticalTextPosition(label.BOTTOM);
        label.setHorizontalTextPosition(label.CENTER);
        row1.add(label);
        swing.labels.addElement(label);
 
        label = new BorderedSwingSetLabel("Label2");
	label.setToolTipText("Yep! You can even have a ToolTip over a Label!.");
        label.setHorizontalAlignment(label.CENTER);
        label.setVerticalTextPosition(label.BOTTOM);
        label.setHorizontalTextPosition(label.CENTER);
        label.setFont(swing.boldFont);
        label.setForeground(Color.red);
        row1.add(label);
        swing.labels.addElement(label);
 
        label = new BorderedSwingSetLabel("Label3");
	label.setToolTipText("Yep! You can even have a ToolTip over a Label!.");
        label.setVerticalTextPosition(label.BOTTOM);
        label.setHorizontalAlignment(label.CENTER);
        label.setHorizontalTextPosition(label.CENTER);
        row2.add(label);
        label.setFont(swing.bigFont);
        swing.labels.addElement(label);
 

        label = new BorderedSwingSetLabel("Label4");
	label.setToolTipText("Yep! You can even have a ToolTip over a Label!.");
        label.setVerticalTextPosition(label.BOTTOM);
        label.setHorizontalAlignment(label.CENTER);
        label.setHorizontalTextPosition(label.CENTER);
        label.setIcon(swing.dukeMagnify);
        row2.add(label);
        label.setFont(swing.bigBoldFont);
        swing.labels.addElement(label);
 
        label = new BorderedSwingSetLabel("Label 5");
	label.setToolTipText("Shhhh.... Duke is taking a little nap.");
        label.setVerticalTextPosition(label.BOTTOM);
        label.setHorizontalAlignment(label.CENTER);
        label.setHorizontalTextPosition(label.CENTER);
        row3.add(label);
        label.setForeground(Color.blue);
        label.setIcon(swing.dukeSnooze);
        label.setFont(swing.bigBoldFont);
        swing.labels.addElement(label);
 
        label = new BorderedSwingSetLabel("Label 6");
	label.setToolTipText("Yep! You can even have a ToolTip over a Label!.");
        label.setVerticalTextPosition(label.BOTTOM);
        label.setHorizontalAlignment(label.CENTER);
        label.setHorizontalTextPosition(label.CENTER);
        row3.add(label);
        label.setFont(swing.reallyBigBoldFont);
        label.setForeground(Color.green);
        swing.labels.addElement(label);

        label = new JLabel("Type Here: ");
        label.setHorizontalTextPosition(label.RIGHT);
	label.setDisplayedMnemonic('T');
	label.setToolTipText("The labelFor and displayedMnemonic properties work!");
	JTextField tf = new JTextField("");
	label.setLabelFor(tf);
	row4.setBorder(swing.emptyBorder15);
        row4.add(label);
        row4.add(tf);
        swing.labels.addElement(label);

        label = new JLabel("And Here: ");
        label.setHorizontalTextPosition(label.RIGHT);
	label.setDisplayedMnemonic('r');
	label.setToolTipText("The labelFor and displayedMnemonic properties work!");
	tf = new JTextField("");
	label.setLabelFor(tf);
	row5.setBorder(swing.emptyBorder15);
        row5.add(label);
        row5.add(tf);
        swing.labels.addElement(label);

	// Add label panels to labelPanel
	JPanel labelPanel = swing.createVerticalPanel(true);
	labelPanel.setAlignmentX(LEFT_ALIGNMENT);
	labelPanel.setAlignmentY(TOP_ALIGNMENT);

	labelPanel.add(row1);
	labelPanel.add(row2);
	labelPanel.add(row3);
	labelPanel.add(row4);
	labelPanel.add(row5);

	labelPanel.add(Box.createGlue());


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

	JPanel rightColumn = swing.createVerticalPanel(false);
	rightColumn.setAlignmentX(LEFT_ALIGNMENT);
	rightColumn.setAlignmentY(TOP_ALIGNMENT);

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
	bordered.setEnabled(false);
        bordered.setMnemonic('b');
 	bordered.addItemListener(swing.buttonDisplayListener);
 	leftColumn.add(bordered);
 
 	JCheckBox focused = new JCheckBox("Paint Focus");
	focused.setEnabled(false);
        focused.setMnemonic('f');
 	focused.addItemListener(swing.buttonDisplayListener);
 	leftColumn.add(focused);

	JCheckBox enabled = new JCheckBox("Enabled");
	enabled.setToolTipText("Click here to enable or disable all labels.");
	enabled.setSelected(true);
        enabled.setMnemonic('e');
	enabled.addItemListener(swing.buttonDisplayListener);
	leftColumn.add(enabled);
 

	leftColumn.add(Box.createRigidArea(swing.vpad20));

	l = new JLabel("Pad Amount:");
	l.setEnabled(false);
	leftColumn.add(l);
	l.setFont(swing.boldFont);
	
	ButtonGroup group = new ButtonGroup();
	JRadioButton defaultPad = new JRadioButton("Default");
	defaultPad.setEnabled(false);
        defaultPad.setMnemonic('d');
	group.add(defaultPad);
	defaultPad.setSelected(true);
 	defaultPad.addItemListener(swing.buttonPadListener);
	leftColumn.add(defaultPad);

	JRadioButton zeroPad = new JRadioButton("0");
        zeroPad.setMnemonic('0');
	zeroPad.setEnabled(false);
	group.add(zeroPad);
 	zeroPad.addItemListener(swing.buttonPadListener);
	leftColumn.add(zeroPad);

	JRadioButton tenPad = new JRadioButton("10");
        tenPad.setMnemonic('1');
	tenPad.setEnabled(false);
	group.add(tenPad);
 	tenPad.addItemListener(swing.buttonPadListener);
	leftColumn.add(tenPad);
	
	// *************** Create the layout controls ****************
	// Create Text Position Layout control
	JPanel textPosition = DirectionButton.createDirectionPanel(true, "S", swing.textPositionListener);
	JPanel labelAlignment = DirectionButton.createDirectionPanel(true, "C", swing.labelAlignmentListener);

	l = new JLabel("Text Position:");
	rightColumn.add(l);
	l.setFont(swing.boldFont);
 	rightColumn.add(textPosition);

 	rightColumn.add(Box.createRigidArea(swing.vpad20));

	l = new JLabel("Content Alignment:");
	rightColumn.add(l);
	l.setFont(swing.boldFont);
 	rightColumn.add(labelAlignment);

 	rightColumn.add(Box.createGlue());

	add(labelPanel);
	add(Box.createRigidArea(swing.hpad10));
 	add(controls);
    }


    class BorderedSwingSetLabel extends JLabel {

	BorderedSwingSetLabel(String text) {
	    super(text);
	}

	public Insets getInsets() {
	    Insets insets = super.getInsets();
	    insets.left += 3;
	    insets.right += 3;
	    insets.top += 3;
	    insets.bottom += 3;
	    return insets;
	}

	public float getAlignX() {
	    return LEFT_ALIGNMENT;
	}

	public Dimension getPreferredSize() {
	    return new Dimension(145, 90);
	}

	public Dimension getMaximumSize() {
	    return new Dimension(250, 160);
	}

	public void paint(Graphics g) {
	    super.paint(g);
	    g.setColor(Color.black);
	    g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
	}
    }
    
}
