package com.zmall.service;

import com.github.pagehelper.PageInfo;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.Shipping;

import java.util.Map;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/11  20:49
 */
public interface IShippingService {
    ServerResponse<Map> add(Integer userId, Shipping shipping);
    ServerResponse<String> del(Integer userId,Integer shippingId);
    ServerResponse update(Integer userId, Shipping shipping);
    ServerResponse<Shipping> select(Integer userId, Integer shippingId);
    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
