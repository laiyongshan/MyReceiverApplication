package com.lys.androidapp.myreceiverapplication.presenter;

import android.util.Log;

import com.lys.androidapp.myreceiverapplication.utils.MD5Utils;
import com.lys.androidapp.myreceiverapplication.utils.ToolUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

/**
 * @author lys
 * @time 2018/9/6 10:02
 * @desc:
 */

public class Presenter {

    ToolUtils mToolUtils = new ToolUtils();
    String BASE_URL = mToolUtils.getApi() + "";

    /**
     * 登录
     */
    public void login(String username, String password, StringCallback stringCallback) {
        OkHttpUtils.post(BASE_URL + "/Api/Other/app_m_login")
                .params("login", username)
                .params("pwd", password)
                .execute(stringCallback);
    }

    /**
     * 获取账号列表
     * pay_type 帐号类型(固定：alipay或wechat)
     * merchant_no  登录后获取的商户号
     */
    public void getAccountList(String pay_type, String merchant_no, StringCallback stringCallback) {
        OkHttpUtils.post(BASE_URL + "/Api/Other/get_account_list")
                .params("pay_type", pay_type)
                .params("merchant_no", merchant_no)
                .execute(stringCallback);
    }


    /**
     * 监控收款通知回调
     * price 支付金额
     * account_id  收款帐号id
     * pay_type  支付类型：固定alipay 或 wechat
     * merchant_no 登录后获取的商户号
     * sign 签名参数
     * /Api/Callback/app_notify
     */
    public void appNotify(String msg_content, String account_id, String pay_type, String merchant_no,String systemMill,StringCallback stringCallback) {
        OkHttpUtils.post(BASE_URL + "/Api/Callback/app_notify")
                .params("account_id", account_id)
                .params("merchant_no", merchant_no)
                .params("msg_content", msg_content)
                .params("pay_type", pay_type)
                .params("time",systemMill)
                .params("sign", getSign(msg_content, account_id, pay_type, merchant_no,systemMill))
                .execute(stringCallback);

        Log.i("lys", "加密签名后的数据：" + getSign(msg_content, account_id, pay_type, merchant_no,systemMill));
    }


    public String getSign(String msg_content, String account_id, String pay_type, String merchant_no,String systemMill) {
        String sign = "account_id=" + account_id + "&"
                + "merchant_no=" + merchant_no + "&"
                + "msg_content=" + msg_content + "&"
                + "pay_type=" + pay_type + "&"
                + "time=" + systemMill
                + mToolUtils.getLoginSecretKey() + URLs.app_key;

        Log.i("lys", sign + "签名数据");

        return MD5Utils.encrypt32(sign);
    }

}
