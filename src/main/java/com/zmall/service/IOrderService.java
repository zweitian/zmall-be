package com.zmall.service;

import com.github.pagehelper.PageInfo;
import com.zmall.common.ServerResponse;
import com.zmall.vo.OrderVo;

import java.util.Map;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/12  15:21
 */
public interface IOrderService {
    /*支付服务*/
    //订单预下单服务
    ServerResponse pay(Long orderNo, Integer userId, String localUploadPath);
    //支付宝回调服务
    ServerResponse aliCallback(Map<String, String> params);
    //订单支付状态查询服务
    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    /*订单服务*/
    //创建订单服务
    ServerResponse createOrder(Integer userId,Integer shippingId) throws Exception;
    //取消订单服务
    ServerResponse<String> cancel(Integer userId,Long orderNo);
    //从购物车中获取被勾选商品的接口
    ServerResponse getOrderCartProduct(Integer userId);
    //获取订单详细信息的接口
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
    //获取订单列表服务
    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    /**
     * 后台订单服务
     */
    //后台订单获取服务
    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);
    //后台订单详情获取服务
    ServerResponse<OrderVo> manageDetail(Long orderNo);
    //后台订单搜索服务
    ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);
    //后台订单发货服务
    ServerResponse<String> manageSendGoods(Long orderNo);
}
