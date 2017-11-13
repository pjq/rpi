package com.aliyun.iot.util;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by Gary on 14-5-10.
 */

public class SecureUtils {
    

    private static byte toByte(char c) {

        return (byte) "0123456789abcdef".indexOf(c);
    }

    public static byte[] hexStringToByte(String hex) {

        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    public static final String bytesToHexString(byte[] bArray) {

        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp);
        }
        return sb.toString();
    }

   

    private static byte[] doAes(byte[] byteData, byte[] strKey, int opmode,
                                String algorithm) throws Exception {

        Cipher cipher = null;
        SecretKeySpec aesKey = new SecretKeySpec(strKey, "AES");
        cipher = Cipher.getInstance(algorithm);
        IvParameterSpec iv = null;
        if (algorithm.toLowerCase().contains("cbc")) {
            iv = new IvParameterSpec(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                                  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
        }
        if (algorithm.toLowerCase().contains("cbc/nopadding")) {
            int len = byteData.length;
            /* 计算补0后的长度 */
            while (len % 16 != 0) {
                len++;
            }
            byte[] sraw = new byte[len];
            /* 在最后补0 */
            for (int i = 0; i < len; ++i) {
                if (i < byteData.length) {
                    sraw[i] = byteData[i];
                } else {
                    sraw[i] = 0;
                }
            }
            byteData = sraw;
        }
        cipher.init(opmode, aesKey, iv);
        return cipher.doFinal(byteData);
    }

    //AES密文解密
    public static byte[] aesDecrypt(byte[] byteMi, byte[] strKey, String algorithm) {
        try {
            return doAes(byteMi, strKey, Cipher.DECRYPT_MODE, algorithm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //AES密文加密
    public static byte[] aesEncrypt(byte[] byteMi, byte[] strKey, String algorithm) {

        try {
            return doAes(byteMi, strKey, Cipher.ENCRYPT_MODE, algorithm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    
    /** 
     * HMAC加密 
     *  
     * @param data 
     * @param key 
     * @return 
     * @throws Exception 
     */
    public static String encryptHMAC(String signMethod,byte[] content, byte[] key){
        try {
            SecretKey secretKey = new SecretKeySpec(key, signMethod);
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            byte[] data = mac.doFinal(content);
            return bytesToHexString(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
