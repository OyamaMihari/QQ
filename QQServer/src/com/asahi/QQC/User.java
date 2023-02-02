package com.asahi.QQC;

import java.io.Serializable;

/**
 * @author Asahi
 */
@SuppressWarnings({"all"})
public class User implements Serializable {
    // 兼容性
    private static final long serialVersionUID = 1L;
    private String userId;
    private String passwd;
    private boolean isLoad;

    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean load) {
        isLoad = load;
    }

    public User() {
    }

    public User(String userId, String passwd) {
        this.userId = userId;
        this.passwd = passwd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
