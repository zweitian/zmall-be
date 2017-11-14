package com.zmall.controller.portal;

import com.zmall.common.Const;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.User;
import com.zmall.service.IUserService;
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
 * @CreateTime: 2017/11/9  15:10
 */
@Controller
public class UserController {
    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录接口
     */
    @RequestMapping(value = "/session/user",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        //登录成功,存放用户信息到session域
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 用户登出接口
     */
    @RequestMapping(value = "/session/user",method = RequestMethod.DELETE)
    @ResponseBody
    public ServerResponse<User> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }
    /**
     * 登录状态下从session获取用户登录信息接口
     */
    @RequestMapping(value = "/session/user",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfoFromSession(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户未登录,获取用户信息失败");
        }
        return ServerResponse.createBySuccess(user);
    }
    /**
     * 用户注册接口
     */
    @RequestMapping(value = "/user",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 用户数据有效性校验接口
     */
    @RequestMapping(value = "/user/validation",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> checkValid(String type,String str){
        return iUserService.checkValid(type,str);
    }

    /**
     * 获取用户提示问题接口
     */
    @RequestMapping(value = "/user/{username}/question",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> getQuestion(@PathVariable("username") String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 根据用户名,提示问题,答案获取新增forget-token接口,返回的forget-token用于用户密码重置
     */
    @RequestMapping(value = "/user/password/forget-token",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getTokenByAnswer(String username,String question,String answer){
        return iUserService.getTokenByAnswer(username,question,answer);
    }

    /**
     * 根据用户名和forget-token重置密码的接口
     */
    @RequestMapping(value = "/user/password/forget-token",method = RequestMethod.PUT)
    @ResponseBody
    public ServerResponse<String> resetPassByToken(String username,String passwordNew,String forgetToken){
        return iUserService.resetPassByToken(username,passwordNew,forgetToken);
    }

    /**
     * 登录状态下重置密码的接口,若用户未登录强制用户登录
     */
    @RequestMapping(value = "/user/password",method = RequestMethod.PUT)
    @ResponseBody
    public ServerResponse<String> resetPassInLogin(String passwordOld,String passwordNew,HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            //返回用户需要登录的服务响应对象
            return ServerResponse.createByNeedLogin();
        }
        return iUserService.resetPassInLogin(passwordOld,passwordNew,user);
    }
    /**
     * 登录状态下更新个人信息的接口,若用户未登录强制用户登录
     */
    @RequestMapping(value = "/user/information",method = RequestMethod.PUT)
    @ResponseBody
    public ServerResponse<User> updateInformation(User user,HttpSession session){
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            //返回用户需要登录的服务响应对象
            return ServerResponse.createByNeedLogin();
        }
        //id与username不能被更新
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        //更新个人信息成功,更新session域里当前用户信息
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
    /**
     * 登录状态下从数据库获取最新用户信息接口,若用户未登录强制用户登录
     */
    @RequestMapping(value = "/user",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfoFromDB(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByNeedLogin();
        }
        return iUserService.getInfoByUserId(user.getId());
    }
}
