/*
 * @(#)BufferStrategy.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.image;

import java.awt.BufferCapabilities;
import java.awt.Graphics;
import java.awt.Image;

/**
 * The <code>BufferStrategy</code> class represents the mechanism with which
 * to organize complex memory on a particular <code>Canvas</code> or
 * <code>Window</code>.  Hardware and software limitations determine whether and
 * how a particular buffer strategy can be implemented.  These limitations
 * are detectible through the capabilities of the
 * <code>GraphicsConfiguration</code> used when creating the
 * <code>Canvas</code> or <code>Window</code>.
 * <p>
 * It is worth noting that the terms <i>buffer</i> and <i>surface</i> are meant
 * to be synonymous: an area of contiguous memory, either in video device
 * memory or in system memory.
 * <p>
 * There are several types of complex buffer strategies;
 * sequential ring buffering, blit buffering, and stereo buffering are
 * common types.  Sequential ring buffering (i.e., double or triple
 * buffering) is the most common; an application draws to a single <i>back
 * buffer</i> and then moves the contents to the front (display) in a single
 * step, either by copying the data or moving the video pointer.
 * Moving the video pointer exchanges the buffers so that the first buffer
 * drawn becomes the <i>front buffer</i>, or what is currently displayed on the
 * device; this is called <i>page flipping</i>.
 * <p>
 * Alternatively, the contents of the back buffer can be copied, or
 * <i>blitted</i> forward in a chain instead of moving the video pointer.
 * <p>
 * <pre>
 * Double buffering:
 *
 *                    ***********         ***********
 *                    *         * ------> *         *
 * [To display] <---- * Front B *   Show  * Back B. * <---- Rendering
 *                    *         * <------ *         *
 *                    ***********         ***********
 *
 * Triple buffering:
 *
 * [To      ***********         ***********        ***********
 * display] *         * --------+---------+------> *         *
 *    <---- * Front B *   Show  * Mid. B. *        * Back B. * <---- Rendering
 *          *         * <------ *         * <----- *         *
 *          ***********         ***********        ***********
 *
 * </pre>
 * <p>
 * Stereo buffering is for hardware that supports rendering separate images for
 * a left and right eye.  It is similar to sequential ring buffering, but
 * there are two buffer chains, one for each eye.  Both buffer chains flip
 * simultaneously:
 *
 * <pre>
 * Stereo buffering:
 *
 *                     ***********         ***********
 *                     *         * ------> *         *
 * [To left eye] <---- * Front B *         * Back B. * <---- Rendering
 *                     *         * <------ *         *
 *                     ***********         ***********
 *                                  Show
 *                     ***********         ***********
 *                     *         * ------> *         *
 * [To right eye] <--- * Front B *         * Back B. * <---- Rendering
 *                     *         * <------ *         *
 *                     ***********         ***********
 * </pre>
 * <p>
 * Here is an example of how buffer strategies can be created and used:
 * <pre><code>
 *
 * // Check the capabilities of the GraphicsConfiguration
 * ...
 *
 * // Create our component
 * Window w = new Window(gc);
 *
 * // Show our window
 * w.setVisible(true);
 *
 * // Create a general double-buffering strategy
 * w.createBufferStrategy(2);
 * BufferStrategy strategy = w.getBufferStrategy();
 *
 * // Render loop
 * while (!done) {
 *    Graphics g = strategy.getDrawGraphics();
 *    // Draw to graphics
 *    ...
 *    strategy.show();
 * }
 *
 * // Dispose the window
 * w.setVisible(false);
 * w.dispose();
 * </code></pre>
 *
 * @see java.awt.Component
 * @see java.awt.GraphicsConfiguration
 * @author Michael Martak
 * @since 1.4
 */
public abstract class BufferStrategy {
    
    /**
     * @return the buffering capabilities of this strategy
     */
    public abstract BufferCapabilities getCapabilities();

    /**
     * @return the graphics on the drawing buffer.  This method may not
     * be synchronized for performance reasons; use of this method by multiple
     * threads should be handled at the application level.  Disposal of the
     * graphics object obtained must be handled by the application.
     */
    public abstract Graphics getDrawGraphics();

    /**
     * Returns whether the drawing buffer was lost since the last call to
     * <code>getDrawGraphics</code>.  Since the buffers in a buffer strategy
     * are usually type <code>VolatileImage</code>, they may become lost.
     * For a discussion on lost buffers, see <code>VolatileImage</code>.
     * @see java.awt.image.VolatileImage
     */
    public abstract boolean contentsLost();

    /**
     * Returns whether the drawing buffer was recently restored from a lost
     * state and reinitialized to the default background color (white).
     * Since the buffers in a buffer strategy are usually type
     * <code>VolatileImage</code>, they may become lost.  If a surface has
     * been recently restored from a lost state since the last call to
     * <code>getDrawGraphics</code>, it may require repainting.
     * For a discussion on lost buffers, see <code>VolatileImage</code>.
     * @see java.awt.image.VolatileImage
     */
    public abstract boolean contentsRestored();

    /**
     * Makes the next available buffer visible by either copying the memory
     * (blitting) or changing the display pointer (flipping).
     */
    public abstract void show();
}

