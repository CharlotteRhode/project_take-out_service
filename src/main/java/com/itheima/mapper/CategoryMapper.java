package com.itheima.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.pojo.Category;
import org.apache.ibatis.annotations.Mapper;


//菜品分类，套餐分类


@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
