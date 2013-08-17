/*
 * @(#)BatchUpdateException.java	1.11 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 *  JDBC 2.0
 *
 *  <P>
 *  An exception thrown when an error
 *  occurs during a batch update operation.  In addition to the
 *  information provided by {@link SQLException}, a 
 *  <code>BatchUpdateException</code> provides the update
 *  counts for all commands that were executed successfully during the
 *  batch update, that is, all commands that were executed before the error 
 *  occurred.  The order of elements in an array of update counts
 *  corresponds to the order in which commands were added to the batch.
 */

public class BatchUpdateException extends SQLException {

  /**
   * Constructs a fully specified <code>BatchUpdateException</code>.
   * @param reason a description of the error 
   * @param SQLState an X/OPEN code identifying the error
   * @param vendorCode an exception code for a particular
   * database vendor
   * @param updateCounts an array of <code>int</code>, with each element
   * indicating the update count for a SQL command that executed 
   * successfully before the exception was thrown
   */
  public BatchUpdateException( String reason, String SQLState, int vendorCode, 
			       int[] updateCounts ) {
    super(reason, SQLState, vendorCode);
    this.updateCounts = updateCounts;
  }

  /**
   * Constructs a <code>BatchUpdateException</code> initialized with 
   * the given arguments (<code>reason</code>,
   * <code>SQLState</code>, and <code>updateCounts</code>) and 0 for the vendor
   * code.
   * @param reason a description of the exception 
   * @param SQLState an X/OPEN code identifying the exception 
   * @param updateCounts an array of <code>int</code>, with each element  
   * indicating the update count for a SQL command that executed
   * successfully before the exception was thrown  
   */
  public BatchUpdateException(String reason, String SQLState, 
			      int[] updateCounts) {
    super(reason, SQLState);
    this.updateCounts = updateCounts;
  }

  /**
   * Constructs a <code>BatchUpdateException</code> initialized with
   * <code>reason</code>, <code>updateCounts</code> and <code>null</code>
   * for the SQLState and 0 for the vendorCode.
   * @param reason a description of the exception 
   * @param updateCounts an array of <code>int</code>, with each element
   * indicating the update count for a SQL command that executed
   * successfully before the exception was thrown
   */
  public  BatchUpdateException(String reason, int[] updateCounts) {
    super(reason);
    this.updateCounts = updateCounts;
  }

  /**
   * Constructs a <code>BatchUpdateException</code> initialized to 
   * <code>null</code> for the reason and SQLState and 0 for the
   * vendor code.
   * @param updateCounts an array of <code>int</code>, with each element
   * indicating the update count for a SQL command that executed
   * successfully before the exception was thrown
   */
  public BatchUpdateException(int[] updateCounts) {
    super();
    this.updateCounts = updateCounts;
  }

  /**
   * Constructs a <code>BatchUpdateException</code> object 
   * with the reason, SQLState, and update count initialized to
   * <code>null</code> and the vendor code initialized to 0.
   */
  public BatchUpdateException() {
    super();
    this.updateCounts = null;
  }

  /**
   * Retrieves the update count for each update statement in the batch
   * update that executed successfully before this exception occurred.
   * @return an array of <code>int</code> containing the update counts
   * for the updates that were executed successfully before this error
   * occurred
   */
  public int[] getUpdateCounts() {
    return updateCounts;
  }

  /**
   * @serial
   */
  private int[] updateCounts;
}
