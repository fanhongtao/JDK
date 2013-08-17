/*
 * @(#)byteorder_md.h	1.7 96/11/23
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */

/*-
 * Win32 dependent machine byte ordering (actually intel ordering)
 */

#ifndef _WIN32_BYTEORDER_MD_H_
#define _WIN32_BYTEORDER_MD_H_

#ifdef	x86
#define ntohl(x)	((x << 24) | 				\
 			  ((x & 0x0000ff00) << 8) |		\
			  ((x & 0x00ff0000) >> 8) | 		\
			  (((unsigned long)(x & 0xff000000)) >> 24))
#define ntohs(x)	(((x & 0xff) << 8) | ((x >> 8) & (0xff)))
#define htonl(x)	ntohl(x)
#define htons(x)	ntohs(x)
#else	/* x86 */
#define ntohl(x)	(x)
#define ntohs(x)	(x)
#define htonl(x)	(x)
#define htons(x)	(x)
#endif	/* x86 */

#endif /* !_WIN32_BYTEORDER_MD_H_ */
