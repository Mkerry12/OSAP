package com.mqq.service;

import com.mqq.dto.UserPasswordUpdateDTO;
import com.mqq.dto.UserPhoneUpdateDTO;
import com.mqq.dto.UserProfileUpdateDTO;
import com.mqq.result.Result;

public interface UserService {
    Result profile();

    Result updateProfile(UserProfileUpdateDTO userProfileUpdateDTO);

    Result updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO);

    Result updatePhone(UserPhoneUpdateDTO userPhoneUpdateDTO);
}
