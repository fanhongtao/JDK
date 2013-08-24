/*
 * @(#)OptionChecker.java	1.3 06/04/08
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.tools;

/**
 * Interface for recognizing options.
 *
 * @author Peter von der Ah&eacute;
 * @since 1.6
 */
public interface OptionChecker {

    /**
     * Determines if the given option is supported and if so, the
     * number of arguments the option takes.
     *
     * @param option an option
     * @return the number of arguments the given option takes or -1 if
     * the option is not supported
     */
    int isSupportedOption(String option);

}
