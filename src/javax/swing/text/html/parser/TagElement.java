/*
 * @(#)TagElement.java	1.6 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.text.html.parser;

import javax.swing.text.html.HTML;
/**
 * A generic HTML TagElement class. The methods define how white
 * space is interpreted around the tag.
 *
 * @version 	1.6, 08/26/98
 * @author      Sunita Mani
 */

public class TagElement {

    Element elem;
    HTML.Tag htmlTag;
    boolean insertedByErrorRecovery;

    public TagElement ( Element elem ) {
	this(elem, false);
    }

    public TagElement (Element elem, boolean fictional) {
	this.elem = elem;
	htmlTag = HTML.getTag(elem.getName());
	if (htmlTag == null) {
	    htmlTag = new HTML.UnknownTag(elem.getName());
	}
	insertedByErrorRecovery = fictional;
    }

    public boolean breaksFlow() {
	return htmlTag.breaksFlow();
    }

    public boolean isPreformatted() {
	return htmlTag.isPreformatted();
    }

    public Element getElement() {
	return elem;
    }

    public HTML.Tag getHTMLTag() {
	return htmlTag;
    }

    public boolean fictional() {
	return insertedByErrorRecovery;
    }
}




