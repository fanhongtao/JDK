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
 * $Id: ApplyImports.java,v 1.13 2004/02/16 22:24:29 minchau Exp $
 */

package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Enumeration;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

/**
 * @author Morten Jorgensen
 */
final class ApplyImports extends Instruction {

    private QName      _modeName;
    private String     _functionName;
    private int        _precedence;

    public void display(int indent) {
	indent(indent);
	Util.println("ApplyTemplates");
	indent(indent + IndentIncrement);
	if (_modeName != null) {
	    indent(indent + IndentIncrement);
	    Util.println("mode " + _modeName);
	}
    }

    /**
     * Returns true if this <xsl:apply-imports/> element has parameters
     */
    public boolean hasWithParams() {
	return hasContents();
    }

    /**
     * Determine the lowest import precedence for any stylesheet imported
     * or included by the stylesheet in which this <xsl:apply-imports/>
     * element occured. The templates that are imported by the stylesheet in
     * which this element occured will all have higher import precedence than
     * the integer returned by this method.
     */
    private int getMinPrecedence(int max) {
	Stylesheet stylesheet = getStylesheet();
	Stylesheet root = getParser().getTopLevelStylesheet();

	int min = max;

	Enumeration templates = root.getContents().elements();
	while (templates.hasMoreElements()) {
	    SyntaxTreeNode child = (SyntaxTreeNode)templates.nextElement();
	    if (child instanceof Template) {
		Stylesheet curr = child.getStylesheet();
		while ((curr != null) && (curr != stylesheet)) {
		    if (curr._importedFrom != null)
			curr = curr._importedFrom;
		    else if (curr._includedFrom != null)
			curr = curr._includedFrom;
		    else
			curr = null;
		}
		if (curr == stylesheet) {
		    int prec = child.getStylesheet().getImportPrecedence();
		    if (prec < min) min = prec;
		}
	    }
	}
	return (min);
    }

    /**
     * Parse the attributes and contents of an <xsl:apply-imports/> element.
     */
    public void parseContents(Parser parser) {
	// Indicate to the top-level stylesheet that all templates must be
	// compiled into separate methods.
	Stylesheet stylesheet = getStylesheet();
	stylesheet.setTemplateInlining(false);

	// Get the mode we are currently in (might not be any)
	Template template = getTemplate();
	_modeName = template.getModeName();
	_precedence = template.getImportPrecedence();

	// Get the method name for <xsl:apply-imports/> in this mode
	stylesheet = parser.getTopLevelStylesheet();

	// Get the [min,max> precedence of all templates imported under the
	// current stylesheet
	final int maxPrecedence = _precedence;
	final int minPrecedence = getMinPrecedence(maxPrecedence);
	final Mode mode = stylesheet.getMode(_modeName);
	_functionName = mode.functionName(minPrecedence, maxPrecedence);

	parseChildren(parser);	// with-params
    }

    /**
     * Type-check the attributes/contents of an <xsl:apply-imports/> element.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	typeCheckContents(stable);		// with-params
	return Type.Void;
    }

    /**
     * Translate call-template. A parameter frame is pushed only if
     * some template in the stylesheet uses parameters. 
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final Stylesheet stylesheet = classGen.getStylesheet();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final int current = methodGen.getLocalIndex("current");

	// Push the arguments that are passed to applyTemplates()
	il.append(classGen.loadTranslet());
	il.append(methodGen.loadDOM());
	// Wrap the current node inside an iterator
	int init = cpg.addMethodref(SINGLETON_ITERATOR,
				    "<init>", "("+NODE_SIG+")V");
	il.append(new NEW(cpg.addClass(SINGLETON_ITERATOR)));
	il.append(DUP);
	il.append(methodGen.loadCurrentNode());
	il.append(new INVOKESPECIAL(init));

	il.append(methodGen.loadHandler());

	// Construct the translet class-name and the signature of the method
	final String className = classGen.getStylesheet().getClassName();
	final String signature = classGen.getApplyTemplatesSig();
	final int applyTemplates = cpg.addMethodref(className,
						    _functionName,
						    signature);
	il.append(new INVOKEVIRTUAL(applyTemplates));
    }

}
