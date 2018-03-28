package com.zmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zmall.common.ServerResponse;
import com.zmall.dao.ShippingMapper;
import com.zmall.pojo.Shipping;
import com.zmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/11  20:49
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse<Map> add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount == 0){
            return ServerResponse.createByErrorMessage("新建地址失败");
        }
        Map result = Maps.newHashMap();
        result.put("shippingId",shipping.getId());
        return ServerResponse.createBySuccess("新建地址成功",result);
    }

    @Override
    public ServerResponse<String> del(Integer userId, Integer shippingId){
        int resultCount = shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("删除地址失败");
        }
        return ServerResponse.createBySuccess("删除地址成功");
    }
    @Override
    public ServerResponse update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount == 0){
            return ServerResponse.createByErrorMessage("更新地址失败");
        }
        return ServerResponse.createBySuccess("更新地址成功");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        //根据shippingId与userId获取收货地址详细信息
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if(shipping == null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess(shipping);
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
