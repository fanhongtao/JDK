/*
 * @(#)TableExample.java	1.19 04/07/26
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
 * @(#)TableExample.java	1.19 04/07/26
 */

/**
 * A a UI around the JDBCAdaptor, allowing database data to be interactively
 * fetched, sorted and displayed using Swing.
 *
 * NOTE: This example uses a modal dialog via the static convenience methods in
 * the JOptionPane. Use of modal dialogs requires JDK 1.1.4 or greater.
 *
 * @version 1.19 07/26/04
 * @author Philip Milne
 */

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class TableExample implements LayoutManager {
    static String[] ConnectOptionNames = { "Connect" };
    static String   ConnectTitle = "Connection Information";

    Dimension   origin = new Dimension(0, 0);

    JButton     fetchButton;
    JButton     showConnectionInfoButton;

    JPanel      connectionPanel;
    JFrame      frame; // The query/results window.

    JLabel      userNameLabel;
    JTextField  userNameField;
    JLabel      passwordLabel;
    JTextField  passwordField;
    // JLabel      queryLabel;
    JTextArea   queryTextArea;
    JComponent  queryAggregate;
    JLabel      serverLabel;
    JTextField  serverField;
    JLabel      driverLabel;
    JTextField  driverField;

    JPanel      mainPanel;

    TableSorter sorter;
    JDBCAdapter dataBase;
    JScrollPane tableAggregate;

    /**
     * Brigs up a JDialog using JOptionPane containing the connectionPanel.
     * If the user clicks on the 'Connect' button the connection is reset.
     */
    void activateConnectionDialog() {
	if(JOptionPane.showOptionDialog(tableAggregate, connectionPanel, ConnectTitle,
		   JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                   null, ConnectOptionNames, ConnectOptionNames[0]) == 0) {
	    connect();
            frame.setVisible(true);
	}
	else if(!frame.isVisible())
	    System.exit(0);
    }

    /**
     * Creates the connectionPanel, which will contain all the fields for
     * the connection information.
     */
    public void createConnectionDialog() {
 	// Create the labels and text fields.
	userNameLabel = new JLabel("User name: ", JLabel.RIGHT);
 	userNameField = new JTextField("guest");

	passwordLabel = new JLabel("Password: ", JLabel.RIGHT);
	passwordField = new JTextField("trustworthy");

        serverLabel = new JLabel("Database URL: ", JLabel.RIGHT);
	serverField = new JTextField("jdbc:sybase://dbtest:1455/pubs2");

	driverLabel = new JLabel("Driver: ", JLabel.RIGHT);
	driverField = new JTextField("connect.sybase.SybaseDriver");


	connectionPanel = new JPanel(false);
	connectionPanel.setLayout(new BoxLayout(connectionPanel,
						BoxLayout.X_AXIS));

	JPanel namePanel = new JPanel(false);
	namePanel.setLayout(new GridLayout(0, 1));
	namePanel.add(userNameLabel);
	namePanel.add(passwordLabel);
	namePanel.add(serverLabel);
	namePanel.add(driverLabel);

	JPanel fieldPanel = new JPanel(false);
	fieldPanel.setLayout(new GridLayout(0, 1));
	fieldPanel.add(userNameField);
	fieldPanel.add(passwordField);
	fieldPanel.add(serverField);
        fieldPanel.add(driverField);

	connectionPanel.add(namePanel);
	connectionPanel.add(fieldPanel);
    }

    public TableExample() {
        mainPanel = new JPanel();

        // Create the panel for the connection information
	createConnectionDialog();

	// Create the buttons.
	showConnectionInfoButton = new JButton("Configuration");
        showConnectionInfoButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            activateConnectionDialog();
	        }
	    }
	);

	fetchButton = new JButton("Fetch");
        fetchButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            fetch();
	        }
	    }
	);

	// Create the query text area and label.
        queryTextArea = new JTextArea("SELECT * FROM titles", 25, 25);
	queryAggregate = new JScrollPane(queryTextArea);
        queryAggregate.setBorder(new BevelBorder(BevelBorder.LOWERED));

        // Create the table.
        tableAggregate = createTable();
        tableAggregate.setBorder(new BevelBorder(BevelBorder.LOWERED));

	// Add all the components to the main panel.
        mainPanel.add(fetchButton);
        mainPanel.add(showConnectionInfoButton);
        mainPanel.add(queryAggregate);
        mainPanel.add(tableAggregate);
        mainPanel.setLayout(this);

        // Create a Frame and put the main panel in it.
        frame = new JFrame("TableExample");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}});
        frame.setBackground(Color.lightGray);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setVisible(false);
        frame.setBounds(200, 200, 640, 480);

	activateConnectionDialog();
    }

    public void connect() {
       dataBase = new JDBCAdapter(
            serverField.getText(),
            driverField.getText(),
            userNameField.getText(),
            passwordField.getText());
       sorter.setModel(dataBase);
   }

    public void fetch() {
        dataBase.executeQuery(queryTextArea.getText());
    }

    public JScrollPane createTable() {
        sorter = new TableSorter();

        //connect();
        //fetch();

        // Create the table
        JTable table = new JTable(sorter); 
	// Use a scrollbar, in case there are many columns. 
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 

        // Install a mouse listener in the TableHeader as the sorter UI.
        sorter.addMouseListenerToHeaderInTable(table);

        JScrollPane scrollpane = new JScrollPane(table);

        return scrollpane;
    }

    public static void main(String s[]) {
        new TableExample();
    }

    public Dimension preferredLayoutSize(Container c){return origin;}
    public Dimension minimumLayoutSize(Container c){return origin;}
    public void addLayoutComponent(String s, Component c) {}
    public void removeLayoutComponent(Component c) {}
    public void layoutContainer(Container c) {
        Rectangle b = c.getBounds();
        int topHeight = 90;
        int inset = 4;
        showConnectionInfoButton.setBounds(b.width-2*inset-120, inset, 120, 25);
        fetchButton.setBounds(b.width-2*inset-120, 60, 120, 25);
        // queryLabel.setBounds(10, 10, 100, 25);
        queryAggregate.setBounds(inset, inset, b.width-2*inset - 150, 80);
        tableAggregate.setBounds(new Rectangle(inset,
                                               inset + topHeight,
                                               b.width-2*inset,
                                               b.height-2*inset - topHeight));
    }

}
