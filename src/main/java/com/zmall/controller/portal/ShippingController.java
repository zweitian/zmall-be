package com.zmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.zmall.common.Const;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.Shipping;
import com.zmall.pojo.User;
import com.zmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/11  20:49
 */
@Controller
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;
    /**
     * 新增收货地址的接口
     */
    @ResponseBody
    @RequestMapping(value = "/shippings",method = RequestMethod.POST)
    public ServerResponse<Map> add(HttpSession session, Shipping shipping){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        return iShippingService.add(user.getId(),shipping);
    }
    /**
     * 删除收货地址的接口
     */
    @ResponseBody
    @RequestMapping(value = "/shippings",method = RequestMethod.DELETE)
    public ServerResponse del(HttpSession session,Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        return iShippingService.del(user.getId(),shippingId);
    }
    /**
     * 更新收货地址的接口
     */
    @ResponseBody
    @RequestMapping(value = "/shippings",method = RequestMethod.PUT)
    public ServerResponse update(HttpSession session,Shipping shipping){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        return iShippingService.update(user.getId(),shipping);
    }
    /**
     * 获取收货地址详情信息的接口
     */
    @ResponseBody
    @RequestMapping(value = "/shippings/{shippingId}",method = RequestMethod.GET)
    public ServerResponse<Shipping> detail(HttpSession session,@PathVariable("shippingId") Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        return iShippingService.select(user.getId(),shippingId);
    }
    /**
     * 获取收货地址列表的接口
     */
    @ResponseBody
    @RequestMapping(value = "/shippings",method = RequestMethod.GET)
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                         HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByNeedLogin();
        }
        return iShippingService.list(user.getId(),pageNum,pageSize);
    }
}
