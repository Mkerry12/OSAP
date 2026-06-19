package com.mqq.UserHolder;

public class UserHolder {

    public static ThreadLocal<Long> userLocal = new ThreadLocal<>();

    public static void setCurrentUser(Long userId){
        userLocal.set(userId);
    }
    public static Long getCurrentUser(){
        return userLocal.get();
    }
    public static void removeCurrentUser(){
        userLocal.remove();
    }

}
