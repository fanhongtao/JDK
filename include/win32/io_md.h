/*
 * @(#)io_md.h	1.18 96/11/23
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
