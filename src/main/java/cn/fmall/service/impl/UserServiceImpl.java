package cn.fmall.service.impl;

import cn.fmall.common.Constant;
import cn.fmall.common.ServerResponse;
import cn.fmall.common.TokenCache;
import cn.fmall.dao.UserMapper;
import cn.fmall.pojo.User;
import cn.fmall.service.IUserService;
import cn.fmall.utils.Md5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Response;
import java.util.UUID;

/**
 * -->dao.mapper
 * 消息服务
 * 如果是成功消息,返回响应数据与状态、消息
 * 如果是错误消息,返回响应服务状态码及消息
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录服务响应
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUserName(username);

        //检验是否存在此用户
        if (resultCount == 0) {
            return ServerResponse.createIfError("不存在此用户");
        }

        //MD5加密登录密码,不存储明文密码
        String md5Password = Md5Utils.Md5EncodeUtf8(password);

        User user= userMapper.selectLogin(username,md5Password);
        //检验密码正确性
        if (user == null) {
            return ServerResponse.createIfError("密码错误");
        }

        //处理返回值密码,将密码置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createIfSuccess("登录成功",user);
    }

    /**
     * 注册服务响应
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user){

//        int resultCount_user = userMapper.checkUserName(user.getUsername());
//        //检验是否存在此用户
//        if (resultCount_user > 0) {
//            return ServerResponse.createIfError("已存在此用户");
//        }

//        int resultCount_email = userMapper.checkEmail(user.getEmail());
//        //检验邮箱是否被注册
//        if (resultCount_email > 0) {
//            return ServerResponse.createIfError("email已被注册");
//        }

        ServerResponse validResponse = this.checkValid(user.getUsername(),Constant.USERNAME);
        //username校验未通过
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        validResponse = this.checkValid(user.getEmail(),Constant.EMAIL);
        //email校验未通过
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        //设置为普通用户组
        user.setRole(Constant.Role.ROLE_CUSTOMER);

        //将用户密码MD5加密
        user.setPassword(Md5Utils.Md5EncodeUtf8(user.getPassword()));

        int resultCount_inserted = userMapper.insert(user);
        //检验注册成功与否
        if (resultCount_inserted == 0) {
            return ServerResponse.createIfError("注册失败");
        }

        //注册成功
        return ServerResponse.createIfSuccess("注册成功");
    }

    /**
     * 忘记密码服务响应
     * 返回问题提示
     * @param username
     * @return
     */
    @Override
    public ServerResponse selectQuestion(String username){

        ServerResponse validResponse = this.checkValid(username,Constant.USERNAME);
        if (!validResponse.isSuccess()) {
            //用户不存在,返回消息提醒
            return ServerResponse.createIfError("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            //返回提示问题内容
            return ServerResponse.createIfSuccess(question);
        }

        //找回密码的问题是空的
        return ServerResponse.createIfError("未取得问题");
    }

    /**
     * 检查问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse forgetCheckAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if (resultCount > 0) {
            //问题及答案属于该用户
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createIfSuccess(forgetToken);
        }
        return ServerResponse.createIfError("答案错误");
    }

    /**
     * 忘记密码情况下重置密码
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
        //判断forgetToken是否存在
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createIfError("参数错误,未接收到Token");
        }

        //校验username,当username为空时key存在危险,可以通过空用户名取得token
        ServerResponse validResponse = this.checkValid(username,Constant.USERNAME);
        if (!validResponse.isSuccess()) {
            //用户不存在,返回消息提醒
            return ServerResponse.createIfError("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        //校验koken是否为空
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createIfError("token无效或过期");
        }
        if (StringUtils.equals(forgetToken,token)) {
            //更新密码,使用MD5加密
            String md5Password = Md5Utils.Md5EncodeUtf8(newPassword);
            //生效行数
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount > 0) {
                //返回成功消息
                return ServerResponse.createIfSuccess("已修改密码");
            }
        } else {
            return ServerResponse.createIfError("token错误,请重新获取重置密码的token");
        }
        //修改失败
        return ServerResponse.createIfError("未能修改密码");
    }

    /**
     * 登录状态下重置密码
     * 为防止横向越权,必须指定校验修改的是当前用户
     * @param oldPassword
     * @param newPassword
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> loggedResetPassword(User user,String oldPassword, String newPassword) {
        //防止横向越权,操作中会查询一个count(1),如果不指定id,结果count>0,即true
        int resultCount = userMapper.checkPassword(user.getId(),Md5Utils.Md5EncodeUtf8(oldPassword));
        if (resultCount == 0) {
            ServerResponse.createIfError("旧密码错误");
        }
        user.setPassword(Md5Utils.Md5EncodeUtf8(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createIfSuccess("密码已更新");
        }
        return ServerResponse.createIfError("未能更新密码");
    }

    /**
     * 更新个人信息
     * @param user
     * @return
     */
    @Override
    public ServerResponse<User> updateUserInfo(User user){

        //email需要进行校验
        int resultCount = userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if (resultCount > 0) {
            ServerResponse.createIfError("Email已存在");
        }

        //更新user
        //username不允许更新
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount < 0) {
            return ServerResponse.createIfSuccess("个人信息已更新",updateUser);
        }
        return ServerResponse.createIfError("未能更新个人信息");
    }

    /**
     * 获取用户详细信息
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<User> getUserDetailInfo(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createIfError("未能找到当前用户");
        }
        //密码置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createIfSuccess(user);
    }


    /**
     * 校验服务响应
     * 校验用户名与邮箱是否存在,主要防止恶意用户通过接口调用注册接口
     * str传入的值用于检验，type判断是email还是username
     * @param str 需要校验的值
     * @param type 校验的类型
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        //另有方法isNotEmpty();这里因为Empty所指的传入参数为空格也表示有值,而不表示空
        if (StringUtils.isNotBlank(type)) {
            //校验username是否存在
            if (Constant.USERNAME.equals(str)) {
                int resultCount_user = userMapper.checkUserName(str);
                if (resultCount_user > 0) {
                    return ServerResponse.createIfError("用户已存在");
                }
            }
            if (Constant.EMAIL.equals(str)) {
                int resultCount_email = userMapper.checkEmail(str);
                if (resultCount_email > 0) {
                    return ServerResponse.createIfError("email已存在");
                }
            }
        } else {
            //如果所指type的值不存在,检验未通过
            return ServerResponse.createIfError("参数错误");
        }
        return ServerResponse.createIfSuccess("校验通过");
    }


    //bakend part
    /**
     * 校验user是否为管理员
     * @param user
     * @return
     */
    @Override
    public ServerResponse checkIsAdmin(User user){
        //检验是否为管理员
        if (user != null && user.getRole().intValue() == Constant.Role.ROLE_ADMIN) {
            return ServerResponse.createIfSuccess();
        }
        return ServerResponse.createIfError();
    }
}
