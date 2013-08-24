/*
 * This file defines heapdump function to heap dump
 * in binary format. User can call this function
 * based on events. For example, a timer thread can
 * keep checking heap threshold and depending on 
 * specific expected threshold value, it can call
 * heapdump to dump the keep. File name can contain
 * timestamp so that multiple heapdumps can be generated
 * for the same process.
 */

/**
 * Function to dump heap in binary format.
 *
 * @param file heap dump file name [optional]
 */
function heapdump(file) {
    // no file specified, show file open dialog
    if (file == undefined) {
        file = fileDialog();
        // check whether user cancelled the dialog
        if (file == null) return;
    }

    /* 
     * Get HotSpotDiagnostic MBean and wrap it as convenient
     * script wrapper using 'mbean' function. Instead of using
     * MBean proxies 'mbean' function creates a script wrapper 
     * that provides similar convenience but uses explicit 
     * invocation behind the scene. This implies that mbean 
     * wrapper would the same for dynamic MBeans as well.
     */
    var diagBean = mbean("com.sun.management:type=HotSpotDiagnostic");

    // dump the heap in the file
    diagBean.dumpHeap(file, true);
}
