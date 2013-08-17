/*
 * @(#)ToolTipPanel.java	1.20 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.lang.*;

/*
 * @version 1.20 11/29/01
 * @author Jeff Dinkins
 * @author Peter Korn (accessibility support)
 */
public class ToolTipPanel extends JPanel {
    SwingSet swing;

    public ToolTipPanel(SwingSet swing) {
	this.swing = swing;
	loadCow();
    }

    public void loadCow() {
        setBackground(Color.white);
	setBorder(swing.etchedBorder10);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	Cow cow = new Cow();
	cow.setToolTipText("Cow.");
	cow.getAccessibleContext().setAccessibleName("Cow image");
	add(Box.createRigidArea(new Dimension(1, 40)));
	add(cow);
    }

    class Cow extends JLabel {
	Polygon cowgon = new Polygon();

        public Cow() {
 	    super(SwingSet.sharedInstance().loadImageIcon("images/cow.gif","Black and white cow"));
	    setAlignmentX(CENTER_ALIGNMENT);
	    cowgon.addPoint(3,20);    cowgon.addPoint(44,4);
	    cowgon.addPoint(79,15);   cowgon.addPoint(130,11);
	    cowgon.addPoint(252,5);   cowgon.addPoint(181,17);
	    cowgon.addPoint(301,45);  cowgon.addPoint(292,214);
	    cowgon.addPoint(269,209); cowgon.addPoint(266,142);
	    cowgon.addPoint(250,161); cowgon.addPoint(235,218);
	    cowgon.addPoint(203,206); cowgon.addPoint(215,137);
	    cowgon.addPoint(195,142); cowgon.addPoint(143,132);
	    cowgon.addPoint(133,189); cowgon.addPoint(160,200);
	    cowgon.addPoint(97,196);  cowgon.addPoint(107,182);
	    cowgon.addPoint(118,185); cowgon.addPoint(110,144);
	    cowgon.addPoint(59,77);   cowgon.addPoint(30,82);
	    cowgon.addPoint(30,35);   cowgon.addPoint(15,36);
        }

	boolean moo = false;
	boolean milk = false;
	boolean tail = false;
	public boolean contains(int x, int y) {
	    if((x > 30) && (x < 60) && (y > 60) && (y < 85)) {
	        if(!moo) {
	           setToolTipText("<html><center><font color=blue size=+3>Mooooo</font></center></html>");
		   moo = true;
		   milk = false;
		   tail = false;
		}
	    } else if((x > 150) && (x < 230) && (y > 90) && (y < 145)) {
	        if(!milk) {
	           setToolTipText("<html><center> Got  <font face=AvantGarde size=+1 color=white>Milk? </font></center></html>");
		   milk = true;
		   moo = false;
		   tail = false;
		}
	    } else if((x > 280) && (x < 300) && (y > 20) && (y < 175)) {
	        if(!tail) {
	           setToolTipText("<html><em><b>Tail.</b></em></html>");
		   tail = true;
		   moo = false;
		   milk = false;
		}
	    } else if(moo || milk || tail) {
	        setToolTipText("<html>In case you thought that tooltips had to be<p>boring, one line descriptions, the <font color=blue size=+2>Swing!</font> team<p> is happy to shatter your illusions.<p>In Swing, they can use HTML to <ul><li>Have Lists<li><b>Bold</b> text<li><em>emphasized</em> text<li>text with <font color=red>Color</font><li>text in different <font size=+3>sizes</font><li>and <font face=AvantGarde>Fonts</font></ul>Oh, and they can be multi-line, too.</html>");
		moo = false;
		tail = false;
		milk = false;
	    }
	    if(cowgon.contains(new Point(x, y))) {
		return true;
	    } else {
		return false;
	    }
	}
    }

    public void itsEaster(boolean b) {
	if(!b) {
	   removeAll();
	   loadCow();
	} else {
	   removeAll();
	   setBackground(Color.black);
	   setLayout(new BorderLayout());
	   Easter easter = new Easter();
	   add(easter, BorderLayout.CENTER);
	   invalidate();
	   validate();
	   easter.go();
	}
    }

    class Easter extends JComponent implements ActionListener {
	Timer animator;

	private ImageIcon amy     = SwingSet.sharedInstance().loadImageIcon("images/people/amy.gif", "Amy Fowler");
	private ImageIcon jag     = SwingSet.sharedInstance().loadImageIcon("images/people/jag.gif", "James Gosling");
	private ImageIcon jeff    = SwingSet.sharedInstance().loadImageIcon("images/people/jeff.gif", "Jeff Dinkins");
	private ImageIcon tim     = SwingSet.sharedInstance().loadImageIcon("images/people/tim.gif", "Tim Prinzing");
	private ImageIcon tom     = SwingSet.sharedInstance().loadImageIcon("images/people/tom.gif", "Tom Ball");
        private ImageIcon rick    = SwingSet.sharedInstance().loadImageIcon("images/people/rick.gif", "Rick Levenson");
        private ImageIcon hans1   = SwingSet.sharedInstance().loadImageIcon("images/people/mathew.gif", "Mathew Muller");
        private ImageIcon hans2   = SwingSet.sharedInstance().loadImageIcon("images/people/pl.gif", "Project Lead");
        private ImageIcon ges     = SwingSet.sharedInstance().loadImageIcon("images/people/ges.gif", "Georges Saab");
        private ImageIcon phil    = SwingSet.sharedInstance().loadImageIcon("images/people/phil.gif", "Phillip Milne");
        private ImageIcon arnaud  = SwingSet.sharedInstance().loadImageIcon("images/people/nathan.gif", "Arnaud Webber");        
        private ImageIcon rich    = SwingSet.sharedInstance().loadImageIcon("images/people/rich.gif", "Rich Schiavi");

	int tmpScale;
	
	private double x1 = 0;
	private double y1 = 0;

	private double x2 = 0;
	private double y2 = 0;
	
	private int xAmy = 0;
	private int xJag = 0;
	private int xJeff = 0;
	private int xTim = 0;
	private int xTom = 0;
	private int xRick = 0;
	private int xHans = 0;
        private int xGes = 0;
        private int xPhil = 0;
        private int xArnaud = 0;
        private int xRich = 0;
	
	private int yAmy = 0;
	private int yJag = 0;
	private int yJeff = 0;
	private int yTim = 0;
	private int yTom = 0;
	private int yRick = 0;
	private int yHans = 0;
	private int yGes = 0;
	private int yPhil = 0;
	private int yArnaud = 0;
	private int yRich = 0;

	public Easter() {
	}

	public void go() {
	    animator = new Timer(22 + 22 + 22, this);
	    animator.start();
	}

	public void paint(Graphics g) {
	    // The code may be goofy, but the code be havin' some fun.
	    g.setColor(getParent().getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());

	    // Unless your name is Jeff Dinkins, don't muck with this. (-:
	    tmpScale = (int) (Math.abs(Math.sin(x1+00)) * 10); double jeffScale   = (double) tmpScale / 10;
	    tmpScale = (int) (Math.abs(Math.sin(x1+10)) * 10); double timScale    = (double) tmpScale / 10;
	    tmpScale = (int) (Math.abs(Math.sin(x1+20)) * 10); double tomScale    = (double) tmpScale / 10;
	    tmpScale = (int) (Math.abs(Math.sin(x1+30)) * 10); double jagScale    = (double) tmpScale / 10;
	    tmpScale = (int) (Math.abs(Math.sin(x1+40)) * 11); double amyScale    = (double) tmpScale / 10;
	    tmpScale = (int) (Math.abs(Math.sin(x1+50)) * 11); double rickScale   = (double) tmpScale / 10;
	    tmpScale = (int) (Math.abs(Math.sin(x2+60)) * 11); double hansScale   = (double) tmpScale / 10;
	    tmpScale = (int) (Math.abs(Math.sin(x2+70)) * 10); double gesScale    = (double) tmpScale / 10;
	    tmpScale = (int) (Math.abs(Math.sin(x1+10)) * 10); double philScale   = (double) tmpScale / 10;
	    tmpScale = (int) (Math.abs(Math.sin(x1+90)) * 10); double arnaudScale = (double) tmpScale / 10;
            tmpScale = (int) (Math.abs(Math.sin(x1+80)) * 10); double richScale   = (double) tmpScale / 10;
	    
	    x1 +=.1;
	    x2 +=.065;
	    int nudgeX = (int) (((double) getWidth()/2) * .8);
	    xTom       = (int) (Math.sin(x1+00) * nudgeX) + nudgeX;
            xAmy       = (int) (Math.sin(x1+10) * nudgeX) + nudgeX;
	    xGes       = (int) (Math.sin(x2+20) * nudgeX) + nudgeX;
            xRick      = (int) (Math.sin(x1+30) * nudgeX) + nudgeX;
	    xJeff      = (int) (Math.sin(x1+40) * nudgeX) + nudgeX;
            xPhil      = (int) (Math.sin(x1+51) * nudgeX) + nudgeX;          
	    xTim       = (int) (Math.sin(x1+60) * nudgeX) + nudgeX;
	    xRich      = (int) (Math.sin(x1+65) * nudgeX) + nudgeX;
            xArnaud    = (int) (Math.sin(x1+70) * nudgeX) + nudgeX;
            xHans      = (int) (Math.sin(x2+85) * nudgeX) + nudgeX;
	    xJag       = (int) (Math.sin(x1+90) * nudgeX) + nudgeX;
	    
	    y1 +=.1;
	    y2 +=.05;
	    int nudgeY    = (int) (((double) getHeight()/2) * .60);
	    int nudgeMe   = (int) (((double) getHeight()/2) * .45);
	    int nudgePhil = (int) (((double) getHeight()/2) * .20);
	    yTom          = (int) (Math.sin(y1+00) * nudgeY)    + nudgeY;
	    yTim          = (int) (Math.sin(y1+10) * nudgeY)    + nudgeY;
	    yRich         = (int) (Math.sin(y1+15) * nudgeY)    + nudgeY;
	    yJeff         = (int) (Math.sin(y1+20) * nudgeMe)   + nudgeY;
            yHans         = (int) (Math.sin(y2+33) * nudgeY)    + nudgeY;
            yPhil         = (int) (Math.sin(y1+45) * nudgePhil) + nudgeY;
            yAmy          = (int) (Math.sin(y1+50) * nudgeY)    + nudgeY;
            yArnaud       = (int) (Math.sin(y1+60) * nudgeY)    + nudgeY;
            yGes          = (int) (Math.sin(y2+70) * nudgeY)    + nudgeY;
            yRick         = (int) (Math.sin(y1+80) * nudgeMe)   + nudgeY;
	    yJag          = (int) (Math.sin(y1+90) * nudgeY)    + nudgeY;

	    // Don't modify this - the order matters!
	    if(x1 > 30) squish(g, arnaud, xArnaud, yArnaud, arnaudScale);
	    if(x1 > 15) squish(g, phil,   xPhil,   yPhil,   philScale);
	    if(x1 >  9) squish(g, rick,   xRick,   yRick,   rickScale);
	    if(x1 > 27) squish(g, tim,    xTim,    yTim,    timScale);
	    if(x1 > 12) squish(g, tom,    xTom,    yTom,    tomScale);
            if(x1 > 18) squish(g, rich, xRich, yRich, richScale);
	    if(x1 > 33) {
		if(hansScale > .65) {
		    squish(g, hans1,   xHans,   yHans,   hansScale);
		} else {
		    squish(g, hans2,   xHans,   yHans,   hansScale);
		}
	    }
	    if(x1 > 21) squish(g, amy,    xAmy,    yAmy,    amyScale);
	    if(x1 > 6) squish(g, jag,    xJag,    yTom,    jagScale);
	    squish(g, jeff,   xJeff,   yJeff,   jeffScale);
	    if(x1 > 3) squish(g, ges,    xGes,    yGes,    gesScale);

	}

	public void squish(Graphics g, ImageIcon icon, int x, int y, double scale) {
	    if(isVisible()) {
		g.drawImage(icon.getImage(),
			    x, y,
			    (int) (icon.getIconWidth()*scale),
			    (int) (icon.getIconHeight()*scale),
			    this);
	    }
	}

	public void actionPerformed(ActionEvent e) {
	    if(isVisible()) {
		repaint();
	    } else {
		animator.stop();
	    }
	}
    }

}
