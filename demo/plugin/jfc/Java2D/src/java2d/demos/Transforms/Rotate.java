/*
 * @(#)Rotate.java	1.29 06/08/29
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
 * @(#)Rotate.java	1.29 06/08/29
 */

package java2d.demos.Transforms;

import static java.awt.Color.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java2d.ControlsSurface;
import java2d.CustomControls;


/**
 * Rotate ellipses with controls for increment and emphasis.
 * Emphasis is defined as which ellipses have a darker color and thicker stroke.
 */
public class Rotate extends ControlsSurface {


    protected double increment = 5.0;
    protected int emphasis = 9;


    public Rotate() {
        setBackground(WHITE);
        setControls(new Component[] { new DemoControls(this) });
    }


    public void render(int w, int h, Graphics2D g2) {
        int size = Math.min(w, h);
        float ew = size/4;
        float eh = size-20;
        Ellipse2D ellipse = new Ellipse2D.Float(-ew/2, -eh/2, ew, eh);
        for (double angdeg = 0; angdeg < 360; angdeg+=increment) {
            if (angdeg % emphasis == 0) {
                g2.setColor(GRAY);
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(LIGHT_GRAY);
                g2.setStroke(new BasicStroke(0.5f));
            }
            AffineTransform at = AffineTransform.getTranslateInstance(w/2, h/2);
            at.rotate(Math.toRadians(angdeg));
            g2.draw(at.createTransformedShape(ellipse));
        }
        g2.setColor(BLUE);
        ellipse.setFrame(w/2-10,h/2-10,20,20);
        g2.fill(ellipse);
        g2.setColor(GRAY);
        g2.setStroke(new BasicStroke(6));
        g2.draw(ellipse);
        g2.setColor(YELLOW);
        g2.setStroke(new BasicStroke(4));
        g2.draw(ellipse);
        g2.setColor(BLACK);
        g2.drawString("Rotate", 5, 15);
    }


    public static void main(String s[]) {
        createDemoFrame(new Rotate());
    }


    static class DemoControls extends CustomControls implements ActionListener {

        Rotate demo;
        JTextField tf1, tf2;

        public DemoControls(Rotate demo) {
            super(demo.name);
            this.demo = demo;
            JLabel l = new JLabel("Increment:");
            l.setForeground(BLACK);
            add(l);
            add(tf1 = new JTextField("5.0"));
            tf1.setPreferredSize(new Dimension(30,24));
            tf1.addActionListener(this);
            add(l = new JLabel("  Emphasis:"));
            l.setForeground(BLACK);
            add(tf2 = new JTextField("9"));
            tf2.setPreferredSize(new Dimension(30,24));
            tf2.addActionListener(this);
        }


        public void actionPerformed(ActionEvent e) {
            try { 
                if (e.getSource().equals(tf1)) {
                    demo.increment = Double.parseDouble(tf1.getText().trim());
                    if (demo.increment < 1.0) {
                        demo.increment = 1.0;
                    }
                } else {
                    demo.emphasis = Integer.parseInt(tf2.getText().trim());
                }
                demo.repaint();
            } catch (Exception ex) {}
        }

        public Dimension getPreferredSize() {
            return new Dimension(200,39);
        }

        public void run() {
            Thread me = Thread.currentThread();
            while (thread == me) {
                for (int i = 3; i < 13; i+=3) {
                    try {
                        thread.sleep(4444);
                    } catch (InterruptedException e) { return; }
                    tf1.setText(String.valueOf(i));
                    demo.increment = i;
                    demo.repaint();
                }
            }
            thread = null;
        }
    } // End DemoControls class
} // End Rotate class
