/*
 * @(#)CollatorProvider.java	1.2 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.text.spi;

import java.text.Collator;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

/**
 * An abstract class for service providers that
 * provide concrete implementations of the 
 * {@link java.text.Collator Collator} class.
 *
 * @since        1.6 
 * @version      @(#)CollatorProvider.java	1.2 05/11/17
 */
public abstract class CollatorProvider extends LocaleServiceProvider {

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected CollatorProvider() {
    }

    /**
     * Returns a new <code>Collator</code> instance for the specified locale.
     * @param locale the desired locale.
     * @return the <code>Collator</code> for the desired locale.
     * @exception NullPointerException if
     * <code>locale</code> is null
     * @exception IllegalArgumentException if <code>locale</code> isn't
     *     one of the locales returned from 
     *     {@link java.util.spi.LocaleServiceProvider#getAvailableLocales() 
     *     getAvailableLocales()}.
     * @see java.text.Collator#getInstance(java.util.Locale)
     */
    public abstract Collator getInstance(Locale locale);
}
