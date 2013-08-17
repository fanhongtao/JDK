/*
 * @(#)ParserDelegator.java	1.4 98/08/26
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

import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.Reader;

/**
 * Responsible for starting up a new DocumentParser
 * each time its parse method is invoked. Stores a
 * reference to the dtd.
 *
 * @author  Sunita Mani
 *
 * @version 1.4, 08/26/98
 */

public class ParserDelegator extends HTMLEditorKit.Parser {

    private static DTD dtd = null;

    protected static void setDefaultDTD() {
        if (dtd == null) {
	    // (PENDING) Hate having to hard code!
	    String nm = "html32";
            try {
                dtd = DTD.getDTD(nm);
            } catch (IOException e) {
		// (PENDING) UGLY!
		System.out.println("Throw an exception: could not get default dtd: " + nm);
            }
            dtd = createDTD(dtd, nm);
        }
    }

    protected static DTD createDTD(DTD dtd, String name) {

	InputStream in = null;
	boolean debug = true;
	try {
	    String path = name + ".bdtd";
	    in = ParserDelegator.class.getResourceAsStream(path);
            if (in != null) {
                dtd.read(new DataInputStream(in));
                dtd.putDTDHash(name, dtd);
	    }
        } catch (Exception e) {
            System.out.println(e);
        }
        return dtd;
    }


    public ParserDelegator() {
	if (dtd == null) {
	    setDefaultDTD();
	}
    }

    public void parse(Reader r, HTMLEditorKit.ParserCallback cb, boolean ignoreCharSet) throws IOException {
	new DocumentParser(dtd).parse(r, cb, ignoreCharSet);
    }
}


