package com.imooc.bilibili.api;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RestfulApi {

    // 定义内部变量存储数据
    private  final Map< Integer, Map<String,Object>> dataMap;

    // 初始化dataMap
    public RestfulApi() {
        dataMap = new HashMap<>();
        for (int i =1; i<4;i++){
            Map<String,Object> data = new HashMap<>();
            data.put("id",1);
            data.put("name","name_" + i);
            dataMap.put(i,data);
        }
    }

    // 4种HTTP方法
    // GET
    @GetMapping("/objects/{id}")
    public Map<String, Object> getData(@PathVariable  Integer id){

        Map<String,Object> containedData = dataMap.get(id);
        if(containedData == null){
            return  null ;
        }else{
            return containedData;
        }
    }

    // DELETE
    @DeleteMapping("/objects/{id}")
    public String deleteData(@PathVariable Integer id){
        dataMap.remove(id);
        return "delete success";
    }

    // POST
    @PostMapping("/objects")
    public String postData( @RequestBody Map<String, Object> data){
        // #将一个 Map 的键（key）转换为一个 Integer 数组 通用#
            // dataMap.keySet()  返回的是一个 Set<Integer>，其中包含所有的 Integer 键
            // .toArray(new Integer[0])：toArray是 Collection 接口提供的方法，将 Set 转换为一个数组
            // new Integer[0] 是一个大小为 0 的数组，toArray() 方法会根据集合的实际大小返回一个适当大小的 Integer[] 数组。
            // new Integer[0] 是一种标准的写法，通常会被优化。

        // 获取目前dataMap中key的集合 以便于新增时id+1
        Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
        Arrays.sort(idArray);// 升序排序
        int nextId = idArray[idArray.length-1] +1;
        dataMap.put(nextId,data);
        return "post success";
    }

    // PUT
    @PutMapping("/objects")
    public String putData(@RequestBody Map<String,Object> data) {
        // 取出数据中的id 判断是更新还是新增
            // 从 data 中获取 "id" 对应的值;
            // 将这个值转换成 String（确保它是字符串形式）;
            // 将字符串转换成 Integer
        Integer id = Integer.valueOf(String.valueOf(data.get("id")));
        Map<String,Object> containedData = dataMap.get(id);
        if(containedData == null){
            Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
            Arrays.sort(idArray);
            int nextId = idArray[idArray.length - 1] + 1;
            dataMap.put(nextId,data);
        }else{
            dataMap.put(id,data);
        }

        return  "put success";

    }
}
