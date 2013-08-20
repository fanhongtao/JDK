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
 * $Id: Axis.java,v 1.5 2004/02/16 22:54:59 minchau Exp $
 */

package com.sun.org.apache.xalan.internal.xsltc.dom;

/*
 * IMPORTANT NOTE - this interface will probably be replaced by
 * com.sun.org.apache.xml.internal.dtm.Axis (very similar)
 */

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 */
public interface Axis extends com.sun.org.apache.xml.internal.dtm.Axis
{
    public static final boolean[] isReverse = {
	true,  // ancestor
	true,  // ancestor-or-self
	false, // attribute
	false, // child
	false, // descendant
	false, // descendant-or-self
	false, // following
	false, // following-sibling
	false, // namespace
	false, // namespace-declarations
	false, // parent (one node, has no order)
	true,  // preceding
	true,  // preceding-sibling
	false  // self (one node, has no order)
    };
}
