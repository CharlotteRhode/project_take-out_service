package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.DishDto;
import com.itheima.pojo.Dish;




public interface DishService extends IService<Dish> {

    //新增菜品时-> 需要操作2张表：dish + dish_flavor:
    public void saveWithFlavor(DishDto dishDto);


    //根据id来回显菜品信息（包含口味）：
    public DishDto getByIdWithFlavor(Long id);


    //修改菜品->点保存按钮以后->回显：(更新dish信息，也同时更新dish_flavor信息）：
    public void updateWithFlavor(DishDto dishDto);
}
