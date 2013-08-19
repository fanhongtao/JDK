/*
 * @(#)TextInputCallback.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth.callback;

/**
 * <p> Underlying security services instantiate and pass a
 * <code>TextInputCallback</code> to the <code>handle</code>
 * method of a <code>CallbackHandler</code> to retrieve generic text
 * information.
 *
 * @version 1.12, 01/23/03
 * @see javax.security.auth.callback.CallbackHandler
 */
public class TextInputCallback implements Callback, java.io.Serializable {

    /**
     * @serial
     * @since 1.4
     */
    private String prompt;
    /**
     * @serial
     * @since 1.4
     */
    private String defaultText;
    /**
     * @serial
     * @since 1.4
     */
    private String inputText;

    /**
     * Construct a <code>TextInputCallback</code> with a prompt.
     *
     * <p>
     *
     * @param prompt the prompt used to request the information.
     *
     * @exception IllegalArgumentException if <code>prompt</code> is null
     *			or if <code>prompt</code> has a length of 0.
     */
    public TextInputCallback(String prompt) {
	if (prompt == null || prompt.length() == 0)
	    throw new IllegalArgumentException();
	this.prompt = prompt;
    }

    /**
     * Construct a <code>TextInputCallback</code> with a prompt
     * and default input value.
     *
     * <p>
     *
     * @param prompt the prompt used to request the information. <p>
     *
     * @param defaultText the text to be used as the default text displayed
     *			with the prompt.
     *
     * @exception IllegalArgumentException if <code>prompt</code> is null,
     *			if <code>prompt</code> has a length of 0,
     *			if <code>defaultText</code> is null
     *			or if <code>defaultText</code> has a length of 0.
     */
    public TextInputCallback(String prompt, String defaultText) {
	if (prompt == null || prompt.length() == 0 ||
	    defaultText == null || defaultText.length() == 0)
	    throw new IllegalArgumentException();

	this.prompt = prompt;
	this.defaultText = defaultText;
    }

    /**
     * Get the prompt.
     *
     * <p>
     *
     * @return the prompt.
     */
    public String getPrompt() {
	return prompt;
    }

    /**
     * Get the default text.
     *
     * <p>
     *
     * @return the default text, or null if this <code>TextInputCallback</code>
     *		was not instantiated with <code>defaultText</code>.
     */
    public String getDefaultText() {
	return defaultText;
    }

    /**
     * Set the retrieved text.
     *
     * <p>
     *
     * @param text the retrieved text, which may be null.
     *
     * @see #getText
     */
    public void setText(String text) {
	this.inputText = text;
    }

    /**
     * Get the retrieved text.
     *
     * <p>
     *
     * @return the retrieved text, which may be null.
     *
     * @see #setText
     */
    public String getText() {
	return inputText;
    }
}
