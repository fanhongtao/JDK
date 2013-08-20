/*
 * @(#)OperatingSystemMXBean.java	1.9 04/04/20
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.management;

/**
 * The management interface for the operating system on which
 * the Java virtual machine is running.
 *
 * <p> A Java virtual machine has a single instance of the implementation
 * class of this interface.  This instance implementing this interface is
 * an <a href="ManagementFactory.html#MXBean">MXBean</a>
 * that can be obtained by calling
 * the {@link ManagementFactory#getOperatingSystemMXBean} method or
 * from the {@link ManagementFactory#getPlatformMBeanServer
 * platform <tt>MBeanServer</tt>} method.
 *
 * <p>The <tt>ObjectName</tt> for uniquely identifying the MXBean for
 * the operating system within an MBeanServer is:
 * <blockquote>
 *    {@link ManagementFactory#OPERATING_SYSTEM_MXBEAN_NAME
 *      <tt>java.lang:type=OperatingSystem</tt>}
 * </blockquote>
 *
 * <p> This interface defines several convenient methods for accessing 
 * system properties about the operating system on which the Java 
 * virtual machine is running.
 *
 * @see <a href="../../../javax/management/package-summary.html">
 *      JMX Specification.</a>
 * @see <a href="package-summary.html#examples">
 *      Ways to Access MXBeans</a>
 *
 * @author  Mandy Chung
 * @version 1.9, 04/20/04 
 * @since   1.5
 */
public interface OperatingSystemMXBean {
    /**
     * Returns the operating system name. 
     * This method is equivalent to <tt>System.getProperty("os.name")</tt>.
     *
     * @return the operating system name.
     *
     * @throws  java.lang.SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
     * @see java.lang.System#getProperty
     */
    public String getName();

    /**
     * Returns the operating system architecture. 
     * This method is equivalent to <tt>System.getProperty("os.arch")</tt>.
     *
     * @return the operating system architecture.
     *
     * @throws  java.lang.SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
     * @see java.lang.System#getProperty
     */
    public String getArch();

    /**
     * Returns the operating system version. 
     * This method is equivalent to <tt>System.getProperty("os.version")</tt>.
     *
     * @return the operating system version.
     *
     * @throws  java.lang.SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
     * @see java.lang.System#getProperty
     */
    public String getVersion();

    /**
     * Returns the number of processors available to the Java virtual machine.
     * This method is equivalent to the {@link Runtime#availableProcessors()}
     * method.
     * <p> This value may change during a particular invocation of
     * the virtual machine.
     *
     * @return  the number of processors available to the virtual
     *          machine; never smaller than one.
     */
    public int getAvailableProcessors();
}

