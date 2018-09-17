package com.lys.androidapp.myreceiverapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lys.androidapp.myreceiverapplication.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;



/**
 * Created by Hjo on 2017/5/17.
 */

public class ToolUtils {


    public void setApi(String api){
        SharedPreferences.Editor editor=getSharedPreferences("ApiXML").edit();
        editor.putString("api",api);
        editor.commit();
    }

    public String getApi(){
        SharedPreferences sharedPreferences=getSharedPreferences("ApiXML");
        String api=sharedPreferences.getString("api","");
        return api;
    }



    public void setNameAndPss(String name,String ps){
        SharedPreferences.Editor editor=getSharedPreferences("NamePssXML").edit();
        editor.putString("Name",name);
        editor.putString("Ps",ps);
        editor.commit();
    }

    public String[] getNameAndPss(){
        SharedPreferences sharedPreferences=getSharedPreferences("NamePssXML");
        String userName=sharedPreferences.getString("Name",null);
        String ps=sharedPreferences.getString("Ps",null);
        String [] str=new String [2];
        str[0]=userName;
        str[1]=ps;
        return str;
    }

    private   SharedPreferences  getSharedPreferences(String XMLName){
        SharedPreferences sharedPreferences= MyApplication.getMyApplicationContext().getSharedPreferences(XMLName, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public void setIsLogin(boolean isLogin){
        SharedPreferences.Editor editor=getSharedPreferences("LoginTimeXML").edit();
        editor.putBoolean("isLogin",isLogin);
        editor.commit();
    }

    public boolean getIsLogin(){
        SharedPreferences sharedPreferences=getSharedPreferences("LoginTimeXML");
        boolean isLogin=sharedPreferences.getBoolean("isLogin",false);
        return isLogin;
    }



    public void setIsUpload(boolean isUpload){
        SharedPreferences.Editor editor=getSharedPreferences("LoginTimeXML").edit();
        editor.putBoolean("isUpload",isUpload);
        editor.commit();
    }

    public boolean getisUpload(){
        SharedPreferences sharedPreferences=getSharedPreferences("LoginTimeXML");
        boolean isUpload=sharedPreferences.getBoolean("isUpload",false);
        return isUpload;
    }


    public void setLoginTime(){
        SharedPreferences.Editor editor=getSharedPreferences("LoginTimeXML").edit();
        editor.putString("time",getNowTime());
        editor.commit();
    }

    public String getLoginTime(){
        SharedPreferences sharedPreferences=getSharedPreferences("LoginTimeXML");
        String time=sharedPreferences.getString("time","");
        return time;
    }


    public void setLoginSecretKey(String login_secret_key){
        SharedPreferences.Editor editor=getSharedPreferences("LoginTimeXML").edit();
        editor.putString("login_secret_key",login_secret_key);
        editor.commit();
    }

    public String getLoginSecretKey(){
        SharedPreferences sharedPreferences=getSharedPreferences("LoginTimeXML");
        String login_secret_key=sharedPreferences.getString("login_secret_key","");
        return login_secret_key;
    }


    public void setMerchantNo(String merchant_no){
        SharedPreferences.Editor editor=getSharedPreferences("LoginTimeXML").edit();
        editor.putString("merchant_no",merchant_no);
        editor.commit();
    }

    public String  getMerchantNo(){
        SharedPreferences sharedPreferences=getSharedPreferences("LoginTimeXML");
        String merchant_no=sharedPreferences.getString("merchant_no","");
        return merchant_no;
    }


    public void setIsAlipay(boolean isAlipay){
        SharedPreferences.Editor editor=getSharedPreferences("PayTypeXML").edit();
        editor.putBoolean("isAlipay",isAlipay);
        editor.commit();
    }

    public boolean getIsAlipay(){
        SharedPreferences sharedPreferences=getSharedPreferences("PayTypeXML");
        boolean isAlipay=sharedPreferences.getBoolean("isAlipay",false);
        return isAlipay;
    }


    public void setAlipayCount(String alipayCount){
        SharedPreferences.Editor editor=getSharedPreferences("PayTypeXML").edit();
        editor.putString("alipayCount",alipayCount);
        editor.commit();
    }

    public String getAlipayCount(){
        SharedPreferences sharedPreferences=getSharedPreferences("PayTypeXML");
        String alipayCount=sharedPreferences.getString("alipayCount","");
        return alipayCount;
    }


    public void setAlipayCountId(String alipayCountId){
        SharedPreferences.Editor editor=getSharedPreferences("PayTypeXML").edit();
        editor.putString("alipayCountId",alipayCountId);
        editor.commit();
    }

    public String getAlipayCountId(){
        SharedPreferences sharedPreferences=getSharedPreferences("PayTypeXML");
        String alipayCountId=sharedPreferences.getString("alipayCountId","");
        return alipayCountId;
    }


    public void setIsWeChat(boolean isWeChat){
        SharedPreferences.Editor editor=getSharedPreferences("PayTypeXML").edit();
        editor.putBoolean("isWeChat",isWeChat);
        editor.commit();
    }

    public boolean getIsWeChat(){
        SharedPreferences sharedPreferences=getSharedPreferences("PayTypeXML");
        boolean isWeChat=sharedPreferences.getBoolean("isWeChat",false);
        return isWeChat;
    }


    public void setWeChartCount(String wechartCount){
        SharedPreferences.Editor editor=getSharedPreferences("PayTypeXML").edit();
        editor.putString("wechartCount",wechartCount);
        editor.commit();
    }

    public String getWeChartCount(){
        SharedPreferences sharedPreferences=getSharedPreferences("PayTypeXML");
        String wechartCount=sharedPreferences.getString("wechartCount","");
        return wechartCount;
    }


    public void setWeChartCountId(String WeChartCountId){
        SharedPreferences.Editor editor=getSharedPreferences("PayTypeXML").edit();
        editor.putString("WeChartCountId",WeChartCountId);
        editor.commit();
    }

    public String getWeChartCountId(){
        SharedPreferences sharedPreferences=getSharedPreferences("PayTypeXML");
        String WeChartCountId=sharedPreferences.getString("WeChartCountId","");
        return WeChartCountId;
    }


    public static String getNowTime() {
        Calendar cal =   Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowDate = sdf.format(cal.getTime());
        return nowDate;
    }

    public String getSystemMill(){
        return  System.currentTimeMillis()+"";
    }


    /**
     * 截取字符串str中指定字符 strStart、strEnd之间的字符串
     *
     * @param str
     * @param strStart
     * @param strEnd
     * @return
     */
    public static String subString(String str, String strStart, String strEnd) {

        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);

        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            return "";
        }
        if (strEndIndex < 0) {
            return "";
        }
        /* 开始截取 */
        String result = str.substring(strStartIndex, strEndIndex).substring(strStart.length());
        return result;
    }



}
