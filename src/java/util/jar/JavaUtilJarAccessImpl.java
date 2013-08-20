/*
 * @(#)JavaUtilJarAccessImpl.java	1.4 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.jar;

import java.io.IOException;
import sun.misc.JavaUtilJarAccess;

class JavaUtilJarAccessImpl implements JavaUtilJarAccess {
    public boolean jarFileHasClassPathAttribute(JarFile jar) throws IOException {
        return jar.hasClassPathAttribute();
    }
}
