package cn.fmall.common;

/**
 * 通用响应码枚举归类
 */
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String description;

    ResponseCode(int code,String description){
        this.code = code;
        this.description = description;
    }

    //返回code码,供外部使用
    public int getCode(){
        return code;
    }

    //返回描述,供外部使用
    public String getDescription() {
        return description;
    }
}


