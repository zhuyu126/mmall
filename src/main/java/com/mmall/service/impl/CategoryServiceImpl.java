package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger= LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;


    public ServerResponse addCategory(String categoryName,Integer parentId){
        //参数校验
        if(parentId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数信息错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount=categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccess("商品品类添加成功!");
        }
        return ServerResponse.createByErrorMessage("商品品类添加失败!");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        //参数校验
        if(categoryId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数信息错误");
        }
        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount=categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount>0){
            return ServerResponse.createBySuccess("商品品类名称修改成功");
        }
        return ServerResponse.createByErrorMessage("商品品类名称修改失败!");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category>categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            //是空的返回日志信息
            logger.info("该类没有子类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询该节点id及孩子节点id
     * @param categoryId
     * @return
     */
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet= Sets.newHashSet();
        findChildCategory(categorySet,categoryId);

        List<Integer>categoryIdList= Lists.newArrayList();
        if(categoryId!=null){
            for(Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归算法，自己调自己，算出子节点
    private Set<Category>findChildCategory(Set<Category>categorySet,Integer categoryId){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        //查找子节点，递归算法必须有退出条件
        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem:categoryList){
            findChildCategory(categorySet,categoryItem.getId());//categoryItem.getId()子节点id
        }
        return categorySet;
    }
}
