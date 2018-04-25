package cn.fmall.dao;

import cn.fmall.pojo.Category;

import java.util.List;

public interface CategoryMapper {

    Category selectByPrimaryKey(Integer id);

    List<Category> selectCategoryChildrenByParentId(Integer parentId);

    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);


}