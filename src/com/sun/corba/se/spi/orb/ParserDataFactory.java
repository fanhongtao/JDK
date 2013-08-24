/*
 * @(#)ParserDataFactory.java	1.6 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.spi.orb ;

import com.sun.corba.se.impl.orb.NormalParserData ;
import com.sun.corba.se.impl.orb.PrefixParserData ;

public class ParserDataFactory {
    public static ParserData make( String  propertyName,
	Operation operation, String fieldName, Object defaultValue,
	Object testValue, String testData )
    {
	return new NormalParserData( propertyName, operation, fieldName,
	    defaultValue, testValue, testData ) ;
    }

    public static ParserData make( String  propertyName,
	Operation operation, String fieldName, Object defaultValue,
	Object testValue, StringPair[] testData, Class componentType )
    {
	return new PrefixParserData( propertyName, operation, fieldName,
	    defaultValue, testValue, testData, componentType ) ;
    }
}
