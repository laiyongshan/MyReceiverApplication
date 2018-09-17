package com.lys.androidapp.myreceiverapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lys.androidapp.myreceiverapplication.bean.LoginBean;
import com.lys.androidapp.myreceiverapplication.cusomView.LoadingProgressDialog;
import com.lys.androidapp.myreceiverapplication.presenter.Presenter;
import com.lys.androidapp.myreceiverapplication.utils.NetWorkUtil;
import com.lys.androidapp.myreceiverapplication.utils.ToolUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author lys
 * @time 2018/9/5 17:11
 * @desc:
 */

public class LoginActivity extends Activity {

    EditText username_et,psw_et;
    Button login_btn;
    TextView toapi_tv;

    String username="";
    String pwd="";

    Presenter mPresenter;
    ToolUtils mToolUtils;

    LoadingProgressDialog mLoadingProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPresenter=new Presenter();
        mToolUtils=new ToolUtils();

        mLoadingProgressDialog=new LoadingProgressDialog(this);
        mLoadingProgressDialog.setProgressText("Loading...");
        mLoadingProgressDialog.setCanceledOnTouchOutside(true);
        mLoadingProgressDialog.setCancelable(true);

        toapi_tv=findViewById(R.id.toapi_tv);
        toapi_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ApiActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
                finish();
            }
        });

        username_et=findViewById(R.id.username_et);
        username_et.setText(mToolUtils.getNameAndPss()[0]);
        psw_et=findViewById(R.id.psw_et);
        psw_et.setText(mToolUtils.getNameAndPss()[1]);
        login_btn=findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetWorkUtil.isNetWorkAvailable(LoginActivity.this)){
                    if(!username_et.getText().toString().trim().equals("")&&!psw_et.getText().toString().trim().equals("")){
                        username=username_et.getText().toString().trim();
                        pwd=psw_et.getText().toString().trim();
                        mPresenter.login(username, pwd, new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                Log.i("lys","登录返回的数据："+s.toString());
                                LoginBean loginBean=LoginBean.objectFromData(s.toString()+"");
                                if(loginBean.getCode().equals("1")){//登录成功
                                    mToolUtils.setIsLogin(true);
                                    mToolUtils.setNameAndPss(username+"",pwd+"");
                                    mToolUtils.setLoginSecretKey(loginBean.getLogin_secret_key()+"");
                                    mToolUtils.setMerchantNo(loginBean.getMerchant_no()+"");
                                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    mToolUtils.setIsLogin(false);
                                    Toast.makeText(LoginActivity.this,""+loginBean.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                mLoadingProgressDialog.showDialog();
                            }

                            @Override
                            public void onAfter(@Nullable String s, @Nullable Exception e) {
                                super.onAfter(s, e);
                                mLoadingProgressDialog.dismiss();
                            }
                        });
                    }else{
                        Toast.makeText(LoginActivity.this,"账号密码不能为空",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"网络不可用！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
