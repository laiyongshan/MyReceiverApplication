package com.lys.androidapp.myreceiverapplication;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.lys.androidapp.myreceiverapplication.cusomView.LoadingProgressDialog;
import com.lys.androidapp.myreceiverapplication.presenter.Presenter;
import com.lys.androidapp.myreceiverapplication.utils.ToolUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import java.lang.reflect.Field;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author lys
 * @time 2018/8/29 19:05
 * @desc:
 */

@SuppressLint({"NewApi", "OverrideAbstract"})
public class MyNotificationListenService extends NotificationListenerService {
    public final static String BROADCAST_ACTION="com.lys.message";

    ToolUtils mToolUtils;
    Presenter presenter;

    private PowerManager.WakeLock wakeLock;
    LoadingProgressDialog mLoadingProgressDialog;


    String L_contentTitle="";
    String L_contentText="";
    String L_contentSubtext="";
    String L_title="";
    String L_when="";

    Intent sendIntent=new Intent();

    Notification notification;


    private BroadcastReceiver mStopRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "stopService":
                    stopSelf();
                    stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
//                    BaseActivity.closeApp();
                    break;
                case "startService":
                    // 参数一：唯一的通知标识；参数二：通知消息。
                    startForeground(110, notification);// 开始前台服务
                    break;
            }
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        mToolUtils = new ToolUtils();
        presenter = new Presenter();

        mLoadingProgressDialog = new LoadingProgressDialog(this);
        mLoadingProgressDialog.setProgressText("Loading...");
        mLoadingProgressDialog.setCanceledOnTouchOutside(true);
        mLoadingProgressDialog.setCancelable(true);

        getLock(getApplicationContext());

        // 在API11之后构建Notification的方式
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent nfIntent = new Intent(this, MainActivity.class);

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.logo)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("积分助手") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.logo) // 设置状态栏内的小图标
                .setContentText("积分助手正在后台运行中") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        notification= builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        notification.flags=Notification.FLAG_ONGOING_EVENT;

        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(110, notification);// 开始前台服务

        IntentFilter filter = new IntentFilter("stopService");
        filter.addAction("stop");
        registerReceiver(mStopRecevier, filter);

        Log.i("lys","已开启监测服务");
        sendIntent.setAction(BROADCAST_ACTION);
        sendIntent.putExtra("isPay", 0);
        sendIntent.putExtra("message", "已开启监测服务"+"\n"+"监测服务正在运行中...");
        sendIntent.putExtra("time",""+ ToolUtils.getNowTime());
        sendBroadcast(sendIntent);
//        final Intent finalIntent = sendIntent;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Log.i("lys","MyNotificationListenService监测服务正在运行中...");
                        Thread.sleep(10*1000);
//                        finalIntent.putExtra("isPay", 0);
//                        finalIntent.putExtra("message", "监测服务正在运行中...");
//                        finalIntent.putExtra("time",""+ToolUtils.getNowTime());
//                        sendBroadcast(finalIntent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    long when;

    // 有新的通知
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.e("lys", "get notify");
        Notification n = sbn.getNotification();
        if (n == null) {
            return;
        }
        // 标题和时间
        String title = "";
        if (n.tickerText != null) {
            title = n.tickerText.toString();
        }

        // 其它的信息存在一个bundle中，此bundle在android4.3及之前是私有的，需要通过反射来获取；android4.3之后可以直接获取
        Bundle bundle = null;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // android 4.3
            try {
                Field field = Notification.class.getDeclaredField("extras");
                bundle = (Bundle) field.get(n);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // android 4.3之后
            bundle = n.extras;
        }
        // 内容标题、内容、副内容
        String contentTitle = bundle.getString(Notification.EXTRA_TITLE);
        if (contentTitle == null) {
            contentTitle = "";
        }
        String contentText = bundle.getString(Notification.EXTRA_TEXT);
        if (contentText == null) {
            contentText = "";
        }
        String contentSubtext = bundle.getString(Notification.EXTRA_SUB_TEXT);
        if (contentSubtext == null) {
            contentSubtext = "";
        }
        Log.e("lys", "notify msg: title=" + title + " ,when=" + when
                + " ,contentTitle=" + contentTitle + " ,contentText="
                + contentText + " ,contentSubtext=" + contentSubtext);

        final String finalContentTitle = contentTitle;
        final String finalTitle = title;
        final String finalContentText = contentText;
        final String finalContentSubtext = contentSubtext;
        final long finalwhen=n.when;
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (when != finalwhen) {
                    sendIntent.putExtra("message","notify msg: title=" + finalTitle + " ,when=" + when
                            + " ,contentTitle=" + finalContentTitle + " ,contentText="
                            + finalContentText + " ,contentSubtext=" + finalContentSubtext);
                    sendIntent.putExtra("time", "" + ToolUtils.getNowTime());
                    if (finalContentTitle.contains("支付宝") || finalContentTitle.contains("交易提醒")) {
                        sendIntent.putExtra("isPay", 1);
                        if(mToolUtils.getIsAlipay()&&mToolUtils.getAlipayCountId()!=null&&!mToolUtils.getAlipayCountId().equals("")) {
                            appNotify(1, finalContentText);//上传数据
                            sendBroadcast(sendIntent);
                        }
                    }

                    if(finalContentTitle.contains("微信支付")){
                        if(mToolUtils.getIsWeChat()&&mToolUtils.getWeChartCountId()!=null&&!mToolUtils.getWeChartCountId().equals("")) {
                            sendIntent.putExtra("isPay", 1);
                            appNotify(2, finalContentText);//上传数据
                            sendBroadcast(sendIntent);
                        }
                    } else {
                        sendIntent.putExtra("isPay", 0);
                    }
                }
                when=finalwhen;
            }
        }).start();
    }


    /**
     * 上傳監測到的通知
     */
    private void appNotify(int isAliOrWeChart, String content) {
        if (mToolUtils.getisUpload()) {
            if (isAliOrWeChart == 1) {
                if (mToolUtils.getIsAlipay()&&mToolUtils.getAlipayCountId()!=null&&!mToolUtils.getAlipayCountId().equals("")) {
                    presenter.appNotify(content, mToolUtils.getAlipayCountId(), "alipay", mToolUtils.getMerchantNo(),mToolUtils.getSystemMill(), new StringCallback() {
                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
//                            mLoadingProgressDialog.showDialog();
                        }
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.i("lys", "上传支付宝监测数据返回：" + s.toString());
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
//                            mLoadingProgressDialog.dismiss();
                        }

                        @Override
                        public void onAfter(@Nullable String s, @Nullable Exception e) {
                            super.onAfter(s, e);
//                            mLoadingProgressDialog.dismiss();
                        }
                    });
                }
            }

            if (isAliOrWeChart == 2) {
                if (mToolUtils.getIsWeChat()&&mToolUtils.getWeChartCountId()!=null&&!mToolUtils.getWeChartCountId().equals("")) {
                    presenter.appNotify(content, mToolUtils.getWeChartCountId(), "wechat", mToolUtils.getMerchantNo(),mToolUtils.getSystemMill(), new StringCallback() {
                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
//                            mLoadingProgressDialog.showDialog();
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
//                            mLoadingProgressDialog.dismiss();
                        }
                    });
                }
            }
        }
    }



    // 通知被删除了
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e("lys", "delete notify");
        sendIntent.putExtra("message","delete notify" );
        sendIntent.putExtra("time",""+ToolUtils.getNowTime());
        sendBroadcast(sendIntent);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("lys", "关闭");
//        wakeLock.release();
//        wakeAndUnlock(false);
        releaseLock();
        Toast.makeText(this, "_已关闭监测服务_", Toast.LENGTH_LONG).show();
        sendIntent.putExtra("isPay", 0);
        sendIntent.putExtra("message", "已停止服务");
        sendIntent.putExtra("time", "" + ToolUtils.getNowTime());
        sendBroadcast(sendIntent);
        Log.i("lys", "已停止服务！");
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知

        unregisterReceiver(mStopRecevier);
    }


    /**
     * 同步方法   得到休眠锁
     *
     * @param context
     * @return
     */
    synchronized private void getLock(Context context) {
        if (wakeLock == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, MyAccessibilityServices.class.getName());
            wakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis((System.currentTimeMillis()));
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour >= 23 || hour <= 6) {
                wakeLock.acquire(5000);
            } else {
                wakeLock.acquire(300000);
            }
        }
        Log.v("lys", "get lock");
    }

    synchronized private void releaseLock() {
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wakeLock.release();
                Log.v("lys", "release lock");
            }
            wakeLock = null;
        }
    }
}
