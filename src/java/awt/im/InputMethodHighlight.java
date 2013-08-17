/*
 * @(#)InputMethodHighlight.java	1.10 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.im;

/**
* A InputMethodHighlight is used to describe in an abstract way the highlight
* attributes of text being composed. A range of text can be selected or
* unselected, and it can be highlighted in different ways to indicate the
* conversion state or other interesting, input method specific information
* about the text. Two states are predefined and supported directly
* by Graphics2D: raw (unconverted) and converted text.
* These styles are recommended for use before and after the
* main conversion step of text composition, say, before and after kana->kanji
* or pinyin->hanzi conversion. However, input methods can add their own style
* variations as necessary.
* 
* InputMethodHighlight instances are typically used as attribute values
* returned from AttributedCharacterIterator for the INPUT_METHOD_HIGHLIGHT
* attribute.
*
* @version 1.10 11/29/01
* @see java.text.AttributedCharacterIterator
*/

public class InputMethodHighlight {

    /**
     * Constant for the raw text state.
     */
    public final static int RAW_TEXT = 0;

    /**
     * Constant for the converted text state.
     */
    public final static int CONVERTED_TEXT = 1;


    /**
     * Constant for the default highlight for unselected raw text.
     */
    public final static InputMethodHighlight UNSELECTED_RAW_TEXT_HIGHLIGHT =
        new InputMethodHighlight(false, RAW_TEXT);

    /**
     * Constant for the default highlight for selected raw text.
     */
    public final static InputMethodHighlight SELECTED_RAW_TEXT_HIGHLIGHT =
        new InputMethodHighlight(true, RAW_TEXT);

    /**
     * Constant for the default highlight for unselected converted text.
     */
    public final static InputMethodHighlight UNSELECTED_CONVERTED_TEXT_HIGHLIGHT =
        new InputMethodHighlight(false, CONVERTED_TEXT);

    /**
     * Constant for the default highlight for selected converted text.
     */
    public final static InputMethodHighlight SELECTED_CONVERTED_TEXT_HIGHLIGHT =
        new InputMethodHighlight(true, CONVERTED_TEXT);


    /**
     * Constructs an input method highlight record.
     * The variation is set to 0.
     * @param selected Whether the text range is selected
     * @param state The conversion state for the text range - RAW_TEXT or CONVERTED_TEXT
     * @see InputMethodHighlight#RAW_TEXT
     * @see InputMethodHighlight#CONVERTED_TEXT
     * @exception IllegalArgumentException if a state other than RAW_TEXT or CONVERTED_TEXT is given
     */
    public InputMethodHighlight(boolean selected, int state) {
        this(selected, state, 0);
    }

    /**
     * Constructs an input method highlight record.
     * @param selected Whether the text range is selected
     * @param state The conversion state for the text range - RAW_TEXT or CONVERTED_TEXT
     * @param variation The style variation for the text range
     * @see InputMethodHighlight#RAW_TEXT
     * @see InputMethodHighlight#CONVERTED_TEXT
     * @exception IllegalArgumentException if a state other than RAW_TEXT or CONVERTED_TEXT is given
     */
    public InputMethodHighlight(boolean selected, int state, int variation) {
        this.selected = selected;
        if (!(state == RAW_TEXT || state == CONVERTED_TEXT)) {
            throw new IllegalArgumentException("unknown input method highlight state");
        }
        this.state = state;
        this.variation = variation;
    }

    /**
     * Returns whether the text range is selected.
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * Returns the conversion state of the text range.
     * @return The conversion state for the text range - RAW_TEXT or CONVERTED_TEXT.
     * @see InputMethodHighlight#RAW_TEXT
     * @see InputMethodHighlight#CONVERTED_TEXT
     */
    public int getState() {
        return state;
    }

    /**
     * Returns the style variation of the text range.
     */
    public int getVariation() {
        return variation;
    }

    private boolean selected;
    private int state;
    private int variation;

};
