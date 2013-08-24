/*
 * @(#)JDBCAdapter.java	1.17 05/11/17
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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
 * @(#)JDBCAdapter.java	1.17 05/11/17
 */

/**
 * An adaptor, transforming the JDBC interface to the TableModel interface.
 *
 * @version 1.20 09/25/97
 * @author Philip Milne
 */

import java.util.Vector;
import java.sql.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;

public class JDBCAdapter extends AbstractTableModel {
    Connection          connection;
    Statement           statement;
    ResultSet           resultSet;
    String[]            columnNames = {};
    Vector		rows = new Vector();
    ResultSetMetaData   metaData;

    public JDBCAdapter(String url, String driverName,
                       String user, String passwd) {
        try {
            Class.forName(driverName);
            System.out.println("Opening db connection");

            connection = DriverManager.getConnection(url, user, passwd);
            statement = connection.createStatement();
        }
        catch (ClassNotFoundException ex) {
            System.err.println("Cannot find the database driver classes.");
            System.err.println(ex);
        }
        catch (SQLException ex) {
            System.err.println("Cannot connect to this database.");
            System.err.println(ex);
        }
     }

    public void executeQuery(String query) {
        if (connection == null || statement == null) {
            System.err.println("There is no database to execute the query.");
            return;
        }
        try {
            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();

            int numberOfColumns =  metaData.getColumnCount();
            columnNames = new String[numberOfColumns];
            // Get the column names and cache them.
            // Then we can close the connection.
            for(int column = 0; column < numberOfColumns; column++) {
                columnNames[column] = metaData.getColumnLabel(column+1);
            }

            // Get all rows.
            rows = new Vector();
            while (resultSet.next()) {
                Vector newRow = new Vector();
                for (int i = 1; i <= getColumnCount(); i++) {
	            newRow.addElement(resultSet.getObject(i));
                }
                rows.addElement(newRow);
            }
            //  close(); Need to copy the metaData, bug in jdbc:odbc driver.
            fireTableChanged(null); // Tell the listeners a new table has arrived.
        }
        catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    public void close() throws SQLException {
        System.out.println("Closing db connection");
        resultSet.close();
        statement.close();
        connection.close();
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    //////////////////////////////////////////////////////////////////////////
    //
    //             Implementation of the TableModel Interface
    //
    //////////////////////////////////////////////////////////////////////////

    // MetaData

    public String getColumnName(int column) {
        if (columnNames[column] != null) {
            return columnNames[column];
        } else {
            return "";
        }
    }

    public Class getColumnClass(int column) {
        int type;
        try {
            type = metaData.getColumnType(column+1);
        }
        catch (SQLException e) {
            return super.getColumnClass(column);
        }

        switch(type) {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
            return String.class;

        case Types.BIT:
            return Boolean.class;

        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER:
            return Integer.class;

        case Types.BIGINT:
            return Long.class;

        case Types.FLOAT:
        case Types.DOUBLE:
            return Double.class;

        case Types.DATE:
            return java.sql.Date.class;

        default:
            return Object.class;
        }
    }

    public boolean isCellEditable(int row, int column) {
        try {
            return metaData.isWritable(column+1);
        }
        catch (SQLException e) {
            return false;
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    // Data methods

    public int getRowCount() {
        return rows.size();
    }

    public Object getValueAt(int aRow, int aColumn) {
        Vector row = (Vector)rows.elementAt(aRow);
        return row.elementAt(aColumn);
    }

    public String dbRepresentation(int column, Object value) {
        int type;

        if (value == null) {
            return "null";
        }

        try {
            type = metaData.getColumnType(column+1);
        }
        catch (SQLException e) {
            return value.toString();
        }

        switch(type) {
        case Types.INTEGER:
        case Types.DOUBLE:
        case Types.FLOAT:
            return value.toString();
        case Types.BIT:
            return ((Boolean)value).booleanValue() ? "1" : "0";
        case Types.DATE:
            return value.toString(); // This will need some conversion.
        default:
            return "\""+value.toString()+"\"";
        }

    }

    public void setValueAt(Object value, int row, int column) {
        try {
            String tableName = metaData.getTableName(column+1);
            // Some of the drivers seem buggy, tableName should not be null.
            if (tableName == null) {
                System.out.println("Table name returned null.");
            }
            String columnName = getColumnName(column);
            String query =
                "update "+tableName+
                " set "+columnName+" = "+dbRepresentation(column, value)+
                " where ";
            // We don't have a model of the schema so we don't know the
            // primary keys or which columns to lock on. To demonstrate
            // that editing is possible, we'll just lock on everything.
            for(int col = 0; col<getColumnCount(); col++) {
                String colName = getColumnName(col);
                if (colName.equals("")) {
                    continue;
                }
                if (col != 0) {
                    query = query + " and ";
                }
                query = query + colName +" = "+
                    dbRepresentation(col, getValueAt(row, col));
            }
            System.out.println(query);
            System.out.println("Not sending update to database");
            // statement.executeQuery(query);
        }
        catch (SQLException e) {
            //     e.printStackTrace();
            System.err.println("Update failed");
        }
        Vector dataRow = (Vector)rows.elementAt(row);
        dataRow.setElementAt(value, column);

    }
}
