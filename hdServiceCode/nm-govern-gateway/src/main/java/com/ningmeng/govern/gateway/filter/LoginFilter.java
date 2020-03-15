package com.ningmeng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.govern.gateway.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wangb on 2020/3/12.
 */
@Component
public class LoginFilter extends ZuulFilter {
    private static final Logger LOGG = LoggerFactory.getLogger(LoginFilter.class);
    @Autowired
    AuthService authService;
    @Override
    public String filterType() {
        //filterType：返回字符串代表过滤器的类型，如下
        // pre：请求在被路由之前执行
        // routing：在路由请求时调用
        // post：在routing和errror过滤器之后调用
        // error：处理请求时发生错误调用
        return "pre";
    }

    @Override
    public int filterOrder() {
        // filterOrder：此方法返回整型数值，通过此数值来定义过滤器的执行顺序，数字越小优先级越高。
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //是否启用当前过滤器  true 是  false 否
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //判断头部信息是否有Authorization，如果没有则拒绝访问，否则转发到微服务
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        HttpServletRequest request = requestContext.getRequest();
        //查询jwt令牌
        String authorization = authService.getJwtFromHeader(request);
        //取出头部信息Authorization
        if(authorization==null){
            access_denied();
        }
        //查询身份令牌
        String access_token = authService.getTokenFromCookie(request);
        if(access_token == null){
            //拒绝访问
            access_denied();
        }
        //从redis中校验身份令牌是否过期
        long expire = authService.getExpire(access_token);
        if(expire<=0){
            //拒绝访问
            access_denied();
        }
        //查询jwt令牌
        String jwt = authService.getJwtFromHeader(request);
        if(jwt == null){
            //拒绝访问
            access_denied();
        }
        return null;
    }
    //拒绝访问
    private void access_denied(){
        //上下文对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.setSendZuulResponse(false);//拒绝访问
        //设置响应内容
        ResponseResult responseResult =new ResponseResult(CommonCode.UNAUTHENTICATED);
        String responseResultString = JSON.toJSONString(responseResult);
        requestContext.setResponseBody(responseResultString);
        //设置状态码
        requestContext.setResponseStatusCode(200);
        HttpServletResponse response = requestContext.getResponse();
        response.setContentType("application/json;charset=utf-8");
    }
}
