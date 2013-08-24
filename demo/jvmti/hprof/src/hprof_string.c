/*
 * @(#)hprof_string.c	1.14 05/11/17
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

/* Table of byte arrays (e.g. char* string + NULL byte) */

/*
 * Strings are unique by their own contents, since the string itself
 *   is the Key, and the hprof_table.c guarantees that keys don't move,
 *   this works out perfect. Any key in this table can be used as
 *   an char*.
 *
 * This does mean that this table has dynamically sized keys.
 *
 * Care needs to be taken to make sure the NULL byte is included, not for
 *   the sake of hprof_table.c, but so that the key can be used as a char*.
 *
 */

#include "hprof.h"

void
string_init(void)
{
    HPROF_ASSERT(gdata->string_table==NULL);
    gdata->string_table = table_initialize("Strings", 4096, 4096, 1024, 0);
}

StringIndex
string_find_or_create(const char *str)
{
    return table_find_or_create_entry(gdata->string_table, 
		(void*)str, (int)strlen(str)+1, NULL, NULL);
}

static void
list_item(TableIndex index, void *str, int len, void *info_ptr, void *arg)
{
    debug_message( "0x%08x: String \"%s\"\n", index, (const char *)str);
}

void
string_list(void)
{
    debug_message( 
        "-------------------- String Table ------------------------\n");
    table_walk_items(gdata->string_table, &list_item, NULL);
    debug_message(
        "----------------------------------------------------------\n");
}

void
string_cleanup(void)
{
    table_cleanup(gdata->string_table, NULL, NULL);
    gdata->string_table = NULL;
}

char *
string_get(StringIndex index)
{
    void *key;
    int   key_len;
    
    table_get_key(gdata->string_table, index, &key, &key_len);
    HPROF_ASSERT(key_len>0);
    return (char*)key;
}

int
string_get_len(StringIndex index)
{
    void *key;
    int   key_len;

    table_get_key(gdata->string_table, index, &key, &key_len);
    HPROF_ASSERT(key_len>0);
    return key_len-1;
}

