/*
 * @(#)RepIdDelegator_1_3_1.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.orbutil;

import org.omg.CORBA.ORB;
import java.io.Serializable;
import java.util.Hashtable;
import java.net.MalformedURLException;
import com.sun.corba.se.internal.io.TypeMismatchException;
import com.sun.corba.se.internal.util.RepositoryId;

/**
 * Delegates to the RepositoryId_1_3_1 implementation in
 * com.sun.corba.se.internal.orbutil.  This is necessary to
 * overcome the fact that many of RepositoryId's methods
 * are static.
 */
public final class RepIdDelegator_1_3_1
    implements RepositoryIdStrings, 
               RepositoryIdUtility,
               RepositoryIdInterface
{
    // RepositoryIdFactory methods

    public String createForAnyType(Class type) {
        return RepositoryId_1_3_1.createForAnyType(type);
    }

    public String createForJavaType(Serializable ser)
        throws TypeMismatchException
    {
        return RepositoryId_1_3_1.createForJavaType(ser);
    }
               
    public String createForJavaType(Class clz)
        throws TypeMismatchException
    {
        return RepositoryId_1_3_1.createForJavaType(clz);
    }

    public String createSequenceRepID(java.lang.Object ser) {
        return RepositoryId_1_3_1.createSequenceRepID(ser);
    }

    public String createSequenceRepID(Class clazz) {
        return RepositoryId_1_3_1.createSequenceRepID(clazz);
    }

    public RepositoryIdInterface getFromString(String repIdString) {
        return new RepIdDelegator_1_3_1(RepositoryId_1_3_1.cache.getId(repIdString));
    }

    // RepositoryIdUtility methods
    
    public boolean isChunkedEncoding(int valueTag) {
        return RepositoryId.isChunkedEncoding(valueTag);
    }

    public boolean isCodeBasePresent(int valueTag) {
        return RepositoryId.isCodeBasePresent(valueTag);
    }

    public String getClassDescValueRepId() {
        return RepositoryId_1_3_1.kClassDescValueRepID;
    }

    public String getWStringValueRepId() {
        return RepositoryId_1_3_1.kWStringValueRepID;
    }

    public int getTypeInfo(int valueTag) {
        return RepositoryId.getTypeInfo(valueTag);
    }

    public int getStandardRMIChunkedNoRepStrId() {
        return RepositoryId.kPreComputed_StandardRMIChunked_NoRep;
    }

    public int getCodeBaseRMIChunkedNoRepStrId() {
        return RepositoryId.kPreComputed_CodeBaseRMIChunked_NoRep;
    }

    public int getStandardRMIChunkedId() {
        return RepositoryId.kPreComputed_StandardRMIChunked;
    }

    public int getCodeBaseRMIChunkedId() {
        return RepositoryId.kPreComputed_CodeBaseRMIChunked;
    }

    public int getStandardRMIUnchunkedId() {
        return RepositoryId.kPreComputed_StandardRMIUnchunked;
    }

    public int getCodeBaseRMIUnchunkedId() {
        return RepositoryId.kPreComputed_CodeBaseRMIUnchunked;
    }

    // RepositoryIdInterface methods

    public Class getClassFromType() throws ClassNotFoundException {
        return delegate.getClassFromType();
    }

    public Class getClassFromType(String codebaseURL) 
        throws ClassNotFoundException, MalformedURLException
    {
        return delegate.getClassFromType(codebaseURL);
    }

    public Class getClassFromType(Class expectedType,
                                  String codebaseURL) 
        throws ClassNotFoundException, MalformedURLException
    {
        return delegate.getClassFromType(expectedType, codebaseURL);
    }

    public String getClassName() {
        return delegate.getClassName();
    }

    // Constructor used for factory/utility cases
    public RepIdDelegator_1_3_1() {}

    // Constructor used by getIdFromString.  All non-static
    // RepositoryId methods will use the provided delegate.
    private RepIdDelegator_1_3_1(RepositoryId_1_3_1 _delegate) {
        this.delegate = _delegate;
    }

    private RepositoryId_1_3_1 delegate = null;

    public String toString() {
        if (delegate != null)
            return delegate.toString();
        else
            return this.getClass().getName();
    }

    public boolean equals(Object obj) {
        if (delegate != null)
            return delegate.equals(obj);
        else
            return super.equals(obj);
    }
}
