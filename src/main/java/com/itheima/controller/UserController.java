package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.common.R;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.utils.SendSms;
import com.itheima.utils.ValidateCodeUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*these 2 api has problems maybe due to alibaba utils*/
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            //调阿里巴巴短信服务的api
            log.info("code={}", code);

            //把用户存储进session：
            session.setAttribute(phone,code);

            return R.success("code is sent");
        }

        return R.error("there's a problem, please try again");
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {

        log.info(map.toString());


        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //和用户之前填写的验证码进行比对
        Object codeInSession = session.getAttribute(phone);
        //如果比对一致，登录成功
        if (codeInSession != null && codeInSession.equals(code)){
            //如果此用户之前没有注册过，自动完成新用户注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userService.getOne(wrapper);

            if (user==null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }

            session.setAttribute("user", user.getId());
            return R.success(user);
        }


        return R.error("login failed");
    }




}
