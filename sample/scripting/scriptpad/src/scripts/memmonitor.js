/*
* @(#)memmonitor.js	1.1 06/08/06
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

// this checker function runs asynchronously
function memoryChecker(memoryBean, threshold, interval) {
    while (true) {
        var memUsage = memoryBean.HeapMemoryUsage;
        var usage = memUsage.get("used") / (1024 * 1024);
        println(usage);
        if (usage > threshold) {
            alert("Hey! heap usage threshold exceeded!");
            // after first alert just return.
            return;
        }
        java.lang.Thread.currentThread().sleep(interval);
    }
}


// add "Tools->Memory Monitor" menu item
if (this.application != undefined) {
    this.application.addTool("Memory Monitor", 
        function () {
            // show threshold box with default of 50 MB
            var threshold = prompt("Threshold (mb)", 50);
            // show interval box with default of 1000 millisec.
            var interval = prompt("Sample Interval (ms):", 1000);
            var memoryBean = mbean("java.lang:type=Memory");

            // ".future" makes the function to be called 
            // asynchronously in a separate thread.
            memoryChecker.future(memoryBean, threshold, interval);
        });
}

