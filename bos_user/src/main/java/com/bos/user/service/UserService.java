package com.bos.user.service;

import com.bos.execption.MyException;
import com.bos.pojo.user.User;
import com.bos.response.Result;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    Result login(User user) throws Exception;

    Result getPermission(HttpServletRequest request) throws MyException;

    Result addUser(User user);
}
