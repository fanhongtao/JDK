/*
 * @(#)typedefs.h	1.17 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef	_TYPEDEFS_H_
#define	_TYPEDEFS_H_

#include "typedefs_md.h"	/* for int64_t */

/*
 * Macros to deal with the JavaVM's stack alignment. Many machines
 * require doublewords to be double aligned.  This union is used by
 * code in math.h as a more portable way do alingnment on machines
 * that require it.  This union and the macros that use it came from
 * Netscape.
 */

typedef union Java8Str {
    int32_t x[2];
    double d;
    int64_t l;
    void *p;
} Java8;


#ifdef HAVE_ALIGNED_LONGLONGS
#define GET_INT64(_t,_addr) ( ((_t).x[0] = ((int32_t*)(_addr))[0]), \
                              ((_t).x[1] = ((int32_t*)(_addr))[1]), \
                              (_t).l )
#define SET_INT64(_t, _addr, _v) ( (_t).l = (_v),                    \
                                   ((int32_t*)(_addr))[0] = (_t).x[0], \
                                   ((int32_t*)(_addr))[1] = (_t).x[1] )
#else
#define GET_INT64(_t,_addr) (*(int64_t*)(_addr))
#define SET_INT64(_t, _addr, _v) (*(int64_t*)(_addr) = (_v))
#endif

/* If double's must be aligned on doubleword boundaries then define this */
#ifdef HAVE_ALIGNED_DOUBLES
#define GET_DOUBLE(_t,_addr) ( ((_t).x[0] = ((int32_t*)(_addr))[0]), \
                               ((_t).x[1] = ((int32_t*)(_addr))[1]), \
                               (_t).d )
#define SET_DOUBLE(_t, _addr, _v) ( (_t).d = (_v),                    \
                                    ((int32_t*)(_addr))[0] = (_t).x[0], \
                                    ((int32_t*)(_addr))[1] = (_t).x[1] )
#else
#define GET_DOUBLE(_t,_addr) (*(double*)(_addr))
#define SET_DOUBLE(_t, _addr, _v) (*(double*)(_addr) = (_v))
#endif

/* If pointers are 64bits then define this */
#ifdef HAVE_64BIT_POINTERS
#define GET_HANDLE(_t,_addr) ( ((_t).x[0] = ((int32_t*)(_addr))[0]), \
                               ((_t).x[1] = ((int32_t*)(_addr))[1]), \
                               (_t).p )
#define SET_HANDLE(_t, _addr, _v) ( (_t).p = (_v),                    \
                                    ((int32_t*)(_addr))[0] = (_t).x[0], \
                                    ((int32_t*)(_addr))[1] = (_t).x[1] )
#else
#define GET_HANDLE(_t,_addr) (*(JHandle*)(_addr))
#define SET_HANDLE(_t, _addr, _v) (*(JHandle*)(_addr) = (_v))
#endif


#endif	/* !_TYPEDEFS_H_ */
