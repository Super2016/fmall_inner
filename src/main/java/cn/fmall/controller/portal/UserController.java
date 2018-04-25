package cn.fmall.controller.portal;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.User;
import cn.fmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * -->service^interface-->dao.mapper
 * 前台门户控制器
 * 控制器调用ServerResponse服务
 */

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return user响应对象
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    //@ResponseBody注解表示将返回值序列化为json
    public ServerResponse<User> login(String username, String password, HttpSession session){

        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()) {
            session.setAttribute(Constant.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 用户登出
     * 删除session的当前user
     * @param session
     * @return 状态码
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        //登出即将session参数删除
        session.removeAttribute(Constant.CURRENT_USER);
        return ServerResponse.createIfSuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 校验用户名与邮箱是否存在
     * 防止恶意用户通过接口调用注册接口
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取用户登录信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Constant.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createIfSuccess(user);
        } else {
            return ServerResponse.createIfError("无法获取当前用户信息,登录后重试");
        }
    }

    /**
     * 获取用户详细信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_detail_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserDetailInfo(HttpSession session){
        User currentUser = (User)session.getAttribute(Constant.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createIfError(ResponseCode.NEED_LOGIN.getCode(),"需要强制登录[status=10]");
        }
        //返回用户详细信息
        return iUserService.getUserDetailInfo(currentUser.getId());
    }

    /**
     * 忘记密码
     * 返回密码提示问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetPassword(String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 检查用户答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.forgetCheckAnswer(username,question,answer);
    }

    /**
     * 忘记密码情况下重置密码
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
        return iUserService.forgetResetPassword(username,newPassword,forgetToken);
    }

    /**
     * 登录状态下重置密码
     * @param session
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "logged_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logedResetPassword(HttpSession session,String oldPassword,String newPassword){
        //校验用户是否存在
        User user = (User)session.getAttribute(Constant.USERNAME);
        if (user == null) {
            ServerResponse.createIfError("用户未登录");
        }
        return iUserService.loggedResetPassword(user,oldPassword,newPassword);
    }

    /**
     * 更新个人信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "update_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session,User user){
        //校验用户是否存在
        User currentUser = (User)session.getAttribute(Constant.USERNAME);
        if (currentUser == null) {
            return ServerResponse.createIfError("用户未登录");
        }

        //接收的user对象中无id,这里需要通过currentUser传入id
        //泛型中USer将存入session中,需要设置userName,否则存入session时将少了userName
        //为防止横向越权,这里设置的userId与userName是从session中获取当前登录的userId与
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = ServerResponse.createIfSuccess(user);
        if (response.isSuccess()) {
            //更新session
            session.setAttribute(Constant.CURRENT_USER,response.getData());
        }
        return response;

    }
}
