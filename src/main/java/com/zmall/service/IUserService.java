package com.zmall.service;

import com.zmall.common.ServerResponse;
import com.zmall.pojo.User;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/9  16:43
 */
public interface IUserService {
    public ServerResponse<User> login(String username,String password);
    public ServerResponse<String> register(User user);
    public ServerResponse<String> checkValid(String type,String str);
    public ServerResponse<String> selectQuestion(String username) ;
    public ServerResponse<String> getTokenByAnswer(String username,String question,String answer);
    public ServerResponse<String> resetPassByToken(String username,String passwordNew,String forgetToken);
    public ServerResponse<String> resetPassInLogin(String passwordOld,String passwordNew,User user);
    public ServerResponse<User> updateInformation(User user);
    public ServerResponse<User> getInfoByUserId(Integer userId);
    public ServerResponse<String> checkAdminRole(User user);
}
