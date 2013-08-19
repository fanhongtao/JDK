/*
 * @(#)TextAttribute.java	1.44 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996 - 1997, All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998, All Rights Reserved
 *
 * The original version of this source code and documentation is
 * copyrighted and owned by Taligent, Inc., a wholly-owned subsidiary
 * of IBM. These materials are provided under terms of a License
 * Agreement between Taligent and Sun. This technology is protected
 * by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.awt.font;

import java.io.InvalidObjectException;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;
import java.util.HashMap;

/**
 * The <code>TextAttribute</code> class defines attribute keys and
 * attribute values used for text rendering. 
 * <p>
 * <code>TextAttribute</code> instances are used as attribute keys to
 * identify attributes in 
 * {@link java.text.AttributedCharacterIterator AttributedCharacterIterator}, 
 * {@link java.awt.Font Font}, and other classes handling text 
 * attributes. Other constants defined in this class are used 
 * as attribute values.
 * <p>
 * For each text attribute, the documentation describes:
 * <UL>
 *   <LI>the type of their values,
 *   <LI>the valid values if there are limitations
 *   <LI>relevant constants
 *   <LI>the default effect if the attribute is absent (or has a
 *	<code>null</code> value).
 *   <LI>a description of the effect.
 *   <LI>the fallback behavior if the exact attribute requested is not 
 *	available.
 * </UL>
 * <p>
 * <H4>Types of Values</H4>
 * <UL>
 *   <LI>The values of attributes must always be immutable.
 *   <LI>Where a list of limitations is given, any value outside of that
 *   set is reserved for future use, and ignored at present.
 *   <LI>If the value is <code>null</code> or not of the proper type
 *	then it has the default effect. The effect of a particular value
 *	can be interpolated, especially in the case of multiple master
 *	fonts. This interpolation is done based on the nearest defined
 * 	constants above and below the request:<BR>
 *      <BLOCKQUOTE><TT>
 *	interpolation = (request - below)/(above - below);
 *	</TT></BLOCKQUOTE>
 * </UL>
 * <p>
 * <H4>Interpolation</H4>
 * <UL>
 *   <LI>Fonts should interpolate values in certain circumstances. For example,
 *   when the WEIGHT value is 2.13. If the nearest surrounding values
 *   in the font are WEIGHT_BOLD = 2.0 and WEIGHT_HEAVY = 2.25 then font would
 *   then interpret the WEIGHT request as being 52% of the way between what
 *   it considers BOLD and what it considers HEAVY. If the nearest surrounding
 *   values are WEIGHT_SEMIBOLD = 1.25 and WEIGHT_ULTRABOLD = 2.75 then the
 *   WEIGHT request is interpreted as being 58.67% of the way between SEMIBOLD
 *   and ULTRABOLD.
 *   <LI>Where a font does not have enough capability to handle a given 
 *   request, such as superscript, then it should simulate it to the best of 
 *   its ability.  To determine if simulation is being performed, the client
 *   should query the font to see what actual attributes were used.
 * </UL>
 * 
 * @see java.text.AttributedCharacterIterator
 * @see java.awt.Font 
 */
public final class TextAttribute extends Attribute {

    // table of all instances in this class, used by readResolve
    private static final Map instanceMap = new HashMap(29);

    /**
     * Constructs a <code>TextAttribute</code> with the specified name.
     * @param name the attribute name to assign to this 
     * <code>TextAttribute</code>
     */
    protected TextAttribute(String name) {
        super(name);
        if (this.getClass() == TextAttribute.class) {
            instanceMap.put(name, this);
        }
    }

    /**
     * Resolves instances being deserialized to the predefined constants.
     */
    protected Object readResolve() throws InvalidObjectException {
        if (this.getClass() != TextAttribute.class) {
            throw new InvalidObjectException("subclass didn't correctly implement readResolve");
        }
        
        TextAttribute instance = (TextAttribute) instanceMap.get(getName());
        if (instance != null) {
            return instance;
        } else {
            throw new InvalidObjectException("unknown attribute name");
        }
    }
    
    // Serialization compatibility with Java 2 platform v1.2.
    // 1.2 will throw an InvalidObjectException if ever asked to deserialize INPUT_METHOD_UNDERLINE.
    // This shouldn't happen in real life.
    static final long serialVersionUID = 7744112784117861702L;

    // 
    // For use with Font.
    //

    /**
     * Attribute key for the unlocalized font family name.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1" 
     *     SUMMARY="Key, Value, Constants, Default, and Description 
     *     for TextAttribute FAMILY">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">FAMILY</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">String</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Constants</TH>
     * <TD VALIGN="TOP">&quot;Serif&quot;, &quot;SansSerif&quot;</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">Host default;</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">The name of the font family. If the family name is not 
     * found, the default font is used. The name should not be the full
     * font name or specify other attributes (such as the name
     * &quot;Helvetica Bold&quot;). Such names might result in the default
     * font if the name does not match a known
     * family name.</TD></TR>
     * </TABLE>
     */
    public static final TextAttribute FAMILY = new TextAttribute("family");

    /**
     * Attribute key for the weight of a font.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Constants, Description, Default, 
     *     and Fallback for TextAttribute WEIGHT">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">WEIGHT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Float</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Constants</TH>
     * <TD VALIGN="TOP">
     * WEIGHT_EXTRA_LIGHT = 0.5,<BR>
     * WEIGHT_LIGHT = 0.75,<BR>
     * WEIGHT_DEMILIGHT = 0.875,<BR>
     * WEIGHT_REGULAR = 1.0,<BR>
     * WEIGHT_SEMIBOLD = 1.25,<BR>
     * WEIGHT_MEDIUM = 1.5,<BR>
     * WEIGHT_DEMIBOLD = 1.75,<BR>
     * WEIGHT_BOLD = 2.0,<BR>
     * WEIGHT_HEAVY = 2.25,<BR>
     * WEIGHT_EXTRABOLD = 2.5,<BR>
     * WEIGHT_ULTRABOLD = 2.75</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">WEIGHT_REGULAR</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">The value is roughly the ratio of the stem width to 
     * that of the regular weight. If the font has a different value for
     * specific constants, then the value is interpolated as described in
     * the class description.</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Fallback</TH>
     * <TD VALIGN="TOP">Currently none. However, in the future, shape 
     * manipulations might be<BR> available to simulate weight variations
     * for fonts that don't have them.</TD></TR>
     * </TABLE>
     * <BR>
     */
    public static final TextAttribute WEIGHT = new TextAttribute("weight");

    /**
     * The lightest predefined weight.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_EXTRA_LIGHT = new Float(0.5f);

    /**
     * The standard light weight.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_LIGHT = new Float(0.75f);

    /**
     * An intermediate weight between LIGHT and STANDARD.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_DEMILIGHT = new Float(0.875f);

    /**
     * The standard weight. This weight is used if WEIGHT is unspecified.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_REGULAR = new Float(1.0f);

    /**
     * A moderately heavier weight than REGULAR.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_SEMIBOLD = new Float(1.25f);

    /**
     * An intermediate weight between the REGULAR and BOLD weights.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_MEDIUM = new Float(1.5f);

    /**
     * A moderately lighter weight than BOLD.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_DEMIBOLD = new Float(1.75f);

    /**
     * The standard bold weight.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_BOLD = new Float(2.0f);

    /**
     * A moderately heavier weight than BOLD.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_HEAVY = new Float(2.25f);

    /**
     * An extra heavy weight.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_EXTRABOLD = new Float(2.5f);

    /**
     * The heaviest predefined weight.
     * @see #WEIGHT
     */
    public static final Float WEIGHT_ULTRABOLD = new Float(2.75f);

    /**
     * Attribute key for the width of a font.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Constants, Description, Default, 
     *     and Fallback for TextAttribute WIDTH">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">WIDTH</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Float</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Constants</TH>
     * <TD VALIGN="TOP">WIDTH_CONDENSED = 0.75,<BR>
     * WIDTH_SEMI_CONDENSED = 0.875,<BR>
     * WIDTH_REGULAR = 1.0,<BR>
     * WIDTH_SEMI_EXTENDED = 1.25,<BR>
     * WIDTH_EXTENDED = 1.5</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">WIDTH_REGULAR</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">The value is roughly the ratio of the advance width 
     * to that of the regular width. If the font has a different value for
     * specific constants, then the value is interpolated as described in
     * the class description.</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Fallback</TH>
     * <TD VALIGN="TOP">If a Narrow font is available and matches, use that. 
     * Otherwise scale with a transform based on the value.</TD></TR>
     * </TABLE>
     */
    public static final TextAttribute WIDTH = new TextAttribute("width");

    /**
     * The most condensed predefined width.
     * @see #WIDTH
     */
    public static final Float WIDTH_CONDENSED = new Float(0.75f);

    /**
     * A moderately condensed width.
     * @see #WIDTH
     */
    public static final Float WIDTH_SEMI_CONDENSED = new Float(0.875f);

    /**
     * The standard width. This width is used if WIDTH is unspecified.
     * @see #WIDTH
     */
    public static final Float WIDTH_REGULAR = new Float(1.0f);

    /**
     * A moderately extended width.
     * @see #WIDTH
     */
    public static final Float WIDTH_SEMI_EXTENDED = new Float(1.25f);

    /**
     * The most extended predefined width.
     * @see #WIDTH
     */
    public static final Float WIDTH_EXTENDED = new Float(1.5f);

    /**
     * Attribute key for the posture of a font.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Constants, Default, Description, 
     *     and Fallback for TextAttribute POSTURE">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">POSTURE</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Float</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Constants</TH>
     * <TD VALIGN="TOP">POSTURE_REGULAR = 0, <BR>
     * POSTURE_OBLIQUE = 0.20</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">POSTURE_REGULAR</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">The value is interpreted generally as a skew slope, 
     * positive leans to the right. If the font has a different value for
     * specific constants, then the value is interpolated as described in
     * the class description. With fonts that have italic faces, not only
     * the skew of the character changes, but also the letter shapes
     * might change.<BR>
     * <B>Notes: </B><BR>
     * To set the value by angle, use:<BR>
     * <TT>value = new Float(Math.tan(Math.PI*degrees/180.0)</TT><BR>
     * To determine the angle from the value, use:<BR>
     * <TT>angle = Math.atan(value.floatValue())*180/Math.PI</TT></TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Fallback</TH>
     * <TD VALIGN="TOP">If an Oblique font is available and matches, use that. 
     * Otherwise skew with a transform using the posture value interpreted as 
     * run/rise.</TD></TR>
     * </TABLE>
     *
     * @see java.awt.Font#getItalicAngle()
     */
    public static final TextAttribute POSTURE = new TextAttribute("posture");

    /**
     * The standard posture, upright.
     * @see #POSTURE
     */
    public static final Float POSTURE_REGULAR = new Float(0.0f);

    /**
     * The standard italic posture.
     * @see #POSTURE
     */
    public static final Float POSTURE_OBLIQUE = new Float(0.20f);

    /**
     * Attribute key for the font size.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Default, Description, and Fallback 
     *     for TextAttribute SIZE">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">SIZE</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Float</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">from System Properties</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">Represents point size. Note that the appearance and 
     * metrics of a 12pt font with a 2X transform might be different than
     * that of a 24 point font with no transform.</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Fallback</TH>
     * <TD VALIGN="TOP">Scale to provided size.</TD></TR>
     * </TABLE>
     */
    public static final TextAttribute SIZE = new TextAttribute("size");

    /**
     * Attribute key for the transform of a font.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Default, and Description for 
     *     TextAttribute TRANSFORM">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">TRANSFORM</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">TransformAttribute</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">Identity transform</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP"><P>Used to transform glyphs rendered by this font. The 
     * primary intent is to support scaling, skewing, and translation. In
     * general, large rotations do not produce very useful results. The
     * transform modifies both the glyph and the advance. The translations
     * in the transform are interpreted as a ratio of the point size. That
     * is, with a point size of 12, a translation of 0.5 results in a
     * movement of 6 points.
     * <p>
     * The advance point of the transformed glyph is the transform of the 
     * advance point projected onto the baseline. If the advance ends up
     * to the left (top) of the glyph origin, the two points are swapped.
     * <p>
     * <P><EM>Example one</EM>: The point 
     * size is 20, the original advance is 10.0, and the transform is a 60 
     * degree counterclockwise rotation plus an offset up and to the right 
     * of 0.1, -0.1. The translation results in an offset of &lt;2.0, -2.0&gt;.
     * The original advance point is &lt;10.0, 0.0&gt;; after the rotation it 
     * is &lt;6.0, -8.0&gt;; when adding the offset this becomes 
     * &lt;8.0,-10.0&gt;, when projecting on the (horizontal) baseline this
     * becomes the new advance point: &lt;8.0, 0.0&gt;. The advance width is
     * the distance from the origin to the advance point: 8.0. The rotated
     * glyph is rendered two points up and to the right of its origin and
     * rotated.  This does not affect the baseline for subsequent
     * glyphs.</P></TD></TR>
     * </TABLE>
     */
    public static final TextAttribute TRANSFORM = new TextAttribute("transform");

    /**
     * Attribute key for super and subscripting.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Constants, Default, Description, 
     *     and Fallback for TextAttribute SUPERSCRIPT">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">SUPERSCRIPT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Integer</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Constants</TH>
     * <TD VALIGN="TOP">SUPERSCRIPT_NONE = 0,<BR>
     * SUPERSCRIPT_SUPER = 1,<BR>
     * SUPERSCRIPT_SUB = -1</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">SUPERSCRIPT_NONE</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">Requests that the font display the characters with 
     * glyphs at a particular superscript level: 0 = none, 1 =
     * superscript, 2 = superscript of superscript,...-1
     * = subscript, -2 = subscript of subscript,... Requests that the font 
     * display text using default superscript (or subscript) glyphs and/or
     * scaling.</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Fallback</TH>
     * <TD VALIGN="TOP">Use transform with translation of +/-1/2 and scale 
     * of 2/3, progressively for each level. That is, for the transform at
     * level N (with N != 0):<BR>
     * <TT>offset = sign(N)*1/2*(2/3)^(abs(N)-1)<BR>
     * scale = (2/3)^abs(N)</TT></TD></TR>
     * </TABLE>
     */
    public static final TextAttribute SUPERSCRIPT = new TextAttribute("superscript");

    /**
     * Standard superscript.
     * @see #SUPERSCRIPT
     */
    public static final Integer SUPERSCRIPT_SUPER = new Integer(1);

    /**
     * Standard subscript.
     * @see #SUPERSCRIPT
     */
    public static final Integer SUPERSCRIPT_SUB = new Integer(-1);

    /**
     * Attribute key for the font to use to render text.
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Default, and Description for TextAttribute FONT">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">FONT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Font</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">None, perform default resolution</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">A way for users to override the resolution of font 
     * attributes into a <code>Font</code>, or force use of a particular
     * <code>Font</code> instance.
     * This also allows users to specify subclasses of <code>Font</code> in 
     * cases where a <code>Font</code> can be subclassed.</TD></TR>
     * </TABLE>
     */
    public static final TextAttribute FONT = new TextAttribute("font");

    /**
     * Attribute key for a user_defined glyph to display in the text in lieu 
     * of a character.
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, and Description for TextAttribute CHAR_REPLACEMENT">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">CHAR_REPLACEMENT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">GraphicAttribute</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">Allows the user to specify an empty position plus 
     * metric information. This method is used to reserve space for a graphic
     * or other embedded component. Required for
     * correct BIDI position of 'inline' components within a line. An optional
     * convenience method allows drawing for simple cases. Follows the 
     * Microsoft model: the character that this is applied to should be 
     * \uFFFC.</TD></TR>
     * </TABLE>
     */
    public static final TextAttribute CHAR_REPLACEMENT = new TextAttribute("char_replacement");

    //
    // Adornments added to text.
    //

    /**
     * Attribute key for the foreground paint
     *  adornment.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Default, and Description of TextAttribute FOREGROUND">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">FOREGROUND</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Paint</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">Color.black</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">Specify the foreground Paint (or Color) of the text.</TD></TR>
     * </TABLE>
     */
    public static final TextAttribute FOREGROUND = new TextAttribute("foreground");

    /**
     * Attribute key for the background Paint adornment.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Default, and Description of TextAttribute BACKGROUND">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">BACKGROUND</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Paint</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">null</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">Specify the background Paint (or Color) of the text.</TD></TR>
     * </TABLE>
     */
    public static final TextAttribute BACKGROUND = new TextAttribute("background");

    /**
     * Attribute key for underline adornments.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1" 
     *     SUMMARY="Key, Value, Constants, Default, Description, 
     *     and Fallback for TextAttribute UNDERLINE">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">UNDERLINE</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Integer</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Constants</TH>
     * <TD VALIGN="TOP">UNDERLINE_ON = 0</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">none</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">An embellishment added to the glyphs rendered by a 
     * font.</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Fallback</TH>
     * <TD VALIGN="TOP"></TD></TR>
     * </TABLE>
     */
    public static final TextAttribute UNDERLINE = new TextAttribute("underline");

    /**
     * Standard underline at the roman baseline for roman text, and below
     * the decenders for other text.
     *
     * @see #UNDERLINE
     */
    public static final Integer UNDERLINE_ON = new Integer((byte)0);

    /**
     * Attribute key for the strikethrough adornment.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Constants, Default, and Description
     *     for TextAttribute STRIKETHROUGH">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">STRIKETHROUGH</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Boolean</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Constants</TH>
     * <TD VALIGN="TOP">true = on, false = off</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">off</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">An embellishment added to the glyphs rendered by a 
     * font.</TD></TR>
     * </TABLE>
     */
    public static final TextAttribute STRIKETHROUGH = new TextAttribute("strikethrough");

    /**
     * A single strikethrough.
     *
     * @see #STRIKETHROUGH
     */
    public static final Boolean STRIKETHROUGH_ON = new Boolean(true);

    //
    // Attributes use to control layout of text on a line.
    //

    /**
     * Attribute key for the run direction of the line.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Constants, Default, and Description 
     *     of TextAttribute RUN_DIRECTION">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">RUN_DIRECTION</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Boolean</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Constants</TH>
     * <TD VALIGN="TOP">RUN_DIRECTION_LTR = true, RUN_DIRECTION_RTL = false
     * </TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">Use the default Unicode base direction from the BIDI 
     * algorithm.</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP"><P>Specifies which base run direction to use when 
     * positioning mixed directional runs within a paragraph. If this value is
     * RUN_DIRECTION_DEFAULT, <code>TextLayout</code> uses the default Unicode
     * base direction from the BIDI algorithm.</P>
     * <P><I>This attribute should have the same value over the whole 
     * paragraph.</I></TD></TR>
     * </TABLE>
     */
    public static final TextAttribute RUN_DIRECTION = new TextAttribute("run_direction");

    /**
     * Left-to-right run direction.
     * @see #RUN_DIRECTION
     */
    public static final Boolean RUN_DIRECTION_LTR = new Boolean(false);

    /**
     * Right-to-left run direction.
     * @see #RUN_DIRECTION
     */
    public static final Boolean RUN_DIRECTION_RTL = new Boolean(true);

    /**
     * Attribute key for the embedding level for nested bidirectional runs.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Limits, Default, and Description 
     *     of TextAttribute BIDI_EMBEDDING">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">BIDI_EMBEDDING</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Integer</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Limits</TH>
     * <TD VALIGN="TOP">Positive values 1 through 61 are <I>embedding</I>
     * levels, negative values<BR> through -61 are <I>override</I> levels
     * </TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">Use standard BIDI to compute levels from formatting
     * characters in the text.</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP">Specifies the bidi embedding level of the character.
     * When this attribute is present anywhere in a paragraph, then the 
     * Unicode characters RLO, LRO, RLE, LRE, PDF are disregarded in the BIDI 
     * analysis of that paragraph. 
     * See the Unicode Standard v. 2.0, section 3-11.
     * </TD></TR>
     * </TABLE>
     */
    public static final TextAttribute BIDI_EMBEDDING = new TextAttribute("bidi_embedding");

    /**
     * Attribute key for the justification of a paragraph.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Limits, Default, and Description
     *     of TextAttribute JUSTIFICATION">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">JUSTIFICATION</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Float</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Limits</TH>
     * <TD VALIGN="TOP">0.0 through1.0</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">1.0</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Description</TH>
     * <TD VALIGN="TOP"><P>Specifies which fraction of the extra space to use 
     * when justification is requested. For example, if the line is 50 points
     * wide and the margins are 70 points apart, a value of 0.5 means that the
     * line is padded to reach a width of 60 points.</P>
     * <P><I>This attribute should have the same value over the whole
     * paragraph.</I></TD></TR>
     * </TABLE>
     */
    public static final TextAttribute JUSTIFICATION = new TextAttribute("justification");

    /**
     * Justify the line to the full requested width.
     * @see #JUSTIFICATION
     */
    public static final Float JUSTIFICATION_FULL = new Float(1.0f);

    /**
     * Do not allow the line to be justified.
     * @see #JUSTIFICATION
     */
    public static final Float JUSTIFICATION_NONE = new Float(0.0f);

    //
    // For use by input method.
    //

    /**
     * Attribute key for input method highlight styles.
     * <p>Values are instances of 
     * {@link java.awt.im.InputMethodHighlight InputMethodHighlight}.
     * These instances should be wrapped in 
     * {@link java.text.Annotation Annotation} instances
     * if segments need to be highlighted separately.
     * <p>
     * Input method highlights are used while text is being composed
     * using an input method. Text editing components should retain them
     * even if they generally only deal with unstyled text, and make them
     * available to the drawing routines.
     * @see java.awt.im.InputMethodHighlight
     */
    public static final TextAttribute INPUT_METHOD_HIGHLIGHT = new TextAttribute("input method highlight");

    /**
     * Attribute key for input method underline adornments.
     *
     * <P><TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1"
     *     SUMMARY="Key, Value, Limits, Default and Description
     *     of TextAttribute INPUT_METHOD_UNDERLINE">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Key</TH>
     * <TD VALIGN="TOP">INPUT_METHOD_UNDERLINE</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Value</TH>
     * <TD VALIGN="TOP">Integer</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Constants</TH>
     * <TD VALIGN="TOP">UNDERLINE_LOW_ONE_PIXEL, UNDERLINE_LOW_TWO_PIXEL,
     *     UNDERLINE_LOW_DOTTED, UNDERLINE_LOW_GRAY, UNDERLINE_LOW_DASHED</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN=RIGHT>Default</TH>
     * <TD VALIGN="TOP">no underline</TD></TR>
     * </TABLE>
     * @since 1.3
     */
    public static final TextAttribute INPUT_METHOD_UNDERLINE
                 = new TextAttribute("input method underline");

    /**
     * Single pixel solid low underline.
     * @see #INPUT_METHOD_UNDERLINE
     * @since 1.3
     */
    public static final Integer UNDERLINE_LOW_ONE_PIXEL = new Integer(1);

    /**
     * Double pixel solid low underline.
     * @see #INPUT_METHOD_UNDERLINE
     * @since 1.3
     */
    public static final Integer UNDERLINE_LOW_TWO_PIXEL = new Integer(2);

    /**
     * Single pixel dotted low underline.
     * @see #INPUT_METHOD_UNDERLINE
     * @since 1.3
     */
    public static final Integer UNDERLINE_LOW_DOTTED = new Integer(3);

    /**
     * Double pixel gray low underline.
     * @see #INPUT_METHOD_UNDERLINE
     * @since 1.3
     */
    public static final Integer UNDERLINE_LOW_GRAY = new Integer(4);

    /**
     * Single pixel dashed low underline.
     * @see #INPUT_METHOD_UNDERLINE
     * @since 1.3
     */
    public static final Integer UNDERLINE_LOW_DASHED = new Integer(5);

    /**
     * Attribute key for swapping foreground and background Paints (or Colors).
     *
     * <p>Values are instances of <code>Boolean</code>.
     * The default is not to swap the foreground and background.
     * If the foreground and background attributes are both defined,
     * this causes them to be swapped when rendering text.  If either is
     * defaulted, the exact effect is undefined--generally it will produce
     * an 'inverted' appearance.
     */
    public static final TextAttribute SWAP_COLORS = new TextAttribute("swap_colors");

    /** Swap foreground and background. */
    public static final Boolean SWAP_COLORS_ON = new Boolean(true);

    /**
     * Attribute key for converting ASCII decimal digits to other decimal ranges.
     *
     * <p>Values are instances of <code>NumericShaping</code>.
     * The default is not to perform numeric shaping.
     */
    public static final TextAttribute NUMERIC_SHAPING = new TextAttribute("numeric_shaping");
}
