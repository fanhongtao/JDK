/*
 * @(#)jcov.h	1.9 98/09/15
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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
 * Definitions for the Jcov	10/27/96
 * @author Leonid Arbouzov
 * leo@nbsp.nsk.su
 */

#ifndef _JAVASOFT_JCOV_H_
#define _JAVASOFT_JCOV_H_

#define JCOV_MODE_DEFAULT	 1	/* default Jcov mode - with using Coverage table */
#define JCOV_MODE_METHODS	 2	/* gathering Jcov datas without Coverage table */

#define JCOVTAB_ELEMENT_LENGTH  12	/* in class file : 2 + 2 + 4 + 4 */

extern unsigned int jcov_flags;	/* Flags of code coverage     */
extern char* cov_file;		/* Name of code coverage file */

#define CT_METHOD         1
#define CT_FIKT_METHOD	  2
#define CT_BLOCK          3
#define CT_FIKT_RET	  4
#define CT_CASE	 	  5
#define CT_SWITH_WO_DEF	  6
#define CT_BRANCH_TRUE	  7
#define CT_BRANCH_FALSE	  8

extern void coverage_if(int flg, JavaFrame *frame, unsigned char *pc);
extern void coverage_switch(JavaFrame *frame, unsigned char *pc, int key, int low, int high);
extern void coverage_lookupswitch(JavaFrame *frame, unsigned char *pc, int npairs, int nodef);
extern void coverage_native_method(struct methodblock *mb);

extern int  jcov_write_data(void);

#endif /* !_JAVASOFT_JCOV_H_ */
