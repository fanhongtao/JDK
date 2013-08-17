/*
 * @(#)GlobalPanel.java	1.11 99/04/23
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


import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Panel that holds the Demo groups, Controls and Monitors for each tab.
 * It's a special "always visible" panel for the Controls, MemoryMonitor &
 * PerformanceMonitor.
 */
public class GlobalPanel extends JPanel implements ChangeListener {


    private JPanel p;
    private int index;


    public GlobalPanel() {
        setLayout(new BorderLayout());
        p = new JPanel(new GridBagLayout());
        EmptyBorder eb = new EmptyBorder(5,0,5,5);
        BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
        p.setBorder(new CompoundBorder(eb,bb));
        Java2Demo.addToGridBag(p,Java2Demo.controls,0,0,1,1,0,0);
        Java2Demo.addToGridBag(p,Java2Demo.memorymonitor,0,1,1,1,0,0);
        Java2Demo.addToGridBag(p,Java2Demo.performancemonitor,0,2,1,1,0,0);
        add(Java2Demo.intro);
    }


    public void stateChanged(ChangeEvent e) {
         
        Java2Demo.group[index].shutDown(Java2Demo.group[index].getPanel());
        if (Java2Demo.tabbedPane.getSelectedIndex() == 0) {
            Java2Demo.memorymonitor.surf.stop();
            Java2Demo.performancemonitor.surf.stop();
            removeAll();
            add(Java2Demo.intro);
            Java2Demo.intro.start();
        } else {
            if (getComponentCount() == 1) {
                Java2Demo.intro.stop();
                remove(Java2Demo.intro);
                add(p, BorderLayout.EAST);
                if (Java2Demo.memoryCB.getState()) { 
                    Java2Demo.memorymonitor.surf.start();
                }
                if (Java2Demo.perfCB.getState()) { 
                    Java2Demo.performancemonitor.surf.start();
                }
            } else {
                remove(Java2Demo.group[index]);
            }
            index = Java2Demo.tabbedPane.getSelectedIndex()-1;
            add(Java2Demo.group[index]);
            Java2Demo.group[index].setup(false);
        }
        validate();
    }
}
