package com.imooc.bilibili.service.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

// json转换数据类

// 配置注解，自动注入到springboot上下文
@Configuration
public class JsonHttpMessageConverterConfig {

    public static void main(String[] args) {
        // 同一对象循环引用的问题
        List<Object> list = new ArrayList<>();

        Object o = new Object();
        list.add(o);
        list.add(o);
        System.out.println(list.size());
        System.out.println(JSONObject.toJSONString(list));
        System.out.println(JSONObject.toJSONString(list,SerializerFeature.DisableCircularReferenceDetect));

    }
    // 经常与@Configuration配合 注入一些其他实体类
    @Bean
    @Primary // 标记更高优先级
    public HttpMessageConverters fastJsonHttpMessageConvertes(){
        // ali的 fastjson
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,         // 美化输出
                SerializerFeature.WriteMapNullValue,    // 转为null的字符串输出
                SerializerFeature.WriteNullListAsEmpty, // List字段如果为null,输出为[],而非null
                SerializerFeature.WriteMapNullValue,     // 输出值为null的字段
                SerializerFeature.MapSortField,          // 是否输出值为null的字段
                SerializerFeature.DisableCircularReferenceDetect // 消除对同一对象循环引用的问题
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return  new  HttpMessageConverters(fastConverter);
    }
}
