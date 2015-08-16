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
    private HashMap<String, String> headers
        = new HashMap<String, String>();
    private Map<String, String> retHeaders
        = new HashMap<String, String>();
    private Proxy proxy;
    private int timeout = 4000;
    private String charset = "GBK";

    @Deprecated
    public String getHeader(String name)
    {
        return headers.get(name);
    }
    @Deprecated
    public void setHeader(String name, String value)
    {
        headers.put(name, value);
    }
    @Deprecated
    public void delHeader(String name)
    {
        headers.remove(name);
    }
    @Deprecated
    public void clearHeader()
    {
        headers.clear();
    }

    public void setDefHeader() { setDefHeader(false); }
    public void setDefHeader(boolean mobile)
    {
        headers.put("Accept", "*/*");
        headers.put("Accept-Language", "zh-cn");
        if(mobile)
            headers.put("User-Agent", "Dalvik/1.1.0 (Linux; U; Android 2.1; sdk Build/ERD79)");
        else
            headers.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Cache-Control", "no-cache");
    }

    public Map<String, String> getRetHeaders() {
        return retHeaders;
    }
    public HashMap<String, String> getHeaders() {
        return headers;
    }
    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    public String getRetHeader(String name)
    {
        return retHeaders.get(name);
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
    
    private InputStream getResponseStream(String method, String tar, byte[] data)
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
        for(String k : headers.keySet())
            conn.setRequestProperty(k, headers.get(k));
        if(method.equals("POST"))
        {
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(data);
            os.close();
        }
        conn.connect();
        retHeaders.clear();
        Map<String, List<String>> oriRetHeader = conn.getHeaderFields();
        for(String k : oriRetHeader.keySet())
        {
            List<String> li = oriRetHeader.get(k);
            StringBuffer sb = new StringBuffer();
            for(String s : li)
                sb.append(s).append(",");
            if(sb.length() != 0)
                sb.setLength(sb.length() - 1);
            retHeaders.put(k, sb.toString());
        }
        return conn.getInputStream();
    }

    private InputStream getResponseStream(String method, String tar, String data)
            throws IOException
    {
        return getResponseStream(method, tar, data.getBytes(charset));
    }

    private String httpSubmit(String method, String tar, String data)
            throws IOException
    {
        StreamReader sr
          = new StreamReader(getResponseStream(method, tar, data), charset);
        String retStr = sr.readToEnd();
        sr.close();
        return retStr;
    }

    private String httpSubmit(String method, String tar, byte[] data)
            throws IOException
    {
        StreamReader sr
                = new StreamReader(getResponseStream(method, tar, data), charset);
        String retStr = sr.readToEnd();
        sr.close();
        return retStr;
    }

    private byte[] httpSubmitData(String method, String tar, String data)
            throws IOException
    {
        InputStream inStream = getResponseStream(method, tar, data);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        streamCopy(inStream, outStream);
        inStream.close();
        return outStream.toByteArray();
    }

    private byte[] httpSubmitData(String method, String tar, byte[] data)
            throws IOException
    {
        InputStream inStream = getResponseStream(method, tar, data);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        streamCopy(inStream, outStream);
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
    public String httpPost(String tar, byte[] data)
            throws IOException
    {
        return httpSubmit("POST", tar, data);
    }

    public byte[] httpGetData(String tar) throws IOException { return httpSubmitData("GET", tar, ""); }

    public byte[] httpPostData(String tar, String data) throws IOException { return httpSubmitData("POST", tar, data); }
    public byte[] httpPostData(String tar, byte[] data) throws IOException { return httpSubmitData("POST", tar, data); }

    private static void streamCopy(InputStream is, OutputStream os)
            throws IOException
    {
        byte[] buffer = new byte[4000];
        while(true)
        {
            int size = is.read(buffer);
            if(size == -1) break;
            os.write(buffer, 0, size);
        }
    }
}
