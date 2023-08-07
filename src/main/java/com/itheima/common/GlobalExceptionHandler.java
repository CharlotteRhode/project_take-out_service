package com.itheima.common;



//全局异常处理器:当controller里抛异常的时候，会自动被这个处理器类拦截并进行处理

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;


@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody //for json
public class GlobalExceptionHandler {


    /*由于employee表中的username字段已经设置成unique,所以在新增员工时，用户如果输入了相同的username, 则controller抛出异常，被
    这个处理器拦截*/
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        if (ex.getMessage().contains("Duplicate entry")){
            String[] arr = ex.getMessage().split(" ");
            String msg = arr[2] + " 已经存在，请重新输入一个username";
            return R.error(msg);
        }
        return R.error("发生未知错误");
    }



    //根据id删除菜品类别时，如果已经关联了某个菜品，或者已经关联了某个套餐，那么这个类别不能删除，抛出异常：
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        return R.error(ex.getMessage());
    }




}
