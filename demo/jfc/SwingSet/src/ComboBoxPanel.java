/*
 * @(#)ComboBoxPanel.java	1.11 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.plaf.*;
import javax.swing.tree.*;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.accessibility.*;

/**
 * SwingSet panel for JComboBox
 *
 * @version 1.11 11/29/01
 * @author Arnaud Weber
 * @author Peter Korn (accessibility support)
 */
public class ComboBoxPanel extends JPanel {
    // The Frame
    SwingSet swing;
    JComboBox  months;
    JComboBox  days;
    JComboBox  cb;
    JComboBox  cb1;
    JComboBox  custom;
    JComboBox  treeComboBox;

    DefaultListModel model = new DefaultListModel();

    public ComboBoxPanel(SwingSet swing) {
      JPanel tp;
      this.swing = swing;
      this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
      setBorder(swing.emptyBorder5);
      
      this.add(Box.createRigidArea(new Dimension(1,50)));
      JPanel panel = new JPanel(false);
      panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
      add(panel);
      // Classic combo box
      tp = new JPanel();
      tp.setMaximumSize(new Dimension(Short.MAX_VALUE,100));
      tp.setBorder(BorderFactory.createTitledBorder("Classic ComboBox"));
//      tp.setTitle("Classic ComboBox");
      tp.setLayout(new BoxLayout(tp,BoxLayout.X_AXIS));
      tp.add(Box.createRigidArea(new Dimension(5,1)));
      months = new JComboBox();
      months.addItem("January");
      months.addItem("February");
      months.addItem("March");
      months.addItem("April");
      months.addItem("May");
      months.addItem("June");
      months.addItem("July");
      months.addItem("August");
      months.addItem("September");
      months.addItem("October");
      months.addItem("November");
      months.addItem("December");
      months.getAccessibleContext().setAccessibleName("Months");
      months.getAccessibleContext().setAccessibleDescription("Choose a month of the year");
      tp.add(months);
      tp.add(Box.createRigidArea(new Dimension(5,1)));
      days = new JComboBox();
      days.addItem("Monday");
      days.addItem("Tuesday");
      days.addItem("Wednesday");
      days.addItem("Thursday");
      days.addItem("Friday");
      days.addItem("Saturday");
      days.addItem("Sunday");
      days.getAccessibleContext().setAccessibleName("Days");
      days.getAccessibleContext().setAccessibleDescription("Choose a day of the week");
      tp.add(days);
      tp.add(Box.createRigidArea(new Dimension(5,1)));
      panel.add(tp);

      // Editable combo box

      add(panel);
      tp = new JPanel();
      tp.setMaximumSize(new Dimension(Short.MAX_VALUE,100));
      tp.setBorder(BorderFactory.createTitledBorder("Editable ComboBox"));
      tp.setLayout(new BoxLayout(tp,BoxLayout.X_AXIS));
      tp.add(Box.createRigidArea(new Dimension(5,1)));
      cb = new JComboBox();
      cb.setEditable(true);
      cb.addItem("0");
      cb.addItem("10");
      cb.addItem("20");
      cb.addItem("30");
      cb.addItem("40");
      cb.addItem("50");
      cb.addItem("60");
      cb.addItem("70");
      cb.addItem("80");
      cb.addItem("90");
      cb.addItem("100");
      cb.addItem("More");
      cb.getAccessibleContext().setAccessibleName("Numbers");
      cb.getAccessibleContext().setAccessibleDescription("Demonstration editable ComboBox with numbers 0-100");
      cb.setSelectedItem("50");
      tp.add(cb);
      tp.add(Box.createRigidArea(new Dimension(5,1)));

      cb1 = new JComboBox();
      cb1.setEditable(true);
      cb1.addItem("0");
      cb1.addItem(".25");
      cb1.addItem(".5");
      cb1.addItem(".75");
      cb1.addItem("1.0");
      cb1.getAccessibleContext().setAccessibleName("Small numbers");
      cb1.getAccessibleContext().setAccessibleDescription("Demonstration editable ComboBox with numbers 0-1");
      cb1.setSelectedItem(".5");
      tp.add(cb1);

      tp.add(Box.createRigidArea(new Dimension(5,1)));
      panel.add(tp);

      // Custom combobox
      panel = new JPanel(false);
      panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
      add(panel);
      tp = new JPanel();
      tp.setMaximumSize(new Dimension(Short.MAX_VALUE,200));
      tp.setBorder(BorderFactory.createTitledBorder("Custom ComboBox"));
//      tp.setTitle("Custom ComboBox");
      tp.setLayout(new BoxLayout(tp,BoxLayout.X_AXIS));
      tp.add(Box.createRigidArea(new Dimension(5,1)));
      custom = new JComboBox(new CustomComboBoxModel());
      custom.setRenderer(new TestCellRenderer(custom));
      custom.setSelectedIndex(0);
      custom.setMaximumRowCount(4);
      custom.getAccessibleContext().setAccessibleName("Custom ComboBox");
      custom.getAccessibleContext().setAccessibleDescription("Sample custom ComboBox with icons in them, one of which changes when selected");
      tp.add(custom);
      tp.add(Box.createRigidArea(new Dimension(5,1)));

      DefaultMutableTreeNode swingNode = new DefaultMutableTreeNode("Swing");
      DefaultMutableTreeNode spec  = new DefaultMutableTreeNode("spec");
      DefaultMutableTreeNode api   = new DefaultMutableTreeNode("api");

      swingNode.add(spec);
      swingNode.add(api);

      api.add(new DefaultMutableTreeNode("JComponent"));
      api.add(new DefaultMutableTreeNode("JTable"));
      api.add(new DefaultMutableTreeNode("JTree"));
      api.add(new DefaultMutableTreeNode("JComboBox"));
      api.add(new DefaultMutableTreeNode("JTextComponent"));

      spec.add(new DefaultMutableTreeNode("JComponent"));
      spec.add(new DefaultMutableTreeNode("JTable"));
      spec.add(new DefaultMutableTreeNode("JTree"));
      spec.add(new DefaultMutableTreeNode("JComboBox"));
      spec.add(new DefaultMutableTreeNode("JTextComponent"));
      
      treeComboBox = new TreeCombo(new DefaultTreeModel(swingNode));
      treeComboBox.getAccessibleContext().setAccessibleName("Swing specs and APIs");
      treeComboBox.getAccessibleContext().setAccessibleDescription("Sample custom ComboBox with a tree heirarchy");
      tp.add(treeComboBox);
      tp.add(Box.createRigidArea(new Dimension(5,1)));
      panel.add(tp);
    }

    class CustomComboBoxModel extends AbstractListModel implements ComboBoxModel {
      Object currentValue;
      ImageIcon images[];
      ImageIcon images_down[];      
      Hashtable cache[];

      public CustomComboBoxModel() {
	images = new ImageIcon[5];
	images_down = new ImageIcon[5];
	images[0] = SwingSet.sharedInstance().loadImageIcon("images/list/a1.gif","blue profile of robot");
        images_down[0] = SwingSet.sharedInstance().loadImageIcon("images/list/a1d.gif","greyed out blue profile of robot");
	images[1] = SwingSet.sharedInstance().loadImageIcon("images/list/a2.gif","pinkish profile of robot");
        images_down[1] = SwingSet.sharedInstance().loadImageIcon("images/list/a2d.gif","greyed out pinkish profile of robot");
	images[2] = SwingSet.sharedInstance().loadImageIcon("images/list/a3.gif","yellow profile of robot");
        images_down[2] = SwingSet.sharedInstance().loadImageIcon("images/list/a3d.gif","greyed out yellow profile of robot");
	images[3] = SwingSet.sharedInstance().loadImageIcon("images/list/a4.gif","green profile of robot");
        images_down[3] = SwingSet.sharedInstance().loadImageIcon("images/list/a4d.gif","greyed out green profile of robot");
	images[4] = SwingSet.sharedInstance().loadImageIcon("images/list/a5.gif","profile of robot");
        images_down[4] = SwingSet.sharedInstance().loadImageIcon("images/list/a5d.gif","greyed out profile of robot");
	cache = new Hashtable[getSize()];
      }

      public void setSelectedItem(Object anObject) {
	currentValue = anObject;
	fireContentsChanged(this,-1,-1);
      }
      
      public Object getSelectedItem() {
	return currentValue;
      }

      public int getSize() {
	return 25;
      }

      public Object getElementAt(int index) {
	if(cache[index] != null)
	  return cache[index];
	else {
	  Hashtable result = new Hashtable();
	    if(index != 24) {
	      result.put("title","Hello I'm the choice " + index);
	      result.put("image",images[index % 5]);
	      result.put("Himage",images_down[index % 5]);
	    } else {
	      result.put("title","Hello I'm Duke");
	      result.put("image",swing.dukeSnooze);
	      result.put("Himage",swing.dukeWave);
	    }
	  cache[index] = result;
	  return result;
	}
      }
    }

    class TestCellRenderer extends JLabel implements ListCellRenderer   {
	JComboBox combobox;


      public TestCellRenderer(JComboBox x) {
        this.combobox = x;
        setOpaque(true);
      }

      public Component getListCellRendererComponent(
          JList listbox, 
          Object value, 
          int index, 
          boolean isSelected, 
          boolean cellHasFocus) 
      {
	Hashtable h = (Hashtable) value;
        if(UIManager.getLookAndFeel().getName().equals("CDE/Motif")) {
            if(index == -1 )
                setOpaque(false);
            else
                setOpaque(true);
        } else 
            setOpaque(true);

        if(value == null) {
	  setText("");
	  setIcon(null);
	} else if(isSelected) {
	    setBackground(UIManager.getColor("ComboBox.selectionBackground"));
	    setForeground(UIManager.getColor("ComboBox.selectionForeground"));
            setIcon((ImageIcon)h.get("Himage"));
            setText((String)h.get("title"));
	} else {
            setIcon((ImageIcon)h.get("image"));
            setText((String)h.get("title"));
	    setBackground(UIManager.getColor("ComboBox.background"));
	    setForeground(UIManager.getColor("ComboBox.foreground"));
	}
	return this;
      }
    }
}






