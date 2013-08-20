/*
 * @(#)CachedPainter.java	1.2 04/02/15
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.metal;

import java.awt.*;
import java.awt.image.*;
import javax.swing.Icon;
import java.lang.ref.SoftReference;
import java.util.*;

/**
 * A base class used for icons or images that are expensive to paint.
 * A subclass will do the following:
 * <ol>
 * <li>Invoke <code>paint</code> when you want to paint the image,
 *     if you are implementing <code>Icon</code> you'll invoke this from
 *     <code>paintIcon</code>.
 *     The args argument is useful when additional state is needed.
 * <li>Override <code>paintToImage</code> to render the image.  The code that
 *     lives here is equivalent to what previously would go in
 *     <code>paintIcon</code>, for an <code>Icon</code>.
 * </ol>
 *
 * @version @(#)CachedPainter.java	1.2 04/02/15
 */
abstract class CachedPainter {
    // CacheMap maps from class to Cache.
    private static final Map<Object,Cache> cacheMap =
                   new HashMap<Object,Cache>();


    private static Cache getCache(Object key) {
        synchronized(cacheMap) {
            Cache cache = cacheMap.get(key);
            if (cache == null) {
                cache = new Cache(1);
                cacheMap.put(key, cache);
            }
            return cache;
        }
    }

    /**
     * Creates an instance of <code>CachedPainter</code> that will cache up
     * to <code>cacheCount</code> images of this class.
     *
     * @param cacheCount Max number of images to cache
     */
    public CachedPainter(int cacheCount) {
        getCache(getClass()).setMaxCount(cacheCount);
    }

    /**
     * Renders the cached image to the the passed in <code>Graphic</code>.
     * If there is no cached image <code>paintToImage</code> will be invoked.
     * <code>paintImage</code> is invoked to paint the cached image.
     */
    protected void paint(Component c, Graphics g, int x,
                         int y, int w, int h, Object... args) {
        if (w <= 0 || h <= 0) {
            return;
        }
        Object key = getClass();
        GraphicsConfiguration config = c.getGraphicsConfiguration();
        Cache cache = getCache(key);
        Image image = cache.getImage(key, config, w, h, args);
        int attempts = 0;
        do {
            boolean draw = false;
            if (image instanceof VolatileImage) {
                // See if we need to recreate the image
                switch (((VolatileImage)image).validate(config)) {
                case VolatileImage.IMAGE_INCOMPATIBLE:
                    ((VolatileImage)image).flush();
                    image = null;
                    break;
                case VolatileImage.IMAGE_RESTORED:
                    draw = true;
                    break;
                }
            }
            if (image == null) {
                // Recreate the image
                image = createImage(c, w, h, config);
                cache.setImage(key, config, w, h, args, image);
                draw = true;
            }
            if (draw) {
                // Render to the Image
                Graphics g2 = image.getGraphics();
                paintToImage(c, g2, w, h, args);
                g2.dispose();
            }

            // Render to the passed in Graphics
            paintImage(c, g, x, y, w, h, image, args);

            // If we did this 3 times and the contents are still lost
            // assume we're painting to a VolatileImage that is bogus and
            // give up.  Presumably we'll be called again to paint.
        } while ((image instanceof VolatileImage) &&
                 ((VolatileImage)image).contentsLost() && ++attempts < 3);
    }

    /**
     * Paints the representation to cache to the supplied Graphics.
     *
     * @param c Component painting to
     * @param g Graphics to paint to
     * @param w Width to paint to
     * @param h Height to paint to
     * @param args Arguments supplied to <code>paint</code>
     */
    protected abstract void paintToImage(Component c, Graphics g,
                                         int w, int h, Object[] args);


    /**
     * Paints the image to the specified location.
     *
     * @param c Component painting to
     * @param g Graphics to paint to
     * @param x X coordinate to paint to
     * @param y Y coordinate to paint to
     * @param w Width to paint to
     * @param h Height to paint to
     * @param image Image to paint
     * @param args Arguments supplied to <code>paint</code>
     */
    protected void paintImage(Component c, Graphics g,
                              int x, int y, int w, int h, Image image,
                              Object[] args) {
        g.drawImage(image, x, y, null);
    }

    /**
     * Creates the image to cache.  This returns an opaque image, subclasses
     * that require translucency or transparency will need to override this
     * method.
     *
     * @param c Component painting to
     * @param w Width of image to create
     * @param h Height to image to create
     * @param config GraphicsConfiguration that will be
     *        rendered to, this may be null.
     */
    protected Image createImage(Component c, int w, int h,
                                GraphicsConfiguration config) {
        if (config == null) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        }
        return config.createCompatibleVolatileImage(w, h);
    }


    /**
     * Cache is used to cache an image based on a set of arguments.
     */
    private static class Cache {
        // Maximum number of entries to cache
        private int maxCount;
        // The entries.
        private java.util.List<SoftReference<Entry>> entries;

        Cache(int maxCount) {
            this.maxCount = maxCount;
            entries = new ArrayList<SoftReference<Entry>>(maxCount);
        }

        void setMaxCount(int maxCount) {
            this.maxCount = maxCount;
        }

        private Entry getEntry(Object key, GraphicsConfiguration config,
                               int w, int h, Object[] args) {
            synchronized(this) {
                Entry entry;
                for (int counter = entries.size() - 1; counter >= 0;counter--){
                    entry = entries.get(counter).get();
                    if (entry == null) {
                        // SoftReference was invalidated, remove the entry
                        entries.remove(counter);
                    }
                    else if (entry.equals(config, w, h, args)) {
                        // Found the entry, return it.
                        return entry;
                    }
                }
                // Entry doesn't exist
                entry = new Entry(config, w, h, args);
                if (entries.size() == maxCount) {
                    entries.remove(0);
                }
                entries.add(new SoftReference<Entry>(entry));
                return entry;
            }
        }

        /**
         * Returns the cached Image, or null, for the specified arguments.
         */
        public Image getImage(Object key, GraphicsConfiguration config,
                              int w, int h, Object[] args) {
            Entry entry = getEntry(key, config, w, h, args);
            return entry.getImage();
        }

        /**
         * Sets the cached image for the specified constraints.
         */
        public void setImage(Object key, GraphicsConfiguration config,
                             int w, int h, Object[] args, Image image) {
            Entry entry = getEntry(key, config, w, h, args);
            entry.setImage(image);
        }


        /**
         * Caches set of arguments and Image.
         */
        private static class Entry {
            private GraphicsConfiguration config;
            private Object[] args;
            private Image image;
            private int w;
            private int h;

            Entry(GraphicsConfiguration config, int w, int h, Object[] args) {
                this.config = config;
                this.args = args;
                this.w = w;
                this.h = h;
            }

            public void setImage(Image image) {
                this.image = image;
            }

            public Image getImage() {
                return image;
            }

            public String toString() {
                String value = super.toString() +
                               "[ graphicsConfig=" + config +
                               ", image=" + image +
                               ", w=" + w + ", h=" + h;
                if (args != null) {
                    for (int counter = 0; counter < args.length; counter++) {
                        value += ", " + args[counter];
                    }
                }
                value += "]";
                return value;
            }

            public boolean equals(GraphicsConfiguration config, int w, int h,
                                  Object[] args){
                if (this.w == w && this.h == h &&
                       ((this.config != null && this.config.equals(config)) ||
                        (this.config == null && config == null))) {
                    if (this.args == null && args == null) {
                        return true;
                    }
                    if (this.args != null && args != null && 
                                this.args.length == args.length) {
                        for (int counter = args.length - 1; counter >= 0;
                             counter--) {
                            if (!this.args[counter].equals(args[counter])) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
