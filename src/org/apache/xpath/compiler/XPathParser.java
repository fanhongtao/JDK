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
package org.apache.xpath.compiler;

import java.util.Vector;
import java.util.Hashtable;

import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XNumber;
import org.apache.xalan.res.XSLMessages;

import javax.xml.transform.TransformerException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.ErrorListener;

/**
 * <meta name="usage" content="general"/>
 * Tokenizes and parses XPath expressions. This should really be named
 * XPathParserImpl, and may be renamed in the future.
 */
public class XPathParser
{

  /**
   * The XPath to be processed.
   */
  private OpMap m_ops;

  /**
   * The next token in the pattern.
   */
  transient String m_token;

  /**
   * The first char in m_token, the theory being that this
   * is an optimization because we won't have to do charAt(0) as
   * often.
   */
  transient char m_tokenChar = 0;

  /**
   * The position in the token queue is tracked by m_queueMark.
   */
  int m_queueMark = 0;

  /**
   * The parser constructor.
   */
  public XPathParser(ErrorListener errorListener, javax.xml.transform.SourceLocator sourceLocator)
  {
    m_errorListener = errorListener;
    m_sourceLocator = sourceLocator;
  }

  /**
   * The prefix resolver to map prefixes to namespaces in the OpMap.
   */
  PrefixResolver m_namespaceContext;

  /**
   * Given an string, init an XPath object for selections,
   * in order that a parse doesn't
   * have to be done each time the expression is evaluated.
   * 
   * @param compiler The compiler object.
   * @param expression A string conforming to the XPath grammar.
   * @param namespaceContext An object that is able to resolve prefixes in
   * the XPath to namespaces.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void initXPath(
          Compiler compiler, String expression, PrefixResolver namespaceContext)
            throws javax.xml.transform.TransformerException
  {

    m_ops = compiler;
    m_namespaceContext = namespaceContext;

    Lexer lexer = new Lexer(compiler, namespaceContext, this);

    lexer.tokenize(expression);

    m_ops.m_opMap[0] = OpCodes.OP_XPATH;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] = 2;

    nextToken();
    Expr();

    if (null != m_token)
    {
      String extraTokens = "";

      while (null != m_token)
      {
        extraTokens += "'" + m_token + "'";

        nextToken();

        if (null != m_token)
          extraTokens += ", ";
      }

      error(XPATHErrorResources.ER_EXTRA_ILLEGAL_TOKENS,
            new Object[]{ extraTokens });  //"Extra illegal tokens: "+extraTokens);
    }

    compiler.shrink();
  }

  /**
   * Given an string, init an XPath object for pattern matches,
   * in order that a parse doesn't
   * have to be done each time the expression is evaluated.
   * @param compiler The XPath object to be initialized.
   * @param expression A String representing the XPath.
   * @param namespaceContext An object that is able to resolve prefixes in
   * the XPath to namespaces.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void initMatchPattern(
          Compiler compiler, String expression, PrefixResolver namespaceContext)
            throws javax.xml.transform.TransformerException
  {

    m_ops = compiler;
    m_namespaceContext = namespaceContext;

    Lexer lexer = new Lexer(compiler, namespaceContext, this);

    lexer.tokenize(expression);

    m_ops.m_opMap[0] = OpCodes.OP_MATCHPATTERN;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] = 2;

    nextToken();
    Pattern();

    if (null != m_token)
    {
      String extraTokens = "";

      while (null != m_token)
      {
        extraTokens += "'" + m_token + "'";

        nextToken();

        if (null != m_token)
          extraTokens += ", ";
      }

      error(XPATHErrorResources.ER_EXTRA_ILLEGAL_TOKENS,
            new Object[]{ extraTokens });  //"Extra illegal tokens: "+extraTokens);
    }

    // Terminate for safety.
    m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = OpCodes.ENDOP;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

    m_ops.shrink();
  }

  /** The error listener where syntax errors are to be sent.
   */
  private ErrorListener m_errorListener;
  
  /** The source location of the XPath. */
  javax.xml.transform.SourceLocator m_sourceLocator;

  /**
   * Allow an application to register an error event handler, where syntax 
   * errors will be sent.  If the error listener is not set, syntax errors 
   * will be sent to System.err.
   * 
   * @param handler Reference to error listener where syntax errors will be 
   *                sent.
   */
  public void setErrorHandler(ErrorListener handler)
  {
    m_errorListener = handler;
  }

  /**
   * Return the current error listener.
   *
   * @return The error listener, which should not normally be null, but may be.
   */
  public ErrorListener getErrorListener()
  {
    return m_errorListener;
  }

  /**
   * Check whether m_token matches the target string. 
   *
   * @param s A string reference or null.
   *
   * @return If m_token is null, returns false (or true if s is also null), or 
   * return true if the current token matches the string, else false.
   */
  final boolean tokenIs(String s)
  {
    return (m_token != null) ? (m_token.equals(s)) : (s == null);
  }

  /**
   * Check whether m_tokenChar==c. 
   *
   * @param c A character to be tested.
   *
   * @return If m_token is null, returns false, or return true if c matches 
   *         the current token.
   */
  final boolean tokenIs(char c)
  {
    return (m_token != null) ? (m_tokenChar == c) : false;
  }

  /**
   * Look ahead of the current token in order to
   * make a branching decision.
   *
   * @param c the character to be tested for.
   * @param n number of tokens to look ahead.  Must be
   * greater than 1.
   *
   * @return true if the next token matches the character argument.
   */
  final boolean lookahead(char c, int n)
  {

    int pos = (m_queueMark + n);
    boolean b;

    if ((pos <= m_ops.m_tokenQueueSize) && (pos > 0)
            && (m_ops.m_tokenQueueSize != 0))
    {
      String tok = ((String) m_ops.m_tokenQueue[pos - 1]);

      b = (tok.length() == 1) ? (tok.charAt(0) == c) : false;
    }
    else
    {
      b = false;
    }

    return b;
  }

  /**
   * Look behind the first character of the current token in order to
   * make a branching decision.
   * 
   * @param c the character to compare it to.
   * @param n number of tokens to look behind.  Must be
   * greater than 1.  Note that the look behind terminates
   * at either the beginning of the string or on a '|'
   * character.  Because of this, this method should only
   * be used for pattern matching.
   *
   * @return true if the token behind the current token matches the character 
   *         argument.
   */
  private final boolean lookbehind(char c, int n)
  {

    boolean isToken;
    int lookBehindPos = m_queueMark - (n + 1);

    if (lookBehindPos >= 0)
    {
      String lookbehind = (String) m_ops.m_tokenQueue[lookBehindPos];

      if (lookbehind.length() == 1)
      {
        char c0 = (lookbehind == null) ? '|' : lookbehind.charAt(0);

        isToken = (c0 == '|') ? false : (c0 == c);
      }
      else
      {
        isToken = false;
      }
    }
    else
    {
      isToken = false;
    }

    return isToken;
  }

  /**
   * look behind the current token in order to
   * see if there is a useable token.
   * 
   * @param n number of tokens to look behind.  Must be
   * greater than 1.  Note that the look behind terminates
   * at either the beginning of the string or on a '|'
   * character.  Because of this, this method should only
   * be used for pattern matching.
   * 
   * @return true if look behind has a token, false otherwise.
   */
  private final boolean lookbehindHasToken(int n)
  {

    boolean hasToken;

    if ((m_queueMark - n) > 0)
    {
      String lookbehind = (String) m_ops.m_tokenQueue[m_queueMark - (n - 1)];
      char c0 = (lookbehind == null) ? '|' : lookbehind.charAt(0);

      hasToken = (c0 == '|') ? false : true;
    }
    else
    {
      hasToken = false;
    }

    return hasToken;
  }

  /**
   * Look ahead of the current token in order to
   * make a branching decision.
   * 
   * @param s the string to compare it to.
   * @param n number of tokens to lookahead.  Must be
   * greater than 1.
   *
   * @return true if the token behind the current token matches the string 
   *         argument.
   */
  private final boolean lookahead(String s, int n)
  {

    boolean isToken;

    if ((m_queueMark + n) <= m_ops.m_tokenQueueSize)
    {
      String lookahead = (String) m_ops.m_tokenQueue[m_queueMark + (n - 1)];

      isToken = (lookahead != null) ? lookahead.equals(s) : (s == null);
    }
    else
    {
      isToken = (null == s);
    }

    return isToken;
  }

  /**
   * Retrieve the next token from the command and
   * store it in m_token string.
   */
  private final void nextToken()
  {

    if (m_queueMark < m_ops.m_tokenQueueSize)
    {
      m_token = (String) m_ops.m_tokenQueue[m_queueMark++];
      m_tokenChar = m_token.charAt(0);
    }
    else
    {
      m_token = null;
      m_tokenChar = 0;
    }
  }

  /**
   * Retrieve a token relative to the current token.
   * 
   * @param i Position relative to current token.
   *
   * @return The string at the given index, or null if the index is out 
   *         of range.
   */
  private final String getTokenRelative(int i)
  {

    String tok;
    int relative = m_queueMark + i;

    if ((relative > 0) && (relative < m_ops.m_tokenQueueSize))
    {
      tok = (String) m_ops.m_tokenQueue[relative];
    }
    else
    {
      tok = null;
    }

    return tok;
  }

  /**
   * Retrieve the previous token from the command and
   * store it in m_token string.
   */
  private final void prevToken()
  {

    if (m_queueMark > 0)
    {
      m_queueMark--;

      m_token = (String) m_ops.m_tokenQueue[m_queueMark];
      m_tokenChar = m_token.charAt(0);
    }
    else
    {
      m_token = null;
      m_tokenChar = 0;
    }
  }

  /**
   * Consume an expected token, throwing an exception if it
   * isn't there.
   *
   * @param expected The string to be expected.
   *
   * @throws javax.xml.transform.TransformerException
   */
  private final void consumeExpected(String expected)
          throws javax.xml.transform.TransformerException
  {

    if (tokenIs(expected))
    {
      nextToken();
    }
    else
    {
      error(XPATHErrorResources.ER_EXPECTED_BUT_FOUND, new Object[]{ expected,
                                                                     m_token });  //"Expected "+expected+", but found: "+m_token);
    }
  }

  /**
   * Consume an expected token, throwing an exception if it
   * isn't there.
   *
   * @param expected the character to be expected.
   *
   * @throws javax.xml.transform.TransformerException
   */
  private final void consumeExpected(char expected)
          throws javax.xml.transform.TransformerException
  {

    if (tokenIs(expected))
    {
      nextToken();
    }
    else
    {
      error(XPATHErrorResources.ER_EXPECTED_BUT_FOUND,
            new Object[]{ String.valueOf(expected),
                          m_token });  //"Expected "+expected+", but found: "+m_token);
    }
  }

  /**
   * Warn the user of a problem.
   *
   * @param msg An error number that corresponds to one of the numbers found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  void warn(int msg, Object[] args) throws TransformerException
  {

    String fmsg = XSLMessages.createXPATHWarning(msg, args);
    ErrorListener ehandler = this.getErrorListener();

    if (null != ehandler)
    {
      // TO DO: Need to get stylesheet Locator from here.
      ehandler.warning(new TransformerException(fmsg, m_sourceLocator));
    }
    else
    {
      // Should never happen.
      System.err.println(fmsg);
    }
  }

  /**
   * Notify the user of an assertion error, and probably throw an
   * exception.
   *
   * @param b  If false, a runtime exception will be thrown.
   * @param msg The assertion message, which should be informative.
   * 
   * @throws RuntimeException if the b argument is false.
   */
  private void assertion(boolean b, String msg)
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
   * Notify the user of an error, and probably throw an
   * exception.
   *
   * @param msg An error number that corresponds to one of the numbers found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  void error(int msg, Object[] args) throws TransformerException
  {

    String fmsg = XSLMessages.createXPATHMessage(msg, args);
    ErrorListener ehandler = this.getErrorListener();

    TransformerException te = new TransformerException(fmsg, m_sourceLocator);
    if (null != ehandler)
    {
      // TO DO: Need to get stylesheet Locator from here.
      ehandler.fatalError(te);
    }
    else
    {
      // System.err.println(fmsg);
      throw te;
    }
  }

  /**
   * Dump the remaining token queue.
   * Thanks to Craig for this.
   *
   * @return A dump of the remaining token queue, which may be appended to 
   *         an error message.
   */
  protected String dumpRemainingTokenQueue()
  {

    int q = m_queueMark;
    String returnMsg;

    if (q < m_ops.m_tokenQueueSize)
    {
      String msg = "\n Remaining tokens: (";

      while (q < m_ops.m_tokenQueueSize)
      {
        String t = (String) m_ops.m_tokenQueue[q++];

        msg += (" '" + t + "'");
      }

      returnMsg = msg + ")";
    }
    else
    {
      returnMsg = "";
    }

    return returnMsg;
  }

  /**
   * Given a string, return the corresponding function token.
   *
   * @param key A local name of a function.
   *
   * @return   The function ID, which may correspond to one of the FUNC_XXX 
   *    values found in {@link org.apache.xpath.compiler.FunctionTable}, but may 
   *    be a value installed by an external module.
   */
  final int getFunctionToken(String key)
  {

    int tok;

    try
    {
      tok = ((Integer) (Keywords.m_functions.get(key))).intValue();
    }
    catch (NullPointerException npe)
    {
      tok = -1;
    }
    catch (ClassCastException cce)
    {
      tok = -1;
    }

    return tok;
  }

  /**
   * Insert room for operation.  This will NOT set
   * the length value of the operation, but will update
   * the length value for the total expression.
   *
   * @param pos The position where the op is to be inserted.
   * @param length The length of the operation space in the op map.
   * @param op The op code to the inserted.
   */
  void insertOp(int pos, int length, int op)
  {

    int totalLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    for (int i = totalLen - 1; i >= pos; i--)
    {
      m_ops.m_opMap[i + length] = m_ops.m_opMap[i];
    }

    m_ops.m_opMap[pos] = op;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] = totalLen + length;
  }

  /**
   * Insert room for operation.  This WILL set
   * the length value of the operation, and will update
   * the length value for the total expression.
   *
   * @param length The length of the operation.
   * @param op The op code to the inserted.
   */
  void appendOp(int length, int op)
  {

    int totalLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    m_ops.m_opMap[totalLen] = op;
    m_ops.m_opMap[totalLen + OpMap.MAPINDEX_LENGTH] = length;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] = totalLen + length;
  }

  // ============= EXPRESSIONS FUNCTIONS =================

  /**
   *
   *
   * Expr  ::=  OrExpr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Expr() throws javax.xml.transform.TransformerException
  {
    OrExpr();
  }

  /**
   *
   *
   * OrExpr  ::=  AndExpr
   * | OrExpr 'or' AndExpr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void OrExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    AndExpr();

    if ((null != m_token) && tokenIs("or"))
    {
      nextToken();
      insertOp(opPos, 2, OpCodes.OP_OR);
      OrExpr();

      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
    }
  }

  /**
   *
   *
   * AndExpr  ::=  EqualityExpr
   * | AndExpr 'and' EqualityExpr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void AndExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    EqualityExpr(-1);

    if ((null != m_token) && tokenIs("and"))
    {
      nextToken();
      insertOp(opPos, 2, OpCodes.OP_AND);
      AndExpr();

      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
    }
  }

  /**
   *
   * @returns an Object which is either a String, a Number, a Boolean, or a vector
   * of nodes.
   *
   * EqualityExpr  ::=  RelationalExpr
   * | EqualityExpr '=' RelationalExpr
   *
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   *
   * @return the position at the end of the equality expression.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int EqualityExpr(int addPos) throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    if (-1 == addPos)
      addPos = opPos;

    RelationalExpr(-1);

    if (null != m_token)
    {
      if (tokenIs('!') && lookahead('=', 1))
      {
        nextToken();
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_NOTEQUALS);

        int opPlusLeftHandLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - addPos;

        addPos = EqualityExpr(addPos);
        m_ops.m_opMap[addPos + OpMap.MAPINDEX_LENGTH] =
          m_ops.m_opMap[addPos + opPlusLeftHandLen + 1] + opPlusLeftHandLen;
        addPos += 2;
      }
      else if (tokenIs('='))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_EQUALS);

        int opPlusLeftHandLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - addPos;

        addPos = EqualityExpr(addPos);
        m_ops.m_opMap[addPos + OpMap.MAPINDEX_LENGTH] =
          m_ops.m_opMap[addPos + opPlusLeftHandLen + 1] + opPlusLeftHandLen;
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * .
   * @returns an Object which is either a String, a Number, a Boolean, or a vector
   * of nodes.
   *
   * RelationalExpr  ::=  AdditiveExpr
   * | RelationalExpr '<' AdditiveExpr
   * | RelationalExpr '>' AdditiveExpr
   * | RelationalExpr '<=' AdditiveExpr
   * | RelationalExpr '>=' AdditiveExpr
   *
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   *
   * @return the position at the end of the relational expression.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int RelationalExpr(int addPos) throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    if (-1 == addPos)
      addPos = opPos;

    AdditiveExpr(-1);

    if (null != m_token)
    {
      if (tokenIs('<'))
      {
        nextToken();

        if (tokenIs('='))
        {
          nextToken();
          insertOp(addPos, 2, OpCodes.OP_LTE);
        }
        else
        {
          insertOp(addPos, 2, OpCodes.OP_LT);
        }

        int opPlusLeftHandLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - addPos;

        addPos = RelationalExpr(addPos);
        m_ops.m_opMap[addPos + OpMap.MAPINDEX_LENGTH] =
          m_ops.m_opMap[addPos + opPlusLeftHandLen + 1] + opPlusLeftHandLen;
        addPos += 2;
      }
      else if (tokenIs('>'))
      {
        nextToken();

        if (tokenIs('='))
        {
          nextToken();
          insertOp(addPos, 2, OpCodes.OP_GTE);
        }
        else
        {
          insertOp(addPos, 2, OpCodes.OP_GT);
        }

        int opPlusLeftHandLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - addPos;

        addPos = RelationalExpr(addPos);
        m_ops.m_opMap[addPos + OpMap.MAPINDEX_LENGTH] =
          m_ops.m_opMap[addPos + opPlusLeftHandLen + 1] + opPlusLeftHandLen;
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * This has to handle construction of the operations so that they are evaluated
   * in pre-fix order.  So, for 9+7-6, instead of |+|9|-|7|6|, this needs to be
   * evaluated as |-|+|9|7|6|.
   *
   * AdditiveExpr  ::=  MultiplicativeExpr
   * | AdditiveExpr '+' MultiplicativeExpr
   * | AdditiveExpr '-' MultiplicativeExpr
   *
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   *
   * @return the position at the end of the equality expression.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int AdditiveExpr(int addPos) throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    if (-1 == addPos)
      addPos = opPos;

    MultiplicativeExpr(-1);

    if (null != m_token)
    {
      if (tokenIs('+'))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_PLUS);

        int opPlusLeftHandLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - addPos;

        addPos = AdditiveExpr(addPos);
        m_ops.m_opMap[addPos + OpMap.MAPINDEX_LENGTH] =
          m_ops.m_opMap[addPos + opPlusLeftHandLen + 1] + opPlusLeftHandLen;
        addPos += 2;
      }
      else if (tokenIs('-'))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_MINUS);

        int opPlusLeftHandLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - addPos;

        addPos = AdditiveExpr(addPos);
        m_ops.m_opMap[addPos + OpMap.MAPINDEX_LENGTH] =
          m_ops.m_opMap[addPos + opPlusLeftHandLen + 1] + opPlusLeftHandLen;
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * This has to handle construction of the operations so that they are evaluated
   * in pre-fix order.  So, for 9+7-6, instead of |+|9|-|7|6|, this needs to be
   * evaluated as |-|+|9|7|6|.
   *
   * MultiplicativeExpr  ::=  UnaryExpr
   * | MultiplicativeExpr MultiplyOperator UnaryExpr
   * | MultiplicativeExpr 'div' UnaryExpr
   * | MultiplicativeExpr 'mod' UnaryExpr
   * | MultiplicativeExpr 'quo' UnaryExpr
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   *
   * @return the position at the end of the equality expression.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int MultiplicativeExpr(int addPos) throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    if (-1 == addPos)
      addPos = opPos;

    UnaryExpr();

    if (null != m_token)
    {
      if (tokenIs('*'))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_MULT);

        int opPlusLeftHandLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.m_opMap[addPos + OpMap.MAPINDEX_LENGTH] =
          m_ops.m_opMap[addPos + opPlusLeftHandLen + 1] + opPlusLeftHandLen;
        addPos += 2;
      }
      else if (tokenIs("div"))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_DIV);

        int opPlusLeftHandLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.m_opMap[addPos + OpMap.MAPINDEX_LENGTH] =
          m_ops.m_opMap[addPos + opPlusLeftHandLen + 1] + opPlusLeftHandLen;
        addPos += 2;
      }
      else if (tokenIs("mod"))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_MOD);

        int opPlusLeftHandLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.m_opMap[addPos + OpMap.MAPINDEX_LENGTH] =
          m_ops.m_opMap[addPos + opPlusLeftHandLen + 1] + opPlusLeftHandLen;
        addPos += 2;
      }
      else if (tokenIs("quo"))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_QUO);

        int opPlusLeftHandLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.m_opMap[addPos + OpMap.MAPINDEX_LENGTH] =
          m_ops.m_opMap[addPos + opPlusLeftHandLen + 1] + opPlusLeftHandLen;
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   *
   * UnaryExpr  ::=  UnionExpr
   * | '-' UnaryExpr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void UnaryExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];
    boolean isNeg = false;

    if (m_tokenChar == '-')
    {
      nextToken();
      appendOp(2, OpCodes.OP_NEG);

      isNeg = true;
    }

    UnionExpr();

    if (isNeg)
      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   * StringExpr  ::=  Expr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void StringExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    appendOp(2, OpCodes.OP_STRING);
    Expr();

    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   *
   * StringExpr  ::=  Expr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void BooleanExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    appendOp(2, OpCodes.OP_BOOL);
    Expr();

    int opLen = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;

    if (opLen == 2)
    {
      error(XPATHErrorResources.ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL, null);  //"boolean(...) argument is no longer optional with 19990709 XPath draft.");
    }

    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] = opLen;
  }

  /**
   *
   *
   * NumberExpr  ::=  Expr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void NumberExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    appendOp(2, OpCodes.OP_NUMBER);
    Expr();

    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }

  /**
   * The context of the right hand side expressions is the context of the
   * left hand side expression. The results of the right hand side expressions
   * are node sets. The result of the left hand side UnionExpr is the union
   * of the results of the right hand side expressions.
   *
   *
   * UnionExpr    ::=    PathExpr
   * | UnionExpr '|' PathExpr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void UnionExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];
    boolean continueOrLoop = true;
    boolean foundUnion = false;

    do
    {
      PathExpr();

      if (tokenIs('|'))
      {
        if (false == foundUnion)
        {
          foundUnion = true;

          insertOp(opPos, 2, OpCodes.OP_UNION);
        }

        nextToken();
      }
      else
      {
        break;
      }

      // this.m_testForDocOrder = true;
    }
    while (continueOrLoop);

    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }

  /**
   * PathExpr  ::=  LocationPath
   * | FilterExpr
   * | FilterExpr '/' RelativeLocationPath
   * | FilterExpr '//' RelativeLocationPath
   *
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void PathExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];
    boolean foundLocationPath;

    FilterExpr();

    if (tokenIs('/'))
    {
      nextToken();

      // int locationPathOpPos = opPos;
      insertOp(opPos, 2, OpCodes.OP_LOCATIONPATH);
      RelativeLocationPath();

      // Terminate for safety.
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = OpCodes.ENDOP;
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;
      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
    }
  }

  /**
   *
   *
   * FilterExpr  ::=  PrimaryExpr
   * | FilterExpr Predicate
   *
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void FilterExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    // boolean isFunc = lookahead('(', 1);
    PrimaryExpr();

    if (tokenIs('['))
    {

      // int locationPathOpPos = opPos;
      insertOp(opPos, 2, OpCodes.OP_LOCATIONPATH);

      while (tokenIs('['))
      {
        Predicate();
      }

      if (tokenIs('/'))
      {
        nextToken();
        RelativeLocationPath();
      }

      // Terminate for safety.
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = OpCodes.ENDOP;
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;
      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
    }

    /*
     * if(tokenIs('['))
     * {
     *   Predicate();
     *   m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
     * }
     */
  }

  /**
   *
   * PrimaryExpr  ::=  VariableReference
   * | '(' Expr ')'
   * | Literal
   * | Number
   * | FunctionCall
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void PrimaryExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    if ((m_tokenChar == '\'') || (m_tokenChar == '"'))
    {
      appendOp(2, OpCodes.OP_LITERAL);
      Literal();

      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
    }
    else if (m_tokenChar == '$')
    {
      nextToken();  // consume '$'
      appendOp(2, OpCodes.OP_VARIABLE);
      QName();
      
      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
    }
    else if (m_tokenChar == '(')
    {
      nextToken();
      appendOp(2, OpCodes.OP_GROUP);
      Expr();
      consumeExpected(')');

      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
    }
    else if ((null != m_token) && ((('.' == m_tokenChar) && (m_token.length() > 1) && Character.isDigit(
            m_token.charAt(1))) || Character.isDigit(m_tokenChar)))
    {
      appendOp(2, OpCodes.OP_NUMBERLIT);
      Number();

      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
    }
    else if (lookahead('(', 1) || (lookahead(':', 1) && lookahead('(', 3)))
    {
      FunctionCall();
    }
    else
    {
      LocationPath();
    }
  }

  /**
   *
   * Argument    ::=    Expr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Argument() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    appendOp(2, OpCodes.OP_ARGUMENT);
    Expr();

    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   * FunctionCall    ::=    FunctionName '(' ( Argument ( ',' Argument)*)? ')'
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void FunctionCall() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    if (lookahead(':', 1))
    {
      appendOp(4, OpCodes.OP_EXTFUNCTION);

      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH + 1] = m_queueMark - 1;

      nextToken();
      consumeExpected(':');

      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH + 2] = m_queueMark - 1;

      nextToken();
    }
    else
    {
      int funcTok = getFunctionToken(m_token);

      if (-1 == funcTok)
      {
        error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
              new Object[]{ m_token });  //"Could not find function: "+m_token+"()");
      }

      switch (funcTok)
      {
      case OpCodes.NODETYPE_PI :
      case OpCodes.NODETYPE_COMMENT :
      case OpCodes.NODETYPE_TEXT :
      case OpCodes.NODETYPE_NODE :
        LocationPath();

        return;
      default :
        appendOp(3, OpCodes.OP_FUNCTION);

        m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH + 1] = funcTok;
      }

      nextToken();
    }

    consumeExpected('(');

    while (!tokenIs(')') && m_token != null)
    {
      if (tokenIs(','))
      {
        error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG, null);  //"Found ',' but no preceding argument!");
      }

      Argument();

      if (!tokenIs(')'))
      {
        consumeExpected(',');

        if (tokenIs(')'))
        {
          error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG,
                null);  //"Found ',' but no following argument!");
        }
      }
    }

    consumeExpected(')');

    // Terminate for safety.
    m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = OpCodes.ENDOP;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;
    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }

  // ============= GRAMMAR FUNCTIONS =================

  /**
   *
   * LocationPath ::= RelativeLocationPath
   * | AbsoluteLocationPath
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void LocationPath() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    // int locationPathOpPos = opPos;
    appendOp(2, OpCodes.OP_LOCATIONPATH);

    if (tokenIs('/'))
    {
      appendOp(4, OpCodes.FROM_ROOT);

      // Tell how long the step is without the predicate
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - 2] = 4;
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - 1] =
        OpCodes.NODETYPE_ROOT;

      nextToken();
    }

    if (m_token != null)
    {
      RelativeLocationPath();
    }

    // Terminate for safety.
    m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = OpCodes.ENDOP;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;
    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   * RelativeLocationPath ::= Step
   * | RelativeLocationPath '/' Step
   * | AbbreviatedRelativeLocationPath
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void RelativeLocationPath() throws javax.xml.transform.TransformerException
  {

    Step();

    while (tokenIs('/'))
    {
      nextToken();
      Step();
    }
  }

  /**
   *
   * Step    ::=    Basis Predicate
   * | AbbreviatedStep
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Step() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    if (tokenIs("."))
    {
      nextToken();

      if (tokenIs('['))
      {
        error(XPATHErrorResources.ER_PREDICATE_ILLEGAL_SYNTAX, null);  //"'..[predicate]' or '.[predicate]' is illegal syntax.  Use 'self::node()[predicate]' instead.");
      }

      appendOp(4, OpCodes.FROM_SELF);

      // Tell how long the step is without the predicate
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - 2] = 4;
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - 1] =
        OpCodes.NODETYPE_NODE;
    }
    else if (tokenIs(".."))
    {
      nextToken();
      appendOp(4, OpCodes.FROM_PARENT);

      // Tell how long the step is without the predicate
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - 2] = 4;
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - 1] =
        OpCodes.NODETYPE_NODE;
    }

    // There is probably a better way to test for this 
    // transition... but it gets real hairy if you try 
    // to do it in basis().
    else if (tokenIs('*') || tokenIs('@') || tokenIs('/')
             || tokenIs('_') || (m_token!= null && Character.isLetter(m_token.charAt(0))))
    {
      Basis();

      while (tokenIs('['))
      {
        Predicate();
      }

      // Tell how long the entire step is.
      m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
    }
  }

  /**
   *
   * Basis    ::=    AxisName '::' NodeTest
   * | AbbreviatedBasis
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Basis() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];
    int axesType;

    // The next blocks guarantee that a FROM_XXX will be added.
    if (lookahead("::", 1))
    {
      axesType = AxisName();

      nextToken();
      nextToken();
    }
    else if (tokenIs('@'))
    {
      axesType = OpCodes.FROM_ATTRIBUTES;

      appendOp(2, axesType);
      nextToken();
    }
    else if (tokenIs('/'))
    {
      axesType = OpCodes.FROM_DESCENDANTS_OR_SELF;

      appendOp(2, axesType);

      // Have to fix up for patterns such as '//@foo' or '//attribute::foo',
      // which translate to 'descendant-or-self::node()/attribute::foo'.
      // notice I leave the '/' on the queue, so the next will be processed
      // by a regular step pattern.
      // if(lookahead('@', 1) || lookahead("::", 2))
      {

        // Make room for telling how long the step is without the predicate
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;
        m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] =
          OpCodes.NODETYPE_NODE;
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

        // Tell how long the step is without the predicate
        m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH + 1] =
          m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;

        return;  // make a quick exit...
      }

      // else
      // {
      //  nextToken();
      // }
    }
    else
    {
      axesType = OpCodes.FROM_CHILDREN;

      appendOp(2, axesType);
    }

    // Make room for telling how long the step is without the predicate
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

    NodeTest(axesType);

    // Tell how long the step is without the predicate
    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH + 1] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   * Basis    ::=    AxisName '::' NodeTest
   * | AbbreviatedBasis
   *
   * @return FROM_XXX axes type, found in {@link org.apache.xpath.compiler.Keywords}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int AxisName() throws javax.xml.transform.TransformerException
  {

    Object val = Keywords.m_axisnames.get(m_token);

    if (null == val)
    {
      error(XPATHErrorResources.ER_ILLEGAL_AXIS_NAME,
            new Object[]{ m_token });  //"illegal axis name: "+m_token);
    }

    int axesType = ((Integer) val).intValue();

    appendOp(2, axesType);

    return axesType;
  }

  /**
   *
   * NodeTest    ::=    WildcardName
   * | NodeType '(' ')'
   * | 'processing-instruction' '(' Literal ')'
   *
   * @param axesType FROM_XXX axes type, found in {@link org.apache.xpath.compiler.Keywords}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void NodeTest(int axesType) throws javax.xml.transform.TransformerException
  {

    if (lookahead('(', 1))
    {
      Object nodeTestOp = Keywords.m_nodetypes.get(m_token);

      if (null == nodeTestOp)
      {
        error(XPATHErrorResources.ER_UNKNOWN_NODETYPE,
              new Object[]{ m_token });  //"Unknown nodetype: "+m_token);
      }
      else
      {
        nextToken();

        int nt = ((Integer) nodeTestOp).intValue();

        m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = nt;
        m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

        consumeExpected('(');

        if (OpCodes.NODETYPE_PI == nt)
        {
          if (!tokenIs(')'))
          {
            Literal();
          }
        }

        consumeExpected(')');
      }
    }
    else
    {

      // Assume name of attribute or element.
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = OpCodes.NODENAME;
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

      if (lookahead(':', 1))
      {
        if (tokenIs('*'))
        {
          m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] =
            OpCodes.ELEMWILDCARD;
        }
        else
        {
          m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = m_queueMark
                  - 1;
        }

        nextToken();
        consumeExpected(':');
      }
      else
      {
        m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = OpCodes.EMPTY;
      }

      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

      if (tokenIs('*'))
      {
        m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] =
          OpCodes.ELEMWILDCARD;
      }
      else
      {
        if (OpCodes.FROM_NAMESPACE == axesType)
        {
          String prefix = (String) this.m_ops.m_tokenQueue[m_queueMark - 1];
          String namespace =
            ((PrefixResolver) m_namespaceContext).getNamespaceForPrefix(
              prefix);

          this.m_ops.m_tokenQueue[m_queueMark - 1] = namespace;
        }

        m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = m_queueMark - 1;
      }

      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

      nextToken();
    }
  }

  /**
   *
   * Predicate ::= '[' PredicateExpr ']'
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Predicate() throws javax.xml.transform.TransformerException
  {

    if (tokenIs('['))
    {
      nextToken();
      PredicateExpr();
      consumeExpected(']');
    }
  }

  /**
   *
   * PredicateExpr ::= Expr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void PredicateExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    appendOp(2, OpCodes.OP_PREDICATE);
    Expr();

    // Terminate for safety.
    m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = OpCodes.ENDOP;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;
    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }

  /**
   * QName ::=  (Prefix ':')? LocalPart
   * Prefix ::=  NCName
   * LocalPart ::=  NCName
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void QName() throws javax.xml.transform.TransformerException
  {
    // Namespace
    if(lookahead(':', 1))
    {
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = m_queueMark - 1;
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

      nextToken();
      consumeExpected(':');
    }
    else
    {
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = OpCodes.EMPTY;
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;
    }
    
    // Local name
    m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = m_queueMark - 1;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

    nextToken();
  }

  /**
   * NCName ::=  (Letter | '_') (NCNameChar)
   * NCNameChar ::=  Letter | Digit | '.' | '-' | '_' | CombiningChar | Extender
   */
  protected void NCName()
  {

    m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = m_queueMark - 1;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

    nextToken();
  }

  /**
   * The value of the Literal is the sequence of characters inside
   * the " or ' characters>.
   *
   * Literal  ::=  '"' [^"]* '"'
   * | "'" [^']* "'"
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Literal() throws javax.xml.transform.TransformerException
  {

    int last = m_token.length() - 1;
    char c0 = m_tokenChar;
    char cX = m_token.charAt(last);

    if (((c0 == '\"') && (cX == '\"')) || ((c0 == '\'') && (cX == '\'')))
    {

      // Mutate the token to remove the quotes and have the XString object
      // already made.
      int tokenQueuePos = m_queueMark - 1;

      m_ops.m_tokenQueue[tokenQueuePos] = null;

      Object obj = new XString(m_token.substring(1, last));

      m_ops.m_tokenQueue[tokenQueuePos] = obj;

      // lit = m_token.substring(1, last);
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = tokenQueuePos;
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

      nextToken();
    }
    else
    {
      error(XPATHErrorResources.ER_PATTERN_LITERAL_NEEDS_BE_QUOTED,
            new Object[]{ m_token });  //"Pattern literal ("+m_token+") needs to be quoted!");
    }
  }

  /**
   *
   * Number ::= [0-9]+('.'[0-9]+)? | '.'[0-9]+
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Number() throws javax.xml.transform.TransformerException
  {

    if (null != m_token)
    {

      // Mutate the token to remove the quotes and have the XNumber object
      // already made.
      double num;

      try
      {
        num = Double.valueOf(m_token).doubleValue();
      }
      catch (NumberFormatException nfe)
      {
        num = 0.0;  // to shut up compiler.

        error(XPATHErrorResources.ER_COULDNOT_BE_FORMATTED_TO_NUMBER,
              new Object[]{ m_token });  //m_token+" could not be formatted to a number!");
      }

      m_ops.m_tokenQueue[m_queueMark - 1] = new XNumber(num);
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = m_queueMark - 1;
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

      nextToken();
    }
  }

  // ============= PATTERN FUNCTIONS =================

  /**
   *
   * Pattern  ::=  LocationPathPattern
   * | Pattern '|' LocationPathPattern
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Pattern() throws javax.xml.transform.TransformerException
  {

    while (true)
    {
      LocationPathPattern();

      if (tokenIs('|'))
      {
        nextToken();
      }
      else
      {
        break;
      }
    }
  }

  /**
   *
   *
   * LocationPathPattern  ::=  '/' RelativePathPattern?
   * | IdKeyPattern (('/' | '//') RelativePathPattern)?
   * | '//'? RelativePathPattern
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void LocationPathPattern() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];

    appendOp(2, OpCodes.OP_LOCATIONPATHPATTERN);

    if (lookahead('(', 1)
            && (tokenIs(Keywords.FUNC_ID_STRING)
                || tokenIs(Keywords.FUNC_KEY_STRING)))
    {
      IdKeyPattern();

      if (tokenIs('/') && lookahead('/', 1))
      {
        appendOp(4, OpCodes.MATCH_ANY_ANCESTOR);

        // Tell how long the step is without the predicate
        m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - 2] = 4;
        m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - 1] =
          OpCodes.NODETYPE_FUNCTEST;

        nextToken();
        nextToken();
      }
    }
    else if (tokenIs('/'))
    {
      if (lookahead('/', 1))
      {
        appendOp(4, OpCodes.MATCH_ANY_ANCESTOR);
      }
      else
      {
        appendOp(4, OpCodes.FROM_ROOT);
      }

      // Tell how long the step is without the predicate
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - 2] = 4;
      m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - 1] =
        OpCodes.NODETYPE_ROOT;

      nextToken();
    }

    if (!tokenIs('|') && (null != m_token))
    {
      RelativePathPattern();
    }

    // Terminate for safety.
    m_ops.m_opMap[m_ops.m_opMap[OpMap.MAPINDEX_LENGTH]] = OpCodes.ENDOP;
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;
    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }

  /**
   *
   * IdKeyPattern  ::=  'id' '(' Literal ')'
   * | 'key' '(' Literal ',' Literal ')'
   * (Also handle doc())
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void IdKeyPattern() throws javax.xml.transform.TransformerException
  {
    FunctionCall();
  }

  /**
   *
   * RelativePathPattern  ::=  StepPattern
   * | RelativePathPattern '/' StepPattern
   * | RelativePathPattern '//' StepPattern
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void RelativePathPattern() throws javax.xml.transform.TransformerException
  {

    StepPattern();

    while (tokenIs('/'))
    {
      nextToken();
      StepPattern();
    }
  }

  /**
   *
   * StepPattern  ::=  AbbreviatedNodeTestStep
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void StepPattern() throws javax.xml.transform.TransformerException
  {
    AbbreviatedNodeTestStep();
  }

  /**
   *
   * AbbreviatedNodeTestStep    ::=    '@'? NodeTest Predicate
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void AbbreviatedNodeTestStep() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];
    int axesType;

    // The next blocks guarantee that a MATCH_XXX will be added.
    int matchTypePos = -1;

    if (tokenIs('@'))
    {
      axesType = OpCodes.MATCH_ATTRIBUTE;

      appendOp(2, axesType);
      nextToken();
    }
    else if (this.lookahead("::", 1))
    {
      if (tokenIs("attribute"))
      {
        axesType = OpCodes.MATCH_ATTRIBUTE;

        appendOp(2, axesType);
      }
      else if (tokenIs("child"))
      {
        axesType = OpCodes.MATCH_IMMEDIATE_ANCESTOR;

        appendOp(2, axesType);
      }
      else
      {
        axesType = -1;

        this.error(XPATHErrorResources.ER_AXES_NOT_ALLOWED,
                   new Object[]{ this.m_token });
      }

      nextToken();
      nextToken();
    }
    else if (tokenIs('/'))
    {
      axesType = OpCodes.MATCH_ANY_ANCESTOR;

      appendOp(2, axesType);
      nextToken();
    }
    else
    {
      if (tokenIs('/'))
      {
        nextToken();
      }

      matchTypePos = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH];
      axesType = OpCodes.MATCH_IMMEDIATE_ANCESTOR;

      appendOp(2, axesType);
    }

    // Make room for telling how long the step is without the predicate
    m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] += 1;

    NodeTest(axesType);

    // Tell how long the step is without the predicate
    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH + 1] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;

    while (tokenIs('['))
    {
      Predicate();
    }

    if ((matchTypePos > -1) && tokenIs('/') && lookahead('/', 1))
    {
      m_ops.m_opMap[matchTypePos] = OpCodes.MATCH_ANY_ANCESTOR;

      nextToken();
    }

    // Tell how long the entire step is.
    m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] =
      m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
  }
}
