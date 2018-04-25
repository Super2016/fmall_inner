package cn.fmall.service.impl;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.dao.CategoryMapper;
import cn.fmall.dao.ProductMapper;
import cn.fmall.pojo.Category;
import cn.fmall.pojo.Product;
import cn.fmall.service.IProductService;
import cn.fmall.utils.DateTimeUtil;
import cn.fmall.utils.PropertiesUtil;
import cn.fmall.vo.ProductDetailVo;
import cn.fmall.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品服务
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    //将Product对象装填至ProductDetailVO对象
    private ProductDetailVo assembleProductVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        //imageHost 不使用硬编码以免繁琐的修改,通过配置文件中获取
        //parentCategoryId
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setImageHost(PropertiesUtil.getValueByKey("ftp.server.http.prefix","http://img.fmall.cn/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        //如果当前分类节点为空,即无分类,则设置其父节点为0[根节点]
        if (category == null) {
            //为空,即当前所在位置为根节点,故设置为根节点
            productDetailVo.setParentCategoryId(0); //根节点
        }
        //不为空则填充Category对象值至ProductDetailVo对象
        productDetailVo.setParentCategoryId(category.getParentId());

        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToString(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToString(product.getUpdateTime()));

        return productDetailVo;

    }

    //将Product对象装填至ProductListVo对象
    private ProductListVo assembleProductVoList(Product product){
        ProductListVo productListVo = new ProductListVo();

        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setImageHost(product.getSubImages());

        return productListVo;
    }

    /**
     * 添加或更新产品信息
     * @param product
     * @return
     */
    @Override
    public ServerResponse addOrUpdateProduct(Product product) {
        if (product != null) {
            //添加产品
            //判断子图是否为空,不为空则将第一个子图赋值给主图
            if (StringUtils.isNotBlank(product.getSubImages())) {
                //使用分割符分割子图字符串
                String[] subImageArray = product.getSubImages().split(",");
                //数组长度不为空则开始设置主图
                if (subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            //修改产品
            //不为空则更新产品
            if (product.getId() != null) {
                int rowCount= productMapper.updateByPrimaryKey(product);
                if (rowCount > 0 ) {
                    return ServerResponse.createIfSuccess("已更新产品");
                }
                return ServerResponse.createIfError("未能更新产品");
            } else {
                //产品为空则添加此产品
                productMapper.insert(product);
                return ServerResponse.createIfSuccess("已添加新产品");
            }
        }
        return ServerResponse.createIfError("产品参数错误[添加或修改]");

    }

    /**
     * 设置产品的在售状态
     * @param productId
     * @param productStatus
     * @return
     */
    @Override
    public ServerResponse<String> setProductSaleStatus(Integer productId,Integer productStatus) {
        if (productId == null || productStatus ==null) {
            return ServerResponse.createIfError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        //设置产品状态值
        Product product = new Product();
        product.setId(productId);
        product.setStatus(productStatus);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createIfSuccess("已更新当前产品在售状态");
        }
        return ServerResponse.createIfError("未能更新当前产品在售状态");
    }

    /**
     * 搜索产品
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> searchProduct(Integer productId,String productName,Integer pageNum,Integer pageSize){
        //startPage开始-设置分页的开始与大小
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectProductByNameAndId(productId,productName);
        List<ProductListVo> productListVoList = Lists.newArrayList();

        for (Product productItem:productList) {
            ProductListVo productListVo = assembleProductVoList(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createIfSuccess(pageResult);
    }














    /**
     * 后台设置产品分类列表[管理员权限]
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> manageGetProductList(int pageNum,int pageSize){
        //startPage开始-设置分页的开始与大小
        PageHelper.startPage(pageNum,pageSize);
        //填充SQL查询逻辑
        List<Product> productList = productMapper.selectProductList();
        //创建ProductListVolist用于接收分页完成后的list,将list返回前端
        List<ProductListVo> productListVoList = Lists.newArrayList();
        //遍历list,将对象存入Product
        for (Product productItem : productList) {
            //使用assemble将[ProductVo]填充进[ProductListVo]
            ProductListVo productListVo = assembleProductVoList(productItem);
            //每接收到一个VO对象,将其装填进VOlist
            productListVoList.add(productListVo);
        }
        //pageHelper收尾-设置分页的结尾,传递集合,内部进行分页处理
        PageInfo pageResult = new PageInfo(productList);
        //设置要返回给前端的已分页的list
        pageResult.setList(productListVoList);

        return ServerResponse.createIfSuccess(pageResult);
    }

    /**
     * 获取产品详细信息[管理员权限]
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<Object> manageGetProductDetailInfo(Integer productId) {
        if (productId == null) {
            return ServerResponse.createIfError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createIfError("产品已下架或删除");
        }
        //VO value-Object
        ProductDetailVo detailVo = assembleProductVo(product);
        return null;
    }
}
