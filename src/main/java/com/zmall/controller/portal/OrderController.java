package com.zmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.zmall.common.Const;
import com.zmall.common.ResponseCode;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.User;
import com.zmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/12  15:20
 */
@Controller
public class OrderController {
    private static  final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private IOrderService iOrderService;
    /**
     * 订单在支付宝预下单的接口
     */
    @ResponseBody
    @RequestMapping(value = "/orders/payment",method = RequestMethod.POST)
    public ServerResponse pay(Long orderNo,HttpSession session, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        String localUploadPath = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),localUploadPath);
    }

    /**
     * 订单状态查询的接口
     */
    @ResponseBody
    @RequestMapping(value = "/orders/{orderNo}/status",method = RequestMethod.GET)
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session,@PathVariable("orderNo") Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

    /**
     * 订单支付完成后支付宝进行回调的接口
     */
    @ResponseBody
    @RequestMapping(value = "/orders/payment/alipay_callback",method = RequestMethod.POST)
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        //把Map<String,String[]>转为Map<String,String>
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){
                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.
        //验签时需除去参数中的sing_type字段
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
            return null;
        }
        //todo 验证各种数据订单与数据库表中订单数据是否一致

        //校验通过,调用改变订单支付状态的服务
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        //返回success给支付宝停止订单的再次调用
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }


    /**
     * 订单创建的接口
     */
    @ResponseBody
    @RequestMapping(value = "/orders",method = RequestMethod.POST)
    public ServerResponse create(HttpSession session, Integer shippingId) throws Exception {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    /**
     * 订单确认页获取订单商品的接口
     */
    @ResponseBody
    @RequestMapping(value = "/orders/cart-product",method = RequestMethod.GET)
    public ServerResponse getOrderCartProduct(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    /**
     * 获取用户订单列表的接口
     */
    @ResponseBody
    @RequestMapping(value = "/orders",method = RequestMethod.GET)
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    /**
     * 获取订单详细信息的接口
     */
    @ResponseBody
    @RequestMapping(value = "/orders/{orderNo}",method = RequestMethod.GET)
    public ServerResponse detail(HttpSession session,@PathVariable("orderNo") Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    /**
     * 订单取消的接口
     */
    @ResponseBody
    @RequestMapping(value = "/orders/canel-server",method = RequestMethod.PUT)
    public ServerResponse cancel(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        return iOrderService.cancel(user.getId(),orderNo);
    }

}
