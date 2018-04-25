package cn.fmall.service.impl;

import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.dao.CategoryMapper;
import cn.fmall.pojo.Category;
import cn.fmall.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

/**
 * 分类管理服务
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类服务
     * @param categoryName
     * @param parentId
     * @return
     */
    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        //校验参数是否存在
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createIfError("添加品类参数错误[品类父节点or品类名]");
        }

        //将信息存入分类对象
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);   //这个分类为true可用

        //插入数据库,并确认插入成功
        int resultCount = categoryMapper.insertSelective(category);
        if (resultCount > 0) {
            return ServerResponse.createIfSuccess("已添加品类");
        }
        return ServerResponse.createIfError("未能添加品类");
    }

    /**
     * 更新品类名
     * @param categoryId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        //校验参数是否存在
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createIfError("添加品类参数错误[ID品类or品类名]");
        }

        //将信息存入对象中
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        //插入数据库,并确认插入成功
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0) {
            return ServerResponse.createIfSuccess("已更新指定品类名");
        }
        return ServerResponse.createIfError("未能更新指定品类名");
    }

    /**
     * 查找平级字节点
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        //查找所有平级子节点,并存入集合
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        //判断集合本身与其元素内容是否为空
        if (CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createIfSuccess(categoryList);
    }

    /**
     * 查询本节点与纵向关系子节点的id[使用递归]
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse selectCurrentAndChildrenCategoryById(Integer categoryId){
        //使用Guava类库创建Set,调用递归算法前初始化哈希值
        Set<Category> categorySet = Sets.newHashSet();
        //在传入哈希初始值与categoryId以开始递归查找分级子节点
        //返回了categorySet,用于遍历categoryId
        recursionFindChildCategory(categorySet,categoryId);
        //使用Guava类库创建ArrayList,新建一个categoryList
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            //遍历categorySet,将遍历的得到的对象存入categoryItem
            for (Category categoryItem : categorySet) {
                //遍历得到的category对象,将其id存入categoryIdList
                categoryIdList.add(categoryItem.getId());
            }
        }
        //返回响应数据[各纵向关系节点id列表]
        return ServerResponse.createIfSuccess(categoryIdList);
    }






//<<<<<<<<<<<<<<<<<<----内部工具部分---->>>>>>>>>>>>>>>>>>>>>
    /**
     * 递归查找纵向关系子节点
     *
     * 递归算法,需要用到Set集合；利用hashCode的排重特点,把方法本身作为[参数]算出纵向关系的子节点
     * 由于Set结构可以用于排重,如果要使用基本对象,可以使用String,String重写了hashCode与equal方法
     * 这里Set集合使用的对象需要重写hashCode与equal
     * 操作过程,将当前方法的[参数]作为[返回值]返回给方法本身,再次调用方法,将返回值传入
     *
     * @param categorySet
     * @param categoryId
     * @return
     */
    private Set<Category> recursionFindChildCategory(Set<Category> categorySet,Integer currentCategoryId){
        //获取当前节点,并添加至categorySet集合
        Category currentCategory = categoryMapper.selectByPrimaryKey(currentCategoryId);
        //当前节点为不为空则将其存入categorySet,用于递归
        if (currentCategory != null) {
            categorySet.add(currentCategory);
        }
        //查找当前节点的字节点
        //categoryMapper使用的是Mybatis返回的集合,Mybatis对集合的处理是集合对象为null则不会返回,这里不必对categoryList进行空验证
        //如果调用不可预知的方法,需要进行空判断,否则foeach循环将报空指针异常
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(currentCategoryId);

        //递归算法反复调用自身方法,使用foreach完成
        //foreach跳出条件为遍历查找子节点直至null
        //遍历categoryList的item,并存入item_currentCategory
        for (Category item_currentCategory : categoryList) {
            //调用方法自身,传入categorySet,以及当前节点,继续挖掘更深层次节点
            recursionFindChildCategory(categorySet,item_currentCategory.getId());
        }
        //返回categorySet集合
        return categorySet;
    }

}
