package com.itdr.controller.portal;

import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.pojo.UserInfo;
import com.itdr.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController //使它的返回值是json格式
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    IUserService userService;
    /*
        登录
     */
    @RequestMapping(value = "/login.do")
    public ServerResponse login(HttpSession session,
                                @RequestParam("username") String username,
                                @RequestParam("password") String password){
        /*
            Springmvc怎么接参数 ,
            required = false(定义之后可不传) ,
            defaultValue = "zhangsan"（默认值为"zhangsan"）
            @RequestParam什么时候可以不写  vllue 的值和形参保持一致
            @RequestParam(value = "username" )
            @RequestParam("password")
         */

        ServerResponse serverResponse = userService.login(username,password);
        if(serverResponse.isSuccess()){//登录成功
            UserInfo userInfo = (UserInfo) serverResponse.getDate();
            session.setAttribute(Const.CURRENTUSER,userInfo);

        }

        return serverResponse;
    }

    /*
        注册
     */
    @RequestMapping(value = "/register.do")
    public ServerResponse register(HttpSession session, UserInfo userInfo){

        ServerResponse serverResponse = userService.register(userInfo);

        return serverResponse;
    }

    /*
       根据用户名查询密保问题
    */
    @RequestMapping(value = "/forget_get_question.do")
    public ServerResponse forget_get_question(String username){

        ServerResponse serverResponse = userService.forget_get_question(username);

        return serverResponse;
    }

    /*
        提交问题答案
     */
    @RequestMapping(value = "/forget_check_answer.do")
    public ServerResponse forget_check_answer(String username , String question , String answer){

        ServerResponse serverResponse = userService.forget_check_answer(username,question,answer);

        return serverResponse;
    }

    /*
        重置密码
     */
    @RequestMapping(value = "/forget_reset_password.do")
    public ServerResponse forget_reset_password(String username , String password , String forgetToken){

        ServerResponse serverResponse = userService.forget_reset_password(username,password,forgetToken);
        return serverResponse;

    }

    /*
        检查用户名或者邮箱是否有效
     */
    @RequestMapping(value = "/check_valid.do")
    public ServerResponse check_valid(String str,String type){

        ServerResponse serverResponse = userService.check_valid(str,type);

        return serverResponse;
    }

    /*
        获取登录状态下的用户信息
     */
    @RequestMapping(value = "/get_user_info.do")
    public ServerResponse get_user_info(HttpSession session){
        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo == null){
            return ServerResponse.serverResponseByError("用户未登录或者已过期");
        }
        userInfo.setPassword("");
        return ServerResponse.serverResponseBySuccess(userInfo,"成功");
    }

    /*
        登录状态下重置密码
     */
    @RequestMapping(value = "/reset_password.do")
    public ServerResponse reset_password(String passwordOld , String passwordNew , HttpSession session){

        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo == null || userInfo.equals("")){
            return ServerResponse.serverResponseByError("用户需要登录");
        }
        ServerResponse serverResponse = userService.reset_password(passwordOld,passwordNew,userInfo);
        return serverResponse;
    }

    /*
        登录状态下更新个人信息
     */
    @RequestMapping(value = "/update_information.do")
    public ServerResponse update_information(HttpSession session,UserInfo userInfo){
        UserInfo info = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(info == null || info.equals("")){
            return ServerResponse.serverResponseByError("用户需要登录");
        }
        userInfo.setId(info.getId());
        userInfo.setUsername(info.getUsername());
        userInfo.setRole(Const.RoleEnum.ROLE_CUSTOMER.getCode());
        ServerResponse serverResponse = userService.update_information(userInfo);
        if(serverResponse.isSuccess()){
            UserInfo userInfo1 = userService.findUserInfoByUserid(userInfo.getId());
            session.setAttribute(Const.CURRENTUSER,userInfo1);
        }
        return serverResponse;
    }

    /*
        退出登录
     */
    @RequestMapping(value = "/logout.do")
    public ServerResponse logout(HttpSession session){
        session.removeAttribute(Const.CURRENTUSER);
        return ServerResponse.serverResponseBySuccess("退出成功");
    }

}
