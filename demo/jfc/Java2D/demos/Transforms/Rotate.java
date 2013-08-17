/*
 * @(#)Rotate.java	1.14 98/09/13
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

package demos.Transforms;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import DemoSurface;
import DemoPanel;
import CustomControls;


/**
 * Rotate ellipses with controls for increment and emphasis.
 * Emphasis is defined as which ellipses have a darker color and thicker stroke.
 */
public class Rotate extends DemoSurface implements CustomControls {


    protected double angdeg = 5.0;
    protected int emphasis = 9;


    public Rotate() {
        setBackground(Color.white);

    }


    public String getCustomControlsConstraint() {
        return "North";
    }


    public Component getCustomControls() {
        return new RotateControls(this);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {
        int size = Math.min(w, h);
        float ew = size/4;
        float eh = size-20;
        Ellipse2D ellipse = new Ellipse2D.Float(-ew/2, -eh/2, ew, eh);
        for (double i = 0; i < 360; i+=angdeg) {
            if (i % emphasis == 0) {
                g2.setColor(Color.gray);
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(Color.lightGray);
                g2.setStroke(new BasicStroke(0.5f));
            }
            AffineTransform at = AffineTransform.getTranslateInstance(w/2, h/2);
            at.rotate(Math.toRadians(i));
            g2.draw(at.createTransformedShape(ellipse));
        }
        g2.setColor(Color.blue);
        ellipse.setFrame(w/2-10,h/2-10,20,20);
        g2.fill(ellipse);
        g2.setColor(Color.gray);
        g2.setStroke(new BasicStroke(6));
        g2.draw(ellipse);
        g2.setColor(Color.yellow);
        g2.setStroke(new BasicStroke(4));
        g2.draw(ellipse);
        g2.setColor(Color.black);
        g2.drawString("Rotate", 5, 15);
    }


    public static void main(String s[]) {
        Frame f = new Frame("Java2D Demo - Rotate");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.add("Center", new DemoPanel(new Rotate()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }


    static class RotateControls extends JPanel implements ActionListener {

        Rotate rot;
        JTextField tf1, tf2;

        public RotateControls(Rotate rot) {
            this.rot = rot;
            setBackground(Color.gray);
            JLabel l = new JLabel("Increment:");
            l.setForeground(Color.black);
            add(l);
            add(tf1 = new JTextField("5.0"));
            tf1.setPreferredSize(new Dimension(30,24));
            tf1.addActionListener(this);
            add(l = new JLabel("  Emphasis:"));
            l.setForeground(Color.black);
            add(tf2 = new JTextField("9"));
            tf2.setPreferredSize(new Dimension(30,24));
            tf2.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            try { 
                if (e.getSource().equals(tf1)) {
                    rot.angdeg = Double.parseDouble(tf1.getText().trim());
                    if (rot.angdeg < 1.0) {
                        rot.angdeg = 1.0;
                    }
                } else {
                    rot.emphasis = Integer.parseInt(tf2.getText().trim());
                }
                rot.repaint();
            } catch (Exception ex) {}
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,36);
        }
    }
}
