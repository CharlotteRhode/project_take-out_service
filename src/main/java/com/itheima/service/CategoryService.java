package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.pojo.Category;

import java.io.PipedOutputStream;


public interface CategoryService extends IService<Category> {


    //根据id把一种菜品分类删除（依据条件：是否关联了套餐）
    public void remove(Long id);




}
