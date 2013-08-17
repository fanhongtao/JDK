/*
 * @(#)CannotRedoException.java	1.13 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.undo;

/**
 * Thrown when an UndoableEdit is told to <code>redo()</code> and can't.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.5 07/16/97
 * @author Ray Ryan
 */
public class CannotRedoException extends RuntimeException {
}

