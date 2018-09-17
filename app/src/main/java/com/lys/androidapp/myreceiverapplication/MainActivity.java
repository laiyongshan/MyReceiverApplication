package com.lys.androidapp.myreceiverapplication;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lys.androidapp.myreceiverapplication.cusomView.LoadingProgressDialog;
import com.lys.androidapp.myreceiverapplication.presenter.Presenter;
import com.lys.androidapp.myreceiverapplication.utils.NotifyUtils;
import com.lys.androidapp.myreceiverapplication.utils.ToolUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;


public class MainActivity extends BaseActivity {
    static TextView message_tv;

    int status = -1;

    Button start_btn, stop_btn;

    static StringBuilder stringBuilder = new StringBuilder();

    MyReceiver mReceiver;
    IntentFilter intentFilter;

    static Context context;

    TextView setting_tv;

    ToolUtils mToolUtils;

    Presenter presenter;

    LoadingProgressDialog mLoadingProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        mToolUtils = new ToolUtils();
        presenter = new Presenter();

        mToolUtils.setIsUpload(false);

        mLoadingProgressDialog = new LoadingProgressDialog(this);
        mLoadingProgressDialog.setProgressText("Loading...");
        mLoadingProgressDialog.setCanceledOnTouchOutside(true);
        mLoadingProgressDialog.setCancelable(true);

        message_tv = findViewById(R.id.tv);

        start_btn = findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                message_tv.append("正在启动服务，请稍后...\n");
//                stringBuilder.append("正在启动服务，请稍后...\n");
                if (isEnabled()) {
                    if (mToolUtils.getIsAlipay() || mToolUtils.getIsWeChat()) {
                        if ((mToolUtils.getAlipayCountId() != null && !mToolUtils.getAlipayCountId().equals("") && !mToolUtils.getAlipayCountId().equals("null")) ||
                                (mToolUtils.getWeChartCountId() != null && !mToolUtils.getWeChartCountId().equals("") && !mToolUtils.getWeChartCountId().equals("null"))) {
//                            openAccessibility("com.lys.androidapp.myreceiverapplication.MyAccessibilityServices", MainActivity.this);
                            if (isNotificationListenersEnabled()) {
                                startNotificationListenService();
                            } else {
                                startActivity(new Intent(
                                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                            }
                        } else {
                            goSetting();
                        }
                    } else {
                        goSetting();
                    }
//                    startNotificationListenService();
                } else {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            }
        });


        stop_btn = findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolUtils.setIsUpload(false);
                message_tv.append("监测服务已停止\n");
                stringBuilder.append("监测服务已停止\n");

                sendBroadcast(new Intent("stopService"));

                setBtnStatus(2);
//                    if (notifyIntent != null)
//                        stopService(notifyIntent);
            }
        });

        findViewById(R.id.setting_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.exit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mReceiver != null) {
                    unregisterReceiver(mReceiver);
                    mReceiver = null;
                    NotifyUtils.cancel();
                }
                mToolUtils.setIsUpload(false);

                message_tv.setText("");
                stringBuilder.append("");

                mToolUtils.setIsLogin(false);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("allMessage", stringBuilder.toString() + "");
        outState.putInt("status", status);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            setBtnStatus(savedInstanceState.getInt("status"));
            message_tv.setText(savedInstanceState.getString("allMessage") + "");
        }
    }

    private void setBtnStatus(int btn) {

        if (btn == 1) {
            status = 1;
            start_btn.setBackgroundColor(Color.rgb(34, 150, 141));
            start_btn.setTextColor(Color.WHITE);
            stop_btn.setBackgroundColor(Color.rgb(190, 190, 190));
            stop_btn.setTextColor(Color.BLACK);
        } else if (btn == 2) {
            status = 2;
            stop_btn.setBackgroundColor(Color.rgb(34, 150, 141));
            stop_btn.setTextColor(Color.WHITE);
            start_btn.setBackgroundColor(Color.rgb(190, 190, 190));
            start_btn.setTextColor(Color.BLACK);
        }
    }


    private void goSetting() {
        Toast.makeText(MainActivity.this, "请先完成设置", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    Intent notifyIntent;

    private void startNotificationListenService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            notifyIntent = new Intent(MainActivity.this,
                    MyNotificationListenService.class);
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(notifyIntent);

            sendBroadcast(new Intent("startService"));

            if (mReceiver == null) {
                mReceiver = new MyReceiver();
                intentFilter = new IntentFilter();
                intentFilter.addAction(MyNotificationListenService.BROADCAST_ACTION);
                registerReceiver(mReceiver, intentFilter);
            }
            setBtnStatus(1);
            mToolUtils.setIsUpload(true);
        } else {
            Toast.makeText(MainActivity.this, "手机的系统不支持此功能", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    private boolean isNotificationListenersEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    // 判断是否打开了通知监听权限
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        message_tv.setText(stringBuilder.toString());
        setBtnStatus(status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
            NotifyUtils.cancel();
        }

        mToolUtils.setIsUpload(false);
        Log.i("lys", "onDestroy()");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String message = intent.getStringExtra("message");
                if (message != null && !message.equals("")) {
                    message_tv.append(intent.getStringExtra("time") + ":" + intent.getStringExtra("message") + "\n");
                    stringBuilder.append(intent.getStringExtra("time") + ":" + intent.getStringExtra("message") + "\n");
                    if (intent.getIntExtra("isPay", 0) == 1) {
                        Vibrator vibrator = (Vibrator) MainActivity.this.getSystemService(MainActivity.this.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
//                        MessageDialog messageDialog = new MessageDialog(MainActivity.this, R.style.BaseDialog, intent.getStringExtra("message"));
//                        messageDialog.show();
//                    appNotify(1,intent.getStringExtra("message"));
                    }
                }
            }
        }
    }

    /**
     * 上傳監測到的通知
     */
    private void appNotify(int isAliOrWeChart, String content) {
        if (mToolUtils.getisUpload()) {
            if (isAliOrWeChart == 1) {
                if (mToolUtils.getIsAlipay()) {
                    presenter.appNotify(content, mToolUtils.getAlipayCountId(), "alipay", mToolUtils.getMerchantNo(), mToolUtils.getSystemMill(), new StringCallback() {
                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            mLoadingProgressDialog.showDialog();
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.i("lys", "上传支付宝监测数据返回：" + s.toString());
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            mLoadingProgressDialog.dismiss();
                        }

                        @Override
                        public void onAfter(@Nullable String s, @Nullable Exception e) {
                            super.onAfter(s, e);
                            mLoadingProgressDialog.dismiss();
                        }
                    });
                }
            }

            if (isAliOrWeChart == 2) {
                if (mToolUtils.getIsAlipay()) {
                    presenter.appNotify("", mToolUtils.getWeChartCountId(), "wechat", mToolUtils.getMerchantNo(), mToolUtils.getSystemMill(), new StringCallback() {
                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            mLoadingProgressDialog.showDialog();
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.i("lys", "上传微信监测数据返回：" + s.toString());
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                        }

                        @Override
                        public void onAfter(@Nullable String s, @Nullable Exception e) {
                            super.onAfter(s, e);
                            mLoadingProgressDialog.dismiss();
                        }
                    });
                }
            }
        }
    }

//    public static void setData(String  time,String message){
//        message_tv.append(time + ":" + message + "\n");
//        stringBuilder.append(time+ ":" + message + "\n");
//            Vibrator vibrator = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
//            vibrator.vibrate(1000);
//            MessageDialog messageDialog = new MessageDialog(context, R.style.BaseDialog, message);
//            messageDialog.show();
//        }


    /**
     * 该辅助功能开关是否打开了
     *
     * @param accessibilityServiceName：指定辅助服务名字
     * @param context：上下文
     * @return
     */
    private boolean isAccessibilitySettingsOn(String accessibilityServiceName, Context context) {
        int accessibilityEnable = 0;
        String serviceName = context.getPackageName() + "/" + accessibilityServiceName;
        try {
            accessibilityEnable = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        } catch (Exception e) {
            Log.e("lys", "get accessibility enable failed, the err:" + e.getMessage());
        }
        if (accessibilityEnable == 1) {
            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        Log.v("lys", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.d("lys", "Accessibility service disable");
        }
        return false;
    }

    /**
     * 跳转到系统设置页面开启辅助功能
     *
     * @param accessibilityServiceName：指定辅助服务名字
     * @param context：上下文
     */
    private void openAccessibility(String accessibilityServiceName, Context context) {
        if (!isAccessibilitySettingsOn(accessibilityServiceName, context)) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        } else {
//            if(mReceiver==null) {
//                mReceiver = new receiver();
//                intentFilter = new IntentFilter();
//                intentFilter.addAction(MyNotificationListenService.BROADCAST_ACTION);
//                registerReceiver(mReceiver, intentFilter);
//            }
            setBtnStatus(1);
            mToolUtils.setIsUpload(true);
        }
    }
}
