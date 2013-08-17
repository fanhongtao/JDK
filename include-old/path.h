/*
 * @(#)path.h	1.2 00/01/12
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

#ifndef _JAVASOFT_PATH_H_
#define _JAVASOFT_PATH_H_

#include "path_md.h"
#include "jvm.h"

typedef void *jzfile;
typedef void *jzentry;

/*
 * Class path element, which is either a directory or zip file.
 */
typedef struct {
    enum { CPE_DIR, CPE_ZIP } type;
    jzfile *zip;
    char *path;
} cpe_t;

cpe_t **GetClassPath(void);

#endif /* !_JAVASOFT_PATH_H_ */
