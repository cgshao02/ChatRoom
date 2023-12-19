package com.cgshao.chatroom.mapper;

import com.cgshao.chatroom.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> findAll();
}
