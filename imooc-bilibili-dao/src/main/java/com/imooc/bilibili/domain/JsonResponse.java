package com.imooc.bilibili.domain;

// json返回数据
public class JsonResponse<T> {
    private String code;
    private String msg;
    private T data;

    public JsonResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public JsonResponse(T data) {
        this.data = data;
        msg = "success";
        code = "0";
    }

    // 根据参数类型定义不同返回类型
    // 不需要返回数据
    public static JsonResponse<String> success() {
        return new JsonResponse<>(null);
    }

    // 返回数据
    public static JsonResponse<String> success(String data) {
        return new JsonResponse<>(data);
    }

    // 通用
    public static JsonResponse<String> fail() {
        return new JsonResponse<>("1", "failed");
    }

    // 自定义返回信息
    public static JsonResponse<String> fail(String code, String msg) {
        return new JsonResponse<>(code, msg);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}


