package com.mqq.mapper;

import com.github.pagehelper.Page;
import com.mqq.dto.UserProfileUpdateDTO;
import com.mqq.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.Map;

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

    @Update("update user set status = #{status} where id = #{userId}")
    void updateStatus(@Param("userId") Long userId, @Param("status") Integer status);

    @Select({"<script>",
             "select * from user",
             "<where>",
             "<if test='keyword != null and keyword != \"\"'>",
             "and (username like concat('%',#{keyword},'%') or phone like concat('%',#{keyword},'%'))",
             "</if>",
             "<if test='status != null and status != \"\"'>",
             "and status = #{status}",
             "</if>",
             "</where>",
             "order by create_at desc",
             "</script>"})
    Page<User> pageQuery(@Param("keyword") String keyword, @Param("status") String status);
}
