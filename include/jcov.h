/*
 * @(#)jcov.h	1.4 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
