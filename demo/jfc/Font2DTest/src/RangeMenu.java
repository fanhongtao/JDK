/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/*
 * @(#)RangeMenu.java	1.13 03/01/23
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

/**
 * RangeMenu.java
 *
 * @version @(#)RangeMenu.java	1.1 00/08/22
 * @author Shinsuke Fukuda
 * @author Ankit Patel [Conversion to Swing - 01/07/30]  
 */

/// Custom made choice menu that holds data for unicode range

public final class RangeMenu extends JComboBox implements ActionListener {

    /// Based on Unicode 3.0

    private final int[][] UNICODE_RANGES = {
        { 0x0000, 0x007f }, /// Basic Latin
        { 0x0080, 0x00ff }, /// Latin-1 Supplement
        { 0x0100, 0x017f }, /// Latin Extended-A
        { 0x0180, 0x024f }, /// Latin Extended-B
        { 0x0250, 0x02af }, /// IPA Extensions
        { 0x02b0, 0x02ff }, /// Spacing Modifier Letters
        { 0x0300, 0x036f }, /// Combining Diacritical Marks
        { 0x0370, 0x03ff }, /// Greek
        { 0x0400, 0x04ff }, /// Cyrillic
        { 0x0530, 0x058f }, /// Armenian
        { 0x0590, 0x05ff }, /// Hebrew
        { 0x0600, 0x06ff }, /// Arabic
        { 0x0700, 0x074f }, /// Syriac
        { 0x0780, 0x07bf }, /// Thaana
        { 0x0900, 0x097f }, /// Devanagari
        { 0x0980, 0x09ff }, /// Bengali
        { 0x0a00, 0x0a7f }, /// Gurmukhi
        { 0x0a80, 0x0aff }, /// Gujarati
        { 0x0b00, 0x0b7f }, /// Oriya
        { 0x0b80, 0x0bff }, /// Tamil
        { 0x0c00, 0x0c7f }, /// Telugu
        { 0x0c80, 0x0cff }, /// Kannada
        { 0x0d00, 0x0d7f }, /// Malayalam
        { 0x0d80, 0x0dff }, /// Sinhala
        { 0x0e00, 0x0e7f }, /// Thai
        { 0x0e80, 0x0eff }, /// Lao
        { 0x0f00, 0x0fbf }, /// Tibetan
        { 0x1000, 0x109f }, /// Myanmar
        { 0x10a0, 0x10ff }, /// Georgian
        { 0x1100, 0x11ff }, /// Hangul Jamo
        { 0x1200, 0x137f }, /// Ethiopic
        { 0x13a0, 0x13ff }, /// Cherokee
        { 0x1400, 0x167f }, /// Canadian Aboriginal Syllabics
        { 0x1680, 0x169f }, /// Ogham
        { 0x16a0, 0x16f0 }, /// Runic
        { 0x1780, 0x17ff }, /// Khmer
        { 0x1800, 0x18af }, /// Mongolian
        { 0x1e00, 0x1eff }, /// Latin Extended Additional
        { 0x1f00, 0x1fff }, /// Greek Extended
        { 0x2000, 0x206f }, /// General Punctuation
        { 0x2070, 0x209f }, /// Superscripts and Supscripts
        { 0x20a0, 0x20cf }, /// Currency Symbols
        { 0x20d0, 0x20ff }, /// Combining Marks for Symbols
        { 0x2100, 0x214f }, /// Letterlike Symbols
        { 0x2150, 0x218f }, /// Number Forms
        { 0x2190, 0x21ff }, /// Arrows
        { 0x2200, 0x22ff }, /// Mathematical Operators
        { 0x2300, 0x23ff }, /// Miscellaneous Technical
        { 0x2400, 0x243f }, /// Control Pictures
        { 0x2440, 0x245f }, /// Optical Character Recognition
        { 0x2460, 0x24ff }, /// Enclosed Alphanumerics
        { 0x2500, 0x257f }, /// Box Drawing
        { 0x2580, 0x259f }, /// Block Elements
        { 0x25a0, 0x25ff }, /// Geometric Shapes
        { 0x2600, 0x26ff }, /// Miscellaneous Symbols
        { 0x2700, 0x27bf }, /// Dingbats
        { 0x2800, 0x28ff }, /// Braille
        { 0x2e80, 0x2fdf }, /// CJK and KangXi Radicals
        { 0x2ff0, 0x2fff }, /// Ideographic Description
        { 0x3000, 0x303f }, /// CJK Symbols and Punctuation
        { 0x3040, 0x309f }, /// Hiragana
        { 0x30a0, 0x30ff }, /// Katakana
        { 0x3100, 0x312f }, /// Bopomofo
        { 0x3130, 0x318f }, /// Hangul Compatibility Jamo
        { 0x3190, 0x319f }, /// Kanbun
        { 0x31a0, 0x31bf }, /// Bopomofo Extended
        { 0x3200, 0x32ff }, /// Enclosed CJK Letters and Months
        { 0x3300, 0x33ff }, /// CJK Compatibility
        { 0x3400, 0x4dbf }, /// CJK Unified Ideographs Extension A
        { 0x4e00, 0x9faf }, /// CJK Unified Ideographs
        { 0xa000, 0xa4cf }, /// Yi
        { 0xac00, 0xd7af }, /// Hangul Syllables
        { 0xe000, 0xf8ff }, /// Private Use Area
        { 0xf900, 0xfaff }, /// CJK Compatibility Ideographs
        { 0xfb00, 0xfb4f }, /// Alphabetic Presentation Forms
        { 0xfb50, 0xfdff }, /// Arabic Presentation Forms-A
        { 0xfe20, 0xfe2f }, /// Combining Half-Marks
        { 0xfe30, 0xfe4f }, /// CJK Compatibility Forms
        { 0xfe50, 0xfe6f }, /// Small Form Variants
        { 0xfe70, 0xfeff }, /// Arabic Presentation Forms-B
        { 0xff00, 0xffef }, /// Halfwidth and Fullwidth Forms
        { 0xfff0, 0xffff }, /// Specials
    };

    private final String[] UNICODE_RANGE_NAMES = {
        "Basic Latin", "Latin-1 Supplement", "Latin Extended-A", "Latin Extended-B",
        "IPA Extensions", "Spacing Modifier Letters", "Combining Diacritical Marks",
        "Greek", "Cyrillic", "Armenian", "Hebrew", "Arabic", "Syriac", "Thaana",
        "Devanagari", "Bengali", "Gurmukhi", "Gujarati", "Oriya", "Tamil", "Telugu",
        "Kannada", "Malayalam", "Sinhala", "Thai", "Lao", "Tibetan", "Myanmar",
        "Georgian", "Hangul Jamo", "Ethiopic", "Cherokee",
        "Canadian Aboriginal Syllabics", "Ogham", "Runic", "Khmer", "Mongolian",
        "Latin Extended Additional", "Greek Extended", "General Punctuation",
        "Superscripts and Supscripts", "Currency Symbols", "Combining Marks for Symbols",
        "Letterlike Symbols", "Number Forms", "Arrows", "Mathematical Operators",
        "Miscellaneous Technical", "Control Pictures", "Optical Character Recognition",
        "Enclosed Alphanumerics", "Box Drawing", "Block Elements", "Geometric Shapes",
        "Miscellaneous Symbols", "Dingbats", "Braille", "CJK and KangXi Radicals",
        "Ideographic Description", "CJK Symbols and Punctuation", "Hiragana", "Katakana",
        "Bopomofo", "Hangul Compatibility Jamo", "Kanbun", "Bopomofo Extended",
        "Enclosed CJK Letters and Months", "CJK Compatibility",
        "CJK Unified Ideographs Extension A", "CJK Unified Ideographs", "Yi",
        "Hangul Syllables", "Private Use Area", "CJK Compatibility Ideographs",
        "Alphabetic Presentation Forms", "Arabic Presentation Forms-A",
        "CJK Compatibility Forms", "Small Form Variants", "Combining Half-Marks",
        "Arabic Presentation Forms-B", "Halfwidth and Fullwidth Forms",
        "Specials", "Other..."
      };

    private boolean useCustomRange = false;
    private int[] customRange = { 0x0000, 0x007f };

    /// Custom range dialog variables
    private final JDialog customRangeDialog;
    private final JTextField customRangeStart = new JTextField( "0000", 4 );
    private final JTextField customRangeEnd   = new JTextField( "007F", 4 );
    private final int CUSTOM_RANGE_INDEX = UNICODE_RANGE_NAMES.length - 1;

    /// Parent Font2DTest Object holder
    private final Font2DTest parent;

    public RangeMenu( Font2DTest demo, JFrame f ) {
        super();
        parent = demo;

        for ( int i = 0; i < UNICODE_RANGE_NAMES.length; i++ )
          addItem( UNICODE_RANGE_NAMES[i] );

        setSelectedIndex( 0 );
        addActionListener( this );

        /// Set up custom range dialog...
        customRangeDialog = new JDialog( f, "Custom Unicode Range", true );
        customRangeDialog.setResizable( false );

        JPanel dialogTop = new JPanel();
        JPanel dialogBottom = new JPanel();
        JButton okButton = new JButton("OK");
        JLabel from = new JLabel( "From" );
        JLabel to = new JLabel("To:");
        Font labelFont = new Font( "dialog", Font.BOLD, 12 );
        from.setFont( labelFont );
        to.setFont( labelFont );
        okButton.setFont( labelFont );

        dialogTop.add( from );
        dialogTop.add( customRangeStart );
        dialogTop.add( to );
        dialogTop.add( customRangeEnd );
        dialogBottom.add( okButton );
        okButton.addActionListener( this );

        customRangeDialog.getContentPane().setLayout( new BorderLayout() );
        customRangeDialog.getContentPane().add( "North", dialogTop );
        customRangeDialog.getContentPane().add( "South", dialogBottom );
        customRangeDialog.pack();
    }

    /// Return the range that is currently selected

    public int[] getSelectedRange() {
        if ( useCustomRange ) {
            int startIndex, endIndex;
            String startText, endText;
            String empty = "";
            try {
                startText = customRangeStart.getText().trim();
                endText = customRangeEnd.getText().trim();
                if ( startText.equals(empty) && !endText.equals(empty) ) {
                    endIndex = Integer.parseInt( endText, 16 );
                    startIndex = endIndex - 7*25;
                }
                else if ( !startText.equals(empty) && endText.equals(empty) ) {
                    startIndex = Integer.parseInt( startText, 16 );
                    endIndex = startIndex + 7*25;                    
                }
                else {
                    startIndex = Integer.parseInt( customRangeStart.getText(), 16 );
                    endIndex = Integer.parseInt( customRangeEnd.getText(), 16 );
                }
            }
            catch ( Exception e ) {
                /// Error in parsing the hex number ---
                /// Reset the range to what it was before and return that
                customRangeStart.setText( Integer.toString( customRange[0], 16 ));
                customRangeEnd.setText( Integer.toString( customRange[1], 16 ));
                return customRange;
            }

            if ( startIndex < 0 )
              startIndex = 0;
            if ( endIndex > 0xffff )
              endIndex = 0xffff;
            if ( startIndex > endIndex )
              startIndex = endIndex;

            customRange[0] = startIndex;
            customRange[1] = endIndex;
            return customRange;
        }
        else
          return UNICODE_RANGES[ getSelectedIndex() ];
    }

    /// Function used by loadOptions in Font2DTest main panel
    /// to reset setting and range selection
    public void setSelectedRange( String name, int start, int end ) {
        setSelectedItem( name );
        customRange[0] = start;
        customRange[1] = end;
        parent.fireRangeChanged();
    }

    /// ActionListener interface function
    /// ABP
    /// moved JComboBox event code into this fcn from
    /// itemStateChanged() method. Part of change to Swing.
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();
        
        if ( source instanceof JComboBox ) {
	        String rangeName = (String)((JComboBox)source).getSelectedItem();

	        if ( rangeName.equals("Other...") ) {
            	    useCustomRange = true;
        	    customRangeDialog.show();
	        }
        	else {
	          useCustomRange = false;
        	}
	        parent.fireRangeChanged();
	}
	else if ( source instanceof JButton ) {
	        /// Since it is only "OK" button that sends any action here...
        	customRangeDialog.hide();
        }
    }
}
