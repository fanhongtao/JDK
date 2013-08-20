/*
 * @(#)JumpingBox.java	1.16 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
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
 * @(#)JumpingBox.java	1.16 04/07/26
 */

import java.awt.event.*;
import java.awt.*;

public class JumpingBox extends java.applet.Applet 
    implements MouseListener, MouseMotionListener, ComponentListener {

    private int mx, my;
    private Dimension size;
    private int onaroll;

    public void init() {
        onaroll = 0;
        setSize(500, 500);
        size = getSize();
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
    }

    public void update(Graphics g) {
        Dimension newSize = getSize();
        if (size.equals(newSize)) {
            // Erase old box
            g.setColor(getBackground());
            g.drawRect(mx, my, (size.width / 10) - 1, 
                       (size.height / 10) - 1);
        } else {
            size = newSize;
            g.clearRect(0, 0, size.width, size.height);
        }
        // Calculate new position
        mx = (int) (Math.random() * 1000) % 
            (size.width - (size.width / 10));
        my = (int) (Math.random() * 1000) % 
            (size.height - (size.height / 10));
        paint(g);
    }
  
    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.drawRect(0, 0, size.width - 1, size.height - 1);
        g.drawRect(mx, my, (size.width / 10) - 1, 
                   (size.height / 10) - 1);
    }

    /*
     * Mouse methods
     */
    public void mouseDragged(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
        e.consume();
        if ((e.getX() % 3 == 0) && (e.getY() % 3 == 0)) {
            repaint();
        }
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        e.consume();
        requestFocus();
        if (mx < x && x < mx + getSize().width / 10 - 1 && 
            my < y && y < my + getSize().height / 10 - 1) {  //determine if hit
            if (onaroll > 0) {   //not first hit
                switch (onaroll%4) {   //play a sound
                case 0:
                    play(getCodeBase(), 
                         "sounds/tiptoe.thru.the.tulips.au");
                    break;
                case 1:
                    play(getCodeBase(), "sounds/danger.au");
                    break;
                case 2:
                    play(getCodeBase(), "sounds/adapt-or-die.au");
                    break;
                case 3:
                    play(getCodeBase(), "sounds/cannot.be.completed.au");
                    break;
                }
                onaroll++;
                if (onaroll > 5) {
                    getAppletContext()
                        .showStatus("You're on your way to THE HALL OF FAME:"
                                    + onaroll + "Hits!");
                } else {
                    getAppletContext().showStatus("YOU'RE ON A ROLL:" 
                                                  + onaroll + "Hits!");
                }
            } else {   //first hit
                getAppletContext().showStatus("HIT IT AGAIN! AGAIN!");
                play(getCodeBase(), "sounds/that.hurts.au");
                onaroll = 1;
            }
        } else {   //miss
            getAppletContext().showStatus("You hit nothing at (" + x + ", " 
                                          + y + "), exactly");
            play(getCodeBase(), "sounds/thin.bell.au");
            onaroll = 0;
        }
        repaint();
    }

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {
        repaint();
    }

    public void mouseExited(MouseEvent e) {
        repaint();
    }

    public void mouseClicked(MouseEvent e) {}

    public void componentHidden(ComponentEvent e) {}
 
    public void componentMoved(ComponentEvent e) {}
 
    public void componentResized(ComponentEvent e) {
        repaint();
    }
 
    public void componentShown(ComponentEvent e) {
        repaint();
    }
  
    public void destroy() {
        removeMouseListener(this);
        removeMouseMotionListener(this);
    }

    public String getAppletInfo() {
        return "Title: JumpingBox\n"
            + "Author: Anonymous";
    }
}
