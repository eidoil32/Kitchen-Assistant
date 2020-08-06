package com.kitchas.kitchenassistant.utils;
import org.apache.commons.codec.digest.DigestUtils;

public class Tools {
    public static String encrypt(String text) {
        return DigestUtils.sha256Hex(text);
    }
}
