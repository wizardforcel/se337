package com.wizard.myapplication.entity;

/**
 * Created by asus on 2015/8/14.
 */
public class Result
{
    int errno;
    String errmsg;

    public Result(int errno, String errmsg)
    {
        this.errno = errno;
        this.errmsg = errmsg;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

}
