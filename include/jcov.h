/*
 * @(#)jcov.h	1.3 98/07/01
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

#ifndef _JCOV_H_
#define _JCOV_H_

extern char* testname; 		/* Name of test */
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

extern void java_cov_dump();
extern int  jcov_write_data(char *);

#endif /* ! _JCOV_H_ */
