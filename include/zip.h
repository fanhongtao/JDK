/*
 * @(#)zip.h	1.4 97/01/24
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
 * Prototypes for "zip" file reader support
 */

#ifndef _ZIP_H_
#define _ZIP_H_

#include <time.h>
#include "bool.h"

/*
 * Central directory entry
 */
typedef struct {
    char *fn;		/* file name */
    int len;		/* file size */
    int size;		/* file compressed size */
    int method;		/* Compression method */
    int mod;		/* file modification time */
    long off;		/* local file header offset */
} direl_t;

/*
 * Zip file
 */
typedef struct {
    char *fn;		/* zip file name */
    int fd;		/* zip file descriptor */
    direl_t *dir;	/* zip file directory */
    int nel;		/* number of directory entries */
    long cenoff;	/* Offset of central directory (CEN) */
    long endoff;	/* Offset of end-of-central-directory record */
} zip_t;

struct stat;

zip_t *zip_open(const char *fn);
void zip_close(zip_t *zip);
bool_t zip_stat(zip_t *zip, const char *fn, struct stat *sbuf);
bool_t zip_get(zip_t *zip, const char *fn, void *buf, int len);

#endif /* !_ZIP_H_ */ 
