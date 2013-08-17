/*
 * @(#)GraphicsDevice.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */


package java.awt;

/**
 * The <code>GraphicsDevice</code> class describes the graphics devices
 * that might be available in a particular graphics environment.  These
 * include screen and printer devices. Note that there can be many screens
 * and many printers in an instance of {@link GraphicsEnvironment}. Each
 * graphics device has one or more {@link GraphicsConfiguration} objects
 * associated with it.  These objects specify the different configurations
 * in which the <code>GraphicsDevice</code> can be used.  
 * @see GraphicsEnvironment
 * @see GraphicsConfiguration
 * @version 10 Feb 1997
 */
public abstract class GraphicsDevice {
    /**
     * This is an abstract class that cannot be instantiated directly.
     * Instances must be obtained from a suitable factory or query method.
     * @see GraphicsEnvironment#getScreenDevices
     * @see GraphicsEnvironment#getDefaultScreenDevice
     * @see GraphicsConfiguration#getDevice
     */
    protected GraphicsDevice() {
    }

    /**
     * Device is a raster screen.
     */
    public final static int TYPE_RASTER_SCREEN		= 0;

    /**
     * Device is a printer.
     */
    public final static int TYPE_PRINTER		= 1;

    /**
     * Device is an image buffer.  This buffer can reside in device
     * or system memory but it is not physically viewable by the user.
     */
    public final static int TYPE_IMAGE_BUFFER           = 2;
    
    /**
     * Returns the type of this <code>GraphicsDevice</code>.
     * @return the type of this <code>GraphicsDevice</code>, which can
     * either be TYPE_RASTER_SCREEN, TYPE_PRINTER or TYPE_IMAGE_BUFFER.
     * @see #TYPE_RASTER_SCREEN
     * @see #TYPE_PRINTER
     * @see #TYPE_IMAGE_BUFFER
     */
    public abstract int getType();

    /**
     * Returns the identification string associated with this 
     * <code>GraphicsDevice</code>.
     * @return a <code>String</code> that is the identification
     * of this <code>GraphicsDevice</code>.
     */
    public abstract String getIDstring();
    
    /**
     * Returns all of the <code>GraphicsConfiguration</code>
     * objects associated with this <code>GraphicsDevice</code>.
     * @return an array of <code>GraphicsConfiguration</code>
     * objects that are associated with this 
     * <code>GraphicsDevice</code>.
     */
    public abstract GraphicsConfiguration[] getConfigurations();

    /**
     * Returns the default <code>GraphicsConfiguration</code>
     * associated with this <code>GraphicsDevice</code>.
     * @return the default <code>GraphicsConfiguration</code>
     * of this <code>GraphicsDevice</code>.
     */
    public abstract GraphicsConfiguration getDefaultConfiguration();

    /**
     * Returns the "best" configuration possible that passes the
     * criteria defined in the {@link GraphicsConfigTemplate}.
     * @param gct the <code>GraphicsConfigTemplate</code> object
     * used to obtain a valid <code>GraphicsConfiguration</code>
     * @return a <code>GraphicsConfiguration</code> that passes
     * the criteria defined in the specified
     * <code>GraphicsConfigTemplate</code>.
     * @see GraphicsConfigTemplate
     */
    public GraphicsConfiguration
           getBestConfiguration(GraphicsConfigTemplate gct) {
        GraphicsConfiguration[] configs = getConfigurations();
        return gct.getBestConfiguration(configs);
    }
 
}
