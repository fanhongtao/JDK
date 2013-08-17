/*
 * @(#)LayoutControlPanel.java	1.3 01/11/29
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

/*
 * The LayoutControlPanel contains controls for setting an 
 * AbstractButton's horizontal and vertical text position and horizontal
 * and vertical alignment.
 */

public class LayoutControlPanel extends JPanel implements SwingConstants {

    LayoutControlPanel(SwingSet swing, Vector controls) {
        this.swing = swing;
        this.controls = controls;
        
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	setAlignmentX(LEFT_ALIGNMENT);
	setAlignmentY(TOP_ALIGNMENT);

        JLabel l;

        // If SwingSet has a ComponentOrientationChanger, then include control
        // for choosing between absolute and relative positioning.  This will
        // only happen when we're running on JDK 1.2 or above.
        if( swing.componentOrientationChanger != null ) {
            l = new JLabel("Positioning:");
            add(l);
            l.setFont(swing.boldFont);
	
            ButtonGroup group = new ButtonGroup();
            PositioningListener positioningListener = new PositioningListener();
            JRadioButton absolutePos = new JRadioButton("Absolute");
            absolutePos.setMnemonic('a');
            absolutePos.setToolTipText("Text/Content positioning is independant of line direction");
            group.add(absolutePos);
            absolutePos.addItemListener(positioningListener);
            add(absolutePos);

            JRadioButton relativePos = new JRadioButton("Relative");
            relativePos.setMnemonic('r');
            relativePos.setToolTipText("Text/Content positioning depends on line direction.");
            group.add(relativePos);
            relativePos.addItemListener(positioningListener);
            add(relativePos);

            add(Box.createRigidArea(swing.vpad20));

            absolutePositions = false;
            relativePos.setSelected(true);

            swing.componentOrientationChanger.addActionListener( new OrientationChangeListener() );
        } else {
            absolutePositions = true;
        }

        textPosition = new DirectionPanel(true, "E", new TextPositionListener());
        labelAlignment = new DirectionPanel(true, "C", new LabelAlignmentListener());

        // Make sure the controls' text position and label alignment match
        // the initial value of the associated direction panel.
        for(int i = 0; i < controls.size(); i++) {
            Component c = (Component) controls.elementAt(i);
            setPosition(c, RIGHT, CENTER);
            setAlignment(c,CENTER,CENTER);
        }

        l = new JLabel("Text Position:");
        add(l);
        l.setFont(swing.boldFont);
        add(textPosition);

        add(Box.createRigidArea(swing.vpad20));

        l = new JLabel("Content Alignment:");
        add(l);
        l.setFont(swing.boldFont);
        add(labelAlignment);

        add(Box.createGlue());
    }


    class OrientationChangeListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            if( !e.getActionCommand().equals("OrientationChanged") ){
                return;
            }
            if( absolutePositions ){
                return;
            }
            
            String currentTextPosition = textPosition.getSelection();
            if( currentTextPosition.equals("NW") )
                textPosition.setSelection("NE");
            else if( currentTextPosition.equals("NE") )
                textPosition.setSelection("NW");
            else if( currentTextPosition.equals("E") )
                textPosition.setSelection("W");
            else if( currentTextPosition.equals("W") )
                textPosition.setSelection("E");
            else if( currentTextPosition.equals("SE") )
                textPosition.setSelection("SW");
            else if( currentTextPosition.equals("SW") )
                textPosition.setSelection("SE");

            String currentLabelAlignment = labelAlignment.getSelection();
            if( currentLabelAlignment.equals("NW") )
                labelAlignment.setSelection("NE");
            else if( currentLabelAlignment.equals("NE") )
                labelAlignment.setSelection("NW");
            else if( currentLabelAlignment.equals("E") )
                labelAlignment.setSelection("W");
            else if( currentLabelAlignment.equals("W") )
                labelAlignment.setSelection("E");
            else if( currentLabelAlignment.equals("SE") )
                labelAlignment.setSelection("SW");
            else if( currentLabelAlignment.equals("SW") )
                labelAlignment.setSelection("SE");
        }
    }

    class PositioningListener implements ItemListener {

	public void itemStateChanged(ItemEvent e) {
	    JRadioButton rb = (JRadioButton) e.getSource();
	    if(rb.getText().equals("Absolute") && rb.isSelected()) {
		absolutePositions = true;
	    } else if(rb.getText().equals("Relative") && rb.isSelected()) {
		absolutePositions = false;
	    } 
            
	    for(int i = 0; i < controls.size(); i++) {
		Component c = (Component)controls.elementAt(i);
                int hPos, vPos, hAlign, vAlign;
                if( c instanceof AbstractButton ) {
                   hPos = ((AbstractButton)c).getHorizontalTextPosition();
                   vPos = ((AbstractButton)c).getVerticalTextPosition();
                   hAlign = ((AbstractButton)c).getHorizontalAlignment();
                   vAlign = ((AbstractButton)c).getVerticalAlignment();
                } else if( c instanceof JLabel ) {
                   hPos = ((JLabel)c).getHorizontalTextPosition();
                   vPos = ((JLabel)c).getVerticalTextPosition();
                   hAlign = ((JLabel)c).getHorizontalAlignment();
                   vAlign = ((JLabel)c).getVerticalAlignment();
                } else {
                    continue;
                }                
                setPosition(c, hPos, vPos);
                setAlignment(c, hAlign, vAlign);
	    }
            
	    int index = swing.tabbedPane.getSelectedIndex();
	    Component currentPage = swing.tabbedPane.getComponentAt(index);
	    currentPage.invalidate();
	    currentPage.validate();
	    currentPage.repaint();            
	}
    };


    // Text Position Listener
    class TextPositionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    JRadioButton rb = (JRadioButton) e.getSource();
	    if(!rb.isSelected()) {
                return;
            }
            String cmd = rb.getActionCommand();
            int hPos, vPos;
            if(cmd.equals("NW")) {
                    hPos = LEFT; vPos = TOP;
            } else if(cmd.equals("N")) {
                    hPos = CENTER; vPos = TOP;
            } else if(cmd.equals("NE")) {
                    hPos = RIGHT; vPos = TOP;
            } else if(cmd.equals("W")) {
                    hPos = LEFT; vPos = CENTER;
            } else if(cmd.equals("C")) {
                    hPos = CENTER; vPos = CENTER;
            } else if(cmd.equals("E")) {
                    hPos = RIGHT; vPos = CENTER;
            } else if(cmd.equals("SW")) {
                    hPos = LEFT; vPos = BOTTOM;
            } else if(cmd.equals("S")) {
                    hPos = CENTER; vPos = BOTTOM;
            } else /*if(cmd.equals("SE"))*/ {
                    hPos = RIGHT; vPos = BOTTOM;
            }
            for(int i = 0; i < controls.size(); i++) {
                Component c = (Component) controls.elementAt(i);
                setPosition(c, hPos, vPos);
            }
            int index = swing.tabbedPane.getSelectedIndex();
            Component currentPage = swing.tabbedPane.getComponentAt(index);
            currentPage.invalidate();
            currentPage.validate();
            currentPage.repaint();
	}
    };


    // Label Alignment Listener
    class LabelAlignmentListener implements  ActionListener {
	public void actionPerformed(ActionEvent e) {
	    JRadioButton rb = (JRadioButton) e.getSource();
	    if(!rb.isSelected()) {
                return;
            }
            String cmd = rb.getActionCommand();
            int hPos, vPos;
            if(cmd.equals("NW")) {
                    hPos = LEFT; vPos = TOP;
            } else if(cmd.equals("N")) {
                    hPos = CENTER; vPos = TOP;
            } else if(cmd.equals("NE")) {
                    hPos = RIGHT; vPos = TOP;
            } else if(cmd.equals("W")) {
                    hPos = LEFT; vPos = CENTER;
            } else if(cmd.equals("C")) {
                    hPos = CENTER; vPos = CENTER;
            } else if(cmd.equals("E")) {
                    hPos = RIGHT; vPos = CENTER;
            } else if(cmd.equals("SW")) {
                    hPos = LEFT; vPos = BOTTOM;
            } else if(cmd.equals("S")) {
                    hPos = CENTER; vPos = BOTTOM;
            } else /*if(cmd.equals("SE"))*/ {
                    hPos = RIGHT; vPos = BOTTOM;
            }
            for(int i = 0; i < controls.size(); i++) {
                Component c = (Component) controls.elementAt(i);
                setAlignment(c,hPos,vPos);
                c.invalidate();
            }
            int index = swing.tabbedPane.getSelectedIndex();
            Component currentPage = swing.tabbedPane.getComponentAt(index);
            currentPage.invalidate();
            currentPage.validate();
            currentPage.repaint();
	}
    };

    // Position
    void setPosition(Component c, int hPos, int vPos) {
        boolean ltr = true;
        
        ltr = c.getComponentOrientation().isLeftToRight();
        
        if( absolutePositions ) {
            if( hPos == LEADING ) {
                hPos = ltr ? LEFT : RIGHT;
            } else if( hPos == TRAILING ) {
                hPos = ltr ? RIGHT : LEFT;
            }
        } else {
            if( hPos == LEFT ) {
                hPos = ltr ? LEADING : TRAILING;
            } else if( hPos == RIGHT ) {
                hPos = ltr ? TRAILING : LEADING;
            }
        }
        if(c instanceof AbstractButton) {
            AbstractButton x = (AbstractButton) c;
            x.setHorizontalTextPosition(hPos);
            x.setVerticalTextPosition(vPos);
        } else if(c instanceof JLabel) {
            JLabel x = (JLabel) c;
            x.setHorizontalTextPosition(hPos);
            x.setVerticalTextPosition(vPos);
        }
    }

    void setAlignment(Component c, int hPos, int vPos) {
        boolean ltr = true;
        
        ltr = c.getComponentOrientation().isLeftToRight();
        
        if( absolutePositions ) {
            if( hPos == LEADING ) {
                hPos = ltr ? LEFT : RIGHT;
            } else if( hPos == TRAILING ) {
                hPos = ltr ? RIGHT : LEFT;
            }
        } else {
            if( hPos == LEFT ) {
                hPos = ltr ? LEADING : TRAILING;
            } else if( hPos == RIGHT ) {
                hPos = ltr ? TRAILING : LEADING;
            }
        }
        if(c instanceof AbstractButton) {
            AbstractButton x = (AbstractButton) c;
            x.setHorizontalAlignment(hPos);
            x.setVerticalAlignment(vPos);
        } else if(c instanceof JLabel) {
            JLabel x = (JLabel) c;
            x.setHorizontalAlignment(hPos);
            x.setVerticalAlignment(vPos);
        }
    }


    private boolean  absolutePositions;
    private SwingSet swing;
    private Vector   controls;
    private DirectionPanel textPosition;
    private DirectionPanel labelAlignment;
}
