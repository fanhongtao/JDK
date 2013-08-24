/*
 * @(#)FileSystemPreferencesFactory.java	1.6 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.prefs;

/**
 * Factory for FileSystemPreferences.  This class allows FileSystemPreferences
 * to be installed as the Preferences implementations via the
 * java.util.prefs.PreferencesFactory system property.
 *
 * @author  Josh Bloch
 * @version 1.6, 11/17/05
 * @see     FileSystemPreferences
 * @see     Preferences
 * @since   1.4
 */

class FileSystemPreferencesFactory implements PreferencesFactory {
    public Preferences userRoot() {
        return FileSystemPreferences.getUserRoot();
    }

    public Preferences systemRoot() {
        return FileSystemPreferences.getSystemRoot();
    }
}
