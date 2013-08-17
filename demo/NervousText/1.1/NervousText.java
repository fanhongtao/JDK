/*
 * @(#)NervousText.java	1.1 97/04/01
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
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

/*  Daniel Wyszynski 
    Center for Applied Large-Scale Computing (CALC) 
    04-12-95 

    Test of text animation.

    kwalrath: Changed string; added thread suspension. 5-9-95
*/
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Font;

public class NervousText extends java.applet.Applet implements Runnable, MouseListener {

	char separated[];
	String s = null;
	Thread killme = null;
	int i;
	int x_coord = 0, y_coord = 0;
	String num;
	int speed=35;
	int counter =0;
	boolean threadSuspended = false; //added by kwalrath

public void init() {
	addMouseListener(this);
	s = getParameter("text");
	if (s == null) {
	    s = "HotJava";
	}

	separated =  new char [s.length()];
	s.getChars(0,s.length(),separated,0);
	resize((s.length()+1)*15, 50);
	setFont(new Font("TimesRoman",Font.BOLD,36));
 }

public void start() {
	if(killme == null) 
	{
        killme = new Thread(this);
        killme.start();
	}
 }

public void stop() {
	killme = null;
 }

public void run() {
	while (killme != null) {
	try {Thread.sleep(100);} catch (InterruptedException e){}
	repaint();
	}
	killme = null;
 }

public void paint(Graphics g) {
	for(i=0;i<s.length();i++)
	{
	x_coord = (int) (Math.random()*10+15*i);
	y_coord = (int) (Math.random()*10+36);
	g.drawChars(separated, i,1,x_coord,y_coord);
	}
 }

  public void mousePressed(MouseEvent e) {
    e.consume();
    if (threadSuspended) {
      killme.resume();
    }
    else {
      killme.suspend();
    }
    threadSuspended = !threadSuspended;
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseClicked(MouseEvent e) {
  }
}
