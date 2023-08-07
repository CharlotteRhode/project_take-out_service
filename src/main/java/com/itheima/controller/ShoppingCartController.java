package com.itheima.controller;


import ch.qos.logback.core.hook.ShutdownHook;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.common.BaseContextForMetaHandler;
import com.itheima.common.R;
import com.itheima.pojo.ShoppingCart;
import com.itheima.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;


    //把一个菜品添加到购物车：
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        // 设置user_id:
        Long currentId = BaseContextForMetaHandler.getCurrentId();
        shoppingCart.setUserId(currentId);


        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentId);


        //查询当前的菜品/套餐，是否已经在购物车中:
        Long dishId = shoppingCart.getDishId();
        if (dishId != null){
            //说明被添加到购物车里的是单独的菜品dish
            wrapper.eq(ShoppingCart::getDishId, dishId);
        }else {
            //说明被添加到购物车里的是套餐setmeal
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId() );
        }

        //select * from shoppingCart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart thisDish = shoppingCartService.getOne(wrapper);

        //如果这个菜品已经存在在购物车中了，number + 1
        if (thisDish != null){
            Integer thisDishNumber = thisDish.getNumber();
            thisDish.setNumber(thisDishNumber + 1);
            shoppingCartService.updateById(thisDish);
        }else {
            //如果不存在，添加到购物车，number == 1；
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            thisDish = shoppingCart;
        }

        return R.success(thisDish);
    }


    //查询/展示购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContextForMetaHandler.getCurrentId());
        wrapper.orderByAsc(ShoppingCart::getCreateTime);

        return R.success(shoppingCartService.list(wrapper));
    }


    //清空按钮->清空购物车(by userId):
    @DeleteMapping("/clean")
    public R<String> clean(){
        //delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContextForMetaHandler.getCurrentId());

        shoppingCartService.remove(wrapper);

        return R.success("购物车已清空");
    }






}
