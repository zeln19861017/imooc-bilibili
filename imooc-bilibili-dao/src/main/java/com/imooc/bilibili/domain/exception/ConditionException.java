package com.imooc.bilibili.domain.exception;

// 条件来抛异常
public class ConditionException extends  RuntimeException {

    public static final long serialVersionUID =1;
    private String code ;

    public ConditionException (String code, String name){
        super(name); // 指向最近的一个父类的
        this.code=code;
    }

    public ConditionException(String name){
        super(name);
        code = "500";
    }

    //getter setter
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
