package com.mqq.mapper;

import com.mqq.dto.UserProfileUpdateDTO;
import com.mqq.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Insert("insert into user(username, phone, email, password, role, create_at, update_at,survey_count,response_count) values(#{username},#{phone},#{email},#{password},#{role},#{createAt},#{updateAt},#{surveyCount},#{responseCount})")
    void insert(User user);

    @Select("select * from user WHERE username = #{username}")
    User getByUsername(String username);

    @Select("select * from user WHERE id = #{id}")
    User getById(Long id);

    @Update("update user set password = #{newPassword} where phone = #{phone}")
    void updatePassword(String phone, String newPassword);

    @Update("update user set username = #{dto.username},phone = #{dto.phone},email = #{dto.email},image = #{dto.image} " +
            "Where id = #{userId} ")
    void updateProfile(@Param("dto") UserProfileUpdateDTO userProfileUpdateDTO, Long userId);

    @Update("update user set phone = #{newPhone} where id = #{userId} ")
    void updatePhone(String newPhone, Long userId);
}
