/*
* @(#)browse.js	1.1 06/08/06
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
* OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
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

/*
 * This function uses new Swing Desktop API in JDK 6.
 * To use this with scriptpad, open this in scriptpad
 * and use "Tools->Run Script" menu.
 */
function browse() {
    with (guiPkgs) {
        var desktop = null;
        // Before more Desktop API is used, first check 
        // whether the API is supported by this particular 
        // virtual machine (VM) on this particular host.
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        } else {
            alert("no desktop support");
            return;
        }

        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            var url = prompt("Address:");
            if (url != null) {
                desktop.browse(new java.net.URI(url));
            }
        } else {
            alert("no browser support");
        }
    }
}

if (this.application != undefined) {
    // add "Browse" menu item under "Tools" menu
    this.application.addTool("Browse", browse);
}

