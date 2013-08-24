/*
 * DirectoryScannerMXBean.java
 *
 * Created on July 10, 2006, 4:20 PM
 *
 * @(#)DirectoryScannerMXBean.java	1.2 06/08/02
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

package com.sun.jmx.examples.scandir;

import com.sun.jmx.examples.scandir.ScanManagerMXBean.ScanState;
import com.sun.jmx.examples.scandir.config.DirectoryScannerConfig;
import java.io.IOException;
import javax.management.InstanceNotFoundException;

/**
 * A <code>DirectoryScannerMXBean</code> is an MBean that
 * scans a file system starting at a given root directory,
 * and then looks for files that match a given criteria.
 * <p>
 * When such a file is found, the <code>DirectoryScannerMXBean</code> takes 
 * the actions for which it was configured: see {@link #scan scan()}.
 * <p>
 * <code>DirectoryScannerMXBeans</code> are created, initialized, and
 * registered by the {@link ScanManagerMXBean}. 
 * The {@link ScanManagerMXBean} will also schedule and run them in 
 * background by calling their {@link #scan} method.
 * </p>
 * @author Sun Microsystems, 2006 - All rights reserved.
 */
public interface DirectoryScannerMXBean {
    /**
     * Get The {@link DirectoryScanner} state.
     * @return the current state of the <code>DirectoryScanner</code>.
     * @throws IOException A connection problem occurred when accessing
     *                     the underlying resource.
     * @throws InstanceNotFoundException The underlying MBean is not
     *         registered in the MBeanServer.
     **/
    public ScanState getState() 
        throws IOException, InstanceNotFoundException;
   
    /**
     * Stops the current scan if {@link ScanState#RUNNING running}.
     * After this method completes the state of the application will
     * be {@link ScanState#STOPPED STOPPED}.
     * @throws IOException A connection problem occurred when accessing
     *                     the underlying resource.
     * @throws InstanceNotFoundException The underlying MBean is not
     *         registered in the MBeanServer.
     **/
    public void stop() 
        throws IOException, InstanceNotFoundException;
    
    /**
     * Scans the file system starting at the specified {@link #getRootDirectory 
     * root directory}.
     * <p>If a file that matches this <code>DirectoryScannerMXBean</code> 
     * {@link #getConfiguration} criteria is found, 
     * the <code>DirectoryScannerMXBean</code> takes the {@link 
     * DirectoryScannerConfig#getActions() actions} for which 
     * it was {@link #getConfiguration configured}: emit a notification, 
     * <i>and or</i> log a {@link 
     * com.sun.jmx.examples.scandir.config.ResultRecord} for this file, 
     * <i>and or</i> delete that file. 
     * </p>
     * <p>
     * The code that would actually delete the file is commented out - so that 
     * nothing valuable is lost if this example is run by mistake on the wrong 
     * set of directories.
     * </p>
     * <p>This method returns only when the directory scan is completed, or
     *    if it was {@link #stop stopped} by another thread.
     * </p>
     * @throws IllegalStateException if already {@link ScanState#RUNNING}
     * @throws IOException A connection problem occurred when accessing
     *                     the underlying resource.
     * @throws InstanceNotFoundException The underlying MBean is not
     *         registered in the MBeanServer.
     **/
    public void scan() 
        throws IOException, InstanceNotFoundException;
    
    /**
     * Gets the root directory at which this <code>DirectoryScannerMXBean</code>
     * will start scanning the file system.
     * <p>
     * This is a shortcut to {@link #getConfiguration 
     * getConfiguration()}.{@link 
     * DirectoryScannerConfig#getRootDirectory
     * getRootDirectory()}.
     * </p>
     * @return This <code>DirectoryScannerMXBean</code> root directory.
     * @throws IOException A connection problem occurred when accessing
     *                     the underlying resource.
     * @throws InstanceNotFoundException The underlying MBean is not
     *         registered in the MBeanServer.
     **/
    public String getRootDirectory()
        throws IOException, InstanceNotFoundException;

    /**
     * The configuration data from which this {@link DirectoryScanner} was
     * created. 
     * <p>
     * You cannot change this configuration here. You can however 
     * {@link ScanDirConfigMXBean#setConfiguration modify} the
     * {@link ScanDirConfigMXBean} configuration, and ask the 
     * {@link ScanManagerMXBean} to {@link ScanManagerMXBean#applyConfiguration
     * apply} it. This will get all <code>DirectoryScannerMXBean</code>
     * replaced by new MBeans created from the modified configuration.
     * </p>
     * 
     * @return This <code>DirectoryScannerMXBean</code> configuration data.
     * @throws IOException A connection problem occurred when accessing
     *                     the underlying resource.
     * @throws InstanceNotFoundException The underlying MBean is not
     *         registered in the MBeanServer.
     **/
    public DirectoryScannerConfig getConfiguration()
        throws IOException, InstanceNotFoundException;
    
    /**
     * A short string describing what's happening in current/latest scan.
     * @return a short info string.
     * @throws IOException A connection problem occurred when accessing
     *                     the underlying resource.
     * @throws InstanceNotFoundException The underlying MBean is not
     *         registered in the MBeanServer.
     **/
    public String getCurrentScanInfo() 
        throws IOException, InstanceNotFoundException;
}


