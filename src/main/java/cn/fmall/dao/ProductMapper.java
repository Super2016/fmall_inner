package cn.fmall.dao;

import cn.fmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product selectByPrimaryKey(Integer id);

    List<Product> selectProductList();

    List<Product> selectProductByNameAndId(@Param("productId") Integer productId,@Param("productName") String productName);
}