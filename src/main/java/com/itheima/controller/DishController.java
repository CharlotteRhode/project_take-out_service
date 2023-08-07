package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.pojo.Category;
import com.itheima.pojo.Dish;
import com.itheima.pojo.DishFlavor;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ietf.jgss.GSSContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    //新增一道菜->点击保存时：
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }


    //菜品管理 -> 菜品的分页查询：
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //创建构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        wrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        wrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo, wrapper);

        //对象拷贝：
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list =   records.stream().map( (item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;

        }).collect(Collectors.toList());


        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    //修改菜品->根据id回显菜品(包含口味->dishDto)的信息：
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto list = dishService.getByIdWithFlavor(id);
        return R.success(list);
    }



    //修改菜品->点保存按钮以后->回显：
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }


    //套餐查询 - 根据条件查询菜品：
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        wrapper.eq(Dish::getStatus, 1);
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(wrapper);

        //for dto:不管显示菜品，同时也要显示口味信息：
        List<DishDto> dishDtoList = list.stream().map((each) ->{
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(each, dto);

            Long categoryId = each.getCategoryId();

            //根据id查询分类信息
            Category category = categoryService.getById(categoryId);

            if (category != null){
                String categoryName = category.getName();
                dto.setCategoryName(categoryName);
            }

            //通过当前菜品的id查询其口味
            Long dishId = each.getId();
            LambdaQueryWrapper<DishFlavor> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(DishFlavor::getDishId, dishId);
            //select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper2);
            dto.setFlavors(dishFlavorList);
            return dto;

        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }



    //套餐 - 起售/停售：
    @PostMapping("/status/{status}")
    public R<String> stopAndStart(@PathVariable int status, @RequestParam List<Long> ids){
        List<Dish> dishList = new ArrayList<>();
        for (int i=0; i< ids.size(); i++){
            Dish dish = new Dish();
            dish.setStatus(status);
            dish.setId(ids.get(i));
            dishList.add(dish);
        }

            dishService.updateBatchById(dishList);
        return R.success("操作成功");
    }
    
    //套餐 - 批量删除：
    @DeleteMapping
    public R<String> deleteBatchIds(@RequestParam List<Long> ids){

        //先批量删除口味
        for (Long id: ids){
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, id);

            dishFlavorService.remove(wrapper);
        }

        //再删除菜品
        dishService.removeByIds(ids);

        return R.success("批量删除成功");
    }




}
