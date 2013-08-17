/*
 * @(#)CreateSuppliers.java	1.3 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.net.URL;
import java.sql.*;
     
public class CreateSuppliers {

	public static void main(String args[]) {
		String url = "jdbc:sybase://dbtest:1455/spring";
		Connection con;
		String createString;

	  /*****************
      DataTable dt = new JDBCAdapter(
            "jdbc:sybase://dbtest:1455/spring",
            // "SELECT * FROM test",
            "SELECT * FROM COFFEES",
            "connect.sybase.SybaseDriver",
            "guest",
            "trustworthy");
	  ****************************/


		createString = "create table SUPPLIERS " + 
						"(SUP_ID int, " +
						"SUP_NAME varchar(40), " +
						"STREET varchar(40), " +
						"CITY varchar(20), " +
						"STATE char(2), ZIP char(5))";
	
		Statement stmt;
	
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
	   		stmt.executeUpdate(createString);
	
			stmt.close();
			con.close();
	
		} catch(SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
	}
}

