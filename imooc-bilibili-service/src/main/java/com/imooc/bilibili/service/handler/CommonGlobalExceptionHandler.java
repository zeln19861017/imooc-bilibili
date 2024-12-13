package com.imooc.bilibili.service.handler;

import com.imooc.bilibili.domain.JsonResponse;
import com.imooc.bilibili.domain.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/* 全局的异常处理:

* 当控制器方法抛出异常时，commonExceptionHandler 会被调用来处理这些异常,
* 异常处理方法返回的 JsonResponse 对象会被自动转换为 JSON 格式，并作为 HTTP 响应返回给客户端。
* 如果异常是 ConditionException 类型，那么就会进行特殊处理（如错误码的返回）。
* 如果是其他类型的异常，则返回一个通用的错误响应。
* @ResponseBody 注解表示将返回值直接写入 HTTP 响应体中，而不是通过视图解析器解析成页面
*/

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
//Spring框架提供的一个注解，用于对控制器提供一些增强功能，主要用于处理全局的异常、数据的绑定以及预处理请求参数等。
// 最大最小优先级

public class CommonGlobalExceptionHandler {
    // 引用处理 domain.exception.ConditionException

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e){
        String errorMsg= e.getMessage();
        if(e instanceof ConditionException){
            // 定制化的ConditionException
            String errorCode = ((ConditionException)e).getCode();
            return new JsonResponse<>(errorCode,errorMsg);
        }else {
            //
            return new JsonResponse<>("500", errorMsg);
        }
    }


}
