/*
 * This file defines 'jstack' function to print stack traces of
 * threads.'jstack' function which can be called once or periodically 
 * from a timer thread (calling it periodically would slow down the target
 * application). To call this once, just call 'jstack()' in script
 * console prompt. To call jtop in a timer thread, you can use
 *
 *     var t = setTimeout(function () { jstack(print); }, 5000); 
 *
 * The above call prints threads in sorted order for every 5 seconds.
 * The print output goes to OS console window from which jconsole was 
 * started. The timer can be cancelled later by clearTimeout() function
 * as shown below:
 * 
 *     clearTimeout(t);    
 */


/**
 * print given ThreadInfo using given printFunc
 */
function printThreadInfo(ti, printFunc) {
    printFunc(ti.threadId + " - " + ti.threadName + " - " + ti.threadState);
    var stackTrace = ti.stackTrace;
    for (var i in stackTrace) {
        printFunc("\t" + stackTrace[i]);
    }
}

/**
 * print stack traces of all threads. 
 *
 * @param printFunc function called to print [optional]
 * @param maxFrames maximum number of frames to print [optional]
 */
function jstack(printFunc, maxFrames) {
    // by default use 'echo' to print. Other choices could be
    // 'print' or custom function that writes in a text file
    if (printFunc == undefined) {
        printFunc = echo;
    }

    // by default print 25 frames
    if (maxFrames == undefined) {
        maxFrames = 25;
    }

    var tmbean = newPlatformMXBeanProxy(
        "java.lang:type=Threading",
        java.lang.management.ThreadMXBean);

    var tids = tmbean.allThreadIds;
    var tinfos = tmbean["getThreadInfo(long[],int)"](tids, maxFrames);

    for (var i in tinfos) {
        printThreadInfo(tinfos[i], printFunc);
    }
}
