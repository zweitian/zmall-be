package com.zmall.controller.backend;

import com.zmall.common.Const;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.User;
import com.zmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/9  22:57
 */
@Controller
public class UserManageController {
    @Autowired
    private IUserService iUserService;
    /**
     * 后台管理员登录接口
     */
    @ResponseBody
    @RequestMapping(value="/backend/session/user",method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            User user = response.getData();
            if(iUserService.checkAdminRole(user).isSuccess()){
                //说明登录的是管理员
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }else{
                return ServerResponse.createByErrorMessage("非管理员用户,无法登录");
            }
        }
        return response;
    }
}