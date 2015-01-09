package com.yjf.note.util;

import com.yjf.note.Exception.AppException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2015/1/3 0003.
 */
public class AESEncryptUtil {

    public static final  String AES_PUBLIC_KEY = "415erfdvsdhyt65498dff";
    private static final  String AES_ALGORITHM = "AES";
    private static final int AES_KEY_SIZE = 128;

    /**
     * AES加密方法
     *
     * @param encryptKey
     *            加密的Key
     * @param data
     *            被加密数据
     * @return
     * @throws AppException
     */
    public static String encryptByAES(String encryptKey, String data) {
        try {
            SecretKey secretKey = getKey(encryptKey);
            byte[] encodeFormat = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(encodeFormat, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM); // 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec); // 初始化
            byte[] result = cipher.doFinal(data.getBytes());
            //加密后的byte数组不能直接转换成string,需要将二进制数据转换成16进制
            return parseByte2HexStr(result);
        } catch (Exception e) {
            throw new AppException(StatusEnum.ErrorCode.APP_ERROR,"加密[" + data + "],出错" + e.getMessage());
        }
    }

    /**
     * AES解密方法
     *
     * @param decryptKey
     * @param data
     * @return
     * @throws AppException
     */
    public static String decryptByAES(String decryptKey, String data) {
        try {
            SecretKey secretKey = getKey(decryptKey);
            byte[] encodeFormat = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(encodeFormat, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);// 初始化
            byte[] decryptedByte = cipher.doFinal(parseHexStr2Byte(data));
            return new String(decryptedByte);
        } catch (Exception e) {
            throw new AppException(StatusEnum.ErrorCode.APP_ERROR,"解密[" + data + "],出错" + e.getMessage());
        }
    }

    private static SecretKey getKey(String key) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes());
        keyGenerator.init(AES_KEY_SIZE, secureRandom);
        return keyGenerator.generateKey();
    }

    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args){
        String password = "123";
        String a = AESEncryptUtil.encryptByAES(AESEncryptUtil.AES_PUBLIC_KEY,password);
        System.out.println(a);
    }

}
