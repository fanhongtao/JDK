/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.text.html.parser;

import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Responsible for starting up a new DocumentParser
 * each time its parse method is invoked. Stores a
 * reference to the dtd.
 *
 * @author  Sunita Mani
 * @version 1.9, 02/06/02
 */

public class ParserDelegator extends HTMLEditorKit.Parser implements Serializable {

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
	    in = getResourceAsStream(path);
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

    /**
     * Fetch a resource relative to the ParserDelegator classfile.
     * If this is called on 1.2 the loading will occur under the
     * protection of a doPrivileged call to allow the ParserDelegator
     * to function when used in an applet.
     *
     * @param name the name of the resource, relative to the
     *  ParserDelegator class.
     * @returns a stream representing the resource
     */
    static InputStream getResourceAsStream(String name) {
	try {
	    Class klass;
	    ClassLoader loader = ParserDelegator.class.getClassLoader();
	    if (loader != null) {
		klass = loader.loadClass("javax.swing.text.html.parser.ResourceLoader");
	    } else {
		klass = Class.forName("javax.swing.text.html.parser.ResourceLoader");
	    }
	    Class[] parameterTypes = { String.class };
	    Method loadMethod = klass.getMethod("getResourceAsStream", parameterTypes);
	    String[] args = { name };
	    return (InputStream) loadMethod.invoke(null, args);
	} catch (Throwable e) {
	    // If the class doesn't exist or we have some other 
	    // problem we just try to call getResourceAsStream directly.
	    return ParserDelegator.class.getResourceAsStream(name);
	}
    }

    private void readObject(ObjectInputStream s)
	throws ClassNotFoundException, IOException {
	s.defaultReadObject();
	if (dtd == null) {
	    setDefaultDTD();
	}
    }
}


