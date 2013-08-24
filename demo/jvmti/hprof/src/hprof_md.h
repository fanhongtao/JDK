/*
 * @(#)hprof_md.h	1.18 05/12/06
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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

#ifndef HPROF_MD_H
#define HPROF_MD_H

void    md_init(void);
int     md_getpid(void);
void    md_sleep(unsigned seconds);
int     md_connect(char *hostname, unsigned short port);
int     md_recv(int f, char *buf, int len, int option);
int     md_shutdown(int filedes, int option);
int     md_open(const char *filename);
int     md_open_binary(const char *filename);
int     md_creat(const char *filename);
int     md_creat_binary(const char *filename);
jlong   md_seek(int filedes, jlong cur);
void    md_close(int filedes);
int 	md_send(int s, const char *msg, int len, int flags);
int 	md_write(int filedes, const void *buf, int nbyte);
int 	md_read(int filedes, void *buf, int nbyte);
jlong 	md_get_microsecs(void);
jlong 	md_get_timemillis(void);
jlong 	md_get_thread_cpu_timemillis(void);
void 	md_get_prelude_path(char *path, int path_len, char *filename);
int     md_snprintf(char *s, int n, const char *format, ...);
int     md_vsnprintf(char *s, int n, const char *format, va_list ap);
void    md_system_error(char *buf, int len);

unsigned md_htons(unsigned short s);
unsigned md_htonl(unsigned l);
unsigned md_ntohs(unsigned short s);
unsigned md_ntohl(unsigned l);

void   md_build_library_name(char *holder, int holderlen, char *pname, char *fname);
void * md_load_library(const char *name, char *err_buf, int err_buflen);
void   md_unload_library(void *handle);
void * md_find_library_entry(void *handle, const char *name);

#endif
