/*
 * @(#)signature.h	1.18 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

/*
 * The keyletters used in type signatures
 */

#ifndef _JAVASOFT_SIGNATURE_H_
#define _JAVASOFT_SIGNATURE_H_

#define SIGNATURE_ANY		'A'
#define SIGNATURE_ARRAY		'['
#define SIGNATURE_BYTE		'B'
#define SIGNATURE_CHAR		'C'
#define SIGNATURE_CLASS		'L'
#define SIGNATURE_ENDCLASS	';'
#define SIGNATURE_ENUM		'E'
#define SIGNATURE_FLOAT		'F'
#define SIGNATURE_DOUBLE        'D'
#define SIGNATURE_FUNC		'('
#define SIGNATURE_ENDFUNC	')'
#define SIGNATURE_INT		'I'
#define SIGNATURE_LONG		'J'
#define SIGNATURE_SHORT		'S'
#define SIGNATURE_VOID		'V'
#define SIGNATURE_BOOLEAN	'Z'

#define SIGNATURE_ANY_STRING		"A"
#define SIGNATURE_ARRAY_STRING		"["
#define SIGNATURE_BYTE_STRING		"B"
#define SIGNATURE_CHAR_STRING		"C"
#define SIGNATURE_CLASS_STRING		"L"
#define SIGNATURE_ENDCLASS_STRING	";"
#define SIGNATURE_ENUM_STRING		"E"
#define SIGNATURE_FLOAT_STRING		"F"
#define SIGNATURE_DOUBLE_STRING       	"D"
#define SIGNATURE_FUNC_STRING		"("
#define SIGNATURE_ENDFUNC_STRING	")"
#define SIGNATURE_INT_STRING		"I"
#define SIGNATURE_LONG_STRING		"J"
#define SIGNATURE_SHORT_STRING		"S"
#define SIGNATURE_VOID_STRING		"V"
#define SIGNATURE_BOOLEAN_STRING	"Z"

/* constants for terse representation of signatures:
 * "(Ljava/net/Socket;ZI[Ljava/lang/String;)V" 
 *   ---> { OBJ, BOOLEAN, INT, OBJ, ENDFUNC, VOID, END }
 *
 * Keep the values in this enumeration in sync with
 * the assembly sysInvokeNative() and the custom
 * invokers.
 */

enum {
    TERSE_SIG_END, /* this one should be '\0' */
    TERSE_SIG_OBJECT, TERSE_SIG_LONG, TERSE_SIG_DOUBLE, 
    TERSE_SIG_BOOLEAN, TERSE_SIG_BYTE, TERSE_SIG_SHORT,
    TERSE_SIG_CHAR, TERSE_SIG_INT, TERSE_SIG_FLOAT,
    TERSE_SIG_VOID, TERSE_SIG_ENDFUNC /* keep this one at end */
};

#endif /* !_JAVASOFT_SIGNATURE_H_ */
