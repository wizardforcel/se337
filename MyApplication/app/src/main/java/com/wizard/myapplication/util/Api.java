package com.wizard.myapplication.util;

import android.util.Log;

import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.BuildingType;
import com.wizard.myapplication.entity.Campus;
import com.wizard.myapplication.entity.Comment;
import com.wizard.myapplication.entity.Event;
import com.wizard.myapplication.entity.Result;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.entity.UserResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by asus on 2015/8/14.
 */
public class Api
{
    public static UserResult login(WizardHTTP http, String un, String pw)
            throws JSONException, IOException
    {
        String postStr = "userName=" + un + "&password=" + pw;
        String retStr = http.httpPost("http://" + UrlConfig.HOST + "/user/login", postStr);
        JSONObject retJson = new JSONObject(retStr);
        int succ = retJson.getInt("code");
        if(succ != 1)
            return new UserResult(1, retJson.getString("detail"), null);
        User user = new User();
        user.setId(Integer.parseInt(retJson.getString("detail")));
        user.setUn(un);
        user.setPw(pw);
        Log.d("UserLogin", "id: " + user.getId() + " un: " + user.getUn() + " pw: " + user.getPw());
        user.setAvatar(getAvatarById(http, user.getId()));
        return new UserResult(0, "", user);
    }

    public static List<String> getPres(WizardHTTP http, int uid)
            throws JSONException, IOException
    {
        String retStr = http.httpGet("http://" + UrlConfig.HOST + "/user/" + uid +"/preferences");
        JSONArray retArr = new JSONArray(retStr);
        List<String> pres = new ArrayList<String>();
        for(int i = 0; i < retArr.length(); i++)
        {
            String type = retArr.getString(i);
            if(Arrays.asList(BuildingType.TYPES).contains(type))
                pres.add(type);
        }
        return pres;
    }

    public static UserResult reg(WizardHTTP http, String un, String pw)
            throws JSONException, IOException
    {
        String postStr = "userName=" + un + "&password=" + pw;
        String retStr = http.httpPost("http://" + UrlConfig.HOST + "/user/register", postStr);
        JSONObject retJson = new JSONObject(retStr);
        int succ = retJson.getInt("code");
        if(succ != 1)
            return new UserResult(1, retJson.getString("detail"), null);
        User user = new User();
        user.setId(Integer.parseInt(retJson.getString("detail")));
        user.setUn(un);
        user.setPw(pw);
        Log.d("UserReg", "id: " + user.getId() + " un: " + user.getUn() + " pw: " + user.getPw());
        user.setAvatar(getAvatarById(http, user.getId()));
        return new UserResult(0, "", user);
    }

    public static Result addPres(WizardHTTP http, int uid, String toAdd)
            throws IOException, JSONException {
        String postStr = "preferenceType=" + toAdd + "&userId="+ uid;
        String retStr
                = http.httpPost("http://" + UrlConfig.HOST + "/user/addpreference", postStr);
        JSONObject json = new JSONObject(retStr);
        int succ = json.getInt("code");
        if(succ == 1)
            return new Result(0, "");
        else
            return new Result(1, json.getString("detail"));
    }

    public static Result delPres(WizardHTTP http, int uid, String toAdd)
            throws IOException, JSONException {
        String postStr = "preferenceType=" + toAdd + "&userId="+ uid;
        String retStr
                = http.httpPost("http://" + UrlConfig.HOST + "/user/deletepreference", postStr);
        JSONObject json = new JSONObject(retStr);
        int succ = json.getInt("code");
        if(succ == 1)
            return new Result(0, "");
        else
            return new Result(1, json.getString("detail"));
    }

    public static boolean commentLike(WizardHTTP http, int commentId)
            throws IOException
    {
        String retStr = http.httpGet("http://" + UrlConfig.HOST + "/comment/like/" + commentId);
        return true;
    }

    public static boolean commentDislike(WizardHTTP http, int commentId)
            throws IOException
    {
        String retStr = http.httpGet("http://" + UrlConfig.HOST + "/comment/dislike/" + commentId);
        return true;
    }

    public static Comment addViewComment(WizardHTTP http, int viewId, User user, String comment)
            throws JSONException, IOException
    {
        JSONObject postJson = new JSONObject();
        postJson.put("viewId", viewId);
        postJson.put("type", "view");
        postJson.put("content", comment);
        postJson.put("userId", user.getId());
        String retStr = http.httpPost("http://" + UrlConfig.HOST + "/comment/add", postJson.toString());
        JSONObject retJson = new JSONObject(retStr);
        Comment c = new Comment();
        c.setId(retJson.getInt("id"));
        c.setUid(user.getId());
        c.setUn(user.getUn());
        c.setContent(comment);
        c.setAvatar(user.getAvatar());
        Log.d("BuildingAddComment",
                "id: " + c.getId() + " uid: " + c.getUid() + " un: " + c.getUn());
        return c;
    }

    public static Comment addActivityComment(WizardHTTP http, int activityId, User user, String comment)
            throws JSONException, IOException
    {
        String postStr = "userId=" + user.getId() + "&activityId=" + activityId + "&content=" + comment;
        String retStr = http.httpPost("http://" + UrlConfig.HOST + "/activity/comment/add", postStr);
        JSONObject retJson = new JSONObject(retStr);
        Comment c = new Comment();
        c.setId(retJson.getInt("id"));
        c.setUid(user.getId());
        c.setUn(user.getUn());
        c.setContent(comment);
        c.setAvatar(user.getAvatar());
        Log.d("EventAddComment",
                "id: " + c.getId() + " uid: " + c.getUid() + " un: " + c.getUn());
        return c;
    }

    public static List<Comment> getViewComment(WizardHTTP http, int viewId)
            throws JSONException, IOException {
        String retStr = http.httpGet("http://" + UrlConfig.HOST + "/comment/view/" + viewId);
        JSONArray retArr = new JSONArray(retStr);
        List<Comment> comments = new ArrayList<Comment>();
        for (int i = 0; i < retArr.length(); i++) {
            JSONObject o = retArr.getJSONObject(i);
            Comment c = new Comment();
            c.setId(o.getInt("id"));
            int uid = o.getInt("userId");
            c.setUid(uid);
            c.setUn(Api.getUnById(http, uid));
            c.setContent(o.getString("content"));
            c.setLike(o.getInt("likes"));
            c.setDislike(o.getInt("dislike"));
            c.setAvatar(Api.getAvatarById(http, uid));
            comments.add(c);
            Log.d("BuildingComment", "id: " + c.getId() + " uid: " + c.getUid() + " un: " + c.getUn());
        }
        return comments;
    }


    public static List<Comment> getActivityComment(WizardHTTP http, int activityId)
            throws JSONException, IOException {
        String retStr = http.httpGet("http://" + UrlConfig.HOST + "/comment/activity/" + activityId);
        JSONArray retArr = new JSONArray(retStr);
        List<Comment> comments = new ArrayList<Comment>();
        for (int i = 0; i < retArr.length(); i++) {
            JSONObject o = retArr.getJSONObject(i);
            Comment c = new Comment();
            c.setId(o.getInt("id"));
            int uid = o.getInt("userId");
            c.setUid(uid);
            c.setUn(Api.getUnById(http, uid));
            c.setContent(o.getString("content"));
            c.setLike(o.getInt("likes"));
            c.setDislike(o.getInt("dislike"));
            c.setAvatar(Api.getAvatarById(http, uid));
            comments.add(c);
            Log.d("EventComment", "id: " + c.getId() + " uid: " + c.getUid() + " un: " + c.getUn());
        }
        return comments;
    }
    public static byte[] getViewPic(WizardHTTP http, int viewId)
            throws JSONException, IOException
    {
        String retStr = http.httpGet("http://" + UrlConfig.HOST + "/picture/view/" + viewId);
        JSONArray retArr = new JSONArray(retStr);
        byte[] imgData = null;
        if(retArr.length() != 0) {
            JSONObject imgJson = retArr.getJSONObject(0);
            String imgPath = imgJson.getString("path");
            imgPath = "http://" + UrlConfig.HOST + "/picture/" + imgPath.replace(".", "/");
            Log.d("BuildingImg", imgPath);
            try {
                imgData = http.httpGetData(imgPath);
            } catch(Exception ex) {}
        }
        return imgData;
    }

    public static List<Building> getHistory(WizardHTTP http, int uid, int campusId)
            throws JSONException, IOException
    {
        String retStr = http.httpGet("http://" + UrlConfig.HOST + "/view/usertoview/" + uid);
        JSONArray retArr = new JSONArray(retStr);
        List<Building> covered = new ArrayList<Building>();
        for (int i = 0; i < retArr.length(); i++) {
            JSONObject o = retArr.getJSONObject(i).getJSONObject("view");
            if (o.getJSONObject("university").getInt("id") != campusId)
                continue;
            Building b = new Building();
            b.setId(o.getInt("id"));
            b.setName(o.getString("name"));
            b.setContent(o.getString("description"));
            b.setLatitude(o.getDouble("latitude"));
            b.setLongitude(o.getDouble("longitude"));
            b.setRadius(o.getDouble("radius"));
            covered.add(b);
            Log.d("History", "id: " + b.getId() + " name: " + b.getName());
        }
        return covered;
    }

    public static boolean addHistory(WizardHTTP http, int uid, int viewId)
            throws IOException
    {
        String retStr
                = http.httpGet("http://" + UrlConfig.HOST + "/addusertoview/view/" + viewId + "/user/" + uid);
        return !retStr.equals("");
    }

    public static Campus getCampus(WizardHTTP http, double lat, double lng)
            throws JSONException, IOException
    {
        //String url = String.format("http://%s/university/findByGPS/longitude/%6f/latitude/%6f",
        //                          UrlConfig.HOST, lng, lat);
        String url = String.format("http://%s/university/findByGPS/longitude/121.449088/latitude/31.028980",  UrlConfig.HOST);
        String retStr = http.httpGet(url);
        JSONArray retArr = new JSONArray(retStr);
        JSONObject retJson = retArr.getJSONObject(0);

        Campus c = new Campus();
        c.setId(retJson.getInt("id"));
        c.setName(retJson.getString("name"));
        c.setContent(retJson.getString("description"));
        c.setRadius(retJson.getDouble("radius"));
        c.setLatitude(retJson.getDouble("latitude"));
        c.setLongitude(retJson.getDouble("longitude"));
        Log.d("Campus", "id: " + c.getId() + " name: " + c.getName());
        c.setAvatar(Api.getCampusPic(http, c.getId()));
        List<Building> buildings = Api.getView(http, c.getId());
        c.setBuildings(buildings);
        return c;
    }

    public static List<Event> getActiivity(WizardHTTP http, int campusId)
            throws JSONException, IOException
    {
        String date = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
                .format(Calendar.getInstance().getTime());
        String retStr = http.httpGet(
                "http://" + UrlConfig.HOST + "/activity/university/" + campusId + "/date/" + date);
        JSONArray retArr = new JSONArray(retStr);
        List<Event> events = new ArrayList<Event>();
        for(int i = 0; i < retArr.length(); i++) {
            JSONObject json = retArr.getJSONObject(i);
            Event event = new Event();
            event.setId(json.getInt("id"));
            event.setName(json.getString("name"));
            event.setContent(json.getString("description"));
            event.setEnrollStartDate(json.getString("enrollStartDate"));
            event.setStartDate(json.getString("activityStartDate"));
            event.setEndDate(json.getString("activityEndDate"));
            event.setMaxPeople(json.getInt("peopleLimit"));
            event.setLat(json.getDouble("longitude"));
            event.setLng(json.getDouble("latitude"));
            event.setLocation(json.getString("location"));
            event.setEnrollEndDate(json.getString("enrollEndDate"));
            int uid = json.getInt("userId");
            event.setUid(uid);
            event.setUn(Api.getUnById(http, uid));
            byte[] imgData
                    = http.httpGetData("http://" + UrlConfig.HOST + "/avatar/user/" + uid);
            event.setAvatar(imgData);
            events.add(event);
            Log.d("Event", "id: " + event.getId() + " uid: " + event.getUid() +
                    " un: " + event.getUn() + " date: " + event.getStartDate());
        }
        return events;
    }

    public static Event addActivity(WizardHTTP http, int campusId, User user, String name, String content,
                                    String enrollStart, String enrollEnd, String start, String end,
                                    int limit, Building loc)
            throws JSONException, IOException
    {
        String postStr = "userId=" + user.getId() + "&universityId=" + campusId + "&name=" + name +
                         "&description=" + content + "&enrollStartDate=" + enrollStart + "&enrollEndDate=" +
                         enrollEnd + "&activityStartDate=" + start + "&activityEndDate=" + end +
                         "&peopleLimit=" + limit + "&location=" + loc.getName();
        String retStr = http.httpPost("http://" + UrlConfig.HOST + "/activity/add/", postStr);
        JSONObject retJson = new JSONObject(retStr);

        Event e = new Event();
        e.setName(name);
        e.setContent(content);
        //e.setId(retJson.getInt("id"));
        e.setUid(user.getId());
        e.setUn(user.getUn());
        e.setLocation(loc.getName());
        e.setLat(loc.getLatitude());
        e.setLng(loc.getLongitude());
        e.setEnrollStartDate(enrollStart);
        e.setEnrollEndDate(enrollEnd);
        e.setStartDate(start);
        e.setEndDate(end);
        Log.d("AddEvent", "id: " + e.getId() + " uid: " + e.getUid() +
                " un: " + e.getUn() + " date: " + e.getStartDate());
        return e;
    }

    public static String getUnById(WizardHTTP http, int uid)
            throws IOException, JSONException
    {
        String retStr = http.httpGet("http://" + UrlConfig.HOST + "/user/" + uid + "/userName/");
        JSONObject json = new JSONObject(retStr);
        return json.getString("detail");
    }

    public static byte[] getAvatarById(WizardHTTP http, int uid)
            throws IOException
    {
        return http.httpGetData("http://" + UrlConfig.HOST + "/avatar/user/" + uid);
    }

    public static byte[] getCampusPic(WizardHTTP http, int campusId)
            throws IOException, JSONException
    {
        String retStr = http.httpGet("http://" + UrlConfig.HOST + "/picture/university/" + campusId);
        JSONArray retArr = new JSONArray(retStr);
        byte[] imgData = null;
        if(retArr.length() != 0) {
            JSONObject imgJson = retArr.getJSONObject(0);
            String imgPath = imgJson.getString("path");
            imgPath = "http://" + UrlConfig.HOST + "/picture/" + imgPath.replace(".", "/");
            imgData  = http.httpGetData(imgPath);
            Log.d("CampusImg", imgPath);
        }
        return imgData;
    }

    public static List<Building> getView(WizardHTTP http, int campusId)
            throws IOException, JSONException
    {
        String retStr = http.httpGet("http://" + UrlConfig.HOST + "/views/university/" + campusId);
        JSONArray retArr = new JSONArray(retStr);
        List<Building> buildings = new ArrayList<Building>();
        for(int i = 0; i < retArr.length(); i++)
        {
            JSONObject buildingJson = retArr.getJSONObject(i);
            Building b = new Building();
            b.setId(buildingJson.getInt("id"));
            b.setName(buildingJson.getString("name"));
            b.setContent(buildingJson.getString("description"));
            b.setLatitude(buildingJson.getDouble("latitude"));
            b.setLongitude(buildingJson.getDouble("longitude"));
            b.setRadius(buildingJson.getDouble("radius"));
            buildings.add(b);
            Log.d("Building", "id: " + b.getId() + " name: " + b.getName());
        }
        return buildings;
    }

}
