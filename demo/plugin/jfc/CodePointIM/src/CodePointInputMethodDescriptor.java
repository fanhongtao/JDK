/*
 * @(#)CodePointInputMethodDescriptor.java	1.4 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)CodePointInputMethodDescriptor.java	1.4 04/07/26
 */

package com.sun.inputmethods.internal.codepointim;

import java.awt.Image;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.im.spi.InputMethod;
import java.util.Locale;

/**
 * The CodePointInputMethod is a simple input method that allows Unicode
 * characters to be entered via their hexadecimal code point values.
 *
 * The class, CodePointInputMethodDescriptor, provides information about the
 * CodePointInputMethod which allows it to be selected and loaded by the
 * Input Method Framework.
 */
public class CodePointInputMethodDescriptor implements InputMethodDescriptor {

    public CodePointInputMethodDescriptor() {
    }

    /**
     * Creates a new instance of the Code Point input method.
     *
     * @return a new instance of the Code Point input method
     * @exception Exception any exception that may occur while creating the
     * input method instance
     */
    public InputMethod createInputMethod() throws Exception {
        return new CodePointInputMethod();
    }

    /**
     * This input method can be used by any locale.
     */
    public Locale[] getAvailableLocales() {
        Locale[] locales = {
            new Locale("","",""),
        };
        return locales;
    }

    public synchronized String getInputMethodDisplayName(Locale inputLocale, Locale displayLanguage) {
        return "CodePoint Input Method";
    }

    public Image getInputMethodIcon(Locale inputLocale) {
        return null;
    }

    public boolean hasDynamicLocaleList() {
        return false;
    }
}
