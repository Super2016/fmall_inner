package cn.fmall.dao;

import cn.fmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User selectByPrimaryKey(Integer id);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);

    int checkUserName(String username);

    int checkPassword(@Param("userId") Integer userId,@Param("password")String password);

    int checkEmail(String email);

    int checkEmailByUserId(@Param("userId") Integer userId,@Param("email") String email);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int updatePasswordByUsername(@Param("username") String username,@Param("password") String password);

}