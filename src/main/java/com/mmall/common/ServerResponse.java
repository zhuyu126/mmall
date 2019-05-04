package com.mmall.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import org.codehaus.jackson.annotate.JsonIgnore;
//import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 *  在实体类前，增加@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL) 注解,指的是属性为空不参加序列化，如果是null的对象，key也会消失
 *  JSON原来经过JACKSON转换以后为{"name"："name","sex":null},加入注解后，结果为{"name"："name"},sex节点被去掉了
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    //私有化构造器，在外部不可以new，但是方法的public
    private ServerResponse(int status){
        this.status=status;
    }
    private ServerResponse(int status,T data){
        this.status=status;
        this.data=data;
    }
    private ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }
    private ServerResponse(int status,String msg,T data){
        this.status=status;
        this.data=data;
        this.msg=msg;
    }

    //判断服务器响应是否成功
    @JsonIgnore//在json序列化过程中，该字段不会显示在序列化结果中
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();//如果为0，True成功，否则是False，失败
    }
    //将变量状态status，状态信息msg，数据data暴露出去
    public int getStatus(){
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    //如果创建成功，返回一个状态
    public static<T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }
    //如果成功，返回一个状态和msg
    public  static<T> ServerResponse<T> createBySuccess(String msg){
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(),msg);
    }
    //成功，返回状态和数据
    public static <T> ServerResponse<T> createBySuccess(T data){
        return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }
    //如果成功，返回一个状态、msg和数据
    public  static<T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    //失败,返回状态和状态描述description
    public  static<T> ServerResponse<T> createByError(){
        return new ServerResponse<>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    //失败,返回一个文本供前端提示使用
    public static<T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    //失败,返回status和提示
    public static<T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }

}
