/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights 
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

import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.UnionPathIterator;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.UnaryOperation;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.StepPattern;
import org.apache.xpath.patterns.UnionPattern;

/**
 * A derivation from this class can be passed to a class that implements 
 * the XPathVisitable interface, to have the appropriate method called 
 * for each component of the XPath.  Aside from possible other uses, the 
 * main intention is to provide a reasonable means to perform expression 
 * rewriting.
 * 
 * <p>Each method has the form 
 * <code>boolean visitComponentType(ExpressionOwner owner, ComponentType compType)</code>. 
 * The ExpressionOwner argument is the owner of the component, and can 
 * be used to reset the expression for rewriting.  If a method returns 
 * false, the sub hierarchy will not be traversed.</p>
 * 
 * <p>This class is meant to be a base class that will be derived by concrete classes, 
 * and doesn't much except return true for each method.</p>
 */
public class XPathVisitor
{
	/**
	 * Visit a LocationPath.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param path The LocationPath object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitLocationPath(ExpressionOwner owner, LocPathIterator path)
	{
		return true;
	}

	/**
	 * Visit a UnionPath.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param path The UnionPath object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitUnionPath(ExpressionOwner owner, UnionPathIterator path)
	{
		return true;
	}
	
	/**
	 * Visit a step within a location path.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param step The Step object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitStep(ExpressionOwner owner, NodeTest step)
	{
		return true;
	}
		
	/**
	 * Visit a predicate within a location path.  Note that there isn't a 
	 * proper unique component for predicates, and that the expression will 
	 * be called also for whatever type Expression is.
	 * 
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param pred The predicate object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitPredicate(ExpressionOwner owner, Expression pred)
	{
		return true;
	}

	/**
	 * Visit a binary operation.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param op The operation object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitBinaryOperation(ExpressionOwner owner, Operation op)
	{
		return true;
	}

	/**
	 * Visit a unary operation.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param op The operation object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitUnaryOperation(ExpressionOwner owner, UnaryOperation op)
	{
		return true;
	}
	
	/**
	 * Visit a variable reference.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param var The variable reference object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitVariableRef(ExpressionOwner owner, Variable var)
	{
		return true;
	}

	/**
	 * Visit a function.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param func The function reference object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitFunction(ExpressionOwner owner, Function func)
	{
		return true;
	}
	
	/**
	 * Visit a match pattern.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param pattern The match pattern object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitMatchPattern(ExpressionOwner owner, StepPattern pattern)
	{
		return true;
	}
	
	/**
	 * Visit a union pattern.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param pattern The union pattern object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitUnionPattern(ExpressionOwner owner, UnionPattern pattern)
	{
		return true;
	}
	
	/**
	 * Visit a string literal.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param str The string literal object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitStringLiteral(ExpressionOwner owner, XString str)
	{
		return true;
	}


	/**
	 * Visit a number literal.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param num The number literal object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitNumberLiteral(ExpressionOwner owner, XNumber num)
	{
		return true;
	}


}

