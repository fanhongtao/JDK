/*
 * @(#)reflect.h	1.38 98/09/16
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

#ifndef _JAVASOFT_REFLECT_H
#define _JAVASOFT_REFLECT_H

#include "oobj.h"
#include "native.h"
#include "jni.h"

/*
 * Keep consistent with constants in java.lang.reflect.Member
 */
enum { MEMBER_PUBLIC, MEMBER_DECLARED };

extern HObject *	reflect_field(ExecEnv *, ClassClass *, char *,
			    int);
extern HArrayOfObject *	reflect_fields(ExecEnv *, ClassClass *, int);

extern HObject *	reflect_method(ExecEnv *, ClassClass *, char *,
			    HArrayOfObject *, int);
extern HArrayOfObject *	reflect_methods(ExecEnv *, ClassClass *, int);

extern HObject *	reflect_constructor(ExecEnv *, ClassClass *,
			    HArrayOfObject *, int);
extern HArrayOfObject *	reflect_constructors(ExecEnv *, ClassClass *, int);

extern ClassClass *	reflect_find_class(char *, ClassClass *,
			    char **);

extern bool_t		reflect_check_access(ExecEnv *, ClassClass *, int,
			    ClassClass *, bool_t for_invoke);

extern HObject *	reflect_new_array(ExecEnv *, ClassClass *, int);
extern HArrayOfObject *	reflect_new_class_array(ExecEnv *, int);

extern bool_t		reflect_is_assignable(ClassClass *, ClassClass *,
			    ExecEnv *);

extern HObject *	reflect_new_method(ExecEnv *, struct methodblock *);
extern HObject *	reflect_new_field(ExecEnv *, struct fieldblock *);

extern struct methodblock *	reflect_get_methodblock(HObject *obj);
extern struct fieldblock *	reflect_get_fieldblock(HObject *obj);

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
#define	member_modifiers(h)	(unhand(h)->modifiers)
#define	member_override(h)	(unhand(h)->override)

#define	REFLECT_GET(p, pCode, v, packed)                                \
{                                                                       \
    switch (pCode) {                                                    \
    case T_BOOLEAN:                                                     \
        v.z = (packed) ? (*(jboolean *) (p)) : ((jboolean) *(jint *) (p));  \
        break;                                                          \
    case T_BYTE:							\
        v.b = (packed) ? (*(jbyte *) (p)) : ((jbyte) *(jint *) (p));    \
        break;                                                          \
    case T_CHAR:							\
        v.c = (packed) ? (*(jchar *) (p)) : ((jchar) *(jint *) (p));    \
        break;                                                          \
    case T_SHORT:							\
        v.s = (packed) ? (*(jshort *) (p)) : ((jshort) *(jint *) (p));  \
        break;                                                          \
    case T_INT:							        \
        v.i = *(jint *) (p);                                            \
        break; 				                                \
    case T_FLOAT:							\
        v.f = *(jfloat *) (p);                                          \
        break;				                                \
    case T_LONG:                                                        \
        v.j = GET_INT64(v, p);                                          \
        break;				                                \
    case T_DOUBLE:							\
        v.d = GET_DOUBLE(v, p);                                         \
        break;				                                \
    default:							        \
        v.l = *(jobject *) (p);                                         \
        break;				                                \
    }                                                                   \
}

#define	REFLECT_SET(p, pCode, v, packed)                                \
{                                                                       \
    switch (pCode) {                                                    \
    case T_BOOLEAN:                                                     \
        if (packed) {                                                   \
            *(jboolean *) (p) = v.z;                                    \
        } else {                                                        \
            *(jint *) (p) = (jint) v.z;                                 \
        }                                                               \
        break;                                                          \
    case T_BYTE:							\
        if (packed) {                                                   \
            *(jbyte *) (p) = v.b;                                       \
        } else {                                                        \
            *(jint *) (p) = (jint) v.b;                                 \
        }                                                               \
        break;                                                          \
    case T_CHAR:							\
        if (packed) {                                                   \
            *(jchar *) (p) = v.c;                                       \
        } else {                                                        \
            *(jint *) (p) = (jint) v.c;                                 \
        }                                                               \
        break;                                                          \
    case T_SHORT:							\
        if (packed) {                                                   \
            *(jshort *) (p) = v.s;                                      \
        } else {                                                        \
            *(jint *) (p) = (jint) v.s;                                 \
        }                                                               \
        break;                                                          \
    case T_INT:							        \
        *(jint *) (p) = v.i; break; 				        \
    case T_FLOAT:							\
        *(jfloat *) (p) = v.f; break;				        \
    case T_LONG:							\
        SET_INT64(v, p, v.j); break;				        \
    case T_DOUBLE:							\
        SET_DOUBLE(v, p, v.d); break;				        \
    default:							        \
        *(jobject *) (p) = v.l; break;				        \
    }                                                                   \
}

#define	REFLECT_WIDEN(v, vCode, wCode, fail)				\
{									\
    switch(wCode) {							\
    case T_BOOLEAN:							\
    case T_BYTE:							\
    case T_CHAR:							\
	goto fail;							\
    case T_SHORT:							\
	switch (vCode) {						\
	case T_BYTE:							\
            v.s = (jshort) v.b; break;					\
	default:							\
	    goto fail;							\
	}								\
	break;								\
    case T_INT:								\
	switch (vCode) {						\
	case T_BYTE:							\
            v.i = (jint) v.b; break;                                    \
	case T_CHAR:							\
            v.i = (jint) v.c; break;                                    \
	case T_SHORT:							\
            v.i = (jint) v.s; break;                                    \
	default:							\
	    goto fail;							\
	}								\
	break;								\
    case T_LONG:							\
	switch (vCode) {						\
	case T_BYTE:							\
	    v.j = int2ll((jint) v.b); break;				\
	case T_CHAR:							\
            v.j = int2ll((jint) v.c); break;                            \
	case T_SHORT:							\
            v.j = int2ll((jint) v.s); break;                            \
	case T_INT:							\
            v.j = int2ll(v.i); break;                                   \
	default:							\
	    goto fail;							\
	}								\
	break;								\
    case T_FLOAT:							\
	switch (vCode) {						\
	case T_BYTE:							\
            v.f = (jfloat) v.b; break;                                  \
	case T_CHAR:							\
            v.f = (jfloat) v.c; break;                                  \
	case T_SHORT:							\
            v.f = (jfloat) v.s; break;                                  \
	case T_INT:							\
	    v.f = (jfloat) v.i; break;					\
	case T_LONG:							\
	    v.f = (jfloat) ll2float(v.j); break;			\
	default:							\
	    goto fail;							\
	}								\
	break;								\
    case T_DOUBLE:							\
	switch (vCode) {						\
	case T_BYTE:							\
            v.d = (jdouble) v.b; break;                                 \
	case T_CHAR:							\
            v.d = (jdouble) v.c; break;                                 \
	case T_SHORT:							\
            v.d = (jdouble) v.s; break;                                 \
	case T_INT:							\
	    v.d = (jdouble) v.i; break;					\
	case T_FLOAT:							\
	    v.d = (jdouble) v.f; break;					\
	case T_LONG:							\
	    v.d = (jdouble) ll2double(v.j); break;			\
	default:							\
	    goto fail;							\
	}								\
	break;								\
    default:								\
	goto fail;							\
    }									\
}

/*
 *
 */

#define	ENSURE_LINKED(ee, cb)						\
if (!CCIs(cb, Linked)) {						\
    LinkClass(cb);							\
    if (exceptionOccurred(ee))						\
	return 0; /* NULL, FALSE */					\
    sysAssert(CCIs(cb, Linked));					\
} else ((void) 0)

/* We can't put:
 *
 *  sysAssert(CCIs(cb, Initialized));
 *
 * in ENSURE_INITIALIZED because the semantics of class initialization
 * is such that it's perfectally legal for InitClass to return without
 * initializing the class if the current thread is in the middle of 
 * running <clinit>.
 */

#define	ENSURE_INITIALIZED(ee, cb)					\
if (!CCIs(cb, Initialized)) {						\
    InitClass(cb);							\
    if (exceptionOccurred(ee))						\
	return 0; /* NULL, FALSE */					\
} else ((void) 0)

/*
 *
 */

#define	fbClass(fb)	(fb->clazz)
#define	fbName(fb)	(fb->name)
#define	fbSig(fb)	(fb->signature)
#define	fbAccess(fb)	(fb->access)
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
#define	mbArgsSize(mb)	(mb->args_size)
#define	mbIsSpecial(mb)	(mbName(mb)[0] == '<')
#define	mbIsPublic(mb)	((mbAccess(mb) & ACC_PUBLIC) != 0)
#define	mbIsStatic(mb)	((mbAccess(mb) & ACC_STATIC) != 0)

#endif /* _REFLECT_IMPL */

#endif /* !_JAVASOFT_REFLECT_H */
