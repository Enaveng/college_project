package com.college.commonutils;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootTest
public class test {
    @Test
    public void test01() {
        Map<String, Object> map = new HashMap<>();
        map.put("apple", 1);
        map.put("banana", 2);
        map.put("orange", 3);
        Set<String> set = map.keySet();
        System.out.println(set);
    }
}
