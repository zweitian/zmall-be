package com.zmall.controller.backend;

import com.zmall.common.Const;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.Category;
import com.zmall.pojo.User;
import com.zmall.service.ICategoryService;
import com.zmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/10  7:19
 */
@Controller
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryservice;

    /**
     * 后台添加商品品类接口
     */
    @RequestMapping(value = "/backend/category",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addCatergory(HttpSession session, String categoryName, @RequestParam(value ="parentId",defaultValue = "0") int parentId)
    {
        //判断用户是否已登录
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByNeedLogin();
        }
        //判断用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            return iCategoryservice.addCategory(categoryName,parentId);
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限进行添加品类操作");
    }

    /**
     * 后台更新商品品类名称接口
     */
    @RequestMapping(value = "/backend/category/name",method = RequestMethod.PUT)
    @ResponseBody
    public ServerResponse<String> updateCategoryName(HttpSession session, String categoryName,int categoryId)
    {
        //判断用户是否已登录
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByNeedLogin();
        }
        //判断用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            return iCategoryservice.updateCategoryName(categoryName,categoryId);
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限进行品类更新操作");
    }
    /**
     * 后台根据categoryId获取下一级商品品类接口接口
     */
    @RequestMapping(value = "/backend/category/parallel-children",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getParallelChildrenCategory(HttpSession session, @RequestParam(value = "categoryId" ,defaultValue = "0") int categoryId)
    {
        //判断用户是否已登录
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByNeedLogin();
        }
        //判断用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            return iCategoryservice.getParallelChildrenCategory(categoryId);
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限进行品类更新操作");
    }
    /**
     * 后台根据categoryId获取商品品类和该商品品类的所有子孙品类的接口
     */
    @RequestMapping(value = "/backend/category/all",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndChildren(HttpSession session, @RequestParam(value = "categoryId" ,defaultValue = "0") int categoryId)
    {
        //判断用户是否已登录
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByNeedLogin();
        }
        //判断用户是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            return iCategoryservice.getCategoryAndChildrenById(categoryId);
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限进行品类更新操作");
    }
}
