package com.lys.androidapp.myreceiverapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/10/15
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public abstract class BaseActivity extends FragmentActivity {

    public static ArrayList<Activity> mActivityList = new ArrayList<>();

    public void startActivity(Class<? extends Activity> target) {
        Intent intent = new Intent(this, target);
        startActivity(intent);
    }

    public void startActivityNoAnim(Class<? extends Activity> target) {
        Intent intent = new Intent(this, target);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void closeActivity() {
        finish();
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityList.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityList.remove(this);
    }

    public static void closeApp() {
        for (Activity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);

    }
}
