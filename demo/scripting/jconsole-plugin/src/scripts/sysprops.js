/*
 * This file defines 'sysprops' function to print Java System
 * properties.'sysprops' function which can be called once or periodically 
 * from a timer thread (calling it periodically would slow down the target
 * application). To call this once, just call 'sysprops()' in script
 * console prompt. To call jtop in a timer thread, you can use
 *
 *     var t = setTimeout(function () { sysprops(print); }, 5000); 
 *
 * The above call prints threads in sorted order for every 5 seconds.
 * The print output goes to OS console window from which jconsole was 
 * started. The timer can be cancelled later by clearTimeout() function
 * as shown below:
 * 
 *     clearTimeout(t);    
 */


/**
 * Returns System properties as a Map
 */
function getSystemProps() {
    var runtimeBean = newPlatformMXBeanProxy(
                "java.lang:type=Runtime",
                java.lang.management.RuntimeMXBean);
    return runtimeBean.systemProperties;
}

/**
 * print System properties
 *
 * @param printFunc function called to print [optional]
 */
function sysprops(printFunc) {
    // by default use 'echo' to print. Other choices could be
    // 'print' or custom function that writes in a text file
    if (printFunc == undefined) {
        printFunc = echo;
    }

    var map = getSystemProps();
    var keys = map.keySet().iterator();
    while (keys.hasNext()) {
        var key = keys.next();
        var value = map.get(key);
        printFunc(key + "=" + value);
    }
}
