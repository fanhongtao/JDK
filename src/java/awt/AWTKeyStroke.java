/*
 * @(#)AWTKeyStroke.java	1.14 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.Serializable;

/**
 * An <code>AWTKeyStroke</code> represents a key action on the
 * keyboard, or equivalent input device. <code>AWTKeyStroke</code>s
 * can correspond to only a press or release of a
 * particular key, just as <code>KEY_PRESSED</code> and
 * <code>KEY_RELEASED</code> <code>KeyEvent</code>s do;
 * alternately, they can correspond to typing a specific Java character, just
 * as <code>KEY_TYPED</code> <code>KeyEvent</code>s do.
 * In all cases, <code>AWTKeyStroke</code>s can specify modifiers
 * (alt, shift, control, meta, or a combination thereof) which must be present
 * during the action for an exact match.
 * <p>
 * <code>AWTKeyStrokes</code> are immutable, and are intended
 * to be unique. Client code should never create an
 * <code>AWTKeyStroke</code> on its own, but should instead use
 * a variant of <code>getAWTKeyStroke</code>. Client use of these factory
 * methods allows the <code>AWTKeyStroke</code> implementation
 * to cache and share instances efficiently.
 *
 * @see #getAWTKeyStroke
 *
 * @version 1.14, 01/23/03
 * @author Arnaud Weber
 * @author David Mendenhall
 * @since 1.4
 */
public class AWTKeyStroke implements Serializable {
    private static Map cache;
    private static AWTKeyStroke cacheKey;
    private static Class subclass = AWTKeyStroke.class;
    private static Map modifierKeywords;
    /**
     * Maps from VK_XXX (as a String) to an Integer. This is done to
     * avoid the overhead of the reflective call to find the constant.
     */
    private static Map vkMap;

    private char keyChar = KeyEvent.CHAR_UNDEFINED;
    private int keyCode = KeyEvent.VK_UNDEFINED;
    private int modifiers;
    private boolean onKeyRelease;

    static {
        /* ensure that the necessary native libraries are loaded */
        Toolkit.loadLibraries();
    }

    /**
     * Constructs an <code>AWTKeyStroke</code> with default values.
     * The default values used are:
     * <table border="1" summary="AWTKeyStroke default values">
     * <tr><th>Property</th><th>Default Value</th></tr>
     * <tr>
     *    <td>Key Char</td>
     *    <td><code>KeyEvent.CHAR_UNDEFINED</code></td>
     * </tr>
     * <tr>
     *    <td>Key Code</td>
     *    <td><code>KeyEvent.VK_UNDEFINED</code></td>
     * </tr>
     * <tr>
     *    <td>Modifiers</td>
     *    <td>none</td>
     * </tr>
     * <tr>
     *    <td>On key release?</td>
     *    <td><code>false</code></td>
     * </tr>
     * </table>
     * 
     * <code>AWTKeyStroke</code>s should not be constructed
     * by client code. Use a variant of <code>getAWTKeyStroke</code>
     * instead.
     *
     * @see #getAWTKeyStroke
     */
    protected AWTKeyStroke() {
    }

    /**
     * Constructs an <code>AWTKeyStroke</code> with the specified
     * values. <code>AWTKeyStroke</code>s should not be constructed
     * by client code. Use a variant of <code>getAWTKeyStroke</code>
     * instead.
     *
     * @param keyChar the character value for a keyboard key
     * @param keyCode the key code for this <code>AWTKeyStroke</code>
     * @param modifiers a bitwise-ored combination of any modifiers
     * @param onKeyRelease <code>true</code> if this
     *        <code>AWTKeyStroke</code> corresponds
     *        to a key release; <code>false</code> otherwise
     * @see #getAWTKeyStroke
     */
    protected AWTKeyStroke(char keyChar, int keyCode, int modifiers,
			   boolean onKeyRelease) {
        this.keyChar = keyChar;
	this.keyCode = keyCode;
	this.modifiers = modifiers;
	this.onKeyRelease = onKeyRelease;
    }

    private void copy(AWTKeyStroke rhs) {
        this.keyChar = rhs.keyChar;
	this.keyCode = rhs.keyCode;
	this.modifiers = rhs.modifiers;
	this.onKeyRelease = rhs.onKeyRelease;
    }

    /**
     * Registers a new class which the factory methods in
     * <code>AWTKeyStroke</code> will use when generating new
     * instances of <code>AWTKeyStroke</code>s. After invoking this
     * method, the factory methods will return instances of the specified
     * Class. The specified Class must be either <code>AWTKeyStroke</code>
     * or derived from <code>AWTKeyStroke</code>, and it must have a
     * no-arg constructor. The constructor can be of any accessibility,
     * including <code>private</code>. This operation
     * flushes the current <code>AWTKeyStroke</code> cache.
     *
     * @param subclass the new Class of which the factory methods should create
     *        instances
     * @throws IllegalArgumentException if subclass is <code>null</code>,
     *         or if subclass does not have a no-arg constructor
     * @throws ClassCastException if subclass is not
     *         <code>AWTKeyStroke</code>, or a class derived from
     *         <code>AWTKeyStroke</code>
     */
    protected static void registerSubclass(Class subclass) {
        if (subclass == null) {
	    throw new IllegalArgumentException("subclass cannot be null");
	}
	if (AWTKeyStroke.subclass.equals(subclass)) {
	    // Already registered
	    return;
	}
	if (!AWTKeyStroke.class.isAssignableFrom(subclass)) {
	    throw new ClassCastException("subclass is not derived from AWTKeyStroke");
	}

	String couldNotInstantiate = "subclass could not be instantiated";
	try {
	    AWTKeyStroke stroke = allocateNewInstance(subclass);
	    if (stroke == null) {
	        throw new IllegalArgumentException(couldNotInstantiate);
	    }
	} catch (NoSuchMethodError e) {
	    throw new IllegalArgumentException(couldNotInstantiate);
	} catch (ExceptionInInitializerError e) {
	    throw new IllegalArgumentException(couldNotInstantiate);
	} catch (InstantiationException e) {
	    throw new IllegalArgumentException(couldNotInstantiate);
	}

	synchronized (AWTKeyStroke.class) {
	    AWTKeyStroke.subclass = subclass;
	    cache = null;
	    cacheKey = null;
	}
    }

    // Native function allows us to bypass all security and access
    // restrictions. This allows us to define javax.swing.KeyStroke with only
    // a private no-arg constructor, yet still instantiate it from AWT without
    // special knowledge of Swing.
    private static native AWTKeyStroke allocateNewInstance(Class clazz)
        throws InstantiationException;

    private static synchronized AWTKeyStroke getCachedStroke
        (char keyChar, int keyCode, int modifiers, boolean onKeyRelease)
    {
	if (cache == null) {
	    cache = new HashMap();
	}
 
	if (cacheKey == null) {
	    try {
		cacheKey = allocateNewInstance(subclass);
	    } catch (InstantiationException e) {
	    }
	}
	cacheKey.keyChar = keyChar;
	cacheKey.keyCode = keyCode;
	cacheKey.modifiers = mapNewModifiers(mapOldModifiers(modifiers));
	cacheKey.onKeyRelease = onKeyRelease;
 
	AWTKeyStroke stroke = (AWTKeyStroke)cache.get(cacheKey);
	if (stroke == null) {
	    stroke = cacheKey;
	    cache.put(stroke, stroke);
	    cacheKey = null;
	}
	
	return stroke;
    }

    /**
     * Returns a shared instance of an <code>AWTKeyStroke</code> 
     * that represents a <code>KEY_TYPED</code> event for the 
     * specified character.
     *
     * @param keyChar the character value for a keyboard key
     * @return an <code>AWTKeyStroke</code> object for that key
     */
    public static AWTKeyStroke getAWTKeyStroke(char keyChar) {
        return getCachedStroke(keyChar, KeyEvent.VK_UNDEFINED, 0, false);
    }

    /**
     * Returns a shared instance of an <code>AWTKeyStroke</code>,
     * given a Character object and a set of modifiers. Note
     * that the first parameter is of type Character rather than
     * char. This is to avoid inadvertent clashes with
     * calls to <code>getAWTKeyStroke(int keyCode, int modifiers)</code>.
     *
     * The modifiers consist of any combination of:<ul>
     * <li>java.awt.event.InputEvent.SHIFT_DOWN_MASK 
     * <li>java.awt.event.InputEvent.CTRL_DOWN_MASK
     * <li>java.awt.event.InputEvent.META_DOWN_MASK
     * <li>java.awt.event.InputEvent.ALT_DOWN_MASK
     * <li>java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK
     * <li>java.awt.event.InputEvent.BUTTON1_DOWN_MASK 
     * <li>java.awt.event.InputEvent.BUTTON2_DOWN_MASK 
     * <li>java.awt.event.InputEvent.BUTTON3_DOWN_MASK
     * </ul>
     * The old modifiers <ul>
     * <li>java.awt.event.InputEvent.SHIFT_MASK 
     * <li>java.awt.event.InputEvent.CTRL_MASK 
     * <li>java.awt.event.InputEvent.META_MASK 
     * <li>java.awt.event.InputEvent.ALT_MASK
     * <li>java.awt.event.InputEvent.ALT_GRAPH_MASK
     * </ul> 
     * also can be used, but they are mapped to _DOWN_ modifiers.
     *
     * Since these numbers are all different powers of two, any combination of
     * them is an integer in which each bit represents a different modifier
     * key. Use 0 to specify no modifiers.
     *
     * @param keyChar the Character object for a keyboard character
     * @param modifiers a bitwise-ored combination of any modifiers
     * @return an <code>AWTKeyStroke</code> object for that key
     * @throws IllegalArgumentException if <code>keyChar</code> is
     *       <code>null</code>
     *
     * @see java.awt.event.InputEvent
     */
    public static AWTKeyStroke getAWTKeyStroke(Character keyChar,
					       int modifiers) {
        if (keyChar == null) {
	    throw new IllegalArgumentException("keyChar cannot be null");
	} 
        return getCachedStroke(keyChar.charValue(), KeyEvent.VK_UNDEFINED,
			       modifiers, false);
    }

    /**
     * Returns a shared instance of an <code>AWTKeyStroke</code>,
     * given a numeric key code and a set of modifiers, specifying
     * whether the key is activated when it is pressed or released.
     * <p>
     * The "virtual key" constants defined in
     * <code>java.awt.event.KeyEvent</code> can be 
     * used to specify the key code. For example:<ul>
     * <li><code>java.awt.event.KeyEvent.VK_ENTER</code> 
     * <li><code>java.awt.event.KeyEvent.VK_TAB</code>
     * <li><code>java.awt.event.KeyEvent.VK_SPACE</code>
     * </ul>
     * The modifiers consist of any combination of:<ul>
     * <li>java.awt.event.InputEvent.SHIFT_DOWN_MASK 
     * <li>java.awt.event.InputEvent.CTRL_DOWN_MASK
     * <li>java.awt.event.InputEvent.META_DOWN_MASK
     * <li>java.awt.event.InputEvent.ALT_DOWN_MASK
     * <li>java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK
     * <li>java.awt.event.InputEvent.BUTTON1_DOWN_MASK 
     * <li>java.awt.event.InputEvent.BUTTON2_DOWN_MASK 
     * <li>java.awt.event.InputEvent.BUTTON3_DOWN_MASK
     * </ul>
     * The old modifiers <ul>
     * <li>java.awt.event.InputEvent.SHIFT_MASK 
     * <li>java.awt.event.InputEvent.CTRL_MASK 
     * <li>java.awt.event.InputEvent.META_MASK 
     * <li>java.awt.event.InputEvent.ALT_MASK
     * <li>java.awt.event.InputEvent.ALT_GRAPH_MASK
     * </ul> 
     * also can be used, but they are mapped to _DOWN_ modifiers.
     *
     * Since these numbers are all different powers of two, any combination of
     * them is an integer in which each bit represents a different modifier
     * key. Use 0 to specify no modifiers.
     *
     * @param keyCode an int specifying the numeric code for a keyboard key
     * @param modifiers a bitwise-ored combination of any modifiers
     * @param onKeyRelease <code>true</code> if the <code>AWTKeyStroke</code>
     *        should represent a key release; <code>false</code> otherwise
     * @return an AWTKeyStroke object for that key
     *
     * @see java.awt.event.KeyEvent
     * @see java.awt.event.InputEvent
     */
    public static AWTKeyStroke getAWTKeyStroke(int keyCode, int modifiers,
					       boolean onKeyRelease) {
        return getCachedStroke(KeyEvent.CHAR_UNDEFINED, keyCode, modifiers,
			       onKeyRelease);
    }

    /**
     * Returns a shared instance of an <code>AWTKeyStroke</code>,
     * given a numeric key code and a set of modifiers. The returned
     * <code>AWTKeyStroke</code> will correspond to a key press.
     * <p>
     * The "virtual key" constants defined in
     * <code>java.awt.event.KeyEvent</code> can be 
     * used to specify the key code. For example:<ul>
     * <li><code>java.awt.event.KeyEvent.VK_ENTER</code> 
     * <li><code>java.awt.event.KeyEvent.VK_TAB</code>
     * <li><code>java.awt.event.KeyEvent.VK_SPACE</code>
     * </ul>
     * The modifiers consist of any combination of:<ul>
     * <li>java.awt.event.InputEvent.SHIFT_DOWN_MASK 
     * <li>java.awt.event.InputEvent.CTRL_DOWN_MASK
     * <li>java.awt.event.InputEvent.META_DOWN_MASK
     * <li>java.awt.event.InputEvent.ALT_DOWN_MASK
     * <li>java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK
     * <li>java.awt.event.InputEvent.BUTTON1_DOWN_MASK 
     * <li>java.awt.event.InputEvent.BUTTON2_DOWN_MASK 
     * <li>java.awt.event.InputEvent.BUTTON3_DOWN_MASK
     * </ul>
     * The old modifiers <ul>
     * <li>java.awt.event.InputEvent.SHIFT_MASK 
     * <li>java.awt.event.InputEvent.CTRL_MASK 
     * <li>java.awt.event.InputEvent.META_MASK 
     * <li>java.awt.event.InputEvent.ALT_MASK
     * <li>java.awt.event.InputEvent.ALT_GRAPH_MASK
     * </ul> 
     * also can be used, but they are mapped to _DOWN_ modifiers.
     *
     * Since these numbers are all different powers of two, any combination of
     * them is an integer in which each bit represents a different modifier
     * key. Use 0 to specify no modifiers.
     *
     * @param keyCode an int specifying the numeric code for a keyboard key
     * @param modifiers a bitwise-ored combination of any modifiers
     * @return an <code>AWTKeyStroke</code> object for that key
     *
     * @see java.awt.event.KeyEvent
     * @see java.awt.event.InputEvent
     */
    public static AWTKeyStroke getAWTKeyStroke(int keyCode, int modifiers) {
        return getCachedStroke(KeyEvent.CHAR_UNDEFINED, keyCode, modifiers,
			       false);
    }

    /**
     * Returns an <code>AWTKeyStroke</code> which represents the
     * stroke which generated a given <code>KeyEvent</code>.
     * <p>
     * This method obtains the key char from a <code>KeyTyped</code>
     * event, and the key code from a <code>KeyPressed</code> or
     * <code>KeyReleased</code> event. The <code>KeyEvent</code> modifiers are
     * obtained for all three types of <code>KeyEvent</code>.
     *
     * @param anEvent the <code>KeyEvent</code> from which to
     *      obtain the <code>AWTKeyStroke</code>
     * @return the <code>AWTKeyStroke</code> that precipitated the event
     */
    public static AWTKeyStroke getAWTKeyStrokeForEvent(KeyEvent anEvent) {
        int id = anEvent.getID();
        switch(id) {
          case KeyEvent.KEY_PRESSED:
          case KeyEvent.KEY_RELEASED:
	    return getCachedStroke(KeyEvent.CHAR_UNDEFINED,
				   anEvent.getKeyCode(),
				   anEvent.getModifiers(),
				   (id == KeyEvent.KEY_RELEASED));
          case KeyEvent.KEY_TYPED:
	    return getCachedStroke(anEvent.getKeyChar(),
				   KeyEvent.VK_UNDEFINED,
				   anEvent.getModifiers(),
				   false);
          default:
            // Invalid ID for this KeyEvent
            return null;
        }
    }

    /**
     * Parses a string and returns an <code>AWTKeyStroke</code>. 
     * The string must have the following syntax:
     * <pre>
     *    &lt;modifiers&gt;* (&lt;typedID&gt; | &lt;pressedReleasedID&gt;)
     *
     *    modifiers := shift | control | ctrl | meta | alt | button1 | button2 | button3
     *    typedID := typed &lt;typedKey&gt;
     *    typedKey := string of length 1 giving Unicode character.
     *    pressedReleasedID := (pressed | released) key
     *    key := KeyEvent key code name, i.e. the name following "VK_".
     * </pre>
     * If typed, pressed or released is not specified, pressed is assumed. Here
     * are some examples:
     * <pre>
     *     "INSERT" => getAWTKeyStroke(KeyEvent.VK_INSERT, 0);
     *     "control DELETE" => getAWTKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK);
     *     "alt shift X" => getAWTKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK);
     *     "alt shift released X" => getAWTKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, true);
     *     "typed a" => getAWTKeyStroke('a');
     * </pre>
     *
     * @param s a String formatted as described above
     * @return an <code>AWTKeyStroke</code> object for that String
     * @throws IllegalArgumentException if <code>s</code> is <code>null</code>,
     *        or is formatted incorrectly
     */
    public static AWTKeyStroke getAWTKeyStroke(String s) {
	if (s == null) {
	    throw new IllegalArgumentException("String cannot be null");
	}

        final String errmsg = "String formatted incorrectly";

        StringTokenizer st = new StringTokenizer(s, " ");

	int mask = 0;
	boolean released = false;
	boolean typed = false;
	boolean pressed = false;

        if (modifierKeywords == null) {
	    synchronized (AWTKeyStroke.class) {
	        if (modifierKeywords == null) {
		    Map uninitializedMap = new HashMap(8, 1.0f);
		    uninitializedMap.put("shift",
					 new Integer(InputEvent.SHIFT_DOWN_MASK
						     |InputEvent.SHIFT_MASK));
		    uninitializedMap.put("control",
					 new Integer(InputEvent.CTRL_DOWN_MASK
						     |InputEvent.CTRL_MASK));
		    uninitializedMap.put("ctrl",
					 new Integer(InputEvent.CTRL_DOWN_MASK
						     |InputEvent.CTRL_MASK));
		    uninitializedMap.put("meta",
					 new Integer(InputEvent.META_DOWN_MASK
						     |InputEvent.META_MASK));
		    uninitializedMap.put("alt",
					 new Integer(InputEvent.ALT_DOWN_MASK
						     |InputEvent.ALT_MASK));
		    uninitializedMap.put("altGraph",
					 new Integer(InputEvent.ALT_GRAPH_DOWN_MASK
						     |InputEvent.ALT_GRAPH_MASK));
		    uninitializedMap.put("button1",
					 new Integer(InputEvent.BUTTON1_DOWN_MASK));
		    uninitializedMap.put("button2",
					 new Integer(InputEvent.BUTTON2_DOWN_MASK));
		    uninitializedMap.put("button3",
					 new Integer(InputEvent.BUTTON3_DOWN_MASK));
		    modifierKeywords =
		        Collections.synchronizedMap(uninitializedMap);
		}
	    }
	}

	int count = st.countTokens();

	for (int i = 1; i <= count; i++) {
	    String token = st.nextToken();

	    if (typed) {
	        if (token.length() != 1 || i != count) {
		    throw new IllegalArgumentException(errmsg);
		}
		return getCachedStroke(token.charAt(0), KeyEvent.VK_UNDEFINED,
				       mask, false);
	    }

	    if (pressed || released || i == count) {
	        if (i != count) {
		    throw new IllegalArgumentException(errmsg);
		}

		String keyCodeName = "VK_" + token;
		int keyCode = getVKValue(keyCodeName);

		return getCachedStroke(KeyEvent.CHAR_UNDEFINED, keyCode,
				       mask, released);
	    }

	    if (token.equals("released")) {
		released = true;
		continue;
	    }
	    if (token.equals("pressed")) {
	        pressed = true;
		continue;
	    }
	    if (token.equals("typed")) {
		typed = true;
		continue;
	    }

	    Integer tokenMask = (Integer)modifierKeywords.get(token);
	    if (tokenMask != null) {
		mask |= tokenMask.intValue();
	    } else {
	        throw new IllegalArgumentException(errmsg);
	    }
	}

	throw new IllegalArgumentException(errmsg);
    }

    /**
     * Returns the integer constant for the KeyEvent.VK field named
     * <code>key</code>. This will throw an
     * <code>IllegalArgumentException</code> if <code>key</code> is
     * not a valid constant.
     */
    private static int getVKValue(String key) {
        if (vkMap == null) {
            vkMap = Collections.synchronizedMap(new HashMap());
        }

        Integer value = (Integer)vkMap.get(key);

        if (value == null) {
            int keyCode = 0;
            final String errmsg = "String formatted incorrectly";

            try {
                keyCode = KeyEvent.class.getField(key).getInt(KeyEvent.class);
            } catch (NoSuchFieldException nsfe) {
                throw new IllegalArgumentException(errmsg);
            } catch (IllegalAccessException iae) {
                throw new IllegalArgumentException(errmsg);
            }
            value = new Integer(keyCode);
            vkMap.put(key, value);
        }
        return value.intValue();
    }


    /**
     * Returns the character for this <code>AWTKeyStroke</code>.
     *
     * @return a char value
     * @see #getAWTKeyStroke(char)
     */
    public final char getKeyChar() {
        return keyChar;
    }

    /**
     * Returns the numeric key code for this <code>AWTKeyStroke</code>.
     *
     * @return an int containing the key code value
     * @see #getAWTKeyStroke(int,int)
     */
    public final int getKeyCode() {
        return keyCode;
    }

    /**
     * Returns the modifier keys for this <code>AWTKeyStroke</code>.
     *
     * @return an int containing the modifiers
     * @see #getAWTKeyStroke(int,int)
     */
    public final int getModifiers() {
        return modifiers;
    }

    /**
     * Returns whether this <code>AWTKeyStroke</code> represents a key release.
     *
     * @return <code>true</code> if this <code>AWTKeyStroke</code>
     *          represents a key release; <code>false</code> otherwise
     * @see #getAWTKeyStroke(int,int,boolean)
     */
    public final boolean isOnKeyRelease() {
        return onKeyRelease;
    }

    /**
     * Returns the type of <code>KeyEvent</code> which corresponds to
     * this <code>AWTKeyStroke</code>.
     *
     * @return <code>KeyEvent.KEY_PRESSED</code>,
     *         <code>KeyEvent.KEY_TYPED</code>,
     *         or <code>KeyEvent.KEY_RELEASED</code>
     * @see java.awt.event.KeyEvent
     */
    public final int getKeyEventType() {
	if (keyCode == KeyEvent.VK_UNDEFINED) {
	    return KeyEvent.KEY_TYPED;
	} else {
	    return (onKeyRelease)
		? KeyEvent.KEY_RELEASED
		: KeyEvent.KEY_PRESSED;
	}
    }

    /**
     * Returns a numeric value for this object that is likely to be unique,
     * making it a good choice as the index value in a hash table.
     *
     * @return an int that represents this object
     */
    public int hashCode() {
        return (((int)keyChar) + 1) * (2 * (keyCode + 1)) * (modifiers + 1) +
            (onKeyRelease ? 1 : 2);
    }

    /**
     * Returns true if this object is identical to the specified object.
     *
     * @param anObject the Object to compare this object to
     * @return true if the objects are identical
     */
    public final boolean equals(Object anObject) {
        if (anObject instanceof AWTKeyStroke) {
            AWTKeyStroke ks = (AWTKeyStroke)anObject;
	    return (ks.keyChar == keyChar && ks.keyCode == keyCode && 
		    ks.onKeyRelease == onKeyRelease &&
		    ks.modifiers == modifiers);
        }
        return false;
    }

    /**
     * Returns a string that displays and identifies this object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
        if (keyCode == KeyEvent.VK_UNDEFINED) {
	    return "keyChar " + KeyEvent.getKeyModifiersText(modifiers) +
	        keyChar;
	} else {
	    return "keyCode " + KeyEvent.getKeyModifiersText(modifiers) +
	        KeyEvent.getKeyText(keyCode) + (onKeyRelease ? "-R" : "-P");
	}
    }

    /**
     * Returns a cached instance of <code>AWTKeyStroke</code> (or a subclass of
     * <code>AWTKeyStroke</code>) which is equal to this instance.
     *
     * @return a cached instance which is equal to this instance
     */
    protected Object readResolve() throws java.io.ObjectStreamException {
        synchronized (AWTKeyStroke.class) {
	    Class newClass = getClass();
	    if (!newClass.equals(subclass)) {
	        registerSubclass(newClass);
	    }
	    return getCachedStroke(keyChar, keyCode, modifiers, onKeyRelease);
	}
    }

    private static int mapOldModifiers(int modifiers) {
       	if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
	    modifiers |= InputEvent.SHIFT_DOWN_MASK;
	}
	if ((modifiers & InputEvent.ALT_MASK) != 0) {
	    modifiers |= InputEvent.ALT_DOWN_MASK;
	}
	if ((modifiers & InputEvent.ALT_GRAPH_MASK) != 0) {
	    modifiers |= InputEvent.ALT_GRAPH_DOWN_MASK;
	}
	if ((modifiers & InputEvent.CTRL_MASK) != 0) {
	    modifiers |= InputEvent.CTRL_DOWN_MASK;
	}
	if ((modifiers & InputEvent.META_MASK) != 0) {
	    modifiers |= InputEvent.META_DOWN_MASK;
	}
	if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
	    modifiers |= InputEvent.BUTTON1_DOWN_MASK;
	}

 	modifiers &= InputEvent.SHIFT_DOWN_MASK
 	    | InputEvent.ALT_DOWN_MASK
 	    | InputEvent.ALT_GRAPH_DOWN_MASK
 	    | InputEvent.CTRL_DOWN_MASK
 	    | InputEvent.META_DOWN_MASK
 	    | InputEvent.BUTTON1_DOWN_MASK
 	    | InputEvent.BUTTON2_DOWN_MASK
 	    | InputEvent.BUTTON3_DOWN_MASK;
 	
	return modifiers;
    }

    private static int mapNewModifiers(int modifiers) {
       	if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
	    modifiers |= InputEvent.SHIFT_MASK;
	}
	if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
	    modifiers |= InputEvent.ALT_MASK;
	}
	if ((modifiers & InputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
	    modifiers |= InputEvent.ALT_GRAPH_MASK;
	}
	if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
	    modifiers |= InputEvent.CTRL_MASK;
	}
	if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
	    modifiers |= InputEvent.META_MASK;
	}
 	
	return modifiers;
    }

}

