package cn.fmall.common;

/**
 * 自定义常量类
 */
public class Constant {
    public static final String CURRENT_USER = "currentUser";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    //将常量分组,用户组
    public interface Role{
        int ROLE_CUSTOMER = 0; // common user
        int ROLE_ADMIN = 1; //administrator
    }
}
