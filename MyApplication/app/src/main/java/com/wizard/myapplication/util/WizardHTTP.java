/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wizard.myapplication.util;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author Wizard
 */
public class WizardHTTP 
{
    private HashMap<String, String> header
    = new HashMap<String, String>();
    private Map<String, String> retHeader
    = new HashMap<String, String>();
    private Proxy proxy;
    private int timeout = 4000;
    private String charset = "GBK";
    
    public String getHeader(String name)
    {
        return header.get(name);
    }
    
    public void setHeader(String name, String value)
    {
        header.put(name, value);
    }
    
    public void delHeader(String name)
    {
        header.remove(name);
    }
    public void clearHeader()
    {
        header.clear();
    }
    public void setDefHeader(boolean mobile)
    {
        header.put("Accept", "*/*");
        header.put("Accept-Language", "zh-cn");
        if(mobile)
            header.put("User-Agent", "Dalvik/1.1.0 (Linux; U; Android 2.1; sdk Build/ERD79)");
        else
            header.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Cache-Control", "no-cache");
    }
    
    public String getRetHeader(String name)
    {
        return retHeader.get(name);
    }
    
    public String getCharset()
    {
        return charset;
    }
    
    public void setCharset(String cs)
    {
        charset = cs;
    }
    
    public Proxy getProxy()
    {
        return proxy;
    }
    
    public void setProxy(Proxy proxy)
    {
        this.proxy = proxy;
    }
    
    private InputStream getResponseStream(String method, String tar, String postdata)
            throws IOException
    {
        URL url = new URL(tar);
        HttpURLConnection conn;
        if(proxy == null)
            conn = (HttpURLConnection) url.openConnection();
        else
            conn = (HttpURLConnection) url.openConnection(proxy);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        for(String k : header.keySet())
            conn.setRequestProperty(k, header.get(k));
        if(method.equals("POST"))
        {
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            StreamWriter sw
              = new StreamWriter(conn.getOutputStream());
            sw.write(postdata);
            sw.close();
        }
        conn.connect();
        retHeader.clear();
        Map<String, List<String>> oriRetHeader = conn.getHeaderFields();
        for(String k : oriRetHeader.keySet())
        {
            List<String> li = oriRetHeader.get(k);
            StringBuffer sb = new StringBuffer();
            for(String s : li)
                sb.append(s).append(",");
            if(sb.length() != 0)
                sb.setLength(sb.length() - 1);
            retHeader.put(k, sb.toString());
        }
        return conn.getInputStream();
    }

    private String httpSubmit(String method, String tar, String postdata)
            throws IOException
    {
        StreamReader sr
          = new StreamReader(getResponseStream(method, tar, postdata), charset);
        String retstr = sr.readToEnd();
        sr.close();
        return retstr;
    }

    private byte[] httpSubmitData(String method, String tar, String postdata)
            throws IOException
    {
        InputStream inStream = getResponseStream(method, tar, postdata);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4000];
        while(true)
        {
            int size = inStream.read(buffer);
            if(size == -1) break;
            outStream.write(buffer, 0, size);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public String httpGet(String tar)
           throws IOException
    {
        return httpSubmit("GET", tar, "");
    }
    
    public String httpPost(String tar, String data)
           throws IOException
    {
        return httpSubmit("POST", tar, data);
    }

    public byte[] httpGetData(String tar) throws IOException { return httpSubmitData("GET", tar, ""); }

    public byte[] httpPostData(String tar, String data) throws IOException { return httpSubmitData("POST", tar, data); }
}
