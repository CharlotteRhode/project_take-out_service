package com.itheima.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContextForMetaHandler;
import com.itheima.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.swing.plaf.synth.SynthTreeUI;
import java.io.IOException;


//用来检查用户是否已经通过登陆页面登陆的->过滤器



@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*") //拦截所有
public class LoginCheckFilter implements Filter {

    //路径匹配器->用来匹配下面设置的要直接放行的前端页面路径
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest)  servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1. 获取本次请求的url：
        String requestURI = request.getRequestURI();

        //2.判断本次请求是否需要处理（是否需要去检查用户的登陆状态）
              //2.1 -> 设置不需要判断，直接放行的路径集
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",  //静态资源可以直接放行，无所谓他看
                "/front/**",
                "/user/sendMsg",
                "/user/login"

        };

            //2.2 判断
        boolean noNeedToHandle = checkUrl(urls, requestURI);


        //3.如果不需要处理，则直接放行
        if (noNeedToHandle){
            filterChain.doFilter(request,response);
            return;
        }

        //4 -1(backend). 如果传进来的路径不在“放行集”里->则需要判断这个人是否已经登陆：从session里获取这个人：
             //如果能从session里取出这个人->那这个人已经登陆了：
        if (request.getSession().getAttribute("employee") != null){

            //for MetaObjectHandler:把通过session获取到的当前用户id装进BaseContext类里->
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContextForMetaHandler.setCurrentId(empId);


            filterChain.doFilter(request,response);
            return;
        }


        //4 -2(front/app). 如果传进来的路径不在“放行集”里->则需要判断这个人是否已经登陆：从session里获取这个人：
        //如果能从session里取出这个人->那这个人已经登陆了：
        if (request.getSession().getAttribute("user") != null){

            //for MetaObjectHandler:把通过session获取到的当前用户id装进BaseContext类里->
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContextForMetaHandler.setCurrentId(userId);


            filterChain.doFilter(request,response);
            return;
        }




        //5. 如果从session里取不出来这个人-> 那么这个人还没有登陆呢->返回登陆页面：
           //**注意，不是直接返回页面->要结合前端写的页面看（backend->request.js），这里是通过输出流的方式，向客户端页面响应数据:
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN"))); //用pom->fast json alibaba：
        return;

    }



    //为上面2.2写一个方法： 用匹配器来检查，传过来的路径，是否在上面的“直接放行路径集”里->
    public boolean checkUrl(String[] urls, String requestURI){
        for (String each : urls) {
            boolean match = PATH_MATCHER.match(each, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }




}
