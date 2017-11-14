package com.zmall.service.impl;

import com.google.common.collect.Lists;
import com.zmall.common.Const;
import com.zmall.common.ResponseCode;
import com.zmall.common.ServerResponse;
import com.zmall.dao.CartMapper;
import com.zmall.dao.ProductMapper;
import com.zmall.pojo.Cart;
import com.zmall.pojo.Product;
import com.zmall.service.ICartService;
import com.zmall.util.BigDecimalUtil;
import com.zmall.util.PropertiesUtil;
import com.zmall.vo.CartProductVo;
import com.zmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/11  17:13
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService{
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;
    //购物车添加商品
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count){
        if(productId == null || count == null){
            ServerResponse.createByBlankArguement();
        }
        //查询数据库是否有要添加的商品
      /*  Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("要添加的商品不存在");
        }*/
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart == null){
            //这个产品不在这个购物车里,需要新增一个这个产品的记录
            Cart cartItem = new Cart();
            //count数量小于等于0时,赋值为1
            cartItem.setQuantity(count<=0?1:count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
            //这个产品已经在购物车里了.
            //如果产品已存在,数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //返回一个封装了data中封装了CartVo的Response对象
        return this.getResponseWithCartVo(userId);
    }
    //购物车更新商品
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            ServerResponse.createByBlankArguement();
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart != null){
            //count数量小于等于0时,赋值为1
            cart.setQuantity(count<=0?1:count);
        }
        cartMapper.updateByPrimaryKey(cart);
        return this.getResponseWithCartVo(userId);
    }
    //购物车删除商品
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        if(userId==null||productIds==null){
            return ServerResponse.createByBlankArguement();
        }
        List<String> productList = Arrays.asList(productIds.split(","));
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        return this.getResponseWithCartVo(userId);
    }

    //获取购物车中的商品
    public ServerResponse<CartVo> list (Integer userId){
        return this.getResponseWithCartVo(userId);
    }
    //购物车商品选择状态改变,productId为null时会根据参数进行全选或全不选
    public ServerResponse<CartVo> selectOrUnSelect (Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        int productCount=cartMapper.selectCartProductCount(userId);
        return ServerResponse.createBySuccess(productCount);
    }

    private ServerResponse<CartVo> getResponseWithCartVo (Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }
    //返回限制数量的购物车的方法
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //计算总价
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                //判断是否需要加入购物车总价
                if(cartItem.getChecked() == Const.Cart.CHECKED
                        &&cartProductVo.getProductTotalPrice()!=null){
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        //设置商品图片的img地址
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.img.products.http","http://img.zmall.com/products/"));
        return cartVo;
    }
    //判断购物车是否全选的方法
    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        //未选中的商品数量是否为0
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
