package com.zmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.zmall.common.Const;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.User;
import com.zmall.service.IOrderService;
import com.zmall.service.IUserService;
import com.zmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/12  22:23
 */
@Controller
public class OrderManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    /**
     * 后台获取订单列表接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/orders", method = RequestMethod.GET)
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充我们增加产品的业务逻辑
            return iOrderService.manageList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 后台订单搜索的接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/orders/search", method = RequestMethod.GET)
    // TODO: 2017/11/12 增加按商品名称查询
    public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充我们增加产品的业务逻辑
            return iOrderService.manageSearch(orderNo, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 后台获取订单详细信息接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/orders/{orderNo}", method = RequestMethod.GET)
    public ServerResponse<OrderVo> orderDetail(HttpSession session, @PathVariable("orderNo") Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充我们增加产品的业务逻辑
            return iOrderService.manageDetail(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 后台订单发货的接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/orders/{orderNo}/shipment", method = RequestMethod.PUT)
    public ServerResponse<String> orderSendGoods(HttpSession session, @PathVariable("orderNo") Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充我们增加产品的业务逻辑
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}
