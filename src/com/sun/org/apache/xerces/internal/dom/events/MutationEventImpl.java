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

package com.sun.org.apache.xerces.internal.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

/**
 * @version $Id: MutationEventImpl.java,v 1.7 2002/08/09 15:18:16 neilg Exp $
 */

public class MutationEventImpl 
extends com.sun.org.apache.xerces.internal.dom.events.EventImpl 
implements MutationEvent
{
    Node relatedNode=null;
    String prevValue=null,newValue=null,attrName=null;
    // REVISIT: The DOM Level 2 PR has a bug: the init method should let this
    // attribute be specified. Since it doesn't we have to give write access.
    public short attrChange;
    
    // NON-DOM CONSTANTS: Storage efficiency, avoid risk of typos.
    public static final String DOM_SUBTREE_MODIFIED = "DOMSubtreeModified";
    public static final String DOM_NODE_INSERTED = "DOMNodeInserted";
    public static final String DOM_NODE_REMOVED = "DOMNodeRemoved";
    public static final String DOM_NODE_REMOVED_FROM_DOCUMENT = "DOMNodeRemovedFromDocument";
    public static final String DOM_NODE_INSERTED_INTO_DOCUMENT = "DOMNodeInsertedIntoDocument";
    public static final String DOM_ATTR_MODIFIED = "DOMAttrModified";
    public static final String DOM_CHARACTER_DATA_MODIFIED = "DOMCharacterDataModified";

    /** @return the name of the Attr which
        changed, for DOMAttrModified events. 
        Undefined for others.
        */
    public String getAttrName()
    {
        return attrName;
    }

    /**
     *  <code>attrChange</code> indicates the type of change which triggered 
     * the DOMAttrModified event. The values can be <code>MODIFICATION</code>
     * , <code>ADDITION</code>, or <code>REMOVAL</code>. 
     */
    public short getAttrChange()
    {
        return attrChange;
    }

    /** @return the new string value of the Attr for DOMAttrModified events, or
        of the CharacterData node for DOMCharDataModifed events.
        Undefined for others.
        */
    public String getNewValue()
    {
        return newValue;
    }

    /** @return the previous string value of the Attr for DOMAttrModified events, or
        of the CharacterData node for DOMCharDataModifed events.
        Undefined for others.
        */
    public String getPrevValue()
    {
        return prevValue;
    }

    /** @return a Node related to this event, other than the target that the
        node was dispatched to. For DOMNodeRemoved, it is the node which
        was removed. 
        No other uses are currently defined.
        */
    public Node getRelatedNode()
    {
        return relatedNode;
    }

    /** Initialize a mutation event, or overwrite the event's current
        settings with new values of the parameters. 
        */
    public void initMutationEvent(String typeArg, boolean canBubbleArg, 
        boolean cancelableArg, Node relatedNodeArg, String prevValueArg, 
        String newValueArg, String attrNameArg, short attrChangeArg)
    {
        relatedNode=relatedNodeArg;
        prevValue=prevValueArg;
        newValue=newValueArg;
        attrName=attrNameArg;
        attrChange=attrChangeArg;
        super.initEvent(typeArg,canBubbleArg,cancelableArg);
    }

}
