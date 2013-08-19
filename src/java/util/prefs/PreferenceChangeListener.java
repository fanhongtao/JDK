/*
 * @(#)PreferenceChangeListener.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.prefs;

/**
 * A listener for receiving preference change events.
 *
 * @author  Josh Bloch
 * @version 1.3, 01/23/03
 * @see Preferences
 * @see PreferenceChangeEvent
 * @see NodeChangeListener
 * @since   1.4
 */
public interface PreferenceChangeListener extends java.util.EventListener {
    /**
     * This method gets called when a preference is added, removed or when
     * its value is changed.
     * <p>
     * @param evt A PreferenceChangeEvent object describing the event source 
     *   	and the preference that has changed.
     */
    void preferenceChange(PreferenceChangeEvent evt);
}
