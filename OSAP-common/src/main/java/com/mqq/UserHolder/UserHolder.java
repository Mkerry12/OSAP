package com.mqq.UserHolder;


import com.mqq.entity.UserInfo;

public class UserHolder {

    public static ThreadLocal<UserInfo> userLocal = new ThreadLocal<>();

    public static void setCurrentUser(UserInfo user){
        userLocal.set(user);
    }

    public static UserInfo getCurrentUser(){
        return userLocal.get();
    }

    public static void removeCurrentUser(){
        userLocal.remove();
    }

}
