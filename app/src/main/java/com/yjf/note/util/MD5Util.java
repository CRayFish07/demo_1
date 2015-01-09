package com.yjf.note.util;

import com.yjf.note.Exception.AppException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2015/1/3 0003.
 */
public class MD5Util {

    public static String getMd5(String content){
      try{
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(content.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for(int i=0;i<bits.length;i++){
            int a = bits[i];
            if(a<0) a+=256;
            if(a<16){
                buf.append("0");
            }
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
      }catch(NoSuchAlgorithmException e){
          throw new AppException(StatusEnum.ErrorCode.APP_ERROR,"获取md5值失败");
      }
    }
}
