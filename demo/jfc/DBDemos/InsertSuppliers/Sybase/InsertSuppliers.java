/*
 * @(#)InsertSuppliers.java	1.3 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.sql.*;
     
public class InsertSuppliers {

	public static void main(String args[]) {
		  
	  /*****************
      DataTable dt = new JDBCAdapter(
            "jdbc:sybase://dbtest:1455/spring",
            // "SELECT * FROM test",
            "SELECT * FROM COFFEES",
            "connect.sybase.SybaseDriver",
            "guest",
            "trustworthy");
	  ****************************/


		String url = "jdbc:sybase://dbtest:1455/spring";
		Connection con;
		Statement stmt;
		String query = "select SUP_NAME, SUP_ID from SUPPLIERS";
	
		try {
			Class.forName("connect.sybase.SybaseDriver");
	
		} catch(java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}	
	
		try {
			con = DriverManager.getConnection(url, 
									 "guest", "trustworthy");
	
			stmt = con.createStatement();							
	
			stmt.executeUpdate("insert into SUPPLIERS " +
	                 "values(49, 'Superior Coffee', '1 Party Place', " +
				 "'Mendocino', 'CA', '95460')");
		
			stmt.executeUpdate("insert into SUPPLIERS " +
				"values(101, 'Acme, Inc.', '99 Market Street', " +
				"'Groundsville', 'CA', '95199')");
	
			stmt.executeUpdate("insert into SUPPLIERS " +
	                 "values(150, 'The High Ground', '100 Coffee Lane', " +
				 "'Meadows', 'CA', '93966')");
	
			ResultSet rs = stmt.executeQuery(query);
	
			System.out.println("Suppliers and their ID Numbers:");
			while (rs.next()) {
				String s = rs.getString("SUP_NAME");
				int n = rs.getInt("SUP_ID");
				System.out.println(s + "   " + n);
			}
	
			stmt.close();
			con.close();
	
		} catch(SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
	}
}

