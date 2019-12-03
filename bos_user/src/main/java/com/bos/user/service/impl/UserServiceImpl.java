package com.bos.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bos.execption.MyException;
import com.bos.pojo.user.Permission;
import com.bos.pojo.user.Role;
import com.bos.pojo.user.User;
import com.bos.response.ProfileResult;
import com.bos.response.Result;
import com.bos.response.ResultCode;
import com.bos.user.mapper.PermissMapper;
import com.bos.user.mapper.UserMapper;
import com.bos.user.repository.RoleRepository;
import com.bos.user.service.UserService;
import com.bos.util.IdWorker;
import com.bos.util.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private PermissMapper permissMapper;
    @Resource
    private IdWorker idWorker;
    @Resource
    private RoleRepository roleRepository;


    /**
     * 实现登录方法
     * @param user
     * @return
     */
    @Override
    public Result login(User user) throws Exception {
        //查询数据库是否由此用户（根据用户名唯一性，查询密码是否正确）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",user.getUsername());
        User target = userMapper.selectOne(queryWrapper);
        if(target == null) return new Result(ResultCode.LOGIN_USER_NOT_ENABLE_STATE);//无此用户
        if(!target.getPassword().equals(user.getPassword())){ return new Result(ResultCode.LOGIN_USER_NOT_ENABLE_STATE);}//密码错误
        else{//登陆成功
            //获取用户权限
            final List<Permission> permissionList = permissMapper.getPermission(target.getUserid());
                    //我们可以根据我们自定义的数据生成jwt，这里因为只是先设置了菜单，所以先不添加api权限
                   // StringBuffer sb = new StringBuffer();
                    //            if(permissionList != null && permissionList.size() > 0){
                    //                for (Permission permission : permissionList) {
                    //                    if(permission != null &&  permission.getP_type() != null){
                    //                        if(permission.getP_type().equals("3")){  type是3的为api权限
                    //                            sb.append(permission.getCode()).append(",");
                    //                        }
                    //                    }
                    //                }
                    //            }
            Map<String,Object> claims = new HashMap<>();
            claims.put("userId",target.getUserid());
            claims.put("nickName",target.getNikename());
            //生成token
            final String jwtToken = JWTUtils.createJwt(target.getUserid().toString(), target.getNikename(), claims);
            return new Result(ResultCode.SUCCESS,jwtToken);
        }
    }

    /**
     * 获取用户权限
     * @param request
     * @return
     */
    @Override
    public Result getPermission(HttpServletRequest request) throws MyException {
        //从请求头中获取token
        Claims claims = JWTUtils.getClaims(request);
        String id = claims.getId();
        if(id == null || id.equals("")){
            throw  new MyException(ResultCode.TOKEN_IS_NULL);//token为空
        }
        Integer userId = Integer.parseInt(id);
        User user = userMapper.selectById(userId);
        if(user == null ){ throw new MyException(ResultCode.LOGIN_IS_EXCEPTION);}//登录异常
        //查询用户的所有权限
        List<Permission> permission = permissMapper.getPermission(user.getUserid());
        if(permission.size() == 0 || permission == null){
            throw new MyException(ResultCode.PERMISSION_IS_NULL);//用户权限为空
        }else{
            ProfileResult profileResult = new ProfileResult(user,permission);
            return new Result(ResultCode.SUCCESS,profileResult);
        }
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Override
    public Result addUser(User user) {
        //生成全局id
        String id = idWorker.nextId()+"";

        //解析角色组
        Set<Role> roles = new HashSet<>();
        String[] split = user.getRoleIds().split(",");
        for (String s : split) {
            Role role = roleRepository.findById(Long.parseLong(s)).get();
            roles.add(role);
        }
        if(roles.size() > 0){
            //执行添加操作
        }
        return Result.FAIL();
    }


}
