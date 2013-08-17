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
package org.apache.xpath;

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;


import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="internal"/>
 * Defines a class to keep track of a stack for
 * template arguments and variables.
 *
 * <p>This has been changed from the previous incarnations of this
 * class to be fairly low level.</p>
 */
public final class VariableStack implements Cloneable
{

  /**
   * Constructor for a variable stack.
   */
  public VariableStack()
  {
    reset();
  }

  /**
   * Returns a clone of this variable stack.
   *
   * @return  a clone of this variable stack.
   *
   * @throws CloneNotSupportedException
   */
  public synchronized Object clone() throws CloneNotSupportedException
  {

    VariableStack vs = (VariableStack) super.clone();

    // I *think* I can get away with a shallow clone here?
    vs._sf = (XObject[]) _sf.clone();
    vs._links = (int[]) _links.clone();

    return vs;
  }

  /**
   * The stack frame where all variables and params will be kept.
   * @serial
   */
  XObject[] _sf = new XObject[XPathContext.RECURSIONLIMIT * 2];

  /**
   * The top of the stack frame (<code>_sf</code>).
   * @serial
   */
  int _top;

  /**
   * The bottom index of the current frame (relative to <code>_sf</code>).
   * @serial
   */
  private int _cfb;

  /**
   * The stack of frame positions.  I call 'em links because of distant
   * <a href="http://math.millikin.edu/mprogers/Courses/currentCourses/CS481-ComputerArchitecture/cs481.Motorola68000.html">
   * Motorola 68000 assembler</a> memories.  :-)
   * @serial
   */
  int[] _links = new int[XPathContext.RECURSIONLIMIT];

  /**
   * The top of the links stack.
   */
  int _linksTop;

  /**
   * Get the element at the given index, regardless of stackframe.
   *
   * @param i index from zero.
   *
   * @return The item at the given index.
   */
  public final XObject elementAt(final int i)
  {
    return _sf[i];
  }

  /**
   * Get size of the stack.
   *
   * @return the total size of the execution stack.
   */
  public final int size()
  {
    return _top;
  }

  /**
   * Reset the stack to a start position.
   *
   * @return the total size of the execution stack.
   */
  public final void reset()
  {

    _top = 0;
    _linksTop = 0;

    // Adding one here to the stack of frame positions will allow us always 
    // to look one under without having to check if we're at zero.
    // (As long as the caller doesn't screw up link/unlink.)
    _links[_linksTop++] = 0;
  }

  /**
   * Set the current stack frame.
   *
   * @param sf The new stack frame position.
   */
  public final void setStackFrame(int sf)
  {
    _cfb = sf;
  }

  /**
   * Get the position from where the search should start,
   * which is either the searchStart property, or the top
   * of the stack if that value is -1.
   *
   * @return The current stack frame position.
   */
  public final int getStackFrame()
  {
    return _cfb;
  }

  /**
   * Allocates memory (called a stackframe) on the stack; used to store
   * local variables and parameter arguments.
   *
   * <p>I use the link/unlink concept because of distant
   * <a href="http://math.millikin.edu/mprogers/Courses/currentCourses/CS481-ComputerArchitecture/cs481.Motorola68000.html">
   * Motorola 68000 assembler</a> memories.</p>
   *
   * @param size The size of the stack frame allocation.  This ammount should
   * normally be the maximum number of variables that you can have allocated
   * at one time in the new stack frame.
   *
   * @return The bottom of the stack frame, from where local variable addressing
   * should start from.
   */
  public final int link(final int size)
  {

    _cfb = _top;
    _top += size;

    if (_top >= _sf.length)
    {
      XObject newsf[] = new XObject[_sf.length + (1024 * 4) + size];

      System.arraycopy(_sf, 0, newsf, 0, _sf.length);

      _sf = newsf;
    }

    if (_linksTop + 1 >= _links.length)
    {
      int newlinks[] = new int[_links.length + (1024 * 2)];

      System.arraycopy(_links, 0, newlinks, 0, _links.length);

      _links = newlinks;
    }

    _links[_linksTop++] = _cfb;

    return _cfb;
  }

  /**
   * Free up the stack frame that was last allocated with
   * {@link link(int size)}.
   */
  public final void unlink()
  {
    _top = _links[--_linksTop];
    _cfb = _links[_linksTop - 1];
  }

  /**
   * Set a local variable or parameter in the current stack frame.
   *
   *
   * @param index Local variable index relative to the current stack
   * frame bottom.
   *
   * @param val The value of the variable that is being set.
   */
  public final void setLocalVariable(int index, XObject val)
  {
    _sf[index + _cfb] = val;
  }

  /**
   * Set a local variable or parameter in the specified stack frame.
   *
   *
   * @param index Local variable index relative to the current stack
   * frame bottom.
   * NEEDSDOC @param stackFrame
   *
   * @param val The value of the variable that is being set.
   */
  public final void setLocalVariable(int index, XObject val, int stackFrame)
  {
    _sf[index + stackFrame] = val;
  }

  /**
   * Get a local variable or parameter in the current stack frame.
   *
   *
   * @param xctxt The XPath context, which must be passed in order to
   * lazy evaluate variables.
   *
   * @param index Local variable index relative to the current stack
   * frame bottom.
   *
   * @return The value of the variable.
   *
   * @throws TransformerException
   */
  public final XObject getLocalVariable(XPathContext xctxt, int index)
          throws TransformerException
  {

    index += _cfb;

    XObject val = _sf[index];
    
    if(null == val)
      throw new TransformerException("Variable accessed before it is bound!", xctxt.getSAXLocator());

    // Lazy execution of variables.
    if (val.getType() == XObject.CLASS_UNRESOLVEDVARIABLE)
      return (_sf[index] = val.execute(xctxt));

    return val;
  }

  /**
   * Get a local variable or parameter in the current stack frame.
   *
   *
   * @param index Local variable index relative to the given
   * frame bottom.
   * NEEDSDOC @param frame
   *
   * @return The value of the variable.
   *
   * @throws TransformerException
   */
  public final XObject getLocalVariable(int index, int frame)
          throws TransformerException
  {

    index += frame;

    XObject val = _sf[index];

    return val;
  }

  /**
   * Tell if a local variable has been set or not.
   *
   * @param index Local variable index relative to the current stack
   * frame bottom.
   *
   * @return true if the value at the index is not null.
   *
   * @throws TransformerException
   */
  public final boolean isLocalSet(int index) throws TransformerException
  {
    return (_sf[index + _cfb] != null);
  }

  /** NEEDSDOC Field m_nulls          */
  private static XObject[] m_nulls = new XObject[1024];

  /**
   * Use this to clear the variables in a section of the stack.  This is
   * used to clear the parameter section of the stack, so that default param
   * values can tell if they've already been set.  It is important to note that
   * this function has a 1K limitation.
   *
   * @param start The start position, relative to the current local stack frame.
   * @param len The number of slots to be cleared.
   */
  public final void clearLocalSlots(int start, int len)
  {

    start += _cfb;

    System.arraycopy(m_nulls, 0, _sf, start, len);
  }

  /**
   * Set a global variable or parameter in the global stack frame.
   *
   *
   * @param index Local variable index relative to the global stack frame
   * bottom.
   *
   * @param val The value of the variable that is being set.
   */
  public final void setGlobalVariable(final int index, final XObject val)
  {
    _sf[index] = val;
  }

  /**
   * Get a global variable or parameter from the global stack frame.
   *
   *
   * @param xctxt The XPath context, which must be passed in order to
   * lazy evaluate variables.
   *
   * @param index Global variable index relative to the global stack
   * frame bottom.
   *
   * @return The value of the variable.
   *
   * @throws TransformerException
   */
  public final XObject getGlobalVariable(XPathContext xctxt, final int index)
          throws TransformerException
  {

    XObject val = _sf[index];

    // Lazy execution of variables.
    if (val.getType() == XObject.CLASS_UNRESOLVEDVARIABLE)
      return (_sf[index] = val.execute(xctxt));

    return val;
  }

  /**
   * Get a variable based on it's qualified name.
   * This is for external use only.
   *
   * @param xctxt The XPath context, which must be passed in order to
   * lazy evaluate variables.
   * 
   * @param qname The qualified name of the variable.
   *
   * @return The evaluated value of the variable.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public final XObject getVariableOrParam(
          XPathContext xctxt, org.apache.xml.utils.QName qname)
            throws javax.xml.transform.TransformerException
  {

    org.apache.xml.utils.PrefixResolver prefixResolver =
      xctxt.getNamespaceContext();

    // Get the current ElemTemplateElement, which must be pushed in as the 
    // prefix resolver, and then walk backwards in document order, searching 
    // for an xsl:param element or xsl:variable element that matches our 
    // qname.  If we reach the top level, use the StylesheetRoot's composed
    // list of top level variables and parameters.

    if (prefixResolver instanceof org.apache.xalan.templates.ElemTemplateElement)
    {
      
      org.apache.xalan.templates.ElemVariable vvar;

      org.apache.xalan.templates.ElemTemplateElement prev =
        (org.apache.xalan.templates.ElemTemplateElement) prefixResolver;

      if (!(prev instanceof org.apache.xalan.templates.Stylesheet))
      {
        while ( !(prev.getParentNode() instanceof org.apache.xalan.templates.Stylesheet) )
        {
          org.apache.xalan.templates.ElemTemplateElement savedprev = prev;

          while (null != (prev = prev.getPreviousSiblingElem()))
          {
            if (prev instanceof org.apache.xalan.templates.ElemVariable)
            {
              vvar = (org.apache.xalan.templates.ElemVariable) prev;

              if (vvar.getName().equals(qname))
                return getLocalVariable(xctxt, vvar.getIndex());
            }
          }
          prev = savedprev.getParentElem();
        }
      }

      vvar = prev.getStylesheetRoot().getVariableOrParamComposed(qname);
      if (null != vvar)
        return getGlobalVariable(xctxt, vvar.getIndex());
    }

    throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_VAR_NOT_RESOLVABLE, new Object[]{qname.toString()})); //"Variable not resolvable: " + qname);
  }
}  // end VariableStack

