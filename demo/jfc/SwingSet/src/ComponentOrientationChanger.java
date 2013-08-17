/*
 * @(#)ComponentOrientationChanger.java	1.5 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * The ComponentOrientationChanger is a menu that lets the user select the
 * desired ComponentOrientation setting for all the Components in SwingSet.
 * When a selection is made, the ComponentOrientationChanger will walk the 
 * entire Component tree and changing each Components orientation setting to
 * the selected value.
 */
class ComponentOrientationChanger extends JMenu implements ItemListener {

    static ComponentOrientationChanger create() {
        
        return new ComponentOrientationChanger();
        


    }

    public ComponentOrientationChanger() {
        super("Component Orientation");

        getAccessibleContext().setAccessibleDescription(
           "Sub-menu containing options for changing the orientation of the Swing Components.");

        ButtonGroup orientationGroup = new ButtonGroup();

        ltrRb = (JRadioButtonMenuItem)add(new JRadioButtonMenuItem("Left To Right"));
        ltrRb.getAccessibleContext().setAccessibleDescription("Orient Components for left to right languages.");
        ltrRb.setSelected(true);
        ltrRb.addItemListener(this);
        orientationGroup.add(ltrRb);
        
        rtlRb = (JRadioButtonMenuItem)add(new JRadioButtonMenuItem("Right To Left"));
        rtlRb.getAccessibleContext().setAccessibleDescription("Orient Components for left to right languages.");
        rtlRb.addItemListener(this);
        orientationGroup.add(rtlRb);
    }

    JRadioButtonMenuItem ltrRb, rtlRb;

    public void itemStateChanged(ItemEvent e) {
        
        JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
        if (rb.isSelected()) {
            String selected = rb.getText();
            ComponentOrientation orientation;
            if (selected.equals("Left To Right")) {
                orientation = ComponentOrientation.LEFT_TO_RIGHT;
            } else  {
                orientation = ComponentOrientation.RIGHT_TO_LEFT;
            } 
            Container swingRoot = SwingSet.sharedInstance().getRootComponent();
            applyOrientation( swingRoot, orientation );
            fireActionPerformed(new ActionEvent(this,0,"OrientationChanged"));
            swingRoot.validate();
            swingRoot.repaint();
        }
        
    }

    
    private void applyOrientation(Component c, ComponentOrientation o) {
        c.setComponentOrientation(o);

        if( c instanceof JMenu ) {
            JMenu menu = (JMenu)c;
            int ncomponents = menu.getMenuComponentCount();
            for (int i = 0 ; i < ncomponents ; ++i) {
                applyOrientation( menu.getMenuComponent(i), o );
            }
        }
        else if( c instanceof Container ) {
            Container container = (Container)c;
            int ncomponents = container.getComponentCount();
            for (int i = 0 ; i < ncomponents ; ++i) {
                applyOrientation( container.getComponent(i), o );
            }
        }
    }

    private void dumpOrientation(Component c, int level) {
        ComponentOrientation o = c.getComponentOrientation();
        for(int i=0; i<level; i++)
            System.out.print("\t");
        System.out.println(c + " isLTR=" + o.isLeftToRight() );

        if( c instanceof Container ) {
            Container container = (Container)c;
            int ncomponents = container.getComponentCount();
            for (int i = 0 ; i < ncomponents ; ++i) {
                dumpOrientation( container.getComponent(i), level+1);
            }
        }
    }
    
}

