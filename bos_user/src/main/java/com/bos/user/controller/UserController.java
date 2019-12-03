package com.bos.user.controller;

import com.bos.execption.MyException;
import com.bos.pojo.user.User;
import com.bos.response.Result;
import com.bos.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("user")
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 登录接口
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    private Result login(@RequestBody User user) throws Exception {
        return userService.login(user);
    }

    /**
     * 获取用户权限
     */
    @RequestMapping(value = "/getPermission",method = RequestMethod.POST)
    private  Result getPermission(HttpServletRequest request) throws MyException {
        return userService.getPermission(request);
    }

    @PostMapping("addUser")
    public Result adduser(@RequestBody User user){
        return userService.addUser(user);
    }

}
