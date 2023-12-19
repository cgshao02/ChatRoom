package com.cgshao.chatroom.service.impl;

import com.cgshao.chatroom.mapper.UserMapper;
import com.cgshao.chatroom.pojo.User;
import com.cgshao.chatroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }
}
