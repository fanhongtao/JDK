/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: TransletOutputHandlerFactory.java,v 1.16 2004/02/16 22:56:25 minchau Exp $
 */

package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2DOM;
import com.sun.org.apache.xml.internal.serializer.ToHTMLSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToHTMLStream;
import com.sun.org.apache.xml.internal.serializer.ToTextSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToTextStream;
import com.sun.org.apache.xml.internal.serializer.ToUnknownStream;
import com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToXMLStream;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.w3c.dom.Node;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * @author Santiago Pericas-Geertsen
 */
public class TransletOutputHandlerFactory {

    public static final int STREAM = 0;
    public static final int SAX    = 1;
    public static final int DOM    = 2;

    private String _encoding       = "utf-8";
    private String _method         = null;
    private int    _outputType     = STREAM;
    private OutputStream _ostream  = System.out;
    private Writer _writer         = null;
    private Node           _node   = null;
    private int _indentNumber      = -1;
    private ContentHandler _handler    = null;
    private LexicalHandler _lexHandler = null;

    static public TransletOutputHandlerFactory newInstance() {
	return new TransletOutputHandlerFactory();
    }

    public void setOutputType(int outputType) {
	_outputType = outputType;
    }

    public void setEncoding(String encoding) {
	if (encoding != null) {
	    _encoding = encoding;
	}
    }

    public void setOutputMethod(String method) {
	_method = method;
    }

    public void setOutputStream(OutputStream ostream) {
	_ostream = ostream;
    }

    public void setWriter(Writer writer) {
	_writer = writer;
    }

    public void setHandler(ContentHandler handler) {
        _handler = handler;
    }

    public void setLexicalHandler(LexicalHandler lex) {
	_lexHandler = lex;
    }

    public void setNode(Node node) {
	_node = node;
    }

    public Node getNode() {
	return (_handler instanceof SAX2DOM) ? ((SAX2DOM)_handler).getDOM() 
	   : null;
    }

    public void setIndentNumber(int value) {
	_indentNumber = value;
    }

    public SerializationHandler getSerializationHandler()
        throws IOException, ParserConfigurationException
    {
        SerializationHandler result = null;
        switch (_outputType)
        {
            case STREAM :

                if (_method == null)
                {
                    result = new ToUnknownStream();
                }
                else if (_method.equalsIgnoreCase("xml"))
                {

                    result = new ToXMLStream();

                }
                else if (_method.equalsIgnoreCase("html"))
                {

                    result = new ToHTMLStream();

                }
                else if (_method.equalsIgnoreCase("text"))
                {

                    result = new ToTextStream();

                }

                if (result != null && _indentNumber >= 0)
                {
                    result.setIndentAmount(_indentNumber);
                }

                result.setEncoding(_encoding);

                if (_writer != null)
                {
                    result.setWriter(_writer);
                }
                else
                {
                    result.setOutputStream(_ostream);
                }
                return result;

            case DOM :
                _handler = (_node != null) ? new SAX2DOM(_node) : new SAX2DOM();
                _lexHandler = (LexicalHandler) _handler;
                // falls through
            case SAX :
                if (_method == null)
                {
                    _method = "xml"; // default case
                }

                if (_method.equalsIgnoreCase("xml"))
                {

                    if (_lexHandler == null)
                    {
                        result = new ToXMLSAXHandler(_handler, _encoding);
                    }
                    else
                    {
                        result =
                            new ToXMLSAXHandler(
                                _handler,
                                _lexHandler,
                                _encoding);
                    }

                }
                else if (_method.equalsIgnoreCase("html"))
                {

                    if (_lexHandler == null)
                    {
                        result = new ToHTMLSAXHandler(_handler, _encoding);
                    }
                    else
                    {
                        result =
                            new ToHTMLSAXHandler(
                                _handler,
                                _lexHandler,
                                _encoding);
                    }

                }
                else if (_method.equalsIgnoreCase("text"))
                {

                    if (_lexHandler == null)
                    {
                        result = new ToTextSAXHandler(_handler, _encoding);
                    }
                    else
                    {
                        result =
                            new ToTextSAXHandler(
                                _handler,
                                _lexHandler,
                                _encoding);
                    }

                }
                return result;
        }
        return null;
    }

}
