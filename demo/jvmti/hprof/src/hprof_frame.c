/*
 * @(#)hprof_frame.c	1.16 04/07/27
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

/* This file contains support for handling frames, or (method,location) pairs. */

#include "hprof.h"

/*
 *  Frames map 1-to-1 to (methodID,location) pairs.
 *  When no line number is known, -1 should be used.
 *
 *  Frames are mostly used in traces (see hprof_trace.c) and will be marked
 *    with their status flag as they are written out to the hprof output file.
 *
 */

typedef struct FrameKey {
    jmethodID   method;
    jlocation   location;
} FrameKey;

typedef struct FrameInfo {
    jint        status;
    jint        lineno;
} FrameInfo;

static FrameKey*
get_pkey(FrameIndex index)
{
    void *key_ptr;
    int   key_len;

    table_get_key(gdata->frame_table, index, &key_ptr, &key_len);
    HPROF_ASSERT(key_len==sizeof(FrameKey));
    HPROF_ASSERT(key_ptr!=NULL);
    return (FrameKey*)key_ptr;
}

static FrameInfo *
get_info(FrameIndex index)
{
    FrameInfo *info;

    info = (FrameInfo*)table_get_info(gdata->frame_table, index);
    return info;
}

static void
list_item(TableIndex i, void *key_ptr, int key_len, void *info_ptr, void *arg)
{
    FrameKey   key;
    FrameInfo *info;
    
    HPROF_ASSERT(key_ptr!=NULL);
    HPROF_ASSERT(key_len==sizeof(FrameKey));
    HPROF_ASSERT(info_ptr!=NULL);
    
    key = *((FrameKey*)key_ptr);
    info = (FrameInfo*)info_ptr;
    debug_message( 
	"Frame 0x%08x: method=%p, location=%d, lineno=%d, status=0x%08x \n",
                i, (void*)key.method, (jint)key.location, 
		info->lineno, info->status);
}

void
frame_init(void)
{
    gdata->frame_table = table_initialize("Frame",
                            1024, 1024, 1023, (int)sizeof(FrameInfo));
}

FrameIndex
frame_find_or_create(jmethodID method, jlocation location)
{
    static FrameKey empty_key;
    FrameKey key;
    static FrameInfo empty_info;
    FrameInfo info;
    
    key          = empty_key;
    key.method   = method;
    key.location = location;
    info         = empty_info;
    if ( location < 0 ) {
	info.lineno = -1;
    }
    return table_find_or_create_entry(gdata->frame_table, 
			&key, (int)sizeof(key), NULL, (void*)&info);
}

void
frame_list(void)
{
    debug_message( 
        "--------------------- Frame Table ------------------------\n");
    table_walk_items(gdata->frame_table, &list_item, NULL);
    debug_message(
        "----------------------------------------------------------\n");
}

void
frame_cleanup(void)
{
    table_cleanup(gdata->frame_table, NULL, NULL);
    gdata->frame_table = NULL;
}

void
frame_set_status(FrameIndex index, jint status)
{
    FrameInfo *info;

    info = get_info(index);
    info->status = status;
}

void
frame_get_location(FrameIndex index, jmethodID *pmethod, 
			jlocation *plocation, jint *plineno)
{
    FrameKey  *pkey;
    FrameInfo *info;

    pkey       = get_pkey(index);
    *pmethod   = pkey->method;
    *plocation = pkey->location;
    info       = get_info(index);
    if ( info->lineno == 0 ) {
	if ( gdata->lineno_in_traces ) {
	    if ( pkey->location >= 0 && !isMethodNative(pkey->method) ) {
		info->lineno = getLineNumber(pkey->method, pkey->location);
	    } else { 
		info->lineno = -1;
	    }
	}
    }
    *plineno   = info->lineno;
}

jint
frame_get_status(FrameIndex index)
{
    FrameInfo *info;

    info = get_info(index);
    return info->status;
}

