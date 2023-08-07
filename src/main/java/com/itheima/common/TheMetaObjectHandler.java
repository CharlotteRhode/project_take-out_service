package com.itheima.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.lang.management.LockInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;


//公共字段自动填充(update_time, etc.)
@Slf4j
@Component
public class TheMetaObjectHandler implements MetaObjectHandler {


    //新增
    @Override
    public void insertFill(MetaObject metaObject) {
            metaObject.setValue("createTime", LocalDateTime.now());
            metaObject.setValue("updateTime", LocalDateTime.now());
            metaObject.setValue("createUser", BaseContextForMetaHandler.getCurrentId());
            metaObject.setValue("updateUser", BaseContextForMetaHandler.getCurrentId());

    }

    //更新/修改
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContextForMetaHandler.getCurrentId());


    }
}
