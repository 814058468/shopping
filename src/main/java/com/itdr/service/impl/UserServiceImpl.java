package com.itdr.service.impl;

import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.dao.UserInfoMapper;
import com.itdr.pojo.UserInfo;
import com.itdr.service.IUserService;
import com.itdr.utils.MD5Utils;
import com.itdr.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UserServiceImpl implements IUserService {

    @Autowired//依赖注入
    UserInfoMapper userInfoMapper;

    @Override
    public ServerResponse login(String username, String password) {

        //step1: 非空校验

        if(username == null || username.equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }

        if(password == null || password.equals("")){
            return ServerResponse.serverResponseByError("密码不能为空");
        }

        //step2: 检查用户名是否存在

        int result = userInfoMapper.checkUsername(username);
        if(result == 0){
            return ServerResponse.serverResponseByError("用户名不存在");
        }

        //step3: 根据用户名和密码找用词信息
        UserInfo userInfo= userInfoMapper.selectUserInfoByUsernameAndPassword(username,MD5Utils.getMD5Code(password));
        if(userInfo == null){
            return ServerResponse.serverResponseByError("密码错误");
        }

        //step4: 返回结果

        userInfo.setPassword("");

        return ServerResponse.serverResponseBySuccess(userInfo);
    }

    @Override
    public ServerResponse register(UserInfo userInfo) {


        // step1: 非空校验

        if(userInfo == null){
            return ServerResponse.serverResponseByError("参数必须");
        }

        // step2: 校验用户名

//        int result = userInfoMapper.checkUsername(userInfo.getUsername());
////        if(result > 0){
////            return ServerResponse.serverResponseByError("用户名已存在");
////        }
        ServerResponse serverResponse = check_valid(userInfo.getUsername(),Const.USERNAME);
        if(!serverResponse.isSuccess()){
            return ServerResponse.serverResponseByError("用户名已存在");
        }

        // step3: 校验邮箱

//        int result_email = userInfoMapper.checkEmail(userInfo.getEmail());
//        if(result_email > 0){
//            return ServerResponse.serverResponseByError("邮箱已存在");
//        }
        ServerResponse serverResponse1 = check_valid(userInfo.getEmail(),Const.EMAIL);
        if(!serverResponse1.isSuccess()){
            return ServerResponse.serverResponseByError("邮箱已存在");
        }

        // step4: 注册

        userInfo.setRole(Const.RoleEnum.ROLE_CUSTOMER.getCode());
        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));

        int count = userInfoMapper.insert(userInfo);
        if(count > 0){
            return ServerResponse.serverResponseBySuccess("注册成功");
        }

        // setp5: 返回结果

        return ServerResponse.serverResponseByError("注册失败");

    }

    @Override
    public ServerResponse forget_get_question(String username) {

        //step1：参数非空校验

        if(username == null || username.equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }

        //step2：校验username

        int result = userInfoMapper.checkUsername(username);
        if(result == 0){
            return ServerResponse.serverResponseByError("用户名不存在");
        }

        //step3：查找密保问题

        String question = userInfoMapper.selectQuestionByUsername(username);
        if(question == null || question.equals("")){
            return ServerResponse.serverResponseByError("密保问题为空");
        }

        return ServerResponse.serverResponseBySuccess(question);
    }

    @Override
    public ServerResponse forget_check_answer(String username, String question, String answer) {

        //step1：参数非空校验

        if(username == null || username.equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }

        if(question == null || question.equals("")){
            return ServerResponse.serverResponseByError("问题不能为空");
        }

        if(answer == null || answer.equals("")){
            return ServerResponse.serverResponseByError("答案不能为空");
        }

        //step2：根据username,question,answer查询

        int result = userInfoMapper.selectUsernameAndQuestionAndAnswer(username,question,answer);
        if(result == 0){
            return ServerResponse.serverResponseByError("答案错误");
        }

        //step3：服务端生成一个token保存，并将token返回客户端

        String forgetToken = UUID.randomUUID().toString();//生成唯一的字符串

        //guava cache 做缓存


        TokenCache.set(username,forgetToken);


        return ServerResponse.serverResponseBySuccess(forgetToken);
    }

    @Override
    public ServerResponse forget_reset_password(String username, String password, String forgetToken) {

        //step1:参数校验

        if(username == null || username.equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }

        if(password == null || password.equals("")){
            return ServerResponse.serverResponseByError("密码不能为空");
        }

        if(forgetToken == null || forgetToken.equals("")){
            return ServerResponse.serverResponseByError("forgetToken不能为空");
        }

        //step2:forgetToken校验

        String token = TokenCache.get(username);
        if(token == null){
            return ServerResponse.serverResponseByError("token过期");
        }
        if(!token.equals(forgetToken)){
            return ServerResponse.serverResponseByError("无效的token");
        }

        //step3:修改密码



        int result = userInfoMapper.updateUserPassword(username,MD5Utils.getMD5Code(password));
        if(result>0){
            return ServerResponse.serverResponseBySuccess();
        }

        return ServerResponse.serverResponseByError("密码修改失败");

    }

    @Override
    public ServerResponse check_valid(String str, String type) {

        //step1: 非空校验
        if(str == null || str.equals("")){
            return ServerResponse.serverResponseByError("参数为空1");
        }

        if(type == null || type.equals("")){
            return ServerResponse.serverResponseByError("参数为空2");
        }

        //step2:校验类型是用户名还是邮箱
        if(type.equals(Const.USERNAME)){

            int username_result = userInfoMapper.checkUsername(str);
            if(username_result>0){
                return ServerResponse.serverResponseByError("用户名已经存在");
            }

        }

        if(type.equals(Const.EMAIL)){

            int email_result = userInfoMapper.checkEmail(str);
            if(email_result > 0){
                return ServerResponse.serverResponseByError("邮箱已经存在");
            }

        }

        //step3:

        return ServerResponse.serverResponseBySuccess("校验成功");
    }

    @Override
    public ServerResponse reset_password(String passwordOld, String passwordNew, UserInfo userInfo) {

        //step1:非空校验

        if(passwordOld == null || passwordOld.equals("")){
            return ServerResponse.serverResponseByError("旧密码不能为空");
        }

        if(passwordNew == null || passwordNew.equals("")){
            return ServerResponse.serverResponseByError("新密码不能为空");
        }

        //step2:密码校验

        int result = userInfoMapper.selectCountByUserIdAndPassword(userInfo.getId(),MD5Utils.getMD5Code(passwordOld));
        if(result == 0){
            return ServerResponse.serverResponseByError("密码错误");
        }

        //step3: 更新密码
        userInfo.setPassword(MD5Utils.getMD5Code(passwordNew));
        int update_result = userInfoMapper.updateBySelectActive(userInfo);
        if(update_result == 0){
            return ServerResponse.serverResponseByError("密码更新失败");
        }

        return ServerResponse.serverResponseBySuccess("密码更新成功");
    }

    @Override
    public ServerResponse update_information(UserInfo userInfo) {

        //setp1: 校验邮箱是否存在：userid email

        int result = userInfoMapper.checkEmailByUserId(userInfo.getId(),userInfo.getEmail());
        if(result > 0 ){
            return ServerResponse.serverResponseByError("邮箱已存在");
        }

        //setp2:更新用户信息

        int update_result = userInfoMapper.updateBySelectActive(userInfo);
        if(update_result == 0){
            return ServerResponse.serverResponseByError("更新失败");
        }

        return ServerResponse.serverResponseBySuccess("更新成功");
    }

    @Override
    public UserInfo findUserInfoByUserid(int id) {

        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(id);
        return userInfo;
    }


}
