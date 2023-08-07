package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.common.BaseContextForMetaHandler;
import com.itheima.common.R;
import com.itheima.pojo.AddressBook;
import com.itheima.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Watchable;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {


    @Autowired
    private AddressBookService addressBookService;


    //新增一个地址
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContextForMetaHandler.getCurrentId());
        addressBookService.save(addressBook);

        return R.success(addressBook);
    }

    //把某一个地址设置成默认地址：
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContextForMetaHandler.getCurrentId());
        wrapper.eq(AddressBook::getIsDefault, 0);

        //把这个用户名下所有的地址都改成0：
        //update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        //再把这一条改成1:
        addressBook.setIsDefault(1);

        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }


    //根据id查询地址
    @GetMapping("/{id}")
    public R get(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null){
            return R.success(addressBook);
        }
        return R.error("没有找到这个地址");
    }


    //查询默认地址
    @GetMapping("default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContextForMetaHandler.getCurrentId());
        wrapper.eq(AddressBook::getIsDefault, 1);

        //select * from address_book where user_id = ? and is_ default = 1
        AddressBook addressBook = addressBookService.getOne(wrapper);

        if (addressBook == null){
            return R.error("没有找到改地址");
        }
        return R.success(addressBook);
    }

    //查询一个用户的全部地址：
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContextForMetaHandler.getCurrentId());

        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(addressBook.getUserId() != null, AddressBook::getId, addressBook.getUserId());
        wrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> resList = addressBookService.list(wrapper);

        return R.success(resList);
    }









}
