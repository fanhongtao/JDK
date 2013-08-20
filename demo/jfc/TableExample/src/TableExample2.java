/*
 * @(#)TableExample2.java	1.16 04/07/26
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
 * @(#)TableExample2.java	1.16 04/07/26
 */

/**
 * A minimal example, using the JTable to view data from a database.
 *
 * @version 1.20 09/25/97
 * @author Philip Milne
 */

import javax.swing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;

public class TableExample2 {

    public TableExample2(String URL, String driver, String user,
                         String passwd, String query) {
        JFrame frame = new JFrame("Table");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}});
        JDBCAdapter dt = new JDBCAdapter(URL, driver, user, passwd);
        dt.executeQuery(query);

        // Create the table
        JTable tableView = new JTable(dt);

        JScrollPane scrollpane = new JScrollPane(tableView);
        scrollpane.setPreferredSize(new Dimension(700, 300));

        frame.getContentPane().add(scrollpane);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Needs database parameters eg. ...");
            System.err.println("java TableExample2 \"jdbc:sybase://dbtest:1455/pubs2\" \"connect.sybase.SybaseDriver\" guest trustworthy \"select * from titles\"");
            return;
        }
        new TableExample2(args[0], args[1], args[2], args[3], args[4]);
    }
}
