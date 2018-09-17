package com.lys.androidapp.myreceiverapplication.bean;

import com.google.gson.Gson;

/**
 * @author lys
 * @time 2018/9/6 14:59
 * @desc:
 */

public class LoginBean  extends BaseBean {


    /**
     * code : 1
     * msg : 成功
     * login_secret_key : jxsurMGmRNeHalhv
     * merchant_no : 8654553769886024
     */

    private String code;
    private String msg;
    private String login_secret_key;
    private String merchant_no;

    public static LoginBean objectFromData(String str) {

        return new Gson().fromJson(str, LoginBean.class);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setLogin_secret_key(String login_secret_key) {
        this.login_secret_key = login_secret_key;
    }

    public void setMerchant_no(String merchant_no) {
        this.merchant_no = merchant_no;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getLogin_secret_key() {
        return login_secret_key;
    }

    public String getMerchant_no() {
        return merchant_no;
    }
}
