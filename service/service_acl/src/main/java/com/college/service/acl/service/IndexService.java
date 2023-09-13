package com.college.service.acl.service;


import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface IndexService {
    //根据用户名获取用户登录信息
    Map<String, Object> getUserInfo(String name);


    List<JSONObject> getMenu(String username);
}
