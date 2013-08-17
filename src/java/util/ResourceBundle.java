/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1999 - All Rights Reserved
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
package java.util;

import java.io.InputStream;
import java.io.FileInputStream;
import sun.misc.SoftCache;
import java.lang.ref.SoftReference;

/**
 *
 * Resource bundles contain locale-specific objects.
 * When your program needs a locale-specific resource,
 * a <code>String</code> for example, your program can load it
 * from the resource bundle that is appropriate for the
 * current user's locale. In this way, you can write
 * program code that is largely independent of the user's
 * locale isolating most, if not all, of the locale-specific
 * information in resource bundles.
 *
 * <p>
 * This allows you to write programs that can:
 * <UL type=SQUARE>
 * <LI> be easily localized, or translated, into different languages
 * <LI> handle multiple locales at once
 * <LI> be easily modified later to support even more locales
 * </UL>
 *
 * <P>
 * One resource bundle is, conceptually, a set of related classes that
 * inherit from <code>ResourceBundle</code>. Each related subclass of
 * <code>ResourceBundle</code> has the same base name plus an additional
 * component that identifies its locale. For example, suppose your resource
 * bundle is named <code>MyResources</code>. The first class you are likely
 * to write is the default resource bundle which simply has the same name as
 * its family--<code>MyResources</code>. You can also provide as
 * many related locale-specific classes as you need: for example, perhaps
 * you would provide a German one named <code>MyResources_de</code>.
 *
 * <P>
 * Each related subclass of <code>ResourceBundle</code> contains the same
 * items, but the items have been translated for the locale represented by that
 * <code>ResourceBundle</code> subclass. For example, both <code>MyResources</code>
 * and <code>MyResources_de</code> may have a <code>String</code> that's used
 * on a button for canceling operations. In <code>MyResources</code> the
 * <code>String</code> may contain <code>Cancel</code> and in
 * <code>MyResources_de</code> it may contain <code>Abbrechen</code>.
 *
 * <P>
 * If there are different resources for different countries, you
 * can make specializations: for example, <code>MyResources_de_CH</code>
 * is the German language (de) in Switzerland (CH). If you want to only
 * modify some of the resources
 * in the specialization, you can do so.
 *
 * <P>
 * When your program needs a locale-specific object, it loads
 * the <code>ResourceBundle</code> class using the <code>getBundle</code>
 * method:
 * <blockquote>
 * <pre>
 * ResourceBundle myResources =
 *      ResourceBundle.getBundle("MyResources", currentLocale);
 * </pre>
 * </blockquote>
 * The first argument specifies the family name of the resource
 * bundle that contains the object in question. The second argument
 * indicates the desired locale. <code>getBundle</code>
 * uses these two arguments to construct the name of the
 * <code>ResourceBundle</code> subclass it should load as follows.
 *
 * <P>
 * The resource bundle lookup searches for classes with various suffixes
 * on the basis of (1) the desired locale and (2) the current default locale
 * as returned by Locale.getDefault(), and (3) the root resource bundle (baseclass),
 * in the following order from lower-level (more specific) to parent-level
 * (less specific):
 * <p> baseclass + "_" + language1 + "_" + country1 + "_" + variant1
 * <BR> baseclass + "_" + language1 + "_" + country1 + "_" + variant1 + ".properties"
 * <BR> baseclass + "_" + language1 + "_" + country1
 * <BR> baseclass + "_" + language1 + "_" + country1 + ".properties"
 * <BR> baseclass + "_" + language1
 * <BR> baseclass + "_" + language1 + ".properties"
 * <BR> baseclass + "_" + language2 + "_" + country2 + "_" + variant2
 * <BR> baseclass + "_" + language2 + "_" + country2 + "_" + variant2 + ".properties"
 * <BR> baseclass + "_" + language2 + "_" + country2
 * <BR> baseclass + "_" + language2 + "_" + country2 + ".properties"
 * <BR> baseclass + "_" + language2
 * <BR> baseclass + "_" + language2 + ".properties"
 * <BR> baseclass
 * <BR> baseclass + ".properties"
 *
 * <P>
 * For example, if the current default locale is <TT>en_US</TT>, the locale the caller
 * is interested in is <TT>fr_CH</TT>, and the resource bundle name is <TT>MyResources</TT>,
 * resource bundle lookup will search for the following classes, in order:
 * <BR> <TT>MyResources_fr_CH
 * <BR> MyResources_fr
 * <BR> MyResources_en_US
 * <BR> MyResources_en
 * <BR> MyResources</TT>
 *
 * <P>
 * The result of the lookup is a class, but that class may be backed
 * by a properties file on disk.  That is, if getBundle does not find
 * a class of a given name, it appends ".properties" to the class name
 * and searches for a properties file of that name.  If it finds such
 * a file, it creates a new PropertyResourceBundle object to hold it.
 * Following on the previous example, it will return classes and
 * and files giving preference as follows:
 *
 *  (class) MyResources_fr_CH
 *  (file)  MyResources_fr_CH.properties
 *  (class) MyResources_fr
 *  (file)  MyResources_fr.properties
 *  (class) MyResources_en_US
 *  (file)  MyResources_en_US.properties
 *  (class) MyResources_en
 *  (file)  MyResources_en.properties
 *  (class) MyResources
 *  (file)  MyResources.properties
 *
 * If a lookup fails,
 * <code>getBundle()</code> throws a <code>MissingResourceException</code>.
 *
 * <P>
 * The baseclass <strong>must</strong> be fully
 * qualified (for example, <code>myPackage.MyResources</code>, not just
 * <code>MyResources</code>). It must
 * also be accessable by your code; it cannot be a class that is private
 * to the package where <code>ResourceBundle.getBundle</code> is called.
 *
 * <P>
 * Note: <code>ResourceBundle</code>s are used internally in accessing
 * <code>NumberFormat</code>s, <code>Collation</code>s, and so on.
 * The lookup strategy is the same.
 *
 * <P>
 * Resource bundles contain key/value pairs. The keys uniquely
 * identify a locale-specific object in the bundle. Here's an
 * example of a <code>ListResourceBundle</code> that contains
 * two key/value pairs:
 * <blockquote>
 * <pre>
 * class MyResource extends ListResourceBundle {
 *      public Object[][] getContents() {
 *              return contents;
 *      }
 *      static final Object[][] contents = {
 *      // LOCALIZE THIS
 *              {"OkKey", "OK"},
 *              {"CancelKey", "Cancel"},
 *      // END OF MATERIAL TO LOCALIZE
 *      };
 * }
 * </pre>
 * </blockquote>
 * Keys are always <code>String</code>s.
 * In this example, the keys are <code>OkKey</code> and <code>CancelKey</code>.
 * In the above example, the values
 * are also <code>String</code>s--<code>OK</code> and <code>Cancel</code>--but
 * they don't have to be. The values can be any type of object.
 *
 * <P>
 * You retrieve an object from resource bundle using the appropriate
 * getter method. Because <code>OkKey</code> and <code>CancelKey</code>
 * are both strings, you would use <code>getString</code> to retrieve them:
 * <blockquote>
 * <pre>
 * button1 = new Button(myResourceBundle.getString("OkKey"));
 * button2 = new Button(myResourceBundle.getString("CancelKey"));
 * </pre>
 * </blockquote>
 * The getter methods all require the key as an argument and return
 * the object if found. If the object is not found, the getter method
 * throws a <code>MissingResourceException</code>.
 *
 * <P>
 * Besides <code>getString</code>; ResourceBundle supports a number
 * of other methods for getting different types of objects such as
 * <code>getStringArray</code>. If you don't have an object that
 * matches one of these methods, you can use <code>getObject</code>
 * and cast the result to the appropriate type. For example:
 * <blockquote>
 * <pre>
 * int[] myIntegers = (int[]) myResources.getObject("intList");
 * </pre>
 * </blockquote>
 *
 * <P>
 * <STRONG>NOTE:</STRONG> You should always supply a baseclass with
 * no suffixes. This will be the class of "last resort", if a locale
 * is requested that does not exist. In fact, you must provide <I>all</I>
 * of the classes in any given inheritance chain that you provide a resource
 * for.  For example, if you provide <TT>MyResources_fr_BE</TT>, you must provide
 * <I>both</I> <TT>MyResources</TT> <I>and</I> <TT>MyResources_fr</TT> or
 * the resource bundle lookup won't work right.
 *
 * <P>
 * The Java 2 platform provides two subclasses of <code>ResourceBundle</code>,
 * <code>ListResourceBundle</code> and <code>PropertyResourceBundle</code>,
 * that provide a fairly simple way to create resources. (Once serialization
 * is fully integrated, we will provide another
 * way.) As you saw briefly in a previous example, <code>ListResourceBundle</code>
 * manages its resource as a List of key/value pairs.
 * <code>PropertyResourceBundle</code> uses a properties file to manage
 * its resources.
 *
 * <p>
 * If <code>ListResourceBundle</code> or <code>PropertyResourceBundle</code>
 * do not suit your needs, you can write your own <code>ResourceBundle</code>
 * subclass.  Your subclasses must override two methods: <code>handleGetObject</code>
 * and <code>getKeys()</code>.
 *
 * <P>
 * The following is a very simple example of a <code>ResourceBundle</code>
 * subclass, MyResources, that manages two resources (for a larger number of
 * resources you would probably use a <code>Hashtable</code>). Notice that if
 * the key is not found, <code>handleGetObject</code> must return null. 
 * If the key is <code>null</code>, a <code>NullPointerException</code> 
 * should be thrown. Notice also that you don't need to supply a value if 
 * a "parent-level" <code>ResourceBundle</code> handles the same
 * key with the same value (as in United Kingdom below).  Also notice that because
 * you specify an <TT>en_GB</TT> resource bundle, you also have to provide a default <TT>en</TT>
 * resource bundle even though it inherits all its data from the root resource bundle.
 * <p><strong>Example:</strong>
 * <blockquote>
 * <pre>
 * // default (English language, United States)
 * abstract class MyResources extends ResourceBundle {
 *     public Object handleGetObject(String key) {
 *         if (key.equals("okKey")) return "Ok";
 *         if (key.equals("cancelKey")) return "Cancel";
 *     return null;
 *     }
 * }
 *
 * // German language
 * public class MyResources_de extends MyResources {
 *     public Object handleGetObject(String key) {
 *         // don't need okKey, since parent level handles it.
 *         if (key.equals("cancelKey")) return "Abbrechen";
 *         return null;
 *     }
 * }
 * </pre>
 * </blockquote>
 * You do not have to restrict yourself to using a single family of
 * <code>ResourceBundle</code>s. For example, you could have a set of bundles for
 * exception messages, <code>ExceptionResources</code>
 * (<code>ExceptionResources_fr</code>, <code>ExceptionResources_de</code>, ...),
 * and one for widgets, <code>WidgetResource</code> (<code>WidgetResources_fr</code>,
 * <code>WidgetResources_de</code>, ...); breaking up the resources however you like.
 *
 * @see ListResourceBundle
 * @see PropertyResourceBundle
 * @see MissingResourceException
 * @since JDK1.1
 */
abstract public class ResourceBundle {
    /**
     * Static key used for resource lookups. Concurrent
     * access to this object is controlled by synchronizing cacheList,
     * not cacheKey.  A static object is used to do cache lookups
     * for performance reasons - the assumption being that synchronization
     * has a lower overhead than object allocation and subsequent
     * garbage collection.
     */
    private static final ResourceCacheKey cacheKey = new ResourceCacheKey();

    /** initial size of the bundle cache */
    private static final int INITIAL_CACHE_SIZE = 25;

    /** capacity of cache consumed before it should grow */
    private static final float CACHE_LOAD_FACTOR = (float)1.0;

    /**
     * Maximum length of one branch of the resource search path tree.
     * Used in getBundle.
     */
    private static final int MAX_BUNDLES_SEARCHED = 3;

    /**
     * This Hashtable is used to keep multiple threads from loading the
     * same bundle concurrently.  The table entries are (cacheKey, thread)
     * where cacheKey is the key for the bundle that is under construction
     * and thread is the thread that is constructing the bundle.
     * This list is manipulated in findBundle and putBundleInCache.
     * Synchronization of this object is done through cacheList, not on
     * this object.
     */
    private static final Hashtable underConstruction = new Hashtable(MAX_BUNDLES_SEARCHED, CACHE_LOAD_FACTOR);

    /** NOTFOUND value used if class loader is null */
    private static final Integer DEFAULT_NOT_FOUND = new Integer(-1);

    /**
     * This is a SoftCache, allowing bundles to be
     * removed from the cache if they are no longer
     * needed.  This will also allow the cache keys
     * to be reclaimed along with the ClassLoaders
     * they reference.
     */
    private static SoftCache cacheList = new SoftCache(INITIAL_CACHE_SIZE, CACHE_LOAD_FACTOR);

    /**
     * The parent bundle is consulted by getObject when this bundle
     * does not contain a particular resource.
     */
    protected ResourceBundle parent = null;

    /**
     * The locale for this bundle.
     */
    private Locale locale = null;

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    public ResourceBundle() {
    }

    /**
     * Get an object from a ResourceBundle.
     * <BR>Convenience method to save casting.
     * @param key see class description.
     * @exception NullPointerException if <code>key</code> is 
     * <code>null</code>.
     */
    public final String getString(String key) throws MissingResourceException {
        return (String) getObject(key);
    }

    /**
     * Get an object from a ResourceBundle.
     * <BR>Convenience method to save casting.
     * @param key see class description.
     * @exception NullPointerException if <code>key</code> is 
     * <code>null</code>.
     */
    public final String[] getStringArray(String key)
        throws MissingResourceException {
        return (String[]) getObject(key);
    }

    /**
     * Get an object from a ResourceBundle.
     * @param key see class description.
     * @exception NullPointerException if <code>key</code> is 
     * <code>null</code>.
     */
    public final Object getObject(String key) throws MissingResourceException {
        Object obj = handleGetObject(key);
        if (obj == null) {
            if (parent != null) {
                obj = parent.getObject(key);
            }
            if (obj == null)
                throw new MissingResourceException("Can't find resource for bundle "
                                                   +this.getClass().getName()
                                                   +", key "+key,
                                                   this.getClass().getName(),
                                                   key);
        }
        return obj;
    }

    /**
     * Return the Locale for this ResourceBundle.  (This function can be used after a
     * call to getBundle() to determine whether the ResourceBundle returned really
     * corresponds to the requested locale or is a fallback.)
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Set the locale for this bundle.  This is the locale that this
     * bundle actually represents and does not depend on how the
     * bundle was found by getBundle.  Ex. if the user was looking
     * for fr_FR and getBundle found en_US, the bundle's locale would
     * be en_US, NOT fr_FR
     * @param baseName the bundle's base name
     * @param bundleName the complete bundle name including locale
     * extension.
     */
    private void setLocale(String baseName, String bundleName) {
        if (baseName.length() == bundleName.length()) {
            locale = new Locale("", "");
        } else if (baseName.length() < bundleName.length()) {
            int pos = baseName.length();
            String temp = bundleName.substring(pos + 1);
            pos = temp.indexOf('_');
            if (pos == -1) {
                locale = new Locale(temp, "", "");
                return;
            }

            String language = temp.substring(0, pos);
            temp = temp.substring(pos + 1);
            pos = temp.indexOf('_');
            if (pos == -1) {
                locale = new Locale(language, temp, "");
                return;
            }

            String country = temp.substring(0, pos);
            temp = temp.substring(pos + 1);

            locale = new Locale(language, country, temp);
        } else {
            //The base name is longer than the bundle name.  Something is very wrong
            //with the calling code.
            throw new IllegalArgumentException();
        }
    }

    /*
     * Automatic determination of the ClassLoader to be used to load
     * resources on behalf of the client.  N.B. The client is getLoader's
     * caller's caller.
     */
    private static ClassLoader getLoader() {
        Class[] stack = getClassContext();
        /* Magic number 2 identifies our caller's caller */
        Class c = stack[2];
        ClassLoader cl = (c == null) ? null : c.getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        return cl;
    }

    private static native Class[] getClassContext();

    /**
     * Set the parent bundle of this bundle.  The parent bundle is
     * searched by getObject when this bundle does not contain a
     * particular resource.
     * @param parent this bundle's parent bundle.
     */
    protected void setParent( ResourceBundle parent ) {
        this.parent = parent;
    }

     /**
      * Key used for cached resource bundles.  The key checks
      * both the resource name and its class loader to determine
      * if the resource is a match to the requested one.  The
      * loader may be null, but the searchName must have a
      * non-null value.
      */
    private static final class ResourceCacheKey implements Cloneable {
        private SoftReference loaderRef;
        private String searchName;
        private int hashCodeCache;

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            try {
                final ResourceCacheKey otherEntry = (ResourceCacheKey)other;
                //quick check to see if they are not equal
                if (hashCodeCache != otherEntry.hashCodeCache) {
                    return false;
                }
                //are the names the same?
                if (!searchName.equals(otherEntry.searchName)) {
                    return false;
                }
                final boolean hasLoaderRef = loaderRef != null;
                //are refs (both non-null) or (both null)?
                if (loaderRef == null) {
                    return otherEntry.loaderRef == null;
                } else {
                    return (otherEntry.loaderRef != null)
                            && (loaderRef.get() == otherEntry.loaderRef.get());
                }
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }

        public int hashCode() {
            return hashCodeCache;
        }

        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                //this should never happen
                throw new InternalError();
            }
        }

        public void setKeyValues(ClassLoader loader, String searchName) {
            this.searchName = searchName;
            hashCodeCache = searchName.hashCode();
            if (loader == null) {
                this.loaderRef = null;
            } else {
                loaderRef = new SoftReference(loader);
                hashCodeCache ^= loader.hashCode();
            }
        }

        public void clear() {
            setKeyValues(null, "");
        }
    }

    /**
     * Get the appropriate ResourceBundle subclass.
     * @param baseName see class description.
     */
    public static final ResourceBundle getBundle(String baseName)
        throws MissingResourceException
    {
        return getBundleImpl(baseName, Locale.getDefault(),
        /* must determine loader here, else we break stack invariant */
        getLoader());
    }

    /**
     * Get the appropriate ResourceBundle subclass.
     * @param baseName see class description.
     * @param locale   see class description.
     */
    public static final ResourceBundle getBundle(String baseName,
                                                         Locale locale)
    {
        return getBundleImpl(baseName, locale, getLoader());
    }

    /**
     * Get the appropriate ResourceBundle subclass.
     * @param baseName see class description.
     * @param locale see class description.
     * @param loader the ClassLoader to load the resource from.
     */
    public static ResourceBundle getBundle(String baseName, Locale locale,
                                           ClassLoader loader)
        throws MissingResourceException
    {
        if (loader == null) {
            throw new NullPointerException();
        }
        return getBundleImpl(baseName, locale, loader);
    }

    private static ResourceBundle getBundleImpl(String baseName, Locale locale,
                                           ClassLoader loader)
    {
        if (baseName == null) {
            throw new NullPointerException();
        }

        //We use the class loader as the "flag" value that signifies a bundle
        //that could not be found.  This allows the entries to be garbage
        //collected when the loader gets garbage collected.  If we don't
        //have a loader, use a default value for NOTFOUND.
        final Object NOTFOUND = (loader != null) ? (Object)loader : (Object)DEFAULT_NOT_FOUND;

        //fast path the case where the bundle is cached
        String bundleName = baseName;
        String localeSuffix = locale.toString();
        if (localeSuffix.length() > 0) {
            bundleName += "_" + localeSuffix;
        } else if (locale.getVariant().length() > 0) {
            //This corrects some strange behavior in Locale where
            //new Locale("", "", "VARIANT").toString == ""
            bundleName += "___" + locale.getVariant();
        }

        Object lookup = findBundleInCache(loader, bundleName);
        if (lookup == NOTFOUND) {
            throwMissingResourceException(baseName, locale);
        } else if (lookup != null) {
            return (ResourceBundle)lookup;
        }

        //The bundle was not cached, so start doing lookup at the root
        //Resources are loaded starting at the root and working toward
        //the requested bundle.

        //If findBundle returns null, we become responsible for defining
        //the bundle, and must call putBundleInCache to complete this
        //task.  This is critical because other threads may be waiting
        //for us to finish.

        Object parent = NOTFOUND;
        try {
            //locate the root bundle and work toward the desired child
            Object root = findBundle(loader, baseName, baseName, null, NOTFOUND);
            if (root == null) {
                putBundleInCache(loader, baseName, NOTFOUND);
                root = NOTFOUND;
            }

            // Search the main branch of the search tree.
            // We need to keep references to the bundles we find on the main path
            // so they don't get garbage collected before we get to propagate().
            final Vector names = calculateBundleNames(baseName, locale);
            Vector bundlesFound = new Vector(MAX_BUNDLES_SEARCHED);
	    // if we found the root bundle and no other bundle names are needed
	    // we can stop here. We don't need to search or load anything further.
            boolean foundInMainBranch = (root != NOTFOUND && names.size() == 0);
	    
	    if (!foundInMainBranch) {
	      parent = root;
	      for (int i = 0; i < names.size(); i++) {
                bundleName = (String)names.elementAt(i);
                lookup = findBundle(loader, bundleName, baseName, parent, NOTFOUND);
                bundlesFound.addElement(lookup);
                if (lookup != null) {
                    parent = lookup;
                    foundInMainBranch = true;
                }
	      }
            }
            parent = root;
            if (!foundInMainBranch) {
                //we didn't find anything on the main branch, so we do the fallback branch
                final Vector fallbackNames = calculateBundleNames(baseName, Locale.getDefault());
                for (int i = 0; i < fallbackNames.size(); i++) {
                    bundleName = (String)fallbackNames.elementAt(i);
                    if (names.contains(bundleName)) {
                        //the fallback branch intersects the main branch so we can stop now.
                        break;
                    }
                    lookup = findBundle(loader, bundleName, baseName, parent, NOTFOUND);
                    if (lookup != null) {
                        parent = lookup;
                    } else {
                        //propagate the parent to the child.  We can do this
                        //here because we are in the default path.
                        putBundleInCache(loader, bundleName, parent);
                    }
                }
            }
            //propagate the inheritance/fallback down through the main branch
            parent = propagate(loader, names, bundlesFound, parent);
        } catch (Exception e) {
            //We should never get here unless there has been a change
            //to the code that doesn't catch it's own exceptions.
            cleanUpConstructionList();
            throwMissingResourceException(baseName, locale);
        } catch (Error e) {
            //The only Error that can currently hit this code is a ThreadDeathError
            //but errors might be added in the future, so we'll play it safe and
            //clean up.
            cleanUpConstructionList();
            throw e;
        }
        if (parent == NOTFOUND) {
            throwMissingResourceException(baseName, locale);
        }
        return (ResourceBundle)parent;
    }

    /**
     * propagate bundles from the root down the specified branch of the search tree.
     * @param loader the class loader for the bundles
     * @param names the names of the bundles along search path
     * @param bundlesFound the bundles corresponding to the names (some may be null)
     * @parent the parent of the first bundle in the path (the root bundle)
     * @return the value of the last bundle along the path
     */
    private static Object propagate(ClassLoader loader, Vector names, Vector bundlesFound, Object parent) {
        for (int i = 0; i < names.size(); i++) {
            final String bundleName = (String)names.elementAt(i);
            final Object lookup = bundlesFound.elementAt(i);
            if (lookup == null) {
                putBundleInCache(loader, bundleName, parent);
            } else {
                parent = lookup;
            }
        }
        return parent;
    }

    /** Throw a MissingResourceException with proper message */
    private static void throwMissingResourceException(String baseName, Locale locale)
            throws MissingResourceException{
        throw new MissingResourceException("Can't find bundle for base name "
                                           + baseName + ", locale " + locale,
                                           baseName + "_" + locale,"");
    }

    /**
     * Remove any entries this thread may have in the construction list.
     * This is done as cleanup in the case where a bundle can't be
     * constructed.
     */
    private static void cleanUpConstructionList() {
        synchronized (cacheList) {
            final Collection entries = underConstruction.values();
            final Thread thisThread = Thread.currentThread();
            while (entries.remove(thisThread)) {
            }
        }
    }

    /**
     * Find a bundle in the cache or load it via the loader or a property file.
     * If the bundle isn't found, an entry is put in the constructionCache
     * and null is returned.  If null is returned, the caller must define the bundle
     * by calling putBundleInCache.  This routine also propagates NOTFOUND values
     * from parent to child bundles when the parent is NOTFOUND.
     * @param loader the loader to use when loading a bundle
     * @param bundleName the complete bundle name including locale extension
     * @param parent the parent of the resource bundle being loaded.  null if
     * the bundle is a root bundle
     * @param NOTFOUND the value to use for NOTFOUND bundles.
     * @return the bundle or null if the bundle could not be found in the cache
     * or loaded.
     */
    private static Object findBundle(ClassLoader loader, String bundleName,
            String baseName, Object parent, final Object NOTFOUND) {
        Object result;
        synchronized (cacheList) {
            //check for bundle in cache
            cacheKey.setKeyValues(loader, bundleName);
            result = cacheList.get(cacheKey);
            if (result != null) {
                cacheKey.clear();
                return result;
            }
            // check to see if some other thread is building this bundle.
            // Note that there is a rare chance that this thread is already
            // working on this bundle, and in the process getBundle was called
            // again, in which case we can't wait (4300693)
            Thread builder = (Thread) underConstruction.get(cacheKey);
            boolean beingBuilt = (builder != null && builder != Thread.currentThread());
            //if some other thread is building the bundle...
            if (beingBuilt) {
                //while some other thread is building the bundle...
                while (beingBuilt) {
                    cacheKey.clear();
                    try {
                        //Wait until the bundle is complete
                        cacheList.wait();
                    } catch (InterruptedException e) {
                    }
                    cacheKey.setKeyValues(loader, bundleName);
                    beingBuilt = underConstruction.containsKey(cacheKey);
                }
                //if someone constructed the bundle for us, return it
                result = cacheList.get(cacheKey);
                if (result != null) {
                    cacheKey.clear();
                    return result;
                }
            }
            //The bundle isn't in the cache, so we are now responsible for
            //loading it and adding it to the cache.
            final Object key = cacheKey.clone();
            underConstruction.put(key, Thread.currentThread());
            //the bundle is removed from the cache by putBundleInCache
            cacheKey.clear();
        }

        //try loading the bundle via the class loader
        result = loadBundle(loader, bundleName);
        if (result != null) {
            // check whether we're still responsible for construction -
            // a recursive call to getBundle might have handled it (4300693)
            boolean constructing;
            synchronized (cacheList) {
                cacheKey.setKeyValues(loader, bundleName);
                constructing = underConstruction.get(cacheKey) == Thread.currentThread();
                cacheKey.clear();
            }
            if (constructing) {
                // set the bundle's parent and put it in the cache
                final ResourceBundle bundle = (ResourceBundle)result;
                if (parent != NOTFOUND) {
                    bundle.setParent((ResourceBundle)parent);
                } else {
                    bundle.setParent((ResourceBundle)null);
                }
                bundle.setLocale(baseName, bundleName);
                putBundleInCache(loader, bundleName, result);
            }
        }
        return result;
    }

    /**
     * Calculate the bundles along the search path from the base bundle to the
     * bundle specified by baseName and locale.
     * @param baseName the base bundle name
     * @param locale the locale
     * @param names the vector used to return the names of the bundles along
     * the search path.
     *
     */
    private static Vector calculateBundleNames(String baseName, Locale locale) {
        final Vector result = new Vector(MAX_BUNDLES_SEARCHED);
        final String language = locale.getLanguage();
        final int languageLength = language.length();
        final String country = locale.getCountry();
        final int countryLength = country.length();
        final String variant = locale.getVariant();
        final int variantLength = variant.length();

        if (languageLength + countryLength + variantLength == 0) {
            //The locale is "", "", "".
            return result;
        }
        final StringBuffer temp = new StringBuffer(baseName);
        temp.append('_');
        temp.append(language);
        result.addElement(temp.toString());

        if (countryLength + variantLength == 0) {
            return result;
        }
        temp.append('_');
        temp.append(country);
        result.addElement(temp.toString());

        if (variantLength == 0) {
            return result;
        }
        temp.append('_');
        temp.append(variant);
        result.addElement(temp.toString());

        return result;
    }

    /**
     * Find a bundle in the cache.
     * @param loader the class loader that is responsible for loading the bundle.
     * @param bundleName the complete name of the bundle including locale extension.
     *      ex. java.text.resources.LocaleElements_fr_BE
     * @return the cached bundle.  null if the bundle is not in the cache.
     */
    private static Object findBundleInCache(ClassLoader loader, String bundleName) {
        //Synchronize access to cacheList, cacheKey, and underConstruction
        synchronized (cacheList) {
            cacheKey.setKeyValues(loader, bundleName);
            Object result = cacheList.get(cacheKey);
            cacheKey.clear();
            return result;
        }
    }

    /**
     * Put a new bundle in the cache and notify waiting threads that a new
     * bundle has been put in the cache.
     */
    private static void putBundleInCache(ClassLoader loader, String bundleName,
            Object value) {
        //we use a static shared cacheKey but we use the lock in cacheList since
        //the key is only used to interact with cacheList.
        synchronized (cacheList) {
            cacheKey.setKeyValues(loader, bundleName);
            cacheList.put(cacheKey.clone(), value);
            underConstruction.remove(cacheKey);
            cacheKey.clear();
            //notify waiters that we're done constructing the bundle
            cacheList.notifyAll();
        }
    }

    /**
     * Load a bundle through either the specified ClassLoader or from a ".properties" file
     * and return the loaded bundle.
     * @param loader the ClassLoader to use to load the bundle.  If null, the system
     *      ClassLoader is used.
     * @param bundleName the name of the resource to load.  The name should be complete
     *      including a qualified class name followed by the locale extension.
     *      ex. java.text.resources.LocaleElements_fr_BE
     * @return the bundle or null if none could be found.
     */
    private static Object loadBundle(final ClassLoader loader, String bundleName) {
        // Search for class file using class loader
        try {
            Class bundleClass;
            if (loader != null) {
                bundleClass = loader.loadClass(bundleName);
            } else {
                bundleClass = Class.forName(bundleName);
            }
            if (ResourceBundle.class.isAssignableFrom(bundleClass)) {
                Object myBundle = bundleClass.newInstance();
                // Creating the instance may have triggered a recursive call to getBundle,
                // in which case the bundle created by the recursive call would be in the
                // cache now (4300693). For consistency, we'd then return the bundle from the cache.
                Object otherBundle = findBundleInCache(loader, bundleName);
                if (otherBundle != null) {
                    return otherBundle;
                } else {
                    return myBundle;
                }
            }
        } catch (Exception e) {
        } catch (LinkageError e) {
        }

        // Next search for a Properties file.
        final String resName = bundleName.replace('.', '/') + ".properties";
        InputStream stream = (InputStream)java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction() {
                public Object run() {
                    if (loader != null) {
                        return loader.getResourceAsStream(resName);
                    } else {
                        return ClassLoader.getSystemResourceAsStream(resName);
                    }
                }
            }
        );

        if (stream != null) {
            // make sure it is buffered
            stream = new java.io.BufferedInputStream(stream);
            try {
                return new PropertyResourceBundle(stream);
            } catch (Exception e) {
            } finally {
                try {
                    stream.close();
                } catch (Exception e) {
                    // to avoid propagating an IOException back into the caller
                    // (I'm assuming this is never going to happen, and if it does,
                    // I'm obeying the precedent of swallowing exceptions set by the
                    // existing code above)
                }
            }
        }
        return null;
    }

    /** Get an object from a ResourceBundle.
     * <STRONG>NOTE: </STRONG>Subclasses must override.
     * @param key see class description.
     * @exception NullPointerException if <code>key</code> is 
     * <code>null</code>.
     */
    protected abstract Object handleGetObject(String key)
        throws MissingResourceException;

    /**
     * Return an enumeration of the keys.
     * <STRONG>NOTE: </STRONG>Subclasses must override.
     */
    public abstract Enumeration getKeys();
}
