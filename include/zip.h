/*
 * @(#)zip.h	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
