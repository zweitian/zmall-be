package com.zmall.service.impl;

import com.zmall.common.Const;
import com.zmall.common.ResponseCode;
import com.zmall.common.ServerResponse;
import com.zmall.common.TokenCache;
import com.zmall.dao.UserMapper;
import com.zmall.pojo.User;
import com.zmall.service.IUserService;
import com.zmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/9  16:44
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username,String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //密码MD5加密后再登录
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if(user==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //用户密码置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user) {
        //用户名校验
        ServerResponse<String> checkResult = this.checkValid(Const.CheckType.USERNAME, user.getUsername());
        //校验不成功,直接返回ServerResponse
        if(!checkResult.isSuccess()){
            return checkResult;
        }
        //email校验
        checkResult = this.checkValid(Const.CheckType.EMAIL, user.getEmail());
        //校验不成功,直接返回ServerResponse
        if(!checkResult.isSuccess()){
            return checkResult;
        }
        //上述校验都通过,用户数据入库
        //入库前用户密码进行md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //设置用户权限
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //用户数据插入数据库中
        int resultCount=userMapper.insert(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    //用户信息字段校验接口,用户名或邮箱已存在时返回false
    public ServerResponse<String> checkValid(String type,String str) {
        //任一参数为空,返回非法参数错误
        if(StringUtils.isBlank(type)||StringUtils.isBlank(str)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    "校验参数不能为空");
        }
        //非法的数据校验类型
        if(!Const.CheckType.EMAIL.equals(type)&&!Const.CheckType.USERNAME.equals(type)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    "非法的数据校验类型");
        }
        //用户名校验
        if(Const.CheckType.USERNAME.equals(type)){
            int resultCount = userMapper.checkUsername(str);
            if(resultCount>0){
                return ServerResponse.createByErrorMessage("注册的用户名已存在");
            }
        }
        //email校验
        if(Const.CheckType.EMAIL.equals(type)){
            int resultCount = userMapper.checkEmail(str);
            if(resultCount>0){
                return ServerResponse.createByErrorMessage("注册的Email已存在");
            }
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> checkResult = this.checkValid(Const.CheckType.USERNAME, username);
        //若校验成功,说明用户名不存在
        if(checkResult.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //问题为空
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isBlank(question)){
            return ServerResponse.createByErrorMessage("用户提示问题为空");
        }
        //用户问题放入data中,返回ServerResponse对象
        return ServerResponse.createBySuccess(question);
    }

    public ServerResponse<String> getTokenByAnswer(String username,String question,String answer){
        int result=userMapper.checkAnswer(username,question,answer);
        if (result>0){
            /*
                用户名,提示问题,问题答案都正确
                新建forgetToken放入缓存中
                密码重置是用到缓存中的forgetToken
            */
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            //返回data中数据为forgetToken的ServerResponse对象
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案不正确");
    }
    public ServerResponse<String> resetPassByToken(String username,String passwordNew,String forgetToken){
        //若参数为空
        if(StringUtils.isBlank(username)||StringUtils.isBlank(passwordNew)||StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByBlankArguement();
        }

        String localToken=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(forgetToken.equals(localToken)){
            //Token校验成功,可重置密码
            String md5Password=MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount=userMapper.updatePasswordByUsername(username,md5Password);
            if(resultCount>0){
                //更改数据库数据成功
                return ServerResponse.createBySuccessMessage("重置密码成功");
            }else{
                //更改数据库数据失败
                return ServerResponse.createByErrorMessage("重置密码失败,请重新尝试");
            }
        }else{
            //Token校验失败
            return ServerResponse.createByErrorMessage("token无效或已过期,请重新获取token");
        }
    }
    public ServerResponse<String> resetPassInLogin(String passwordOld,String passwordNew,User user){
        //若参数为空
        if(StringUtils.isBlank(passwordOld)||StringUtils.isBlank(passwordNew)){
            return ServerResponse.createByBlankArguement();
        }
        //校验旧密码
        int rowCount=userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if(rowCount==0){
            return ServerResponse.createByErrorMessage("旧密码输入错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        rowCount=userMapper.updateByPrimaryKeySelective(user);
        if(rowCount==0){
            return ServerResponse.createByErrorMessage("重置密码错误,请稍后再试");
        }
        return ServerResponse.createBySuccessMessage("重置密码成功");
    }
    public ServerResponse<User> updateInformation(User user){
        //更新用户数据时,校验字段非空
        Integer userId = user.getId();
        String username = user.getUsername();
        String email = user.getEmail();
        String phone = user.getPhone();
        String question = user.getQuestion();
        String answer = user.getAnswer();
        if(userId==null||StringUtils.isBlank(username)
                ||StringUtils.isBlank(email)||StringUtils.isBlank(phone)
                ||StringUtils.isBlank(question)||StringUtils.isBlank(answer)){
            return ServerResponse.createByBlankArguement();
        }
        //保证email在用户表的唯一性
        int resultCount=userMapper.checkEmailByUserId(userId,email);
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("email已存在,请更换email后再试");
        }
        //进行用户数据更新
        resultCount=userMapper.updateByPrimaryKeySelective(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("更新个人信息失败,请稍后再试");
        }
        return ServerResponse.createBySuccess("更新个人信息成功",user);
    }
    public ServerResponse<User> getInfoByUserId(Integer userId){
        //若参数为空
        if(userId==null){
            return ServerResponse.createByBlankArguement();
        }
        User user=userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMessage("获取用户信息失败,请稍后再试");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //backend 校验用户是否为管理员
    public ServerResponse<String> checkAdminRole(User user)
    {
        if(user.getRole().intValue()==Const.Role.ROLE_ADMIN)
        {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
