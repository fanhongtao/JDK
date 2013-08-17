/*
 * @(#)path.h	1.13 98/09/30
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
