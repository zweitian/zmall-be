package com.zmall.service;

import com.zmall.common.ServerResponse;
import com.zmall.pojo.User;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/9  16:43
 */
public interface IUserService {
     ServerResponse<User> login(String username,String password);
     ServerResponse<String> register(User user);
     ServerResponse<String> checkValid(String type,String str);
     ServerResponse<String> selectQuestion(String username) ;
     ServerResponse<String> getTokenByAnswer(String username,String question,String answer);
     ServerResponse<String> resetPassByToken(String username,String passwordNew,String forgetToken);
     ServerResponse<String> resetPassInLogin(String passwordOld,String passwordNew,User user);
     ServerResponse<User> updateInformation(User user);
     ServerResponse<User> getInfoByUserId(Integer userId);
     ServerResponse<String> checkAdminRole(User user);
}
