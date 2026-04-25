package com.snjdigitalsolutions.lablensfx.utility;

public class DebugUtility {

    public static String getCallerInfo() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // 0 = getStackTrace, 1 = getCallerInfo, 2 = the method that called this helper, 3 = the real caller
        if (stack.length >= 4) {
            StackTraceElement caller = stack[3];
            return caller.getClassName() + "." + caller.getMethodName()
                    + "() line " + caller.getLineNumber();
        }
        return "unknown";
    }

}
