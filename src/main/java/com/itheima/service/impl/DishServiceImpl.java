package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.dto.DishDto;
import com.itheima.mapper.DishMapper;
import com.itheima.pojo.Dish;
import com.itheima.pojo.DishFlavor;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    //新增菜品时，同时保存flavor数据：
    @Override
    @Transactional //涉及到多张表的操作 -> 开始事务控制：
    public void saveWithFlavor(DishDto dishDto) {

        //保存一道菜的基本信息到dish表：
        this.save(dishDto);

        //把每道菜的dish_id拿出来：
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();

        //给每一道菜赋值一个id:
        flavors = flavors.stream().map((each) -> {
            each.setDishId(dishId);
            return each;
        }).collect(Collectors.toList());


        //保存一道菜的口味信息到dish_flavor表：
        dishFlavorService.saveBatch(flavors);
    }


    //根据id来回显菜品信息（包含口味）-> 要查2张表：
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的基本信息：dish
        Dish dish = this.getById(id);

        //properties copy: 把基本信息dish的属性都拷贝到dishDto里：
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);


        //查询菜品的口味信息：dish_flavor
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(wrapper);

        //给dishDto赋值flavors属性：
        dishDto.setFlavors(flavors);

        return dishDto;

    }


    //修改菜品->点保存按钮以后->回显：(更新dish信息，也同时更新dish_flavor信息）：
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        //更新dish表：
         this.updateById(dishDto);

        //清理以前的口味信息：delete from dish_flavor where dish_id=?
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(wrapper);

        //更新dish_flavor表：insert:
        List<DishFlavor> flavors = dishDto.getFlavors();

        //***!!
        flavors = flavors.stream().map( (each)->{
            each.setDishId(dishDto.getId());
            return each;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }


}
