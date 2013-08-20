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
 * $Id: FilterExpr.java,v 1.11 2004/02/16 22:24:29 minchau Exp $
 */

package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 */
class FilterExpr extends Expression {
    
    /**
     * Primary expression of this filter. I.e., 'e' in '(e)[p1]...[pn]'.
     */
    private Expression   _primary;
    
    /**
     * Array of predicates in '(e)[p1]...[pn]'.
     */
    private final Vector _predicates;

    public FilterExpr(Expression primary, Vector predicates) {
	_primary = primary;
	_predicates = predicates;
	primary.setParent(this);
    }

    protected Expression getExpr() {
	if (_primary instanceof CastExpr)
	    return ((CastExpr)_primary).getExpr();
	else
	    return _primary;
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	_primary.setParser(parser);
	if (_predicates != null) {
	    final int n = _predicates.size();
	    for (int i = 0; i < n; i++) {
		final Expression exp = (Expression)_predicates.elementAt(i);
		exp.setParser(parser);
		exp.setParent(this);
	    }
	}
    }
    
    public String toString() {
	return "filter-expr(" + _primary + ", " + _predicates + ")";
    }

    /**
     * Type check a FilterParentPath. If the filter is not a node-set add a 
     * cast to node-set only if it is of reference type. This type coercion 
     * is needed for expressions like $x where $x is a parameter reference.
     * All optimizations are turned off before type checking underlying
     * predicates.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	Type ptype = _primary.typeCheck(stable);

	if (ptype instanceof NodeSetType == false) {
	    if (ptype instanceof ReferenceType)  {
		_primary = new CastExpr(_primary, Type.NodeSet);
	    }
	    else {
		throw new TypeCheckError(this);
	    }
	}

        // Type check predicates and turn all optimizations off
	int n = _predicates.size();
	for (int i = 0; i < n; i++) {
	    Predicate pred = (Predicate) _predicates.elementAt(i);
            pred.dontOptimize();
	    pred.typeCheck(stable);
	}
	return _type = Type.NodeSet;	
    }
	
    /**
     * Translate a filter expression by pushing the appropriate iterator
     * onto the stack.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	if (_predicates.size() > 0) {
	    translatePredicates(classGen, methodGen);
	}
	else {
	    _primary.translate(classGen, methodGen);
	}
    }

    /**
     * Translate a sequence of predicates. Each predicate is translated 
     * by constructing an instance of <code>CurrentNodeListIterator</code> 
     * which is initialized from another iterator (recursive call), a 
     * filter and a closure (call to translate on the predicate) and "this". 
     */
    public void translatePredicates(ClassGenerator classGen,
				    MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

        // If not predicates left, translate primary expression
	if (_predicates.size() == 0) {
	    translate(classGen, methodGen);
	}
	else {
            // Translate predicates from right to left
	    final int initCNLI = cpg.addMethodref(CURRENT_NODE_LIST_ITERATOR,
						  "<init>",
						  "("+NODE_ITERATOR_SIG+"Z"+
						  CURRENT_NODE_LIST_FILTER_SIG +
						  NODE_SIG+TRANSLET_SIG+")V");

	    Predicate predicate = (Predicate)_predicates.lastElement();
	    _predicates.remove(predicate);

            // Create a CurrentNodeListIterator
            il.append(new NEW(cpg.addClass(CURRENT_NODE_LIST_ITERATOR)));
            il.append(DUP);
            
            // Translate the rest of the predicates from right to left
            translatePredicates(classGen, methodGen);
            
            // Initialize CurrentNodeListIterator
            il.append(ICONST_1);
            predicate.translate(classGen, methodGen);
            il.append(methodGen.loadCurrentNode());
            il.append(classGen.loadTranslet());
            il.append(new INVOKESPECIAL(initCNLI));
	}
    }
}
