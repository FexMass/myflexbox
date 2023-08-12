package com.myflexbox;

import java.util.ResourceBundle;

public class MessageUtil {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    private MessageUtil() {
        // private constructor to prevent instantiation
    }

    public static String getMessage(String key) {
        return bundle.getString(key);
    }
}