package com.lys.androidapp.myreceiverapplication.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.lys.androidapp.myreceiverapplication.R;

/**
 * @author lys
 * @time 2018/9/7 17:29
 * @desc:
 */

public class NotifyUtils {

    public static NotificationManager nm;

    public static void sendNotify(Context context) {
        String service = Context.NOTIFICATION_SERVICE;
        nm = (NotificationManager) context.getSystemService(service); // get system
        Notification n = new Notification();
        n.icon = R.mipmap.logo;
        n.tickerText = "test";
        n.when = System.currentTimeMillis();


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)//创建通知消息实例
                .setContentTitle("监测服务")
                .setContentText("服务正在后台运行中...")
                .setWhen(System.currentTimeMillis())//通知栏显示时间
                .setSmallIcon(R.mipmap.logo)//通知栏小图标
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo))//通知栏下拉是图标
                .setPriority(NotificationCompat.PRIORITY_MAX)//设置通知消息优先级
                .setAutoCancel(false);//设置点击通知栏消息后，通知消息自动消失



        n = builder.build();
        //如果不想被清理加上下面这两行代码的任何一行都可以
//        n.flags |= Notification.FLAG_NO_CLEAR;
        n.flags=Notification.FLAG_ONGOING_EVENT;

        //id为通知栏消息标识符，每个id都是不同的
        nm.notify(1, n);
    }

    public static void cancel() {
        if (nm != null) {
            nm.cancel(1);
        }
    }

}
