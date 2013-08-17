/*
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted.
 * Please refer to the file http://java.sun.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://java.sun.com/licensing.html for further important licensing
 * information for the Java (tm) Technology.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */

// author: Rachel Gollub, 1995
// modified 96/04/24 Jim Hagen : use getBackground()
// modified 96/05/29 Rachel Gollub : add garbage collecting
// modified 96/10/22 Rachel Gollub : add bgcolor, fgcolor1, fgcolor2 params
// Time!

import java.util.*;
import java.awt.*;
import java.applet.*;

public class Clock2 extends Applet implements Runnable {
  Thread timer = null;
  int lastxs=0, lastys=0, lastxm=0, lastym=0, lastxh=0, lastyh=0;
  Date dummy = new Date();
  String lastdate = dummy.toLocaleString();
  Font F = new Font("TimesRoman", Font.PLAIN, 14);
  Date dat = null;
  Color fgcol = Color.blue;
  Color fgcol2 = Color.darkGray;

public void init() {
  int x,y;

  try {
    setBackground(new Color(Integer.parseInt(getParameter("bgcolor"),16)));
  } catch (Exception E) { }
  try {
    fgcol = new Color(Integer.parseInt(getParameter("fgcolor1"),16));
  } catch (Exception E) { }
  try {
    fgcol2 = new Color(Integer.parseInt(getParameter("fgcolor2"),16));
  } catch (Exception E) { }
  resize(300,300);              // Set clock window size
}

  // Plotpoints allows calculation to only cover 45 degrees of the circle,
  // and then mirror

public void plotpoints(int x0, int y0, int x, int y, Graphics g) {

  g.drawLine(x0+x,y0+y,x0+x,y0+y);
  g.drawLine(x0+y,y0+x,x0+y,y0+x);
  g.drawLine(x0+y,y0-x,x0+y,y0-x);
  g.drawLine(x0+x,y0-y,x0+x,y0-y);
  g.drawLine(x0-x,y0-y,x0-x,y0-y);
  g.drawLine(x0-y,y0-x,x0-y,y0-x);
  g.drawLine(x0-y,y0+x,x0-y,y0+x);
  g.drawLine(x0-x,y0+y,x0-x,y0+y);
}

  // Circle is just Bresenham's algorithm for a scan converted circle

public void circle(int x0, int y0, int r, Graphics g) {
  int x,y;
  float d;

  x=0;
  y=r;
  d=5/4-r;
  plotpoints(x0,y0,x,y,g);

  while (y>x){
    if (d<0) {
      d=d+2*x+3;
      x++;
    }
    else {
      d=d+2*(x-y)+5;
      x++;
      y--;
    }
    plotpoints(x0,y0,x,y,g);
  }
}


  // Paint is the main part of the program

public void paint(Graphics g) {
  int xh, yh, xm, ym, xs, ys, s, m, h, xcenter, ycenter;
  String today;

  dat = new Date();
  s = dat.getSeconds();
  m = dat.getMinutes();
  h = dat.getHours();
  today = dat.toLocaleString();
  xcenter=80;
  ycenter=55;
  
  // a= s* pi/2 - pi/2 (to switch 0,0 from 3:00 to 12:00)
  // x = r(cos a) + xcenter, y = r(sin a) + ycenter
  
  xs = (int)(Math.cos(s * 3.14f/30 - 3.14f/2) * 45 + xcenter);
  ys = (int)(Math.sin(s * 3.14f/30 - 3.14f/2) * 45 + ycenter);
  xm = (int)(Math.cos(m * 3.14f/30 - 3.14f/2) * 40 + xcenter);
  ym = (int)(Math.sin(m * 3.14f/30 - 3.14f/2) * 40 + ycenter);
  xh = (int)(Math.cos((h*30 + m/2) * 3.14f/180 - 3.14f/2) * 30 + xcenter);
  yh = (int)(Math.sin((h*30 + m/2) * 3.14f/180 - 3.14f/2) * 30 + ycenter);
  
  // Draw the circle and numbers
  
  g.setFont(F);
  g.setColor(fgcol);
  circle(xcenter,ycenter,50,g);
  g.setColor(fgcol2);
  g.drawString("9",xcenter-45,ycenter+3); 
  g.drawString("3",xcenter+40,ycenter+3);
  g.drawString("12",xcenter-5,ycenter-37);
  g.drawString("6",xcenter-3,ycenter+45);

  // Erase if necessary, and redraw
  
  g.setColor(getBackground());
  if (xs != lastxs || ys != lastys) {
    g.drawLine(xcenter, ycenter, lastxs, lastys);
    g.drawString(lastdate, 5, 125);
  }
  if (xm != lastxm || ym != lastym) {
    g.drawLine(xcenter, ycenter-1, lastxm, lastym);
    g.drawLine(xcenter-1, ycenter, lastxm, lastym); }
  if (xh != lastxh || yh != lastyh) {
    g.drawLine(xcenter, ycenter-1, lastxh, lastyh);
    g.drawLine(xcenter-1, ycenter, lastxh, lastyh); }
  g.setColor(fgcol2);
  g.drawString(today, 5, 125);  
  g.drawLine(xcenter, ycenter, xs, ys);
  g.setColor(fgcol);
  g.drawLine(xcenter, ycenter-1, xm, ym);
  g.drawLine(xcenter-1, ycenter, xm, ym);
  g.drawLine(xcenter, ycenter-1, xh, yh);
  g.drawLine(xcenter-1, ycenter, xh, yh);
  lastxs=xs; lastys=ys;
  lastxm=xm; lastym=ym;
  lastxh=xh; lastyh=yh;
  lastdate = today;
  dat=null;
}

public void start() {
  if(timer == null)
    {
      timer = new Thread(this);
      timer.start();
    }
}

public void stop() {
  timer = null;
}

public void run() {
  while (timer != null) {
    try {Thread.sleep(100);} catch (InterruptedException e){}
    repaint();
  }
  timer = null;
}

public void update(Graphics g) {
  paint(g);
}
}

