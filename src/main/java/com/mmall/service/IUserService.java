package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String>register(User user);
    ServerResponse<String>checkValid(String str,String type);
    ServerResponse<String>forgetPassword(String username,String newPassword,String forgetToken);
    ServerResponse<String>findQuestion(String username);
    ServerResponse<String>checkAnswer(String username,String question,String answer);
    ServerResponse<String>resetPassword(User user,String oldPassword,String newPassword);
    ServerResponse<User>getUserInfo(Integer id);
    ServerResponse<User>updateUserInfo(User user);
    ServerResponse<String> checkAdminRole(User user);
}
