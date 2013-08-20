/*
 * @(#)SourceOrderDeclScanner.java	1.4 04/05/03
 *
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package com.sun.mirror.util;

import com.sun.mirror.declaration.*;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A visitor for declarations that scans declarations contained within
 * the given declaration in source code order.  For example, when
 * visiting a class, the methods, fields, constructors, and nested
 * types of the class are also visited.
 *
 * To control the processing done on a declaration, users of this
 * class pass in their own visitors for pre and post processing.  The
 * preprocessing visitor is called before the contained declarations
 * are scanned; the postprocessing visitor is called after the
 * contained declarations are scanned.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @version 1.4 04/05/03
 * @since 1.5
 */
class SourceOrderDeclScanner extends DeclarationScanner {
    static class SourceOrderComparator implements java.util.Comparator<Declaration> {
	SourceOrderComparator(){}
	
	public int compare(Declaration d1, Declaration d2) {
	    if (d1 == d2)
		return 0;

	    SourcePosition p1 = d1.getPosition();
	    SourcePosition p2 = d2.getPosition();

	    if (p1 == null)
		return (p2 == null) ?0:1 ;
	    else {
		if (p2 == null)
		    return -1;
		
		int fileComp = p1.file().compareTo(p2.file()) ;
		if (fileComp == 0) {
		    long diff = (long)p1.line() - (long)p2.line();
		    if (diff == 0) {
			diff = Long.signum((long)p1.column() - (long)p2.column());
			if (diff != 0)
			    return (int)diff;
			else { 
			    // declarations may be two
			    // compiler-generated members with the
			    // same source position
			    return (int)( Long.signum((long)System.identityHashCode(d1) -
						      (long)System.identityHashCode(d2)));
			}
		    } else
			return (diff<0)? -1:1;
		} else
		    return fileComp; 
	    }
	}
    }

    final static java.util.Comparator<Declaration> comparator = new SourceOrderComparator();

    SourceOrderDeclScanner(DeclarationVisitor pre, DeclarationVisitor post) {
	super(pre, post);
    }

    /**
     * Visits a type declaration.
     *
     * @param d the declaration to visit
     */
    public void visitTypeDeclaration(TypeDeclaration d) {
	d.accept(pre);

	SortedSet<Declaration> decls = new 
	    TreeSet<Declaration>(SourceOrderDeclScanner.comparator) ;

	for(TypeParameterDeclaration tpDecl: d.getFormalTypeParameters()) {
	    decls.add(tpDecl);
	}
	
	for(FieldDeclaration fieldDecl: d.getFields()) {
	    decls.add(fieldDecl);
	}
	
	for(MethodDeclaration methodDecl: d.getMethods()) {
	    decls.add(methodDecl);
	}
	
	for(TypeDeclaration typeDecl: d.getNestedTypes()) {
	    decls.add(typeDecl);
	}

	for(Declaration decl: decls )
	    decl.accept(this);

	d.accept(post);
    }

    /**
     * Visits a class declaration.
     *
     * @param d the declaration to visit
     */
    public void visitClassDeclaration(ClassDeclaration d) {
	d.accept(pre);

	SortedSet<Declaration> decls = new 
	    TreeSet<Declaration>(SourceOrderDeclScanner.comparator) ;

	for(TypeParameterDeclaration tpDecl: d.getFormalTypeParameters()) {
	    decls.add(tpDecl);
	}
	
	for(FieldDeclaration fieldDecl: d.getFields()) {
	    decls.add(fieldDecl);
	}
	
	for(MethodDeclaration methodDecl: d.getMethods()) {
	    decls.add(methodDecl);
	}
	
	for(TypeDeclaration typeDecl: d.getNestedTypes()) {
	    decls.add(typeDecl);
	}

	for(ConstructorDeclaration ctorDecl: d.getConstructors()) {
	    decls.add(ctorDecl);
	}

	for(Declaration decl: decls )
	    decl.accept(this);

	d.accept(post);
    }
    
    public void visitExecutableDeclaration(ExecutableDeclaration d) {
	d.accept(pre);

	SortedSet<Declaration> decls = new 
	    TreeSet<Declaration>(SourceOrderDeclScanner.comparator) ;
	
	for(TypeParameterDeclaration tpDecl: d.getFormalTypeParameters())
	    decls.add(tpDecl);

	for(ParameterDeclaration pDecl: d.getParameters())
	    decls.add(pDecl);

	for(Declaration decl: decls )
	    decl.accept(this);

	d.accept(post);
    }

}
