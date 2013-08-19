/*
 * @(#)BatchUpdateException.java	1.22 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 * An exception thrown when an error
 * occurs during a batch update operation.  In addition to the
 * information provided by {@link SQLException}, a 
 * <code>BatchUpdateException</code> provides the update
 * counts for all commands that were executed successfully during the
 * batch update, that is, all commands that were executed before the error 
 * occurred.  The order of elements in an array of update counts
 * corresponds to the order in which commands were added to the batch.
 * <P>
 * After a command in a batch update fails to execute properly
 * and a <code>BatchUpdateException</code> is thrown, the driver
 * may or may not continue to process the remaining commands in
 * the batch.  If the driver continues processing after a failure,
 * the array returned by the method 
 * <code>BatchUpdateException.getUpdateCounts</code> will have 
 * an element for every command in the batch rather than only
 * elements for the commands that executed successfully before 
 * the error.  In the case where the driver continues processing
 * commands, the array element for any command
 * that failed is <code>Statement.EXECUTE_FAILED</code>.
 * <P>
 * @since 1.2
 */

public class BatchUpdateException extends SQLException {

  /**
   * Constructs a fully-specified <code>BatchUpdateException</code> object,
   * initializing it with the given values.
   * @param reason a description of the error 
   * @param SQLState an X/OPEN code identifying the error
   * @param vendorCode an exception code used by a particular
   * database vendor
   * @param updateCounts an array of <code>int</code>, with each element
   * indicating the update count for a SQL command that executed 
   * successfully before the exception was thrown
   * @since 1.2
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
   * @since 1.2
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
   * @since 1.2
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
   * @since 1.2
   */
  public BatchUpdateException(int[] updateCounts) {
    super();
    this.updateCounts = updateCounts;
  }

  /**
   * Constructs a <code>BatchUpdateException</code> object 
   * with the reason, SQLState, and update count initialized to
   * <code>null</code> and the vendor code initialized to 0.
   * @since 1.2
   */
  public BatchUpdateException() {
    super();
    this.updateCounts = null;
  }

  /**
   * Retrieves the update count for each update statement in the batch
   * update that executed successfully before this exception occurred.
   * A driver that implements batch updates may or may not continue to
   * process the remaining commands in a batch when one of the commands
   * fails to execute properly. If the driver continues processing commands,
   * the array returned by this method will have as many elements as
   * there are commands in the batch; otherwise, it will contain an
   * update count for each command that executed successfully before
   * the <code>BatchUpdateException</code> was thrown.
   *<P>
   * The possible return values for this method were modified for
   * the Java 2 SDK, Standard Edition, version 1.3.  This was done to
   * accommodate the new option of continuing to process commands
   * in a batch update after a <code>BatchUpdateException</code> object
   * has been thrown.
   *
   * @return an array of <code>int</code> containing the update counts
   * for the updates that were executed successfully before this error
   * occurred.  Or, if the driver continues to process commands after an
   * error, one of the following for every command in the batch:
   * <OL>
   * <LI>an update count
   *  <LI><code>Statement.SUCCESS_NO_INFO</code> to indicate that the command
   *     executed successfully but the number of rows affected is unknown
   *  <LI><code>Statement.EXECUTE_FAILED</code> to indicate that the command 
   *     failed to execute successfully
   * </OL>
   * @since 1.3
   */
  public int[] getUpdateCounts() {
    return updateCounts;
  }

  /**
   * The array that describes the outcome of a batch execution.
   * @serial
   * @since 1.2
   */
  private int[] updateCounts;
}
