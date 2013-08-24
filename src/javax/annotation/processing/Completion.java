/*
 * @(#)Completion.java	1.2 06/07/31
 *
 * Copyright 2006 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package javax.annotation.processing;

/**
 * A suggested {@linkplain Processor#getCompletions <em>completion</em>} for an
 * annotation.  A completion is text meant to be inserted into a
 * program as part of an annotation.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @author Peter von der Ah&eacute;
 * @version 1.2 06/07/31
 * @since 1.6
 */
public interface Completion {

    /**
     * Returns the text of the suggested completion.
     * @return the text of the suggested completion.
     */
    String getValue();

    /**
     * Returns an informative message about the completion.
     * @return an informative message about the completion.
     */
    String getMessage();
}
