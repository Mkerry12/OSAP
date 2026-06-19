package com.mqq.mapper;

import com.mqq.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("insert into user(username, phone, email, password, role, create_at, update_at) values(#{username},#{phone},#{email},#{password},#{role},#{createAt},#{updateAt})")
    void insert(User user);

    @Select("select * from user WHERE username = #{username}")
    User getByUsername(String username);
}
