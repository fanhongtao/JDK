/*
 * @(#)Font.java	1.181 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
import java.text.AttributedCharacterIterator.Attribute;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import sun.awt.font.NativeFontWrapper;
import sun.awt.font.StandardGlyphVector;
import sun.java2d.FontSupport;
import sun.java2d.SunGraphicsEnvironment;

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
 * <a href="../../../guide/intl/faq.html#fonts">Internationalization FAQ</a>
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
 * @version 	1.181, 01/23/03
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
     * Create a Font of type TRUETYPE.
     * In future other types may be added to support other font types.
     */

    public static final int TRUETYPE_FONT = 0;

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
    private transient long pNativeFont; // native Java 2 platform font reference

    /*
     * If the origin of a Font is a created font then this attribute
     * must be set on all derived fonts too.
     */
    private transient boolean createdFont = false;

    // cached values - performance
    private transient int numGlyphs = -1;
    private transient int missingGlyph = -1;
    private transient int canRotate = -1;
    private transient double[] matrix;
    private transient boolean nonIdentityTx;

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

    private void initializeFont(Hashtable attributes) {
        if (this.name == null) {
            this.name = "Default";
        }
        if (attributes == null) {
            fRequestedAttributes = new Hashtable(5, (float)0.9);
            fRequestedAttributes.put(TextAttribute.TRANSFORM, IDENT_TX_ATTRIBUTE);
            fRequestedAttributes.put(TextAttribute.FAMILY, name);
            fRequestedAttributes.put(TextAttribute.SIZE, new Float(size));
	    fRequestedAttributes.put(TextAttribute.WEIGHT, (style & BOLD) != 0 ? 
                TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
	    fRequestedAttributes.put(TextAttribute.POSTURE, (style & ITALIC) != 0 ? 
		TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);

	    // nonIdentityTx = false;
        } else {
            // fRequestedAttributes = ffApply(style, attributes);
	    Object obj = fRequestedAttributes.get(TextAttribute.TRANSFORM);
	    if (obj instanceof TransformAttribute) {
		nonIdentityTx = !((TransformAttribute)obj).isIdentity();
	    } else if (obj instanceof AffineTransform) {
		nonIdentityTx = !((AffineTransform)obj).isIdentity();
	    }
	    // else nonIdentityTx = false;
        }
        GraphicsEnvironment env =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        String localName = this.name;
        if (env instanceof FontSupport && createdFont == false) {
            localName = ((FontSupport)env).mapFontName(
                this.name, this.style);
        }

        NativeFontWrapper.initializeFont(this, localName, style); // sets pNativeFont
        // System.out.println("Initializing font: '" + localName + "'.");
    }

    /**
     * Creates a new <code>Font</code> from the specified name, style and
     * point size.
     * @param name the font name.  This can be a logical font name or a
     * font face name. A logical name must be either: Dialog, DialogInput,
     * Monospaced, Serif, or SansSerif.  If <code>name</code> is
     * <code>null</code>, the <code>name</code> of the new
     * <code>Font</code> is set to the name "Default".
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
	this.name = name;
	this.style = (style & ~0x03) == 0 ? style : 0;
	this.size = size;
        this.pointSize = size;
	initializeFont(null);
    }

    private Font(String name, int style, int size, boolean created) {
        this.createdFont = created;
	this.name = name;
        this.style = (style & ~0x03) == 0 ? style : 0;
        this.size = size;
        this.pointSize = size;
        initializeFont(null);
    }

    private Font(String name, int style, float sizePts, boolean created) {
        this.createdFont = created;
	this.name = name;
	this.style = (style & ~0x03) == 0 ? style : 0;
	this.size = (int)(sizePts + 0.5);
        this.pointSize = sizePts;
	initializeFont(null);
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
    public Font(Map attributes){
	initFromMap(attributes);
    }

    private Font(Map attributes, boolean created) {
        this.createdFont = created;
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

            if ((obj = attributes.get(TextAttribute.WEIGHT)) != null){
                if(obj.equals(TextAttribute.WEIGHT_BOLD)) {
                    this.style |= BOLD;
                }
            }

            if ((obj = attributes.get(TextAttribute.POSTURE)) != null){
                if(obj.equals(TextAttribute.POSTURE_OBLIQUE)) {
                    this.style |= ITALIC;
                }
            }

            if ((obj = attributes.get(TextAttribute.SIZE)) != null){
                this.pointSize = ((Float)obj).floatValue();
                this.size = (int)(this.pointSize + 0.5);
            }
        }
        initializeFont(fRequestedAttributes);
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
    public static Font getFont(Map attributes) {
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
   * Returns a new <code>Font</code> with the specified font type
   * and input data.  The new <code>Font</code> is
   * created with a point size of 1 and style {@link #PLAIN PLAIN}.
   * This base font can then be used with the <code>deriveFont</code>
   * methods in this class to derive new <code>Font</code> objects with
   * varying sizes, styles, transforms and font features.  This
   * method does not close the {@link InputStream}.
   * @param fontFormat the type of the <code>Font</code>, which is
   * {@link #TRUETYPE_FONT TRUETYPE_FONT} if a TrueType is desired.  Other
   * types might be provided in the future.
   * @param fontStream an <code>InputStream</code> object representing the
   * input data for the font.
   * @return a new <code>Font</code> created with the specified font type.
   * @throws IllegalArgumentException if <code>fontType</code> is not
   *     <code>TRUETYPE_FONT</code>
   * @throws FontFormatException if the <code>fontStream</code> data does
   *     not contain the required Truetype font tables.
   * @throws IOException if the <code>fontStream</code>
   *     cannot be completely read.
   * @since 1.3
   */
  // REMIND - need to update this for serialization
    public static Font createFont ( int fontFormat, InputStream fontStream ) 
      throws java.awt.FontFormatException, java.io.IOException {

      if ( fontFormat != Font.TRUETYPE_FONT ) {
	throw new IllegalArgumentException ( "font format not recognized" );
      }
      final File fontFile = 
	  (File)java.security.AccessController.doPrivileged(
		 new java.security.PrivilegedAction() {
	      public Object run() {
		  File tFile = null;
		  try {		    
		      tFile = File.createTempFile ( "+~JF", ".tmp", null );
		      tFile.deleteOnExit();
		  } catch (IOException ioe) {
		  }
		  return tFile;
	      }
	  });
      BufferedInputStream bufferedStream = null;
      bufferedStream = new BufferedInputStream ( fontStream ); 
      FileOutputStream theOutputStream = 
	  (FileOutputStream)java.security.AccessController.doPrivileged(
		 new java.security.PrivilegedAction() {
	      public Object run() {
		  FileOutputStream fos = null;
		  try {
		      fos = new FileOutputStream(fontFile);
		  } catch (IOException ioe) {
		  }
		  return fos;
	      }
	  });
      int bytesRead = 0;
      byte buf[];
      int bufSize = 8192;  // this seems like a reasonable number
      buf = new byte[bufSize]; 
      while ( bytesRead != -1 ) {
	bytesRead = bufferedStream.read ( buf, 0, bufSize );
	if ( bytesRead != -1 )
	  theOutputStream.write ( buf, 0, bytesRead );
      }
      bufferedStream.close();
      theOutputStream.close();
      String createName = SunGraphicsEnvironment.createFont( fontFile );
      if ( createName == null ) {
	throw new FontFormatException ( "Unable to create font - bad font data" );
      }
      //    System.out.println (" Creating font with the name " + createName );
      Font createFont = new Font ( createName, Font.PLAIN, 1, true);
      return createFont;
    }


    /**
     * Returns a copy of the transform associated with this 
     * <code>Font</code>.
     * @return an {@link AffineTransform} object representing the
     *		transform attribute of this <code>Font</code> object.
     */
    public AffineTransform getTransform() {
        Object obj = fRequestedAttributes.get(TextAttribute.TRANSFORM);

        if (obj != null) {
	    if( obj instanceof TransformAttribute ){
	          return ((TransformAttribute)obj).getTransform();
	    }
	    else {
	      if ( obj instanceof AffineTransform){
	         return new AffineTransform((AffineTransform)obj);
	      }
	    }
	}else{
	     obj = new AffineTransform();
	}
	return (AffineTransform)obj;
    }

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
        if (SunGraphicsEnvironment.isLogicalFont(name)) {
            return name;
        }
	short lcid = NativeFontWrapper.getLCIDFromLocale(l);
        return NativeFontWrapper.getFamilyName(this, lcid);
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
	String psName = NativeFontWrapper.getPostscriptName(this);
	return (psName == null ? getFontName() : psName);
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
	return new String(name);
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
        if (SunGraphicsEnvironment.isLogicalFont(name)) {
            return name + "." + SunGraphicsEnvironment.styleStr(style);
        }
	short lcid = NativeFontWrapper.getLCIDFromLocale(l);
        return NativeFontWrapper.getFullName(this, lcid);
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
     * @param nm the property name
     * @return a <code>Font</code> object that the property name
     *		describes.
     * @since 1.2
     */
    public static Font getFont(String nm) {
	return getFont(nm, null);
    }

    /**
     * Returns the <code>Font</code> that the <code>str</code> 
     * argument describes.
     * To ensure that this method returns the desired Font, 
     * format the <code>str</code> parameter in
     * one of two ways:
     * <p>
     * "fontfamilyname-style-pointsize" or <br>
     * "fontfamilyname style pointsize"<p>
     * in which <i>style</i> is one of the three 
     * case-insensitive strings:
     * <code>"BOLD"</code>, <code>"BOLDITALIC"</code>, or
     * <code>"ITALIC"</code>, and pointsize is a decimal
     * representation of the point size.
     * For example, if you want a font that is Arial, bold, and
     * a point size of 18, you would call this method with:
     * "Arial-BOLD-18".
     * <p>
     * The default size is 12 and the default style is PLAIN.
     * If you don't specify a valid size, the returned 
     * <code>Font</code> has a size of 12.  If you don't specify 
     * a valid style, the returned Font has a style of PLAIN.
     * If you do not provide a valid font family name in 
     * the <code>str</code> argument, this method still returns 
     * a valid font with a family name of "dialog".
     * To determine what font family names are available on
     * your system, use the
     * {@link GraphicsEnvironment#getAvailableFontFamilyNames()} method.
     * If <code>str</code> is <code>null</code>, a new <code>Font</code>
     * is returned with the family name "dialog", a size of 12 and a 
     * PLAIN style.
       If <code>str</code> is <code>null</code>, 
     * a new <code>Font</code> is returned with the name "dialog", a  
     * size of 12 and a PLAIN style.
     * @param str the name of the font, or <code>null</code>
     * @return the <code>Font</code> object that <code>str</code>
     *		describes, or a new default <code>Font</code> if 
     *          <code>str</code> is <code>null</code>.
     * @see #getFamily
     * @since JDK1.1
     */
    public static Font decode(String str) {
	String fontName = str;
	String fontSizeStr;
	int index;
	int fontSize = 12;
	int fontStyle = Font.PLAIN;

        if (str == null) {
            return new Font("dialog", fontStyle, fontSize);
        }

	int i = str.indexOf('-');
	if (i >= 0) {
	    fontName = str.substring(0, i);
	    str = str.substring(i+1);
	    str = str.toLowerCase(); // name may be in upper/lower causing incorrect style matching
	    
	    index = str.indexOf ( "bold-italic" );
	    if ( index != -1 ) {
	      fontStyle = Font.BOLD | Font.ITALIC;
	    }
	    if ( index == -1 ) {
	      index = str.indexOf ( "bolditalic" );
              if (index != -1) {
                  fontStyle = Font.BOLD | Font.ITALIC;
              }
	    }
	    if ( index == -1 ) {
	      index = str.indexOf ( "bold" );
	      if ( index != -1 ) {
		fontStyle = Font.BOLD;
              }
	    }
	    if ( index == -1 ) {
	      index = str.indexOf ( "italic" );
              if (index != -1) {
                  fontStyle = Font.ITALIC;
              }
	    }
	    index = str.lastIndexOf ( "-" );
	    if ( index != -1 ) {
	      str = str.substring(index+1);
	    }
	    try {
		fontSize = Integer.valueOf(str).intValue();
	    } catch (NumberFormatException e) {
	    }
	} else if ( i == -1 ) {

	  fontStyle = Font.PLAIN;
	  fontSize = 12;

	  str = str.toLowerCase();
	  index = str.indexOf ( "bolditalic" );
	  if (index != -1 ) {
	    fontStyle = Font.BOLD | Font.ITALIC;
	  }
	  if ( index == -1 ) {
	    index = str.indexOf ( "bold italic" );
	    if ( index != -1 ) {
	      fontStyle = Font.BOLD | Font.ITALIC;
	    }
	  }
	  if ( index == -1 ) {
	    index = str.indexOf ("bold");
   
	    if ( index != -1 ) {
	      fontStyle = Font.BOLD;	
	    }
	  }
	  if ( index == -1 ) {
	    index = str.indexOf  ("italic");
	    if ( index != -1 )
	      fontStyle = Font.ITALIC;
	  }

	  if ( index != -1 ) {  // found a style
	    fontName = fontName.substring(0, index );
	    fontName = fontName.trim();
	  }
	  index = str.lastIndexOf (" " );
	  if ( index != -1 ) {
	     fontSizeStr = str.substring ( index );
	     fontSizeStr = fontSizeStr.trim();
	     try {
		fontSize = Integer.valueOf(fontSizeStr).intValue();
	     } catch (NumberFormatException e) {
	     }
	  }
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
     * The property value should be one of the following forms: 
     * <ul>
     * <li><em>fontname-style-pointsize</em>
     * <li><em>fontname-pointsize</em>
     * <li><em>fontname-style</em>
     * <li><em>fontname</em>
     * </ul>
     * where <i>style</i> is one of the three case-insensitive strings 
     * <code>"BOLD"</code>, <code>"BOLDITALIC"</code>, or 
     * <code>"ITALIC"</code>, and point size is a decimal 
     * representation of the point size. 
     * <p>
     * The default style is <code>PLAIN</code>. The default point size 
     * is 12. 
     * <p>
     * If the specified property is not found, the <code>font</code> 
     * argument is returned instead. 
     * @param nm the case-insensitive property name
     * @param font a default <code>Font</code> to return if property
     * 		<code>nm</code> is not defined
     * @return    the <code>Font</code> value of the property.
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
	    
	    double[] thismat = this.getMatrix();
	    double[] thatmat = font.getMatrix();
	    
	    return  (size == font.size)
	      && (pointSize == font.pointSize)
	      && (style == font.style)
	      && name.equals(font.name)
	      && thismat[0] == thatmat[0]
	      && thismat[1] == thatmat[1]
	      && thismat[2] == thatmat[2]
	      && thismat[3] == thatmat[3];
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
        if (numGlyphs == -1) {
            numGlyphs = NativeFontWrapper.getNumGlyphs(this);
        }
        return numGlyphs;
    }

    /**
     * Returns the glyphCode which is used when this <code>Font</code> 
     * does not have a glyph for a specified unicode.
     * @return the glyphCode of this <code>Font</code>.
     * @since 1.2
     */
    public int getMissingGlyphCode() {
        if (missingGlyph == -1) {
            missingGlyph = NativeFontWrapper.getMissingGlyphCode(this);
        }
        return missingGlyph;
    }

    /**
     * get the transform matrix for this font.
     */
    private static double cachedMat[];
    private double[] getMatrix() {
        if (matrix == null) {
	    double ptSize = this.getSize2D();
	    if (nonIdentityTx) {
		AffineTransform tx = getTransform();
		tx.scale(ptSize, ptSize);
		matrix = new double[] { tx.getScaleX(), tx.getShearY(),
					tx.getShearX(), tx.getScaleY() };
	    } else {
		synchronized (Font.class) {
		    double[] m = cachedMat;
		    if (m == null || m[0] != ptSize) {
			cachedMat = m = new double[] {ptSize, 0, 0, ptSize};
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
        return NativeFontWrapper.getBaselineFor(this, c);
    }

    /**
     * Returns a map of font attributes available in this
     * <code>Font</code>.  Attributes include things like ligatures and
     * glyph substitution.
     * @return the attributes map of this <code>Font</code>.
     */
    public Map getAttributes(){
        return (Map)fRequestedAttributes.clone();
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
	    TextAttribute.TRANSFORM
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
	Hashtable newAttributes = (Hashtable)fRequestedAttributes.clone();
	applyStyle(style, newAttributes);
	applySize(size, newAttributes);
        return new Font(newAttributes, createdFont);
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
	Hashtable newAttributes = (Hashtable)fRequestedAttributes.clone();
	applyStyle(style, newAttributes);
	applyTransform(trans, newAttributes);
        return new Font(newAttributes, createdFont);
    }

    /**
     * Creates a new <code>Font</code> object by replicating the current
     * <code>Font</code> object and applying a new size to it.
     * @param size the size for the new <code>Font</code>.
     * @return a new <code>Font</code> object.
     * @since 1.2
     */
    public Font deriveFont(float size){
	Hashtable newAttributes = (Hashtable)fRequestedAttributes.clone();
	applySize(size, newAttributes);
        return new Font(newAttributes, createdFont);
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
	Hashtable newAttributes = (Hashtable)fRequestedAttributes.clone();
	applyTransform(trans, newAttributes);
        return new Font(newAttributes, createdFont);
    }

    /**
     * Creates a new <code>Font</code> object by replicating the current
     * <code>Font</code> object and applying a new style to it.
     * @param style the style for the new <code>Font</code>
     * @return a new <code>Font</code> object.
     * @since 1.2
     */
    public Font deriveFont(int style){
	Hashtable newAttributes = (Hashtable)fRequestedAttributes.clone();
	applyStyle(style, newAttributes);
        return new Font(newAttributes, createdFont);
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
    public Font deriveFont(Map attributes) {
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
        return new Font(newAttrs, createdFont);
    }

    /**
     * Checks if this <code>Font</code> has a glyph for the specified
     * character.
     * @param c a unicode character code
     * @return <code>true</code> if this <code>Font</code> can display the
     *		character; <code>false</code> otherwise.
     * @since 1.2
     */
    public boolean canDisplay(char c){
        return NativeFontWrapper.canDisplay(this, c);
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
     * @param text the specified array of characters
     * @param start the specified starting offset into the specified array
     *		of characters
     * @param limit the specified ending offset into the specified
     *		array of characters
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
     * Indicates whether or not this <code>Font</code> can display
     * the specified <code>String</code>.  For strings with Unicode
     * encoding, it is important to know if a particular font can display
     * the string. This method returns an offset
     * into the <code>String</code> <code>str</code> which is the first
     * character this <code>Font</code> cannot display without using the
     * missing glyph code . If this <code>Font</code> can display all
     * characters, <code>-1</code> is returned.
     * @param iter a {@link CharacterIterator} object
     * @param start the specified starting offset into the specified array
     *          of characters
     * @param limit the specified ending offset into the specified
     *          array of characters
     * @return an offset into the <code>String</code> object that can be
     * 		displayed by this <code>Font</code>.
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
    public float getItalicAngle(){
        double matrix[] = getMatrix();
        return NativeFontWrapper.getItalicAngle(this, matrix, false, false);
    }

    /**
    * Metrics from a font for layout of characters along a line
    * and layout of set of lines.
    */
    private final class FontLineMetrics extends LineMetrics {
        // package private fields
        int   numchars;
        float ascent, descent, leading, height;
        int   baselineIndex;
        float [] baselineOffsets;
        float strikethroughOffset, strikethroughThickness;
        float underlineOffset, underlineThickness;

        public final int getNumChars() {
            return numchars;
        }

        public final float getAscent() {
            return ascent;
        }

        public final float getDescent() {
            return descent;
        }

        public final float getLeading() {
            return leading;
        }

        public final float getHeight() {
            return height;
        }

        public final int getBaselineIndex() {
            return baselineIndex;
        }

        public final float[] getBaselineOffsets() {
            return baselineOffsets;
        }

        public final float getStrikethroughOffset() {
            return strikethroughOffset;
        }

        public final float getStrikethroughThickness() {
            return strikethroughThickness;
        }

        public final float getUnderlineOffset() {
            return underlineOffset;
        }

        public final float getUnderlineThickness() {
            return underlineThickness;
        }

        public final boolean equals(Object rhs) {
	  if (rhs != null) {
	    if (this == rhs) {
	      return true;
	    }
	    try {
	       FontLineMetrics rlm = (FontLineMetrics)rhs;

	       // does not include numchars, which should never have been here anyway
	       return ascent == rlm.ascent 
		 && descent == rlm.descent
		 && leading == rlm.leading
		 && baselineIndex == rlm.baselineIndex
		 && baselineOffsets[0] == rlm.baselineOffsets[0]
		 && baselineOffsets[1] == rlm.baselineOffsets[1]
		 && baselineOffsets[2] == rlm.baselineOffsets[2]
		 && strikethroughOffset == rlm.strikethroughOffset
		 && strikethroughThickness == rlm.strikethroughThickness
		 && underlineOffset == rlm.underlineOffset
		 && underlineThickness == rlm.underlineThickness;
	    }
	    catch (ClassCastException e) {
	    }
	  }
	  return false;
	}
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

    private FontLineMetrics defaultLineMetrics(FontRenderContext frc) {
        FontLineMetrics flm = new FontLineMetrics();

        double [] matrix = getMatrix();
        float [] metrics = new float[4];
        NativeFontWrapper.getFontMetrics(this, matrix,
                                        frc.isAntiAliased(),
                                        frc.usesFractionalMetrics(),
                                        metrics);

        flm.ascent      = metrics[0];
        flm.descent     = metrics[1];
        flm.leading     = metrics[2];
        flm.height      = metrics[0] + metrics[1] + metrics[2];
        flm.baselineIndex       = 0;
        flm.baselineOffsets     = new float[3];
        flm.baselineOffsets[0]  = 0;

        flm.strikethroughOffset     = -(flm.ascent / 3.0f);
        flm.strikethroughThickness  = pointSize / 12.0f;

        flm.underlineOffset     = 0f;
        flm.underlineThickness  = pointSize / 12.0f;
        return flm;
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
        double [] matrix = getMatrix();
        float [] metrics = new float[4];
        NativeFontWrapper.getFontMetrics(this, matrix,
                                        frc.isAntiAliased(),
                                        frc.usesFractionalMetrics(),
                                        metrics);
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
      return new StandardGlyphVector(this, text, start, limit-start, flags, frc);
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
