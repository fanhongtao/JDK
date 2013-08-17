/*
 * @(#)DemoGroup.java	1.22 98/09/21
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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Vector;
import java.net.URL;
import java.io.File;


/**
 * DemoGroup handles multiple demos inside of a panel.  Demos are loaded
 * dynamically this is achieved by a directory listing of all the class 
 * files in a demo group directory.
 * Demo groups can be loaded individually, for example : 
 *      java DemoGroup Fonts
 * Loads all the demos found in the demos/Fonts directory.
 */
public class DemoGroup extends JPanel implements MouseListener, ChangeListener, ActionListener {

    private static Font font = new Font("serif", Font.PLAIN, 10);
    private static EmptyBorder emptyB = new EmptyBorder(5,5,5,5);
    private static BevelBorder bevelB = new BevelBorder(BevelBorder.LOWERED);
    private String groupName;
    public JPanel clonePanels[];
    public JTabbedPane tabbedPane;


    public DemoGroup(String name) {

        groupName = name;

        setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(0,2));
        p.setBorder(new CompoundBorder(emptyB, bevelB));

        // Retrieve class names in a specified demos/* directory
        URL url = DemoGroup.class.getResource("demos/" + name);
        File dir = new File(url.getFile());
        if (dir != null && dir.isDirectory()) {
            String list[] = dir.list();
            Java2Demo.sort(list);
            Vector vector = new Vector();
            for (int i = 0; i < list.length; i++) {
                if (list[i].indexOf('$') == -1 && list[i].endsWith(".class")) {
                    String s1 = list[i].substring(0,list[i].indexOf('.'));
                    vector.add("demos." + name + "." + s1);
                }
            }
            if (vector.size()%2 == 1) {
                p.setLayout(new GridBagLayout());
            } 
            for (int i = 0; i < vector.size(); i++) {
                DemoPanel dp = new DemoPanel((String) vector.elementAt(i));
                dp.setDemoBorder(p);
                if (dp.surface != null) {
                    dp.surface.addMouseListener(this);
                    dp.surface.setMonitor(Java2Demo.performancemonitor != null);
                } 
                if (p.getLayout() instanceof GridBagLayout) {
                    int x = p.getComponentCount() % 2;
                    int y = p.getComponentCount() / 2;
                    int w = i == vector.size()-1 ? 2 : 1;
                    Java2Demo.addToGridBag(p,dp,x,y,w,1,1,1);
                } else {
                    p.add(dp);
                }
            }
        } else {
            System.out.println("Couldn't find " + name);
        }

        add(p);
    }


    public void mouseClicked(MouseEvent e) {

        if (tabbedPane == null) {
            shutDown(getPanel());
            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(new CompoundBorder(emptyB, bevelB));

            tabbedPane = new JTabbedPane();
            tabbedPane.setFont(font);

            JPanel tmpP = (JPanel) getComponent(0);
            tabbedPane.addTab(groupName, tmpP);

            clonePanels = new JPanel[tmpP.getComponentCount()];
            for (int i = 0; i < clonePanels.length; i++) {
                clonePanels[i] = new JPanel(new BorderLayout());
                DemoPanel dp = (DemoPanel) tmpP.getComponent(i);
                DemoPanel c = new DemoPanel(dp.className);
                c.setDemoBorder(clonePanels[i]);
                if (c.surface != null) {
                    c.surface.setMonitor(Java2Demo.performancemonitor != null);
                    c.toolbar.cloneB = 
                        c.toolbar.addTool("clone.gif","Clone the Surface",this);
                } 
                clonePanels[i].add(c);
                String s = dp.className.substring(dp.className.indexOf('.')+1);
                tabbedPane.addTab(s, clonePanels[i]);
            }
            p.add(tabbedPane);
            remove(tmpP);
            add(p);

            tabbedPane.addChangeListener(this);
            validate();
        }

        String className = e.getComponent().toString();
        className = className.substring(0, className.indexOf('['));

        if (Java2Demo.controls != null) {
            // even if Controls.toolBarCB is off, lets make visible.
            if (!Java2Demo.controls.toolBarCB.isSelected()) {
                Java2Demo.controls.toolBarCB.setSelected(true);
            }
        }

        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String s1 = className.substring(className.indexOf('.')+1);
            if (tabbedPane.getTitleAt(i).equals(s1)) {
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }

        validate();
    }

    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }


    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        if (b.getToolTipText().startsWith("Clone")) {
            cloneDemo();
        } else {
            removeClone(b.getParent().getParent().getParent());
        }
    }


    private int index;

    public void stateChanged(ChangeEvent e) {
        shutDown((JPanel) tabbedPane.getComponentAt(index));
        index = tabbedPane.getSelectedIndex();
        setup(false);
    }


    public JPanel getPanel() {
        if (tabbedPane != null) {
            return (JPanel) tabbedPane.getSelectedComponent();
        } else {
            return (JPanel) getComponent(0);
        }
    }


    public void setup(boolean issueRepaint) {

        JPanel p = getPanel();

        // Let PerformanceMonitor know which demos are running
        if (Java2Demo.performancemonitor != null) {
            Java2Demo.performancemonitor.mc.setPanel(p);
            Java2Demo.performancemonitor.mc.setSurfaceState();
        }

        // .. toolbar check against global controls settings ..
        // .. & start demo if need be ..
        for (int i = 0; i < p.getComponentCount(); i++) {
            DemoPanel dp = (DemoPanel) p.getComponent(i);
            // Check toolbar against global controls
            if (dp.surface == null || dp.toolbar == null) {
                continue;
            }
            ToolBar tb = dp.toolbar;
            if (tb.startStopB != null && tb.startStopB.isSelected()) {
               dp.surface.start();
            }
            Controls c = Java2Demo.controls;
            if (c == null) {
                continue;
            }
            tb.verboseFlag = c.verboseCB.isSelected();
            tb.issueRepaint = issueRepaint;
            JButton b[] = {tb.aliasB, tb.renderB, tb.textureB, tb.compositeB};
            JCheckBox cb[] = {c.aliasCB, c.renderCB, c.textureCB,c.compositeCB};
            for (int j = 0; j < b.length; j++) {
                if (b[j].isSelected() != cb[j].isSelected()) {
                    b[j].doClick();
                }
            }
            if ((tb.isVisible() && !c.toolBarCB.isSelected()) ||
                (!tb.isVisible() && c.toolBarCB.isSelected())) {
                tb.setVisible(c.toolBarCB.isSelected());
                dp.validate();
            }

            if (c.imgTypeCombo.getSelectedIndex() != tb.imgTypeCombo.getSelectedIndex()) {
                tb.imgTypeCombo.setSelectedIndex(c.imgTypeCombo.getSelectedIndex());
            }
            tb.issueRepaint = true;
        } 
    }


    public void shutDown(JPanel p) {
        for (int i = 0; i < p.getComponentCount(); i++) {
            DemoPanel dp = (DemoPanel) p.getComponent(i);
            if (dp.surface != null) {
                dp.surface.stop();
                dp.surface.bimg = null;
            }
        } 
        System.gc();
    }


    public void cloneDemo() {
        int i = tabbedPane.getSelectedIndex() - 1;
        if (clonePanels[i].getComponentCount() == 1) {
            clonePanels[i].invalidate();
            clonePanels[i].setLayout(new GridLayout(0,2,2,2));
            clonePanels[i].validate();
        }
        DemoPanel original = (DemoPanel) getPanel().getComponent(0);
        DemoPanel clone = new DemoPanel(original.className);
        clone.setDemoBorder(clonePanels[i]);
        clone.toolbar.cloneB = 
              clone.toolbar.addTool("remove.gif","Remove the Surface",this);
        clone.surface.start();
        clone.surface.setMonitor(Java2Demo.performancemonitor != null);
        clonePanels[i].add(clone);
        clonePanels[i].repaint();
        clonePanels[i].validate();
    }


    public void removeClone(Component theClone) {
        int i = tabbedPane.getSelectedIndex() - 1;
        if (clonePanels[i].getComponentCount() == 2) {
            Component cmp = clonePanels[i].getComponent(0);
            clonePanels[i].removeAll();
            clonePanels[i].setLayout(new BorderLayout());
            clonePanels[i].validate();
            clonePanels[i].add(cmp);
        } else {
            clonePanels[i].remove(theClone);
            int cmpCount = clonePanels[i].getComponentCount();
            for (int j = 1;j < cmpCount; j++) {
                int top = (j+1 >= 3) ? 0 : 5;
                int left = ((j+1) % 2) == 0 ? 0 : 5;
                EmptyBorder eb = new EmptyBorder(top,left,5,5);
                SoftBevelBorder sbb = new SoftBevelBorder(BevelBorder.RAISED);
                JPanel p = (JPanel) clonePanels[i].getComponent(j);
                p.setBorder(new CompoundBorder(eb, sbb));
            }
        }
        clonePanels[i].repaint();
        clonePanels[i].validate();
    }


    public static void main(String args[]) {
        final DemoGroup group = new DemoGroup(args[0]);
        JFrame f = new JFrame("Java2D Demo - DemoGroup");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { group.setup(false); }
            public void windowIconified(WindowEvent e) { 
                group.shutDown(group.getPanel()); 
            }
        });
        f.getContentPane().add("Center", group);
        f.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int WIDTH = 640;
        int HEIGHT = 580;
        f.setLocation(screenSize.width/2 - WIDTH/2,
                          screenSize.height/2 - HEIGHT/2);
        f.setSize(WIDTH, HEIGHT);
        f.show();
        group.setup(false);
    }
}
