/*
 * @(#)DemoPanel.java	1.9 98/09/13
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


import java.awt.*;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.CompoundBorder;


/**
 * The panel for the DemoSurface & ToolBar.
 * Other component types welcome.
 */
public class DemoPanel extends JPanel {

    public DemoSurface surface;
    public ToolBar toolbar;
    public String className;


    public DemoPanel(Object obj) {
        setLayout(new BorderLayout());
        try {
            if (obj instanceof String) {
                className = (String) obj;
                obj = Class.forName(className).newInstance();
            }
            if (obj instanceof DemoSurface) {
                add(surface = (DemoSurface) obj);
                add("South", toolbar = new ToolBar(surface));
            } else if (obj instanceof Component) {
                add((Component) obj);
            }
            if (obj instanceof CustomControls) {
                CustomControls custom = (CustomControls) obj;
                add(custom.getCustomControlsConstraint(), 
                                custom.getCustomControls());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setDemoBorder(JPanel p) {
        int top = (p.getComponentCount()+1 >= 3) ? 0 : 5;
        int left = ((p.getComponentCount()+1) % 2) == 0 ? 0 : 5;
        EmptyBorder eb = new EmptyBorder(top,left,5,5);
        SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.RAISED);
        setBorder(new CompoundBorder(eb, sbb));
    }
}
