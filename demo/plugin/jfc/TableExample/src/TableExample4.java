/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)TableExample4.java	1.15 03/01/23
 */

/**
 * Another JTable example, showing how column attributes can be refined
 * even when columns have been created automatically. Here we create some
 * specialised renderers and editors as well as changing widths and colors
 * for some of the columns in the SwingSet demo table.
 *
 * @version 1.15 01/23/03
 * @author Philip Milne
 */

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;

public class TableExample4 {

    public TableExample4() {
        JFrame frame = new JFrame("Table");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}});

        // Take the dummy data from SwingSet.
        final String[] names = {"First Name", "Last Name", "Favorite Color",
                                "Favorite Number", "Vegetarian"};
        final Object[][] data = {
	    {"Mark", "Andrews", "Red", new Integer(2), new Boolean(true)},
	    {"Tom", "Ball", "Blue", new Integer(99), new Boolean(false)},
	    {"Alan", "Chung", "Green", new Integer(838), new Boolean(false)},
	    {"Jeff", "Dinkins", "Turquois", new Integer(8), new Boolean(true)},
	    {"Amy", "Fowler", "Yellow", new Integer(3), new Boolean(false)},
	    {"Brian", "Gerhold", "Green", new Integer(0), new Boolean(false)},
	    {"James", "Gosling", "Pink", new Integer(21), new Boolean(false)},
	    {"David", "Karlton", "Red", new Integer(1), new Boolean(false)},
	    {"Dave", "Kloba", "Yellow", new Integer(14), new Boolean(false)},
	    {"Peter", "Korn", "Purple", new Integer(12), new Boolean(false)},
	    {"Phil", "Milne", "Purple", new Integer(3), new Boolean(false)},
	    {"Dave", "Moore", "Green", new Integer(88), new Boolean(false)},
	    {"Hans", "Muller", "Maroon", new Integer(5), new Boolean(false)},
	    {"Rick", "Levenson", "Blue", new Integer(2), new Boolean(false)},
	    {"Tim", "Prinzing", "Blue", new Integer(22), new Boolean(false)},
	    {"Chester", "Rose", "Black", new Integer(0), new Boolean(false)},
	    {"Ray", "Ryan", "Gray", new Integer(77), new Boolean(false)},
	    {"Georges", "Saab", "Red", new Integer(4), new Boolean(false)},
	    {"Willie", "Walker", "Phthalo Blue", new Integer(4), new Boolean(false)},
	    {"Kathy", "Walrath", "Blue", new Integer(8), new Boolean(false)},
	    {"Arnaud", "Weber", "Green", new Integer(44), new Boolean(false)}
        };

        // Create a model of the data.
        TableModel dataModel = new AbstractTableModel() {
            // These methods always need to be implemented.
            public int getColumnCount() { return names.length; }
            public int getRowCount() { return data.length;}
            public Object getValueAt(int row, int col) {return data[row][col];}

            // The default implementations of these methods in
            // AbstractTableModel would work, but we can refine them.
            public String getColumnName(int column) {return names[column];}
            public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
            public boolean isCellEditable(int row, int col) {return true;}
            public void setValueAt(Object aValue, int row, int column) {
                System.out.println("Setting value to: " + aValue);
                data[row][column] = aValue;
            }
         };

        // Create the table
        JTable tableView = new JTable(dataModel);
        // Turn off auto-resizing so that we can set column sizes programmatically. 
	// In this mode, all columns will get their preferred widths, as set blow. 
        tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	// Create a combo box to show that you can use one in a table.
	JComboBox comboBox = new JComboBox();
	comboBox.addItem("Red");
	comboBox.addItem("Orange");
	comboBox.addItem("Yellow");
	comboBox.addItem("Green");
	comboBox.addItem("Blue");
	comboBox.addItem("Indigo");
	comboBox.addItem("Violet");

        TableColumn colorColumn = tableView.getColumn("Favorite Color");
        // Use the combo box as the editor in the "Favorite Color" column.
        colorColumn.setCellEditor(new DefaultCellEditor(comboBox));

        // Set a pink background and tooltip for the Color column renderer.
        DefaultTableCellRenderer colorColumnRenderer = new DefaultTableCellRenderer();
        colorColumnRenderer.setBackground(Color.pink);
        colorColumnRenderer.setToolTipText("Click for combo box");
        colorColumn.setCellRenderer(colorColumnRenderer);

        // Set a tooltip for the header of the colors column.
	TableCellRenderer headerRenderer = colorColumn.getHeaderRenderer();
	if (headerRenderer instanceof DefaultTableCellRenderer)
	    ((DefaultTableCellRenderer)headerRenderer).setToolTipText("Hi Mom!");

	// Set the width of the "Vegetarian" column.
        TableColumn vegetarianColumn = tableView.getColumn("Vegetarian");
        vegetarianColumn.setPreferredWidth(100);

	// Show the values in the "Favorite Number" column in different colors.
        TableColumn numbersColumn = tableView.getColumn("Favorite Number");
        DefaultTableCellRenderer numberColumnRenderer = new DefaultTableCellRenderer() {
	    public void setValue(Object value) {
	        int cellValue = (value instanceof Number) ? ((Number)value).intValue() : 0;
	        setForeground((cellValue > 30) ? Color.black : Color.red);
	        setText((value == null) ? "" : value.toString());
	    }
        };
        numberColumnRenderer.setHorizontalAlignment(JLabel.RIGHT);
        numbersColumn.setCellRenderer(numberColumnRenderer);
        numbersColumn.setPreferredWidth(110);

        // Finish setting up the table.
        JScrollPane scrollpane = new JScrollPane(tableView);
	scrollpane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        scrollpane.setPreferredSize(new Dimension(430, 200));
        frame.getContentPane().add(scrollpane);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new TableExample4();
    }
}
