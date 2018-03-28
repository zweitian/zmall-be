package com.zmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.zmall.common.ServerResponse;
import com.zmall.service.IProductService;
import com.zmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/11  13:54
 */
@Controller
public class ProductController {
    @Autowired
    private IProductService iProductService;

    /**
     * 门户获得商品搜索服务的接口
     */
    @RequestMapping("/products/search")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }

    /**
     * 门户获得商品详细信息的接口
     */
    @ResponseBody
    @RequestMapping(value = "/products/{productId}",method = RequestMethod.GET)
    public ServerResponse<ProductDetailVo> getProductDetail(@PathVariable("productId") Integer prductId){
        return iProductService.portalProductDetail(prductId);
    }
}
