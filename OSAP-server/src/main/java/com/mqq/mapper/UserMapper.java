package com.mqq.mapper;

import com.github.pagehelper.Page;
import com.mqq.annotation.AutoFill;
import com.mqq.dto.UserProfileUpdateDTO;
import com.mqq.entity.User;
import com.mqq.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.Map;

@Mapper
public interface UserMapper {

    @AutoFill(OperationType.INSERT)
    @Insert("insert into user(username, phone, email, password, role, image, create_at, update_at,survey_count,response_count) values(#{username},#{phone},#{email},#{password},#{role},#{image},#{createAt},#{updateAt},#{surveyCount},#{responseCount})")
    void insert(User user);

    @Select("select * from user WHERE username = #{username}")
    User getByUsername(String username);

    @Select("select * from user WHERE id = #{id}")
    User getById(Long id);

    @Update("update user set password = #{newPassword} where phone = #{phone}")
    void updatePassword(String newPassword,String phone);

    @Update("update user set username = #{dto.username},email = #{dto.email},image = #{dto.image} " +
            "Where id = #{userId} ")
    void updateProfile(@Param("dto") UserProfileUpdateDTO userProfileUpdateDTO, Long userId);

    @Update("update user set phone = #{newPhone} where id = #{userId} ")
    void updatePhone(String newPhone, Long userId);

    @Update("update user set status = #{status} where id = #{userId}")
    void updateStatus(@Param("userId") Long userId, @Param("status") Integer status);

    @Delete("delete from user where id = #{id}")
    void deleteById(Long id);

    Page<User> pageQuery(@Param("keyword") String keyword, @Param("status") String status);
}
