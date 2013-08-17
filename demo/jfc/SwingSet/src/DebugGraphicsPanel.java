/*
 * @(#)DebugGraphicsPanel.java	1.7 99/04/23
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
import javax.swing.text.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * Debug Graphics!
 *
 * @version 1.7 04/23/99
 * @author Jeff Dinkins
 * @author Peter Korn (accessibility support)
 */
public class DebugGraphicsPanel extends JPanel 
{
    // The Frame
    SwingSet swing;
    JPanel components;

    JButton button = new JButton("Button");
    JRadioButton radio = new JRadioButton("RadioButton");
    JCheckBox check = new JCheckBox("Checkbox");
    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
    JScrollBar scrollbar = new JScrollBar(JScrollBar.HORIZONTAL, 50, 20, 0, 100);

    JSlider flashSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, 10);
    JCheckBox buttonCheckbox = new JCheckBox("Button");
    JCheckBox radioCheckbox = new JCheckBox("RadioButton");
    JCheckBox checkboxCheckbox = new JCheckBox("Checkbox");
    JCheckBox sliderCheckbox = new JCheckBox("Slider");
    JCheckBox scrollbarCheckbox = new JCheckBox("ScrollBar");

    DebugGraphicsListener debugGraphicsListener = new DebugGraphicsListener();
    ChangeListener sliderListener;

    public DebugGraphicsPanel(SwingSet swing) {
	this.swing = swing;
	sliderListener = new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
		JSlider s = (JSlider)e.getSource();
		DebugGraphics.setFlashTime(s.getValue());
	    }
	};
	flashSlider.addChangeListener(sliderListener);
	

	setBorder(swing.emptyBorder5);
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	// *************** buttons ****************
	components = SwingSet.createVerticalPanel(true);
	components.setBorder(swing.emptyBorder10);

        button.setEnabled(false);
        radio.setEnabled(false);
        radio.setSelected(true);
        check.setEnabled(false);
        check.setSelected(true);
	
	// Add buttons to buttonPanel
	JPanel buttonPanel = swing.createHorizontalPanel(false);
	buttonPanel.setBorder(swing.etchedBorder10);
	buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
	buttonPanel.setAlignmentY(TOP_ALIGNMENT);

	JLabel l = (JLabel) components.add(new JLabel("Buttons"));
	l.setFont(swing.boldFont);
	buttonPanel.add(button);
	buttonPanel.add(Box.createRigidArea(swing.hpad20));
	buttonPanel.add(radio);
	buttonPanel.add(Box.createRigidArea(swing.hpad20));
	buttonPanel.add(check);
	components.add(buttonPanel);
	components.add(Box.createRigidArea(swing.vpad20));

	// *************** slider ****************

        // slider.setEnabled(false);
	slider.setEnabled(false);
	slider.setPaintTicks(true);
	slider.setMinorTickSpacing(10);
	slider.setMajorTickSpacing(40);
	slider.getAccessibleContext().setAccessibleName("Sample slider");

	JPanel sliderPanel = new JPanel() {
	    public Dimension getMaximumSize() {
		return new Dimension(super.getMaximumSize().width, 60);
	    }
	};
	sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
	sliderPanel.setBorder(swing.etchedBorder10);
	sliderPanel.setAlignmentX(LEFT_ALIGNMENT);
	sliderPanel.setAlignmentY(TOP_ALIGNMENT);

	l = (JLabel) components.add(new JLabel("Slider"));
	l.setFont(swing.boldFont);
	sliderPanel.add(slider);
	components.add(sliderPanel);
	components.add(Box.createRigidArea(swing.vpad20));

	// *************** scrollbar ****************

        scrollbar.setEnabled(false);
	scrollbar.getAccessibleContext().setAccessibleName("Sample scrollbarlider");

	JPanel scrollbarPanel = new JPanel() {
	    public Dimension getMaximumSize() {
		return new Dimension(super.getMaximumSize().width, 60);
	    }
	};
	scrollbarPanel.setLayout(new BoxLayout(scrollbarPanel, BoxLayout.Y_AXIS));
	scrollbarPanel.setBorder(swing.etchedBorder10);
	scrollbarPanel.setAlignmentX(LEFT_ALIGNMENT);
	scrollbarPanel.setAlignmentY(TOP_ALIGNMENT);

	JPanel box = swing.createHorizontalPanel(false);
	box.add(Box.createRigidArea(swing.hpad5));
	box.add(scrollbar);
	box.add(Box.createRigidArea(swing.hpad5));

	l = (JLabel) components.add(new JLabel("ScrollBar"));
	l.setFont(swing.boldFont);
	scrollbarPanel.add(Box.createRigidArea(swing.vpad5));
	scrollbarPanel.add(box);
	scrollbarPanel.add(Box.createRigidArea(swing.vpad5));
	components.add(scrollbarPanel);
	components.add(Box.createRigidArea(swing.vpad20));

	l = (JLabel) components.add(new JLabel("Note: the above components are intentionally disabled."));
	components.add(Box.createRigidArea(swing.vpad5));
	l = (JLabel) components.add(new JLabel("Choose a component checkbox at right, then click on"));
	l = (JLabel) components.add(new JLabel("the \"Repaint\" button to see debug graphics at work."));


	// ***** fill out the rest of the panel
	components.add(Box.createGlue());

	// *************** Create the debug graphics controls ****************
	JPanel controls = new JPanel() {
	    public Dimension getMaximumSize() {
		return new Dimension(300, super.getMaximumSize().height);
	    }
	};
	controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

	JPanel debugControls = swing.createHorizontalPanel(true);
	debugControls.setAlignmentY(TOP_ALIGNMENT);
	debugControls.setAlignmentX(LEFT_ALIGNMENT);

	JPanel leftColumn = swing.createVerticalPanel(false);
	leftColumn.setAlignmentX(LEFT_ALIGNMENT);
	leftColumn.setAlignmentY(TOP_ALIGNMENT);

	debugControls.add(leftColumn);
	debugControls.add(Box.createRigidArea(swing.hpad20));
	debugControls.add(Box.createRigidArea(swing.hpad20));

	controls.add(debugControls);

	// Display Options
	l = new JLabel("Use Debug Graphics On:");
	leftColumn.add(l);
	l.setFont(swing.boldFont);

 	buttonCheckbox.addItemListener(debugGraphicsListener);
	buttonCheckbox.setToolTipText("Turns DebugGraphics on or off.");
 	leftColumn.add(buttonCheckbox);

 	radioCheckbox.addItemListener(debugGraphicsListener);
	radioCheckbox.setToolTipText("Turns DebugGraphics on or off.");
 	leftColumn.add(radioCheckbox);

 	checkboxCheckbox.addItemListener(debugGraphicsListener);
	checkboxCheckbox.setToolTipText("Turns DebugGraphics on or off.");
 	leftColumn.add(checkboxCheckbox);

 	sliderCheckbox.addItemListener(debugGraphicsListener);
	sliderCheckbox.setToolTipText("Turns DebugGraphics on or off.");
 	leftColumn.add(sliderCheckbox);

 	scrollbarCheckbox.addItemListener(debugGraphicsListener);
	scrollbarCheckbox.setToolTipText("Turns DebugGraphics on or off.");
 	leftColumn.add(scrollbarCheckbox);

	// debug flashTime
	leftColumn.add(Box.createRigidArea(swing.vpad40));
	l = new JLabel("Debug Flash Interval:");
	l.setFont(swing.boldFont);
	leftColumn.add(l);
	leftColumn.add(flashSlider);
	flashSlider.setMaximumSize(new Dimension(150, 60));
	flashSlider.setPaintTicks(true);
	flashSlider.setMinorTickSpacing(5);
	flashSlider.setMajorTickSpacing(20);
	flashSlider.setToolTipText("Sets the sleep time between graphics operations, from 1 to 50 milliseconds");
	flashSlider.getAccessibleContext().setAccessibleName("Debug Flash Interval");

	// repaint button
	leftColumn.add(Box.createRigidArea(swing.vpad40));
 	JButton repaintButton = new JButton("Repaint");
	repaintButton.setToolTipText("Causes the selected components to be repainted using DebugGraphics.");
 	repaintButton.addActionListener(debugGraphicsListener);
 	leftColumn.add(repaintButton);
 	leftColumn.add(Box.createGlue());

	add(components);
	add(Box.createRigidArea(swing.hpad10));
 	add(controls);
    }

    public void resetAll() {
	scrollbarCheckbox.setSelected(false);
	buttonCheckbox.setSelected(false);
	radioCheckbox.setSelected(false);
	sliderCheckbox.setSelected(false);
	checkboxCheckbox.setSelected(false);
    }

	
    class DebugGraphicsListener implements ItemListener, ActionListener {
	    boolean repaintButton = false;
	    boolean repaintRadio = false;
	    boolean repaintCheck = false;
	    boolean repaintSlider = false;
	    boolean repaintScrollBar = false;

	    public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof JButton) {
		    if(repaintButton) {
			button.repaint();
		    }
		    if(repaintRadio) {
			radio.repaint();
		    }
		    if(repaintCheck) {
			check.repaint();
		    }
		    if(repaintSlider) {
			slider.repaint();
		    }
		    if(repaintScrollBar) {
			scrollbar.repaint();
		    }
		}
	    }

	    public void itemStateChanged(ItemEvent e) {
		AbstractButton b = (AbstractButton) e.getSource();
		String label = b.getText();
		if(label.equals("Button")) {
		    if(b.isSelected()) {
			button.setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);
			repaintButton = true;
		    } else {
			button.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
			repaintButton = false;
		    }
		} else if(label.equals("RadioButton")) {
		    if(b.isSelected()) {
			radio.setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);
			repaintRadio = true;
		    } else {
			radio.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
			repaintRadio = false;
		    }
		} else if(label.equals("Checkbox")) {
		    if(b.isSelected()) {
			check.setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);
			repaintCheck = true;
		    } else {
			check.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
			repaintCheck = false;
		    }
		} else if(label.equals("Slider")) {
		    if(b.isSelected()) {
			slider.setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);
			repaintSlider = true;
		    } else {
			slider.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
			repaintSlider = false;
		    }
		} else if(label.equals("ScrollBar")) {
		    if(b.isSelected()) {
			scrollbar.setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);
			repaintScrollBar = true;
		    } else {
			scrollbar.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
			repaintScrollBar = false;
		    }
		} else {
		}
	}
    }

}
