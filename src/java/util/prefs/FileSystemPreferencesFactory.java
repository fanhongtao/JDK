/*
 * @(#)FileSystemPreferencesFactory.java	1.2 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.prefs;

/**
 * Factory for FileSystemPreferences.  This class allows FileSystemPreferences
 * to be installed as the Preferences implementations via the
 * java.util.prefs.PreferencesFactory system property.
 *
 * @author  Josh Bloch
 * @version 1.2, 12/03/01
 * @see     FileSystemPreferences
 * @see     Preferences
 * @since   1.4
 */

class FileSystemPreferencesFactory implements PreferencesFactory {
    public Preferences userRoot() {
        return FileSystemPreferences.userRoot;
    }

    public Preferences systemRoot() {
        return FileSystemPreferences.systemRoot;
    }
}
