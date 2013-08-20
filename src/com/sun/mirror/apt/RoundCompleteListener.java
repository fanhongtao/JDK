/*
 * @(#)RoundCompleteListener.java	1.1 04/06/25
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.apt;

/**
 * Listener for the completion of a round of annotation processing.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.1 04/06/25
 * @since 1.5
 */
public interface RoundCompleteListener extends AnnotationProcessorListener {
    /**
     * Invoked after all processors for a round have run to completion.
     *
     * @param event An event for round completion
     */
    void roundComplete(RoundCompleteEvent event);
}
