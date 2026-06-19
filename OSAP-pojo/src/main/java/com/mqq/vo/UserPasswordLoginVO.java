package com.mqq.vo;

import com.mqq.entity.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserPasswordLoginVO implements Serializable {

    private String token;
    private String expiresIn;
    private User user;
}
