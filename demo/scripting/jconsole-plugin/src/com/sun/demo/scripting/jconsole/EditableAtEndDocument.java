/*
 * @(#)EditableAtEndDocument.java	1.1 06/04/19 08:43:44
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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

package com.sun.demo.scripting.jconsole;

import javax.swing.text.*;

/** This class implements a special type of document in which edits
 * can only be performed at the end, from "mark" to the end of the
 * document. This is used in ScriptShellPanel class as document for editor.
 */
public class EditableAtEndDocument extends PlainDocument {
    private int mark;

    public void insertString(int offset, String text, AttributeSet a)
        throws BadLocationException {
        int len = getLength();
        super.insertString(len, text, a);
    }

    public void remove(int offs, int len) throws BadLocationException {
        int start = offs;
        int end = offs + len;
  
        int markStart = mark;
        int markEnd = getLength();
      
        if ((end < markStart) || (start > markEnd)) {
            // no overlap
            return;
        }

        // Determine interval intersection
        int cutStart = Math.max(start, markStart);
        int cutEnd = Math.min(end, markEnd);
        super.remove(cutStart, cutEnd - cutStart);
    }

    public void setMark() {
        mark = getLength();
    }

    public String getMarkedText() throws BadLocationException {
        return getText(mark, getLength() - mark);
    }

    /** Used to reset the contents of this document */
    public void clear() {
        try {
            super.remove(0, getLength());
            setMark();
        } catch (BadLocationException e) {
        }
    }
}
