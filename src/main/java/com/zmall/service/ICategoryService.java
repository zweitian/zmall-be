package com.zmall.service;

import com.zmall.common.ServerResponse;
import com.zmall.pojo.Category;
import com.zmall.pojo.User;

import java.util.List;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/10  7:43
 */
public interface ICategoryService {
    public ServerResponse<String> addCategory(String categoryName,Integer parentId);
    public ServerResponse<String> updateCategoryName(String categoryName,Integer categoryId);
    public ServerResponse<List<Category>> getParallelChildrenCategory(Integer parentId);
    public ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer parentId);
}
