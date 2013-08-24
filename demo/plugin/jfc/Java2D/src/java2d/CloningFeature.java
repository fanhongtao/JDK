/*
 * @(#)CloningFeature.java	1.23 05/11/17
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
 * @(#)CloningFeature.java	1.23 05/11/17
 */


package java2d;

import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.CompoundBorder;

/**
 * Illustration of how to use the clone feature of the demo.
 */
public class CloningFeature extends JPanel implements Runnable {

    private Thread thread;
    private JTextArea ta;


    public CloningFeature() {

        setLayout(new BorderLayout());
        EmptyBorder eb = new EmptyBorder(5,5,5,5);
        SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.RAISED);
        setBorder(new CompoundBorder(eb, sbb));

        ta = new JTextArea("Cloning Demonstrated\n\nClicking once on a demo\n");
        ta.setMinimumSize(new Dimension(300,500));
        JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(ta);
        ta.setFont(new Font("Dialog", Font.PLAIN, 14));
        ta.setForeground(Color.black);
        ta.setBackground(Color.lightGray);
        ta.setEditable(false);

        add("Center", scroller);

        start();
    }

    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setName("CloningFeature");
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = null;
    }


    public void run() {


        int index = Java2Demo.tabbedPane.getSelectedIndex();
        if (index == 0) {
           Java2Demo.tabbedPane.setSelectedIndex(1);
           try { thread.sleep(3333); } catch (Exception e) { return; }
        }

        if (!Java2Demo.controls.toolBarCB.isSelected()) {
            Java2Demo.controls.toolBarCB.setSelected(true);
            try { thread.sleep(2222); } catch (Exception e) { return; }
        }

        index = Java2Demo.tabbedPane.getSelectedIndex()-1;
        DemoGroup dg = Java2Demo.group[index];
        DemoPanel dp = (DemoPanel) dg.getPanel().getComponent(0);
        if (dp.surface == null) {
            ta.append("Sorry your zeroth component is not a Surface.");
            return;
        } 

        dg.mouseClicked(new MouseEvent(dp.surface, MouseEvent.MOUSE_CLICKED, 0, 0, 10, 10, 1, false));

        try { thread.sleep(3333); } catch (Exception e) { return; }

        ta.append("Clicking the ToolBar double document button\n");
        try { thread.sleep(3333); } catch (Exception e) { return; }

        dp = (DemoPanel) dg.clonePanels[0].getComponent(0);

        if (dp.tools != null) {
            for (int i = 0; i < 3 && thread != null; i++) {
                ta.append("   Cloning\n");
                dp.tools.cloneB.doClick();
                try { thread.sleep(3333); } catch (Exception e) { return; }
            }
        }

        ta.append("Changing attributes \n");

        try { thread.sleep(3333); } catch (Exception e) { return; }

        Component cmps[] = dg.clonePanels[0].getComponents();
        for (int i = 0; i < cmps.length && thread != null; i++) {
            if ((dp = (DemoPanel) cmps[i]).tools == null) {
                continue;
            }
            switch (i) {
                case 0 : ta.append("   Changing AntiAliasing\n");
                         dp.tools.aliasB.doClick();
                         break;
                case 1 : ta.append("   Changing Composite & Texture\n");
                         dp.tools.compositeB.doClick();
                         dp.tools.textureB.doClick();
                         break;
                case 2 : ta.append("   Changing Screen\n");
                         dp.tools.screenCombo.setSelectedIndex(4);
                         break;
                case 3 : ta.append("   Removing a clone\n");
                         dp.tools.cloneB.doClick();
            }
            try { thread.sleep(3333); } catch (Exception e) { return; }
        }

        ta.append("\nAll Done!");
    }
}
