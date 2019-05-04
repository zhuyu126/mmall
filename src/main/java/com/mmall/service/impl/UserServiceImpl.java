package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        //service->mybatis->dao
        int resultCount=userMapper.checkUsername(username);
        //如果查询结果为0则用户名不存在
        if(resultCount==0){
            //调用泛型做的高可复用服务端响应类
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //todo 密码MD5校验
        String md5Password=MD5Utils.MD5EncodeUtf8(password);
        User user=userMapper.selectLogin(username,md5Password);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户或密码错误");
        }
        user.setPassword( StringUtils.EMPTY );
        return ServerResponse.createBySuccess("登录成功",user);
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user){
        /*
        //校验用户名是否存在
        int resultCount=userMapper.checkUsername(user.getUsername());
        //如果查询结果为0则用户名不存在
        if(resultCount>0){
            //调用泛型做的高可复用服务端响应类
            return ServerResponse.createByErrorMessage("用户已存在");
        }
        //校验email是否存在
        resultCount=userMapper.checkEmail(user.getEmail());
        //如果查询结果为0则用户名不存在
        if(resultCount>0){
            //调用泛型做的高可复用服务端响应类
            return ServerResponse.createByErrorMessage("用户已存在");
        }
        */
        //复用checkValid方法
        ServerResponse validResponse=this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){//没有校验成功则返回
            return validResponse;
        }
        validResponse=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole( Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Utils.MD5EncodeUtf8(user.getPassword()));
        int resultCount=userMapper.insert(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败！");
        }
        return ServerResponse.createBySuccess("注册成功");
    }

    /**
     * 登录校验用户名和邮箱
     * @param str
     * @param type
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if(StringUtils.isNotBlank(type)){//登录类型不为空
            if(Const.USERNAME.equals(type)){//如果type是用户名，判断用户名是否存在
                int resultCount=userMapper.checkUsername(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名存在");
                }
            }
            if(Const.EMAIL.equals(type)){//如果type是邮箱，判断邮箱是否存在
                int resultCount=userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("传入参数错误");
        }
        return ServerResponse.createBySuccess("校验成功");
    }

    /**
     * 忘记密码的重置新密码
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> forgetPassword(String username, String newPassword, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，Token需要传递");
        }
        //验证用户名
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){//校验成功则说明用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或过期");
        }
        if(StringUtils.equals(token,forgetToken)){
            String md5Password=MD5Utils.MD5EncodeUtf8(newPassword);
            int rowCount=userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount>0){
                return ServerResponse.createBySuccess("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 查找问题
     * @param username
     * @return
     */
    @Override
    public ServerResponse<String> findQuestion(String username) {
        //校验用户名
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){//校验成功则说明用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question=userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码问题为空!");
    }

    /**
     * 校验答案是否正确
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){//答案正确
            //获取一个唯一标识码作为forgetToken
            String forgetToken= UUID.randomUUID().toString();
            //将forgetToken放到本地缓存,设置有效期
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
            //返回forgetToken给客户端，下次请求的时候携带
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("答案错误");
    }

    /**
     * 重置密码
     * @param user
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(User user, String oldPassword, String newPassword) {
        //防止横向越权，要校验一下这个用户的旧密码，异地要指定是这个用户，因为会查询count（1），如果不指定id，那结果就是true count>0
        int resultCount=userMapper.checkOldPassword(user.getId(),MD5Utils.MD5EncodeUtf8(oldPassword));
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误！");
        }
        user.setPassword(MD5Utils.MD5EncodeUtf8(newPassword));
        int updateResult=userMapper.updateByPrimaryKeySelective(user);
        if(updateResult>0){
            return ServerResponse.createBySuccess("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败！");
    }

    /**
     * 查看用户信息
     * @param id
     * @return
     */
    public ServerResponse<User> getUserInfo(Integer id) {
        //service层主要用来处理返回用户的密码置空功能
        User user=userMapper.selectByPrimaryKey(id);
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Override
    public ServerResponse<User> updateUserInfo(User user) {
        //用户名是不能被更改的
        //校验新的email是否已经存在，并且不能是当前用户的email
        int resultCount=userMapper.checkEmailCurrentId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("email已经存在，请更换email");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int result=userMapper.updateByPrimaryKey(updateUser);
        if(result>0){
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新信息失败");
    }

    //校验当前用户登录是否是管理员身份
    @Override
    public ServerResponse<String> checkAdminRole(User user) {
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
