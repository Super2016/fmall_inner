package cn.fmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * -->ResponseCode
 * 通用服务响应对象
 * 使用泛型便于封装不太能够数据类型
 * @param <T>
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//使用场景,当响应为失败时并不会返回data,仅返回status、msg,此时json为存在key的空节点
//该注解保证序列化json的时候,如果对象值是null,对应key同样消失
public class ServerResponse<T> implements Serializable{
    private int status;
    private String msg;
    private T data;

    @JsonIgnore
    //该注解表示不出现在序列化结果中
    //判断响应是否正确,采用枚举将响应码归类便于直观
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    private ServerResponse(int status){
        this.status = status;
    }

    private ServerResponse(int status,String msg){
        this.status = status;
    }

    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    //返回状态码
    public int getStatus(){
        return status;
    }

    //返回数据
    public T getData(){
        return data;
    }

    //成功响应(4)-->
    //仅返回状态码
    public static <T> ServerResponse<T> createIfSuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    //返回消息文本供前端提示使用
    public static <T> ServerResponse<T> createIfSuccess(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    //返回数据填充
    public static <T> ServerResponse<T> createIfSuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    //返回消息与数据
    public static <T> ServerResponse<T> createIfSuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    //失败响应(3)-->
    //返回一个公共(默认)的错误描述
    public static <T> ServerResponse<T> createIfError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDescription());
    }

    //返回其他特指错误提示描述
    public static <T> ServerResponse<T> createIfError(String errorMsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMsg);
    }

    //将code作为变量的方法,返回其他的错误状态码
    public static <T> ServerResponse<T> createIfError(int code,String errorMsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMsg);
    }
}
