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

import java.io.Serializable;
import java.util.Vector;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;
import org.w3c.dom.Node;

/**
 * <meta name="usage" content="advanced"/>
 * The XPath class wraps an expression object and provides general services 
 * for execution of that expression.
 */
public class XPath implements Serializable, ExpressionOwner
{

  /** The top of the expression tree. 
   *  @serial */
  private Expression m_mainExp;

  /**
   * Get the raw Expression object that this class wraps.
   *
   *
   * @return the raw Expression object, which should not normally be null.
   */
  public Expression getExpression()
  {
    return m_mainExp;
  }
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    m_mainExp.fixupVariables(vars, globalsSize);
  }

  /**
   * Set the raw expression object for this object.
   *
   *
   * @param exp the raw Expression object, which should not normally be null.
   */
  public void setExpression(Expression exp)
  {
  	if(null != m_mainExp)
    	exp.exprSetParent(m_mainExp.exprGetParent()); // a bit bogus
    m_mainExp = exp;
  }

  /**
   * Get the SourceLocator on the expression object.
   *
   *
   * @return the SourceLocator on the expression object, which may be null.
   */
  public SourceLocator getLocator()
  {
    return m_mainExp;
  }

//  /**
//   * Set the SourceLocator on the expression object.
//   *
//   *
//   * @param l the SourceLocator on the expression object, which may be null.
//   */
//  public void setLocator(SourceLocator l)
//  {
//    // Note potential hazards -- l may not be serializable, or may be changed
//      // after being assigned here.
//    m_mainExp.setSourceLocator(l);
//  }

  /** The pattern string, mainly kept around for diagnostic purposes.
   *  @serial  */
  String m_patternString;

  /**
   * Return the XPath string associated with this object.
   *
   *
   * @return the XPath string associated with this object.
   */
  public String getPatternString()
  {
    return m_patternString;
  }

  /** Represents a select type expression. */
  public static final int SELECT = 0;

  /** Represents a match type expression.  */
  public static final int MATCH = 1;

  /**
   * Construct an XPath object.  
   *
   * (Needs review -sc) This method initializes an XPathParser/
   * Compiler and compiles the expression.
   * @param exprString The XPath expression.
   * @param locator The location of the expression, may be null.
   * @param prefixResolver A prefix resolver to use to resolve prefixes to 
   *                       namespace URIs.
   * @param type one of {@link #SELECT} or {@link #MATCH}.
   * @param errorListener The error listener, or null if default should be used.
   *
   * @throws javax.xml.transform.TransformerException if syntax or other error.
   */
  public XPath(
          String exprString, SourceLocator locator, PrefixResolver prefixResolver, int type,
          ErrorListener errorListener)
            throws javax.xml.transform.TransformerException
  {      
    if(null == errorListener)
      errorListener = new org.apache.xml.utils.DefaultErrorHandler();
    
    m_patternString = exprString;

    XPathParser parser = new XPathParser(errorListener, locator);
    Compiler compiler = new Compiler(errorListener, locator);

    if (SELECT == type)
      parser.initXPath(compiler, exprString, prefixResolver);
    else if (MATCH == type)
      parser.initMatchPattern(compiler, exprString, prefixResolver);
    else
      throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_DEAL_XPATH_TYPE, new Object[]{Integer.toString(type)})); //"Can not deal with XPath type: " + type);

    // System.out.println("----------------");
    Expression expr = compiler.compile(0);

    // System.out.println("expr: "+expr);
    this.setExpression(expr);
    
    if((null != locator) && locator instanceof ExpressionNode)
    {
    	expr.exprSetParent((ExpressionNode)locator);
    }

  }
  
  /**
   * Construct an XPath object.  
   *
   * (Needs review -sc) This method initializes an XPathParser/
   * Compiler and compiles the expression.
   * @param exprString The XPath expression.
   * @param locator The location of the expression, may be null.
   * @param prefixResolver A prefix resolver to use to resolve prefixes to 
   *                       namespace URIs.
   * @param type one of {@link #SELECT} or {@link #MATCH}.
   *
   * @throws javax.xml.transform.TransformerException if syntax or other error.
   */
  public XPath(
          String exprString, SourceLocator locator, PrefixResolver prefixResolver, int type)
            throws javax.xml.transform.TransformerException
  {  
    this(exprString, locator, prefixResolver, type, null);    
  }

  /**
   * Construct an XPath object.
   *
   * @param expr The Expression object.
   *
   * @throws javax.xml.transform.TransformerException if syntax or other error.
   */
  public XPath(Expression expr)
  {  
    this.setExpression(expr);   
  }
  
  /**
   * <meta name="usage" content="experimental"/>
   * Given an expression and a context, evaluate the XPath
   * and return the result.
   * 
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the
   * XPath are supposed to be expanded.
   *
   * @return The result of the XPath or null if callbacks are used.
   * @throws TransformerException thrown if
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(
          XPathContext xctxt, org.w3c.dom.Node contextNode, 
          PrefixResolver namespaceContext)
            throws javax.xml.transform.TransformerException
  {
    return execute(
          xctxt, xctxt.getDTMHandleFromNode(contextNode), 
          namespaceContext);
  }
  

  /**
   * <meta name="usage" content="experimental"/>
   * Given an expression and a context, evaluate the XPath
   * and return the result.
   * 
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the
   * XPath are supposed to be expanded.
   * 
   * @throws TransformerException thrown if the active ProblemListener decides
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(
          XPathContext xctxt, int contextNode, PrefixResolver namespaceContext)
            throws javax.xml.transform.TransformerException
  {

    xctxt.pushNamespaceContext(namespaceContext);

    xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);

    XObject xobj = null;

    try
    {
      xobj = m_mainExp.execute(xctxt);
    }
    catch (TransformerException te)
    {
      te.setLocator(this.getLocator());
      ErrorListener el = xctxt.getErrorListener();
      if(null != el) // defensive, should never happen.
      {
        el.error(te);
      }
      else
        throw te;
    }
    catch (Exception e)
    {
      while (e instanceof org.apache.xml.utils.WrappedRuntimeException)
      {
        e = ((org.apache.xml.utils.WrappedRuntimeException) e).getException();
      }
      // e.printStackTrace();

      String msg = e.getMessage();
      
      if (msg == null || msg.length() == 0) {
           msg = XSLMessages.createXPATHMessage(
               XPATHErrorResources.ER_XPATH_ERROR, null);
     
      }  
      TransformerException te = new TransformerException(msg,
              getLocator(), e);
      ErrorListener el = xctxt.getErrorListener();
      // te.printStackTrace();
      if(null != el) // defensive, should never happen.
      {
        el.fatalError(te);
      }
      else
        throw te;
    }
    finally
    {
      xctxt.popNamespaceContext();

      xctxt.popCurrentNodeAndExpression();
    }

    return xobj;
  }
  
  /**
   * <meta name="usage" content="experimental"/>
   * Given an expression and a context, evaluate the XPath
   * and return the result.
   * 
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the
   * XPath are supposed to be expanded.
   * 
   * @throws TransformerException thrown if the active ProblemListener decides
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean bool(
          XPathContext xctxt, int contextNode, PrefixResolver namespaceContext)
            throws javax.xml.transform.TransformerException
  {

    xctxt.pushNamespaceContext(namespaceContext);

    xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);

    try
    {
      return m_mainExp.bool(xctxt);
    }
    catch (TransformerException te)
    {
      te.setLocator(this.getLocator());
      ErrorListener el = xctxt.getErrorListener();
      if(null != el) // defensive, should never happen.
      {
        el.error(te);
      }
      else
        throw te;
    }
    catch (Exception e)
    {
      while (e instanceof org.apache.xml.utils.WrappedRuntimeException)
      {
        e = ((org.apache.xml.utils.WrappedRuntimeException) e).getException();
      }
      // e.printStackTrace();

      String msg = e.getMessage();
      
      if (msg == null || msg.length() == 0) {
           msg = XSLMessages.createXPATHMessage(
               XPATHErrorResources.ER_XPATH_ERROR, null);
     
      }        
      
      TransformerException te = new TransformerException(msg,
              getLocator(), e);
      ErrorListener el = xctxt.getErrorListener();
      // te.printStackTrace();
      if(null != el) // defensive, should never happen.
      {
        el.fatalError(te);
      }
      else
        throw te;
    }
    finally
    {
      xctxt.popNamespaceContext();

      xctxt.popCurrentNodeAndExpression();
    }

    return false;
  }

  /** Set to true to get diagnostic messages about the result of 
   *  match pattern testing.  */
  private static final boolean DEBUG_MATCHES = false;

  /**
   * Get the match score of the given node.
   *
   * @param xctxt XPath runtime context.
   * @param context The current source tree context node.
   * 
   * @return score, one of {@link #MATCH_SCORE_NODETEST},
   * {@link #MATCH_SCORE_NONE}, {@link #MATCH_SCORE_OTHER}, 
   * or {@link #MATCH_SCORE_QNAME}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double getMatchScore(XPathContext xctxt, int context)
          throws javax.xml.transform.TransformerException
  {

    xctxt.pushCurrentNode(context);
    xctxt.pushCurrentExpressionNode(context);

    try
    {
      XObject score = m_mainExp.execute(xctxt);

      if (DEBUG_MATCHES)
      {
        DTM dtm = xctxt.getDTM(context);
        System.out.println("score: " + score.num() + " for "
                           + dtm.getNodeName(context) + " for xpath "
                           + this.getPatternString());
      }

      return score.num();
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.popCurrentExpressionNode();
    }

    // return XPath.MATCH_SCORE_NONE;
  }

  /**
   * Install a built-in function.
   * @param name The unqualified name of the function; not currently used.
   * @param funcIndex The index of the function in the table.
   * @param func An Implementation of an XPath Function object.
   * @return the position of the function in the internal index.
   */
  public void installFunction(String name, int funcIndex, Function func)
  {
    FunctionTable.installFunction(func, funcIndex);
  }

  /**
   * Warn the user of an problem.
   *
   * @param xctxt The XPath runtime context.
   * @param sourceNode Not used.
   * @param msg An error msgkey that corresponds to one of the constants found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  public void warn(
          XPathContext xctxt, int sourceNode, String msg, Object[] args)
            throws javax.xml.transform.TransformerException
  {

    String fmsg = XSLMessages.createXPATHWarning(msg, args);
    ErrorListener ehandler = xctxt.getErrorListener();

    if (null != ehandler)
    {

      // TO DO: Need to get stylesheet Locator from here.
      ehandler.warning(new TransformerException(fmsg, (SAXSourceLocator)xctxt.getSAXLocator()));
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an
   * exception.
   *
   * @param b  If false, a runtime exception will be thrown.
   * @param msg The assertion message, which should be informative.
   * 
   * @throws RuntimeException if the b argument is false.
   */
  public void assertion(boolean b, String msg)
  {

    if (!b)
    {
      String fMsg = XSLMessages.createXPATHMessage(
        XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION,
        new Object[]{ msg });

      throw new RuntimeException(fMsg);
    }
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param xctxt The XPath runtime context.
   * @param sourceNode Not used.
   * @param msg An error msgkey that corresponds to one of the constants found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  public void error(
          XPathContext xctxt, int sourceNode, String msg, Object[] args)
            throws javax.xml.transform.TransformerException
  {

    String fmsg = XSLMessages.createXPATHMessage(msg, args);
    ErrorListener ehandler = xctxt.getErrorListener();

    if (null != ehandler)
    {
      ehandler.fatalError(new TransformerException(fmsg,
                              (SAXSourceLocator)xctxt.getSAXLocator()));
    }
    else
    {
      SourceLocator slocator = xctxt.getSAXLocator();
      System.out.println(fmsg + "; file " + slocator.getSystemId()
                         + "; line " + slocator.getLineNumber() + "; column "
                         + slocator.getColumnNumber());
    }
  }
  
  /**
   * This will traverse the heararchy, calling the visitor for 
   * each member.  If the called visitor method returns 
   * false, the subtree should not be called.
   * 
   * @param owner The owner of the visitor, where that path may be 
   *              rewritten if needed.
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	m_mainExp.callVisitors(this, visitor);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if no match is made.
   */
  public static final double MATCH_SCORE_NONE = Double.NEGATIVE_INFINITY;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern has the form
   * of a QName optionally preceded by an @ character.
   */
  public static final double MATCH_SCORE_QNAME = 0.0;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern pattern has the form NCName:*.
   */
  public static final double MATCH_SCORE_NSWILD = -0.25;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern consists of just a NodeTest.
   */
  public static final double MATCH_SCORE_NODETEST = -0.5;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern consists of something
   * other than just a NodeTest or just a qname.
   */
  public static final double MATCH_SCORE_OTHER = 0.5;
}
