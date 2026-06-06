package com.mdt.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtils {

    public static void doNothing(Object... ignored) {

    }

    public static boolean returnTrue(Object... ignored) {
        return true;
    }

    public static boolean returnFalse(Object... ignored) {
        return false;
    }
}
