/*
 * @(#)dirent.h	1.4 98/07/01
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

/*
 * Posix-compatible directory access routines
 */

#ifndef _WIN32_DIRENT_H_
#define _WIN32_DIRENT_H_

struct dirent {
    char d_name[MAX_PATH];
};

typedef struct {
    struct dirent dirent;
    char *path;
    HANDLE handle;
    WIN32_FIND_DATA find_data;
} DIR;

DIR *opendir(const char *dirname);
struct dirent *readdir(DIR *dirp);
int closedir(DIR *dirp);
void rewinddir(DIR *dirp);

#endif
