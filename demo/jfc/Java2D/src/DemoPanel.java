/*
 * @(#)DemoPanel.java	1.12 99/09/07
 *
 * Copyright (c) 1998, 1999 by Sun Microsystems, Inc. All Rights Reserved.
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
