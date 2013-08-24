/*
 * @(#)splashscreen.h	1.5 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


int     DoSplashLoadMemory(void* pdata, int size); /* requires preloading the file */
int     DoSplashLoadFile(const char* filename);
void    DoSplashInit(void);
void    DoSplashClose(void);
void    DoSplashSetFileJarName(const char* fileName, const char* jarName);


