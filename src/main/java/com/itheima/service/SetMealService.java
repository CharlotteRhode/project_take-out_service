package com.itheima.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.SetmealDto;
import com.itheima.pojo.Setmeal;

import java.util.List;


public interface SetMealService extends IService<Setmeal> {

    //套餐 - 保存按钮 - 需要关联2张表：
    public void saveWithDish(SetmealDto setmealDto);


    //套餐 - 删除：
    public void removeWithDish(List<Long> ids);
}
