/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.text.resources;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

/**
 * This class used to be the base class for all of the LocaleElements classes.  This has
 * been changed so that all of the LocaleElements classes descend from ListResourceBundle.
 * This class now exists only to allow a way to get the list of available resources.  Even
 * this will be changing in the future.
 *
 * @author Asmus Freytag
 * @author Mark Davis
 * @version 1.27 02/06/02
 */

public class LocaleData {
    /**
     * Returns a list of the installed locales.
     * @param key A resource tag.  Currently, this parameter is ignored.  The obvious
     * intent, however,  is for getAvailableLocales() to return a list of only those
     * locales that contain a resource with the specified resource tag.
     *
     * <p>Before we implement this function this way, however, some thought should be
     * given to whether this is really the right thing to do.  Because of the lookup
     * algorithm, a NumberFormat, for example, is "installed" for all locales.  But if
     * we're trying to put up a list of NumberFormats to choose from, we may want to see
     * only a list of those locales that uniquely define a NumberFormat rather than
     * inheriting one from another locale.  Thus, if fr and fr_CA uniquely define
     * NumberFormat data, but fr_BE doesn't, the user wouldn't see "French (Belgium)" in
     * the list and would go for "French (default)" instead.  Of course, this means
     * "English (United States)" would not be in the list, since it is the default locale.
     * This might be okay, but might be confusing to some users.
     *
     * <p>In addition, the other functions that call getAvailableLocales() don't currently
     * all pass the right thing for "key," meaning that all of these functions should be
     * looked at before anything is done to this function.
     *
     * <p>We recommend that someone take some careful consideration of these issues before
     * modifying this function to pay attention to the "key" parameter.  --rtg 1/26/98
     */
    public static Locale[] getAvailableLocales(String key)
    {
        Locale[] temp = new Locale[localeList.length];
        System.arraycopy(localeList, 0, temp, 0, localeList.length);
        return temp;
    }

    // ========== privates ==========

    private static Vector classPathSegments = new Vector();
    private static Locale[] localeList;
    private static final String PACKAGE = "java.text.resources";
    private static final String PREFIX = "LocaleElements_";
    private static final char ZIPSEPARATOR = '/';

    static {
        String classPath = 
	    (String) java.security.AccessController.doPrivileged(
             new sun.security.action.GetPropertyAction("sun.boot.class.path"));
        String s = 
	    (String) java.security.AccessController.doPrivileged(
             new sun.security.action.GetPropertyAction("java.class.path"));

	// Search combined system and application class path
	if (s != null && s.length() != 0) {
	    classPath += File.pathSeparator + s;
	}
        while (classPath != null && classPath.length() != 0) {
            int i = classPath.lastIndexOf(java.io.File.pathSeparatorChar);
            String dir = classPath.substring(i + 1);
            if (i == -1) {
                classPath = null;
            } else {
                classPath = classPath.substring(0, i);
            }
            classPathSegments.insertElementAt(dir, 0);
        }

        String[] classList = (String[])
	    java.security.AccessController.doPrivileged(
				    new java.security.PrivilegedAction() {
		public Object run() {
		    return getClassList(PACKAGE, PREFIX);
		}
	    });

        int plen = PREFIX.length();
        localeList = new Locale[classList.length];
        for (int i = 0; i < classList.length; i++) {
            int p2 = 0;
            int p1 = classList[i].indexOf('_', plen);
            String lang = "";
            String region = "";
            String var = "";

            if (p1 == -1) {
                lang = classList[i].substring(plen);
            } else {
                lang = classList[i].substring(plen, p1);
                p2 = classList[i].indexOf('_', p1 + 1);
                if (p2 == -1) {
                    region = classList[i].substring(p1 + 1);
                } else {
                    region = classList[i].substring(p1 + 1, p2);
                    if (p2 < classList[i].length())
                            var = classList[i].substring(p2 + 1);
                }
            }
            localeList[i] = new Locale(lang, region, var);
        }
    }

    /**
     * Walk through CLASSPATH and find class list from a package.
     * The class names start with prefix string
     * @param package name, class name prefix
     * @return class list in an array of String
     */
    private static String[] getClassList(String pkgName, String prefix) {
        Vector listBuffer = new Vector();
        String packagePath = pkgName.replace('.', File.separatorChar)
            + File.separatorChar;
        String zipPackagePath = pkgName.replace('.', ZIPSEPARATOR)
            + ZIPSEPARATOR;
        for (int i = 0; i < classPathSegments.size(); i++){
            String onePath = (String) classPathSegments.elementAt(i);
            File f = new File(onePath);
            if (!f.exists())
                continue;
            if (f.isFile())
                scanFile(f, zipPackagePath, listBuffer, prefix);
            else if (f.isDirectory()) {
                String fullPath;
                if (onePath.endsWith(File.separator))
                    fullPath = onePath + packagePath;
                else
                    fullPath = onePath + File.separatorChar + packagePath;
                File dir = new File(fullPath);
                if (dir.exists() && dir.isDirectory())
                    scanDir(dir, listBuffer, prefix);
            }
        }
        String[] classNames = new String[listBuffer.size()];
        listBuffer.copyInto(classNames);
        return classNames;
    }

    private static void addClass (String className, Vector listBuffer, String prefix) {
        if (className != null && className.startsWith(prefix)
                    && !listBuffer.contains(className))
            listBuffer.addElement(className);
    }

    private static String midString(String str, String pre, String suf) {
        String midStr;
        if (str.startsWith(pre) && str.endsWith(suf))
            midStr = str.substring(pre.length(), str.length() - suf.length());
        else
            midStr = null;
        return midStr;
    }

    private static void scanDir(File dir, Vector listBuffer, String prefix) {
        String[] fileList = dir.list();
        for (int i = 0; i < fileList.length; i++) {
            addClass(midString(fileList[i], "", ".class"), listBuffer, prefix);
        }
    }

    private static void scanFile(File f, String packagePath, Vector listBuffer,
                String prefix) {
        try {
            ZipInputStream zipFile = new ZipInputStream(new FileInputStream(f));
            boolean gotThere = false;
            ZipEntry entry;
            while ((entry = zipFile.getNextEntry()) != null) {
                String eName = entry.getName();
                if (eName.startsWith(packagePath)) {
                    gotThere = true;
                    if (eName.endsWith(".class")) {
                        addClass(midString(eName, packagePath, ".class"),
                                listBuffer, prefix);
                    }
                } else {
                    if (gotThere)    // Found the package, now we are leaving
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found:" + e);
        } catch (IOException e) {
            System.out.println("file IO Exception:" + e);
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
    }
}
