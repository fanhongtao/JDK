/*
 * @(#)InsertSuppliers.java	1.2 99/04/23
 *
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
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

