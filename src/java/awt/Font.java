/*
 * @(#)Font.java	1.203 09/06/16
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.peer.FontPeer;
import java.io.*;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import sun.font.StandardGlyphVector;
import sun.java2d.FontSupport;

import sun.font.CreatedFontTracker;
import sun.font.Font2D;
import sun.font.Font2DHandle;
import sun.font.FontManager;
import sun.font.GlyphLayout;
import sun.font.FontLineMetrics;
import sun.font.CoreMetrics;

/**
 * The <code>Font</code> class represents fonts, which are used to
 * render text in a visible way.
 * A font provides the information needed to map sequences of
 * <em>characters</em> to sequences of <em>glyphs</em>
 * and to render sequences of glyphs on <code>Graphics</code> and
 * <code>Component</code> objects.
 * 
 * <h4>Characters and Glyphs</h4>
 * 
 * A <em>character</em> is a symbol that represents an item such as a letter,
 * a digit, or punctuation in an abstract way. For example, <code>'g'</code>,
 * <font size=-1>LATIN SMALL LETTER G</font>, is a character.
 * <p>
 * A <em>glyph</em> is a shape used to render a character or a sequence of
 * characters. In simple writing systems, such as Latin, typically one glyph
 * represents one character. In general, however, characters and glyphs do not
 * have one-to-one correspondence. For example, the character '&aacute;'
 * <font size=-1>LATIN SMALL LETTER A WITH ACUTE</font>, can be represented by
 * two glyphs: one for 'a' and one for '&acute;'. On the other hand, the
 * two-character string "fi" can be represented by a single glyph, an
 * "fi" ligature. In complex writing systems, such as Arabic or the South
 * and South-East Asian writing systems, the relationship between characters
 * and glyphs can be more complicated and involve context-dependent selection
 * of glyphs as well as glyph reordering.
 *
 * A font encapsulates the collection of glyphs needed to render a selected set
 * of characters as well as the tables needed to map sequences of characters to
 * corresponding sequences of glyphs.
 *
 * <h4>Physical and Logical Fonts</h4>
 *
 * The Java 2 platform distinguishes between two kinds of fonts:
 * <em>physical</em> fonts and <em>logical</em> fonts.
 * <p>
 * <em>Physical</em> fonts are the actual font libraries containing glyph data
 * and tables to map from character sequences to glyph sequences, using a font
 * technology such as TrueType or PostScript Type 1.
 * All implementations of the Java 2 platform must support TrueType fonts;
 * support for other font technologies is implementation dependent.
 * Physical fonts may use names such as Helvetica, Palatino, HonMincho, or
 * any number of other font names.
 * Typically, each physical font supports only a limited set of writing
 * systems, for example, only Latin characters or only Japanese and Basic
 * Latin.
 * The set of available physical fonts varies between configurations.
 * Applications that require specific fonts can bundle them and instantiate
 * them using the {@link #createFont createFont} method.
 * <p>
 * <em>Logical</em> fonts are the five font families defined by the Java
 * platform which must be supported by any Java runtime environment:
 * Serif, SansSerif, Monospaced, Dialog, and DialogInput.
 * These logical fonts are not actual font libraries. Instead, the logical
 * font names are mapped to physical fonts by the Java runtime environment.
 * The mapping is implementation and usually locale dependent, so the look
 * and the metrics provided by them vary.
 * Typically, each logical font name maps to several physical fonts in order to
 * cover a large range of characters.
 * <p>
 * Peered AWT components, such as {@link Label Label} and
 * {@link TextField TextField}, can only use logical fonts.
 * <p>
 * For a discussion of the relative advantages and disadvantages of using
 * physical or logical fonts, see the
 * <a href="http://java.sun.com/j2se/corejava/intl/reference/faqs/index.html#desktop-rendering">Internationalization FAQ</a>
 * document.
 *
 * <h4>Font Faces and Names</h4>
 * 
 * A <code>Font</code> 
 * can have many faces, such as heavy, medium, oblique, gothic and
 * regular. All of these faces have similar typographic design.
 * <p>
 * There are three different names that you can get from a 
 * <code>Font</code> object.  The <em>logical font name</em> is simply the
 * name that was used to construct the font.
 * The <em>font face name</em>, or just <em>font name</em> for
 * short, is the name of a particular font face, like Helvetica Bold. The
 * <em>family name</em> is the name of the font family that determines the
 * typographic design across several faces, like Helvetica.
 * <p>
 * The <code>Font</code> class represents an instance of a font face from
 * a collection of  font faces that are present in the system resources
 * of the host system.  As examples, Arial Bold and Courier Bold Italic
 * are font faces.  There can be several <code>Font</code> objects
 * associated with a font face, each differing in size, style, transform
 * and font features.  
 * The {@link GraphicsEnvironment#getAllFonts() getAllFonts} method 
 * of the <code>GraphicsEnvironment</code> class returns an
 * array of all font faces available in the system. These font faces are
 * returned as <code>Font</code> objects with a size of 1, identity
 * transform and default font features. These
 * base fonts can then be used to derive new <code>Font</code> objects
 * with varying sizes, styles, transforms and font features via the
 * <code>deriveFont</code> methods in this class.
 *
 * @version 	1.203, 06/16/09
 */
public class Font implements java.io.Serializable
{
   
    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
        initIDs();
    }

    /**
     * A map of font attributes available in this font.
     * Attributes include things like ligatures and glyph substitution.
     *
     * @serial
     * @see #getAttributes()
     */
    private Hashtable fRequestedAttributes;
    
    private static final Map EMPTY_MAP = new Hashtable(5, (float)0.9);
    private static final TransformAttribute IDENT_TX_ATTRIBUTE =
	new TransformAttribute(new AffineTransform());

    /*
     * Constants to be used for styles. Can be combined to mix
     * styles.
     */

    /**
     * The plain style constant.
     */
    public static final int PLAIN	= 0;

    /**
     * The bold style constant.  This can be combined with the other style
     * constants (except PLAIN) for mixed styles.
     */
    public static final int BOLD	= 1;

    /**
     * The italicized style constant.  This can be combined with the other
     * style constants (except PLAIN) for mixed styles.
     */
    public static final int ITALIC	= 2;

    /**
     * The baseline used in most Roman scripts when laying out text.
     */
    public static final int ROMAN_BASELINE = 0;

    /**
     * The baseline used in ideographic scripts like Chinese, Japanese,
     * and Korean when laying out text.
     */
    public static final int CENTER_BASELINE = 1;

    /**
     * The baseline used in Devanigiri and similar scripts when laying
     * out text.
     */
    public static final int HANGING_BASELINE = 2;

    /**
     * Identify a font resource of type TRUETYPE.
     * Used to specify a TrueType font resource to the
     * {@link #createFont} method.
     * @since 1.3
     */

    public static final int TRUETYPE_FONT = 0;

    /**
     * Identify a font resource of type TYPE1.
     * Used to specify a Type1 font resource to the
     * {@link #createFont} method.
     * @since 1.5
     */
    public static final int TYPE1_FONT = 1;

    /**
     * The logical name of this <code>Font</code>, as passed to the
     * constructor.
     * @since JDK1.0
     *
     * @serial
     * @see #getName
     */
    protected String name;

    /**
     * The style of this <code>Font</code>, as passed to the constructor.
     * This style can be PLAIN, BOLD, ITALIC, or BOLD+ITALIC.
     * @since JDK1.0
     *
     * @serial
     * @see #getStyle()
     */
    protected int style;

    /**
     * The point size of this <code>Font</code>, rounded to integer.
     * @since JDK1.0
     *
     * @serial
     * @see #getSize()
     */
    protected int size;

    /**
     * The point size of this <code>Font</code> in <code>float</code>.
     *
     * @serial
     * @see #getSize()
     * @see #getSize2D()
     */
    protected float pointSize;

    /**
     * The platform specific font information.
     */
    private transient FontPeer peer;
    private transient long pData;       // native JDK1.1 font pointer
    private transient Font2DHandle font2DHandle;
    private transient int superscript;
    private transient float width = 1f;

    /*
     * If the origin of a Font is a created font then this attribute
     * must be set on all derived fonts too.
     */
    private transient boolean createdFont = false;

    // cached values - performance
    private transient double[] matrix;
    private transient boolean nonIdentityTx;

    private static final AffineTransform identityTx = new AffineTransform();
    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = -4206021311591459213L;

    /**
     * Gets the peer of this <code>Font</code>.
     * @return  the peer of the <code>Font</code>.
     * @since JDK1.1
     * @deprecated Font rendering is now platform independent.
     */
    @Deprecated
    public FontPeer getPeer(){
	return getPeer_NoClientCode();
    }
    // NOTE: This method is called by privileged threads.
    //       We implement this functionality in a package-private method 
    //       to insure that it cannot be overridden by client subclasses. 
    //       DO NOT INVOKE CLIENT CODE ON THIS THREAD!
    final FontPeer getPeer_NoClientCode() {
        if(peer == null) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            this.peer = tk.getFontPeer(name, style);
        }
        return peer;
    }

    /* create this map only when requested - which may be rarely */
    private Hashtable getRequestedAttributes() {
	if (fRequestedAttributes == null) {
	    fRequestedAttributes = new Hashtable(7, (float)0.9);
            fRequestedAttributes.put(TextAttribute.TRANSFORM,
				     IDENT_TX_ATTRIBUTE);
            fRequestedAttributes.put(TextAttribute.FAMILY, name);
            fRequestedAttributes.put(TextAttribute.SIZE, new Float(size));
	    fRequestedAttributes.put(TextAttribute.WEIGHT,
				     (style & BOLD) != 0 ? 
				     TextAttribute.WEIGHT_BOLD :
				     TextAttribute.WEIGHT_REGULAR);
	    fRequestedAttributes.put(TextAttribute.POSTURE,
				     (style & ITALIC) != 0 ? 
				     TextAttribute.POSTURE_OBLIQUE :
				     TextAttribute.POSTURE_REGULAR);
            fRequestedAttributes.put(TextAttribute.SUPERSCRIPT,
                                     new Integer(superscript));
            fRequestedAttributes.put(TextAttribute.WIDTH,
                                     new Float(width));
	}
	return fRequestedAttributes;
    }

    private void initializeFont(Hashtable attributes) {
        if (attributes != null) {
	    Object obj = attributes.get(TextAttribute.TRANSFORM);
	    if (obj instanceof TransformAttribute) {
		nonIdentityTx = !((TransformAttribute)obj).isIdentity();
	    } else if (obj instanceof AffineTransform) {
		nonIdentityTx = !((AffineTransform)obj).isIdentity();
	    }

            obj = attributes.get(TextAttribute.SUPERSCRIPT);
            if (obj instanceof Integer) {
                superscript = ((Integer)obj).intValue();

                // !!! always synthesize superscript
                nonIdentityTx |= superscript != 0;
            }

            obj = attributes.get(TextAttribute.WIDTH);
            if (obj instanceof Integer) {
                width = ((Float)obj).floatValue();

                // !!! always synthesize width
                nonIdentityTx |= width != 1;
            }
        }
    }

    private Font2D getFont2D() {
	if (FontManager.usingPerAppContextComposites &&
	    font2DHandle != null &&
	    font2DHandle.font2D instanceof sun.font.CompositeFont &&
	    ((sun.font.CompositeFont)(font2DHandle.font2D)).isStdComposite()) {
	    return FontManager.findFont2D(name, style,
					  FontManager.LOGICAL_FALLBACK);
	} else if (font2DHandle == null) {
	    font2DHandle =
		FontManager.findFont2D(name, style,
				       FontManager.LOGICAL_FALLBACK).handle;
	}
	/* Do not cache the de-referenced font2D. It must be explicitly
	 * de-referenced to pick up a valid font in the event that the
	 * original one is marked invalid
	 */
	return font2DHandle.font2D;
    }

    /*
     * If this font was created by "createFont" return its handle,
     * else return null. created fonts always have a non-null handle.
     * The test for CompositeFont is because the boolean "createdFont"
     * is overloaded to indicate any font that needs to copy its handle
     * and that doesn't apply to true created fonts.
     */
    private Font2DHandle getFont2DHandleForCreatedFont() {
        if (font2DHandle != null && createdFont &&
            !(font2DHandle.font2D instanceof sun.font.CompositeFont)) {
            return font2DHandle;
        } else {
            return null;
        }
    }

    /**
     * Creates a new <code>Font</code> from the specified name, style and
     * point size.
     * <p>
     * The font name can be a font face name or a font family name.
     * It is used together with the style to find an appropriate font face.
     * When a font family name is specified, the style argument is used to
     * select the most appropriate face from the family. When a font face
     * name is specified, the face's style and the style argument are
     * merged to locate the best matching font from the same family.
     * For example if face name "Arial Bold" is specified with style
     * <code>Font.ITALIC</code>, the font system looks for a face in the
     * "Arial" family that is bold and italic, and may associate the font
     * instance with the physical font face "Arial Bold Italic".
     * The style argument is merged with the specified face's style, not
     * added or subtracted.
     * This means, specifying a bold face and a bold style does not
     * double-embolden the font, and specifying a bold face and a plain
     * style does not lighten the font.
     * <p>
     * If no face for the requested style can be found, the font system
     * may apply algorithmic styling to achieve the desired style.
     * For example, if <code>ITALIC</code> is requested, but no italic
     * face is available, glyphs from the plain face may be algorithmically
     * obliqued (slanted).
     * <p>
     * Font name lookup is case insensitive, using the case folding
     * rules of the US locale.
     *
     * @param name the font name.  This can be a font face name or a font
     * family name, and may represent either a logical font or a physical
     * font found in this <code>GraphicsEnvironment</code>.
     * The family names for logical fonts are: Dialog, DialogInput,
     * Monospaced, Serif, or SansSerif.	 If <code>name</code> is
     * <code>null</code>, the <em>logical font name</em> of the new
     * <code>Font</code> as returned by <code>getName()</code>is set to
     * the name "Default".
     * @param style the style constant for the <code>Font</code>
     * The style argument is an integer bitmask that may
     * be PLAIN, or a bitwise union of BOLD and/or ITALIC
     * (for example, ITALIC or BOLD|ITALIC).
     * If the style argument does not conform to one of the expected
     * integer bitmasks then the style is set to PLAIN.
     * @param size the point size of the <code>Font</code>
     * @see GraphicsEnvironment#getAllFonts
     * @see GraphicsEnvironment#getAvailableFontFamilyNames
     * @since JDK1.0
     */
    public Font(String name, int style, int size) {
	this.name = (name != null) ? name : "Default";
	this.style = (style & ~0x03) == 0 ? style : 0;
	this.size = size;
        this.pointSize = size;
    }

    private Font(String name, int style, float sizePts) {
	this.name = (name != null) ? name : "Default";
	this.style = (style & ~0x03) == 0 ? style : 0;
	this.size = (int)(sizePts + 0.5);
        this.pointSize = sizePts;
    }

    /* used to implement Font.createFont */
	private Font(File fontFile, int fontFormat,
                  boolean isCopy, CreatedFontTracker tracker)
        throws FontFormatException {
	this.createdFont = true;
	/* Font2D instances created by this method track their font file
	 * so that when the Font2D is GC'd it can also remove the file.
	 */
	this.font2DHandle =
	    FontManager.createFont2D(fontFile, fontFormat,
                                  isCopy, tracker).handle;

	this.name = this.font2DHandle.font2D.getFontName(Locale.getDefault());
	this.style = Font.PLAIN;
	this.size = 1;
	this.pointSize = 1f;
    }

    private Font(Map attributes, boolean created, Font2DHandle handle) {
	this.createdFont = created;
	/* Fonts created from a stream will use the same font2D instance
	 * as the parent.
	 */
	if (created) {
	    this.font2DHandle = handle;
	}
	initFromMap(attributes);
    }

    /**
     * Creates a new <code>Font</code> with the specified attributes.
     * This <code>Font</code> only recognizes keys defined in 
     * {@link TextAttribute} as attributes.  If <code>attributes</code>
     * is <code>null</code>, a new <code>Font</code> is initialized
     * with default attributes.
     * @param attributes the attributes to assign to the new
     *		<code>Font</code>, or <code>null</code>
     */
    public Font(Map<? extends Attribute, ?> attributes) {
	initFromMap(attributes);
    }	


    private void initFromMap(Map attributes) {
	this.name = "Dialog";
        this.pointSize = 12;
        this.size = 12;

        if((attributes != null) &&
           (!attributes.equals(EMPTY_MAP)))
        {
            Object obj;
            fRequestedAttributes = new Hashtable(attributes);
            if ((obj = attributes.get(TextAttribute.FAMILY)) != null) {
                this.name = (String)obj;
            }

            if ((obj = attributes.get(TextAttribute.WEIGHT)) != null) {
                if(obj.equals(TextAttribute.WEIGHT_BOLD)) {
                    this.style |= BOLD;
                }
            }

            if ((obj = attributes.get(TextAttribute.POSTURE)) != null) {
                if(obj.equals(TextAttribute.POSTURE_OBLIQUE)) {
                    this.style |= ITALIC;
                }
            }

            if ((obj = attributes.get(TextAttribute.SIZE)) != null) {
                this.pointSize = ((Float)obj).floatValue();
                this.size = (int)(this.pointSize + 0.5);
            }

            if ((obj = attributes.get(TextAttribute.TRANSFORM)) != null) {
		if (obj instanceof TransformAttribute) {
		    nonIdentityTx = !((TransformAttribute)obj).isIdentity();
		} else if (obj instanceof AffineTransform) {
		    nonIdentityTx = !((AffineTransform)obj).isIdentity();
		}
	    }

            if ((obj = attributes.get(TextAttribute.SUPERSCRIPT)) != null) {
                if (obj instanceof Integer) {
                    superscript = ((Integer)obj).intValue();
                    nonIdentityTx |= superscript != 0;
                }
            }

            if ((obj = attributes.get(TextAttribute.WIDTH)) != null) {
                if (obj instanceof Float) {
                    width = ((Float)obj).floatValue();
                    nonIdentityTx |= width != 1;
                }
            }
        }
    }

     /**
     * Returns a <code>Font</code> appropriate to this attribute set.
     *
     * @param attributes the attributes to assign to the new 
     *		<code>Font</code>
     * @return a new <code>Font</code> created with the specified
     * 		attributes
     * @since 1.2
     * @see java.awt.font.TextAttribute
     */
    public static Font getFont(Map<? extends Attribute, ?> attributes) {
        Font font = (Font)attributes.get(TextAttribute.FONT);
        if (font != null) {
            return font;
        }

	return get(new Key(attributes));
    }

    private static SoftReference cacheRef = new SoftReference(new HashMap());
    private static Font get(Key key) {
	Font f = null;
	Map cache = (Map)cacheRef.get();
	if (cache == null) {
	    cache = new HashMap();
	    cacheRef = new SoftReference(cache);
	} else {
	    f = (Font)cache.get(key);
	}

	if (f == null) {
	    f = new Font(key.attrs);
	    cache.put(key, f);
	}

	return f;
    }

    // ideally we would construct a font directly from a key, and not
    // bother to keep the map around for this.  That ought to be a bit 
    // faster than picking out the params from the Map again, but the
    // cache ought to hide this overhead, so I'll skip it for now.

    private static class Key {
	String family = "Dialog"; // defaults chosen to match Font implementation
	float weight = 1.0f;
	float posture = 0.0f;
	float size = 12.0f;
        int superscript = 0;
        float width = 1.0f;
	double[] txdata = null; // identity

	Map attrs;
	int hashCode = 0;

	Key(Map map) {
	    attrs = map;

	    Object o = map.get(TextAttribute.FAMILY);
	    if (o != null) {
		family = (String)o;
	    }
	    hashCode = family.hashCode();

	    o = map.get(TextAttribute.WEIGHT);
	    if (o != null && o != TextAttribute.WEIGHT_REGULAR) {
		// ugh, force to the only values we understand
		// weight is either bold, or it's not...
		float xweight = ((Float)o).floatValue();
		if (xweight == TextAttribute.WEIGHT_BOLD.floatValue()) {
		    weight = xweight;
		    hashCode = (hashCode << 3) ^ Float.floatToIntBits(weight);
		}
	    }

	    o = map.get(TextAttribute.POSTURE);
	    if (o != null && o != TextAttribute.POSTURE_REGULAR) {
		// ugh, same problem as with weight
		float xposture = ((Float)o).floatValue();
		if (xposture == TextAttribute.POSTURE_OBLIQUE.floatValue()) {
		    posture = xposture;
		    hashCode = (hashCode << 3) ^ Float.floatToIntBits(posture);
		}
	    }

	    o = map.get(TextAttribute.SIZE);
	    if (o != null) {
		size = ((Float)o).floatValue();
		if (size != 12.0f) {
		    hashCode = (hashCode << 3) ^ Float.floatToIntBits(size);
		}
	    }

	    o = map.get(TextAttribute.TRANSFORM);
	    if (o != null) {
		AffineTransform tx = null;
		if (o instanceof TransformAttribute) {
		    TransformAttribute ta = (TransformAttribute)o;
		    if (!ta.isIdentity()) {
			tx = ta.getTransform();
		    }
		} else if (o instanceof AffineTransform) {
		    AffineTransform at = (AffineTransform)o;
		    if (!at.isIdentity()) {
			tx = at;
		    }
		}
		if (tx != null) {
		    txdata = new double[6];
		    tx.getMatrix(txdata);
		    hashCode = (hashCode << 3) ^ new Double(txdata[0]).hashCode();
		}
	    }

            o = map.get(TextAttribute.SUPERSCRIPT);
            if (o != null) {
                if (o instanceof Integer) {
                    superscript = ((Integer)o).intValue();
                    hashCode = hashCode << 3 ^ superscript;
                }
            }

            o = map.get(TextAttribute.WIDTH);
            if (o != null) {
                if (o instanceof Float) {
                    width = ((Float)o).floatValue();
                    hashCode = hashCode << 3 ^ Float.floatToIntBits(width);
                }
            }
	}

	public int hashCode() {
	    return hashCode;
	}

	public boolean equals(Object rhs) {
	    Key rhskey = (Key)rhs;
	    if (this.hashCode == rhskey.hashCode && 
		this.size == rhskey.size &&
		this.weight == rhskey.weight &&
		this.posture == rhskey.posture &&
                this.superscript == rhskey.superscript &&
                this.width == rhskey.width &&
		this.family.equals(rhskey.family) &&
		((this.txdata == null) == (rhskey.txdata == null))) {
		
		if (this.txdata != null) {
		    for (int i = 0; i < this.txdata.length; ++i) {
			if (this.txdata[i] != rhskey.txdata[i]) {
			    return false;
			}
		    }
		}
		return true;
	    }
	    return false;
	}
    }
    
 
    /**
     * Used with the byte count tracker for fonts created from streams.
     * If a thread can create temp files anyway, no point in counting
     * font bytes.
     */
    private static boolean hasTempPermission() {

        if (System.getSecurityManager() == null) {
            return true;
        }
        File f = null;
        boolean hasPerm = false;
        try {
            f = File.createTempFile("+~JT", ".tmp", null);
            f.delete();
            f = null;
            hasPerm = true;
        } catch (Throwable t) {
            /* inc. any kind of SecurityException */
        }
        return hasPerm;
    }

  /**
   * Returns a new <code>Font</code> using the specified font type
   * and input data.  The new <code>Font</code> is
   * created with a point size of 1 and style {@link #PLAIN PLAIN}.
   * This base font can then be used with the <code>deriveFont</code>
   * methods in this class to derive new <code>Font</code> objects with
   * varying sizes, styles, transforms and font features.  This
   * method does not close the {@link InputStream}.
   * @param fontFormat the type of the <code>Font</code>, which is
   * {@link #TRUETYPE_FONT TRUETYPE_FONT} if a TrueType resource is specified.
   * or {@link #TYPE1_FONT TYPE1_FONT} if a Type 1 resource is specified.
   * @param fontStream an <code>InputStream</code> object representing the
   * input data for the font.
   * @return a new <code>Font</code> created with the specified font type.
   * @throws IllegalArgumentException if <code>fontFormat</code> is not
   *     <code>TRUETYPE_FONT</code>or<code>TYPE1_FONT</code>.
   * @throws FontFormatException if the <code>fontStream</code> data does
   *     not contain the required font tables for the specified format.
   * @throws IOException if the <code>fontStream</code>
   *     cannot be completely read.
   * @since 1.3
   */
    public static Font createFont(int fontFormat, InputStream fontStream) 
    throws java.awt.FontFormatException, java.io.IOException {

	if (fontFormat != Font.TRUETYPE_FONT &&
	    fontFormat != Font.TYPE1_FONT) {
	    throw new IllegalArgumentException ("font format not recognized");
	}
	boolean copiedFontData = false;

	try {
             final File tFile = AccessController.doPrivileged(
                 new PrivilegedExceptionAction<File>() {
                     public File run() throws IOException {
                         File tFile =  File.createTempFile("+~JF", ".tmp", null);
                         tFile.deleteOnExit();
                         return tFile;
                     }
                }
            );

            int totalSize = 0;
            CreatedFontTracker tracker = null;
             try {
                 final OutputStream outStream =
                     AccessController.doPrivileged(
                         new PrivilegedExceptionAction<OutputStream>() {
                             public OutputStream run() throws IOException {
                                 return new FileOutputStream(tFile);
                             }
                         }
                     );
                 if (!hasTempPermission()) {
                     tracker = CreatedFontTracker.getTracker();
                 }
                 try {
                     byte[] buf = new byte[8192]; 
                     for (;;) {
                         int bytesRead = fontStream.read(buf);
                         if (bytesRead < 0) {
                             break;
                         }
                         if (tracker != null) {
                             if (totalSize+bytesRead > tracker.MAX_FILE_SIZE) {
                                 throw new IOException("File too big.");
                             }
                             if (totalSize+tracker.getNumBytes() >
                                 tracker.MAX_TOTAL_BYTES)
                               {
                                 throw new IOException("Total files too big.");
                             }
                             totalSize += bytesRead;
                             tracker.addBytes(bytesRead);
                         }
                         outStream.write(buf, 0, bytesRead);
                     }
                     /* don't close the input stream */
                 } finally {
                     outStream.close();
                 }
                 /* After all references to a Font2D are dropped, the file
                  * will be removed. To support long-lived AppContexts,
                  * we need to then decrement the byte count by the size
                  * of the file.
                  * If the data isn't a valid font, the implementation will
                  * delete the tmp file and decrement the byte count
                  * in the tracker object before returning from the
                  * constructor, so we can set 'copiedFontData' to true here
                  * without waiting for the results of that constructor.
                  */
                 copiedFontData = true;
                 Font font = new Font(tFile, fontFormat, true, tracker);
                 return font;
             } finally {
                 if (!copiedFontData) {
                     if (tracker != null) {
			tracker.subBytes(totalSize);
                     } 
		     AccessController.doPrivileged(
                         new PrivilegedExceptionAction<Void>() {
                             public Void run() {
                                 tFile.delete();
                                 return null;
                             }
                         }
                     );
                 }
             }
         } catch (Throwable t) {
             if (t instanceof FontFormatException) {
                 throw (FontFormatException)t;
             }
             if (t instanceof IOException) {
                 throw (IOException)t;
             }
             Throwable cause = t.getCause();
             if (cause instanceof FontFormatException) {
                 throw (FontFormatException)cause;
             }
             throw new IOException("Problem reading font data.");
         }
     }

    /**
     * Returns a new <code>Font</code> using the specified font type
     * and the specified font file.  The new <code>Font</code> is
     * created with a point size of 1 and style {@link #PLAIN PLAIN}.
     * This base font can then be used with the <code>deriveFont</code>
     * methods in this class to derive new <code>Font</code> objects with
     * varying sizes, styles, transforms and font features.
     * @param fontFormat the type of the <code>Font</code>, which is
     * {@link #TRUETYPE_FONT TRUETYPE_FONT} if a TrueType resource is
     * specified or {@link #TYPE1_FONT TYPE1_FONT} if a Type 1 resource is
     * specified.
     * So long as the returned font, or its derived fonts are referenced
     * the implementation may continue to access <code>fontFile</code>
     * to retrieve font data. Thus the results are undefined if the file
     * is changed, or becomes inaccessible.
     * @param fontFile a <code>File</code> object representing the
     * input data for the font.
     * @return a new <code>Font</code> created with the specified font type.
     * @throws IllegalArgumentException if <code>fontFormat</code> is not
     *     <code>TRUETYPE_FONT</code>or<code>TYPE1_FONT</code>.
     * @throws NullPointerException if <code>fontFile</code> is null.
     * @throws IOException if the <code>fontFile</code> cannot be read.
     * @throws FontFormatException if <code>fontFile</code> does
     *     not contain the required font tables for the specified format.
     * @throws SecurityException if the executing code does not have
     * permission to read from the file.
     * @since 1.5
     */
    public static Font createFont(int fontFormat, File fontFile)
        throws java.awt.FontFormatException, java.io.IOException {

        fontFile = new File(fontFile.getPath());

        if (fontFormat != Font.TRUETYPE_FONT &&
            fontFormat != Font.TYPE1_FONT) {
            throw new IllegalArgumentException ("font format not recognized");
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            FilePermission filePermission =
                new FilePermission(fontFile.getPath(), "read");
            sm.checkPermission(filePermission);
        }
        if (!fontFile.canRead()) {
            throw new IOException("Can't read " + fontFile);
        }
        return new Font(fontFile, fontFormat, false, null);
    }

    /**
     * Returns a copy of the transform associated with this 
     * <code>Font</code>.
     * @return an {@link AffineTransform} object representing the
     *		transform attribute of this <code>Font</code> object.
     */
    public AffineTransform getTransform() {
	/* The most common case is the identity transform.  Most callers 
	 * should call isTransformed() first, to decide if they need to
	 * get the transform, but some may not.  Here we check to see
	 * if we have a nonidentity transform, and only do the work to
	 * fetch and/or compute it if so, otherwise we return a new
	 * identity transform.
	 *
	 * Note that the transform is _not_ necessarily the same as 
	 * the transform passed in as an Attribute in a Map, as the
	 * transform returned will also reflect the effects of WIDTH and
	 * SUPERSCRIPT attributes.  Clients who want the actual transform
	 * need to call getRequestedAttributes.
	 */
	if (nonIdentityTx) {
            AffineTransform at = null;
	    Object obj = getRequestedAttributes().get(TextAttribute.TRANSFORM);
	    if (obj != null) {
		if( obj instanceof TransformAttribute ){
		    at = ((TransformAttribute)obj).getTransform();
		}
		else {
		    if ( obj instanceof AffineTransform){
			at = new AffineTransform((AffineTransform)obj);
		    }
		}
	    } else {
                at = new AffineTransform();
            }
            
            if (superscript != 0) {
                // can't get ascent and descent here, recursive call to this fn, so use pointsize
                // let users combine super- and sub-scripting
                double trans = 0;
                int n = 0;
                boolean up = superscript > 0;
                int sign = up ? -1 : 1;
                int ss = up ? superscript : -superscript;

                while ((ss & 7) > n) {
                    int newn = ss & 7;
                    trans += sign * (ssinfo[newn] - ssinfo[n]);
                    ss >>= 3;
                    sign = -sign;
                    n = newn;
                }
                trans *= pointSize;
                double scale = Math.pow(2./3., n);

                at.preConcatenate(AffineTransform.getTranslateInstance(0, trans));
                at.scale(scale, scale);

                // note on placement and italics
                // We preconcatenate the transform because we don't want to translate along
                // the italic angle, but purely perpendicular to the baseline.  While this
                // looks ok for superscripts, it can lead subscripts to stack on each other
                // and bring the following text too close.  The way we deal with potential
                // collisions that can occur in the case of italics is by adjusting the
                // horizontal spacing of the adjacent glyphvectors.  Examine the italic
                // angle of both vectors, if one is non-zero, compute the minimum ascent
                // and descent, and then the x position at each for each vector along its
                // italic angle starting from its (offset) baseline.  Compute the difference
                // between the x positions and use the maximum difference to adjust the
                // position of the right gv.
            }

            if (width != 1f) {
                at.scale(width, 1f);
            }

            return at;
        }

        return new AffineTransform();
    }

    // x = r^0 + r^1 + r^2... r^n
    // rx = r^1 + r^2 + r^3... r^(n+1)
    // x - rx = r^0 - r^(n+1)
    // x (1 - r) = r^0 - r^(n+1)
    // x = (r^0 - r^(n+1)) / (1 - r)
    // x = (1 - r^(n+1)) / (1 - r)

    // scale ratio is 2/3
    // trans = 1/2 of ascent * x
    // assume ascent is 3/4 of point size

    private static final float[] ssinfo = {
        0.0f,
        0.375f,
        0.625f,
        0.7916667f,
        0.9027778f,
        0.9768519f,
        1.0262346f,
        1.0591564f,
    };

    /**
     * Returns the family name of this <code>Font</code>.  
     * 
     * <p>The family name of a font is font specific. Two fonts such as 
     * Helvetica Italic and Helvetica Bold have the same family name, 
     * <i>Helvetica</i>, whereas their font face names are 
     * <i>Helvetica Bold</i> and <i>Helvetica Italic</i>. The list of 
     * available family names may be obtained by using the 
     * {@link GraphicsEnvironment#getAvailableFontFamilyNames()} method.
     * 
     * <p>Use <code>getName</code> to get the logical name of the font.
     * Use <code>getFontName</code> to get the font face name of the font.
     * @return a <code>String</code> that is the family name of this
     *		<code>Font</code>.
     * 
     * @see #getName
     * @see #getFontName
     * @since JDK1.1
     */
    public String getFamily() {
	return getFamily_NoClientCode();
    }
    // NOTE: This method is called by privileged threads.
    //       We implement this functionality in a package-private
    //       method to insure that it cannot be overridden by client
    //       subclasses. 
    //       DO NOT INVOKE CLIENT CODE ON THIS THREAD!
    final String getFamily_NoClientCode() {
      return getFamily(Locale.getDefault());
    }

    /**
     * Returns the family name of this <code>Font</code>, localized for
     * the specified locale.
     * 
     * <p>The family name of a font is font specific. Two fonts such as 
     * Helvetica Italic and Helvetica Bold have the same family name, 
     * <i>Helvetica</i>, whereas their font face names are 
     * <i>Helvetica Bold</i> and <i>Helvetica Italic</i>. The list of 
     * available family names may be obtained by using the 
     * {@link GraphicsEnvironment#getAvailableFontFamilyNames()} method.
     * 
     * <p>Use <code>getFontName</code> to get the font face name of the font.
     * @param l locale for which to get the family name
     * @return a <code>String</code> representing the family name of the
     *		font, localized for the specified locale.
     * @see #getFontName
     * @see java.util.Locale
     * @since 1.2
     */
    public String getFamily(Locale l) {
        if (l == null) {
            throw new NullPointerException("null locale doesn't mean default");
        }
	return getFont2D().getFamilyName(l);
    }

    /**
     * Returns the postscript name of this <code>Font</code>.
     * Use <code>getFamily</code> to get the family name of the font.
     * Use <code>getFontName</code> to get the font face name of the font.
     * @return a <code>String</code> representing the postscript name of
     *		this <code>Font</code>.
     * @since 1.2
     */
    public String getPSName() {
	return getFont2D().getPostscriptName();
    }

    /**
     * Returns the logical name of this <code>Font</code>.
     * Use <code>getFamily</code> to get the family name of the font.
     * Use <code>getFontName</code> to get the font face name of the font.
     * @return a <code>String</code> representing the logical name of
     *		this <code>Font</code>.
     * @see #getFamily
     * @see #getFontName
     * @since JDK1.0
     */
    public String getName() {
	return name;
    }

    /**
     * Returns the font face name of this <code>Font</code>.  For example,
     * Helvetica Bold could be returned as a font face name.
     * Use <code>getFamily</code> to get the family name of the font.
     * Use <code>getName</code> to get the logical name of the font.
     * @return a <code>String</code> representing the font face name of 
     *		this <code>Font</code>.
     * @see #getFamily
     * @see #getName
     * @since 1.2
     */
    public String getFontName() {
      return getFontName(Locale.getDefault());
    }

    /**
     * Returns the font face name of the <code>Font</code>, localized
     * for the specified locale. For example, Helvetica Fett could be
     * returned as the font face name.
     * Use <code>getFamily</code> to get the family name of the font.
     * @param l a locale for which to get the font face name
     * @return a <code>String</code> representing the font face name,
     *		localized for the specified locale.
     * @see #getFamily
     * @see java.util.Locale
     */
    public String getFontName(Locale l) {
        if (l == null) {
            throw new NullPointerException("null locale doesn't mean default");
        }
	return getFont2D().getFontName(l);
    }

    /**
     * Returns the style of this <code>Font</code>.  The style can be
     * PLAIN, BOLD, ITALIC, or BOLD+ITALIC.
     * @return the style of this <code>Font</code>
     * @see #isPlain
     * @see #isBold
     * @see #isItalic
     * @since JDK1.0
     */
    public int getStyle() {
	return style;
    }

    /**
     * Returns the point size of this <code>Font</code>, rounded to
     * an integer.
     * Most users are familiar with the idea of using <i>point size</i> to
     * specify the size of glyphs in a font. This point size defines a
     * measurement between the baseline of one line to the baseline of the
     * following line in a single spaced text document. The point size is
     * based on <i>typographic points</i>, approximately 1/72 of an inch.
     * <p>
     * The Java(tm)2D API adopts the convention that one point is
     * equivalent to one unit in user coordinates.  When using a
     * normalized transform for converting user space coordinates to
     * device space coordinates 72 user
     * space units equal 1 inch in device space.  In this case one point
     * is 1/72 of an inch.
     * @return the point size of this <code>Font</code> in 1/72 of an 
     *		inch units.
     * @see #getSize2D
     * @see GraphicsConfiguration#getDefaultTransform
     * @see GraphicsConfiguration#getNormalizingTransform
     * @since JDK1.0
     */
    public int getSize() {
	return size;
    }

    /**
     * Returns the point size of this <code>Font</code> in
     * <code>float</code> value.
     * @return the point size of this <code>Font</code> as a
     * <code>float</code> value.
     * @see #getSize
     * @since 1.2
     */
    public float getSize2D() {
	return pointSize;
    }

    /**
     * Indicates whether or not this <code>Font</code> object's style is
     * PLAIN.
     * @return    <code>true</code> if this <code>Font</code> has a
     * 		  PLAIN sytle;
     *            <code>false</code> otherwise.
     * @see       java.awt.Font#getStyle
     * @since     JDK1.0
     */
    public boolean isPlain() {
	return style == 0;
    }

    /**
     * Indicates whether or not this <code>Font</code> object's style is
     * BOLD.
     * @return    <code>true</code> if this <code>Font</code> object's
     *		  style is BOLD;
     *            <code>false</code> otherwise.
     * @see       java.awt.Font#getStyle
     * @since     JDK1.0
     */
    public boolean isBold() {
	return (style & BOLD) != 0;
    }

    /**
     * Indicates whether or not this <code>Font</code> object's style is
     * ITALIC.
     * @return    <code>true</code> if this <code>Font</code> object's
     *		  style is ITALIC;
     *            <code>false</code> otherwise.
     * @see       java.awt.Font#getStyle
     * @since     JDK1.0
     */
    public boolean isItalic() {
	return (style & ITALIC) != 0;
    }

    /**
     * Indicates whether or not this <code>Font</code> object has a
     * transform that affects its size in addition to the Size
     * attribute.
     * @return	<code>true</code> if this <code>Font</code> object
     *		has a non-identity AffineTransform attribute.
     *		<code>false</code> otherwise.
     * @see	java.awt.Font#getTransform
     * @since	1.4
     */
    public boolean isTransformed() {
	return nonIdentityTx;
    }

    /**
     * Returns a <code>Font</code> object from the system properties list.
     * <code>nm</code> is treated as the name of a system property to be
     * obtained.  The <code>String</code> value of this property is then
     * interpreted as a <code>Font</code> object according to the
     * specification of <code>Font.decode(String)</code>
     * If the specified property is not found, null is returned instead.
     * 
     * @param nm the property name
     * @return a <code>Font</code> object that the property name
     *		describes, or null if no such property exists.
     * @throws NullPointerException if nm is null.
     * @since 1.2
     * @see #decode(String)
     */
    public static Font getFont(String nm) {
	return getFont(nm, null);
    }

    /**
     * Returns the <code>Font</code> that the <code>str</code> 
     * argument describes.
     * To ensure that this method returns the desired Font, 
     * format the <code>str</code> parameter in
     * one of these ways
     * <p>
     * <ul>
     * <li><em>fontname-style-pointsize</em>
     * <li><em>fontname-pointsize</em>
     * <li><em>fontname-style</em>
     * <li><em>fontname</em>
     * <li><em>fontname style pointsize</em>
     * <li><em>fontname pointsize</em>
     * <li><em>fontname style</em>
     * <li><em>fontname</em>
     * </ul>
     * in which <i>style</i> is one of the four 
     * case-insensitive strings:
     * <code>"PLAIN"</code>, <code>"BOLD"</code>, <code>"BOLDITALIC"</code>, or
     * <code>"ITALIC"</code>, and pointsize is a positive decimal integer
     * representation of the point size.
     * For example, if you want a font that is Arial, bold, with
     * a point size of 18, you would call this method with:
     * "Arial-BOLD-18".
     * This is equivalent to calling the Font constructor :
     * <code>new Font("Arial", Font.BOLD, 18);</code>
     * and the values are interpreted as specified by that constructor.
     * <p>
     * A valid trailing decimal field is always interpreted as the pointsize.
     * Therefore a fontname containing a trailing decimal value should not
     * be used in the fontname only form.
     * <p>
     * If a style name field is not one of the valid style strings, it is
     * interpreted as part of the font name, and the default style is used.
     * <p>
     * Only one of ' ' or '-' may be used to separate fields in the input.
     * The identified separator is the one closest to the end of the string
     * which separates a valid pointsize, or a valid style name from
     * the rest of the string.
     * Null (empty) pointsize and style fields are treated
     * as valid fields with the default value for that field. 
     *<p>
     * Some font names may include the separator characters ' ' or '-'.
     * If <code>str</code> is not formed with 3 components, e.g. such that
     * <code>style</code> or <code>pointsize</code> fields are not present in
     * <code>str</code>, and <code>fontname</code> also contains a
     * character determined to be the separator character
     * then these characters where they appear as intended to be part of
     * <code>fontname</code> may instead be interpreted as separators
     * so the font name may not be properly recognised.
     * 
     * <p>
     * The default size is 12 and the default style is PLAIN.
     * If <code>str</code> does not specify a valid size, the returned 
     * <code>Font</code> has a size of 12.  If <code>str</code> does not
     * specify a valid style, the returned Font has a style of PLAIN.
     * If you do not specify a valid font name in 
     * the <code>str</code> argument, this method will return
     * a font with the family name "Dialog".
     * To determine what font family names are available on
     * your system, use the
     * {@link GraphicsEnvironment#getAvailableFontFamilyNames()} method.
     * If <code>str</code> is <code>null</code>, a new <code>Font</code>
     * is returned with the family name "Dialog", a size of 12 and a 
     * PLAIN style.
     * @param str the name of the font, or <code>null</code>
     * @return the <code>Font</code> object that <code>str</code>
     *		describes, or a new default <code>Font</code> if 
     *          <code>str</code> is <code>null</code>.
     * @see #getFamily
     * @since JDK1.1
     */
    public static Font decode(String str) {
	String fontName = str;
	String styleName = "";
	int fontSize = 12;
	int fontStyle = Font.PLAIN;

        if (str == null) {
            return new Font("Dialog", fontStyle, fontSize);
        }
	
	int lastHyphen = str.lastIndexOf('-');
	int lastSpace = str.lastIndexOf(' ');
	char sepChar = (lastHyphen > lastSpace) ? '-' : ' ';
	int sizeIndex = str.lastIndexOf(sepChar);
	int styleIndex = str.lastIndexOf(sepChar, sizeIndex-1);
	int strlen = str.length();

	if (sizeIndex > 0 && sizeIndex+1 < strlen) {
	    try {
		fontSize =
		    Integer.valueOf(str.substring(sizeIndex+1)).intValue();
		if (fontSize <= 0) {
		    fontSize = 12;
		}
	    } catch (NumberFormatException e) {
		/* It wasn't a valid size, if we didn't also find the
		 * start of the style string perhaps this is the style */
		styleIndex = sizeIndex;
		sizeIndex = strlen;
		if (str.charAt(sizeIndex-1) == sepChar) {
		    sizeIndex--;
		}
	    }
	}

	if (styleIndex >= 0 && styleIndex+1 < strlen) {
	    styleName = str.substring(styleIndex+1, sizeIndex);
	    styleName = styleName.toLowerCase(Locale.ENGLISH);
	    if (styleName.equals("bolditalic")) {
		fontStyle = Font.BOLD | Font.ITALIC;
	    } else if (styleName.equals("italic")) {
		fontStyle = Font.ITALIC;
	    } else if (styleName.equals("bold")) {
		fontStyle = Font.BOLD;
	    } else if (styleName.equals("plain")) {
		fontStyle = Font.PLAIN;
	    } else {
		/* this string isn't any of the expected styles, so
		 * assume its part of the font name
		 */
		styleIndex = sizeIndex;
		if (str.charAt(styleIndex-1) == sepChar) {
		    styleIndex--;
		}
	    }
	    fontName = str.substring(0, styleIndex);

	} else {
	    int fontEnd = strlen;
	    if (styleIndex > 0) {
		fontEnd = styleIndex;
	    } else if (sizeIndex > 0) {
		fontEnd = sizeIndex;
	    }
	    if (fontEnd > 0 && str.charAt(fontEnd-1) == sepChar) {
		fontEnd--;
	    }
	    fontName = str.substring(0, fontEnd);
	}

	return new Font(fontName, fontStyle, fontSize);
    }

    /**
     * Gets the specified <code>Font</code> from the system properties
     * list.  As in the <code>getProperty</code> method of 
     * <code>System</code>, the first
     * argument is treated as the name of a system property to be
     * obtained.  The <code>String</code> value of this property is then
     * interpreted as a <code>Font</code> object. 
     * <p>
     * The property value should be one of the forms accepted by
     * <code>Font.decode(String)</code>
     * If the specified property is not found, the <code>font</code> 
     * argument is returned instead. 
     * @param nm the case-insensitive property name
     * @param font a default <code>Font</code> to return if property
     * 		<code>nm</code> is not defined
     * @return    the <code>Font</code> value of the property.
     * @throws NullPointerException if nm is null.
     * @see #decode(String)
     */
    public static Font getFont(String nm, Font font) {
	String str = null;
	try {
	    str =System.getProperty(nm);
	} catch(SecurityException e) {
	}
	if (str == null) {
	    return font;
	}
	return decode ( str );
    }


    /**
     * Returns a hashcode for this <code>Font</code>.
     * @return     a hashcode value for this <code>Font</code>.
     * @since      JDK1.0
     */
    public int hashCode() {
	return name.hashCode() ^ style ^ size;
    }

    /**
     * Compares this <code>Font</code> object to the specified 
     * <code>Object</code>.
     * @param obj the <code>Object</code> to compare
     * @return <code>true</code> if the objects are the same
     *          or if the argument is a <code>Font</code> object
     *          describing the same font as this object; 
     *		<code>false</code> otherwise.
     * @since JDK1.0
     */
    public boolean equals(Object obj) {
        if (obj == this) {
	    return true;
        }

	if (obj != null) {
	  try {
	    Font font = (Font)obj;
	    if ((size == font.size) &&
		(pointSize == font.pointSize) &&
		(style == font.style) &&
                (superscript == font.superscript) &&
                (width == font.width) &&
		name.equals(font.name)) {
		
		double[] thismat = this.getMatrix();
		double[] thatmat = font.getMatrix();
	    
		return thismat[0] == thatmat[0]
		    && thismat[1] == thatmat[1]
		    && thismat[2] == thatmat[2]
		    && thismat[3] == thatmat[3]
		    && thismat[4] == thatmat[4]
		    && thismat[5] == thatmat[5];
	    }
	  }
	  catch (ClassCastException e) {
	  }
	}
	return false;
    }

    /**
     * Converts this <code>Font</code> object to a <code>String</code>
     * representation.
     * @return     a <code>String</code> representation of this 
     *		<code>Font</code> object.
     * @since      JDK1.0
     */
    // NOTE: This method may be called by privileged threads.
    //       DO NOT INVOKE CLIENT CODE ON THIS THREAD!
    public String toString() {
	String	strStyle;

	if (isBold()) {
	    strStyle = isItalic() ? "bolditalic" : "bold";
	} else {
	    strStyle = isItalic() ? "italic" : "plain";
	}

	return getClass().getName() + "[family=" + getFamily() + ",name=" + name + ",style=" +
	    strStyle + ",size=" + size + "]";
    } // toString()


    /** Serialization support.  A <code>readObject</code>
     *  method is neccessary because the constructor creates
     *  the font's peer, and we can't serialize the peer.
     *  Similarly the computed font "family" may be different
     *  at <code>readObject</code> time than at
     *  <code>writeObject</code> time.  An integer version is
     *  written so that future versions of this class will be
     *  able to recognize serialized output from this one.
     */
    /**
     * The <code>Font</code> Serializable Data Form.
     *
     * @serial
     */
    private int fontSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to a stream.
     *
     * @param s the <code>ObjectOutputStream</code> to write
     * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
     * @see #readObject(java.io.ObjectInputStream)
     */
    private void writeObject(java.io.ObjectOutputStream s)
      throws java.lang.ClassNotFoundException,
	     java.io.IOException
    {
      s.defaultWriteObject();
    }

    /**
     * Reads the <code>ObjectInputStream</code>.
     * Unrecognized keys or values will be ignored.
     *
     * @param s the <code>ObjectInputStream</code> to read
     * @serial
     * @see #writeObject(java.io.ObjectOutputStream)
     */
    private void readObject(java.io.ObjectInputStream s)
      throws java.lang.ClassNotFoundException,
	     java.io.IOException
    {
      s.defaultReadObject();
      if (pointSize == 0) {
		pointSize = (float)size;
 	  }
      width = 1f; // init transient field
      initializeFont(fRequestedAttributes);
    }

    /**
     * Returns the number of glyphs in this <code>Font</code>. Glyph codes
     * for this <code>Font</code> range from 0 to 
     * <code>getNumGlyphs()</code> - 1.
     * @return the number of glyphs in this <code>Font</code>.
     * @since 1.2
     */
    public int getNumGlyphs() {
	return  getFont2D().getNumGlyphs();
    }

    /**
     * Returns the glyphCode which is used when this <code>Font</code> 
     * does not have a glyph for a specified unicode.
     * @return the glyphCode of this <code>Font</code>.
     * @since 1.2
     */
    public int getMissingGlyphCode() {
	return getFont2D().getMissingGlyphCode();
    }

    /**
     * get the transform matrix for this font.
     */
    /* for identity transforms this code attempts to share a matrix
     * amongst fonts with the same pt size */
    private static double cachedMat[];
    private double[] getMatrix() {
        if (matrix == null) {
	    double ptSize = this.getSize2D();
	    if (nonIdentityTx) {
		AffineTransform tx = getTransform();
		tx.scale(ptSize, ptSize);
		tx.getMatrix(matrix = new double[6]);
	    } else {
		synchronized (Font.class) {
		    double[] m = cachedMat;
		    if (m == null || m[0] != ptSize) {
			cachedMat = m =
			    new double[] {ptSize, 0, 0, ptSize, 0, 0 };
		    }
		    matrix = m;
		}
	    }
        }
	return matrix;
    }

    /**
     * Returns the baseline appropriate for displaying this character.
     * <p>
     * Large fonts can support different writing systems, and each system can
     * use a different baseline.
     * The character argument determines the writing system to use. Clients
     * should not assume all characters use the same baseline.
     *
     * @param c a character used to identify the writing system
     * @return the baseline appropriate for the specified character.
     * @see LineMetrics#getBaselineOffsets
     * @see #ROMAN_BASELINE
     * @see #CENTER_BASELINE
     * @see #HANGING_BASELINE
     * @since 1.2
     */
    public byte getBaselineFor(char c) {
	return getFont2D().getBaselineFor(c);
    }

    /**
     * Returns a map of font attributes available in this
     * <code>Font</code>.  Attributes include things like ligatures and
     * glyph substitution.
     * @return the attributes map of this <code>Font</code>.
     */
    public Map<TextAttribute,?> getAttributes(){
        return (Map<TextAttribute,?>)getRequestedAttributes().clone();
    }

    /**
     * Returns the keys of all the attributes supported by this 
     * <code>Font</code>.  These attributes can be used to derive other
     * fonts.
     * @return an array containing the keys of all the attributes
     *		supported by this <code>Font</code>.
     * @since 1.2
     */
    public Attribute[] getAvailableAttributes(){
        Attribute attributes[] = {
            TextAttribute.FAMILY,
            TextAttribute.WEIGHT,
            TextAttribute.POSTURE,
            TextAttribute.SIZE,
	    TextAttribute.TRANSFORM,
            TextAttribute.SUPERSCRIPT,
            TextAttribute.WIDTH,
        };

        return attributes;
    }

    /**
     * Creates a new <code>Font</code> object by replicating this
     * <code>Font</code> object and applying a new style and size.
     * @param style the style for the new <code>Font</code>
     * @param size the size for the new <code>Font</code>
     * @return a new <code>Font</code> object.
     * @since 1.2
     */
    public Font deriveFont(int style, float size){
	Hashtable newAttributes = (Hashtable)getRequestedAttributes().clone();
	applyStyle(style, newAttributes);
	applySize(size, newAttributes);
        return new Font(newAttributes, createdFont, font2DHandle);
    }

    /**
     * Creates a new <code>Font</code> object by replicating this
     * <code>Font</code> object and applying a new style and transform.
     * @param style the style for the new <code>Font</code>
     * @param trans the <code>AffineTransform</code> associated with the
     * new <code>Font</code>
     * @return a new <code>Font</code> object.
     * @throws IllegalArgumentException if <code>trans</code> is
     *         <code>null</code>
     * @since 1.2
     */
    public Font deriveFont(int style, AffineTransform trans){
	Hashtable newAttributes = (Hashtable)getRequestedAttributes().clone();
	applyStyle(style, newAttributes);
	applyTransform(trans, newAttributes);
        return new Font(newAttributes, createdFont, font2DHandle);
    }

    /**
     * Creates a new <code>Font</code> object by replicating the current
     * <code>Font</code> object and applying a new size to it.
     * @param size the size for the new <code>Font</code>.
     * @return a new <code>Font</code> object.
     * @since 1.2
     */
    public Font deriveFont(float size){
	Hashtable newAttributes = (Hashtable)getRequestedAttributes().clone();
	applySize(size, newAttributes);
        return new Font(newAttributes, createdFont, font2DHandle);
    }

    /**
     * Creates a new <code>Font</code> object by replicating the current
     * <code>Font</code> object and applying a new transform to it.
     * @param trans the <code>AffineTransform</code> associated with the
     * new <code>Font</code>
     * @return a new <code>Font</code> object.
     * @throws IllegalArgumentException if <code>trans</code> is 
     *         <code>null</code>
     * @since 1.2
     */
    public Font deriveFont(AffineTransform trans){
	Hashtable newAttributes = (Hashtable)getRequestedAttributes().clone();
	applyTransform(trans, newAttributes);
        return new Font(newAttributes, createdFont, font2DHandle);
    }

    /**
     * Creates a new <code>Font</code> object by replicating the current
     * <code>Font</code> object and applying a new style to it.
     * @param style the style for the new <code>Font</code>
     * @return a new <code>Font</code> object.
     * @since 1.2
     */
    public Font deriveFont(int style){
	Hashtable newAttributes = (Hashtable)getRequestedAttributes().clone();
	applyStyle(style, newAttributes);
        return new Font(newAttributes, createdFont, font2DHandle);
    }

    /**
     * Creates a new <code>Font</code> object by replicating the current
     * <code>Font</code> object and applying a new set of font attributes
     * to it.
     * @param attributes a map of attributes enabled for the new 
     * <code>Font</code>
     * @return a new <code>Font</code> object.
     * @since 1.2
     */
    public Font deriveFont(Map<? extends Attribute, ?> attributes) {
	if (attributes == null || attributes.size() == 0) {
	    return this;
	}

        Hashtable newAttrs = new Hashtable(getAttributes());
	Attribute validAttribs[] = getAvailableAttributes();
	Object obj;

	for(int i = 0; i < validAttribs.length; i++){
	  if ((obj = attributes.get(validAttribs[i])) != null) {
	    newAttrs.put(validAttribs[i],obj);
	  } 
	}
        return new Font(newAttrs, createdFont, font2DHandle);
    }

    /**
     * Checks if this <code>Font</code> has a glyph for the specified
     * character.
     *
     * <p> <b>Note:</b> This method cannot handle <a
     * href="../../java/lang/Character.html#supplementary"> supplementary
     * characters</a>. To support all Unicode characters, including
     * supplementary characters, use the {@link #canDisplay(int)}
     * method or <code>canDisplayUpTo</code> methods.
     *
     * @param c the character for which a glyph is needed
     * @return <code>true</code> if this <code>Font</code> has a glyph for this
     *		character; <code>false</code> otherwise.
     * @since 1.2
     */
    public boolean canDisplay(char c){
	return getFont2D().canDisplay(c);
    }

    /**
     * Checks if this <code>Font</code> has a glyph for the specified
     * character.
     *
     * @param codePoint the character (Unicode code point) for which a glyph
     *        is needed.
     * @return <code>true</code> if this <code>Font</code> has a glyph for the
     *		character; <code>false</code> otherwise.
     * @throws IllegalArgumentException if the code point is not a valid Unicode
     *          code point.
     * @see Character#isValidCodePoint(int)
     * @since 1.5
     */
    public boolean canDisplay(int codePoint) {
	if (!Character.isValidCodePoint(codePoint)) {
	    throw new IllegalArgumentException("invalid code point: " + Integer.toHexString(codePoint));
	}
	return getFont2D().canDisplay(codePoint);
    }

    /**
     * Indicates whether or not this <code>Font</code> can display a
     * specified <code>String</code>.  For strings with Unicode encoding,
     * it is important to know if a particular font can display the
     * string. This method returns an offset into the <code>String</code> 
     * <code>str</code> which is the first character this 
     * <code>Font</code> cannot display without using the missing glyph
     * code. If the <code>Font</code> can display all characters, -1 is
     * returned.
     * @param str a <code>String</code> object
     * @return an offset into <code>str</code> that points
     *		to the first character in <code>str</code> that this
     *		<code>Font</code> cannot display; or <code>-1</code> if
     *		this <code>Font</code> can display all characters in
     *		<code>str</code>.
     * @since 1.2
     */
    public int canDisplayUpTo(String str) {
        return canDisplayUpTo(new StringCharacterIterator(str), 0,
            str.length());
    }

    /**
     * Indicates whether or not this <code>Font</code> can display
     * the characters in the specified <code>text</code> 
     * starting at <code>start</code> and ending at 
     * <code>limit</code>.  This method is a convenience overload.
     * @param text the specified array of <code>char</code> values
     * @param start the specified starting offset (in
     *              <code>char</code>s) into the specified array of
     *              <code>char</code> values
     * @param limit the specified ending offset (in
     *              <code>char</code>s) into the specified array of
     *              <code>char</code> values
     * @return an offset into <code>text</code> that points
     *		to the first character in <code>text</code> that this
     *		<code>Font</code> cannot display; or <code>-1</code> if
     *		this <code>Font</code> can display all characters in
     *		<code>text</code>.
     * @since 1.2
     */
    public int canDisplayUpTo(char[] text, int start, int limit) {
	while (start < limit && canDisplay(text[start])) {
	    ++start;
	}

	return start == limit ? -1 : start;
    }

    /**
     * Indicates whether or not this <code>Font</code> can display the
     * text specified by the <code>iter</code> starting at
     * <code>start</code> and ending at <code>limit</code>.
     *
     * @param iter  a {@link CharacterIterator} object
     * @param start the specified starting offset into the specified
     *              <code>CharacterIterator</code>.
     * @param limit the specified ending offset into the specified
     *              <code>CharacterIterator</code>.
     * @return an offset into <code>iter</code> that points
     *		to the first character in <code>iter</code> that this
     *		<code>Font</code> cannot display; or <code>-1</code> if
     *		this <code>Font</code> can display all characters in
     *		<code>iter</code>.
     * @since 1.2
     */
    public int canDisplayUpTo(CharacterIterator iter, int start, int limit) {
        for (char c = iter.setIndex(start);
             iter.getIndex() < limit && canDisplay(c);
             c = iter.next()) {
        }

	int result = iter.getIndex();
	return result == limit ? -1 : result;
    }

    /**
     * Returns the italic angle of this <code>Font</code>.  The italic angle
     * is the inverse slope of the caret which best matches the posture of this
     * <code>Font</code>.
     * @see TextAttribute#POSTURE
     * @return the angle of the ITALIC style of this <code>Font</code>.
     */
    public float getItalicAngle() {
	AffineTransform at = (isTransformed()) ? getTransform() : identityTx;
	return getFont2D().getItalicAngle(this, at, false, false);
    }

    /**
     * Checks whether or not this <code>Font</code> has uniform 
     * line metrics.  A logical <code>Font</code> might be a
     * composite font, which means that it is composed of different
     * physical fonts to cover different code ranges.  Each of these
     * fonts might have different <code>LineMetrics</code>.  If the
     * logical <code>Font</code> is a single
     * font then the metrics would be uniform. 
     * @return <code>true</code> if this <code>Font</code> has
     * uniform line metrics; <code>false</code> otherwise.
     */
    public boolean hasUniformLineMetrics() {
        return false;   // REMIND always safe, but prevents caller optimize
    }

    private transient SoftReference flmref;
    private FontLineMetrics defaultLineMetrics(FontRenderContext frc) {
        FontLineMetrics flm = null;
        if (flmref == null
            || (flm = (FontLineMetrics)flmref.get()) == null
            || !flm.frc.equals(frc)) {
            
            /* The device transform in the frc is not used in obtaining line
             * metrics, although it probably should be: REMIND find why not?
             * The font transform is used but its applied in getFontMetrics, so
             * just pass identity here
             */
            float [] metrics = new float[4];
            getFont2D().getFontMetrics(this, identityTx,
                                       frc.isAntiAliased(),
                                       frc.usesFractionalMetrics(),
                                       metrics);
            float ascent  = metrics[0];
            float descent = metrics[1];
            float leading = metrics[2];
            float ssOffset = 0;
            if (superscript != 0) {
                ssOffset = (float)getTransform().getTranslateY();
                ascent -= ssOffset;
                descent += ssOffset;
            }
            float height = ascent + descent + leading;

            int baselineIndex = 0; // need real index, assumes roman for everything
            float[] baselineOffsets = { 0, (descent/2f - ascent) / 2f, -ascent }; // need real baselines eventually

            // !!! desperately need real data here
            float strikethroughOffset = ssOffset -(metrics[0] / 2.5f);
            float strikethroughThickness  = (float)(Math.log(pointSize / 4));

            float underlineOffset = ssOffset + strikethroughThickness / 1.5f;
            float underlineThickness = strikethroughThickness;

            float italicAngle = getItalicAngle();

            CoreMetrics cm = new CoreMetrics(ascent, descent, leading, height,
                                             baselineIndex, baselineOffsets,
                                             strikethroughOffset, strikethroughThickness,
                                             underlineOffset, underlineThickness,
                                             ssOffset, italicAngle);

            flm = new FontLineMetrics(0, cm, frc);
            flmref = new SoftReference(flm);
        }

        return (FontLineMetrics)flm.clone();
    }

    /**
     * Returns a {@link LineMetrics} object created with the specified
     * <code>String</code> and {@link FontRenderContext}.
     * @param str the specified <code>String</code>
     * @param frc the specified <code>FontRenderContext</code>
     * @return a <code>LineMetrics</code> object created with the
     * specified <code>String</code> and {@link FontRenderContext}.
     */ 
    public LineMetrics getLineMetrics( String str, FontRenderContext frc) {
        FontLineMetrics flm = defaultLineMetrics(frc);
        flm.numchars = str.length();
        return flm;
    }

    /**
     * Returns a <code>LineMetrics</code> object created with the
     * specified arguments.
     * @param str the specified <code>String</code>
     * @param beginIndex the initial offset of <code>str</code> 
     * @param limit the end offset of <code>str</code>
     * @param frc the specified <code>FontRenderContext</code>
     * @return a <code>LineMetrics</code> object created with the
     * specified arguments.
     */
    public LineMetrics getLineMetrics( String str,
                                    int beginIndex, int limit,
                                    FontRenderContext frc) {
        FontLineMetrics flm = defaultLineMetrics(frc);
        int numChars = limit - beginIndex;
        flm.numchars = (numChars < 0)? 0: numChars;
        return flm;
    }

    /**
     * Returns a <code>LineMetrics</code> object created with the
     * specified arguments.
     * @param chars an array of characters
     * @param beginIndex the initial offset of <code>chars</code>
     * @param limit the end offset of <code>chars</code>
     * @param frc the specified <code>FontRenderContext</code>
     * @return a <code>LineMetrics</code> object created with the
     * specified arguments.
     */
    public LineMetrics getLineMetrics(char [] chars,
                                    int beginIndex, int limit,
                                    FontRenderContext frc) {
        FontLineMetrics flm = defaultLineMetrics(frc);
        int numChars = limit - beginIndex;
        flm.numchars = (numChars < 0)? 0: numChars;
        return flm;
    }

    /**
     * Returns a <code>LineMetrics</code> object created with the
     * specified arguments.
     * @param ci the specified <code>CharacterIterator</code>
     * @param beginIndex the initial offset in <code>ci</code>
     * @param limit the end offset of <code>ci</code>
     * @param frc the specified <code>FontRenderContext</code>
     * @return a <code>LineMetrics</code> object created with the
     * specified arguments.
     */
    public LineMetrics getLineMetrics(CharacterIterator ci,
                                    int beginIndex, int limit,
                                    FontRenderContext frc) {
        FontLineMetrics flm = defaultLineMetrics(frc);
        int numChars = limit - beginIndex;
        flm.numchars = (numChars < 0)? 0: numChars;
        return flm;
    }

    /**
     * Returns the logical bounds of the specified <code>String</code> in
     * the specified <code>FontRenderContext</code>.  The logical bounds
     * contains the origin, ascent, advance, and height, which includes 
     * the leading.  The logical bounds does not always enclose all the
     * text.  For example, in some languages and in some fonts, accent
     * marks can be positioned above the ascent or below the descent.
     * To obtain a visual bounding box, which encloses all the text,
     * use the {@link TextLayout#getBounds() getBounds} method of
     * <code>TextLayout</code>.
     * @param str the specified <code>String</code>
     * @param frc the specified <code>FontRenderContext</code>
     * @return a {@link Rectangle2D} that is the bounding box of the
     * specified <code>String</code> in the specified
     * <code>FontRenderContext</code>.
     * @see FontRenderContext
     * @see Font#createGlyphVector
     * @since 1.2
     */
    public Rectangle2D getStringBounds( String str, FontRenderContext frc) {
      char[] array = str.toCharArray();
      return getStringBounds(array, 0, array.length, frc);
    }

   /**
     * Returns the logical bounds of the specified <code>String</code> in
     * the specified <code>FontRenderContext</code>.  The logical bounds
     * contains the origin, ascent, advance, and height, which includes   
     * the leading.  The logical bounds does not always enclose all the
     * text.  For example, in some languages and in some fonts, accent
     * marks can be positioned above the ascent or below the descent.
     * To obtain a visual bounding box, which encloses all the text,
     * use the {@link TextLayout#getBounds() getBounds} method of 
     * <code>TextLayout</code>.
     * @param str the specified <code>String</code>
     * @param beginIndex the initial offset of <code>str</code>
     * @param limit the end offset of <code>str</code>
     * @param frc the specified <code>FontRenderContext</code>   
     * @return a <code>Rectangle2D</code> that is the bounding box of the
     * specified <code>String</code> in the specified
     * <code>FontRenderContext</code>.
     * @throws IndexOutOfBoundsException if <code>beginIndex</code> is 
     *         less than zero, or <code>limit</code> is greater than the
     *         length of <code>str</code>, or <code>beginIndex</code>
     *         is greater than <code>limit</code>.
     * @see FontRenderContext
     * @see Font#createGlyphVector
     * @since 1.2
     */
    public Rectangle2D getStringBounds( String str,
                                    int beginIndex, int limit,
                                    FontRenderContext frc) {
      String substr = str.substring(beginIndex, limit);
      return getStringBounds(substr, frc);
    }

   /**
     * Returns the logical bounds of the specified array of characters
     * in the specified <code>FontRenderContext</code>.  The logical
     * bounds contains the origin, ascent, advance, and height, which
     * includes the leading.  The logical bounds does not always enclose
     * all the text.  For example, in some languages and in some fonts,
     * accent marks can be positioned above the ascent or below the
     * descent.  To obtain a visual bounding box, which encloses all the
     * text, use the {@link TextLayout#getBounds() getBounds} method of 
     * <code>TextLayout</code>.
     * @param chars an array of characters
     * @param beginIndex the initial offset in the array of
     * characters
     * @param limit the end offset in the array of characters
     * @param frc the specified <code>FontRenderContext</code>   
     * @return a <code>Rectangle2D</code> that is the bounding box of the
     * specified array of characters in the specified
     * <code>FontRenderContext</code>.
     * @throws IndexOutOfBoundsException if <code>beginIndex</code> is 
     *         less than zero, or <code>limit</code> is greater than the
     *         length of <code>chars</code>, or <code>beginIndex</code>
     *         is greater than <code>limit</code>.
     * @see FontRenderContext
     * @see Font#createGlyphVector
     * @since 1.2
     */
    public Rectangle2D getStringBounds(char [] chars,
                                    int beginIndex, int limit,
                                    FontRenderContext frc) {
      if (beginIndex < 0) {
	throw new IndexOutOfBoundsException("beginIndex: " + beginIndex);
      } 
      if (limit > chars.length) {
	throw new IndexOutOfBoundsException("limit: " + limit);
      }
      if (beginIndex > limit) {
	throw new IndexOutOfBoundsException("range length: " + (limit - beginIndex));
      }

      // this code should be in textlayout
      // quick check for simple text, assume GV ok to use if simple

      boolean simple = true;
      for (int i = beginIndex; i < limit; ++i) {
	char c = chars[i];
	if (c >= '\u0590' && c <= '\u206f') {
	  simple = false;
	  break;
	}
      }
      if (simple) {
	GlyphVector gv = new StandardGlyphVector(this, chars, beginIndex, limit - beginIndex, frc);
	return gv.getLogicalBounds();
      } else {
	// need char array constructor on textlayout
	String str = new String(chars, beginIndex, limit - beginIndex);
	TextLayout tl = new TextLayout(str, this, frc);
	return new Rectangle2D.Float(0, -tl.getAscent(), tl.getAdvance(), tl.getDescent() + tl.getLeading());
      }
    }

   /**
     * Returns the logical bounds of the characters indexed in the
     * specified {@link CharacterIterator} in the
     * specified <code>FontRenderContext</code>.  The logical bounds
     * contains the origin, ascent, advance, and height, which includes   
     * the leading.  The logical bounds does not always enclose all the
     * text.  For example, in some languages and in some fonts, accent
     * marks can be positioned above the ascent or below the descent. 
     * To obtain a visual bounding box, which encloses all the text, 
     * use the {@link TextLayout#getBounds() getBounds} method of 
     * <code>TextLayout</code>.
     * @param ci the specified <code>CharacterIterator</code>
     * @param beginIndex the initial offset in <code>ci</code>
     * @param limit the end offset in <code>ci</code>
     * @param frc the specified <code>FontRenderContext</code>   
     * @return a <code>Rectangle2D</code> that is the bounding box of the
     * characters indexed in the specified <code>CharacterIterator</code>
     * in the specified <code>FontRenderContext</code>.
     * @see FontRenderContext
     * @see Font#createGlyphVector
     * @since 1.2
     * @throws IndexOutOfBoundsException if <code>beginIndex</code> is
     *         less than the start index of <code>ci</code>, or 
     *         <code>limit</code> is greater than the end index of 
     *         <code>ci</code>, or <code>beginIndex</code> is greater 
     *         than <code>limit</code>
     */
    public Rectangle2D getStringBounds(CharacterIterator ci,
                                    int beginIndex, int limit,
                                    FontRenderContext frc) {
      int start = ci.getBeginIndex();
      int end = ci.getEndIndex();

      if (beginIndex < start) {
	throw new IndexOutOfBoundsException("beginIndex: " + beginIndex);
      } 
      if (limit > end) {
	throw new IndexOutOfBoundsException("limit: " + limit);
      }
      if (beginIndex > limit) {
	throw new IndexOutOfBoundsException("range length: " + (limit - beginIndex));
      }

      char[]  arr = new char[limit - beginIndex];

      ci.setIndex(beginIndex);
      for(int idx = 0; idx < arr.length; idx++) {
	arr[idx] = ci.current();
	ci.next();
      }

      return getStringBounds(arr,0,arr.length,frc);
    }

    /**
     * Returns the bounds for the character with the maximum
     * bounds as defined in the specified <code>FontRenderContext</code>.
     * @param frc the specified <code>FontRenderContext</code>
     * @return a <code>Rectangle2D</code> that is the bounding box
     * for the character with the maximum bounds.
     */
    public Rectangle2D getMaxCharBounds(FontRenderContext frc) {
        float [] metrics = new float[4]; 

	getFont2D().getFontMetrics(this, frc, metrics);

        return new Rectangle2D.Float(0, -metrics[0],
                                metrics[3],
                                metrics[0] + metrics[1] + metrics[2]);
    }

    /**
     * Creates a {@link java.awt.font.GlyphVector GlyphVector} by 
     * mapping characters to glyphs one-to-one based on the 
     * Unicode cmap in this <code>Font</code>.  This method does no other
     * processing besides the mapping of glyphs to characters.  This
     * means that this method is not useful for some scripts, such
     * as Arabic, Hebrew, Thai, and Indic, that require reordering, 
     * shaping, or ligature substitution.
     * @param frc the specified <code>FontRenderContext</code>
     * @param str the specified <code>String</code>
     * @return a new <code>GlyphVector</code> created with the 
     * specified <code>String</code> and the specified
     * <code>FontRenderContext</code>.
     */
    public GlyphVector createGlyphVector(FontRenderContext frc, String str)
    {
        return (GlyphVector)new StandardGlyphVector(this, str, frc);
    }

    /**
     * Creates a {@link java.awt.font.GlyphVector GlyphVector} by
     * mapping characters to glyphs one-to-one based on the
     * Unicode cmap in this <code>Font</code>.  This method does no other
     * processing besides the mapping of glyphs to characters.  This
     * means that this method is not useful for some scripts, such
     * as Arabic, Hebrew, Thai, and Indic, that require reordering,   
     * shaping, or ligature substitution. 
     * @param frc the specified <code>FontRenderContext</code>
     * @param chars the specified array of characters
     * @return a new <code>GlyphVector</code> created with the
     * specified array of characters and the specified
     * <code>FontRenderContext</code>.
     */
    public GlyphVector createGlyphVector(FontRenderContext frc, char[] chars)
    {
        return (GlyphVector)new StandardGlyphVector(this, chars, frc);
    }

    /**
     * Creates a {@link java.awt.font.GlyphVector GlyphVector} by
     * mapping the specified characters to glyphs one-to-one based on the
     * Unicode cmap in this <code>Font</code>.  This method does no other
     * processing besides the mapping of glyphs to characters.  This
     * means that this method is not useful for some scripts, such
     * as Arabic, Hebrew, Thai, and Indic, that require reordering,   
     * shaping, or ligature substitution. 
     * @param frc the specified <code>FontRenderContext</code>
     * @param ci the specified <code>CharacterIterator</code>
     * @return a new <code>GlyphVector</code> created with the
     * specified <code>CharacterIterator</code> and the specified
     * <code>FontRenderContext</code>.
     */
    public GlyphVector createGlyphVector(   FontRenderContext frc,
                                            CharacterIterator ci)
    {
        return (GlyphVector)new StandardGlyphVector(this, ci, frc);
    }

    /**
     * Creates a {@link java.awt.font.GlyphVector GlyphVector} by
     * mapping characters to glyphs one-to-one based on the
     * Unicode cmap in this <code>Font</code>.  This method does no other
     * processing besides the mapping of glyphs to characters.  This
     * means that this method is not useful for some scripts, such
     * as Arabic, Hebrew, Thai, and Indic, that require reordering,   
     * shaping, or ligature substitution.
     * @param frc the specified <code>FontRenderContext</code>
     * @param glyphCodes the specified integer array
     * @return a new <code>GlyphVector</code> created with the
     * specified integer array and the specified
     * <code>FontRenderContext</code>.
     */
    public GlyphVector createGlyphVector(   FontRenderContext frc,
                                            int [] glyphCodes)
    {
        return (GlyphVector)new StandardGlyphVector(this, glyphCodes, frc);
    }

    /**
     * Returns a new <code>GlyphVector</code> object, performing full
     * layout of the text if possible.  Full layout is required for
     * complex text, such as Arabic or Hindi.  Support for different
     * scripts depends on the font and implementation.  
     * <p
     * Layout requires bidi analysis, as performed by 
     * <code>Bidi</code>, and should only be performed on text that
     * has a uniform direction.  The direction is indicated in the
     * flags parameter,by using LAYOUT_RIGHT_TO_LEFT to indicate a
     * right-to-left (Arabic and Hebrew) run direction, or 
     * LAYOUT_LEFT_TO_RIGHT to indicate a left-to-right (English) 
     * run direction.
     * <p>
     * In addition, some operations, such as Arabic shaping, require 
     * context, so that the characters at the start and limit can have 
     * the proper shapes.  Sometimes the data in the buffer outside
     * the provided range does not have valid data.  The values
     * LAYOUT_NO_START_CONTEXT and LAYOUT_NO_LIMIT_CONTEXT can be
     * added to the flags parameter to indicate that the text before
     * start, or after limit, respectively, should not be examined
     * for context.
     * <p>
     * All other values for the flags parameter are reserved.
     * 
     * @param frc the specified <code>FontRenderContext</code>
     * @param text the text to layout
     * @param start the start of the text to use for the <code>GlyphVector</code>
     * @param limit the limit of the text to use for the <code>GlyphVector</code>
     * @param flags control flags as described above
     * @return a new <code>GlyphVector</code> representing the text between
     * start and limit, with glyphs chosen and positioned so as to best represent 
     * the text
     * @throws ArrayIndexOutOfBoundsException if start or limit is 
     * out of bounds
     * @see java.text.Bidi
     * @see #LAYOUT_LEFT_TO_RIGHT
     * @see #LAYOUT_RIGHT_TO_LEFT
     * @see #LAYOUT_NO_START_CONTEXT
     * @see #LAYOUT_NO_LIMIT_CONTEXT
     */
    public GlyphVector layoutGlyphVector(FontRenderContext frc,
					 char[] text,
					 int start,
					 int limit,
					 int flags) {

      GlyphLayout gl = GlyphLayout.get(null); // !!! no custom layout engines
      StandardGlyphVector gv = gl.layout(this, frc, text,
					 start, limit, flags, null);
      GlyphLayout.done(gl);
      return gv;
    }

    /**
     * A flag to layoutGlyphVector indicating that text is left-to-right as
     * determined by Bidi analysis.
     */
    public static final int LAYOUT_LEFT_TO_RIGHT = 0;

    /** 
     * A flag to layoutGlyphVector indicating that text is right-to-left as
     * determined by Bidi analysis.
     */
    public static final int LAYOUT_RIGHT_TO_LEFT = 1;

    /**
     * A flag to layoutGlyphVector indicating that text in the char array
     * before the indicated start should not be examined.
     */
    public static final int LAYOUT_NO_START_CONTEXT = 2;

    /**
     * A flag to layoutGlyphVector indicating that text in the char array
     * after the indicated limit should not be examined.
     */
    public static final int LAYOUT_NO_LIMIT_CONTEXT = 4;


    private static void applyTransform(AffineTransform trans, Map attributes) {
	if (trans == null) {
	    throw new IllegalArgumentException("transform must not be null");
	}
	if (trans.isIdentity()) {
	    attributes.remove(TextAttribute.TRANSFORM);
	} else {
	    attributes.put(TextAttribute.TRANSFORM, new TransformAttribute(trans));
	}
    }

    private static void applyStyle(int style, Map attributes) {
        if ((style & BOLD) != 0) {
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        } else {
            attributes.remove(TextAttribute.WEIGHT);
        }

        if ((style & ITALIC) != 0) {
            attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        } else {
            attributes.remove(TextAttribute.POSTURE);
        }
    }

    private static void applySize(float size, Map attributes) {
        attributes.put(TextAttribute.SIZE, new Float(size));
    }

    /*
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();
    private native void pDispose();

    /**
     * Disposes the native <code>Font</code> object.
     */
    protected void finalize() throws Throwable {
        if (this.peer != null) {
            pDispose();
        }
        super.finalize();
    }
}
