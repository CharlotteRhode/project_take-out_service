package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.pojo.Category;
import com.itheima.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;




    //新增菜品/套餐->用一个接口就行：
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category is :{}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }


    //分类页面的分页查询
    @GetMapping("/page")
    public R<IPage> getPage(int page, int pageSize){
        //分页构造器
        IPage<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        //添加排序条件：根据sort进行排序
        wrapper.orderByAsc(Category::getSort);
        //执行分页查询操作
        categoryService.page(pageInfo,wrapper);

        return R.success(pageInfo);
    }


    //根据id删除某个类别（根据是否关联了套餐判断）：
    @DeleteMapping
    public R<String> delete( Long ids ){  //在这把参数名称和浏览器对应上就可以了，serviceimpl什么的无所谓；
        categoryService.remove(ids);
        return R.success("此类别已经删除成功");
    }


    //修改某个类别
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("此类别的信息已经修改成功");
    }



    //新增菜品 -> 展示（查询）菜品分类列表：条件查询：
    @GetMapping("/list")
    public R<List<Category>> showList(Category category){
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType()!= null, Category::getType, category.getType());
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(wrapper);

        return R.success(list);
    }





}
