package com.itdr.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/*
    服务端返回到前端的高复用的对象
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL) // 只包含非空值
public class ServerResponse<T> {
    private int status;//返回到前端的状态码
    private T date;//返回到前端的数据
    private String msg;//当status != 0 时，封装了一个错误信息

    private ServerResponse() {

    }

    public ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T date) {
        this.status = status;
        this.date = date;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T date, String msg) {
        this.status = status;
        this.date = date;
        this.msg = msg;
    }

    /*
        调用接口成功的回调
     */

    public static ServerResponse serverResponseBySuccess (){

        return new ServerResponse(ResconseCode.SUCCESS);

    }

    public static <T>ServerResponse serverResponseBySuccess (T date){

        return new ServerResponse(ResconseCode.SUCCESS,date);

    }

    public static <T>ServerResponse serverResponseBySuccess (T date,String msg){

        return new ServerResponse(ResconseCode.SUCCESS,date,msg);

    }

    /*
        调用接口失败的回调
     */

    public static ServerResponse serverResponseByError(){
        return new ServerResponse(ResconseCode.ERROR);
    }

    public static ServerResponse serverResponseByError(String msg){
        return new ServerResponse(ResconseCode.ERROR,msg);
    }

    public static ServerResponse serverResponseByError(int status){
        return new ServerResponse(status);
    }
    public static ServerResponse serverResponseByError(int status,String msg){
        return new ServerResponse(status,msg);
    }

    /*
        判断接口是否正确返回
        status == 0
     */
    @JsonIgnore//不显示 忽略
    public  boolean isSuccess(){
        return this.status == ResconseCode.SUCCESS;
    }







    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getDate() {
        return date;
    }

    public void setDate(T date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "status=" + status +
                ", date=" + date +
                ", msg='" + msg + '\'' +
                '}';
    }


}
