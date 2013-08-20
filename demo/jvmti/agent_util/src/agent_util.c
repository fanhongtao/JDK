/*
 * @(#)agent_util.c	1.11 04/07/27
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

#include <agent_util.h>

/* ------------------------------------------------------------------- */
/* Generic C utility functions */

/* Send message to stdout or whatever the data output location is */
void
stdout_message(const char * format, ...)
{
    va_list ap;

    va_start(ap, format);
    (void)vfprintf(stdout, format, ap);
    va_end(ap);
}

/* Send message to stderr or whatever the error output location is and exit  */
void
fatal_error(const char * format, ...)
{
    va_list ap;

    va_start(ap, format);
    (void)vfprintf(stderr, format, ap);
    (void)fflush(stderr);
    va_end(ap);
    exit(3);
}

/* Get a token from a string (strtok is not MT-safe)
 *    str	String to scan
 *    seps      Separation characters
 *    buf       Place to put results
 *    max       Size of buf
 *  Returns NULL if no token available or can't do the scan.
 */
char *
get_token(char *str, char *seps, char *buf, int max)
{
    int len;
    
    buf[0] = 0;
    if ( str==NULL || str[0]==0 ) {
	return NULL;
    }
    str += strspn(str, seps);
    if ( str[0]==0 ) {
	return NULL;
    }
    len = (int)strcspn(str, seps);
    if ( len >= max ) {
	return NULL;
    }
    (void)strncpy(buf, str, len);
    buf[len] = 0;
    return str+len;
}

/* Determines if a class/method is specified by a list item 
 *   item	String that represents a pattern to match
 *          	  If it starts with a '*', then any class is allowed
 *                If it ends with a '*', then any method is allowed
 *   cname	Class name, e.g. "java.lang.Object"
 *   mname      Method name, e.g. "<init>"
 *  Returns 1(true) or 0(false).
 */
static int
covered_by_list_item(char *item, char *cname, char *mname)
{
    int      len;
    
    len = (int)strlen(item);
    if ( item[0]=='*' ) {
	if ( strncmp(mname, item+1, len-1)==0 ) {
	    return 1;
	}
    } else if ( item[len-1]=='*' ) {
	if ( strncmp(cname, item, len-1)==0 ) {
	    return 1;
	}
    } else {
	int cname_len;
	
	cname_len = (int)strlen(cname);
	if ( strncmp(cname, item, (len>cname_len?cname_len:len))==0 ) {
	    if ( cname_len >= len ) {
		/* No method name supplied in item, we must have matched */
		return 1;
	    } else {
		int mname_len;
		
		mname_len = (int)strlen(mname);
		item += cname_len+1;
		len -= cname_len+1;
		if ( strncmp(mname, item, (len>mname_len?mname_len:len))==0 ) {
		    return 1;
		}
	    }
	}
    }
    return 0;
}

/* Determines if a class/method is specified by this list
 *   list	String of comma separated pattern items
 *   cname	Class name, e.g. "java.lang.Object"
 *   mname      Method name, e.g. "<init>"
 *  Returns 1(true) or 0(false).
 */
static int
covered_by_list(char *list, char *cname, char *mname)
{
    char  token[1024];
    char *next;
    
    if ( list[0] == 0 ) {
        return 0;
    }
	
    next = get_token(list, ",", token, sizeof(token));
    while ( next != NULL ) {
	if ( covered_by_list_item(token, cname, mname) ) {
	    return 1;
	}
        next = get_token(next, ",", token, sizeof(token));
    }
    return 0;
}

/* Determines which class and methods we are interested in 
 *   cname		Class name, e.g. "java.lang.Object"
 *   mname      	Method name, e.g. "<init>"
 *   include_list	Empty or an explicit list for inclusion
 *   exclude_list	Empty or an explicit list for exclusion
 *  Returns 1(true) or 0(false).
 */
int
interested(char *cname, char *mname, char *include_list, char *exclude_list)
{
    if ( exclude_list!=NULL && exclude_list[0]!=0 && 
	    covered_by_list(exclude_list, cname, mname) ) {
        return 0;
    }
    if ( include_list!=NULL && include_list[0]!=0 && 
	    !covered_by_list(include_list, cname, mname) ) {
        return 0;
    }
    return 1;
}

/* ------------------------------------------------------------------- */
