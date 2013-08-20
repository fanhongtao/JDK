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

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

/** EventImpl is an implementation of the basic "generic" DOM Level 2 Event
    object. It may be subclassed by more specialized event sets.
    Note that in our implementation, events are re-dispatchable (dispatch
    clears the stopPropagation and preventDefault flags before it starts);
    I believe that is the DOM's intent but I don't see an explicit statement
    to this effect.
*/
/**
 * @version $Id: EventImpl.java,v 1.8 2003/05/08 19:52:41 elena Exp $
 */
public class EventImpl implements Event
{
    public String type=null;
    public EventTarget target;
    public EventTarget currentTarget;
    public short eventPhase;
    public boolean initialized=false, bubbles=true, cancelable=false;
    public boolean stopPropagation=false, preventDefault=false;
     
    protected long timeStamp = System.currentTimeMillis();

    /** The DOM doesn't deal with constructors, so instead we have an
        initializer call to set most of the read-only fields. The
        others are set, and reset, by the event subsystem during dispatch.
        <p>
        Note that init() -- and the subclass-specific initWhatever() calls --
        may be reinvoked. At least one initialization is required; repeated
        initializations overwrite the event with new values of their
        parameters.
    */
    public void initEvent(String eventTypeArg, boolean canBubbleArg, 
                        boolean cancelableArg)
    {
            type=eventTypeArg;
            bubbles=canBubbleArg;
            cancelable=cancelableArg;
            
            initialized=true;
    }
    
    /** @return true iff this Event is of a class and type which supports
        bubbling. In the generic case, this is True.
        */
    public boolean getBubbles()
    {
        return bubbles;
    }

    /** @return true iff this Event is of a class and type which (a) has a
        Default Behavior in this DOM, and (b)allows cancellation (blocking)
        of that behavior. In the generic case, this is False.
        */
    public boolean getCancelable()
    {
        return cancelable;
    }

    /** @return the Node (EventTarget) whose EventListeners are currently
        being processed. During capture and bubble phases, this may not be
        the target node. */
    public EventTarget getCurrentTarget()
    {
        return currentTarget;
    }

    /** @return the current processing phase for this event -- 
        CAPTURING_PHASE, AT_TARGET, BUBBLING_PHASE. (There may be
        an internal DEFAULT_PHASE as well, but the users won't see it.) */
    public short getEventPhase()
    {
        return eventPhase;
    }

    /** @return the EventTarget (Node) to which the event was originally
        dispatched.
        */
    public EventTarget getTarget()
    {
        return target;
    }

    /** @return event name as a string
    */
    public String getType()
    {
        return type;
    }

    public long getTimeStamp() {
	return timeStamp;
    }

    /** Causes exit from in-progress event dispatch before the next
        currentTarget is selected. Replaces the preventBubble() and 
        preventCapture() methods which were present in early drafts; 
        they may be reintroduced in future levels of the DOM. */
    public void stopPropagation()
    {
        stopPropagation=true;
    }

    /** Prevents any default processing built into the target node from
        occurring.
      */
    public void preventDefault()
    {
        preventDefault=true;
    }

}
