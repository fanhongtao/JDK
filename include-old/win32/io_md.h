/*
 * @(#)io_md.h	1.26 98/09/15
 *
 * Copyright 1994-1998 by Sun Microsystems, Inc.,
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
 * Win32 system dependent low level io definitions
 */

#ifndef _JAVASOFT_WIN32_IO_MD_H_
#define _JAVASOFT_WIN32_IO_MD_H_

#include <stdio.h>
#include <io.h>                 /* For read(), lseek() etc. */
#include <direct.h>             /* For mkdir() */
#include <windows.h>
#include <winsock.h>
#include <sys/types.h>
#include <ctype.h>
#include <stdlib.h>

#include "jvm_md.h"

#define R_OK	4
#define W_OK	2
#define X_OK	1
#define F_OK	0

#define	MAXPATHLEN _MAX_PATH

#define S_ISFIFO(mode)	(((mode) & _S_IFIFO) == _S_IFIFO)
#define S_ISCHR(mode)	(((mode) & _S_IFCHR) == _S_IFCHR)
#define S_ISDIR(mode)	(((mode) & _S_IFDIR) == _S_IFDIR)
#define S_ISREG(mode)	(((mode) & _S_IFREG) == _S_IFREG)

#define LINE_SEPARATOR "\r\n"

#endif /* !_JAVASOFT_WIN32_IO_MD_H_ */
