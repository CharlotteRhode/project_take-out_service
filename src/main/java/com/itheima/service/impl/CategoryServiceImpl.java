package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.mapper.CategoryMapper;
import com.itheima.pojo.Category;
import com.itheima.pojo.Dish;
import com.itheima.pojo.Setmeal;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import com.itheima.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;



    //根据id删除菜品分类
    @Override
    public void remove(Long id) {


        //是否已经关联了菜品？
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, id);
        long count1 = dishService.count(dishWrapper);
        if (count1 > 0){
            throw new CustomException("当前类别已经关联了菜品，不能删除");
        }


        //是否已经关联了套餐？
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId, id);
        long count2 = setMealService.count(setmealWrapper);
        if (count2 > 0){
            throw new CustomException("当前类别已经关联了套餐，不能删除");
        }


        //以上2个条件都没关联，可以执行删除操作：
        super.removeById(id);


    }


}
