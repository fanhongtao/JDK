/*
 * @(#)VisualTest.java	1.13 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
import java.awt.*;
import java.applet.*;

class VTest extends Frame {
    boolean	inReshape = false;
    Menu	componentMenu;
    Menu	backgroundMenu;
    Menu	foregroundMenu;
    Menu	shapeMenu;
    Menu	sizeMenu;
    Menu	fontMenu;
    Menu	enableMenu;
    Menu	familyMenu;
    Menu	containerMenu;

    int		currentSize = 10;
    Font	currentFont;
    boolean	enableComponents = true;
    Color	currentForeground;
    Color	currentBackground;
    Container	currentContainer;
    Component	component;
    Font	font10;
    Font	font14;
    Font	font24;
    Font	font36;

    public VTest() {
	super("VTest");

	MenuBar mb = new MenuBar();
	currentContainer = this;
	componentMenu = new Menu("Component");
	componentMenu.add(new MenuItem("Button"));
	componentMenu.add(new MenuItem("Checkbox"));
	componentMenu.add(new MenuItem("Choice"));
	componentMenu.add(new MenuItem("Label"));
	componentMenu.add(new MenuItem("List"));
	componentMenu.add(new MenuItem("Panel"));
	componentMenu.add(new MenuItem("TextArea"));
	componentMenu.add(new MenuItem("TextField"));
	componentMenu.add(new MenuItem("HScrollbar"));
	componentMenu.add(new MenuItem("VScrollbar"));
	mb.add(componentMenu);

	enableMenu = new Menu("Enable/Disable");
	enableMenu.add(new MenuItem("Enable"));
	enableMenu.add(new MenuItem("Disable"));
	mb.add(enableMenu);

	fontMenu = new Menu("Font");
	familyMenu = new Menu("Family");
	familyMenu.add(new MenuItem("Courier"));
	familyMenu.add(new MenuItem("Dialog"));
	familyMenu.add(new MenuItem("TimesRoman"));
	familyMenu.add(new MenuItem("Helvetica"));
	familyMenu.add(new MenuItem("Symbol"));
	fontMenu.add(familyMenu);

	sizeMenu = new Menu("Size");
	sizeMenu.add(new MenuItem("10"));
	font10 = new Font("Helvetica", Font.PLAIN, 10);
	sizeMenu.add(new MenuItem("14"));
	font14 = new Font("Helvetica", Font.PLAIN, 14);
	sizeMenu.add(new MenuItem("24"));
	font24 = new Font("Helvetica", Font.PLAIN, 24);
	sizeMenu.add(new MenuItem("36"));
	font36 = new Font("Helvetica", Font.PLAIN, 36);
	fontMenu.add(sizeMenu);

	mb.add(fontMenu);

	shapeMenu = new Menu("Move/Reshape");
	shapeMenu.add(new CheckboxMenuItem("Move"));
	shapeMenu.add(new CheckboxMenuItem("Reshape"));
	mb.add(shapeMenu);

	foregroundMenu = new Menu("Foreground");
	foregroundMenu.add(new CheckboxMenuItem("default"));
	foregroundMenu.add(new CheckboxMenuItem("red"));
	foregroundMenu.add(new CheckboxMenuItem("green"));
	foregroundMenu.add(new CheckboxMenuItem("blue"));
	mb.add(foregroundMenu);

	backgroundMenu = new Menu("Background");
	backgroundMenu.add(new CheckboxMenuItem("default"));
	backgroundMenu.add(new CheckboxMenuItem("red"));
	backgroundMenu.add(new CheckboxMenuItem("green"));
	backgroundMenu.add(new CheckboxMenuItem("blue"));
	mb.add(backgroundMenu);

	containerMenu = new Menu("Container");
	containerMenu.add(new MenuItem("FlowLayout"));
	containerMenu.add(new MenuItem("GridLayout02"));
	containerMenu.add(new MenuItem("GridLayout20"));
	containerMenu.add(new MenuItem("GridLayout03"));
	containerMenu.add(new MenuItem("GridLayout30"));
	containerMenu.add(new MenuItem("BorderLayout"));
	mb.add(containerMenu);

	setMenuBar(mb);
	setLayout(null);

	currentFont = font10;
	currentForeground = getForeground();
	currentBackground = getBackground();
	enableComponents = true;
	resize(500, 300);
	show();
    }

    public boolean handleEvent(Event e) {
	switch (e.id) {
	  case Event.WINDOW_DESTROY:
	    System.exit(0);
	    return true;
	  case Event.MOUSE_DOWN:
	    currentContainer = this;
	    setCurrentComponent(e.x, e.y);
	    /* fall into next case */
	  case Event.MOUSE_DRAG:
	    if (component != null) {
		if (inReshape) {
		    Rectangle bounds = component.bounds();
		    component.resize(Math.abs(e.x-bounds.x), Math.abs(e.y-bounds.y));
		    component.validate();
		} else {
		    component.move(e.x, e.y);
		}
	    }
	    return true;
	  case Event.MOUSE_UP:
	    currentContainer.validate();
	    return true;
	  default:
	    return super.handleEvent(e);
	}
    }

    void setAttributes(Component c) {
	if (c instanceof Container) {
	    return;
	}
	c.setForeground(currentForeground);
	c.setBackground(currentBackground);
	c.setFont(currentFont);
	if (enableComponents) {
	    c.enable();
	} else {
	    c.disable();
	}
    }

    int computeDistance(int x, int y, Rectangle r) {
	int mx;
	int my;

	mx = x - (r.x + (r.width / 2));
	my = y - (r.y + (r.height / 2));

	return (mx*mx) + (my*my);
    }

    void setCurrentComponent(int x, int y) {
	int n = countComponents();
	int distance = -1;

	for (int i=0; i<n; i++) {
	    Component c = getComponent(i);
	    Rectangle b = c.bounds();
	    int       d;
	    
	    d = computeDistance(x, y, b);
	    if (distance == -1 || d < distance) {
		distance = d;
		component = c;
	    }
	}
    }

    void setAttributes() {
	int n = countComponents();

	for (int i=0; i < n; i++) {
	    setAttributes(getComponent(i));
	}
    }

    public boolean action(Event e, Object arg) {
	if (e.target instanceof MenuItem) {
	    Menu menu = (Menu)(((MenuItem)e.target).getParent());
	    String label = (String)arg;

	    if (menu == backgroundMenu) {
		if (label.equals("red")) {
		    currentBackground = Color.red;
		} else if (label.equals("green")) {
		    currentBackground = Color.green;
		} else if (label.equals("blue")) {
		    currentBackground = Color.blue;
		} else if (label.equals("default")) {
		    currentBackground = Color.lightGray;
		}
	    } else if (menu == foregroundMenu) {
		if (label.equals("red")) {
		    currentForeground = Color.red.darker();
		} else if (label.equals("green")) {
		    currentForeground = Color.green.darker();
		} else if (label.equals("blue")) {
		    currentForeground = Color.blue.darker();
		} else if (label.equals("default")) {
		    currentForeground = Color.black;
		}
	    } else if (menu == shapeMenu) {
		if (label.equals("Move")) {
		    inReshape = false;
		} else if (label.equals("Reshape")) {
		    inReshape = true;
		}
	    } else if (menu == sizeMenu) {
		if (label.equals("10")) {
		    currentFont = font10;
		} else if (label.equals("14")) {
		    currentFont = font14;
		} else if (label.equals("24")) {
		    currentFont = font24;
		} else if (label.equals("36")) {
		    currentFont = font36;
		}
	    } else if (menu == familyMenu) {
		font10 = new Font(label, Font.PLAIN, 10);
		font14 = new Font(label, Font.PLAIN, 14);
		font24 = new Font(label, Font.PLAIN, 24);
		font36 = new Font(label, Font.PLAIN, 36);
		switch (currentSize) {
		  case 10:
		  default:
		    currentFont = font10;
		    break;
		  case 14:
		    currentFont = font14;
		    break;
		  case 24:
		    currentFont = font24;
		    break;
		  case 36:
		    currentFont = font36;
		    break;
		}
	    } else if (menu == enableMenu) {
		if (label.equals("Enable")) {
		    enableComponents = true;
		} else if (label.equals("Disable")) {
		    enableComponents = false;
		}
	    } else if (menu == componentMenu) {
		Component component;

		if (label.equalsIgnoreCase("Button")) {
		    component = new Button("Button");
		} else if (label.equalsIgnoreCase("Label")) {
		    component = new Label("label");
		} else if (label.equalsIgnoreCase("TextField")) {
		    component = new TextField("textfield");
		} else if (label.equalsIgnoreCase("Choice")) {
		    component = new Choice();
		    ((Choice)component).addItem("Choice");
		} else if (label.equalsIgnoreCase("List")) {
		    component = new List(4, false);
		    ((List)component).addItem("List1");
		    ((List)component).addItem("List2");
		    ((List)component).addItem("List3");
		    ((List)component).addItem("List4");
		    ((List)component).addItem("List5");
		    currentContainer.add(component);
		} else if (label.equalsIgnoreCase("TextArea")) {
		    component = new TextArea(5, 15);
		    ((TextArea)component).setText("TextArea");
		} else if (label.equalsIgnoreCase("Checkbox")) {
		    component = new Checkbox("Checkbox");
		} else if (label.equalsIgnoreCase("Panel")) {
		    component = new VPanel(this);
		} else if (label.equalsIgnoreCase("HScrollbar")) {
		    component = new Scrollbar(Scrollbar.HORIZONTAL);
		} else if (label.equalsIgnoreCase("VScrollbar")) {
		    component = new Scrollbar(Scrollbar.VERTICAL);
		} else {
		    component = new Button("Button");
		}
		if (! (component instanceof Container)) {
		    Dimension d = component.preferredSize();
		    component.reshape(10, 10, d.width, d.height);
		}
		currentContainer.add(component);
		currentContainer.validate();
	    } else if (menu == containerMenu) {
		if (currentContainer != this) {
		    if (label.equalsIgnoreCase("FlowLayout")) {
			currentContainer.setLayout(new FlowLayout());
		    } else if (label.equalsIgnoreCase("GridLayout02")) {
			currentContainer.setLayout(new GridLayout(0,2));
		    } else if (label.equalsIgnoreCase("GridLayout20")) {
			currentContainer.setLayout(new GridLayout(2,0));
		    } else if (label.equalsIgnoreCase("GridLayout03")) {
			currentContainer.setLayout(new GridLayout(0,3));
		    } else if (label.equalsIgnoreCase("GridLayout30")) {
			currentContainer.setLayout(new GridLayout(3, 0));
		    } else if (label.equalsIgnoreCase("BorderLayout")) {
			currentContainer.setLayout(new BorderLayout());
			Component comp1;
			Component comp2;
			Component comp3;
			Component comp4;
			Component comp5;
			switch (currentContainer.countComponents()) {
			  case 1:
			    comp1 = currentContainer.getComponent(0);
			    currentContainer.remove(comp1);
			    currentContainer.add("Center", comp1);
			    break;
			  case 2:
			    comp1 = currentContainer.getComponent(0);
			    comp2 = currentContainer.getComponent(1);

			    currentContainer.remove(comp1);
			    currentContainer.remove(comp2);

			    currentContainer.add("North", comp1);
			    currentContainer.add("Center", comp2);
			    break;
			  case 3:
			    comp1 = currentContainer.getComponent(0);
			    comp2 = currentContainer.getComponent(1);
			    comp3 = currentContainer.getComponent(2);
			    currentContainer.remove(comp1);
			    currentContainer.remove(comp2);
			    currentContainer.remove(comp3);

			    currentContainer.add("North", comp1);
			    currentContainer.add("South", comp2);
			    currentContainer.add("Center", comp3);
			    break;
			  case 4:
			    comp1 = currentContainer.getComponent(0);
			    comp2 = currentContainer.getComponent(1);
			    comp3 = currentContainer.getComponent(2);
			    comp4 = currentContainer.getComponent(3);

			    currentContainer.remove(comp1);
			    currentContainer.remove(comp2);
			    currentContainer.remove(comp3);
			    currentContainer.remove(comp4);

			    currentContainer.add("North", comp1);
			    currentContainer.add("South", comp2);
			    currentContainer.add("East", comp3);
			    currentContainer.add("Center", comp4);
			    break;
			  case 5:
			  default:
			    comp1 = currentContainer.getComponent(0);
			    comp2 = currentContainer.getComponent(1);
			    comp3 = currentContainer.getComponent(2);
			    comp4 = currentContainer.getComponent(3);
			    comp5 = currentContainer.getComponent(4);

			    currentContainer.remove(comp1);
			    currentContainer.remove(comp2);
			    currentContainer.remove(comp3);
			    currentContainer.remove(comp4);
			    currentContainer.remove(comp5);

			    currentContainer.add("North", comp1);
			    currentContainer.add("South", comp2);
			    currentContainer.add("East", comp3);
			    currentContainer.add("West", comp4);
			    currentContainer.add("Center", comp5);
			    break;
			}
		    }
		    currentContainer.validate();
		}
	    }
	    setAttributes();
	    return true;
	}
	return false;
    }
}

public class VisualTest extends Applet {
    public void init() {
	new VTest();
    }

    public static void main(String args[]) {
	Frame f = new Frame("VisualTest");
	VisualTest visualtest = new VisualTest();

	visualtest.init();
	visualtest.start();

	f.add("Center", visualtest);
    }
}

class VPanel extends Panel {
    VTest	target;

    public VPanel(VTest target) {
	this.target = target;
	setBackground(target.getBackground().darker());
	resize(100, 100);
    }

    public boolean mouseDown(Event evt, int x, int y) {
	target.currentContainer = this;
	target.containerMenu.enable();
	return true;
    }
}

