/*
 * @(#)RenderingHints.java	1.21 04/05/05
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import sun.awt.SunHints;
import java.lang.ref.WeakReference;

/**
 * The <code>RenderingHints</code> class contains rendering hints that can
 * be used by the {@link java.awt.Graphics2D} class, and classes that
 * implement {@link java.awt.image.BufferedImageOp} and
 * {@link java.awt.image.Raster}.
 */
public class RenderingHints
    implements Map<Object,Object>, Cloneable
{
    /**
     * Defines the base type of all keys used to control various
     * aspects of the rendering and imaging pipelines.  Instances
     * of this class are immutable and unique which means that
     * tests for matches can be made using the == operator instead
     * of the more expensive equals() method.
     */
    public abstract static class Key {
	private static HashMap identitymap = new HashMap(17);

	private String getIdentity() {
	    // Note that the identity string is dependent on 3 variables:
	    //     - the name of the subclass of Key
	    //     - the identityHashCode of the subclass of Key
	    //     - the integer key of the Key
	    // It is theoretically possible for 2 distinct keys to collide
	    // along all 3 of those attributes in the context of multiple
	    // class loaders, but that occurence will be extremely rare and
	    // we account for that possibility below in the recordIdentity
	    // method by slightly relaxing our uniqueness guarantees if we
	    // end up in that situation.
	    return getClass().getName()+"@"+
		Integer.toHexString(System.identityHashCode(getClass()))+":"+
		Integer.toHexString(privatekey);
	}

	private synchronized static void recordIdentity(Key k) {
	    Object identity = k.getIdentity();
	    Object otherref = identitymap.get(identity);
	    if (otherref != null) {
		Key otherkey = (Key) ((WeakReference) otherref).get();
		if (otherkey != null && otherkey.getClass() == k.getClass()) {
		    throw new IllegalArgumentException(identity+
						       " already registered");
		}
		// Note that this system can fail in a mostly harmless
		// way.  If we end up generating the same identity
		// String for 2 different classes (a very rare case)
		// then we correctly avoid throwing the exception above,
		// but we are about to drop through to a statement that
		// will replace the entry for the old Key subclass with
		// an entry for the new Key subclass.  At that time the
		// old subclass will be vulnerable to someone generating
		// a duplicate Key instance for it.  We could bail out
		// of the method here and let the old identity keep its
		// record in the map, but we are more likely to see a
		// duplicate key go by for the new class than the old
		// one since the new one is probably still in the
		// initialization stage.  In either case, the probability
		// of loading 2 classes in the same VM with the same name
		// and identityHashCode should be nearly impossible.
	    }
	    // Note: Use a weak reference to avoid holding on to extra
	    // objects and classes after they should be unloaded.
	    identitymap.put(identity, new WeakReference(k));
	}

	private int privatekey;

	/**
	 * Construct a key using the indicated private key.  Each
	 * subclass of Key maintains its own unique domain of integer
	 * keys.  No two objects with the same integer key and of the
	 * same specific subclass can be constructed.  An exception
	 * will be thrown if an attempt is made to construct another
	 * object of a given class with the same integer key as a
	 * pre-existing instance of that subclass of Key.
	 * @param privatekey the specified key
	 */
	protected Key(int privatekey) {
	    this.privatekey = privatekey;
	    recordIdentity(this);
	}

	/**
	 * Returns true if the specified object is a valid value
	 * for this Key.
	 * @param val the <code>Object</code> to test for validity
	 * @return <code>true</code> if <code>val</code> is valid;
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean isCompatibleValue(Object val);

	/**
	 * Returns the private integer key that the subclass
	 * instantiated this Key with.
	 * @return the private integer key that the subclass
         * instantiated this Key with.
	 */
	protected final int intKey() {
	    return privatekey;
	}

	/**
	 * The hash code for all Key objects will be the same as the
	 * system identity code of the object as defined by the
	 * System.identityHashCode() method.
	 */
	public final int hashCode() {
	    return System.identityHashCode(this);
	}

	/**
	 * The equals method for all Key objects will return the same
	 * result as the equality operator '=='.
	 */
	public final boolean equals(Object o) {
	    return this == o;
	}
    }

    HashMap hintmap = new HashMap(7);

    /**
     * Antialiasing hint key.
     */
    public static final Key KEY_ANTIALIASING =
	SunHints.KEY_ANTIALIASING;

    /**
     * Antialiasing hint values -- rendering is done with antialiasing.
     */
    public static final Object VALUE_ANTIALIAS_ON =
	SunHints.VALUE_ANTIALIAS_ON;

    /**
     * Antialiasing hint values -- rendering is done without antialiasing.
     */
    public static final Object VALUE_ANTIALIAS_OFF =
	SunHints.VALUE_ANTIALIAS_OFF;

    /**
     * Antialiasing hint values -- rendering is done with the platform
     * default antialiasing mode.
     */
    public static final Object VALUE_ANTIALIAS_DEFAULT =
	 SunHints.VALUE_ANTIALIAS_DEFAULT;

    /**
     * Rendering hint key.
     */
    public static final Key KEY_RENDERING =
	 SunHints.KEY_RENDERING;

    /**
     * Rendering hint values -- Appropriate rendering algorithms are chosen
     * with a preference for output speed.
     */
    public static final Object VALUE_RENDER_SPEED =
	 SunHints.VALUE_RENDER_SPEED;

    /**
     * Rendering hint values -- Appropriate rendering algorithms are chosen
     * with a preference for output quality.
     */
    public static final Object VALUE_RENDER_QUALITY =
	 SunHints.VALUE_RENDER_QUALITY;

    /**
     * Rendering hint values -- The platform default rendering algorithms
     * are chosen.
     */
    public static final Object VALUE_RENDER_DEFAULT =
	 SunHints.VALUE_RENDER_DEFAULT;


    /**
     * Dithering hint key.
     */
    public static final Key KEY_DITHERING =
	 SunHints.KEY_DITHERING;

    /**
     * Dithering hint values -- do not dither when rendering.
     */
    public static final Object VALUE_DITHER_DISABLE =
	 SunHints.VALUE_DITHER_DISABLE;

    /**
     * Dithering hint values -- dither when rendering, if needed.
     */
    public static final Object VALUE_DITHER_ENABLE =
	 SunHints.VALUE_DITHER_ENABLE;

    /**
     * Dithering hint values -- use the platform default for dithering.
     */
    public static final Object VALUE_DITHER_DEFAULT =
	 SunHints.VALUE_DITHER_DEFAULT;

    /**
     * Text antialiasing hint key.
     */
    public static final Key KEY_TEXT_ANTIALIASING =
	 SunHints.KEY_TEXT_ANTIALIASING;

    /**
     * Text antialiasing hint value -- text rendering is done with
     * antialiasing.
     */
    public static final Object VALUE_TEXT_ANTIALIAS_ON =
	 SunHints.VALUE_TEXT_ANTIALIAS_ON;

    /**
     * Text antialiasing hint value -- text rendering is done without
     * antialiasing.
     */
    public static final Object VALUE_TEXT_ANTIALIAS_OFF =
	 SunHints.VALUE_TEXT_ANTIALIAS_OFF;

    /**
     * Text antialiasing hint value -- text rendering is done using the
     * platform default text antialiasing mode.
     */
    public static final Object VALUE_TEXT_ANTIALIAS_DEFAULT =
	 SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT;

    /**
     * Font fractional metrics hint key.
     */
    public static final Key KEY_FRACTIONALMETRICS =
	 SunHints.KEY_FRACTIONALMETRICS;

    /**
     * Font fractional metrics hint values -- fractional metrics disabled.
     */
    public static final Object VALUE_FRACTIONALMETRICS_OFF =
	 SunHints.VALUE_FRACTIONALMETRICS_OFF;

    /**
     * Font fractional metrics hint values -- fractional metrics enabled.
     */
    public static final Object VALUE_FRACTIONALMETRICS_ON =
	 SunHints.VALUE_FRACTIONALMETRICS_ON;

    /**
     * Font fractional metrics hint values -- use the platform default for
     * fractional metrics.
     */
    public static final Object VALUE_FRACTIONALMETRICS_DEFAULT =
	 SunHints.VALUE_FRACTIONALMETRICS_DEFAULT;


    /**
     * Interpolation hint key.
     */
    public static final Key KEY_INTERPOLATION =
	 SunHints.KEY_INTERPOLATION;

    /**
     * Interpolation hint value -- INTERPOLATION_NEAREST_NEIGHBOR.
     */
    public static final Object VALUE_INTERPOLATION_NEAREST_NEIGHBOR =
	 SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

    /**
     * Interpolation hint value -- INTERPOLATION_BILINEAR.
     */
    public static final Object VALUE_INTERPOLATION_BILINEAR =
	 SunHints.VALUE_INTERPOLATION_BILINEAR;

    /**
     * Interpolation hint value -- INTERPOLATION_BICUBIC.
     */
    public static final Object VALUE_INTERPOLATION_BICUBIC =
	 SunHints.VALUE_INTERPOLATION_BICUBIC;

    /**
     * Alpha interpolation hint key.
     */
    public static final Key KEY_ALPHA_INTERPOLATION =
	 SunHints.KEY_ALPHA_INTERPOLATION;

    /**
     * Alpha interpolation hint value -- ALPHA_INTERPOLATION_SPEED.
     */
    public static final Object VALUE_ALPHA_INTERPOLATION_SPEED =
	 SunHints.VALUE_ALPHA_INTERPOLATION_SPEED;

    /**
     * Alpha interpolation hint value -- ALPHA_INTERPOLATION_QUALITY.
     */
    public static final Object VALUE_ALPHA_INTERPOLATION_QUALITY =
	 SunHints.VALUE_ALPHA_INTERPOLATION_QUALITY;

    /**
     * Alpha interpolation hint value -- ALPHA_INTERPOLATION_DEFAULT.
     */
    public static final Object VALUE_ALPHA_INTERPOLATION_DEFAULT =
	 SunHints.VALUE_ALPHA_INTERPOLATION_DEFAULT;

    /**
     * Color rendering hint key.
     */
    public static final Key KEY_COLOR_RENDERING =
	 SunHints.KEY_COLOR_RENDERING;

    /**
     * Color rendering hint value -- COLOR_RENDER_SPEED.
     */
    public static final Object VALUE_COLOR_RENDER_SPEED =
	 SunHints.VALUE_COLOR_RENDER_SPEED;

    /**
     * Color rendering hint value -- COLOR_RENDER_QUALITY.
     */
    public static final Object VALUE_COLOR_RENDER_QUALITY =
	 SunHints.VALUE_COLOR_RENDER_QUALITY;

    /**
     * Color rendering hint value -- COLOR_RENDER_DEFAULT.
     */
    public static final Object VALUE_COLOR_RENDER_DEFAULT =
	 SunHints.VALUE_COLOR_RENDER_DEFAULT;

    /**
     * Stroke normalization control hint key.
     */
    public static final Key KEY_STROKE_CONTROL =
	SunHints.KEY_STROKE_CONTROL;

    /**
     * Stroke normalization control hint value -- STROKE_DEFAULT.
     */
    public static final Object VALUE_STROKE_DEFAULT =
	SunHints.VALUE_STROKE_DEFAULT;

    /**
     * Stroke normalization control hint value -- STROKE_NORMALIZE.
     */
    public static final Object VALUE_STROKE_NORMALIZE =
	SunHints.VALUE_STROKE_NORMALIZE;

    /**
     * Stroke normalization control hint value -- STROKE_PURE.
     */
    public static final Object VALUE_STROKE_PURE =
	SunHints.VALUE_STROKE_PURE;

    /**
     * Constructs a new object with keys and values initialized
     * from the specified Map object (which may be null).
     * @param init a map of key/value pairs to initialize the hints
     *		or null if the object should be empty
     */
    public RenderingHints(Map<Key,?> init) {
	if (init != null) {
	    hintmap.putAll(init);
	}
    }

    /**
     * Constructs a new object with the specified key/value pair.
     * @param key the key of the particular hint property
     * @param value the value of the hint property specified with 
     * <code>key</code>
     */
    public RenderingHints(Key key, Object value) {
	hintmap.put(key, value);
    }

    /**
     * Returns the number of key-value mappings in this 
     * <code>RenderingHints</code>.
     *
     * @return the number of key-value mappings in this 
     * <code>RenderingHints</code>.
     */
    public int size() {
	return hintmap.size();
    }

    /**
     * Returns <code>true</code> if this 
     * <code>RenderingHints</code> contains no key-value mappings.
     *
     * @return <code>true</code> if this 
     * <code>RenderingHints</code> contains no key-value mappings.
     */
    public boolean isEmpty() {
	return hintmap.isEmpty();
    }

    /**
     * Returns <code>true</code> if this <code>RenderingHints</code>
     *  contains a mapping for the specified key.
     *
     * @param key key whose presence in this 
     * <code>RenderingHints</code> is to be tested.
     * @return <code>true</code> if this <code>RenderingHints</code> 
     * 		contains a mapping for the specified key.
     * @exception <code>ClassCastException</code> key is not 
     * of type <code>RenderingHints.Key</code>
     * @exception <code>NullPointerException</code>
     *  key is <code>null</code>
     */
    public boolean containsKey(Object key) {
	return hintmap.containsKey((Key) key);
    }

    /**
     * Returns true if this RenderingHints maps one or more keys to the
     * specified value.
     * More formally, returns <code>true</code> if and only 
     * if this <code>RenderingHints</code>
     * contains at least one mapping to a value <code>v</code> such that
     * <pre>
     * (value==null ? v==null : value.equals(v))
     * </pre>.
     * This operation will probably require time linear in the
     * <code>RenderingHints</code> size for most implementations 
     * of <code>RenderingHints</code>.
     *
     * @param value value whose presence in this 
     *		<code>RenderingHints</code> is to be tested.
     * @return <code>true</code> if this <code>RenderingHints</code>
     *		 maps one or more keys to the specified value.
     */
    public boolean containsValue(Object value) {
	return hintmap.containsValue(value);
    }

    /**
     * Returns the value to which the specified key is mapped.
     * @param   key   a rendering hint key
     * @return  the value to which the key is mapped in this object or 
     *          <code>null</code> if the key is not mapped to any value in
     *          this object.
     * @exception <code>ClassCastException</code> key is not of 
     *		type <code>RenderingHints.Key</code>.
     * @see     #put(Object, Object)
     */
    public Object get(Object key) {
	return hintmap.get((Key) key);
    }

    /**
     * Maps the specified <code>key</code> to the specified
     * <code>value</code> in this <code>RenderingHints</code> object.
     * Neither the key nor the value can be <code>null</code>.
     * The value can be retrieved by calling the <code>get</code> method
     * with a key that is equal to the original key.
     * @param      key     the rendering hint key.
     * @param      value   the rendering hint value.
     * @return     the previous value of the specified key in this object
     *             or <code>null</code> if it did not have one.
     * @exception  <code>NullPointerException</code>  if the key or value is
     *               <code>null</code>.
     * @exception <code>ClassCastException</code> key is not of 
     *		type <code>RenderingHints.Key</code>.
     * @exception <code>IllegalArgumentException</code> value is not 
     *			appropriate for the specified key.
     * @see     #get(Object)
     */
    public Object put(Object key, Object value) {
	if (!((Key) key).isCompatibleValue(value)) {
	    throw new IllegalArgumentException(value+
					       " incompatible with "+
					       key);
	}
        return hintmap.put((Key) key, value);
    }

    /**
     * Adds all of the keys and corresponding values from the specified
     * <code>RenderingHints</code> object to this
     * <code>RenderingHints</code> object. Keys that are present in
     * this <code>RenderingHints</code> object, but not in the specified
     * <code>RenderingHints</code> object are not affected.
     * @param hints the set of key/value pairs to be added to this 
     * <code>RenderingHints</code> object
     */
    public void add(RenderingHints hints) {
	hintmap.putAll(hints.hintmap);
    }

    /**
     * Clears this <code>RenderingHints</code> object of all key/value
     * pairs.
     */
    public void clear() {
	hintmap.clear();
    }

    /**
     * Removes the key and its corresponding value from this
     * <code>RenderingHints</code> object. This method does nothing if the
     * key is not in this <code>RenderingHints</code> object.
     * @param   key   the rendering hints key that needs to be removed
     * @exception <code>ClassCastException</code> key is not of 
     *		type <code>RenderingHints.Key</code>.
     * @return  the value to which the key had previously been mapped in this 
     *		<code>RenderingHints</code> object, or <code>null</code>
     * 		if the key did not have a mapping.
     */
    public Object remove(Object key) {
	return hintmap.remove((Key) key);
    }

    /**
     * Copies all of the mappings from the specified <code>Map</code>
     * to this <code>RenderingHints</code>.  These mappings replace 
     * any mappings that this <code>RenderingHints</code> had for any 
     * of the keys currently in the specified <code>Map</code>.
     * @param m the specified <code>Map</code>
     * @exception <code>ClassCastException</code> class of a key or value 
     *		in the specified <code>Map</code> prevents it from being 
     *		stored in this <code>RenderingHints</code>.
     * @exception <code>IllegalArgumentException</code> some aspect 
     *		of a key or value in the specified <code>Map</code>
     *		 prevents it from being stored in
     * 		  this <code>RenderingHints</code>.
     */
    public void putAll(Map<?,?> m) {
	// ## javac bug?
	//if (m instanceof RenderingHints) {
	if (RenderingHints.class.isInstance(m)) {
	    //hintmap.putAll(((RenderingHints) m).hintmap);
	    for (Map.Entry<?,?> entry : m.entrySet())
		hintmap.put(entry.getKey(), entry.getValue());
	} else {
	    // Funnel each key/value pair through our protected put method
	    for (Map.Entry<?,?> entry : m.entrySet())
		put(entry.getKey(), entry.getValue());
	}
    }

    /**
     * Returns a <code>Set</code> view of the Keys contained in this 
     * <code>RenderingHints</code>.  The Set is backed by the 
     * <code>RenderingHints</code>, so changes to the
     * <code>RenderingHints</code> are reflected in the <code>Set</code>, 
     * and vice-versa.  If the <code>RenderingHints</code> is modified 
     * while an iteration over the <code>Set</code> is in progress, 
     * the results of the iteration are undefined.  The <code>Set</code>
     * supports element removal, which removes the corresponding
     * mapping from the <code>RenderingHints</code>, via the 
     * <code>Iterator.remove</code>, <code>Set.remove</code>,
     * <code>removeAll</code> <code>retainAll</code>, and 
     * <code>clear</code> operations.  It does not support
     * the <code>add</code> or <code>addAll</code> operations.
     *
     * @return a <code>Set</code> view of the keys contained 
     * in this <code>RenderingHints</code>.
     */
    public Set<Object> keySet() {
	return hintmap.keySet();
    }

    /**
     * Returns a <code>Collection</code> view of the values 
     * contained in this <code>RenderinHints</code>.
     * The <code>Collection</code> is backed by the 
     * <code>RenderingHints</code>, so changes to
     * the <code>RenderingHints</code> are reflected in 
     * the <code>Collection</code>, and vice-versa.
     * If the <code>RenderingHints</code> is modified while 
     * an iteration over the <code>Collection</code> is 
     * in progress, the results of the iteration are undefined.
     * The <code>Collection</code> supports element removal, 
     * which removes the corresponding mapping from the 
     * <code>RenderingHints</code>, via the
     * <code>Iterator.remove</code>, 
     * <code>Collection.remove</code>, <code>removeAll</code>, 
     * <code>retainAll</code> and <code>clear</code> operations.  
     * It does not support the <code>add</code> or 
     * <code>addAll</code> operations.
     *
     * @return a <code>Collection</code> view of the values 
     *		contained in this <code>RenderingHints</code>.
     */
    public Collection<Object> values() {
	return hintmap.values();
    }

    /**
     * Returns a <code>Set</code> view of the mappings contained 
     * in this <code>RenderingHints</code>.  Each element in the 
     * returned <code>Set</code> is a <code>Map.Entry</code>.  
     * The <code>Set</code> is backed by the <code>RenderingHints</code>, 
     * so changes to the <code>RenderingHints</code> are reflected
     * in the <code>Set</code>, and vice-versa.  If the 
     * <code>RenderingHints</code> is modified while
     * while an iteration over the <code>Set</code> is in progress, 
     * the results of the iteration are undefined.
     * <p>
     * The entrySet returned from a <code>RenderingHints</code> object 
     * is not modifiable.
     *
     * @return a <code>Set</code> view of the mappings contained in 
     * this <code>RenderingHints</code>.
     */
    public Set<Map.Entry<Object,Object>> entrySet() {
	return Collections.unmodifiableMap(hintmap).entrySet();
    }

    /**
     * Compares the specified <code>Object</code> with this 
     * <code>RenderingHints</code> for equality.
     * Returns <code>true</code> if the specified object is also a 
     * <code>Map</code> and the two <code>Map</code> objects represent 
     * the same mappings.  More formally, two <code>Map</code> objects 
     * <code>t1</code> and <code>t2</code> represent the same mappings
     * if <code>t1.keySet().equals(t2.keySet())</code> and for every
     * key <code>k</code> in <code>t1.keySet()</code>, 
     * <pre>
     * (t1.get(k)==null ? t2.get(k)==null : t1.get(k).equals(t2.get(k)))
     * </pre>.  
     * This ensures that the <code>equals</code> method works properly across
     * different implementations of the <code>Map</code> interface.
     *
     * @param o <code>Object</code> to be compared for equality with 
     * this <code>RenderingHints</code>.
     * @return <code>true</code> if the specified <code>Object</code> 
     * is equal to this <code>RenderingHints</code>.
     */
    public boolean equals(Object o) {
	if (o instanceof RenderingHints) {
	    return hintmap.equals(((RenderingHints) o).hintmap);
	} else if (o instanceof Map) {
	    return hintmap.equals(o);
	}
	return false;
    }

    /**
     * Returns the hash code value for this <code>RenderingHints</code>.  
     * The hash code of a <code>RenderingHints</code> is defined to be 
     * the sum of the hashCodes of each <code>Entry</code> in the 
     * <code>RenderingHints</code> object's entrySet view.  This ensures that
     * <code>t1.equals(t2)</code> implies that
     * <code>t1.hashCode()==t2.hashCode()</code> for any two <code>Map</code>
     * objects <code>t1</code> and <code>t2</code>, as required by the general
     * contract of <code>Object.hashCode</code>.
     *
     * @return the hash code value for this <code>RenderingHints</code>.
     * @see java.util.Map.Entry#hashCode()
     * @see Object#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    public int hashCode() {
	return hintmap.hashCode();
    }

    /**
     * Creates a clone of this <code>RenderingHints</code> object
     * that has the same contents as this <code>RenderingHints</code>
     * object.
     * @return a clone of this instance.
     */
    public Object clone() {
        RenderingHints rh;
        try {
            rh = (RenderingHints) super.clone();
	    if (hintmap != null) {
		rh.hintmap = (HashMap) hintmap.clone();
	    }
        } catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}

        return rh;
    }

    /**
     * Returns a rather long string representation of the hashmap
     * which contains the mappings of keys to values for this
     * <code>RenderingHints</code> object.
     * @return  a string representation of this object.
     */
    public String toString() {
        if (hintmap == null) {
            return getClass().getName() + "@" +
                Integer.toHexString(hashCode()) +
                " (0 hints)";
        }

        return hintmap.toString();
    }
}
