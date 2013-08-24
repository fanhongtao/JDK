/*
 * @(#)NormalParserData.java	1.6 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.orb ;

import java.util.Properties ;

import com.sun.corba.se.spi.orb.Operation ;
import com.sun.corba.se.spi.orb.PropertyParser ;

public class NormalParserData extends ParserDataBase {
    private String testData ;

    public NormalParserData( String  propertyName,
	Operation operation, String fieldName, Object defaultValue,
	Object testValue, String testData )
    {
	super( propertyName, operation, fieldName, defaultValue, testValue ) ;
	this.testData = testData ;
    }
    public void addToParser( PropertyParser parser ) 
    {
	parser.add( getPropertyName(), getOperation(), getFieldName() ) ;
    }

    public void addToProperties( Properties props ) 
    {
	props.setProperty( getPropertyName(), testData ) ;
    }
}

