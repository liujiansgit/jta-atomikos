package com.example.atomikos.dao.first;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Author: Administrator
 * @Date: 2019/9/10 11:36
 */
@Mapper
public interface FirstDao {

    /**
     * user表插入
     *
     * @param map
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    @Insert("INSERT INTO `user`.`table_user`( `user_name`) VALUES ( #{userName})")
    Integer insertUser(Map<String,Object> map);

}
