package com.itheima.mapper;

import ch.qos.logback.core.BasicStatusManager;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.common.BaseContextForMetaHandler;
import com.itheima.pojo.OrderDetail;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
