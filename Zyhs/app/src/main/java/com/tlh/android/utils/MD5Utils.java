package com.tlh.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * FIXME
 *
 * @author feicien (ithcheng@gmail.com)
 * @since 2015-11-12 19:18
 */
public class MD5Utils {

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
