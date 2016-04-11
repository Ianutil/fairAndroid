package com.ian.android.templateproject.entity;

import java.io.Serializable;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 用户登录信息
 *
 */
public class User implements Serializable{
    public String loginName;
    public String password;

    public String userToken;
    public String tokenCacheKey;
    public long userGuid;

    public String validateCode;

    @Override
    public String toString() {
        return "User{" +
                "loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", userToken='" + userToken + '\'' +
                ", tokenCacheKey='" + tokenCacheKey + '\'' +
                ", userGuid='" + userGuid + '\'' +
                '}';
    }
}
