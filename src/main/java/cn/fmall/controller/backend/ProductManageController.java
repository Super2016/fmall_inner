package cn.fmall.controller.backend;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.Product;
import cn.fmall.pojo.User;
import cn.fmall.service.IFileService;
import cn.fmall.service.IProductService;
import cn.fmall.service.IUserService;
import cn.fmall.utils.PropertiesUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 *商品管理控制器/manage/
 */
@Controller
@RequestMapping("product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 添加或更新产品信息
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("add_or_update_product.do")
    @ResponseBody
    public ServerResponse saveProduct(HttpSession session, Product product){
        //强制用户跳转登录
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createIfError(ResponseCode.NEED_LOGIN.getCode(),"当前未登录,请登录管理员账户");
        }
        //验证管理员权限
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            //添加产品的业务逻辑
            return iProductService.addOrUpdateProduct(product);
        } else {
            return ServerResponse.createIfError("当前账户非管理员账户,无权操作");
        }

    }

    /**
     * 设置产品的当前在售状态
     * @param session
     * @param productId
     * @param productStatus
     * @return
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setProductSaleStatus(HttpSession session,Integer productId,Integer productStatus){
        //强制用户跳转登录
        User user = (User)session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createIfError(ResponseCode.NEED_LOGIN.getCode(),"当前未登录,请登录管理员账户");
        }
        //验证管理员权限
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            //设置产品在售状态的业务逻辑
            return iProductService.setProductSaleStatus(productId,productStatus);
        } else {
            return ServerResponse.createIfError("当前账户非管理员账户,无权操作");
        }
    }

    /**
     * 查找产品
     * @param session
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search_product.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session,Integer productId,String productName, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        //强制用户跳转登录
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createIfError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDescription());
        }
        //验证管理员权限
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            //查找产品的业务逻辑
            return iProductService.searchProduct(productId,productName,pageNum,pageSize);
        } else {
            return ServerResponse.createIfError("当前账户非管理员账户,无权操作");
        }
    }

    /**
     * 获取产品详细信息[管理员权限]
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("get_product_detail_info.do")
    @ResponseBody
    public ServerResponse getProductDetailInfo(HttpSession session,Integer productId){
        //强制用户跳转登录
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createIfError(ResponseCode.NEED_LOGIN.getCode(),"当前未登录,请登录管理员账户");
        }
        //验证管理员权限
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            //获取产品详情的业务逻辑
            return iProductService.manageGetProductDetailInfo(productId);
        } else {
            return ServerResponse.createIfError("当前账户非管理员账户,无权操作");
        }
    }

    /**
     * 获取产品列表[管理员权限]
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("get_product_list.do")
    @ResponseBody
    public ServerResponse getProductList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        //强制用户跳转登录
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createIfError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDescription());
        }
        //验证管理员权限
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            //后台查询列表的业务逻辑
            return iProductService.manageGetProductList(pageNum,pageSize);
        } else {
            return ServerResponse.createIfError("当前账户非管理员账户,无权操作");
        }
    }

    /**
     * 上传文件[管理员权限]
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("upload_file.do")
    @ResponseBody
    public ServerResponse uploadFile(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createIfError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"当前未登录,请登录后操作");
        }
        if (iUserService.checkIsAdmin(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName= iFileService.uploadFile(file,path);
            String url = PropertiesUtil.getValueByKey("ftp.server.http.prefix"+targetFileName);
            Map fileMap = new HashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createIfSuccess(fileMap);
        }else {
            return ServerResponse.createIfError("当前账户非管理员,无权限操作");
        }
    }

    /**
     * 上传富文本[管理员权限]
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("upload_rich_file.do")
    @ResponseBody
    public Map uploadRichTextFile(HttpSession session, @RequestParam(value = "rich_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        //这里富文本使用simditor插件,返回参数格式需要符合其要求
        Map resultMap = Maps.newHashMap();
        User user = (User)session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            resultMap.put("success",false);
            resultMap.put("msg","当前未登录,请登录管理员账户");
            return resultMap;
        }

        if (iUserService.checkIsAdmin(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFile = iFileService.uploadFile(file,path);
            if (StringUtils.isBlank(targetFile)) {
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url =  PropertiesUtil.getValueByKey("ftp.server.http.prefix"+targetFile);
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-name");
            return resultMap;
        } else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作,请登录管理员账户");
            return resultMap;
        }


    }

}
