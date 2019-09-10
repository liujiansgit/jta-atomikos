package com.example.atomikos.dao.second;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Administrator
 * @Date: 2019/9/10 11:36
 */
@Mapper
public interface SecondDao {

    /**
     * 插入用户日志
     *
     * @param userId
     * @param event
     * @return
     */
    @Insert("INSERT INTO `user_log`.`user_log_event`(`user_id`, `event`, `log_time`) VALUES (#{userId}, #{event}, now());")
    Integer insertLog(@Param("userId") String userId, @Param("event") String event);

}
