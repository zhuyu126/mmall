package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    //@PostMapping(value = "login.do")
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        //service->mybatis->dao
        ServerResponse<User>response=iUserService.login(username,password);
        //如果登录成功，把User数据放到session里
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 用户登出
     * @param session
     * @return
     */
    //@GetMapping(value = "logout.do")
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    //@PostMapping(value = "register.do")
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>register(User user){
        return iUserService.register(user);
    }

    /**
     * 校验email和username
     * @param str
     * @param type
     * @return
     */
    //@PostMapping(value = "check_valid.do")
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>checkValid(String str,String type){
        return iUserService.checkValid(str, type);
    }

    /**
     * 忘记密码时找回密码的问题
     * @param username
     * @return
     */
    //@GetMapping(value = "forget_get_question.do")
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>findQuestion(String username){
        return iUserService.findQuestion(username);
    }

    /**
     * 检查问题答案是否正确
     * @param username
     * @param question
     * @param answer
     * @return
     */
    //@GetMapping(value = "forget_check_answer.do")
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    //放入token
    public ServerResponse<String>checkAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 查看Session中获取的用户登录信息
     * @param session
     * @return
     */
    //@GetMapping(value = "get_user_info.do")
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInfo(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser != null){
            return ServerResponse.createBySuccess(currentUser);
        }
        return ServerResponse.createByErrorMessage("用户未登录无法获取信息");
    }

    //@PostMapping(value = "update_information.do")
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录，无法修改");
            //return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录状态使10");
        }
        //为了防止横向越权，将更改用户信息的id设置成和当前用一样的id
        user.setId(currentUser.getId());
        //限制用户名不可以修改
        user.setUsername(currentUser.getUsername());
        ServerResponse<User>response=iUserService.updateUserInfo(user);
        if(response.isSuccess()){
            response.getData().setUsername(user.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 查看Session中获取的用户登录信息
     * @param session
     * @return
     */
    //@GetMapping(value = "get_information.do")
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录状态使10");
        }
        return iUserService.getUserInfo(currentUser.getId());
    }

    /**
     * 忘记密码下的重置密码
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    //@PostMapping(value = "forget_reset_password.do")
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetPassword(String username,String newPassword,String forgetToken){
        return iUserService.forgetPassword(username,newPassword,forgetToken);
    }

    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>resetPassword(HttpSession session,String oldPassword,String newPassword){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(user,oldPassword,newPassword);
    }
}
