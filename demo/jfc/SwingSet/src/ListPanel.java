/*
 * @(#)ListPanel.java	1.10 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * ListBox!
 *
 * @version 1.10 11/29/01
 * @author Jeff Dinkins
 */
public class ListPanel extends JPanel
{
    int fastfoodIndex;
    int dessertIndex;
    int fruitIndex;
    int veggieIndex;

    boolean fastfoodShown;
    boolean dessertShown;
    boolean fruitShown;
    boolean veggieShown;

    // Fast Food
    public ImageIcon burger    = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/burger.gif","burger");
    public ImageIcon fries     = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/fries.gif","fries");
    public ImageIcon softdrink = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/softdrink.gif","soft drink");
    public ImageIcon hotdog    = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/hotdog.gif","hot dog");
    public ImageIcon pizza     = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/pizza.gif","pizza");

    // Dessert
    public ImageIcon icecream = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/icecream.gif","ice cream");
    public ImageIcon pie      = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/pie.gif","pie");
    public ImageIcon cake     = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/cake.gif","cake");
    public ImageIcon donut    = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/donut.gif","donut");
    public ImageIcon treat    = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/treat.gif","treat");

    // Fruit
    public ImageIcon grapes      = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/grapes.gif","grapes");
    public ImageIcon banana      = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/banana.gif","banana");
    public ImageIcon watermelon  = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/watermelon.gif","watermelon");
    public ImageIcon cantaloupe  = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/cantaloupe.gif","cantaloupe");
    public ImageIcon peach       = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/peach.gif","peach");

    // Veggie
    public ImageIcon broccoli = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/broccoli.gif","broccoli");
    public ImageIcon carrot   = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/carrot.gif","carrot");
    public ImageIcon peas     = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/peas.gif","peas");
    public ImageIcon corn     = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/corn.gif","corn");
    public ImageIcon radish   = SwingSet.sharedInstance().loadImageIcon("images/ImageClub/food/radish.gif","radish");


    // The Frame
    SwingSet swing;
    JList listBox;
    JScrollPane scrollPane;

    DefaultListModel model = new DefaultListModel();
    JLabel priceLabel;
    int listPrice = 0;

    JButton reset;
    JButton purchase;

    JRadioButton dessertRadioButton;
    JRadioButton veggieRadioButton;
    JRadioButton fruitRadioButton;
    JRadioButton fastfoodRadioButton;

    JCheckBox dessertCheckbox;
    JCheckBox veggieCheckbox;
    JCheckBox fruitCheckbox;
    JCheckBox fastfoodCheckbox;

    public ListPanel(SwingSet swing) {
	this.swing = swing;

	setBorder(swing.emptyBorder5);
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	// create the list
	for(int i = 0; i < ITEMS; i++) {
            model.addElement(new Integer(i));
        }

	listBox = new JList(model) {
	    public Dimension getMaximumSize() {
		return new Dimension(400, super.getMaximumSize().height);
	    }
	};
        listBox.setCellRenderer(new TestCellRenderer(listBox));

	// Create the controls
	JPanel controlPanel = new JPanel() {
	    public Dimension getMaximumSize() {
		return new Dimension(300, super.getMaximumSize().height);
	    }
	};
	controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
	controlPanel.setBorder(swing.loweredBorder);
	controlPanel.setAlignmentY(TOP_ALIGNMENT);

	// List operations

	JPanel pricePanel = swing.createHorizontalPanel(false);
	pricePanel.setAlignmentY(TOP_ALIGNMENT);
	pricePanel.setAlignmentX(LEFT_ALIGNMENT);
	controlPanel.add(pricePanel);
	purchase = new JButton("Purchase");
	purchase.setToolTipText("Adds the selected item(s) to your grocery bill.");
	pricePanel.add(purchase);
	pricePanel.add(Box.createRigidArea(swing.hpad10));

	priceLabel = new JLabel("Total:                           ");
	pricePanel.add(priceLabel);

	controlPanel.add(Box.createRigidArea(swing.vpad20));
	JLabel l = new JLabel("Jump To:");
	l.setFont(swing.boldFont);
	controlPanel.add(l);
	ButtonGroup group = new ButtonGroup();

	fastfoodRadioButton = new JRadioButton("Fast Food");
	fastfoodRadioButton.setToolTipText("Calls list.ensureVisible() to jump to the items.");
	group.add(fastfoodRadioButton);
	controlPanel.add(fastfoodRadioButton);

	dessertRadioButton = new JRadioButton("Desserts");
	dessertRadioButton.setToolTipText("Calls list.ensureVisible() to jump to the items.");
	group.add(dessertRadioButton);
	controlPanel.add(dessertRadioButton);

	fruitRadioButton = new JRadioButton("Fruits");
	fruitRadioButton.setToolTipText("Calls list.ensureVisible() to jump to the items.");
	group.add(fruitRadioButton);
	controlPanel.add(fruitRadioButton);

	veggieRadioButton = new JRadioButton("Vegetables");
	veggieRadioButton.setToolTipText("Calls list.ensureVisible(index) to jump to the items.");
	group.add(veggieRadioButton);
	controlPanel.add(veggieRadioButton);

	controlPanel.add(Box.createRigidArea(swing.vpad20));
	l = new JLabel("Show:");
	l.setFont(swing.boldFont);
	controlPanel.add(l);
	fastfoodCheckbox = new JCheckBox("Fast Food");
	fastfoodCheckbox.setToolTipText("Calls list.remove(index1,indexN) to remove items.");
	fastfoodCheckbox.setSelected(true);
	controlPanel.add(fastfoodCheckbox);

	dessertCheckbox = new JCheckBox("Desserts");
	dessertCheckbox.setToolTipText("Calls list.remove(index1,indexN) to remove items.");
	dessertCheckbox.setSelected(true);
	controlPanel.add(dessertCheckbox);

	fruitCheckbox = new JCheckBox("Fruits");
	fruitCheckbox.setToolTipText("Calls list.remove(index1,indexN) to remove items.");
	fruitCheckbox.setSelected(true);
	controlPanel.add(fruitCheckbox);

	veggieCheckbox = new JCheckBox("Vegetables");
	veggieCheckbox.setToolTipText("Calls list.remove(index1,indexN) to remove items.");
	veggieCheckbox.setSelected(true);
	controlPanel.add(veggieCheckbox);

	controlPanel.add(Box.createGlue());
	reset = new JButton("Reset");
	reset.setToolTipText("Resets the state of the demo.");
	controlPanel.add(reset);


	scrollPane = new JScrollPane(listBox);
	scrollPane.setAlignmentX(LEFT_ALIGNMENT);
	scrollPane.setAlignmentY(TOP_ALIGNMENT);
	add(scrollPane);
	add(Box.createRigidArea(swing.hpad10));
 	add(controlPanel);

	ActionListener purchaseListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
		int first = listBox.getMinSelectionIndex();
                int last = listBox.getMaxSelectionIndex();
		if(first < 0) {
		    return;
		}
		for(int i = first; i <= last; i++) {
		    Integer item = (Integer) model.getElementAt(i);
		    listPrice += price[item.intValue()];
		}
		priceLabel.setText("Total: $" + ((double) listPrice)/100.0);
		priceLabel.repaint();
	    }
	};
	purchase.addActionListener(purchaseListener);

	ActionListener showListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
		JCheckBox cb = (JCheckBox) e.getSource();
		String label = cb.getText();
		if(!cb.isSelected()) {

		    if(label.equals("Fast Food")) {
			fastfoodShown = false;
			for(int i = fastfoodIndex; i < fastfoodIndex+5; i++) {
			    model.removeElementAt(fastfoodIndex);
			}
			fastfoodRadioButton.setEnabled(false);
			dessertIndex -= 5;
			fruitIndex -= 5;
			veggieIndex -= 5;
			scrollPane.validate();
		    } else if(label.equals("Desserts")) {
			for(int i = dessertIndex; i < dessertIndex+5; i++) {
			    model.removeElementAt(dessertIndex);
			}
			dessertRadioButton.setEnabled(false);
			fruitIndex -= 5;
			veggieIndex -= 5;
			scrollPane.validate();
		    } else if(label.equals("Fruits")) {
			for(int i = fruitIndex; i < fruitIndex+5; i++) {
			    model.removeElementAt(fruitIndex);
			}
			fruitRadioButton.setEnabled(false);
			veggieIndex -= 5;
			scrollPane.validate();
		    } else if(label.equals("Vegetables")) {
			for(int i = veggieIndex; i < veggieIndex+5; i++) {
			    model.removeElementAt(veggieIndex);
			}
			veggieRadioButton.setEnabled(false);
			scrollPane.validate();
		    }
		    if(model.getSize() < 1)
		        listBox.getParent().repaint();
		} else {
		    if(label.equals("Fast Food")) {
			model.insertElementAt(new Integer(4), 0);
			model.insertElementAt(new Integer(3), 0);
			model.insertElementAt(new Integer(2), 0);
			model.insertElementAt(new Integer(1), 0);
			model.insertElementAt(new Integer(0), 0);
			dessertIndex += 5;
			fruitIndex += 5;
			veggieIndex += 5;
			fastfoodRadioButton.setEnabled(true);
			scrollPane.validate();
		    } else if(label.equals("Desserts")) {
			model.insertElementAt(new Integer(9), dessertIndex);
			model.insertElementAt(new Integer(8), dessertIndex);
			model.insertElementAt(new Integer(7), dessertIndex);
			model.insertElementAt(new Integer(6), dessertIndex);
			model.insertElementAt(new Integer(5), dessertIndex);
			fruitIndex += 5;
			veggieIndex += 5;
			dessertRadioButton.setEnabled(true);
			scrollPane.validate();
		    } else if(label.equals("Fruits")) {
			model.insertElementAt(new Integer(14), fruitIndex);
			model.insertElementAt(new Integer(13), fruitIndex);
			model.insertElementAt(new Integer(12), fruitIndex);
			model.insertElementAt(new Integer(11), fruitIndex);
			model.insertElementAt(new Integer(10), fruitIndex);
			veggieIndex += 5;
			fruitRadioButton.setEnabled(true);
			scrollPane.validate();
		    } else if(label.equals("Vegetables")) {
			model.insertElementAt(new Integer(19), veggieIndex);
			model.insertElementAt(new Integer(18), veggieIndex);
			model.insertElementAt(new Integer(17), veggieIndex);
			model.insertElementAt(new Integer(16), veggieIndex);
			model.insertElementAt(new Integer(15), veggieIndex);
			veggieRadioButton.setEnabled(true);
			scrollPane.validate();
		    }
		}
	    }
	};
	fruitCheckbox.addActionListener(showListener);
	veggieCheckbox.addActionListener(showListener);
	dessertCheckbox.addActionListener(showListener);
	fastfoodCheckbox.addActionListener(showListener);

	ActionListener jumpListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
		JRadioButton rb = (JRadioButton) e.getSource();
		if(rb.isSelected()) {
		    String label = rb.getText();
		    if(label.equals("Fruits")) {
			listBox.ensureIndexIsVisible(fruitIndex+5);
			listBox.ensureIndexIsVisible(fruitIndex);
		    } else if(label.equals("Desserts")) {
			listBox.ensureIndexIsVisible(dessertIndex+5);
			listBox.ensureIndexIsVisible(dessertIndex);
		    } else if(label.equals("Vegetables")) {
			listBox.ensureIndexIsVisible(veggieIndex+5);
			listBox.ensureIndexIsVisible(veggieIndex);
		    } else if(label.equals("Fast Food")) {
			listBox.ensureIndexIsVisible(fastfoodIndex+5);
			listBox.ensureIndexIsVisible(fastfoodIndex);
		    }
		}
	    }
	};
	fruitRadioButton.addActionListener(jumpListener);
	veggieRadioButton.addActionListener(jumpListener);
	dessertRadioButton.addActionListener(jumpListener);
	fastfoodRadioButton.addActionListener(jumpListener);

	ActionListener resetListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
		resetAll();
            }
	};
	reset.addActionListener(resetListener);
    }

    public void resetAll() {
	model.removeAllElements();

	fastfoodCheckbox.setSelected(true);
	fruitCheckbox.setSelected(true);
	veggieCheckbox.setSelected(true);
	dessertCheckbox.setSelected(true);

	fastfoodRadioButton.setEnabled(true);
	fruitRadioButton.setEnabled(true);
	veggieRadioButton.setEnabled(true);
	dessertRadioButton.setEnabled(true);

	fastfoodRadioButton.setSelected(true);

	for(int i = 0; i < ITEMS; i++) {
	    model.addElement(new Integer(i));
	}

	fastfoodShown = true;
	dessertShown  = true;
	fruitShown    = true;
	veggieShown   = true;

	fastfoodIndex = 0;
	dessertIndex  = 5;
	fruitIndex    = 10;
	veggieIndex   = 15;

	listPrice = 0;

	priceLabel.setText("Total:  $0.00   ");
	listBox.ensureIndexIsVisible(fastfoodIndex);

	scrollPane.validate();
    }

    static int ITEMS = 20;
    ImageIcon images[];
    String     desc[];
    int        price[];

    class TestCellRenderer extends DefaultListCellRenderer
    {
	TestCellRenderer(JList listBox) {
	    super();

	    images = new ImageIcon[ITEMS];
	    desc = new String[ITEMS];
	    price = new int[ITEMS];

	    int i = 0;
	    // 5 - FastFood
	    images[i] = burger;      price[i] = 199;   desc[i++] = "Burger";
	    images[i] = fries;       price[i] = 99;    desc[i++] = "Fries";
	    images[i] = softdrink;   price[i] = 89;    desc[i++] = "Cola";
	    images[i] = pizza;       price[i] = 399;   desc[i++] = "Pizza";
	    images[i] = hotdog;      price[i] = 299;   desc[i++] = "Hotdog";

	    // 10 - Dessert
	    images[i] = icecream;    price[i] = 199;   desc[i++] = "Ice Cream";
	    images[i] = pie;         price[i] = 249;   desc[i++] = "Cherry Pie";
	    images[i] = cake;        price[i] = 355;   desc[i++] = "Cake";
	    images[i] = donut;       price[i] = 25;    desc[i++] = "Donut";
	    images[i] = treat;       price[i] = 52;    desc[i++] = "Fruit Pop";

	    // 15 Fruit
	    images[i] = grapes;      price[i] = 99;    desc[i++] = "Grapes";
	    images[i] = watermelon;  price[i] = 59;    desc[i++] = "Watermelon";
	    images[i] = peach;       price[i] = 35;    desc[i++] = "Peach";
	    images[i] = cantaloupe;  price[i] = 85;    desc[i++] = "Cantaloupe";
	    images[i] = banana;      price[i] = 25;    desc[i++] = "Banana";

	    // 20 - Veggies
	    images[i] = broccoli;    price[i] = 99;    desc[i++] = "Broccoli";
	    images[i] = corn;        price[i] = 65;    desc[i++] = "Corn";
	    images[i] = carrot;      price[i] = 25;    desc[i++] = "Carrot";
	    images[i] = peas;        price[i] =  3;    desc[i++] = "Peas";
	    images[i] = radish;      price[i] = 45;    desc[i++] = "Radish";
	}


	public Component getListCellRendererComponent(
            JList list,
            Object value,
            int modelIndex,
            boolean isSelected,
            boolean cellHasFocus)
        {
	    int index = ((Integer)value).intValue();
	    String text;
	    if(isSelected) {
		text = "  " + desc[index] + "    $" + ((double)price[index])/100.0;
	    }
	    else {
		text = "  " + desc[index];
	    }
	    setIcon(images[index]);

	    return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
	}
    }
}


