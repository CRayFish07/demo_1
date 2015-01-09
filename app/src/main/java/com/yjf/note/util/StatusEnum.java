package com.yjf.note.util;

/**
 * Created by Administrator on 2015/1/3 0003.
 */
public class StatusEnum {

    public static interface EnumGetInf{
        public int getKey();
    };

    public enum ErrorCode implements EnumGetInf{
        INNER_ERROR(20,"服务器错误"),
        APP_ERROR(10,"app错误");

        private int key;
        private String message;

        ErrorCode(int key,String message){
            this.key = key;
            this.message = message;
        }

        public int getKey(){
            return key;
        }

        public String message(){
            return message;
        }

    }
}
