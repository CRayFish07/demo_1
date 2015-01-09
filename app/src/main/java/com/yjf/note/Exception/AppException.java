package com.yjf.note.Exception;

/**
 * Created by Administrator on 2015/1/3 0003.
 */
public class AppException extends  RuntimeException {

    private Enum errorEnum;

    public AppException(String message){
        super(message);
    }

    public AppException(Enum errorEnum,String message){
        super(message);
        this.errorEnum = errorEnum;
    }
    public Enum getErrorEnum(){
        return errorEnum;
    }

}
