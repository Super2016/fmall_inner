package cn.fmall.controller.backend;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.User;
import cn.fmall.service.ICategoryService;
import cn.fmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 分类管理/manage/
 */
@Controller
@RequestMapping("category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加分类
     * @param session
     * @param categoryName 类品名称
     * @param parentId 父节点
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){

        //从session中获取当前对象
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        //user是否登录
        if (user == null) {
            return ServerResponse.createIfError(ResponseCode.NEED_LOGIN.getCode(),"请先登录帐号");
        }
        //校验是否是管理员
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            //校验通过则为管理员
            //添加类品
            return iCategoryService.addCategory(categoryName,parentId);
        } else {
            //校验未通过即为普通用户
            return ServerResponse.createIfError("当前普通用户无法权限操作");
        }
    }

    /**
     * 更新分类名称
     * @param session
     * @param categoryId
     * @param catergoryName
     * @return
     */
    @RequestMapping("update_category.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String catergoryName){

        //从session中获取当前对象
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        //user是否登录
        if (user == null) {
            return ServerResponse.createIfError(ResponseCode.NEED_LOGIN.getCode(),"请先登录帐号");
        }
        //校验user是否是管理员
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            //校验通过则为管理员
            //更新categoryName
            return iCategoryService.updateCategoryName(categoryId,catergoryName);
        } else {
            //校验未通过即为普通用户
            return ServerResponse.createIfError("当前普通用户无法权限操作");
        }
    }

    /**
     * 查找子类品的category信息,保持平级,不做递归查询
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_parallel_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        //从session中获取当前对象
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        //user是否登录
        if (user == null) {
            return ServerResponse.createIfError(ResponseCode.NEED_LOGIN.getCode(),"请先登录帐号");
        }
        //校验是否是管理员
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            //校验通过则为管理员
            //查找子类品的category信息,保持平级不递归
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            //校验未通过即为普通用户
            return ServerResponse.createIfError("当前普通用户无法权限操作");
        }
    }

    /**
     *
     * 递归查询纵向关系节点及子节点
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCurrentAndDeepGradeCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        //从session中获取当前对象
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        //user是否登录
        if (user == null) {
            return ServerResponse.createIfError(ResponseCode.NEED_LOGIN.getCode(),"请先登录帐号");
        }
        //校验是否是管理员
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            //rootNode-->secondaryNode-->thirdlyNode-->fourthlyNode
            //传入当前节点的id,查找当前节点的id、递归子节点的id
            return iCategoryService.selectCurrentAndChildrenCategoryById(categoryId);
        } else {
            //校验未通过即为普通用户
            return ServerResponse.createIfError("当前普通用户无法权限操作");
        }
    }
}
