/*
 * @(#)PrintJobAttributeListener.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.print.event;

/**
  * Implementations of this interface are attached to a
  * {@link javax.print.DocPrintJob DocPrintJob} to monitor
  * the status of attribute changes associated with the print job.
  *
  */
public interface PrintJobAttributeListener {

    /**
     * Notifies the listener of a change in some print job attributes.
     * One example of an occurrence triggering this event is if the 
     * {@link javax.print.attribute.standard.JobState JobState} 
     * attribute changed from
     * <code>PROCESSING</code> to <code>PROCESSING_STOPPED</code>.
     * @param pjae the event.
     */
    public void attributeUpdate(PrintJobAttributeEvent pjae) ;
 
}
