/*
 * @(#)decode.h	1.13 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

void DecodeFile(char *);

/*
 * Options for code printing
 */

extern int	PrintCode;
extern int	PrintAsC;
extern int	PrintPrivate;
extern int	PrintLocalTable;
extern int	PrintLineTable;
extern int	PrintConstantPool;
extern int      PrintPublicOnly; /* print public/protected only */
extern int	PrintInternalSigs;
