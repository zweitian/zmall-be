package com.zmall.controller.backend;

import com.google.common.collect.Maps;
import com.zmall.common.Const;
import com.zmall.common.ResponseCode;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.Category;
import com.zmall.pojo.Product;
import com.zmall.pojo.User;
import com.zmall.service.ICategoryService;
import com.zmall.service.IFtpService;
import com.zmall.service.IProductService;
import com.zmall.service.IUserService;
import com.zmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/10  7:19
 */
@Controller
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFtpService iFtpService;
    /**
     * 后台根据是否传入商品id执行商品更新/添加的接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/products",method = RequestMethod.POST)
    public ServerResponse<String> saveOrUpdateProduct(Product product, HttpSession session)
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
            //调用服务层更新或新增产品
            return iProductService.saveOrUpdateProduct(product);
        }
        return ServerResponse.createByErrorMessage("非管理员用户,无权限进行操作");
    }
    /**
     * 后台更新商品上下架状态接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/products/{productId}/status",method = RequestMethod.PUT)
    public ServerResponse setSaleStatus(HttpSession session, @PathVariable("productId") Integer productId,Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByNeedLogin();
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            //return iProductService.setSaleStatus(productId,status);
            return ServerResponse.createByErrorMessage("非管理员用户,无权限进行操作");
        }
        return iProductService.setSaleStatus(productId,status);
    }

    /**
     * 后台根据商品id获取商品详细信息的接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/products/{productId}",method = RequestMethod.GET)
    public ServerResponse getDetail(HttpSession session,@PathVariable("productId") Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByNeedLogin();
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户,无权限进行操作");
        }
    }
    /**
     * 后台查询商品列表的接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/products",method = RequestMethod.GET)
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByNeedLogin();
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户,无权限进行操作");
        }
    }

    /**
     * 后台根据条件查询商品列表的接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/products/search",method = RequestMethod.GET)
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId,
                                        @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByNeedLogin();
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("非管理员用户,无权限进行操作");
        }
    }

    /**
     * 后台上传商品图片到FTP服务器的接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/ftpserver/products/img",method = RequestMethod.POST)
    public ServerResponse upload(HttpSession session,HttpServletRequest request,
                                 @RequestParam(value = "upload_file",required = false)MultipartFile file)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByNeedLogin();
        }
        //管理员权限检测
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("非管理员用户,无权限进行操作");
        }
        //文件上传流程->先上传到本地tomcat服务器->上传到ftp服务器
        //获取本地上传文件路径
        String localPath = request.getSession().getServletContext().getRealPath("upload");
        //文件上传到Ftp服务器路径
        String ftpPath = PropertiesUtil.getProperty("ftp.img.products.path","img/products");
        //调用Ftp上传服务上传文件到Ftp服务器,返回上传文件名
        String targetFileName = iFtpService.upload(file,localPath,ftpPath);
        //若文件上传失败,targetFileName为null
        if(targetFileName==null){
            return ServerResponse.createByErrorMessage("文件上传失败,请检测FTP服务器状态");
        }
        //拼接uri、url地址
        String url = PropertiesUtil.getProperty("ftp.server.img.products.http","http://img.zmall.com/products/")
                    +targetFileName;
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri",targetFileName);
        fileMap.put("url",url);
        return ServerResponse.createBySuccess(fileMap);
    }
    /**
     * 后台上传商品富文本图片到FTP服务器的接口
     */
    @ResponseBody
    @RequestMapping(value = "/backend/ftpserver/products/rich-img",method = RequestMethod.POST)
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //管理员权限检测
        if(!iUserService.checkAdminRole(user).isSuccess()){
            resultMap.put("success",false);
            resultMap.put("msg","非管理员用户,无权限进行操作");
            return resultMap;
        }
        //simditor上传结果返回格式要求
        /*
        {
            "success": true/false,
                "msg": "error message", # optional
            "file_path": "[real file path]"
        }
        */
        String localPath = request.getSession().getServletContext().getRealPath("upload");
        //文件上传到Ftp服务器路径
        String ftpPath = PropertiesUtil.getProperty("ftp.img.products.path","img/products");
        //调用Ftp上传服务上传文件到Ftp服务器,返回上传文件名
        String targetFileName = iFtpService.upload(file,localPath,ftpPath);
        if(StringUtils.isBlank(targetFileName)){
            resultMap.put("success",false);
            resultMap.put("msg","上传失败");
            return resultMap;
        }
        String url = PropertiesUtil.getProperty("ftp.server.img.products.http","http://img.zmall.com/products/")
                        +targetFileName;
        resultMap.put("success",true);
        resultMap.put("msg","上传成功");
        resultMap.put("file_path",url);
        response.addHeader("Access-Control-Allow-Headers","X-File-Name");
        return resultMap;
    }

}
