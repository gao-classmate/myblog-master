package com.xsh.test01;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestMap {
    public static void main(String[] args) {
        Map<String,String> map=new HashMap<>();
        for(int i=0;i<100;i++){
            map.put(i+"test",i+"00"+i);
        }


        Set<Map.Entry<String, String>> entries = map.entrySet();
        for(Map.Entry<String,String> entry : entries){
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("key:"+key+" "+"value:"+value);
        }
        System.out.println(map.size());
    }
}
