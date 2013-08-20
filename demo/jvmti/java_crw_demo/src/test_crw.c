/*
 * @(#)test_crw.c	1.8 04/07/27
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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include "java_crw_demo.h"

static int error_code = 0;

#define ERROR(message) error(message, __FILE__, __LINE__)

static void
error(const char *message, const char *file, int line)
{
    error_code = 1;
    (void)fprintf(stderr, "ERROR: %s\n", message);
    exit(error_code);
}

static void
file_error(char *filename, char *message)
{
    error_code = 1;
    (void)fprintf(stderr, "ERROR: \"%s\": %s\n", filename, message);
    exit(error_code);
}

static void *
allocate(int size)
{
    return calloc(1, size);
}

static void
deallocate(void *ptr)
{
    free(ptr);
}

static void
mnums(unsigned cnum, const char **names, const char **descrs, int count)
{
    int i;
    
    (void)printf("Methods in class number 0x%08x:\n", cnum);
    for ( i = 0; i < count ; i++ ) {
	int j;

	(void)printf("\t0x%08x: name=%s, signature=%s\n", i, names[i], descrs[i]);
    }

}

int
main(int argc, char **argv)
{
    int i;
    unsigned class_number = 0x0FEED000;
    int obj_watch;
    int call_sites;
    int ret_sites;

    obj_watch = 0;
    call_sites = 0;
    ret_sites = 0;

    if ( argc < 3 ) {
        char buf[256];
        (void)snprintf(buf, sizeof(buf), "Usage: %s input_file output_file", argv[0]);
        ERROR(buf);
    }
    
    for(i=1; i<argc; i++) {
        FILE *fin;
        FILE *fout;
        const unsigned char *file_image;
        int file_len;
        unsigned char *new_file_image;
        long new_file_len;
        
	if ( strcmp(argv[i],"-n")==0 ) {
	    obj_watch = 1;
            continue;
        } else if ( strcmp(argv[i], "-c")==0 ) {
	    call_sites = 1;
            continue;
        } else if ( strcmp(argv[i], "-r")==0 ) {
	    ret_sites = 1;
            continue;
        }

        fin = fopen(argv[i], "r");
        if ( fin == NULL ) {
            file_error(argv[i], "Cannot open file");
        }
        (void)fseek(fin, 0, SEEK_END);
        file_len = ftell(fin);
        if ( file_len<=0 ) {
            file_error(argv[i], "File has 0 size");
        }
        (void)fseek(fin, 0, SEEK_SET);
        file_image = (const unsigned char *)malloc((size_t)file_len);
        assert(file_image!=NULL);
        if ( fread((void*)file_image, 1, (size_t)file_len, fin)!=(size_t)file_len) {
            file_error(argv[i], "File read failed");
        }
        
        java_crw_demo(class_number++, 
	    NULL, 
	    file_image, 
	    file_len, 
	    0,
            "sun/tools/hprof/Tracker",
            "Lsun/tools/hprof/Tracker;",
            call_sites?"CallSite":NULL,
            call_sites?"(II)V":NULL,
            ret_sites?"ReturnSite":NULL,
            ret_sites?"(II)V":NULL,
            obj_watch?"ObjectInit":NULL,
            obj_watch?"(Ljava/lang/Object;)V":NULL,
            obj_watch?"NewArray":NULL,
            obj_watch?"(Ljava/lang/Object;)V":NULL,
	    &new_file_image,
	    &new_file_len,
            &error, 
	    &mnums);
        
        fout = fopen(argv[i+1], "w");
        if ( fout == NULL ) {
            file_error(argv[i+1], "Cannot create file");
        }
        if ( new_file_len > 0 ) {
            if ( fwrite(new_file_image, 1, (size_t)new_file_len, fout)!=(size_t)new_file_len ) {
                file_error(argv[i+1], "File write failed");
            }
            (void)printf("Processed file %s to %s\n", argv[i], argv[i+1]);
        } else {
            if ( fwrite(file_image, 1, file_len, fout)!=(size_t)file_len ) {
                file_error(argv[i+1], "File write failed");
            }
            (void)printf("Duplicated file %s to %s (no injections)\n", argv[i], argv[i+1]);
        }

        (void)fclose(fout);

        free((void*)file_image);
	if ( new_file_image != NULL ) {
	    free(new_file_image);
	}

        i++;
    }
    return error_code;
}

