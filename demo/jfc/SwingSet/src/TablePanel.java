/*
 * @(#)TablePanel.java	1.35 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/*
 * @version 1.25 01/31/98
 * @author Philip Milne
 * @author Steve Wilson
 */
public class TablePanel extends JPanel {
    JTable      tableView;
    JScrollPane scrollpane;
    Dimension   origin = new Dimension(0, 0);

    JCheckBox   isColumnReorderingAllowedCheckBox;
    JCheckBox   showHorizontalLinesCheckBox;
    JCheckBox   showVerticalLinesCheckBox;

    JCheckBox   isColumnSelectionAllowedCheckBox;
    JCheckBox   isRowSelectionAllowedCheckBox;
    JCheckBox   isRowAndColumnSelectionAllowedCheckBox;

    JLabel      interCellSpacingLabel;
    JLabel      rowHeightLabel;

    JSlider     interCellSpacingSlider;
    JSlider     rowHeightSlider;

    JComponent  selectionModeButtons;
    JComponent  resizeModeButtons;

    JPanel      mainPanel;
    JPanel      controlPanel;
    JScrollPane tableAggregate;

    public TablePanel(SwingSet swing) {
	super();

	setLayout(new BorderLayout());
        mainPanel = this;
	controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JPanel column1 = new JPanel (new ColumnLayout() );
	JPanel column2 = new JPanel (new ColumnLayout() );
	JPanel column3 = new JPanel (new ColumnLayout() );

	mainPanel.add(controlPanel, BorderLayout.NORTH);


	// start column 1
    	isColumnReorderingAllowedCheckBox = new JCheckBox("Reordering allowed", true);
        column1.add(isColumnReorderingAllowedCheckBox);
        isColumnReorderingAllowedCheckBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        boolean flag = ((JCheckBox)e.getSource()).isSelected();
                tableView.getTableHeader().setReorderingAllowed(flag);
                tableView.repaint();
	    }
        });


    	showHorizontalLinesCheckBox = new JCheckBox("Horiz. Lines", true);
        column1.add(showHorizontalLinesCheckBox);
        showHorizontalLinesCheckBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        boolean flag = ((JCheckBox)e.getSource()).isSelected();
                tableView.setShowHorizontalLines(flag); ;
                tableView.repaint();
	    }
        });

    	showVerticalLinesCheckBox = new JCheckBox("Vert. Lines", true);
        column1.add(showVerticalLinesCheckBox);
        showVerticalLinesCheckBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        boolean flag = ((JCheckBox)e.getSource()).isSelected();
                tableView.setShowVerticalLines(flag); ;
                tableView.repaint();
	    }
        });

        interCellSpacingLabel = new JLabel("Inter-cell spacing:");
	column1.add(interCellSpacingLabel);

    	interCellSpacingSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 1);
	interCellSpacingSlider.getAccessibleContext().setAccessibleName("Inter-cell spacing");
	interCellSpacingLabel.setLabelFor(interCellSpacingSlider);
        column1.add(interCellSpacingSlider);
        interCellSpacingSlider.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
	        int spacing = ((JSlider)e.getSource()).getValue();
                tableView.setIntercellSpacing(new Dimension(spacing, spacing));
                tableView.repaint();
	    }
        });

        controlPanel.add(column1);

	// start column 2

 	isColumnSelectionAllowedCheckBox = new JCheckBox("Column selection", false);
        column2.add(isColumnSelectionAllowedCheckBox);
        isColumnSelectionAllowedCheckBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        boolean flag = ((JCheckBox)e.getSource()).isSelected();
                tableView.setColumnSelectionAllowed(flag); ;
                tableView.repaint();
	    }
        });

    	isRowSelectionAllowedCheckBox = new JCheckBox("Row selection", true);
        column2.add(isRowSelectionAllowedCheckBox);
        isRowSelectionAllowedCheckBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        boolean flag = ((JCheckBox)e.getSource()).isSelected();
                tableView.setRowSelectionAllowed(flag); ;
                tableView.repaint();
	    }
        });

    	isRowAndColumnSelectionAllowedCheckBox = new JCheckBox("Cell selection", false);
        column2.add(isRowAndColumnSelectionAllowedCheckBox);
        isRowAndColumnSelectionAllowedCheckBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        boolean flag = ((JCheckBox)e.getSource()).isSelected();
                tableView.setCellSelectionEnabled(flag); ;
                tableView.repaint();
	    }
        });

        rowHeightLabel = new JLabel("Row height:");
	column2.add(rowHeightLabel);

    	rowHeightSlider = new JSlider(JSlider.HORIZONTAL, 5, 100, 20);
	rowHeightSlider.getAccessibleContext().setAccessibleName("Row height");
	rowHeightLabel.setLabelFor(rowHeightSlider);
        column2.add(rowHeightSlider);
        rowHeightSlider.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
	        int height = ((JSlider)e.getSource()).getValue();
                tableView.setRowHeight(height);
                tableView.repaint();
	    }
        });

        controlPanel.add(column2);

        // Create the table.
        tableAggregate = createTable();
        mainPanel.add(tableAggregate, BorderLayout.CENTER);



        // ComboBox for selection modes.
	JPanel selectMode = new JPanel();
        column3.setLayout(new ColumnLayout());
      	selectMode.setBorder(new TitledBorder("Selection mode"));


        JComboBox selectionModeComboBox = new JComboBox();
        selectionModeComboBox.addItem("Single");
        selectionModeComboBox.addItem("One range");
        selectionModeComboBox.addItem("Multiple ranges");
        selectionModeComboBox.setSelectedIndex(tableView.getSelectionModel().getSelectionMode());
        selectionModeComboBox.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
	        JComboBox source = (JComboBox)e.getSource();
                tableView.setSelectionMode(source.getSelectedIndex());
	    }
        });

	selectMode.add(selectionModeComboBox);
        column3.add(selectMode);

        // Combo box for table resize mode.

	JPanel resizeMode = new JPanel();

	resizeMode.setBorder(new TitledBorder("Autoresize mode"));


        JComboBox resizeModeComboBox = new JComboBox();
        resizeModeComboBox.addItem("Off");
        resizeModeComboBox.addItem("Column boundries");
        resizeModeComboBox.addItem("Subsequent columns");
        resizeModeComboBox.addItem("Last column");
        resizeModeComboBox.addItem("All columns");
        resizeModeComboBox.setSelectedIndex(tableView.getAutoResizeMode());
        resizeModeComboBox.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
	        JComboBox source = (JComboBox)e.getSource();
                tableView.setAutoResizeMode(source.getSelectedIndex());
	    }
        });

	resizeMode.add(resizeModeComboBox);
        column3.add(resizeMode);

        controlPanel.add(column3);
    }


    private ImageIcon loadIcon(String name, String description) {
	String path = "images/ImageClub/food/" + name;
	return SwingSet.sharedInstance().loadImageIcon(path, description);
    }

    public JScrollPane createTable() {

        // final
        final String[] names = {"First Name", "Last Name", "Favorite Color",
                                "Favorite Sport", "Favorite Number", "Favorite Food"};

	ImageIcon burger = loadIcon("burger.gif","burger");
	ImageIcon fries = loadIcon("fries.gif","fries");
	ImageIcon softdrink = loadIcon("softdrink.gif","soft drink");
	ImageIcon hotdog = loadIcon("hotdog.gif","hot dog");
	ImageIcon pizza = loadIcon("pizza.gif","pizza");
	ImageIcon icecream = loadIcon("icecream.gif","ice cream");
	ImageIcon pie = loadIcon("pie.gif","pie");
	ImageIcon cake = loadIcon("cake.gif","cake");
	ImageIcon donut = loadIcon("donut.gif","donut");
	ImageIcon treat = loadIcon("treat.gif","treat");
	ImageIcon grapes = loadIcon("grapes.gif","grapes");
	ImageIcon banana = loadIcon("banana.gif","banana");
	ImageIcon watermelon = loadIcon("watermelon.gif","watermelon");
	ImageIcon cantaloupe = loadIcon("cantaloupe.gif","cantaloupe");
	ImageIcon peach = loadIcon("peach.gif","peach");
	ImageIcon broccoli = loadIcon("broccoli.gif","broccoli");
	ImageIcon carrot = loadIcon("carrot.gif","carrot");
	ImageIcon peas = loadIcon("peas.gif","peas");
	ImageIcon corn = loadIcon("corn.gif","corn");
	ImageIcon radish = loadIcon("radish.gif","radish");


        // Create the dummy data (a few rows of names)
        final Object[][] data = {
	  {"Mike", "Albers",        Color.green, "Soccer", new Integer(44), banana},
	  {"Mark", "Andrews",       Color.red, "Baseball", new Integer(2), broccoli},
	  {"Tom", "Ball",           Color.blue, "Football", new Integer(99), burger},
	  {"Alan", "Chung",         Color.green, "Baseball", new Integer(838), cake},
	  {"Jeff", "Dinkins",       Color.magenta, "Football", new Integer(8), cantaloupe},
	  {"Amy", "Fowler",         Color.yellow, "Hockey", new Integer(3), carrot},
	  {"Brian", "Gerhold",      Color.green, "Rugby", new Integer(7), corn},
	  {"James", "Gosling",      Color.pink, "Tennis", new Integer(21), donut},
	  {"Earl", "Johnson",       Color.green, "Bicycling", new Integer(8), carrot},
	  {"David", "Karlton",      Color.red, "Baseball", new Integer(1), fries},
	  {"Dave", "Kloba",         Color.yellow, "Football", new Integer(14), grapes},
	  {"Peter", "Korn",         new Color(100, 100, 255), "Scuba Diving", new Integer(12), broccoli},
	  {"Dana", "Miller",        Color.blue, "Ice Skating", new Integer(8), banana},
	  {"Phil", "Milne",         Color.magenta, "Rugby", new Integer(3), banana},
	  {"Dave", "Moore",         Color.green, "Tennis", new Integer(88), peach},
	  {"Hans", "Muller",        Color.magenta, "Baseball", new Integer(5), peas},
	  {"Rick", "Levenson",      Color.blue, "Football", new Integer(2), pie},
	  {"Tim", "Prinzing",       Color.blue, "Baseball", new Integer(22), pizza},
	  {"Chester", "Rose",       Color.black, "Hockey", new Integer(0), radish},
	  {"Chris", "Ryan",         Color.black, "None", new Integer(6), softdrink},
	  {"Ray", "Ryan",           Color.gray, "Football", new Integer(77), treat},
	  {"Georges", "Saab",       Color.red, "Hockey", new Integer(4), watermelon},
	  {"Tom", "Santos",         Color.blue, "Football", new Integer(3), banana},
	  {"Rich", "Schiavi",       Color.blue, "Hockey", new Integer(4), grapes},
	  {"Nancy", "Schorr",       Color.blue, "Hockey", new Integer(8), corn},
	  {"Violet", "Scott",       Color.magenta, "Basketball", new Integer(44), grapes},
	  {"Joseph", "Scheuhammer", Color.green, "Hockey", new Integer(66), corn},
	  {"Jeff", "Shapiro",       Color.black, "Skiing", new Integer(42), peach},
	  {"Willie", "Walker",      Color.blue, "Hockey", new Integer(4), banana},
	  {"Kathy", "Walrath",      Color.blue, "Baseball", new Integer(8), banana},
	  {"Arnaud", "Weber",       Color.green, "Football", new Integer(993), peach},
	  {"Steve", "Wilson",       Color.green, "Baseball", new Integer(7), fries}
        };

        // Create a model of the data.
        TableModel dataModel = new AbstractTableModel() {
            public int getColumnCount() { return names.length; }
            public int getRowCount() { return data.length;}
            public Object getValueAt(int row, int col) {return data[row][col];}
            public String getColumnName(int column) {return names[column];}
            public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
            public boolean isCellEditable(int row, int col) {return getColumnClass(col) == String.class;}
            public void setValueAt(Object aValue, int row, int column) { data[row][column] = aValue; }
         };


        // Create the table
        tableView = new JTable(dataModel);

        // Show colors by rendering them in their own color.
        DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
	    public void setValue(Object value) {
	        if (value instanceof Color) {
	            Color c = (Color)value;
	            setForeground(c);
	            setText(c.getRed() + ", " + c.getGreen() + ", " + c.getBlue());
	        }
	    }

        };

        colorRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tableView.getColumn("Favorite Color").setCellRenderer(colorRenderer);

        tableView.setRowHeight(20);

        scrollpane = new JScrollPane(tableView);
        return scrollpane;
    }
}

class ColumnLayout implements LayoutManager {

  int xInset = 5;
  int yInset = 5;
  int yGap = 2;

  public void addLayoutComponent(String s, Component c) {}

  public void layoutContainer(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;

      Component[] children = c.getComponents();
      Dimension compSize = null;
      for (int i = 0; i < children.length; i++) {
	  compSize = children[i].getPreferredSize();
	  children[i].setSize(compSize.width, compSize.height);
	  children[i].setLocation( xInset + insets.left, height);
	  height += compSize.height + yGap;
      }

  }

  public Dimension minimumLayoutSize(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;
      int width = 0 + insets.left + insets.right;

      Component[] children = c.getComponents();
      Dimension compSize = null;
      for (int i = 0; i < children.length; i++) {
	  compSize = children[i].getPreferredSize();
	  height += compSize.height + yGap;
	  width = Math.max(width, compSize.width + insets.left + insets.right + xInset*2);
      }
      height += insets.bottom;
      return new Dimension( width, height);
  }

  public Dimension preferredLayoutSize(Container c) {
      return minimumLayoutSize(c);
  }

  public void removeLayoutComponent(Component c) {}

}
