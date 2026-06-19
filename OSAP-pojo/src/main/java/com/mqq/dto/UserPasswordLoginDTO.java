package com.mqq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPasswordLoginDTO implements Serializable {

    private String username;

    private String password;
}
