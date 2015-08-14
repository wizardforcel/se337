package com.wizard.myapplication.entity;

/**
 * Created by asus on 2015/8/14.
 */
public class UserResult extends Result
{
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserResult(int errno, String errmsg, User user)
    {
        super(errno, errmsg);
        this.user = user;
    }

    User user;
}
