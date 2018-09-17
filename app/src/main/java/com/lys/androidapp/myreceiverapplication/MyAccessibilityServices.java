package com.lys.androidapp.myreceiverapplication;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.lys.androidapp.myreceiverapplication.cusomView.LoadingProgressDialog;
import com.lys.androidapp.myreceiverapplication.presenter.Presenter;
import com.lys.androidapp.myreceiverapplication.utils.ToolUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author lys
 * @time 2018/9/3 18:13
 * @desc:
 */

public class MyAccessibilityServices extends AccessibilityService {

    ToolUtils mToolUtils;
    Presenter presenter;

    private boolean canGet = false;//能否点击红包
    private boolean enableKeyguard = true;//默认有屏幕锁

    //窗口状态
    private static final int WINDOW_NONE = 0;
    private static final int WINDOW_LUCKYMONEY_RECEIVEUI = 1;
    private static final int WINDOW_LUCKYMONEY_DETAIL = 2;
    private static final int WINDOW_LAUNCHER = 3;
    private static final int WINDOW_OTHER = -1;
    //当前窗口
    private int mCurrentWindow = WINDOW_NONE;

    //锁屏、解锁相关
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    //唤醒屏幕相关
    private PowerManager pm;
    private PowerManager.WakeLock wl = null;

    private PowerManager.WakeLock wakeLock;

    LoadingProgressDialog mLoadingProgressDialog;

    //播放提示声音
    private MediaPlayer player;

    public void playSound(Context context) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        //夜间不播放提示音
        if (hour > 7 && hour < 22) {
            player.start();
        }
    }

    //唤醒屏幕和解锁
    private void wakeAndUnlock(boolean unLock) {
        if (unLock) {
            //若为黑屏状态则唤醒屏幕
            if (!pm.isScreenOn()) {
                //获取电源管理器对象，ACQUIRE_CAUSES_WAKEUP这个参数能从黑屏唤醒屏幕
                wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "bright");
                //点亮屏幕
                wl.acquire();
                Log.i("demo", "亮屏");
            }
            //若在锁屏界面则解锁直接跳过锁屏
            if (km.inKeyguardRestrictedInputMode()) {
                //设置解锁标志，以判断抢完红包能否锁屏
                enableKeyguard = false;
                //解锁
                kl.disableKeyguard();
                Log.i("demo", "解锁");
            }
        } else {
            //如果之前解过锁则加锁以恢复原样
            if (!enableKeyguard) {
                //锁屏
                kl.reenableKeyguard();
                Log.i("demo", "加锁");
            }
            //若之前唤醒过屏幕则释放之使屏幕不保持常亮
            if (wl != null) {
                wl.release();
                wl = null;
                Log.i("demo", "关灯");
            }
        }
    }

    //通过文本查找节点
    public AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    //模拟点击事件
    public void performClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }

    //模拟返回事件
    public void performBack(AccessibilityService service) {
        if (service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    Intent sendIntent = new Intent();

    //实现辅助功能
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.i("lys", Integer.toString(eventType));
        switch (eventType) {
            //第一步：监听通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        final String content = text.toString();
//                        Log.i("lys", "text:" + content + "\n" + event.getParcelableData().toString() + "\n" + event.getPackageName().toString());
                        if ((content.contains("支付宝") || content.contains("交易提醒") || (content.contains("成功收款") && content.contains("元")) || content.contains("付款"))) {

                            appNotify(1, content);//支付宝

                            sendIntent.putExtra("message", "notify msg: " + content);
                            sendIntent.putExtra("isPay", 1);
                            sendIntent.putExtra("time", "" + ToolUtils.getNowTime());
                            sendBroadcast(sendIntent);
                        } else if ((content.contains("微信") || content.contains("交易提醒") || (content.contains("成功收款") && content.contains("元")) || content.contains("付款"))) {
                            appNotify(2, content);//微信
                            sendIntent.putExtra("message", "notify msg: " + content);
                            sendIntent.putExtra("isPay", 1);
                            sendIntent.putExtra("time", "" + ToolUtils.getNowTime());
                            sendBroadcast(sendIntent);
                        }

                        //收到通知提醒
                        if (content.contains("支付宝") || content.contains("交易提醒") || (content.contains("成功收款") && content.contains("元")) || content.contains("付款")) {
                            Log.i("demo", "canGet=true");
                            canGet = true;
                            try {
                                Notification notification = (Notification) event.getParcelableData();
                                PendingIntent pendingIntent = notification.contentIntent;
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
//                            }
                            break;
                        }
                    }
                }
                break;
            //第二步：监听是否进入消息界面
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    mCurrentWindow = WINDOW_LAUNCHER;
                    //开始抢红包
                    Log.i("demo", "准备抢红包...");
                    getPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    mCurrentWindow = WINDOW_LUCKYMONEY_RECEIVEUI;
                    //开始打开红包
                    Log.i("demo", "打开红包");
                    openPacket();
                    wakeAndUnlock(false);
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    mCurrentWindow = WINDOW_LUCKYMONEY_DETAIL;
                    //返回以方便下次收红包
                    Log.i("demo", "返回");
                    performBack(this);
                } else {
                    mCurrentWindow = WINDOW_OTHER;
                }
                break;

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (mCurrentWindow != WINDOW_LAUNCHER) { //不在聊天界面或聊天列表，不处理
                    return;
                }
                if (canGet) {
                    getPacket();
                }
                break;
        }
    }

    /**
     * 上傳監測到的通知
     */
    private void appNotify(int isAliOrWeChart, String content) {
        if (mToolUtils.getisUpload()) {
            if (isAliOrWeChart == 1) {
                if (mToolUtils.getIsAlipay()) {
                    presenter.appNotify(content, mToolUtils.getAlipayCountId(), "alipay", mToolUtils.getMerchantNo(),mToolUtils.getSystemMill(), new StringCallback() {
                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            mLoadingProgressDialog.showDialog();
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.i("lys", "上传支付宝监测数据返回：" + s.toString());
                            sendIntent.putExtra("message", "上传返回的数据: " + s.toString());
                            sendIntent.putExtra("isPay", 0);
                            sendIntent.putExtra("time", "" + ToolUtils.getNowTime());
                            sendBroadcast(sendIntent);
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

            if (isAliOrWeChart == 2) {
                if (mToolUtils.getIsAlipay()) {
                    presenter.appNotify("", mToolUtils.getWeChartCountId(), "wechat", mToolUtils.getMerchantNo(),mToolUtils.getSystemMill(), new StringCallback() {
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


    //找到红包并点击
    @SuppressLint("NewApi")
    private void getPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        // 找到领取红包的点击事件
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");

        if (list != null) {
            if (list.isEmpty()) {
                Log.i("demp", "领取列表为空");
                // 从消息列表查找红包
                AccessibilityNodeInfo node = findNodeInfosByText(nodeInfo, "[微信红包]");
                if (node != null) {
                    canGet = true;
                    performClick(node);
                }
            } else {
                if (canGet) {
                    //最新的红包领起
                    AccessibilityNodeInfo node = list.get(list.size() - 1);
                    performClick(node);
                    Log.i("demo", "canGet=false");
                    canGet = false;
                }
            }
        }
    }

    //打开红包
    @SuppressLint("NewApi")
    private void openPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        Log.i("demo", "查找打开按钮...");
        AccessibilityNodeInfo targetNode = null;

        //如果红包已经被抢完则直接返回
        targetNode = findNodeInfosByText(nodeInfo, "看看大家的手气");
        if (targetNode != null) {
            performBack(this);
            return;
        }
        //通过组件名查找开红包按钮，还可通过组件id直接查找但需要知道id且id容易随版本更新而变化，旧版微信还可直接搜“開”字找到按钮
        if (targetNode == null) {
            Log.i("lys", "打开按钮中...");
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo node = nodeInfo.getChild(i);
                if ("android.widget.Button".equals(node.getClassName())) {
                    targetNode = node;
                    break;
                }
            }
        }
        //若查找到打开按钮则模拟点击
        if (targetNode != null) {
            final AccessibilityNodeInfo n = targetNode;
            performClick(n);
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "监测服务被中断啦~", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        mToolUtils = new ToolUtils();
        presenter = new Presenter();

//        //创建PowerManager对象
//        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        //保持cpu一直运行，不管屏幕是否黑屏
//        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
//        wakeLock.acquire();

        mLoadingProgressDialog = new LoadingProgressDialog(this);
        mLoadingProgressDialog.setProgressText("Loading...");
        mLoadingProgressDialog.setCanceledOnTouchOutside(true);
        mLoadingProgressDialog.setCancelable(true);

//        Log.i("demo", "开启");
//        //获取电源管理器对象
//        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        //得到键盘锁管理器对象
//        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        //初始化一个键盘锁管理器对象
//        kl = km.newKeyguardLock("unLock");

//        Toast.makeText(this, "_已开启监测服务_", Toast.LENGTH_LONG).show();


        Log.i("lys", "已开启监测服务");
        sendIntent.setAction(MyNotificationListenService.BROADCAST_ACTION);
        final Intent finalIntent = sendIntent;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Log.i("lys", "监测服务正在运行中...");
                        finalIntent.putExtra("isPay", 0);
                        finalIntent.putExtra("message", "监测服务正在运行中...");
                        finalIntent.putExtra("time", "" + ToolUtils.getNowTime());
                        sendBroadcast(finalIntent);
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i("lys", "sevice is onStart()!");
    }

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
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_round)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("监测服务") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("监测服务正在后台运行中") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        notification.flags=Notification.FLAG_ONGOING_EVENT;

        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(110, notification);// 开始前台服务

        return super.onStartCommand(intent, flags, startId);
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
