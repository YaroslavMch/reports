package com.easypark.reports.util;

public class NameCreator {
    public static String createNameFromKey(String key) {
        try {
            if ("5fbe3451cbead50069310a6c".equals(key)) {
                return "V.Horodetskyi";
            }
            if ("60193493a41ea5006a945ad6".equals(key)) {
                return "R.Shvets";
            }
            String surname = key.split("\\.")[1];
            return key.substring(0, 1).toUpperCase() + "." + surname.substring(0, 1).toUpperCase() + surname.substring(1);
        } catch (Exception e) {
            return key;
        }

    }
}
