package com.mqq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPasswordUpdateDTO implements Serializable {

    private String oldPassword;

    private String newPassword;

}
