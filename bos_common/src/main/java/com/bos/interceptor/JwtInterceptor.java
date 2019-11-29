package com.bos.interceptor;

import com.bos.execption.MyException;
import com.bos.response.ResultCode;
import com.bos.util.JWTUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@SuppressWarnings("all")
public class JwtInterceptor extends HandlerInterceptorAdapter {



  /**执行控制器方法之前执行的方法*/
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws MyException {
//      try {
          response.setHeader("Access-Control-Allow-Origin","*");
          response.setHeader("Access-Control-Allow-Headers","Content-Type,Content-Length,Authorization,Accept,X-Requested-With");
          response.setHeader("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
          String method= request.getMethod();
          if (method.equals("OPTIONS")){
              response.setStatus(200);
              return false;
          }

          String urlstr = request.getRequestURI();
          System.out.println(request);
          if(urlstr.equals("/user/login")){
              return true;
          }
          String Authorization = request.getHeader("Authorization");
          if(!StringUtils.isEmpty(Authorization) && Authorization.startsWith("Bearer")){
             String token = Authorization.substring(6).trim();
             Claims claims = JWTUtils.parseJwt(token);
                if(claims == null){
                    throw new MyException(ResultCode.PERMISSION_IS_NULL);
                }
              return true;
          }else{
              throw new MyException(ResultCode.TOKEN_IS_NULL);
          }
//      }catch (Exception e){ throw new MyException(ResultCode.FAIL); }
  }


}




