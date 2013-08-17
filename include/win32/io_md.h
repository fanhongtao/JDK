/*
 * @(#)io_md.h	1.20 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Win32 system dependent low level io definitions
 */

#ifndef _WIN32_IO_MD_H_
#define _WIN32_IO_MD_H_

#include <stdio.h>
#include <windows.h>
#include <sys/types.h>
#include <ctype.h>
#include <stdlib.h>
#include "dirent.h"

#define R_OK	4
#define W_OK	2
#define X_OK	1
#define F_OK	0

#define	MAXPATHLEN _MAX_PATH

#define S_ISFIFO(mode)	(((mode) & _S_IFIFO) == _S_IFIFO)
#define S_ISCHR(mode)	(((mode) & _S_IFCHR) == _S_IFCHR)
#define S_ISDIR(mode)	(((mode) & _S_IFDIR) == _S_IFDIR)
#define S_ISREG(mode)	(((mode) & _S_IFREG) == _S_IFREG)

int sysOpen(const char *, int, int);
int sysAccess(const char *, int);
int sysMkdir(const char *, int);
int sysUnlink(const char *);
int sysAvailable(int fd, long *bytes);

#endif /* !_WIN32_IO_MD_H_ */
