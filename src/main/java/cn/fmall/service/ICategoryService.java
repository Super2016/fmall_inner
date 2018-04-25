package cn.fmall.service;

import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.Category;

import java.util.List;

/**
 * 分类管理服务接口
 */
public interface ICategoryService {

    //添加品类服务
    public ServerResponse addCategory(String categoryName,Integer parentId);

    //更新categoryName服务
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    //查询平级子节点服务
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    //查询当前节点与分级子节点的id
    public ServerResponse selectCurrentAndChildrenCategoryById(Integer categoryId);
}
