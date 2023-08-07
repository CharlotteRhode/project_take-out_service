package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.dto.SetmealDto;
import com.itheima.pojo.Category;
import com.itheima.pojo.Dish;
import com.itheima.pojo.Setmeal;
import com.itheima.service.CategoryService;
import com.itheima.service.SetMealService;
import com.itheima.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {


    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    //新增套餐 - 保存：
    @PostMapping
    public R<String> saveSetMeal(@RequestBody SetmealDto setmealDto) {
        setMealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }


    //套餐 - 分页展示（包含图片下载）：
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Setmeal::getName, name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);

        setMealService.page(pageInfo, wrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((each) -> {

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(each, setmealDto);

            Long categoryId = each.getCategoryId();
            Category thisone = categoryService.getById(categoryId);
            if (thisone != null) {
                String categoryName = thisone.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;

        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }


    //套餐 - 删除（单个， 批量 - 用同一个）：
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setMealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }


    //为前端app洁面查询/展示2种套餐的信息：
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        wrapper.orderByDesc(Setmeal::getUpdateTime);

        return R.success(setMealService.list(wrapper));

    }




}
