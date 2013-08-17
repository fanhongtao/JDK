/*
 * @(#)RTFEditorKit.java	1.6 98/08/26
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
package javax.swing.text.rtf;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.Action;
import javax.swing.text.*;
import javax.swing.*;

/**
 * This is the default implementation of rtf editing
 * functionality.
 *
 * @author  Timothy Prinzing
 * @version 1.6 08/26/98
 */
public class RTFEditorKit extends StyledEditorKit {

    /**
     * Constructs an RTFEditorKit.
     */
    public RTFEditorKit() {
	super();
    }

    /**
     * Create a copy of the editor kit.  This
     * allows an implementation to serve as a prototype
     * for others, so that they can be quickly created.
     *
     * @return the copy
     */
    public Object clone() {
	return new RTFEditorKit();
    }

    /**
     * Get the MIME type of the data that this
     * kit represents support for.  This kit supports
     * the type <code>text/rtf</code>.
     *
     * @return the type
     */
    public String getContentType() {
	return "text/rtf";
    }

    /**
     * Insert content from the given stream which is expected
     * to be in a format appropriate for this kind of content
     * handler.
     *
     * @param in  The stream to read from
     * @param doc The destination for the insertion.
     * @param pos The location in the document to place the
     *   content.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
    public void read(InputStream in, Document doc, int pos) throws IOException, BadLocationException {

	if (doc instanceof StyledDocument) {
	    // PENDING(prinz) this needs to be fixed to
	    // insert to the given position.
	    RTFReader rdr = new RTFReader((StyledDocument) doc);
	    rdr.readFromStream(in);
	    rdr.close();
	} else {
	    throw new IOException("Document must be StyledDocument");
	}
    }

    /**
     * Write content from a document to the given stream
     * in a format appropriate for this kind of content handler.
     *
     * @param out  The stream to write to
     * @param doc The source for the write.
     * @param pos The location in the document to fetch the
     *   content.
     * @param len The amount to write out.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
    public void write(OutputStream out, Document doc, int pos, int len)
	throws IOException, BadLocationException {

	    // PENDING(prinz) this needs to be fixed to
	    // use the given document range.
	    RTFGenerator.writeDocument(doc, out);
    }

    /**
     * Insert content from the given stream, which will be
     * treated as plain text.
     *
     * @param in  The stream to read from
     * @param doc The destination for the insertion.
     * @param pos The location in the document to place the
     *   content.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
    public void read(Reader in, Document doc, int pos)
	throws IOException, BadLocationException {

	throw new IOException("RTF is an 8-bit format");
    }

    /**
     * Write content from a document to the given stream
     * as plain text.
     *
     * @param out  The stream to write to
     * @param doc The source for the write.
     * @param pos The location in the document to fetch the
     *   content.
     * @param len The amount to write out.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
    public void write(Writer out, Document doc, int pos, int len)
	throws IOException, BadLocationException {

	throw new IOException("RTF is an 8-bit format");
    }

}
