/*
 * @(#)BreakIterator.java	1.35 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
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

package java.text;

import java.util.Vector;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import sun.text.resources.LocaleData;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;

import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The <code>BreakIterator</code> class implements methods for finding
 * the location of boundaries in text. Instances of <code>BreakIterator</code>
 * maintain a current position and scan over text
 * returning the index of characters where boundaries occur.
 * Internally, <code>BreakIterator</code> scans text using a
 * <code>CharacterIterator</code>, and is thus able to scan text held
 * by any object implementing that protocol. A <code>StringCharacterIterator</code>
 * is used to scan <code>String</code> objects passed to <code>setText</code>.
 *
 * <p>
 * You use the factory methods provided by this class to create
 * instances of various types of break iterators. In particular,
 * use <code>getWordIterator</code>, <code>getLineIterator</code>,
 * <code>getSentenceIterator</code>, and <code>getCharacterIterator</code>
 * to create <code>BreakIterator</code>s that perform
 * word, line, sentence, and character boundary analysis respectively.
 * A single <code>BreakIterator</code> can work only on one unit
 * (word, line, sentence, and so on). You must use a different iterator
 * for each unit boundary analysis you wish to perform.
 *
 * <p>
 * Line boundary analysis determines where a text string can be
 * broken when line-wrapping. The mechanism correctly handles
 * punctuation and hyphenated words.
 *
 * <p>
 * Sentence boundary analysis allows selection with correct interpretation
 * of periods within numbers and abbreviations, and trailing punctuation
 * marks such as quotation marks and parentheses.
 *
 * <p>
 * Word boundary analysis is used by search and replace functions, as
 * well as within text editing applications that allow the user to
 * select words with a double click. Word selection provides correct
 * interpretation of punctuation marks within and following
 * words. Characters that are not part of a word, such as symbols
 * or punctuation marks, have word-breaks on both sides.
 *
 * <p>
 * Character boundary analysis allows users to interact with characters
 * as they expect to, for example, when moving the cursor through a text
 * string. Character boundary analysis provides correct navigation of
 * through character strings, regardless of how the character is stored.
 * For example, an accented character might be stored as a base character
 * and a diacritical mark. What users consider to be a character can
 * differ between languages.
 *
 * <p>
 * <code>BreakIterator</code> is intended for use with natural
 * languages only. Do not use this class to tokenize a programming language.
 *
 * <P>
 * <strong>Examples</strong>:<P>
 * Creating and using text boundaries
 * <blockquote>
 * <pre>
 * public static void main(String args[]) {
 *      if (args.length == 1) {
 *          String stringToExamine = args[0];
 *          //print each word in order
 *          BreakIterator boundary = BreakIterator.getWordInstance();
 *          boundary.setText(stringToExamine);
 *          printEachForward(boundary, stringToExamine);
 *          //print each sentence in reverse order
 *          boundary = BreakIterator.getSentenceInstance(Locale.US);
 *          boundary.setText(stringToExamine);
 *          printEachBackward(boundary, stringToExamine);
 *          printFirst(boundary, stringToExamine);
 *          printLast(boundary, stringToExamine);
 *      }
 * }
 * </pre>
 * </blockquote>
 *
 * Print each element in order
 * <blockquote>
 * <pre>
 * public static void printEachForward(BreakIterator boundary, String source) {
 *     int start = boundary.first();
 *     for (int end = boundary.next();
 *          end != BreakIterator.DONE;
 *          start = end, end = boundary.next()) {
 *          System.out.println(source.substring(start,end));
 *     }
 * }
 * </pre>
 * </blockquote>
 *
 * Print each element in reverse order
 * <blockquote>
 * <pre>
 * public static void printEachBackward(BreakIterator boundary, String source) {
 *     int end = boundary.last();
 *     for (int start = boundary.previous();
 *          start != BreakIterator.DONE;
 *          end = start, start = boundary.previous()) {
 *         System.out.println(source.substring(start,end));
 *     }
 * }
 * </pre>
 * </blockquote>
 *
 * Print first element
 * <blockquote>
 * <pre>
 * public static void printFirst(BreakIterator boundary, String source) {
 *     int start = boundary.first();
 *     int end = boundary.next();
 *     System.out.println(source.substring(start,end));
 * }
 * </pre>
 * </blockquote>
 *
 * Print last element
 * <blockquote>
 * <pre>
 * public static void printLast(BreakIterator boundary, String source) {
 *     int end = boundary.last();
 *     int start = boundary.previous();
 *     System.out.println(source.substring(start,end));
 * }
 * </pre>
 * </blockquote>
 *
 * Print the element at a specified position
 * <blockquote>
 * <pre>
 * public static void printAt(BreakIterator boundary, int pos, String source) {
 *     int end = boundary.following(pos);
 *     int start = boundary.previous();
 *     System.out.println(source.substring(start,end));
 * }
 * </pre>
 * </blockquote>
 *
 * Find the next word
 * <blockquote>
 * <pre>
 * public static int nextWordStartAfter(int pos, String text) {
 *     BreakIterator wb = BreakIterator.getWordInstance();
 *     wb.setText(text);
 *     int last = wb.following(pos);
 *     int current = wb.next();
 *     while (current != BreakIterator.DONE) {
 *         for (int p = last; p < current; p++) {
 *             if (Character.isLetter(text.codePointAt(p))
 *                 return last;
 *         }
 *         last = current;
 *         current = wb.next();
 *     }
 *     return BreakIterator.DONE;
 * }
 * </pre>
 * (The iterator returned by BreakIterator.getWordInstance() is unique in that
 * the break positions it returns don't represent both the start and end of the
 * thing being iterated over.  That is, a sentence-break iterator returns breaks
 * that each represent the end of one sentence and the beginning of the next.
 * With the word-break iterator, the characters between two boundaries might be a
 * word, or they might be the punctuation or whitespace between two words.  The
 * above code uses a simple heuristic to determine which boundary is the beginning
 * of a word: If the characters between this boundary and the next boundary
 * include at least one letter (this can be an alphabetical letter, a CJK ideograph,
 * a Hangul syllable, a Kana character, etc.), then the text between this boundary
 * and the next is a word; otherwise, it's the material between words.)
 * </blockquote>
 *
 * @see CharacterIterator
 *
 */

public abstract class BreakIterator implements Cloneable
{
    /**
     * Constructor. BreakIterator is stateless and has no default behavior.
     */
    protected BreakIterator()
    {
    }

    /**
     * Create a copy of this iterator
     * @return A copy of this
     */
    public Object clone()
    {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * DONE is returned by previous() and next() after all valid
     * boundaries have been returned.
     */
    public static final int DONE = -1;

    /**
     * Return the first boundary. The iterator's current position is set
     * to the first boundary.
     * @return The character index of the first text boundary.
     */
    public abstract int first();

    /**
     * Return the last boundary. The iterator's current position is set
     * to the last boundary.
     * @return The character index of the last text boundary.
     */
    public abstract int last();

    /**
     * Return the nth boundary from the current boundary
     * @param n which boundary to return.  A value of 0
     * does nothing.  Negative values move to previous boundaries
     * and positive values move to later boundaries.
     * @return The index of the nth boundary from the current position.
     */
    public abstract int next(int n);

    /**
     * Return the boundary following the current boundary.
     * @return The character index of the next text boundary or DONE if all
     * boundaries have been returned.  Equivalent to next(1).
     */
    public abstract int next();

    /**
     * Return the boundary preceding the current boundary.
     * @return The character index of the previous text boundary or DONE if all
     * boundaries have been returned.
     */
    public abstract int previous();

    /**
     * Return the first boundary following the specified offset.
     * The value returned is always greater than the offset or
     * the value BreakIterator.DONE
     * @param offset the offset to begin scanning. Valid values
     * are determined by the CharacterIterator passed to
     * setText().  Invalid values cause
     * an IllegalArgumentException to be thrown.
     * @return The first boundary after the specified offset.
     */
    public abstract int following(int offset);

    /**
     * Return the last boundary preceding the specfied offset.
     * The value returned is always less than the offset or the value
     * BreakIterator.DONE.
     * @param offset the offset to begin scanning.  Valid values are
     * determined by the CharacterIterator passed to setText().
     * Invalid values cause an IllegalArgumentException to be thrown.
     * @return The last boundary before the specified offset.
     * @since 1.2
     */
    public int preceding(int offset) {
        // NOTE:  This implementation is here solely because we can't add new
        // abstract methods to an existing class.  There is almost ALWAYS a
        // better, faster way to do this.
        int pos = following(offset);
        while (pos >= offset && pos != DONE)
            pos = previous();
        return pos;
    }

    /**
     * Return true if the specified position is a boundary position.
     * @param offset the offset to check.
     * @return True if "offset" is a boundary position.
     * @since 1.2
     */
    public boolean isBoundary(int offset) {
        // NOTE: This implementation probably is wrong for most situations
        // because it fails to take into account the possibility that a
        // CharacterIterator passed to setText() may not have a begin offset
        // of 0.  But since the abstract BreakIterator doesn't have that
        // knowledge, it assumes the begin offset is 0.  If you subclass
        // BreakIterator, copy the SimpleTextBoundary implementation of this
        // function into your subclass.  [This should have been abstract at
        // this level, but it's too late to fix that now.]
        if (offset == 0)
            return true;
        else
            return following(offset - 1) == offset;
    }

    /**
     * Return character index of the text boundary that was most recently
     * returned by next(), previous(), first(), or last()
     * @return The boundary most recently returned.
     */
    public abstract int current();

    /**
     * Get the text being scanned
     * @return the text being scanned
     */
    public abstract CharacterIterator getText();

    /**
     * Set a new text string to be scanned.  The current scan
     * position is reset to first().
     * @param newText new text to scan.
     */
    public void setText(String newText)
    {
        setText(new StringCharacterIterator(newText));
    }

    /**
     * Set a new text for scanning.  The current scan
     * position is reset to first().
     * @param newText new text to scan.
     */
    public abstract void setText(CharacterIterator newText);

    private static final int CHARACTER_INDEX = 0;
    private static final int WORD_INDEX = 1;
    private static final int LINE_INDEX = 2;
    private static final int SENTENCE_INDEX = 3;
    private static final SoftReference[] iterCache = new SoftReference[4];

    /**
     * Create BreakIterator for word-breaks using default locale.
     * Returns an instance of a BreakIterator implementing word breaks.
     * WordBreak  is usefull for word selection (ex. double click)
     * @return A BreakIterator for word-breaks
     * @see java.util.Locale#getDefault
     */
    public static BreakIterator getWordInstance()
    {
        return getWordInstance(Locale.getDefault());
    }

    /**
     * Create BreakIterator for word-breaks using specified locale.
     * Returns an instance of a BreakIterator implementing word breaks.
     * WordBreak is usefull for word selection (ex. double click)
     * @param where the local.  If a specific WordBreak is not
     * avaliable for the specified locale, a default WordBreak is returned.
     * @return A BreakIterator for word-breaks
     */
    public static BreakIterator getWordInstance(Locale where)
    {
        return getBreakInstance(where,
                                WORD_INDEX,
                                "WordData",
                                "WordDictionary");
    }

    /**
     * Create BreakIterator for line-breaks using default locale.
     * Returns an instance of a BreakIterator implementing line breaks. Line
     * breaks are logically possible line breaks, actual line breaks are
     * usually determined based on display width.
     * LineBreak is useful for word wrapping text.
     * @return A BreakIterator for line-breaks
     * @see java.util.Locale#getDefault
     */
    public static BreakIterator getLineInstance()
    {
        return getLineInstance(Locale.getDefault());
    }

    /**
     * Create BreakIterator for line-breaks using specified locale.
     * Returns an instance of a BreakIterator implementing line breaks. Line
     * breaks are logically possible line breaks, actual line breaks are
     * usually determined based on display width.
     * LineBreak is useful for word wrapping text.
     * @param where the local.  If a specific LineBreak is not
     * avaliable for the specified locale, a default LineBreak is returned.
     * @return A BreakIterator for line-breaks
     */
    public static BreakIterator getLineInstance(Locale where)
    {
        return getBreakInstance(where,
                                LINE_INDEX,
                                "LineData",
                                "LineDictionary");
    }

    /**
     * Create BreakIterator for character-breaks using default locale
     * Returns an instance of a BreakIterator implementing character breaks.
     * Character breaks are boundaries of combining character sequences.
     * @return A BreakIterator for character-breaks
     * @see Locale#getDefault
     */
    public static BreakIterator getCharacterInstance()
    {
        return getCharacterInstance(Locale.getDefault());
    }

    /**
     * Create BreakIterator for character-breaks using specified locale
     * Returns an instance of a BreakIterator implementing character breaks.
     * Character breaks are boundaries of combining character sequences.
     * @param where the local.  If a specific character break is not
     * avaliable for the specified local, a default character break is returned.
     * @return A BreakIterator for character-breaks
     */
    public static BreakIterator getCharacterInstance(Locale where)
    {
        return getBreakInstance(where,
                                CHARACTER_INDEX,
                                "CharacterData",
                                "CharacterDictionary");
    }

    /**
     * Create BreakIterator for sentence-breaks using default locale
     * Returns an instance of a BreakIterator implementing sentence breaks.
     * @return A BreakIterator for sentence-breaks
     * @see java.util.Locale#getDefault
     */
    public static BreakIterator getSentenceInstance()
    {
        return getSentenceInstance(Locale.getDefault());
    }

    /**
     * Create BreakIterator for sentence-breaks using specified locale
     * Returns an instance of a BreakIterator implementing sentence breaks.
     * @param where the local.  If a specific SentenceBreak is not
     * avaliable for the specified local, a default SentenceBreak is returned.
     * @return A BreakIterator for sentence-breaks
     */
    public static BreakIterator getSentenceInstance(Locale where)
    {
        return getBreakInstance(where,
                                SENTENCE_INDEX,
                                "SentenceData",
                                "SentenceDictionary");
    }

    private static BreakIterator getBreakInstance(Locale where,
                                                  int type,
                                                  String dataName,
                                                  String dictionaryName) {
        if (iterCache[type] != null) {
            BreakIteratorCache cache = (BreakIteratorCache) iterCache[type].get();
            if (cache != null) {
                if (cache.getLocale().equals(where)) {
                    return cache.createBreakInstance();
                }
            }
        }

        BreakIterator result = createBreakInstance(where,
                                                   type,
                                                   dataName,
                                                   dictionaryName);
        BreakIteratorCache cache = new BreakIteratorCache(where, result);
        iterCache[type] = new SoftReference(cache);
        return result;
    }

    private static ResourceBundle getBundle(final String baseName, final Locale locale) {
         return (ResourceBundle) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return ResourceBundle.getBundle(baseName, locale);
            }
        });
    }

    private static BreakIterator createBreakInstance(Locale where,
                                                     int type,
                                                     String dataName,
                                                     String dictionaryName) {

        ResourceBundle bundle = getBundle(
                        "sun.text.resources.BreakIteratorInfo", where);
        String[] classNames = bundle.getStringArray("BreakIteratorClasses");

        String dataFile = bundle.getString(dataName);

        try {
            if (classNames[type].equals("RuleBasedBreakIterator")) {
                return new RuleBasedBreakIterator(dataFile);
            }
            else if (classNames[type].equals("DictionaryBasedBreakIterator")) {
                String dictionaryFile = bundle.getString(dictionaryName);
                return new DictionaryBasedBreakIterator(dataFile, dictionaryFile);
            }
            else {
                throw new IllegalArgumentException("Invalid break iterator class \"" +
                                classNames[type] + "\"");
            }
        }
        catch (Exception e) {
            throw new InternalError(e.toString()); 
        }
    }

    /**
     * Returns an array of all locales for which the
     * <code>get*Instance</code> methods of this class can return
     * localized instances.
     * The array returned must contain at least a <code>Locale</code>
     * instance equal to {@link java.util.Locale#US Locale.US}.
     *
     * @return An array of locales for which localized
     *         <code>BreakIterator</code> instances are available.
     */
    public static synchronized Locale[] getAvailableLocales()
    {
        //FIX ME - this is a known bug.  It should return
        //all locales.
        return LocaleData.getAvailableLocales("NumberPatterns");
    }

    private static final class BreakIteratorCache {

        private BreakIterator iter;
        private Locale where;

        BreakIteratorCache(Locale where, BreakIterator iter) {
            this.where = where;
            this.iter = (BreakIterator) iter.clone();
        }

        Locale getLocale() {
            return where;
        }

        BreakIterator createBreakInstance() {
            return (BreakIterator) iter.clone();
        }
    }

    protected static long getLong(byte[] buf, int offset) {
        long num = buf[offset]&0xFF;
        for (int i = 1; i < 8; i++) {
            num = num<<8 | (buf[offset+i]&0xFF);
        }
        return num;
    }

    protected static int getInt(byte[] buf, int offset) {
        int num = buf[offset]&0xFF;
        for (int i = 1; i < 4; i++) {
            num = num<<8 | (buf[offset+i]&0xFF);
        }
        return num;
    }

    protected static short getShort(byte[] buf, int offset) {
        short num = (short)(buf[offset]&0xFF);
        num = (short)(num<<8 | (buf[offset+1]&0xFF));
        return num;
    }
}
