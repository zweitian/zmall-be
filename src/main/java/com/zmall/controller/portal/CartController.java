package com.zmall.controller.portal;

import com.zmall.common.Const;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.User;
import com.zmall.service.ICartService;
import com.zmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/11  17:13
 */
@Controller
public class CartController {
    @Autowired
    private ICartService iCartService;

    /**
     * 查询购物车商品列表的接口,需要登录状态
     */
    @ResponseBody
    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public ServerResponse<CartVo> list(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        return iCartService.list(user.getId());
    }

    /**
     * 购物车添加商品的接口
     */
    @ResponseBody
    @RequestMapping(value = "/cart", method = RequestMethod.POST)
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        return iCartService.add(user.getId(), productId, count);
    }

    /**
     * 购物车更新商品数量的接口
     */
    @ResponseBody
    @RequestMapping(value = "/cart/count", method = RequestMethod.PUT)
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        return iCartService.update(user.getId(), productId, count);
    }

    /**
     * 购物车删除商品的接口
     */
    @ResponseBody
    @RequestMapping(value = "/cart", method = RequestMethod.DELETE)
    public ServerResponse<CartVo> deleteProduct(HttpSession session, String productIds) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        return iCartService.deleteProduct(user.getId(), productIds);
    }

    /**
     * 购物车单项商品选择的接口
     */
    @ResponseBody
    @RequestMapping(value = "/cart/selection/{productId}", method = RequestMethod.PUT)
    public ServerResponse<CartVo> select(HttpSession session, @PathVariable("productId") Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    /**
     * 购物车单项商品取消选择的接口
     */
    @ResponseBody
    @RequestMapping(value = "/cart/deselection/{productId}", method = RequestMethod.PUT)
    public ServerResponse<CartVo> unSelect(HttpSession session, @PathVariable("productId") Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    /**
     * 获取购物车中商品数量的接口
     */
    @ResponseBody
    @RequestMapping(value = "/cart/count", method = RequestMethod.GET)
    public ServerResponse<Integer> getCartProductCount(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }

    /**
     * 购物车商品全部选择的接口
     */
    @ResponseBody
    @RequestMapping(value = "/cart/selection/all", method = RequestMethod.PUT)
    public ServerResponse<CartVo> selectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
    }

    /**
     * 购物车商品全部取消选择的接口
     */
    @ResponseBody
    @RequestMapping(value = "/cart/deselection/all", method = RequestMethod.PUT)
    public ServerResponse<CartVo> unSelectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByNeedLogin();
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

}
