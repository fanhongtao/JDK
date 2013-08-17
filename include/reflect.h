/*
 * @(#)reflect.h	1.24 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

#ifndef	_REFLECT_H
#define	_REFLECT_H

#include "oobj.h"
#include "native.h"

/*
 * Keep consistent with constants in java.lang.reflect.Member
 */
enum { MEMBER_PUBLIC, MEMBER_DECLARED };

extern HObject *	reflect_field(ClassClass *, char *, long);
extern HArrayOfObject *	reflect_fields(ClassClass *, long);

extern HObject *	reflect_method(ClassClass *, char *, HArrayOfObject *,
			    long);
extern HArrayOfObject *	reflect_methods(ClassClass *, long);

extern HObject *	reflect_constructor(ClassClass *, HArrayOfObject *,
			    long);
extern HArrayOfObject *	reflect_constructors(ClassClass *, long);

extern ClassClass *	reflect_find_class(char *, ClassClass *, char **);
extern bool_t		reflect_check_access(ExecEnv *, ClassClass *, int,
			    ClassClass *);
extern HObject *	reflect_new_array(ClassClass *, int);
extern HArrayOfObject *	reflect_new_class_array(int);
extern bool_t		reflect_is_assignable(ClassClass *, ClassClass *,
			    ExecEnv *);

/*
 * Implementation
 */

#ifdef _REFLECT_IMPL

/*
 * "Generic" accessors for Field, Method, Constructor
 */
#define	member_class(h)		(unhand(h)->clazz)
#define	member_slot(h)		(unhand(h)->slot)
#define	member_name(h)		(unhand(h)->name)
#define	member_type(h)		(unhand(h)->type)
#define	member_returnType(h)	(unhand(h)->returnType)
#define	member_parameterTypes(h)(unhand(h)->parameterTypes)
#define	member_exceptionTypes(h)(unhand(h)->exceptionTypes)


#define	REFLECT_GET(p, pCode, v, packed)				\
if (TRUE)								\
{									\
    switch (pCode) {							\
    case T_BOOLEAN:							\
    case T_BYTE:							\
	if (packed) { v.i = *(char *) (p); break; }			\
    case T_CHAR:							\
	if (packed) { v.i = *(unicode *) (p); break; }			\
    case T_SHORT:							\
	if (packed) { v.i = *(short *) (p); break; }			\
    case T_INT:								\
	v.i = *(long *) (p); break;					\
    case T_FLOAT:							\
	v.f = *(float *) (p); break;					\
    case T_LONG:							\
	v.l = GET_INT64(v, p); break;					\
    case T_DOUBLE:							\
	v.d = GET_DOUBLE(v, p); break;					\
    default:								\
	v.h = *(HObject **) (p);					\
    }									\
} else

#define	REFLECT_SET(p, pCode, v, packed)				\
if (TRUE)								\
{									\
    switch (pCode) {							\
    case T_BOOLEAN:							\
    case T_BYTE:							\
	if (packed) { *(char *) (p) = v.i; break; }			\
    case T_CHAR:							\
	if (packed) { *(unicode *) (p) = v.i; break; }			\
    case T_SHORT:							\
	if (packed) { *(short *) (p) = v.i; break; }			\
    case T_INT:								\
	*(long *) (p) = v.i ; break;					\
    case T_FLOAT:							\
	*(float *) (p) = v.f; break;					\
    case T_LONG:							\
	SET_INT64(v, p, v.l); break;					\
    case T_DOUBLE:							\
	SET_DOUBLE(v, p, v.d); break;					\
    default:								\
	*(HObject **) (p) = v.h;					\
    }									\
} else

#define	REFLECT_WIDEN(v, vCode, wCode, fail)				\
if (TRUE)								\
{									\
    switch(wCode) {							\
    case T_BOOLEAN:							\
    case T_BYTE:							\
    case T_CHAR:							\
	goto fail;							\
    case T_SHORT:							\
	switch (vCode) {						\
	case T_BYTE:							\
	    break;							\
	default:							\
	    goto fail;							\
	}								\
	break;								\
    case T_INT:								\
	switch (vCode) {						\
	case T_BYTE:							\
	case T_CHAR:							\
	case T_SHORT:							\
	    break;							\
	default:							\
	    goto fail;							\
	}								\
	break;								\
    case T_LONG:							\
	switch (vCode) {						\
	case T_BYTE:							\
	case T_CHAR:							\
	case T_SHORT:							\
	case T_INT:							\
	    v.l = int2ll(v.i); break;						\
	default:							\
	    goto fail;							\
	}								\
	break;								\
    case T_FLOAT:							\
	switch (vCode) {						\
	case T_BYTE:							\
	case T_CHAR:							\
	case T_SHORT:							\
	case T_INT:							\
	    v.f = (float) v.i; break;					\
	case T_LONG:							\
	    v.f = (float) ll2float(v.l); break;					\
	default:							\
	    goto fail;							\
	}								\
	break;								\
    case T_DOUBLE:							\
	switch (vCode) {						\
	case T_BYTE:							\
	case T_CHAR:							\
	case T_SHORT:							\
	case T_INT:							\
	    v.d = (double) v.i; break;					\
	case T_FLOAT:							\
	    v.d = (double) v.f; break;					\
	case T_LONG:							\
	    v.d = (double) ll2double(v.l); break;					\
	default:							\
	    goto fail;							\
	}								\
	break;								\
    default:								\
	goto fail;							\
    }									\
} else

/*
 *
 */

#define	REFLECT_ERROR(nm, det)						\
	SignalError(0, JAVAPKG #nm, det)

#define	OutOfMemoryError()						\
	REFLECT_ERROR(OutOfMemoryError, 0)

#define	NullPointerException()						\
	REFLECT_ERROR(NullPointerException, 0)

#define	InternalError(det)						\
	REFLECT_ERROR(InternalError, det)

/*
 *
 */

#define	ENSURE_RESOLVED(cb)						\
    if (!CCIs(cb, Resolved)) {						\
	char   *detail = NULL;						\
	char   *exception = ResolveClass(cb, &detail);			\
	if (exception != NULL) {					\
	    SignalError(0, exception, detail);				\
	    return NULL;						\
	}								\
    }

/*
 *
 */

#define	fbClass(fb)	(fb->clazz)
#define	fbName(fb)	(fb->name)
#define	fbSig(fb)	(fb->signature)
#define	fbAccess(fb)	(fb->access)
#define	fbID(fb)	(fb->ID)
#define	fbIsArray(fb)	(fbSig(fb)[0] == SIGNATURE_ARRAY)
#define	fbIsClass(fb)	(fbSig(fb)[0] == SIGNATURE_CLASS)
#define	fbIsPublic(fb)	((fbAccess(fb) & ACC_PUBLIC) != 0)
#define	fbIsStatic(fb)	((fbAccess(fb) & ACC_STATIC) != 0)
#define	fbIsFinal(fb)	((fbAccess(fb) & ACC_FINAL) != 0)
#define	fbOffset(fb)	(fb->u.offset)

#define	mbFb(mb)	(&mb->fb)
#define	mbClass(mb)	(mb->fb.clazz)
#define	mbName(mb)	(mb->fb.name)
#define	mbSig(mb)	(mb->fb.signature)
#define	mbAccess(mb)	(mb->fb.access)
#define	mbOffset(mb)	(mb->fb.u.offset)
#define	mbID(mb)	(mb->fb.ID)
#define	mbArgsSize(mb)	(mb->args_size)
#define	mbIsSpecial(mb)	(mbName(mb)[0] == '<')
#define	mbIsPublic(mb)	((mbAccess(mb) & ACC_PUBLIC) != 0)
#define	mbIsStatic(mb)	((mbAccess(mb) & ACC_STATIC) != 0)

#endif /* _REFLECT_IMPL */

#endif /* _REFLECT_H */
