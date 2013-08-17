/*
 * @(#)typecodes.h	1.10 98/07/01
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

/*
 * Type codes  6/12/91

	This typecode system allows us to represent the type
	of scalars in a uniform way. For instance, all integer types
	have some bits in common, and are distinguished by a built-in
	size field. Types without multiple sizes don't have a size field.

	Scalars may only have sizes which are powers of 2. The size
	field holds the log-based-2 of the object's size.

	All run-time types can be encoded in 4 bits. There are more
	compile- time types. These fit in 5 bits.
	Schematically, we have:
		+----+----+----+----+----+
		| c  |    t    |    s    |
		+----+----+----+----+----+

		Encoding is:
		   c   t  s	type
		-------------------------
		   0  00 00	unassigned
		   0  00 01	array
		   0  00 10	class
		   0  00 11	proxy    (OBSOLETE)
		   0  01 00	boolean (1 byte)
		   0  01 01	char	(2 bytes)
		   0  01 1s	float (single=1; double=2)
		   0  1u xx	integer (byte=0; short=1; int=2; long=3;
					 u=1 => unsigned)

			For runtime types, the size of an object
			is 1<<(t&3)

		   1  00 00	typedef	(compiler only)
		   1  00 01	void	(compiler only)
		   1  00 10	func    (compiler only)
		   1  00 11	unknown (compiler only)
		   1  01 00     error   (compiler only)

	Char and Boolean are not int's because they need a different signature,
	so have to be distinguishable, even at runtime. We allow arrays
	of objects, arrays(?), booleans, char, integers, and floats.
	Note that the low-order two bits of all these gives the log of
	the size, except for arrays, of course.

	I would prefer not to have unsigned int in the language, but
	don't want to make that decision at this level. We could come up
	with a better encoding of boolean and char if there were no
	unsigned.

	The compile-only type values that could be confused with the 
	integer and float scalar types must not ever be used. Value 0 must
	not be assigned a runtime type, as this is used for some sleazy
	trickery in punning types and pointer. In fact, we even have a name
	for it.
*/

/* If you change these typecodes, you'll have to fix the arrayinfo table
   in gc.c and the {in,}direct_{load,store}_ops tables in
   compiler/tree2code.c */

#ifndef _TYPECODES_H_
#define _TYPECODES_H_

#define T_NORMAL_OBJECT	0
#define T_XXUNUSEDXX1   1	/* Used to be T_ARRAY */
#define T_CLASS		2
#define T_BOOLEAN	4
#define T_CHAR		5

#define T_FLOATING	4	/* add log2 size to get correct code:
					float has code 6,
					double has code 7 */
#define T_INTEGER	010
#define T_UINTEGER	014

#define	T_MAXNUMERIC	020

#define	T_XXUNUSEDXX2	020
#define	T_VOID		021
#define	T_FUNC		022
#define	T_UNKNOWN	023
#define	T_ERROR		024

/* for type construction */
#define T_TMASK	034
#define T_LMASK 003
#define T_LSIZE 2
#define T_MKTYPE( t, l )  ( ( (t)&T_TMASK ) | ( (l)&T_LMASK) )

/* for type deconstruction */
	/*
	 * Because we promise always to let ints and compile-only types be 
	 * distinguished by the "t" and "s" bits above, we can simplify
	 * some of our predicates by masking out the "c" bit when testing
	 * for integers. Thus the T_TS_MASK...
	 */
#define T_TS_MASK 034
#define T_ISINTEGER(t)  ( ((t)&030) == T_INTEGER  )
#define T_ISFLOATING(t) ( ((t)&036) == T_FLOAT )
#define T_ISNUMERIC(t)  ( (t) >= T_CHAR && (t) < T_MAXNUMERIC )
#define T_SIZEFIELD(t)	((t)&T_LMASK)
#define T_ELEMENT_SIZE(t) (1<<T_SIZEFIELD(t))	/* only for some!! */

#define T_IS_BIG_TYPE(t) ((t == T_DOUBLE) || (t == T_LONG))
#define T_TYPE_WORDS(t) (T_IS_BIG_TYPE(t) ? 2 : 1)

/* nick-names for the usual scalar types */
#define T_FLOAT  T_MKTYPE(T_FLOATING,2)
#define T_DOUBLE T_MKTYPE(T_FLOATING,3)
#define T_BYTE	 T_MKTYPE(T_INTEGER,0)
#define T_SHORT	 T_MKTYPE(T_INTEGER,1)
#define T_INT	 T_MKTYPE(T_INTEGER,2)
#define T_LONG	 T_MKTYPE(T_INTEGER,3)

#ifdef NO_LONGER_USED
/* We no longer support these types */
#define T_UBYTE	 T_MKTYPE(T_UINTEGER,0)
#define T_USHORT T_MKTYPE(T_UINTEGER,1)
#define T_UINT	 T_MKTYPE(T_UINTEGER,2)
#define T_ULONG	 T_MKTYPE(T_UINTEGER,3)

#endif

/* only a slight exaggeration */
#define N_TYPECODES	(1<<6)
#define	N_TYPEMASK	(N_TYPECODES-1)

#endif /* !_TYPECODES_H_ */
