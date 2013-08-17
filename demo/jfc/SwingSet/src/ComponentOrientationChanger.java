/*
 * @(#)ComponentOrientationChanger.java	1.4 99/04/23
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

