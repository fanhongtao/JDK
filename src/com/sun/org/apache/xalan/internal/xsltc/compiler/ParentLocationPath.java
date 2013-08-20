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
 * $Id: ParentLocationPath.java,v 1.23 2004/02/16 22:24:29 minchau Exp $
 */

package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.dom.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class ParentLocationPath extends RelativeLocationPath {
    private Expression _step;
    private final RelativeLocationPath _path;
    private Type stype;
    private boolean _orderNodes = false;
    private boolean _axisMismatch = false;

    public ParentLocationPath(RelativeLocationPath path, Expression step) {
	_path = path;
	_step = step;
	_path.setParent(this);
	_step.setParent(this);

	if (_step instanceof Step) {
	    _axisMismatch = checkAxisMismatch();
	}
    }
		
    public void setAxis(int axis) {
	_path.setAxis(axis);
    }

    public int getAxis() {
	return _path.getAxis();
    }

    public RelativeLocationPath getPath() {
	return(_path);
    }

    public Expression getStep() {
	return(_step);
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	_step.setParser(parser);
	_path.setParser(parser);
    }
    
    public String toString() {
	return "ParentLocationPath(" + _path + ", " + _step + ')';
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	stype = _step.typeCheck(stable);
	_path.typeCheck(stable);

	if (_axisMismatch) enableNodeOrdering();

	return _type = Type.NodeSet;	
    }

    public void enableNodeOrdering() {
	SyntaxTreeNode parent = getParent();
	if (parent instanceof ParentLocationPath)
	    ((ParentLocationPath)parent).enableNodeOrdering();
	else {
	    _orderNodes = true;
	}
    }

    /**
     * This method is used to determine if this parent location path is a
     * combination of two step's with axes that will create duplicate or
     * unordered nodes.
     */
    public boolean checkAxisMismatch() {

	int left = _path.getAxis();
	int right = ((Step)_step).getAxis();

	if (((left == Axis.ANCESTOR) || (left == Axis.ANCESTORORSELF)) &&
	    ((right == Axis.CHILD) ||
	     (right == Axis.DESCENDANT) ||
	     (right == Axis.DESCENDANTORSELF) ||
	     (right == Axis.PARENT) ||
	     (right == Axis.PRECEDING) ||
	     (right == Axis.PRECEDINGSIBLING)))
	    return true;

	if ((left == Axis.CHILD) &&
	    (right == Axis.ANCESTOR) ||
	    (right == Axis.ANCESTORORSELF) ||
	    (right == Axis.PARENT) ||
	    (right == Axis.PRECEDING))
	    return true;

	if ((left == Axis.DESCENDANT) || (left == Axis.DESCENDANTORSELF))
	    return true;

	if (((left == Axis.FOLLOWING) || (left == Axis.FOLLOWINGSIBLING)) &&
	    ((right == Axis.FOLLOWING) ||
	     (right == Axis.PARENT) ||
	     (right == Axis.PRECEDING) ||
	     (right == Axis.PRECEDINGSIBLING)))
	    return true;

	if (((left == Axis.PRECEDING) || (left == Axis.PRECEDINGSIBLING)) &&
	    ((right == Axis.DESCENDANT) ||
	     (right == Axis.DESCENDANTORSELF) ||
	     (right == Axis.FOLLOWING) ||
	     (right == Axis.FOLLOWINGSIBLING) ||
	     (right == Axis.PARENT) ||
	     (right == Axis.PRECEDING) ||
	     (right == Axis.PRECEDINGSIBLING)))
	    return true;

	if ((right == Axis.FOLLOWING) && (left == Axis.CHILD)) {
	    // Special case for '@*/following::*' expressions. The resulting
	    // iterator is initialised with the parent's first child, and this
	    // can cause duplicates in the output if the parent has more than
	    // one attribute that matches the left step.
	    if (_path instanceof Step) {
		int type = ((Step)_path).getNodeType();
		if (type == DTM.ATTRIBUTE_NODE) return true;
	    }
	}

	return false;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Create new StepIterator
	final int initSI = cpg.addMethodref(STEP_ITERATOR_CLASS,
					    "<init>",
					    "("
					    +NODE_ITERATOR_SIG
					    +NODE_ITERATOR_SIG
					    +")V");
	il.append(new NEW(cpg.addClass(STEP_ITERATOR_CLASS)));
	il.append(DUP);

	// Compile path iterator
	_path.translate(classGen, methodGen); // iterator on stack....
	_step.translate(classGen, methodGen);

	// Initialize StepIterator with iterators from the stack
	il.append(new INVOKESPECIAL(initSI));

	// This is a special case for the //* path with or without predicates
	Expression stp = _step;
	if (stp instanceof ParentLocationPath)
	    stp = ((ParentLocationPath)stp).getStep();

	if ((_path instanceof Step) && (stp instanceof Step)) {
	    final int path = ((Step)_path).getAxis();
	    final int step = ((Step)stp).getAxis();
	    if ((path == Axis.DESCENDANTORSELF && step == Axis.CHILD) ||
		(path == Axis.PRECEDING        && step == Axis.PARENT)) {
		final int incl = cpg.addMethodref(NODE_ITERATOR_BASE,
						  "includeSelf",
						  "()" + NODE_ITERATOR_SIG);
		il.append(new INVOKEVIRTUAL(incl));
	    }
	}

	/*
	 * If this pattern contains a sequence of descendant iterators we
	 * run the risk of returning the same node several times. We put
	 * a new iterator on top of the existing one to assure node order
	 * and prevent returning a single node multiple times.
	 */
	if (_orderNodes) {
	    final int order = cpg.addInterfaceMethodref(DOM_INTF,
							ORDER_ITERATOR,
							ORDER_ITERATOR_SIG);
	    il.append(methodGen.loadDOM());
	    il.append(SWAP);
	    il.append(methodGen.loadContextNode());
	    il.append(new INVOKEINTERFACE(order, 3));
	}
    }
}
