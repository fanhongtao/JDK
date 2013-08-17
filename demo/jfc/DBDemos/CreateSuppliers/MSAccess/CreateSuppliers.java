/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.net.URL;
import java.sql.*;
     
public class CreateSuppliers {

	public static void main(String args[]) {
		String url = "jdbc:odbc:CafeJava";
		Connection con;
		String createString;
		createString = "create table SUPPLIERS " + 
						"(SUP_ID int, " +
						"SUP_NAME varchar(40), " +
						"STREET varchar(40), " +
						"CITY varchar(20), " +
						"STATE char(2), ZIP char(5))";
	
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

