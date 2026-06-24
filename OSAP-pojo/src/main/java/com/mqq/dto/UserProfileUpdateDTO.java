package com.mqq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfileUpdateDTO implements Serializable {

    private String username;

    private String email;

    private String image;

}
