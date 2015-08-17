package com.wizard.myapplication.entity;

/**
 * Created by asus on 2015/8/14.
 */
public class DataResult<T> extends Result
{
    public T getData() {
        return data;
    }

    public void setUser(T data) {
        this.data = data;
    }

    public DataResult(int errno, String errmsg, T data)
    {
        super(errno, errmsg);
        this.data = data;
    }

    T data;
}
