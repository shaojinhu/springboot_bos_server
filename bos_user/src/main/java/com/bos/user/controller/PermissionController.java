package com.bos.user.controller;

import com.bos.response.Result;
import com.bos.user.service.PermissionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    /**
     * 获取Permission列表
     */
    @PostMapping("getPermissionList")
    public Result getPermissionList(){
        return permissionService.getPermisionList();
    }

}
