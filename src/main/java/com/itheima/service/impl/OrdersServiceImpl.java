package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.BaseContextForMetaHandler;
import com.itheima.common.CustomException;
import com.itheima.mapper.OrdersMapper;
import com.itheima.pojo.*;
import com.itheima.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    @Override
    @Transactional
    public void submit(Orders orders) {

        //获取当前用户的id
        Long currentId = BaseContextForMetaHandler.getCurrentId();

        //获取他的购物车
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        if (shoppingCarts == null){
            throw new CustomException("购物车为空");
        }

        //查询用户数据
        User user = userService.getById(currentId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressInfo = addressBookService.getById(addressBookId);


        //计算购物车里的总金额->遍历购物车里的每条数据 ->
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((each)->{
            OrderDetail orderDetail = new OrderDetail();



            orderDetail.setNumber(each.getNumber());//份数
            orderDetail.setDishFlavor(each.getDishFlavor());
            orderDetail.setAmount(each.getAmount());//菜品单价
            //etc...

            // 每遍历一样东西， 都 += 单价 x 份数
            amount.addAndGet(each.getAmount().multiply(new BigDecimal(each.getNumber())).intValue());

            return orderDetail;

        }).collect(Collectors.toList());



        //往orders表里插入1条
        long orderId = IdWorker.getId();
        orders.setNumber(String.valueOf(orderId));

        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get())); //总金额->遍历购物车数据->
        orders.setUserName(user.getName());
        orders.setConsignee(addressInfo.getConsignee());

        this.save(orders);



        //往order_detail表里插入多条
        orderDetailService.saveBatch(orderDetails);



        //清空购物车
        shoppingCartService.remove(wrapper);
    }





}
