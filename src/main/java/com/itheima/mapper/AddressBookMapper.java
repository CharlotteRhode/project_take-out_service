package com.itheima.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
