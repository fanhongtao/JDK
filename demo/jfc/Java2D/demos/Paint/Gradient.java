/*
 * @(#)Gradient.java	1.4 98/09/13
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

package demos.Paint;


import java.awt.*;
import java.awt.font.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import DemoSurface;
import DemoPanel;
import CustomControls;


public class Gradient extends DemoSurface implements CustomControls {

    protected Color innerColor, outerColor;

    public Gradient() {
        setBackground(Color.white);
        innerColor = Color.green;
        outerColor = Color.blue;
    }


    public String getCustomControlsConstraint() {
        return "North";
    }


    public Component getCustomControls() {
        return new GradientControls(this);
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        Rectangle2D rect1 = new Rectangle2D.Float(0.0f, 0.0f, w/2, h/2);
        GradientPaint gp = 
            new GradientPaint(0,0,outerColor,w*.35f,h*.35f,innerColor);
        g2.setPaint(gp);
        g2.fill(rect1);

        rect1 = new Rectangle2D.Float(w/2, 0f, w/2, h/2);
        gp = new GradientPaint(w,0,outerColor,w*.65f,h*.35f,innerColor);
        g2.setPaint(gp);
        g2.fill(rect1);

        rect1 = new Rectangle2D.Float(0f, h/2, w/2, h/2);
        gp = new GradientPaint(0,h,outerColor,w*.35f,h*.65f,innerColor);
        g2.setPaint(gp);
        g2.fill(rect1);

        rect1 = new Rectangle2D.Float(w/2, h/2, w/2, h/2);
        gp = new GradientPaint(w,h,outerColor,w*.65f,h*.65f,innerColor);
        g2.setPaint(gp);
        g2.fill(rect1);


        g2.setColor(Color.black);
        TextLayout tl = new TextLayout(
                "GradientPaint", g2.getFont(), g2.getFontRenderContext());
        tl.draw(g2, (int) (w/2-tl.getBounds().getWidth()/2),
                (int) (h/2+tl.getBounds().getHeight()/2));
    }


    public static void main(String s[]) {
        JFrame f = new JFrame("Java 2D Demo - Gradient");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.getContentPane().add("Center", new DemoPanel(new Gradient()));
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
    }


    static class GradientControls extends JPanel implements ActionListener {

        Gradient gradient;
        Color colors[] = 
                { Color.red, Color.orange, Color.yellow, Color.green,
                  Color.blue, Color.lightGray, Color.cyan, Color.magenta };
        String colorName[] =
                { "Red", "Orange", "Yellow", "Green", 
                  "Blue", "lightGray", "Cyan", "Magenta" };
        
        JMenuItem innerMI[] = new JMenuItem[colors.length];
        JMenuItem outerMI[] = new JMenuItem[colors.length];
        JMenu imenu, omenu;

        public GradientControls(Gradient gradient) {
            this.gradient = gradient;
            setBackground(Color.gray);
            JMenuBar inMenuBar = new JMenuBar();
            add(inMenuBar);
            JMenuBar outMenuBar = new JMenuBar();
            add(outMenuBar);
            Font font = new Font("serif", Font.PLAIN, 10);

            imenu = (JMenu) inMenuBar.add(new JMenu("Inner Color"));
            imenu.setFont(font);
            imenu.setIcon(new ColoredSquare(gradient.innerColor));
            omenu = (JMenu) outMenuBar.add(new JMenu("Outer Color"));
            omenu.setFont(font);
            omenu.setIcon(new ColoredSquare(gradient.outerColor));
            for (int i = 0; i < colors.length; i++) {
                ColoredSquare cs = new ColoredSquare(colors[i]);
                innerMI[i] = imenu.add(new JMenuItem(colorName[i]));
                innerMI[i].setFont(font);
                innerMI[i].setIcon(cs);
                innerMI[i].addActionListener(this);
                outerMI[i] = omenu.add(new JMenuItem(colorName[i]));
                outerMI[i].setFont(font);
                outerMI[i].setIcon(cs);
                outerMI[i].addActionListener(this);
            } 
        }


        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < colors.length; i++) {
                if (e.getSource().equals(innerMI[i])) {
                    gradient.innerColor = colors[i];
                    imenu.setIcon(new ColoredSquare(colors[i]));
                    imenu.validate();
                    break;
                } else if (e.getSource().equals(outerMI[i])) {
                    gradient.outerColor = colors[i];
                    omenu.setIcon(new ColoredSquare(colors[i]));
                    omenu.validate();
                    break;
                }
            }
            gradient.repaint();
        }


        public Dimension getPreferredSize() {
            return new Dimension(200,31);
        }


        class ColoredSquare implements Icon {
            Color color;
            public ColoredSquare(Color c) {
                this.color = c;
            }
    
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Color oldColor = g.getColor();
                g.setColor(color);
                g.fill3DRect(x,y,getIconWidth(), getIconHeight(), true);
                g.setColor(oldColor);
            }
            public int getIconWidth() { return 12; }
            public int getIconHeight() { return 12; }
        }
    }
}
