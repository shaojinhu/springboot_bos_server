package com.bos.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bos.execption.MyException;
import com.bos.pojo.user.Depa;
import com.bos.pojo.user.Permission;
import com.bos.pojo.user.Role;
import com.bos.pojo.user.User;
import com.bos.response.PageResult;
import com.bos.response.ProfileResult;
import com.bos.response.Result;
import com.bos.response.ResultCode;
import com.bos.user.mapper.PermissMapper;
import com.bos.user.mapper.RoleMapper;
import com.bos.user.mapper.UserMapper;
import com.bos.user.repository.DepaRepository;
import com.bos.user.repository.RoleRepository;
import com.bos.user.repository.UserRepository;
import com.bos.user.service.UserService;
import com.bos.util.IdWorker;
import com.bos.util.JWTUtils;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional
@SuppressWarnings("all")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private PermissMapper permissMapper;
    @Resource
    private IdWorker idWorker; //全局id生成类
    @Resource
    private RoleRepository roleRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private DepaRepository depaRepository;


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
        user.setUserid(id);
        //解析角色组
        Set<Role> roles = new HashSet<>();
        String[] split = user.getRoleIds().split(",");
        for (String s : split) {
            Role role = roleRepository.findById(s).get();
            roles.add(role);
        }
        if(roles.size() > 0){
            user.setRoles(roles);
            //执行添加操作，同时会添加中间表
            userRepository.save(user);
            return Result.SUCCESS();
        }
        return Result.FAIL();
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @Override
    public Result updateUser(User user) {
        //首先查询该用户
        User target = userRepository.findById(user.getUserid()).get();
        BeanUtils.copyProperties(user,target);//将user的值赋值给target
        //解析角色
        Set<Role> roles = new HashSet<>();
        String[] split = user.getRoleIds().split(",");
        for (String s : split) {
            Role role = roleRepository.findById(s).get();
            roles.add(role);
        }
        if(roles.size()>0 && ObjectUtils.anyNotNull(roles)){
            //执行添加操作
            target.setRoles(roles);
            userRepository.save(target);
            return Result.SUCCESS();
        }
        return Result.FAIL();
    }

    /**
     * 删除用户User
     * @param user
     * @return
     */
    @Override
    public Result deleteUser(User user) {
        //直接删除用户（逻辑删除、物理删除），这里使用物理删除
        try {
            userRepository.delete(user);
            return Result.SUCCESS();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
    }

    /**
     * 获取用户User列表，有分页
     * @param map
     * @return
     */
    @Override
    public Result getUserList(Map<String, String> map) {
        //获得部门id
        String depaId = map.get("depaId");
        //首先查出部门下所有的用户id
         List<String> userListByDepaId = depaRepository.getUserListByDepaId(depaId);
        if(userListByDepaId.size()  == 0){
            return new Result(ResultCode.DEPA_IS_NOTHAVE_USER);
        }
        //分页
        Pageable pageable = PageRequest.of(Integer.parseInt(map.get("page"))-1,Integer.parseInt(map.get("size")),new Sort(Sort.Direction.DESC,"userid"));
        //条件IN查询
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                List<Predicate> predicates = new ArrayList<>();
//                Path<Object> path = root.get("userid");//定义查询的字段
//                CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
//                for (int i = 0; i <userListByDepaId.size() ; i++) {
//                    in.value(userListByDepaId.get(i));//存入值
//                }
//                predicates.add(criteriaBuilder.and(criteriaBuilder.and(in)));//存入条件集合里
//                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

                List<Predicate> list = new ArrayList<Predicate>();
                Expression<String> exp = root.<String>get("userid");
                list.add(exp.in(userListByDepaId));
                if(list.size() >0){
                    System.out.println("list"+list.size());
                    return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                }else{
                    return null;
                }
            }
        };
        Page<User> all = userRepository.findAll(specification,pageable);
        PageResult<User> pageResult = new PageResult<>(all.getTotalElements(),all.getContent());
        return new Result(ResultCode.SUCCESS,pageResult);
    }


}
