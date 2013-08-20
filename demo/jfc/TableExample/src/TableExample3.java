/*
 * @(#)TableExample3.java	1.16 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)TableExample3.java	1.16 04/07/26
 */

/**
 * An example showing the JTable with a dataModel that is not derived
 * from a database. We add the optional TableSorter object to give the
 * JTable the ability to sort.
 *
 * @version 1.3 10/14/97
 * @author Philip Milne
 */

import javax.swing.*;
import javax.swing.table.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;

public class TableExample3 {

    public TableExample3() {
        JFrame frame = new JFrame("Table");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}});

        // Take the dummy data from SwingSet.
        final String[] names = {"First Name", "Last Name", "Favorite Color",
                                "Favorite Number", "Vegetarian"};
        final Object[][] data = {
	    {"Mark", "Andrews", "Red", new Integer(2), Boolean.TRUE},
	    {"Tom", "Ball", "Blue", new Integer(99), Boolean.FALSE},
	    {"Alan", "Chung", "Green", new Integer(838), Boolean.FALSE},
	    {"Jeff", "Dinkins", "Turquois", new Integer(8), Boolean.TRUE},
	    {"Amy", "Fowler", "Yellow", new Integer(3), Boolean.FALSE},
	    {"Brian", "Gerhold", "Green", new Integer(0), Boolean.FALSE},
	    {"James", "Gosling", "Pink", new Integer(21), Boolean.FALSE},
	    {"David", "Karlton", "Red", new Integer(1), Boolean.FALSE},
	    {"Dave", "Kloba", "Yellow", new Integer(14), Boolean.FALSE},
	    {"Peter", "Korn", "Purple", new Integer(12), Boolean.FALSE},
	    {"Phil", "Milne", "Purple", new Integer(3), Boolean.FALSE},
	    {"Dave", "Moore", "Green", new Integer(88), Boolean.FALSE},
	    {"Hans", "Muller", "Maroon", new Integer(5), Boolean.FALSE},
	    {"Rick", "Levenson", "Blue", new Integer(2), Boolean.FALSE},
	    {"Tim", "Prinzing", "Blue", new Integer(22), Boolean.FALSE},
	    {"Chester", "Rose", "Black", new Integer(0), Boolean.FALSE},
	    {"Ray", "Ryan", "Gray", new Integer(77), Boolean.FALSE},
	    {"Georges", "Saab", "Red", new Integer(4), Boolean.FALSE},
	    {"Willie", "Walker", "Phthalo Blue", new Integer(4), Boolean.FALSE},
	    {"Kathy", "Walrath", "Blue", new Integer(8), Boolean.FALSE},
	    {"Arnaud", "Weber", "Green", new Integer(44), Boolean.FALSE}
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
            public Class getColumnClass(int col) {return getValueAt(0,col).getClass();}
            public boolean isCellEditable(int row, int col) {return (col==4);}
            public void setValueAt(Object aValue, int row, int column) {
                data[row][column] = aValue;
            }
         };

        // Instead of making the table display the data as it would normally with:
        // JTable tableView = new JTable(dataModel);
        // Add a sorter, by using the following three lines instead of the one above.
        TableSorter  sorter = new TableSorter(dataModel);
        JTable    tableView = new JTable(sorter);
        sorter.addMouseListenerToHeaderInTable(tableView);

        JScrollPane scrollpane = new JScrollPane(tableView);

        scrollpane.setPreferredSize(new Dimension(700, 300));
        frame.getContentPane().add(scrollpane);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new TableExample3();
    }
}
