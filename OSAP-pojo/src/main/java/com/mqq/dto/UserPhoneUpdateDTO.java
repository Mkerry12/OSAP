package com.mqq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPhoneUpdateDTO implements Serializable {

    private String oldPhone;

    private String newPhone;

    private String code;

}
