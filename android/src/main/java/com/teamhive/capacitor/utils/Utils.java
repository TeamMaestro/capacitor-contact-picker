package com.teamhive.capacitor.utils;

import com.getcapacitor.JSObject;

import java.util.Map;

public class Utils {

    public static String[] getMapKeysAsArray(Map<String, ?> map) {
        return map.keySet().toArray(new String[]{});
    }

    public static JSObject wrapIntoResult(JSObject contact) {
        JSObject result = new JSObject();
        result.put("value", contact);
        return result;
    }

}
