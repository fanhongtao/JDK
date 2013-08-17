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

/**
 * Operations codes for XPath.
 *
 * Code for the descriptions of the operations codes:
 * [UPPER CASE] indicates a literal value,
 * [lower case] is a description of a value,
 *      ([length] always indicates the length of the operation,
 *       including the operations code and the length integer.)
 * {UPPER CASE} indicates the given production,
 * {description} is the description of a new production,
 *      (For instance, {boolean expression} means some expression
 *       that should be resolved to a boolean.)
 *  * means that it occurs zero or more times,
 *  + means that it occurs one or more times,
 *  ? means that it is optional.
 *
 * returns: indicates what the production should return.
 */
public class OpCodes
{

  /**
   * <meta name="usage" content="advanced"/>
   * [ENDOP]
   * Some operators may like to have a terminator.
   */
  public static final int ENDOP = -1;

  /**
   * [EMPTY]
   * Empty slot to indicate NULL.
   */
  public static final int EMPTY = -2;

  /**
   * <meta name="usage" content="advanced"/>
   * [ELEMWILDCARD]
   * Means ELEMWILDCARD ("*"), used instead
   * of string index in some places.
   */
  public static final int ELEMWILDCARD = -3;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_XPATH]
   * [length]
   *  {expression}
   *
   * returns:
   *  XNodeSet
   *  XNumber
   *  XString
   *  XBoolean
   *  XRTree
   *  XObject
   */
  public static final int OP_XPATH = 1;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_OR]
   * [length]
   *  {boolean expression}
   *  {boolean expression}
   *
   * returns:
   *  XBoolean
   */
  public static final int OP_OR = 2;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_AND]
   * [length]
   *  {boolean expression}
   *  {boolean expression}
   *
   * returns:
   *  XBoolean
   */
  public static final int OP_AND = 3;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_NOTEQUALS]
   * [length]
   *  {expression}
   *  {expression}
   *
   * returns:
   *  XBoolean
   */
  public static final int OP_NOTEQUALS = 4;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_EQUALS]
   * [length]
   *  {expression}
   *  {expression}
   *
   * returns:
   *  XBoolean
   */
  public static final int OP_EQUALS = 5;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_LTE] (less-than-or-equals)
   * [length]
   *  {number expression}
   *  {number expression}
   *
   * returns:
   *  XBoolean
   */
  public static final int OP_LTE = 6;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_LT] (less-than)
   * [length]
   *  {number expression}
   *  {number expression}
   *
   * returns:
   *  XBoolean
   */
  public static final int OP_LT = 7;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_GTE] (greater-than-or-equals)
   * [length]
   *  {number expression}
   *  {number expression}
   *
   * returns:
   *  XBoolean
   */
  public static final int OP_GTE = 8;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_GT] (greater-than)
   * [length]
   *  {number expression}
   *  {number expression}
   *
   * returns:
   *  XBoolean
   */
  public static final int OP_GT = 9;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_PLUS]
   * [length]
   *  {number expression}
   *  {number expression}
   *
   * returns:
   *  XNumber
   */
  public static final int OP_PLUS = 10;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_MINUS]
   * [length]
   *  {number expression}
   *  {number expression}
   *
   * returns:
   *  XNumber
   */
  public static final int OP_MINUS = 11;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_MULT]
   * [length]
   *  {number expression}
   *  {number expression}
   *
   * returns:
   *  XNumber
   */
  public static final int OP_MULT = 12;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_DIV]
   * [length]
   *  {number expression}
   *  {number expression}
   *
   * returns:
   *  XNumber
   */
  public static final int OP_DIV = 13;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_MOD]
   * [length]
   *  {number expression}
   *  {number expression}
   *
   * returns:
   *  XNumber
   */
  public static final int OP_MOD = 14;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_QUO]
   * [length]
   *  {number expression}
   *  {number expression}
   *
   * returns:
   *  XNumber
   */
  public static final int OP_QUO = 15;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_NEG]
   * [length]
   *  {number expression}
   *
   * returns:
   *  XNumber
   */
  public static final int OP_NEG = 16;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_STRING] (cast operation)
   * [length]
   *  {expression}
   *
   * returns:
   *  XString
   */
  public static final int OP_STRING = 17;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_BOOL] (cast operation)
   * [length]
   *  {expression}
   *
   * returns:
   *  XBoolean
   */
  public static final int OP_BOOL = 18;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_NUMBER] (cast operation)
   * [length]
   *  {expression}
   *
   * returns:
   *  XBoolean
   */
  public static final int OP_NUMBER = 19;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_UNION]
   * [length]
   *  {PathExpr}+
   *
   * returns:
   *  XNodeSet
   */
  public static final int OP_UNION = 20;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_LITERAL]
   * [3]
   * [index to token]
   *
   * returns:
   *  XString
   */
  public static final int OP_LITERAL = 21;

  /** The low opcode for nodesets, needed by getFirstPredicateOpPos and 
   *  getNextStepPos.          */
  static final int FIRST_NODESET_OP = 22;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_VARIABLE]
   * [4]
   * [index to namespace token, or EMPTY]
   * [index to function name token]
   *
   * returns:
   *  XString
   */
  public static final int OP_VARIABLE = 22;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_GROUP]
   * [length]
   *  {expression}
   *
   * returns:
   *  XNodeSet
   *  XNumber
   *  XString
   *  XBoolean
   *  XRTree
   *  XObject
   */
  public static final int OP_GROUP = 23;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_EXTFUNCTION] (Extension function.)
   * [length]
   * [index to namespace token]
   * [index to function name token]
   *  {OP_ARGUMENT}
   *
   * returns:
   *  XNodeSet
   *  XNumber
   *  XString
   *  XBoolean
   *  XRTree
   *  XObject
   */
  public static final int OP_EXTFUNCTION = 24;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_FUNCTION]
   * [length]
   * [FUNC_name]
   *  {OP_ARGUMENT}
   * [ENDOP]
   *
   * returns:
   *  XNodeSet
   *  XNumber
   *  XString
   *  XBoolean
   *  XRTree
   *  XObject
   */
  public static final int OP_FUNCTION = 25;

  /** The last opcode for stuff that can be a nodeset.         */
  static final int LAST_NODESET_OP = 25;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_ARGUMENT] (Function argument.)
   * [length]
   *  {expression}
   *
   * returns:
   *  XNodeSet
   *  XNumber
   *  XString
   *  XBoolean
   *  XRTree
   *  XObject
   */
  public static final int OP_ARGUMENT = 26;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_NUMBERLIT] (Number literal.)
   * [3]
   * [index to token]
   *
   * returns:
   *  XString
   */
  public static final int OP_NUMBERLIT = 27;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_LOCATIONPATH]
   * [length]
   *   {FROM_stepType}
   * | {function}
   * {predicate}
   * [ENDOP]
   *
   * (Note that element and attribute namespaces and
   * names can be wildcarded '*'.)
   *
   * returns:
   *  XNodeSet
   */
  public static final int OP_LOCATIONPATH = 28;

  // public static final int LOCATIONPATHEX_MASK = 0x0000FFFF;
  // public static final int LOCATIONPATHEX_ISSIMPLE = 0x00010000;
  // public static final int OP_LOCATIONPATH_EX = (28 | 0x00010000);

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_PREDICATE]
   * [length]
   *  {expression}
   * [ENDOP] (For safety)
   *
   * returns:
   *  XBoolean or XNumber
   */
  public static final int OP_PREDICATE = 29;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_MATCHPATTERN]
   * [length]
   *  {PathExpr}+
   *
   * returns:
   *  XNodeSet
   */
  public static final int OP_MATCHPATTERN = 30;

  /**
   * <meta name="usage" content="advanced"/>
   * [OP_LOCATIONPATHPATTERN]
   * [length]
   *   {FROM_stepType}
   * | {function}{predicate}
   * [ENDOP]
   * returns:
   *  XNodeSet
   */
  public static final int OP_LOCATIONPATHPATTERN = 31;

  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_COMMENT]
   * No size or arguments.
   * Note: must not overlap function OP number!
   *
   * returns:
   *  XBoolean
   */
  public static final int NODETYPE_COMMENT = 1030;

  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_TEXT]
   * No size or arguments.
   * Note: must not overlap function OP number!
   *
   * returns:
   *  XBoolean
   */
  public static final int NODETYPE_TEXT = 1031;

  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_PI]
   * [index to token]
   * Note: must not overlap function OP number!
   *
   * returns:
   *  XBoolean
   */
  public static final int NODETYPE_PI = 1032;

  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_NODE]
   * No size or arguments.
   * Note: must not overlap function OP number!
   *
   * returns:
   *  XBoolean
   */
  public static final int NODETYPE_NODE = 1033;

  /**
   * <meta name="usage" content="advanced"/>
   * [NODENAME]
   * [index to ns token or EMPTY]
   * [index to name token]
   *
   * returns:
   *  XBoolean
   */
  public static final int NODENAME = 34;

  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_ROOT]
   * No size or arguments.
   *
   * returns:
   *  XBoolean
   */
  public static final int NODETYPE_ROOT = 35;

  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_ANY]
   * No size or arguments.
   *
   * returns:
   *  XBoolean
   */
  public static final int NODETYPE_ANYELEMENT = 36;

  /**
   * <meta name="usage" content="advanced"/>
   * [NODETYPE_ANY]
   * No size or arguments.
   *
   * returns:
   *  XBoolean
   */
  public static final int NODETYPE_FUNCTEST = 1034;

  /**
   * <meta name="usage" content="advanced"/>
   * [FROM_stepType]
   * [length, including predicates]
   * [length of just the step, without the predicates]
   * {node test}
   * {predicates}?
   *
   * returns:
   *  XBoolean
   */
  public static final int AXES_START_TYPES = 37;

  /** ancestor axes opcode.         */
  public static final int FROM_ANCESTORS = 37;

  /** ancestor-or-self axes opcode.         */
  public static final int FROM_ANCESTORS_OR_SELF = 38;

  /** attribute axes opcode.         */
  public static final int FROM_ATTRIBUTES = 39;

  /** children axes opcode.         */
  public static final int FROM_CHILDREN = 40;

  /** descendants axes opcode.         */
  public static final int FROM_DESCENDANTS = 41;

  /** descendants-of-self axes opcode.         */
  public static final int FROM_DESCENDANTS_OR_SELF = 42;

  /** following axes opcode.         */
  public static final int FROM_FOLLOWING = 43;

  /** following-siblings axes opcode.         */
  public static final int FROM_FOLLOWING_SIBLINGS = 44;

  /** parent axes opcode.         */
  public static final int FROM_PARENT = 45;

  /** preceding axes opcode.         */
  public static final int FROM_PRECEDING = 46;

  /** preceding-sibling axes opcode.         */
  public static final int FROM_PRECEDING_SIBLINGS = 47;

  /** self axes opcode.         */
  public static final int FROM_SELF = 48;

  /** namespace axes opcode.         */
  public static final int FROM_NAMESPACE = 49;

  /** '/' axes opcode.         */
  public static final int FROM_ROOT = 50;

  /**
   * <meta name="usage" content="advanced"/>
   * For match patterns.
   */
  public static final int MATCH_ATTRIBUTE = 51;

  /**
   * <meta name="usage" content="advanced"/>
   * For match patterns.
   */
  public static final int MATCH_ANY_ANCESTOR = 52;

  /**
   * <meta name="usage" content="advanced"/>
   * For match patterns.
   */
  public static final int MATCH_IMMEDIATE_ANCESTOR = 53;

  /** The end of the axes types.    */
  public static final int AXES_END_TYPES = 53;

  /** The next free ID.  Please keep this up to date.  */
  private static final int NEXT_FREE_ID = 99;
}
