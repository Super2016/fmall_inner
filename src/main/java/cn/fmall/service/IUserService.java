package cn.fmall.service;

import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.User;

import javax.servlet.http.HttpSession;

/**
 * user数据响应服务接口
 */
public interface IUserService {
    //通用数据响应对象

    //登录服务
    ServerResponse<User> login(String username, String password);

    //注册服务
    public ServerResponse<String> register(User user);

    //校验服务,用于username,email等
    public ServerResponse<String> checkValid(String str,String type);

    //忘记密码,提示问题
    public ServerResponse selectQuestion(String username);

    //检查问题答案
    public ServerResponse forgetCheckAnswer(String username,String question,String answer);

    //忘记密码情况下重置密码服务
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken);

    //登录状态下重置密码
    public ServerResponse<String> loggedResetPassword(User user,String oldPassword,String newPassword);

    //更新个人信息
    public ServerResponse<User> updateUserInfo(User user);

    //获得用户详细信息
    public ServerResponse<User> getUserDetailInfo(Integer userId);

    //校验用户是否为管理员
    public ServerResponse checkIsAdmin(User user);
}
