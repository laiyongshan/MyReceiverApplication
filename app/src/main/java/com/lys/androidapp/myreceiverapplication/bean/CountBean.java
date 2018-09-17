package com.lys.androidapp.myreceiverapplication.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lys
 * @time 2018/9/6 17:39
 * @desc:
 */

public class CountBean extends BaseBean {


    /**
     * code : 1
     * msg : 成功
     * account_list : [{"id":"7","account":"13502244840"},{"id":"8","account":"13192585630"},{"id":"9","account":"137059401@qq.com"},{"id":"11","account":"1137163146@qq.com"}]
     */

    private String code;
    private String msg;
    private List<AccountListBean> account_list;

    public static CountBean objectFromData(String str) {

        return new Gson().fromJson(str, CountBean.class);
    }


    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setAccount_list(List<AccountListBean> account_list) {
        this.account_list = account_list;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public List<AccountListBean> getAccount_list() {
        return account_list;
    }

    public static class AccountListBean {
        /**
         * id : 7
         * account : 13502244840
         */

        private String id;
        private String account;


        public static List<AccountListBean> arrayAccountListBeanFromData(String str) {

            Type listType = new TypeToken<ArrayList<AccountListBean>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getId() {
            return id;
        }

        public String getAccount() {
            return account;
        }
    }
}
