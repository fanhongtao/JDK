/*
 * @(#)Annotation.java	1.7 98/07/07
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.text;

/**
* An Annotation object is used as a wrapper for a text attribute value if
* the attribute has annotation characteristics. These characteristics are:
* <ul>
* <li>The text range that the attribute is applied to is critical to the
* semantics of the range. That means, the attribute cannot be applied to subranges
* of the text range that it applies to, and, if two adjacent text ranges have
* the same value for this attribute, the attribute still cannot be applied to
* the combined range as a whole with this value.
* <li>The attribute or its value usually do no longer apply if the underlying text is
* changed.
* </ul>
*
* An example is grammatical information attached to a sentence:
* For the previous sentence, you can say that "an example"
* is the subject, but you cannot say the same about "an", "example", or "exam".
* When the text is changed, the grammatical information typically becomes invalid.
* Another example is Japanese reading information (yomi).
*
* <p>
* Wrapping the attribute value into an Annotation object guarantees that
* adjacent text runs don't get merged even if the attribute values are equal,
* and indicates to text containers that the attribute should be discarded if
* the underlying text is modified.
*
* @see AttributedCharacterIterator
*/

public class Annotation {

    /**
     * Constructs an annotation record with the given value.
     * @param value The value of the attribute
     */
    public Annotation(Object value) {
        this.value = value;
    }

    /**
     * Returns the value of the attribute.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns the String representation of this Annotation.
     */
    public String toString() {
	return getClass().getName() + "[value=" + value + "]";
    }

    private Object value;

};
