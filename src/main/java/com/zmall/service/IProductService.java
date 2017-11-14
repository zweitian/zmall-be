package com.zmall.service;

import com.github.pagehelper.PageInfo;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.Product;
import com.zmall.vo.ProductDetailVo;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/10  15:09
 */
public interface IProductService {
    /*产品后台服务*/
    public ServerResponse<String> saveOrUpdateProduct(Product product);
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);
    /*产品门户服务*/
    public ServerResponse<ProductDetailVo> portalProductDetail(Integer productId);
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);

}
