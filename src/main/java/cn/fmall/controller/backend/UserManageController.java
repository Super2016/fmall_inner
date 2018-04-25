package cn.fmall.controller.backend;

import cn.fmall.common.Constant;
import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.User;
import cn.fmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 后台控制器/manage/
 */

@Controller
@RequestMapping("superuser")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param session
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(HttpSession session,String username,String password){
        ServerResponse<User> response = iUserService.login(username,password);
        //如果servic请求成功
        if (response.isSuccess()) {
            User currentUser = response.getData();
            //检验登录的是管理员
            if (currentUser.getRole() == Constant.Role.ROLE_ADMIN) {
                session.setAttribute(Constant.CURRENT_USER,currentUser);
                return response;
            } else {
                ServerResponse.createIfError("非管理员,无法登录");
            }
        }
        //如果service请求错误
        return response;
    }



}
