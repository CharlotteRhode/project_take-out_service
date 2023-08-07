package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.dto.SetmealDto;
import com.itheima.mapper.SetMealMapper;
import com.itheima.pojo.Setmeal;
import com.itheima.pojo.SetmealDish;
import com.itheima.service.SetMealService;
import com.itheima.service.SetmealDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {


    @Autowired
    private SetmealDishService setmealDishService;



    //套餐 - 保存按钮 - 需要关联2张表：
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        //保存套餐的基本信息 - setmeal表 - insert操作：
        this.save(setmealDto);


        //保存菜品的关联信息 - setmeal_dish表 - insert操作：
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();


        setmealDishes.stream().map((each) -> {
            each.setSetmealId(setmealDto.getId());
            return each;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }


    //套餐 - 删除：
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //只有停售的套餐才能删除：
        //所以先查询状态：
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, 1);

        long count = this.count(wrapper);

        //如果不能删除，抛出异常信息：
        if (count > 0){
            throw  new CustomException("can't delete it, ur selling it");
        }

        //删除套餐表：
        this.removeByIds(ids);

        //删除关系表：
        LambdaQueryWrapper<SetmealDish> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(wrapper1);
    }




}
