/*
 * @(#)TagElement.java	1.10 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.text.html.parser;

import javax.swing.text.html.HTML;
/**
 * A generic HTML TagElement class. The methods define how white
 * space is interpreted around the tag.
 *
 * @version 	1.10, 01/23/03
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




