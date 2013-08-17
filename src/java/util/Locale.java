/*
 * @(#)Locale.java  1.21 97/01/29
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * Portions copyright (c) 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package java.util;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 *
 * A <code>Locale</code> object represents a specific geographical, political,
 * or cultural region. An operation that requires a <code>Locale</code> to perform
 * its task is called <em>locale-sensitive</em> and uses the <code>Locale</code>
 * to tailor information for the user. For example, displaying a number
 * is a locale-sensitive operation--the number should be formatted
 * according to the customs/conventions of the user's native country,
 * region, or culture.
 *
 * <P>
 * You create a <code>Locale</code> object using one of the two constructors in
 * this class:
 * <blockquote>
 * <pre>
 * Locale(String language, String country)
 * Locale(String language, String country, String variant)
 * </pre>
 * </blockquote>
 * The first argument to both constructors is a valid <STRONG>ISO
 * Language Code.</STRONG> These codes are the lower-case two-letter
 * codes as defined by ISO-639.
 * You can find a full list of these codes at a number of sites, such as:
 * <BR><a href ="http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt">
 * <code>http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt</code></a>
 *
 * <P>
 * The second argument to both constructors is a valid <STRONG>ISO Country
 * Code.</STRONG> These codes are the upper-case two-letter codes
 * as defined by ISO-3166.
 * You can find a full list of these codes at a number of sites, such as:
 * <BR><a href="http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html">
 * <code>http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html</code></a>
 *
 * <P>
 * The second constructor requires a third argument--the <STRONG>Variant.</STRONG>
 * The Variant codes are vendor and browser-specific.
 * For example, use WIN for Windows, MAC for Macintosh, and POSIX for POSIX.
 * Where there are two variants, separate them with an underscore, and
 * put the most important one first. For
 * example, a Traditional Spanish collation might be referenced, with
 * "ES", "ES", "Traditional_WIN".
 *
 * <P>
 * Because a <code>Locale</code> object is just an identifier for a region,
 * no validity check is performed when you construct a <code>Locale</code>.
 * If you want to see whether particular resources are available for the
 * <code>Locale</code> you construct, you must query those resources. For
 * example, ask the <code>NumberFormat</code> for the locales it supports
 * using its <code>getAvailableLocales</code> method.
 * <BR><STRONG>Note:</STRONG> When you ask for a resource for a particular
 * locale, you get back the best available match, not necessarily
 * precisely what you asked for. For more information, look at
 * <a href="java.util.ResourceBundle.html"><code>ResourceBundle</code></a>.
 *
 * <P>
 * The <code>Locale</code> class provides a number of convenient constants
 * that you can use to create <code>Locale</code> objects for commonly used
 * locales. For example, the following creates a <code>Locale</code> object
 * for the United States:
 * <blockquote>
 * <pre>
 * Locale.US
 * </pre>
 * </blockquote>
 *
 * <P>
 * Once you've created a <code>Locale</code> you can query it for information about
 * itself. Use <code>getCountry</code> to get the ISO Country Code and
 * <code>getLanguage</code> to get the ISO Language Code. You can
 * use <code>getDisplayCountry</code> to get the
 * name of the country suitable for displaying to the user. Similarly,
 * you can use <code>getDisplayLanguage</code> to get the name of
 * the language suitable for displaying to the user. Interestingly,
 * the <code>getDisplayXXX</code> methods are themselves locale-sensitive
 * and have two versions: one that uses the default locale and one
 * that uses the locale specified as an argument.
 *
 * <P>
 * The JDK provides a number of classes that perform locale-sensitive
 * operations. For example, the <code>NumberFormat</code> class formats
 * numbers, currency, or percentages in a locale-sensitive manner. Classes
 * such as <code>NumberFormat</code> have a number of convenience methods
 * for creating a default object of that type. For example, the
 * <code>NumberFormat</code> class provides these three convenience methods
 * for creating a default <code>NumberFormat</code> object:
 * <blockquote>
 * <pre>
 * NumberFormat.getInstance()
 * NumberFormat.getCurrencyInstance()
 * NumberFormat.getPercentInstance()
 * </pre>
 * </blockquote>
 * These methods have two variants; one with an explicit locale
 * and one without; the latter using the default locale.
 * <blockquote>
 * <pre>
 * NumberFormat.getInstance(myLocale)
 * NumberFormat.getCurrencyInstance(myLocale)
 * NumberFormat.getPercentInstance(myLocale)
 * </pre>
 * </blockquote>
 * A <code>Locale</code> is the mechanism for identifying the kind of object
 * (<code>NumberFormat</code>) that you would like to get. The locale is
 * <STRONG>just</STRONG> a mechanism for identifying objects,
 * <STRONG>not</STRONG> a container for the objects themselves.
 *
 * <P>
 * Each class that performs locale-sensitive operations allows you
 * to get all the available objects of that type. You can sift
 * through these objects by language, country, or variant,
 * and use the display names to present a menu to the user.
 * For example, you can create a menu of all the collation objects
 * suitable for a given language. Such classes must implement these
 * three class methods:
 * <blockquote>
 * <pre>
 * public static Locale[] getAvailableLocales()
 * public static String getDisplayName(Locale objectLocale,
 *                                     Locale displayLocale)
 * public static final String getDisplayName(Locale objectLocale)
 *     // getDisplayName will throw MissingResourceException if the locale
 *     // is not one of the available locales.
 * </pre>
 * </blockquote>
 *
 * @see         ResourceBundle
 * @see         java.text.Format
 * @see         java.text.NumberFormat
 * @see         java.text.Collation
 * @version     1.21 29 Jan 1997
 * @author      Mark Davis
 */

public final class Locale implements Cloneable, Serializable {

    /** Useful constant for language.
     */
    static public final Locale ENGLISH = new Locale("en","","");

    /** Useful constant for language.
     */
    static public final Locale FRENCH = new Locale("fr","","");

    /** Useful constant for language.
     */
    static public final Locale GERMAN = new Locale("de","","");

    /** Useful constant for language.
     */
    static public final Locale ITALIAN = new Locale("it","","");

    /** Useful constant for language.
     */
    static public final Locale JAPANESE = new Locale("ja","","");

    /** Useful constant for language.
     */
    static public final Locale KOREAN = new Locale("ko","","");

    /** Useful constant for language.
     */
    static public final Locale CHINESE = new Locale("zh","","");

    /** Useful constant for language.
     */
    static public final Locale SIMPLIFIED_CHINESE = new Locale("zh","CN","");

    /** Useful constant for language.
     */
    static public final Locale TRADITIONAL_CHINESE = new Locale("zh","TW","");

    /** Useful constant for country.
     */
    static public final Locale FRANCE = new Locale("fr","FR","");

    /** Useful constant for country.
     */
    static public final Locale GERMANY = new Locale("de","DE","");

    /** Useful constant for country.
     */
    static public final Locale ITALY = new Locale("it","IT","");

    /** Useful constant for country.
     */
    static public final Locale JAPAN = new Locale("ja","JP","");

    /** Useful constant for country.
     */
    static public final Locale KOREA = new Locale("ko","KR","");

    /** Useful constant for country.
     */
    static public final Locale CHINA = new Locale("zh","CN","");

    /** Useful constant for country.
     */
    static public final Locale PRC = new Locale("zh","CN","");

    /** Useful constant for country.
     */
    static public final Locale TAIWAN = new Locale("zh","TW","");

    /** Useful constant for country.
     */
    static public final Locale UK = new Locale("en","GB","");

    /** Useful constant for country.
     */
    static public final Locale US = new Locale("en","US","");

    /** Useful constant for country.
     */
    static public final Locale CANADA = new Locale("en","CA","");

    /** Useful constant for country.
     */
    static public final Locale CANADA_FRENCH = new Locale("fr","CA","");

    /** serialization ID
     */
    static final long serialVersionUID = 9149081749638150636L;

    /**
     * Construct a locale from language, country, variant.
     * NOTE:  ISO 639 is not a stable standard; some of the language codes it defines
     * (specifically iw, ji, and in) have changed.  This constructor accepts both the
     * old codes (iw, ji, and in) and the new codes (he, yi, and id), but all other
     * API on Locale will return only the OLD codes.
     * @param language lowercase two-letter ISO-639 code.
     * @param country uppercase two-letter ISO-3166 code.
     * @param variant vendor and browser specific code. See class description.
     */
    public Locale(String language, String country, String variant) {
        // we accept both the old and the new ISO codes for the languages whose ISO
        // codes have changed, but we always store the OLD code, for backward compatibility
        language = toLowerCase(language).intern();
        if (language == "he")
            language = "iw";
        else if (language == "yi")
            language = "ji";
        else if (language == "id")
            language = "in";

        this.language = language;
        this.country = toUpperCase(country).intern();
        this.variant = toUpperCase(variant).intern();
    }

    /**
     * Construct a locale from language, country.
     * NOTE:  ISO 639 is not a stable standard; some of the language codes it defines
     * (specifically iw, ji, and in) have changed.  This constructor accepts both the
     * old codes (iw, ji, and in) and the new codes (he, yi, and id), but all other
     * API on Locale will return only the OLD codes.
     * @param language lowercase two-letter ISO-639 code.
     * @param country uppercase two-letter ISO-3166 code.
     */
    public Locale(String language, String country) {
        this(language, country, "");
    }

    /**
     * Common method of getting the current default Locale.
     * Used for the presentation: menus, dialogs, etc.
     * Generally set once when your applet or application is initialized,
     * then never reset. (If you do reset the default locale, you
     * probably want to reload your GUI, so that the change is reflected
     * in your interface.)
     * <p>More advanced programs will allow users to use different locales
     * for different fields, e.g. in a spreadsheet.
     * <BR>Note that the initial setting will match the host system.
     */
    public static Locale getDefault() {
        return defaultLocale;   // this variable is now initialized at static init time
    }

    /**
     * Sets the default.
     * Normally set once at the beginning of applet or application,
     * then never reset. <code>setDefault</code> does not reset the host locale.
     * @param newLocale Locale to set to.
     */
    public static synchronized void setDefault(Locale newLocale) {
    SecurityManager security = System.getSecurityManager();
    if (security != null) {
        security.checkPropertyAccess("user.language");
    }
        defaultLocale = newLocale;
    }

    /**
     * Getter for programmatic name of field,
     * an lowercased two-letter ISO-639 code.
     * @see #getDisplayLanguage
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Getter for programmatic name of field,
     * an uppercased two-letter ISO-3166 code.
     * @see #getDisplayCountry
     */
    public String getCountry() {
        return country;
    }

    /**
     * Getter for programmatic name of field.
     * @see #getDisplayVariant
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Getter for the programmatic name of the entire locale,
     * with the language, country and variant separated by underbars.
     * Language is always lower case, and country is always uppcer case.
     * If a field is missing, at most one underbar will occur.
     * Example: "Een, "de_DE", "en_US_WIN", "de_POSIX", "fr_MAC"
     * @see #getDisplayName
     */
    public final String toString() {
        StringBuffer result = new StringBuffer(language);
        if (country.length() != 0) {
            result.append('_');
            result.append(country);
            if (variant.length() != 0) {
                result.append('_');
                result.append(variant);
            }
        }
        return result.toString();
    }

    /**
     * Getter for the three-letter ISO language abbreviation
     * of the locale.  Returns the empty string if the locale doesn't specify a language.
     * @exception MissingResourceException Throws MissingResourceException if the
     * three-letter language abbreviation is not available for this locale.
     */
    public String getISO3Language() throws MissingResourceException {
        if (language.length() == 0)
            return "";

        // the call to getISO2Language() will throw a MissingResourceException if
        // the appropriate locale isn't installed
        getISO2Language();

        ResourceBundle resource = ResourceBundle.getBundle
                ("java.text.resources.LocaleElements", this);
        return resource.getString("ShortLanguage");
    }

    /**
     * Getter for the three-letter ISO country abbreviation
     * of the locale.  Returns the empty string if the locale doesn't specify a country.
     * @exception MissingResourceException Throws MissingResourceException if the
     * three-letter language abbreviation is not available for this locale.
     */
    public String getISO3Country() throws MissingResourceException {
        if (country.length() == 0)
            return "";

        // the call to getISO2Country() will throw a MissingResourceException if
        // the appropriate locale isn't installed
        getISO2Country();

        ResourceBundle resource = ResourceBundle.getBundle
                ("java.text.resources.LocaleElements", this);
        return resource.getString("ShortCountry");
    }

    /**
     * Getter for the two-letter ISO language abbreviation
     * of the locale.  Returns the empty string if the locale doesn't specify a language.
     * @exception MissingResourceException Throws MissingResourceException if the
     * two-letter language abbreviation is not available for this locale.
     */
    /*public*/ String getISO2Language() throws MissingResourceException {
        if (language.length() == 0)
            return "";

        ResourceBundle resource = ResourceBundle.getBundle
                ("java.text.resources.LocaleElements", this);
        String localeID = resource.getString("LocaleString");
        String result = localeID.substring(0, 2);
        if (!result.equals(language))
            throw new MissingResourceException("Requested resource bundle not installed",
                "LocaleElements", "LocaleString");
        return result;
    }

    /**
     * Getter for the two-letter ISO country abbreviation
     * of the locale.  Returns the empty string if the locale doesn't specify a country.
     * @exception MissingResourceException Throws MissingResourceException if the
     * two-letter language abbreviation is not available for this locale.
     */
    /*public*/ String getISO2Country() throws MissingResourceException {
        if (country.length() == 0)
            return "";

        ResourceBundle resource = ResourceBundle.getBundle
                ("java.text.resources.LocaleElements", this);
        String localeID = resource.getString("LocaleString");
        String result = localeID.substring(3, 5);
        if (!result.equals(country))
            throw new MissingResourceException("Requested resource bundle not installed",
                "LocaleElements", "LocaleString");
        return result;
    }

    /**
     * Returns a name for the locale's language that is appropriate for display to the
     * user.  This will be the name the locale's language localized for the default locale,
     * if that data is available.  For example, if the locale is fr_FR and the default locale
     * is en_US, getDisplayLanguage() will return "French"; if the locale is en_US and
     * the default locale is fr_FR, getDisplayLanguage() will return "anglais".  If the
     * appropriate name isn't available (say, we don't have a Japanese name for Croatian),
     * this function falls back on the English name and uses the ISO code as a last-resort
     * value.  If the locale doesn't specify a language, this function returns the empty string.
     */
    public final String getDisplayLanguage() {
        return getDisplayLanguage(getDefault());
    }

    /**
     * Returns a name for the locale's language that is appropriate for display to the
     * user.  This will be the name the locale's language localized for inLocale,
     * if that data is available.  For example, if the locale is fr_FR and inLocale
     * is en_US, getDisplayLanguage() will return "French"; if the locale is en_US and
     * inLocale is fr_FR, getDisplayLanguage() will return "anglais".  If the
     * appropriate name isn't available (say, we don't have a Japanese name for Croatian),
     * this function falls back on the default locale, on the English name, and finally
     * on the ISO code as a last-resort value.  If the locale doesn't specify a language,
     * this function returns the empty string.
     */
    public String getDisplayLanguage(Locale inLocale) {
        String  langCode = language;
        if (langCode.length() == 0)
            return "";

        Locale  workingLocale = (Locale)inLocale.clone();
        String  result = null;
        int     phase = 0;
        boolean done = false;

        if (workingLocale.variant.length() == 0)
            phase = 1;
        if (workingLocale.country.length() == 0)
            phase = 2;

        while (!done) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(
                    "java.text.resources.LocaleElements", workingLocale);
                result = findStringMatch((String[][])bundle.getObject("Languages"),
                                    langCode, langCode);
                if (result.length() != 0)
                    done = true;
            }
            catch (Exception e) {
                // just fall through
            }

            if (!done) {
                switch (phase) {
                    case 0:
                        workingLocale.variant = "";
                        break;

                    case 1:
                        workingLocale.country = "";
                        break;

                    case 2:
                        workingLocale = getDefault();
                        break;

                    case 3:
                        workingLocale = new Locale("", "", "");
                        break;

                    default:
                        return langCode;
                }
                phase++;
            }
        }
        return result;
    }

    /**
     * Returns a name for the locale's country that is appropriate for display to the
     * user.  This will be the name the locale's country localized for the default locale,
     * if that data is available.  For example, if the locale is fr_FR and the default locale
     * is en_US, getDisplayCountry() will return "France"; if the locale is en_US and
     * the default locale is fr_FR, getDisplayLanguage() will return "Etats-Unis".  If the
     * appropriate name isn't available (say, we don't have a Japanese name for Croatia),
     * this function falls back on the English name and uses the ISO code as a last-resort
     * value.  If the locale doesn't specify a country, this function returns the empty string.
     */
    public final String getDisplayCountry() {
        return getDisplayCountry(getDefault());
    }

    /**
     * Returns a name for the locale's country that is appropriate for display to the
     * user.  This will be the name the locale's country localized for inLocale,
     * if that data is available.  For example, if the locale is fr_FR and inLocale
     * is en_US, getDisplayCountry() will return "France"; if the locale is en_US and
     * inLocale is fr_FR, getDisplayLanguage() will return "Etats-Unis".  If the
     * appropriate name isn't available (say, we don't have a Japanese name for Croatia),
     * this function falls back on the default locale, on the English name, and finally
     * on the ISO code as a last-resort value.  If the locale doesn't specify a country,
     * this function returns the empty string.
     */
    public String getDisplayCountry(Locale inLocale) {
        String  ctryCode = country;
        if (ctryCode.length() == 0)
            return "";

        Locale  workingLocale = (Locale)inLocale.clone();
        String  result = null;
        int     phase = 0;
        boolean done = false;

        if (workingLocale.variant.length() == 0)
            phase = 1;
        if (workingLocale.country.length() == 0)
            phase = 2;

        while (!done) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(
                    "java.text.resources.LocaleElements", workingLocale);
                result = findStringMatch((String[][])bundle.getObject("Countries"),
                                    ctryCode, ctryCode);
                if (result.length() != 0)
                    done = true;
            }
            catch (Exception e) {
                // just fall through
            }

            if (!done) {
                switch (phase) {
                    case 0:
                        workingLocale.variant = "";
                        break;

                    case 1:
                        workingLocale.country = "";
                        break;

                    case 2:
                        workingLocale = getDefault();
                        break;

                    case 3:
                        workingLocale = new Locale("", "", "");
                        break;

                    default:
                        return ctryCode;
                }
                phase++;
            }
        }
        return result;
    }

    /**
     * Returns a name for the locale's variant code that is appropriate for display to the
     * user.  If possible, the name will be localized for the default locale.  If the locale
     * doesn't specify a variant code, this function returns the empty string.
     */
    public final String getDisplayVariant() {
        return getDisplayVariant(getDefault());
    }

    /**
     * Returns a name for the locale's variant code that is appropriate for display to the
     * user.  If possible, the name will be localized for inLocale.  If the locale
     * doesn't specify a variant code, this function returns the empty string.
     */
    public String getDisplayVariant(Locale inLocale) {
        if (variant.length() == 0)
            return "";

        ResourceBundle bundle = ResourceBundle.getBundle(
                "java.text.resources.LocaleElements", inLocale);

        String names[] = getDisplayVariantArray(bundle);

        // Get the localized patterns for formatting a list, and use
        // them to format the list.
        String[] patterns;
        try {
            patterns = (String[])bundle.getObject("LocaleNamePatterns");
        }
        catch (MissingResourceException e) {
            patterns = null;
        }
        return formatList(patterns, names);
   }

    /**
     * Returns a name for the locale that is appropriate for display to the
     * user.  This will be the values returned by getDisplayLanguage(), getDisplayCountry(),
     * and getDisplayVariant() assembled into a single string.  The display name will have
     * one of the following forms:<p><blockquote>
     * language (country, variant)<p>
     * language (country)<p>
     * language (variant)<p>
     * country (variant)<p>
     * language<p>
     * country<p>
     * variant<p></blockquote>
     * depending on which fields are specified in the locale.  If the language, country,
     * and variant fields are all empty, this function returns the empty string.
     */
    public final String getDisplayName() {
        return getDisplayName(getDefault());
    }

    /**
     * Returns a name for the locale that is appropriate for display to the
     * user.  This will be the values returned by getDisplayLanguage(), getDisplayCountry(),
     * and getDisplayVariant() assembled into a single string.  The display name will have
     * one of the following forms:<p><blockquote>
     * language (country, variant)<p>
     * language (country)<p>
     * language (variant)<p>
     * country (variant)<p>
     * language<p>
     * country<p>
     * variant<p></blockquote>
     * depending on which fields are specified in the locale.  If the language, country,
     * and variant fields are all empty, this function returns the empty string.
     */
    public String getDisplayName(Locale inLocale) {
        ResourceBundle bundle = ResourceBundle.getBundle(
                "java.text.resources.LocaleElements", inLocale);

        String languageName = getDisplayLanguage(inLocale);
        String countryName = getDisplayCountry(inLocale);
        String[] variantNames = getDisplayVariantArray(bundle);

        // Get the localized patterns for formatting a display name.
        String[] patterns;
        try {
            patterns = (String[])bundle.getObject("LocaleNamePatterns");
        }
        catch (MissingResourceException e) {
            patterns = null;
        }

        // The display name consists of a main name, followed by qualifiers.
        // Typically, the format is "MainName (Qualifier, Qualifier)" but this
        // depends on what pattern is stored in the display locale.
        String   mainName       = null;
        String[] qualifierNames = null;

        // The main name is the language, or if there is no language, the country.
        // If there is neither language nor country (an anomalous situation) then
        // the display name is simply the variant's display name.
        if (languageName.length() != 0) {
            mainName = languageName;
            if (countryName.length() != 0) {
                qualifierNames = new String[variantNames.length + 1];
                System.arraycopy(variantNames, 0, qualifierNames, 1, variantNames.length);
                qualifierNames[0] = countryName;
            }
            else qualifierNames = variantNames;
        }
        else if (countryName.length() != 0) {
            mainName = countryName;
            qualifierNames = variantNames;
        }
        else {
            return formatList(patterns, variantNames);
        }

        // Create an array whose first element is the number of remaining
        // elements.  This serves as a selector into a ChoiceFormat pattern from
        // the resource.  The second and third elements are the main name and
        // the qualifier; if there are no qualifiers, the third element is
        // unused by the format pattern.
        Object[] displayNames = {
            new Integer(qualifierNames.length != 0 ? 2 : 1),
            mainName,
            // We could also just call formatList() and have it handle the empty
            // list case, but this is more efficient, and we want it to be
            // efficient since all the language-only locales will not have any
            // qualifiers.
            qualifierNames.length != 0 ? formatList(patterns, qualifierNames) : null
        };

        if (patterns != null) {
            return new MessageFormat(patterns[0]).format(displayNames);
        }
        else {
            // If we cannot get the message format pattern, then we use a simple
            // hard-coded pattern.  This should not occur in practice unless the
            // installation is missing some core files (LocaleElements etc.).
            StringBuffer result = new StringBuffer();
            result.append((String)displayNames[1]);
            if (displayNames.length > 2) {
                result.append(" (");
                result.append((String)displayNames[2]);
                result.append(")");
            }
            return result.toString();
        }
    }

    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        try {
            Locale that = (Locale)super.clone();
            return that;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Override hashCode.
     * Since Locales are often used in hashtables, caches the value
     * for speed.
     */
      // XXX Depending on performance of synchronized, may want to
      // XXX just compute in constructor.
    public synchronized int hashCode() {
        if (hashcode == -1) {
            hashcode =
        language.hashCode() ^
        country.hashCode() ^
        variant.hashCode();
        }
        return hashcode;
    }

    // Overrides

    public boolean equals(Object obj) {
        if (this == obj)                      // quick check
            return true;
        if (!(obj instanceof Locale))         // (1) same object?
            return false;
        Locale other = (Locale) obj;
        if (hashCode() != other.hashCode()) return false;       // quick check
        if (!language.equals(other.language)) return false;
        if (!country.equals(other.country)) return false;
        if (!variant.equals(other.variant)) return false;
        return true; // we made it through the guantlet.
        // (1)  We don't check super.equals since it is Object.
        //      Since Locale is final, we don't have to check both directions.
    }

    // ================= privates =====================================

    // XXX instance and class variables. For now keep these separate, since it is
    // faster to match. Later, make into single string.

    private String language = "";
    private String country = "";
    private String variant = "";

    // this field really should be transient, but we made the mistake of letting it
    // ship this way, so now we're stuck with it
    private int hashcode = -1;        // lazy evaluated

    private static Locale defaultLocale;

    static {
        /* The user.region property may be of the form country, country_variant,
         * or _variant.  Since the Locale constructor takes the country value as
         * an unparsed literal, and we don't want to change that behavior, we
         * must premunge it here into country and variant.  Liu 7/9/98
         */
        String country = System.getProperty("user.region", "");
        String variant = "";
        int i = country.indexOf('_');
        if (i >= 0) {
            variant = country.substring(i+1);
            country = country.substring(0, i);
        }
        defaultLocale = new Locale(System.getProperty("user.language", "EN"),
                                   country, variant);
    }

    /*
     * Locale needs its own, locale insenitive version of toLowerCase to
     * avoid circularity problems between Locale and String.
     * The most straightforward algorithm is used. Look at optimizations later.
     */
    private String toLowerCase(String str) {
        char[] buf = str.toCharArray();
        for (int i = 0; i < buf.length; i++) {
            buf[i] = Character.toLowerCase( buf[i] );
        }
        return new String( buf );
    }

    /*
     * Locale needs its own, locale insenitive version of toUpperCase to
     * avoid circularity problems between Locale and String.
     * The most straightforward algorithm is used. Look at optimizations later.
     */
    private String toUpperCase(String str) {
        char[] buf = str.toCharArray();
        for (int i = 0; i < buf.length; i++) {
            buf[i] = Character.toUpperCase( buf[i] );
        }
        return new String( buf );
    }

    private String findStringMatch(String[][] languages,
                                   String desiredLanguage, String fallbackLanguage)
    {
        for (int i = 0; i < languages.length; ++i)
            if (desiredLanguage.equals(languages[i][0]))
                return languages[i][1];
        if (!fallbackLanguage.equals(desiredLanguage))
            for (int i = 0; i < languages.length; ++i)
                if (fallbackLanguage.equals(languages[i][0]))
                    return languages[i][1];
        if (!"EN".equals(desiredLanguage) && "EN".equals(fallbackLanguage))
            for (int i = 0; i < languages.length; ++i)
                if ("EN".equals(languages[i][0]))
                    return languages[i][1];
        return "";
    }

    /**
     * Return an array of the display names of the variant.
     * @param bundle the ResourceBundle to use to get the display names
     * @return an array of display names, possible of zero length.
     */
    private String[] getDisplayVariantArray(ResourceBundle bundle) {
        // Split the variant name into tokens separated by '_'.
        StringTokenizer tokenizer = new StringTokenizer(variant, "_");
        String[] names = new String[tokenizer.countTokens()];

        // For each variant token, lookup the display name.  If
        // not found, use the variant name itself.
        for (int i=0; i<names.length; ++i) {
            String token = tokenizer.nextToken();
            try {   
                names[i] = (String)bundle.getObject("%%" + token);
            }
            catch (MissingResourceException e) {
                names[i] = token;
            }
        }

        return names;
    }

    /**
     * Format a list with an array of patterns.
     * @param patterns an array of three patterns. The first pattern is not
     * used. The second pattern should create a MessageFormat taking 0-3 arguments
     * and formatting them into a list. The third pattern should take 2 arguments
     * and is used by composeList. If patterns is null, then a the list is
     * formatted by concatenation with the delimiter ','.
     * @param stringList the list of strings to be formatted.
     * @return a string representing the list.
     */
    private static String formatList(String[] patterns, String[] stringList) {
        // If we have no list patterns, compose the list in a simple,
        // non-localized way.
        if (patterns == null) {
            StringBuffer result = new StringBuffer();
            for (int i=0; i<stringList.length; ++i) {
                if (i>0) result.append(',');
                result.append(stringList[i]);
            }
            return result.toString();
        }

        // Compose the list down to three elements if necessary
        if (stringList.length > 3) {
            MessageFormat format = new MessageFormat(patterns[2]);
            stringList = composeList(format, stringList);
        }

        // Rebuild the argument list with the list length as the first element
        Object[] args = new Object[stringList.length + 1];
        System.arraycopy(stringList, 0, args, 1, stringList.length);
        args[0] = new Integer(stringList.length);

        // Format it using the pattern in the resource
        MessageFormat format = new MessageFormat(patterns[1]);
        return format.format(args);
    }

    /**
     * Given a list of strings, return a list shortened to three elements.
     * Shorten it by applying the given format to the first two elements
     * recursively.
     * @param format a format which takes two arguments
     * @param list a list of strings
     * @return if the list is three elements or shorter, the same list;
     * otherwise, a new list of three elements.
     */
    private static String[] composeList(MessageFormat format, String[] list) {
        if (list.length <= 3) return list;

        // Use the given format to compose the first two elements into one
        String[] listItems = { list[0], list[1] };
        String newItem = format.format(listItems);

        // Form a new list one element shorter
        String[] newList = new String[list.length-1];
        System.arraycopy(list, 2, newList, 1, newList.length-1);
        newList[0] = newItem;

        // Recurse
        return composeList(format, newList);
    }
}
