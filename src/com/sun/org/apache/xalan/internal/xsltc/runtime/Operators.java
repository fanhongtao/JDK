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
 * $Id: Operators.java,v 1.3 2004/02/16 22:55:54 minchau Exp $
 */

package com.sun.org.apache.xalan.internal.xsltc.runtime;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
public interface Operators {
    public static final int EQ = 0;
    public static final int NE = 1;
    public static final int GT = 2;
    public static final int LT = 3;
    public static final int GE = 4;
    public static final int LE = 5;
	
    public static final String[] names = {
	"=", "!=", ">", "<", ">=", "<="
    };
}
