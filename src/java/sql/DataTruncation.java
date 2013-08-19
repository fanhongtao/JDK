/*
 * @(#)DataTruncation.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 * An exception that reports a
 * DataTruncation warning (on reads) or throws a DataTruncation exception
 * (on writes) when JDBC unexpectedly truncates a data value.
 *
 * <P>The SQLstate for a <code>DataTruncation</code> is <code>01004</code>.
 */

public class DataTruncation extends SQLWarning {

    /**
     * Creates a <code>DataTruncation</code> object 
	 * with the SQLState initialized
     * to 01004, the reason set to "Data truncation", the
     * vendorCode set to the SQLException default, and
	 * the other fields set to the given values.
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
     * Retrieves the index of the column or parameter that was truncated.
     *
     * <P>This may be -1 if the column or parameter index is unknown, in 
     * which case the <code>parameter</code> and <code>read</code> fields should be ignored.
     *
     * @return the index of the truncated paramter or column value
     */
    public int getIndex() {
	return index;
    }

    /**
     * Indicates whether the value truncated was a parameter value or
	 * a column value.
     *
     * @return <code>true</code> if the value truncated was a parameter;
	 *         <code>false</code> if it was a column value
     */
    public boolean getParameter() {
	return parameter;
    }

    /**
     * Indicates whether or not the value was truncated on a read.
     *
     * @return <code>true</code> if the value was truncated when read from
	 *         the database; <code>false</code> if the data was truncated on a write
     */
    public boolean getRead() {
	return read;
    }

    /**
     * Gets the number of bytes of data that should have been transferred.
     * This number may be approximate if data conversions were being
     * performed.  The value may be <code>-1</code> if the size is unknown.
     *
     * @return the number of bytes of data that should have been transferred
     */
    public int getDataSize() {
	return dataSize;
    }

    /**
     * Gets the number of bytes of data actually transferred.
     * The value may be <code>-1</code> if the size is unknown.
     *
     * @return the number of bytes of data actually transferred
     */
    public int getTransferSize() {
	return transferSize;
    }

	/**
   	* @serial
	*/
    private int index;

	/**
	* @serial
	*/
    private boolean parameter;

	/**
	* @serial
	*/
    private boolean read;	

	/**
	* @serial
	*/
    private int dataSize;

	/**
	* @serial
	*/
    private int transferSize;

}
