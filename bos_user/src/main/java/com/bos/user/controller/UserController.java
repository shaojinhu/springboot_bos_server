package com.bos.user.controller;

import com.bos.execption.MyException;
import com.bos.pojo.user.User;
import com.bos.response.Result;
import com.bos.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
     * 获取用户User的权限Permission
     * @param request
     * @return
     * @throws MyException
     */
    @RequestMapping(value = "/getPermission",method = RequestMethod.POST)
    private  Result getPermission(HttpServletRequest request) throws MyException {
        return userService.getPermission(request);
    }

    /**
     * 获得用户列表
     * @param map
     * @return
     */
    @PostMapping("getUserList")
    public Result getUserList(@RequestBody Map<String,String> map){
        return userService.getUserList(map);
    }


    /**
     * 添加用户User
     * @param user
     * @return
     */
    @PostMapping("addUser")
    public Result adduser(@RequestBody User user){
        return userService.addUser(user);
    }

    /**
     * 修改用户User
     * @param user
     * @return
     */
    @PutMapping("updateUser")
    public Result updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    /**
     * 删除用户User
     * @param user
     * @return
     */
    @DeleteMapping("deleteUser")
    public Result deleteUser(@RequestBody User user){
        return userService.deleteUser(user);
    }
}
