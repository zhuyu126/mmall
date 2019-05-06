package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/product")
public class ProductController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;


    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user= (User) session.getAttribute( Const.CURRENT_USER);
        if(user==null){//用户为空
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录管理员进行操作");
        }
        //校验用户
        if(iUserService.checkAdminRole(user).isSuccess()){//是管理员
            //处理业务逻辑 商品添加
            return iProductService.saveOrupdateProduct(product);
        }else {
            return ServerResponse.createByErrorMessage("没有权限执行该操作");
        }
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user= (User) session.getAttribute( Const.CURRENT_USER);
        if(user==null){//用户为空
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录管理员进行操作");
        }
        //校验用户
        if(iUserService.checkAdminRole(user).isSuccess()){//是管理员
            //处理业务逻辑 商品添加
            return iProductService.setSaleStatus(productId,status);
        }else {
            return ServerResponse.createByErrorMessage("没有权限执行该操作");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getProductDetail(HttpSession session, Integer productId){
        User user= (User) session.getAttribute( Const.CURRENT_USER);
        if(user==null){//用户为空
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录管理员进行操作");
        }
        //校验用户
        if(iUserService.checkAdminRole(user).isSuccess()){//是管理员
            //处理业务逻辑 在service层填充获取商品详情逻辑
            return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("没有权限执行该操作");
        }
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getProductList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        User user= (User) session.getAttribute( Const.CURRENT_USER);
        if(user==null){//用户为空
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录管理员进行操作");
        }
        //校验用户
        if(iUserService.checkAdminRole(user).isSuccess()){//是管理员
            //处理业务逻辑 在service层填充获取产品列表
            return iProductService.getProductList(pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("没有权限执行该操作");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId ,@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        User user= (User) session.getAttribute( Const.CURRENT_USER);
        if(user==null){//用户为空
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录管理员进行操作");
        }
        //校验用户
        if(iUserService.checkAdminRole(user).isSuccess()){//是管理员
            //处理业务逻辑 在service层填充搜索
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("没有权限执行该操作");
        }
    }
}
