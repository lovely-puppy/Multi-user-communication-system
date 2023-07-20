package com.wang.qqcommon;

import java.io.Serializable;

/**
 * @author 汪文松
 * @date 2023/7/18 9:46
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String pwd;

    public User() {}
    public User(String userId, String pwd) {
        this.userId = userId;
        this.pwd = pwd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
