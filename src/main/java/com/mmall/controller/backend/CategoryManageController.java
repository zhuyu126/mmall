package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;


    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        //用户判断
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){//用户为空
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录状态使10");
        }
        //校验用户
        if(iUserService.checkAdminRole(user).isSuccess()){//是管理员
            //处理业务逻辑
            //增加分类信息
            return iCategoryService.addCategory(categoryName,parentId);

        }else {
            return ServerResponse.createByErrorMessage("没有权限执行该操作");
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse updateCategory(HttpSession session,Integer categoryId,String categoryName) {
        //用户判断
        User user = (User) session.getAttribute( Const.CURRENT_USER );
        if (user == null) {//用户为空
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(), "未登录，需要强制登录状态使10" );
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {//是管理员
            //处理业务逻辑
            //增加分类信息
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else {
            return ServerResponse.createByErrorMessage("没有权限执行该操作");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId) {
        //用户判断
        User user = (User) session.getAttribute( Const.CURRENT_USER );
        if (user == null) {//用户为空
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(), "未登录，需要强制登录状态使10" );
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {//是管理员
            //处理业务逻辑 查询子节点的category信息，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("没有权限执行该操作");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId) {
        //用户判断
        User user = (User) session.getAttribute( Const.CURRENT_USER );
        if (user == null) {//用户为空
            return ServerResponse.createByErrorCodeMessage( ResponseCode.NEED_LOGIN.getCode(), "未登录，需要强制登录状态使10" );
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {//是管理员
            //处理业务逻辑 查询当前节点和递归子节点的id信息 0->1000->100000
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("没有权限执行该操作");
        }
    }
}
