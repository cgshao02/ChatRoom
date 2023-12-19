package com.cgshao.chatroom.controller;

import com.cgshao.chatroom.pojo.Result;
import com.cgshao.chatroom.pojo.User;
import com.cgshao.chatroom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/findAll")
    public List<User> findAll(){
        List<User> users = userService.findAll();
        return users;
    }

    /**
     * 登录 user 提交的用户数据，包含用户名和密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpSession session) {
        Result result = new Result();
        if(user != null && "123".equals(user.getPassword())) {
            result.setFlag(true);
            //将数据存储到session对象中
            session.setAttribute("user",user.getUsername());
        } else {
            result.setFlag(false);
            result.setMessage("登陆失败");
        }
        return result;
    }

    /**
     * 获取用户名
     * @param session
     * @return
     */
    @GetMapping("/getUsername")
    public String getUsername(HttpSession session) {

        String username = (String) session.getAttribute("user");
        return username;
    }
}
