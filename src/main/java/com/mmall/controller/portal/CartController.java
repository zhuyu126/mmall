package com.mmall.controller.portal;

import com.google.common.base.Splitter;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session){

        User user= (User) session.getAttribute( Const.CURRENT_USER );
        if(user==null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc() );
        }
        //业务逻辑
        return iCartService.list(user.getId());
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){

        User user= (User) session.getAttribute( Const.CURRENT_USER );
        if(user==null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc() );
        }
        //业务逻辑
        return iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){

        User user= (User) session.getAttribute( Const.CURRENT_USER );
        if(user==null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc() );
        }
        //业务逻辑
        return iCartService.update(user.getId(),productId,count);
    }
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session, String productIds) {
        User user= (User) session.getAttribute( Const.CURRENT_USER );
        if(user==null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    //全选
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session) {
        User user= (User) session.getAttribute( Const.CURRENT_USER );
        if(user==null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    //全反选
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session) {
        User user= (User) session.getAttribute( Const.CURRENT_USER );
        if(user==null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.NO_CHECKED);
    }

    //单独选
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session,Integer productId) {
        User user= (User) session.getAttribute( Const.CURRENT_USER );
        if(user==null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    //单独反选
    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId) {
        User user= (User) session.getAttribute( Const.CURRENT_USER );
        if(user==null){
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc() );
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.NO_CHECKED);
    }

    //查询当前用户的购物车里面的产品数量，如果某一个产品有10个，那么数量就是10
    @RequestMapping("get_cart_product.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProduct(HttpSession session) {
        User user= (User) session.getAttribute( Const.CURRENT_USER );
        if(user==null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }


}
