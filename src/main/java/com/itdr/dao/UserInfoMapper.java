package com.itdr.dao;

import com.itdr.pojo.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbg.generated Mon Oct 29 21:53:53 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbg.generated Mon Oct 29 21:53:53 CST 2018
     */
    int insert(UserInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbg.generated Mon Oct 29 21:53:53 CST 2018
     */
    UserInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbg.generated Mon Oct 29 21:53:53 CST 2018
     */
    List<UserInfo> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbg.generated Mon Oct 29 21:53:53 CST 2018
     */
    int updateByPrimaryKey(UserInfo record);

    /**
     * 校验用户名是否存在
     */
    int checkUsername(String username);

    /**
     * 校验邮箱是否存在
     */
    int checkEmail(String email);

    /**
     * 根据用户名和密码查询用户信息
     */
    UserInfo selectUserInfoByUsernameAndPassword(@Param("username") String username,
                                                 @Param("password") String password);

    /**
     * 根据用户名查询密码问题
     */

    String selectQuestionByUsername(String username);

    /**
     * 根据用户名和问题和答案查询
     */
    int selectUsernameAndQuestionAndAnswer(@Param("username") String username,
                                           @Param("question") String question,
                                           @Param("answer") String answer);

    /**
     * 修改用户密码接口
     */

    int updateUserPassword(@Param("username") String username,
                           @Param("password") String newPassword);

    /**
     *根据userid和passwordOld查询
     */
    int selectCountByUserIdAndPassword(@Param("userid") int userid,
                                       @Param("password") String password);

    /**
     * 根据userid更新密码
     */

    int updateBySelectActive(UserInfo record);

    /**
     * 校验邮箱
     */
    int checkEmailByUserId(@Param("userid") int userid,
                           @Param("email") String email);

}