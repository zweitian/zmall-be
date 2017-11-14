package com.zmall.dao;

import com.zmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String emial);

    int checkAnswer(@Param(value = "username") String username,
                    @Param(value = "question") String question, @Param(value = "answer") String answer);
    int updatePasswordByUsername(@Param(value = "username") String username, @Param(value = "passwordNew")String passwordNew);
    User selectLogin(@Param(value = "username") String username, @Param(value = "password")String password);

    String selectQuestionByUsername(@Param(value = "username") String username);

    int checkPassword(@Param(value = "userId") int userId,@Param(value = "password") String password);

    int checkEmailByUserId(@Param(value = "userId") int userId,@Param(value = "email") String email);

}