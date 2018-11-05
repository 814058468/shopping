package com.itdr.controller.backend;

import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.pojo.UserInfo;
import com.itdr.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 后台用户控制器
 */
@RestController//返回json
@RequestMapping(value = "/mana/user")
public class UsermanagerController {

    @Autowired
    IUserService userService;

    /**
     * 管理员登录
     */
    @RequestMapping(value = "/login.do")
    public ServerResponse login(HttpSession session , String username,String password){

        ServerResponse serverResponse = userService.login(username,password);
        if(serverResponse.isSuccess()){
            UserInfo userInfo = (UserInfo)serverResponse.getDate();
            if(userInfo.getRole() == Const.RoleEnum.ROLE_CUSTOMER.getCode()){
                return ServerResponse.serverResponseByError("无权登录");
            }
            session.setAttribute(Const.CURRENTUSER,userInfo);
        }
        return serverResponse;

    }
}
