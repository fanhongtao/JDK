/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.util.Vector;

/**
 * @version $Id: Op.java,v 1.3 2002/08/09 15:18:17 neilg Exp $
 */
class Op {
    static final int DOT = 0;
    static final int CHAR = 1;                  // Single character
    static final int RANGE = 3;                 // [a-zA-Z]
    static final int NRANGE = 4;                // [^a-zA-Z]
    static final int ANCHOR = 5;                // ^ $ ...
    static final int STRING = 6;                // literal String 
    static final int CLOSURE = 7;               // X*
    static final int NONGREEDYCLOSURE = 8;      // X*?
    static final int QUESTION = 9;              // X?
    static final int NONGREEDYQUESTION = 10;    // X??
    static final int UNION = 11;                // X|Y
    static final int CAPTURE = 15;              // ( and )
    static final int BACKREFERENCE = 16;        // \1 \2 ...
    static final int LOOKAHEAD = 20;            // (?=...)
    static final int NEGATIVELOOKAHEAD = 21;    // (?!...)
    static final int LOOKBEHIND = 22;           // (?<=...)
    static final int NEGATIVELOOKBEHIND = 23;   // (?<!...)
    static final int INDEPENDENT = 24;          // (?>...)
    static final int MODIFIER = 25;             // (?ims-ims:...)
    static final int CONDITION = 26;            // (?(..)yes|no)

    static int nofinstances = 0;
    static final boolean COUNT = false;

    static Op createDot() {
        if (Op.COUNT)  Op.nofinstances ++;
        return new Op(Op.DOT);
    }
    static CharOp createChar(int data) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new CharOp(Op.CHAR, data);
    }
    static CharOp createAnchor(int data) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new CharOp(Op.ANCHOR, data);
    }
    static CharOp createCapture(int number, Op next) {
        if (Op.COUNT)  Op.nofinstances ++;
        CharOp op = new CharOp(Op.CAPTURE, number);
        op.next = next;
        return op;
    }
    static UnionOp createUnion(int size) {
        if (Op.COUNT)  Op.nofinstances ++;
        //System.err.println("Creates UnionOp");
        return new UnionOp(Op.UNION, size);
    }
    static ChildOp createClosure(int id) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new ModifierOp(Op.CLOSURE, id, -1);
    }
    static ChildOp createNonGreedyClosure() {
        if (Op.COUNT)  Op.nofinstances ++;
        return new ChildOp(Op.NONGREEDYCLOSURE);
    }
    static ChildOp createQuestion(boolean nongreedy) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new ChildOp(nongreedy ? Op.NONGREEDYQUESTION : Op.QUESTION);
    }
    static RangeOp createRange(Token tok) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new RangeOp(Op.RANGE, tok);
    }
    static ChildOp createLook(int type, Op next, Op branch) {
        if (Op.COUNT)  Op.nofinstances ++;
        ChildOp op = new ChildOp(type);
        op.setChild(branch);
        op.next = next;
        return op;
    }
    static CharOp createBackReference(int refno) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new CharOp(Op.BACKREFERENCE, refno);
    }
    static StringOp createString(String literal) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new StringOp(Op.STRING, literal);
    }
    static ChildOp createIndependent(Op next, Op branch) {
        if (Op.COUNT)  Op.nofinstances ++;
        ChildOp op = new ChildOp(Op.INDEPENDENT);
        op.setChild(branch);
        op.next = next;
        return op;
    }
    static ModifierOp createModifier(Op next, Op branch, int add, int mask) {
        if (Op.COUNT)  Op.nofinstances ++;
        ModifierOp op = new ModifierOp(Op.MODIFIER, add, mask);
        op.setChild(branch);
        op.next = next;
        return op;
    }
    static ConditionOp createCondition(Op next, int ref, Op conditionflow, Op yesflow, Op noflow) {
        if (Op.COUNT)  Op.nofinstances ++;
        ConditionOp op = new ConditionOp(Op.CONDITION, ref, conditionflow, yesflow, noflow);
        op.next = next;
        return op;
    }

    int type;
    Op next = null;

    protected Op(int type) {
        this.type = type;
    }

    int size() {                                // for UNION
        return 0;
    }
    Op elementAt(int index) {                   // for UNIoN
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    Op getChild() {                             // for CLOSURE, QUESTION
        throw new RuntimeException("Internal Error: type="+this.type);
    }
                                                // ModifierOp
    int getData() {                             // CharOp  for CHAR, BACKREFERENCE, CAPTURE, ANCHOR, 
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    int getData2() {                            // ModifierOp
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    RangeToken getToken() {                     // RANGE, NRANGE
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    String getString() {                        // STRING
        throw new RuntimeException("Internal Error: type="+this.type);
    }

    // ================================================================
    static class CharOp extends Op {
        int charData;
        CharOp(int type, int data) {
            super(type);
            this.charData = data;
        }
        int getData() {
            return this.charData;
        }
    }

    // ================================================================
    static class UnionOp extends Op {
        Vector branches;
        UnionOp(int type, int size) {
            super(type);
            this.branches = new Vector(size);
        }
        void addElement(Op op) {
            this.branches.addElement(op);
        }
        int size() {
            return this.branches.size();
        }
        Op elementAt(int index) {
            return (Op)this.branches.elementAt(index);
        }
    }

    // ================================================================
    static class ChildOp extends Op {
        Op child;
        ChildOp(int type) {
            super(type);
        }
        void setChild(Op child) {
            this.child = child;
        }
        Op getChild() {
            return this.child;
        }
    }
    // ================================================================
    static class ModifierOp extends ChildOp {
        int v1;
        int v2;
        ModifierOp(int type, int v1, int v2) {
            super(type);
            this.v1 = v1;
            this.v2 = v2;
        }
        int getData() {
            return this.v1;
        }
        int getData2() {
            return this.v2;
        }
    }
    // ================================================================
    static class RangeOp extends Op {
        Token tok;
        RangeOp(int type, Token tok) {
            super(type);
            this.tok = tok;
        }
        RangeToken getToken() {
            return (RangeToken)this.tok;
        }
    }
    // ================================================================
    static class StringOp extends Op {
        String string;
        StringOp(int type, String literal) {
            super(type);
            this.string = literal;
        }
        String getString() {
            return this.string;
        }
    }
    // ================================================================
    static class ConditionOp extends Op {
        int refNumber;
        Op condition;
        Op yes;
        Op no;
        ConditionOp(int type, int refno, Op conditionflow, Op yesflow, Op noflow) {
            super(type);
            this.refNumber = refno;
            this.condition = conditionflow;
            this.yes = yesflow;
            this.no = noflow;
        }
    }
}
