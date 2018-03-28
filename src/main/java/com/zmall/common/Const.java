package com.zmall.common;

import com.google.common.collect.Sets;

import java.util.*;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2017/11/9  17:53
 */
public class Const {

    public static final String CURRENT_USER="currentUser";
    /**
        用户角色类型
    */
    public interface Role{
        int ROLE_CUSTOMER=0;
        int ROLE_ADMIN=1;
    }
    /**
        用户数据校验类型
    */
    public interface CheckType{
        String EMAIL="email";
        String USERNAME="username";
    }
    /*
          商品销售状态枚举类
     */
    public enum ProductStatusEnum{
        ON_SALE(1,"在售"),
        OFF_SALE(2,"下架"),
        DELETE(3,"已删除");
        private final int status;
        private final String desc;
        ProductStatusEnum(int status,String desc){
            this.status=status;
            this.desc=desc;
        }
        public String getDesc() {
            return desc;
        }
        public int getStatus() {
            return status;
        }
        public static boolean isDefined(int status){
            for(ProductStatusEnum productStatus:values()){
                if(productStatus.getStatus()==status){
                    return true;
                }
            }
            return false;
        }
    }
    /*
        商品排序字段set
    */
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
    /*
       购物车状态
   */
    public interface Cart{
        int CHECKED = 1;//即购物车选中状态
        int UN_CHECKED = 0;//购物车中未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }
    /*
       当面付状态
   */
    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }
    //订单状态枚举
    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");

        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("未找到订单状态对应的枚举");
        }
    }
    /*
     * 支付平台枚举
     */
    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        PayPlatformEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
    /*
      * 支付方式枚举
     */
    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");

        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
        public static PaymentTypeEnum codeOf(int code){
            for(PaymentTypeEnum paymentTypeEnum : values()){
                if(paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }
}
