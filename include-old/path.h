/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
