package com.wust.mapper;

import com.wust.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

//@Mapper
public interface UserMapper {

    /**
     * 插入用户数据
     * @param record
     * @return
     */
    int insert(User record);

    /**
     * 根据条件查询用户列表
     * @param record
     * @return
     */
    List<User> selectByRecord(User record);

    /**
     * 查询所有用户列表
     * @return
     */
    List<User> selectAllUser();

    /**
     * 根据手机号查询指定用户
     * @param phone
     * @return
     */
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据手机号删除指定用户
     * @param phone
     * @return
     */
    int deleteByPhone(@Param("phone") String phone);

    /**
     * 更新用户数据
     * @param record
     * @return
     */
    int updateByRecord(User record);

    /**
     * 更新用户手机号
     * @param newPhone
     * @param oldPhone
     * @return
     */
    int updatePhone(@Param("newPhone") String newPhone, @Param("oldPhone") String oldPhone);

    /**
     * 查询满足条件用户数目
     * @return
     */
    long countByRecord(User record);

    /**
     * 根据手机号查询注册用户数量
     * @param phone
     * @return
     */
    int countByPhone(@Param("phone") String phone);

    /**
     * 根据手机号更换密码
     * @param phone
     * @param pwd
     * @return
     */
    int updatePwd(@Param("phone") String phone, @Param("pwd") String pwd);
}