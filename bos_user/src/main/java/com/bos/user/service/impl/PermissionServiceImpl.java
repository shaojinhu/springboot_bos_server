package com.bos.user.service.impl;

import com.bos.pojo.user.Permission;
import com.bos.response.Result;
import com.bos.response.ResultCode;
import com.bos.user.mapper.PermissMapper;
import com.bos.user.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理的Service
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissMapper permissMapper;

    @Override
    public Result getPermisionList(){
        List<Permission> permissionsList = permissMapper.selectList(null);
        //调用递归方法，生成父子权限
        //首先获取非1的权限
        final List<Permission> permiss = permissionsList.stream().filter(item -> !item.getPtype().equals("1")).collect(Collectors.toList());
        //获得根菜单
        List<Permission> permissOne = permissionsList.stream().filter(item -> item.getPtype().equals("1")).collect(Collectors.toList());
        permissOne.parallelStream().forEach(item ->{
            setChild(item,permiss);
        });
        return new Result(ResultCode.SUCCESS,permissOne);
    }

    /**
     * 递归方法
     * @param permissions
     */
    private void setChild(Permission p,List<Permission> permissions){
        //获取与父id相等的list
        List<Permission> list = permissions.parallelStream().filter(item -> item.getParentid().equals(p.getPid().toString())).collect(Collectors.toList());
        p.setChildPerList(list);
        if(list!=null){
            list.parallelStream().forEach(item ->{
                setChild(item,permissions);
            });
        }
    }

}
