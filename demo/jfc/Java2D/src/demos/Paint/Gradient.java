/*
 * @(#)Gradient.java	1.7 99/09/07
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

package demos.Paint;


import java.awt.*;
import java.awt.font.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import ControlsSurface;
import CustomControls;



public class Gradient extends ControlsSurface {

    protected Color innerC, outerC;
    private DemoControls controls;


    public Gradient() {
        setBackground(Color.white);
        innerC = Color.green;
        outerC = Color.blue;
        setControls(new Component[] { new DemoControls(this) });
    }


    public void render(int w, int h, Graphics2D g2) {

        int w2 = w/2;
        int h2 = h/2;
        g2.setPaint(new GradientPaint(0,0,outerC,w*.35f,h*.35f,innerC));
        g2.fillRect(0, 0, w2, h2);
        g2.setPaint(new GradientPaint(w,0,outerC,w*.65f,h*.35f,innerC));
        g2.fillRect(w2, 0, w2, h2);
        g2.setPaint(new GradientPaint(0,h,outerC,w*.35f,h*.65f,innerC));
        g2.fillRect(0, h2, w2, h2);
        g2.setPaint(new GradientPaint(w,h,outerC,w*.65f,h*.65f,innerC));
        g2.fillRect(w2, h2, w2, h2);

        g2.setColor(Color.black);
        TextLayout tl = new TextLayout(
                "GradientPaint", g2.getFont(), g2.getFontRenderContext());
        tl.draw(g2, (int) (w/2-tl.getBounds().getWidth()/2),
                (int) (h/2+tl.getBounds().getHeight()/2));
    }


    public static void main(String s[]) {
        createDemoFrame(new Gradient());
    }


    static class DemoControls extends CustomControls implements ActionListener {

        Gradient demo;
        Color colors[] = 
                { Color.red, Color.orange, Color.yellow, Color.green,
                  Color.blue, Color.lightGray, Color.cyan, Color.magenta };
        String colorName[] =
                { "Red", "Orange", "Yellow", "Green", 
                  "Blue", "lightGray", "Cyan", "Magenta" };
        
        JMenuItem innerMI[] = new JMenuItem[colors.length];
        JMenuItem outerMI[] = new JMenuItem[colors.length];
        ColoredSquare squares[] = new ColoredSquare[colors.length];
        JMenu imenu, omenu;

        public DemoControls(Gradient demo) {
            super(demo.name);
            this.demo = demo;
            setBackground(Color.gray);
            JMenuBar inMenuBar = new JMenuBar();
            add(inMenuBar);
            JMenuBar outMenuBar = new JMenuBar();
            add(outMenuBar);
            Font font = new Font("serif", Font.PLAIN, 10);

            imenu = (JMenu) inMenuBar.add(new JMenu("Inner Color"));
            imenu.setFont(font);
            imenu.setIcon(new ColoredSquare(demo.innerC));
            omenu = (JMenu) outMenuBar.add(new JMenu("Outer Color"));
            omenu.setFont(font);
            omenu.setIcon(new ColoredSquare(demo.outerC));
            for (int i = 0; i < colors.length; i++) {
                squares[i] = new ColoredSquare(colors[i]);
                innerMI[i] = imenu.add(new JMenuItem(colorName[i]));
                innerMI[i].setFont(font);
                innerMI[i].setIcon(squares[i]);
                innerMI[i].addActionListener(this);
                outerMI[i] = omenu.add(new JMenuItem(colorName[i]));
                outerMI[i].setFont(font);
                outerMI[i].setIcon(squares[i]);
                outerMI[i].addActionListener(this);
            } 
        }


        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < colors.length; i++) {
                if (e.getSource().equals(innerMI[i])) {
                    demo.innerC = colors[i];
                    imenu.setIcon(squares[i]);
                    break;
                } else if (e.getSource().equals(outerMI[i])) {
                    demo.outerC = colors[i];
                    omenu.setIcon(squares[i]);
                    break;
                }
            }
            demo.repaint();
        }


        public Dimension getPreferredSize() {
            return new Dimension(200,31);
        }


        public void run() {
            // goto double buffering
            if (demo.getImageType() <= 1) {
                demo.setImageType(2);
            }
            Thread me = Thread.currentThread();
            while (thread == me) {
                for (int i = 0; i < innerMI.length; i++) {
                    if (i != 4) {
                        try {
                            thread.sleep(4444);
                        } catch (InterruptedException e) { return; }
                        innerMI[i].doClick();
                    }
                }
            }
            thread = null;
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
        } // End ColoredSquare class
    } // End DemoControls
} // End Gradient class
