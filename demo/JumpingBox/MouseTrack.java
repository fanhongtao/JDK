/*
 * @(#)MouseTrack.java	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.event.*;
import java.awt.Graphics;
import java.lang.Math;

public class MouseTrack extends java.applet.Applet implements MouseListener, MouseMotionListener {

    int mx, my;
    int onaroll;

    public void init() {
	onaroll = 0;
	setSize(500, 500);
	addMouseListener(this);
	addMouseMotionListener(this);
    }

    public void destroy() {
        removeMouseListener(this);
        removeMouseMotionListener(this);
    }

    public void paint(Graphics g) {
	g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
	mx = (int)(Math.random()*1000) % (getSize().width - (getSize().width/10));
	my = (int)(Math.random()*1000) % (getSize().height - (getSize().height/10));
	g.drawRect(mx, my, (getSize().width/10) - 1, (getSize().height/10) - 1);
    }

    /*
     * Mouse methods
     */
    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        e.consume();
        if((e.getX() % 3 == 0) && (e.getY() % 3 == 0))
            repaint();
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        e.consume();
        requestFocus();
        if((mx < x && x < mx+getSize().width/10-1) && (my < y && y < my+getSize().height/10-1)) {
            if(onaroll > 0) {
                switch(onaroll%4) {
                    case 0:
                        play(getCodeBase(), "sounds/tiptoe.thru.the.tulips.au");
                        break;
                    case 1:
                        play(getCodeBase(), "sounds/danger,danger...!.au");
                        break;
                    case 2:
                        play(getCodeBase(), "sounds/adapt-or-die.au");
                        break;
                    case 3:
                        play(getCodeBase(), "sounds/cannot.be.completed.au");
                        break;
                }
                onaroll++;
                if(onaroll > 5)
                    getAppletContext().showStatus("You're on your way to THE HALL OF FAME:"
+ onaroll + "Hits!");
                else
                    getAppletContext().showStatus("YOU'RE ON A ROLL:" + onaroll + "Hits!");
            }
            else {
                getAppletContext().showStatus("HIT IT AGAIN! AGAIN!");
                play(getCodeBase(), "sounds/that.hurts.au");
                onaroll = 1;
            }
        }
        else {
            getAppletContext().showStatus("You hit nothing at (" + x + ", " + y + "), exactly\n");
            play(getCodeBase(), "sounds/thin.bell.au");
            onaroll = 0;
        }
        repaint();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        repaint();
    }

    public void mouseExited(MouseEvent e) {
        onaroll = 0;
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Focus methods
     */
    public void keyDown(int key) {
	requestFocus();
	onaroll = 0;
	play(getCodeBase(), "sounds/ip.au");
    }

    public String getAppletInfo() {
        return "Title: JumpingBox\nAuthor: Anonymous";
    }
}
