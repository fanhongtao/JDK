/*
 * @(#)DemoPanel.java	1.22 06/08/09
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)DemoPanel.java	1.22 06/08/09
 */


package java2d;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.CompoundBorder;

import static java2d.CustomControlsContext.State.*;

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
            ccc.handleThread(START);
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
            ccc.handleThread(STOP);
        }
    }


    public void setDemoBorder(JPanel p) {
        int top  =  (p.getComponentCount()+1 >= 3)      ? 0 : 5;
        int left = ((p.getComponentCount()+1) % 2) == 0 ? 0 : 5;
        EmptyBorder eb = new EmptyBorder(top,left,5,5);
        SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.RAISED);
        setBorder(new CompoundBorder(eb, sbb));
    }
}
