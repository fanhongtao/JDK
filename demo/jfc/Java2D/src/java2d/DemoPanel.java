/*
 * @(#)DemoPanel.java	1.16 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package java2d;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.CompoundBorder;


/**
 * The panel for the Surface, Custom Controls & Tools. 
 * Other component types welcome.
 */
public class DemoPanel extends JPanel {

    public Surface surface;
    public CustomControlsContext ccc;
    public Tools tools;
    public String className;


    public DemoPanel(Object obj) {
        setLayout(new BorderLayout());
        try {
            if (obj instanceof String) {
                className = (String) obj;
                obj = Class.forName(className).newInstance();
            }
            if (obj instanceof Component) {
                add((Component) obj);
            } 
            if (obj instanceof Surface) {
                add("South", tools = new Tools(surface = (Surface) obj));
            }
            if (obj instanceof CustomControlsContext) {
                ccc = (CustomControlsContext) obj;
                Component cmps[] = ccc.getControls();
                String cons[] = ccc.getConstraints();
                for (int i = 0; i < cmps.length; i++) {
                    add(cmps[i], cons[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void start() {
	if (surface != null)
	    surface.startClock();
        if (tools != null && surface != null) {
            if (tools.startStopB != null && tools.startStopB.isSelected()) {
                   surface.animating.start();
            }
        }
        if (ccc != null
            && Java2Demo.ccthreadCB != null 
                && Java2Demo.ccthreadCB.isSelected()) 
        {
            ccc.handleThread(CustomControlsContext.START);
        }
    }


    public void stop() {
        if (surface != null) {
            if (surface.animating != null) {
                surface.animating.stop();
            }
            surface.bimg = null;
        }
        if (ccc != null) {
            ccc.handleThread(CustomControlsContext.STOP);
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
