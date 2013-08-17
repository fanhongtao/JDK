/*
 * @(#)path_md.h	1.10 98/07/01
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

/*-
 * Win32 dependent search path definitions and API
 */

#ifndef _WIN32_PATH_MD_H_
#define _WIN32_PATH_MD_H_

#define	DIR_SEPARATOR		'/'
#define	LOCAL_DIR_SEPARATOR	'\\'

#define	CLS_CONST_STRING	"<init>([C)Ljava/lang/String;"

#define	CLS_RESLV_INIT_CLASS	"java/lang/Class"
#define	CLS_RESLV_INIT_OBJECT	"java/lang/Object"
#define	CLS_RESLV_INIT_REF	"sun/misc/Ref"

#define INTRP_BRKPT_STRING	"sun/tools/debug/BreakpointHandler"

#define LANG_MATH_INTEGER_VALOF	"<init>(I)Ljava/lang/Integer;"
#define LANG_MATH_LONG_VALOF	"<init>(J)Ljava/lang/Long;"
#define LANG_MATH_FLOAT_VALOF	"<init>(F)Ljava/lang/Float;"
#define LANG_MATH_DOUBLE_VALOF	"<init>(D)Ljava/lang/Double;"

#define LANG_OBJECT_CLONE	"copy(Ljava/lang/Object;)V"

#define LANG_STRING_MAKE_STR	"<init>([C)Ljava/lang/String;"

char *sysNativePath(char *path);

#endif /* !_WIN32_PATH_MD_H_ */
