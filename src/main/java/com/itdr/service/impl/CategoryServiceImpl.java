package com.itdr.service.impl;

import com.google.common.collect.Sets;
import com.itdr.common.ServerResponse;
import com.itdr.dao.CategoryMapper;
import com.itdr.pojo.Category;
import com.itdr.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServerResponse get_category(Integer categoryId) {

        //setp1: 非空校验
        if(categoryId == null || categoryId.equals("")){
            return ServerResponse.serverResponseByError("参数不能为空");
        }

        //step2: 根据categoryid查询类别
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category == null){
            return ServerResponse.serverResponseByError("查询的类别不存在");
        }

        //step3: 查询子类别

        List<Category> categoryList = categoryMapper.findByChildCategory(categoryId);

        //step4: 返回结果
        return ServerResponse.serverResponseBySuccess(categoryList);
    }

    @Override
    public ServerResponse add_category(Integer parentId, String categoryName) {

        //setp1: 参数校验
        if(parentId == null || parentId.equals("")){
            return ServerResponse.serverResponseByError("参数不能为空");
        }
        if(categoryName == null || categoryName.equals("")){
            return ServerResponse.serverResponseByError("类别名称不能为空");
        }

        //step2: 添加节点

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(1);
        int result = categoryMapper.insert(category);
        if(result > 0 ){
            return ServerResponse.serverResponseBySuccess("添加成功");
        }

        //step3: 返回结果

        return ServerResponse.serverResponseByError("添加失败");
    }

    @Override
    public ServerResponse set_category_name(Integer categoryId, String categoryName) {

        //step1:参数校验

        if(categoryId == null || categoryId.equals("")){
            return ServerResponse.serverResponseByError("参数不能为空");
        }
        if(categoryName == null || categoryName.equals("")){
            return ServerResponse.serverResponseByError("类别名称不能为空");
        }

        //step2:根据categoryId查询

        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category == null){
            return ServerResponse.serverResponseByError("要修改的类别不存在");
        }

        //step3:修改
        category.setName(categoryName);
        int result = categoryMapper.updateByPrimaryKey(category);

        //step4: 返回结果
        if(result > 0){
           return ServerResponse.serverResponseBySuccess("修改成功");
        }

        return ServerResponse.serverResponseByError("修改失败");
    }

    private Set<Category> findAllChildCategory(Set<Category> categoriesSet,Integer categoryId){

        //查找本节点
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categoriesSet.add(category);//id
        }
        //查找categoryId下的子节点(平级)
        List<Category> categoryList = categoryMapper.findByChildCategory(categoryId);
        if(categoryList != null && categoryList.size()>0){
            for (Category category1 : categoryList) {
                findAllChildCategory(categoriesSet,category1.getId());
            }
        }

        return categoriesSet;
    }

    @Override
    public ServerResponse get_deep_category(Integer categoryId) {

        //step1:参数的非空校验
        if(categoryId == null || categoryId.equals("")){
            return ServerResponse.serverResponseByError("参数不能为空");
        }

        //step2:查询

        Set<Category> categorySet = Sets.newHashSet();
        categorySet = findAllChildCategory(categorySet,categoryId);

        Set<Integer> integerSet = Sets.newHashSet();

        Iterator<Category> categoryIterator = categorySet.iterator();
        while(categoryIterator.hasNext()){
            Category category = categoryIterator.next();
            integerSet.add(category.getId());
        }

        return ServerResponse.serverResponseBySuccess(integerSet);
    }


}
