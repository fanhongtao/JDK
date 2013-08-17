/*
 * @(#)DataTruncation.java	1.6 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.sql;

/**
 * <P>When JDBC unexpectedly truncates a data value, it reports a
 * DataTruncation warning (on reads) or throws a DataTruncation exception
 * (on writes).
 *
 * <P>The SQLstate for a DataTruncation is "01004".
 */

public class DataTruncation extends SQLWarning {

    /**
     * <P>Create a DataTruncation object. The SQLState is initialized
     * to 01004, the reason is set to "Data truncation" and the
     * vendorCode is set to the SQLException default.
     *
     * @param index The index of the parameter or column value
     * @param parameter true if a parameter value was truncated
     * @param read true if a read was truncated
     * @param dataSize the original size of the data
     * @param transferSize the size after truncation 
     */
    public DataTruncation(int index, boolean parameter, 
			  boolean read, int dataSize, 
			  int transferSize) {
	super("Data truncation", "01004");
	this.index = index;
	this.parameter = parameter;
	this.read = read;
	this.dataSize = dataSize;
	this.transferSize = transferSize;
	DriverManager.println("    DataTruncation: index(" + index + 
			      ") parameter(" + parameter +
			      ") read(" + read +
			      ") data size(" + dataSize +
			      ") transfer size(" + transferSize + ")");
    }

    /**
     * Get the index of the column or parameter that was truncated.
     *
     * <P>This may be -1 if the column or parameter index is unknown, in 
     * which case the "parameter" and "read" fields should be ignored.
     *
     * @return the index of the truncated paramter or column value.
     */
    public int getIndex() {
	return index;
    }

    /**
     * Is this a truncated parameter value?
     *
     * @return True if the value was a parameter; false if it was a column value.
     */
    public boolean getParameter() {
	return parameter;
    }

    /**
     * Was this a read truncation?
     *
     * @return True if the value was truncated when read from the database; false
     * if the data was truncated on a write.
     */
    public boolean getRead() {
	return read;
    }

    /**
     * Get the number of bytes of data that should have been transferred.
     * This number may be approximate if data conversions were being
     * performed.  The value may be "-1" if the size is unknown.
     *
     * @return the number of bytes of data that should have been transferred
     */
    public int getDataSize() {
	return dataSize;
    }

    /**
     * Get the number of bytes of data actually transferred.
     * The value may be "-1" if the size is unknown.
     *
     * @return the number of bytes of data actually transferred
     */
    public int getTransferSize() {
	return transferSize;
    }

    private int index;
    private boolean parameter;
    private boolean read;	
    private int dataSize;
    private int transferSize;

}
