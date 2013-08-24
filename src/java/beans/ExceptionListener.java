/*
 * @(#)ExceptionListener.java	1.7 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.beans;

/**
 * An ExceptionListener is notified of internal exceptions. 
 * 
 * @since 1.4
 *
 * @version 1.7 11/17/05
 * @author Philip Milne
 */        
public interface ExceptionListener { 
    /**
     * This method is called when a recoverable exception has 
     * been caught. 
     *
     * @param e The exception that was caught. 
     * 
     */
    public void exceptionThrown(Exception e); 
}















































