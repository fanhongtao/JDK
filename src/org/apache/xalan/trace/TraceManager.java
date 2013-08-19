/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.trace;

import java.util.Vector;
import java.util.TooManyListenersException;

import org.w3c.dom.Node;

import org.apache.xml.utils.QName;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPath;

/**
 * This class manages trace listeners, and acts as an
 * interface for the tracing functionality in Xalan.
 */
public class TraceManager
{

  /** A transformer instance */
  private TransformerImpl m_transformer;

  /**
   * Constructor for the trace manager.
   *
   * @param transformer a non-null instance of a transformer
   */
  public TraceManager(TransformerImpl transformer)
  {
    m_transformer = transformer;
  }

  /**
   * List of listeners who are interested in tracing what's
   * being generated.
   */
  private Vector m_traceListeners = null;

  /**
   * Add a trace listener for the purposes of debugging and diagnosis.
   * @param tl Trace listener to be added.
   *
   * @throws TooManyListenersException
   */
  public void addTraceListener(TraceListener tl)
          throws TooManyListenersException
  {

    TransformerImpl.S_DEBUG = true;

    if (null == m_traceListeners)
      m_traceListeners = new Vector();

    m_traceListeners.addElement(tl);
  }

  /**
   * Remove a trace listener.
   * @param tl Trace listener to be removed.
   */
  public void removeTraceListener(TraceListener tl)
  {

    if (null != m_traceListeners)
    {
      m_traceListeners.removeElement(tl);
    }
  }

  /**
   * Fire a generate event.
   *
   * @param te Generate Event to fire
   */
  public void fireGenerateEvent(GenerateEvent te)
  {

    if (null != m_traceListeners)
    {
      int nListeners = m_traceListeners.size();

      for (int i = 0; i < nListeners; i++)
      {
        TraceListener tl = (TraceListener) m_traceListeners.elementAt(i);

        tl.generated(te);
      }
    }
  }

  /**
   * Tell if trace listeners are present.
   *
   * @return True if there are trace listeners
   */
  public boolean hasTraceListeners()
  {
    return (null != m_traceListeners);
  }

  /**
   * Fire a trace event.
   *
   * @param sourceNode Current source node
   * @param mode Template mode
   * @param styleNode Stylesheet template node
   */
  public void fireTraceEvent(ElemTemplateElement styleNode)
  {

    if (hasTraceListeners())
    {
      int sourceNode = m_transformer.getXPathContext().getCurrentNode();
      Node source = m_transformer.getXPathContext().getDTM(
        sourceNode).getNode(sourceNode);

      fireTraceEvent(new TracerEvent(m_transformer, source, 
                     m_transformer.getMode(),  /*sourceNode, mode,*/
                                     styleNode));
    }
  }

  /**
   * Fire a end trace event, after all children of an element have been
   * executed.
   *
   * @param sourceNode Current source node
   * @param mode Template mode
   * @param styleNode Stylesheet template node
   */
  public void fireTraceEndEvent(ElemTemplateElement styleNode)
  {

    if (hasTraceListeners())
    {
      int sourceNode = m_transformer.getXPathContext().getCurrentNode();
      Node source = m_transformer.getXPathContext().getDTM(
        sourceNode).getNode(sourceNode);

      fireTraceEndEvent(new TracerEvent(m_transformer, source,
                     m_transformer.getMode(),  /*sourceNode, mode,*/
                                     styleNode));
    }
  }

  /**
   * Fire a trace event.
   *
   * @param te Trace event to fire
   */
  public void fireTraceEndEvent(TracerEvent te)
  {

    if (hasTraceListeners())
    {
      int nListeners = m_traceListeners.size();

      for (int i = 0; i < nListeners; i++)
      {
        TraceListener tl = (TraceListener) m_traceListeners.elementAt(i);
        if(tl instanceof TraceListenerEx2)
        {
          ((TraceListenerEx2)tl).traceEnd(te);
        }
      }
    }
  }



  /**
   * Fire a trace event.
   *
   * @param te Trace event to fire
   */
  public void fireTraceEvent(TracerEvent te)
  {

    if (hasTraceListeners())
    {
      int nListeners = m_traceListeners.size();

      for (int i = 0; i < nListeners; i++)
      {
        TraceListener tl = (TraceListener) m_traceListeners.elementAt(i);

        tl.trace(te);
      }
    }
  }

  /**
   * Fire a selection event.
   *
   * @param sourceNode Current source node
   * @param styleNode node in the style tree reference for the event.
   * @param attributeName The attribute name from which the selection is made.
   * @param xpath The XPath that executed the selection.
   * @param selection The result of the selection.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void fireSelectedEvent(
          int sourceNode, ElemTemplateElement styleNode, String attributeName, 
          XPath xpath, XObject selection)
            throws javax.xml.transform.TransformerException
  {

    if (hasTraceListeners())
    {
      Node source = m_transformer.getXPathContext().getDTM(
        sourceNode).getNode(sourceNode);
        
      fireSelectedEvent(new SelectionEvent(m_transformer, source, styleNode,
                                           attributeName, xpath, selection));
    }
  }
  
  /**
   * Fire a selection event.
   *
   * @param sourceNode Current source node
   * @param styleNode node in the style tree reference for the event.
   * @param attributeName The attribute name from which the selection is made.
   * @param xpath The XPath that executed the selection.
   * @param selection The result of the selection.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void fireSelectedEndEvent(
          int sourceNode, ElemTemplateElement styleNode, String attributeName, 
          XPath xpath, XObject selection)
            throws javax.xml.transform.TransformerException
  {

    if (hasTraceListeners())
    {
      Node source = m_transformer.getXPathContext().getDTM(
        sourceNode).getNode(sourceNode);
        
      fireSelectedEndEvent(new EndSelectionEvent(m_transformer, source, styleNode,
                                           attributeName, xpath, selection));
    }
  }
  
  /**
   * Fire a selection event.
   *
   * @param se Selection event to fire
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void fireSelectedEndEvent(EndSelectionEvent se)
          throws javax.xml.transform.TransformerException
  {

    if (hasTraceListeners())
    {
      int nListeners = m_traceListeners.size();

      for (int i = 0; i < nListeners; i++)
      {
        TraceListener tl = (TraceListener) m_traceListeners.elementAt(i);

        if(tl instanceof TraceListenerEx)
          ((TraceListenerEx)tl).selectEnd(se);
      }
    }
  }

  /**
   * Fire a selection event.
   *
   * @param se Selection event to fire
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void fireSelectedEvent(SelectionEvent se)
          throws javax.xml.transform.TransformerException
  {

    if (hasTraceListeners())
    {
      int nListeners = m_traceListeners.size();

      for (int i = 0; i < nListeners; i++)
      {
        TraceListener tl = (TraceListener) m_traceListeners.elementAt(i);

        tl.selected(se);
      }
    }
  }
}
