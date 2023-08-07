package com.itheima.config;


import com.itheima.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;


@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /*
    默认情况下：浏览器只能访问在static或者templates文件夹下的文件。
    现在假如说，我们的前端资源文件们，没有在这两个文件夹下的话，就需要-> 创建这个配置类 ->
    设置静态资源（static）（移动端+管理员）页面 -> 的路径映射：在浏览器地址栏输入相应的地址以后能访问到
    相对应的页面
    */
    protected void addResourceHandlers(ResourceHandlerRegistry registry){


        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }



    //扩展mvc的消息转换器-> for 禁用/启用 员工账号 时， id被js丢失精度的问题-> 把java对象转换成json对象：
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //用common->JacksonObjectMapper类，将java对象转换为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //把我们自己建造的这个转换器，放进mvc提供的这个转换器容器中：
        converters.add(0, messageConverter); //index设置为0，第一个先用它
    }




}
