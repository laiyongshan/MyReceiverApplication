package com.lys.androidapp.myreceiverapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lys.androidapp.myreceiverapplication.cusomView.SuccessProgressDialog;
import com.lys.androidapp.myreceiverapplication.utils.ToolUtils;

/**
 * @author lys
 * @time 2018/9/5 17:43
 * @desc:
 */

public class SettingActivity extends Activity {

    RelativeLayout set_api_layout,exit_layout,save_start_layout;
    CheckBox alipay_cb,weixin_cb;
    TextView alipaycount_tv,weixincont_tv;

    ImageView back_iv;

    String AlipayCount;
    String AlipayCountId;
    String WeChartCount;
    String WeChartCountId;

    ToolUtils mToolUtils;

    SuccessProgressDialog successProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mToolUtils=new ToolUtils();
        initView();
    }

    private void initView(){
        successProgressDialog=new SuccessProgressDialog(SettingActivity.this);

        AlipayCount=mToolUtils.getAlipayCount()+"";
        AlipayCountId=mToolUtils.getAlipayCountId()+"";
        WeChartCount=mToolUtils.getWeChartCount()+"";
        WeChartCountId=mToolUtils.getWeChartCountId()+"";

        back_iv=findViewById(R.id.back_iv);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        set_api_layout=findViewById(R.id.set_api_layout);
        set_api_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingActivity.this,ApiActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
                finish();
            }
        });

        exit_layout=findViewById(R.id.exit_layout);
        exit_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolUtils.setIsLogin(false);
                Intent intent=new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        alipay_cb=findViewById(R.id.alipay_cb);
        alipay_cb.setChecked(mToolUtils.getIsAlipay());
        alipay_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mToolUtils.setIsAlipay(isChecked);
            }
        });

        weixin_cb=findViewById(R.id.weixin_cb);
        weixin_cb.setChecked(mToolUtils.getIsWeChat());
        weixin_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mToolUtils.setIsWeChat(isChecked);
            }
        });



        alipaycount_tv=findViewById(R.id.alipaycount_tv);
        if(!mToolUtils.getAlipayCount().equals("null")) {
            alipaycount_tv.setText(mToolUtils.getAlipayCount() + "");
        }
        alipaycount_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountDialog  countDialog=new CountDialog(SettingActivity.this, R.style.BaseDialog, "alipay", new CountDialog.CountListener() {
                    @Override
                    public void getCount(String count) {
                        AlipayCount=count;
                        alipaycount_tv.setText(count+"");
                    }

                    @Override
                    public void getCountId(String countId) {
                        AlipayCountId=countId;
                    }
                });
                countDialog.show();
            }
        });
        weixincont_tv=findViewById(R.id.weixincount_tv);
        if(!mToolUtils.getWeChartCount().equals("null")) {
            weixincont_tv.setText(mToolUtils.getWeChartCount() + "");
        }
        weixincont_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountDialog  countDialog=new CountDialog(SettingActivity.this, R.style.BaseDialog, "wechat", new CountDialog.CountListener() {
                    @Override
                    public void getCount(String count) {
                        WeChartCount=count;
                        weixincont_tv.setText(""+count);
                    }

                    @Override
                    public void getCountId(String countId) {
                        WeChartCountId=countId;
                    }
                });
                countDialog.show();
            }
        });

        save_start_layout=findViewById(R.id.save_start_layout);
        save_start_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolUtils.setIsAlipay(alipay_cb.isChecked());
                mToolUtils.setIsWeChat(weixin_cb.isChecked());
                mToolUtils.setAlipayCount(AlipayCount+"");
                mToolUtils.setAlipayCountId(AlipayCountId+"");
                mToolUtils.setWeChartCount(WeChartCount+"");
                mToolUtils.setWeChartCountId(WeChartCountId+"");

                finish();
            }
        });
    }
}
