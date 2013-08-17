/*
 * @(#)CreateCoffees.java	1.3 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.sql.*;
     
// Create the Coffee Table.
public class CreateCoffees {

	public static void main(String args[]) {		  
		  
		String url = "jdbc:odbc:CafeJava";
		Connection con;
		String createString;
		createString = "create table COFFEES " +
							"(COF_NAME varchar(32), " +
							"SUP_ID int, " +
							"PRICE float, " +
							"SALES int, " +
							"TOTAL int)";
		Statement stmt;
	
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

		} catch(java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: "); 
			System.err.println(e.getMessage());
		}

		try {
			con = DriverManager.getConnection(url, 
									 "Admin", "duke1");
	
			stmt = con.createStatement();							
	   		    stmt.executeUpdate(createString);
	
			stmt.close();
			con.close();
	
		} catch(SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}

	}
}

