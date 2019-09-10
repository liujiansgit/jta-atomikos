package com.example.atomikos.controller;

import com.example.atomikos.dao.first.FirstDao;
import com.example.atomikos.dao.second.SecondDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.UUID;

/**
 * @Author: Administrator
 * @Date: 2019/9/10 11:55
 */
@RestController
@Slf4j
public class TestController {

    @Autowired
    private FirstDao firstDao;

    @Autowired
    private SecondDao secondDao;

    @GetMapping("/testInsert")
    @Transactional
    public Integer testInsert() {
        HashMap<String, Object> hashMap = new HashMap<>();
        String userId = UUID.randomUUID().toString().replaceAll("-", "");
        hashMap.put("userName", userId);
        Integer user = firstDao.insertUser(hashMap);
//        int a = 1 / 0; //模拟异常
        secondDao.insertLog(hashMap.get("userId").toString(), "测试插入用户" + userId);
        return user;
    }

}
