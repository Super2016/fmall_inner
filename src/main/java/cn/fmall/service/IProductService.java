package cn.fmall.service;

import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.Product;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

/**
 * 产品服务接口
 */
public interface IProductService {

    //添加产品
    public ServerResponse addOrUpdateProduct(Product product);

    //设置产品的在售状态
    public ServerResponse<String> setProductSaleStatus(Integer productId,Integer roductStatus);

    //获取产品详情[管理员权限]
    public ServerResponse<Object> manageGetProductDetailInfo(Integer productId);

    //后台产品分页列表[管理员权限]
    public ServerResponse<PageInfo> manageGetProductList(int pageNum, int pageSize);

    //搜索产品
    public ServerResponse<PageInfo> searchProduct(Integer productId,String productName,Integer pageNum,Integer pageSize);
}
