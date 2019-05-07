package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER="currentUser";
    public static final String USERNAME="username";
    public static final String EMAIL="email";

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
    public enum ProductStatusEnum {
        ON_SALE("在线",1);
        private String value;
        private int code;
        ProductStatusEnum(String value, int code){
            this.code = code;
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public int getCode(){
            return code;
        }
    }

    public interface Role{
        int ROLE_CUSTOMER=0;//普通用户
        int ROLE_ADMIN=1;//普通用户
    }

    public interface Cart{
        int CHECKED=1;//购物车中选中商品
        int NO_CHECKED=0;//购物车中未选中商品
        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
    }

}
