/*
 * @(#)Areas.java	1.27 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java2d.demos.Clipping;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import javax.swing.*;
import java2d.ControlsSurface;
import java2d.CustomControls;


/**
 * The Areas class demonstrates the CAG (Constructive Area Geometry) 
 * operations: Add(union), Subtract, Intersect, and ExclusiveOR.
 */
public class Areas extends ControlsSurface {

    protected String areaType = "nop";


    public Areas() {
        setBackground(Color.white);
        setControls(new Component[] { new DemoControls(this) });
    }


    public void render(int w, int h, Graphics2D g2) {
        GeneralPath p1 = new GeneralPath();
        p1.moveTo( w * .25f, 0.0f);
        p1.lineTo( w * .75f, h * .5f);
        p1.lineTo( w * .25f, (float) h);
        p1.lineTo( 0.0f, h * .5f);
        p1.closePath();

        GeneralPath p2 = new GeneralPath();
        p2.moveTo( w * .75f, 0.0f);
        p2.lineTo( (float) w, h * .5f);
        p2.lineTo( w * .75f, (float) h);
        p2.lineTo( w * .25f, h * .5f);
        p2.closePath();


        Area area = new Area(p1);
        g2.setColor(Color.yellow);
        if (areaType.equals("nop")) {
            g2.fill(p1);
            g2.fill(p2);
            g2.setColor(Color.red);
            g2.draw(p1);
            g2.draw(p2);
            return;
        } else if (areaType.equals("add")) {
	    area.add(new Area(p2));
        } else if (areaType.equals("sub")) {
	    area.subtract(new Area(p2));
        } else if (areaType.equals("xor")) {
	    area.exclusiveOr(new Area(p2));
        } else if (areaType.equals("int")) {
	    area.intersect(new Area(p2));
        } else if (areaType.equals("pear")) {

            double sx = w/100;
            double sy = h/140;
            g2.scale(sx, sy);
            double x = w/sx/2;
            double y = h/sy/2;

            // Creates the first leaf by filling the intersection of two Area 
            // objects created from an ellipse.
            Ellipse2D leaf = new Ellipse2D.Double(x-16, y-29, 15.0, 15.0);
            Area leaf1 = new Area(leaf);
            leaf.setFrame(x-14, y-47, 30.0, 30.0);
            Area leaf2 = new Area(leaf); 
            leaf1.intersect(leaf2);   
            g2.setColor(Color.green);
            g2.fill(leaf1);   

            // Creates the second leaf.
            leaf.setFrame(x+1, y-29, 15.0, 15.0);
            leaf1 = new Area(leaf);
            leaf2.intersect(leaf1);
            g2.fill(leaf2);

            // Creates the stem by filling the Area resulting from the 
            // subtraction of two Area objects created from an ellipse.
            Ellipse2D stem = new Ellipse2D.Double(x, y-42, 40.0, 40.0);
            Area st1 = new Area(stem);
            stem.setFrame(x+3, y-47, 50.0, 50.0);
            st1.subtract(new Area(stem));
            g2.setColor(Color.black);
            g2.fill(st1);

            // Creates the pear itself by filling the Area resulting from the 
            // union of two Area objects created by two different ellipses.
            Ellipse2D circle = new Ellipse2D.Double(x-25, y, 50.0, 50.0);
            Ellipse2D oval = new Ellipse2D.Double(x-19, y-20, 40.0, 70.0);
            Area circ = new Area(circle);
            circ.add(new Area(oval));

            g2.setColor(Color.yellow);
            g2.fill(circ);
            return;
        }
        
        g2.fill(area);
        g2.setColor(Color.red);
        g2.draw(area);
    }


    public static void main(String argv[]) {
        createDemoFrame(new Areas());
    }



    static class DemoControls extends CustomControls implements ActionListener {

        Areas demo;
        JToolBar toolbar;
        JComboBox combo;

        public DemoControls(Areas demo) {
            super(demo.name);
            this.demo = demo;
            setBackground(Color.gray);
            add(toolbar = new JToolBar());
            toolbar.setFloatable(false);
            addTool("nop", "no area operation", true);
            addTool("add", "add", false);
            addTool("sub", "subtract", false);
            addTool("xor", "exclusiveOr", false);
            addTool("int", "intersection", false);
            addTool("pear", "pear", false);
        }


        public void addTool(String str, String tooltip, boolean state) {
            JButton b = (JButton) toolbar.add(new JButton(str));
            b.setBackground(state ? Color.green : Color.lightGray);
            b.setToolTipText(tooltip);
            b.setSelected(state);
            b.addActionListener(this);
        }


        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < toolbar.getComponentCount(); i++) {
                JButton b = (JButton) toolbar.getComponentAtIndex(i);
                b.setBackground(Color.lightGray);
            }
            JButton b = (JButton) e.getSource();
            b.setBackground(Color.green);
            demo.areaType = b.getText();
            demo.repaint();
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,37);
        }

        public void run() {
            try { thread.sleep(1111); } catch (Exception e) { return; }
            Thread me = Thread.currentThread();
            while (thread == me) {
                for (int i = 0; i < toolbar.getComponentCount(); i++) {
                    ((JButton) toolbar.getComponentAtIndex(i)).doClick();
                    try {
                        thread.sleep(4444);
                    } catch (InterruptedException e) { return; }
                }
            }
            thread = null;
        }
    } // End DemoControls
} // End Areas
