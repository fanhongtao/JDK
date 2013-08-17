/*
 * @(#)byteorder_md.h	1.8 98/07/01
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
