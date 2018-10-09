package com.easypark.reports.util;

public class NameCreator {
    public static String createNameFromKey(String key) {
        String surname = key.split("\\.")[1];
        return key.substring(0, 1).toUpperCase() + "." + surname.substring(0, 1).toUpperCase() + surname.substring(1);
    }
}
