/*
 * @(#)hprof_stack.c	1.9 05/11/17
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/* Simple stack storage mechanism (or simple List). */

/* 
 * Stack is any depth (grows as it needs to), elements are arbitrary
 *   length but known at stack init time.
 *
 * Stack elements can be accessed via pointers (be careful, if stack
 *   moved while you point into stack you have problems)
 *
 * Pointers to stack elements passed in are copied.
 *
 * Since the stack can be inspected, it can be used for more than just
 *    a simple stack.
 *
 */

#include "hprof.h"

static void
resize(Stack *stack)
{
    void  *old_elements;
    void  *new_elements;
    int    old_size;
    int    new_size;

    HPROF_ASSERT(stack!=NULL);
    HPROF_ASSERT(stack->elements!=NULL);
    HPROF_ASSERT(stack->size>0);
    HPROF_ASSERT(stack->elem_size>0);
    HPROF_ASSERT(stack->incr_size>0);
    old_size     = stack->size;
    old_elements = stack->elements;
    if ( (stack->resizes % 10) && stack->incr_size < (old_size >> 2) ) {
	stack->incr_size = old_size >> 2; /* 1/4 the old_size */
    }
    new_size = old_size + stack->incr_size;
    new_elements = HPROF_MALLOC(new_size*stack->elem_size);
    (void)memcpy(new_elements, old_elements, old_size*stack->elem_size);
    stack->size     = new_size;
    stack->elements = new_elements;
    HPROF_FREE(old_elements);
    stack->resizes++;
}

Stack *
stack_init(int init_size, int incr_size, int elem_size)
{
    Stack *stack;
    void  *elements;

    HPROF_ASSERT(init_size>0);
    HPROF_ASSERT(elem_size>0);
    HPROF_ASSERT(incr_size>0);
    stack            = (Stack*)HPROF_MALLOC((int)sizeof(Stack));
    elements         = HPROF_MALLOC(init_size*elem_size);
    stack->size      = init_size;
    stack->incr_size = incr_size;
    stack->elem_size = elem_size;
    stack->count       = 0;
    stack->elements  = elements;
    stack->resizes   = 0;
    return stack;
}

void *
stack_element(Stack *stack, int i)
{
    HPROF_ASSERT(stack!=NULL);
    HPROF_ASSERT(stack->elements!=NULL);
    HPROF_ASSERT(stack->count>i);
    HPROF_ASSERT(i>=0);
    return (void*)(((char*)stack->elements) + i * stack->elem_size);
}

void *
stack_top(Stack *stack)
{
    void *element;

    HPROF_ASSERT(stack!=NULL);
    element = NULL;
    if ( stack->count > 0 ) {
	element = stack_element(stack, (stack->count-1));
    }
    return element;
}

int
stack_depth(Stack *stack)
{
    HPROF_ASSERT(stack!=NULL);
    return stack->count;
}

void *
stack_pop(Stack *stack)
{
    void *element;

    element = stack_top(stack);
    if ( element != NULL ) {
	stack->count--;
    }
    return element;
}

void   
stack_push(Stack *stack, void *element)
{
    void *top_element;
    
    HPROF_ASSERT(stack!=NULL);
    if ( stack->count >= stack->size ) {
	resize(stack);
    }
    stack->count++;
    top_element = stack_top(stack);
    (void)memcpy(top_element, element, stack->elem_size);
}

void
stack_term(Stack *stack)
{
    HPROF_ASSERT(stack!=NULL);
    if ( stack->elements != NULL ) {
	HPROF_FREE(stack->elements);
    }
    HPROF_FREE(stack);
}

