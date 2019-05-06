package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.utils.PropertiesUtil;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping(value = "/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;


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

    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user= (User) session.getAttribute( Const.CURRENT_USER);
        if(user==null){//用户为空
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录管理员进行操作");
        }
        //校验用户
        if(iUserService.checkAdminRole(user).isSuccess()){//是管理员
            //处理业务逻辑 在service层填充业务代码
            //根据servlet的上下文动态的创建一个相对路径
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap= Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }else {
            return ServerResponse.createByErrorMessage("没有权限执行该操作");
        }
    }


    @RequestMapping(value = "richtext_img_upload.do")
    @ResponseBody
    public Map richTextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap=Maps.newHashMap();
        //校验当前用户
        User user= (User) session.getAttribute( Const.CURRENT_USER);
        if(user==null){//用户为空
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }

        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }

        //校验用户
        if(iUserService.checkAdminRole(user).isSuccess()){//是管理员
            //处理业务逻辑 在service层填充业务代码
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
            }
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }
}
