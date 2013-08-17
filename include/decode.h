/*
 * @(#)decode.h	1.12 98/07/01
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
