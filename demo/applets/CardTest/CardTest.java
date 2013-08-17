/*
 * @(#)CardTest.java	1.6 99/07/12
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
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

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

class CardPanel extends Panel {
    ActionListener listener;

    Panel create(LayoutManager layout) {
	Button b = null;
	Panel p = new Panel();

	p.setLayout(layout);

	b = new Button("one");
	b.addActionListener(listener);
	p.add("North", b);

	b = new Button("two");
	b.addActionListener(listener);
	p.add("West", b);

	b = new Button("three");
	b.addActionListener(listener);
	p.add("South", b);

	b = new Button("four");
	b.addActionListener(listener);
	p.add("East", b);

	b = new Button("five");
	b.addActionListener(listener);
	p.add("Center", b);

	b = new Button("six");
	b.addActionListener(listener);
	p.add("Center", b);

	return p;
    }

    CardPanel(ActionListener actionListener) {
	listener = actionListener;
	setLayout(new CardLayout());
	add("one", create(new FlowLayout()));
	add("two", create(new BorderLayout()));
	add("three", create(new GridLayout(2, 2)));
	add("four", create(new BorderLayout(10, 10)));
	add("five", create(new FlowLayout(FlowLayout.LEFT, 10, 10)));
	add("six", create(new GridLayout(2, 2, 10, 10)));
    }

    public Dimension getPreferredSize() {
	return new Dimension(200, 100);
    }
}

public class CardTest extends Applet
		      implements ActionListener,
				 ItemListener {
    CardPanel cards;

    public CardTest() {
	setLayout(new BorderLayout());
	add("Center", cards = new CardPanel(this));
	Panel p = new Panel();
	p.setLayout(new FlowLayout());
	add("South", p);

	Button b = new Button("first");
	b.addActionListener(this);
	p.add(b);

	b = new Button("next");
	b.addActionListener(this);
	p.add(b);

	b = new Button("previous");
	b.addActionListener(this);
	p.add(b);

	b = new Button("last");
	b.addActionListener(this);
	p.add(b);

	Choice c = new Choice();
	c.addItem("one");
	c.addItem("two");
	c.addItem("three");
	c.addItem("four");
	c.addItem("five");
	c.addItem("six");
	c.addItemListener(this);
	p.add(c);
    }

    public void itemStateChanged(ItemEvent e) {
	((CardLayout)cards.getLayout()).show(cards,
	                                     (String)(e.getItem()));
    }

    public void actionPerformed(ActionEvent e) {
	String arg = e.getActionCommand();

	if ("first".equals(arg)) {
	    ((CardLayout)cards.getLayout()).first(cards);
	} else if ("next".equals(arg)) {
	    ((CardLayout)cards.getLayout()).next(cards);
	} else if ("previous".equals(arg)) {
	    ((CardLayout)cards.getLayout()).previous(cards);
	} else if ("last".equals(arg)) {
	    ((CardLayout)cards.getLayout()).last(cards);
	} else {
	    ((CardLayout)cards.getLayout()).show(cards,(String)arg);
	}
    }

    public static void main(String args[]) {
	Frame f = new Frame("CardTest");
	CardTest cardTest = new CardTest();
	cardTest.init();
	cardTest.start();

	f.add("Center", cardTest);
	f.setSize(300, 300);
	f.show();
    }
    
    public String getAppletInfo() {
        return "Demonstrates the different types of layout managers.";
    }
}
