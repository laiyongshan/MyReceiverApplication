package com.lys.androidapp.myreceiverapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lys.androidapp.myreceiverapplication.utils.ToolUtils;

/**
 * @author lys
 * @time 2018/9/5 16:42
 * @desc:
 */

public class ApiActivity extends Activity {

    EditText api_et;
    Button save_api_btn;

    ToolUtils mToolUtils;

    int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        mToolUtils=new ToolUtils();

        type=getIntent().getIntExtra("type",0);

        if(type!=1) {
            if (mToolUtils.getApi() != null && !mToolUtils.getApi().equals("")) {
                if (mToolUtils.getIsLogin()) {
                    Intent intent = new Intent(ApiActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ApiActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }

        api_et=findViewById(R.id.api_et);
        api_et.setText(mToolUtils.getApi()+"");
        save_api_btn=findViewById(R.id.save_api_btn);
        save_api_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(api_et.getText().toString().trim().equals("")){
                    Toast.makeText(ApiActivity.this,"请输入Api地址",Toast.LENGTH_SHORT).show();
                }else{
                    if(Patterns.WEB_URL.matcher(api_et.getText().toString().trim()).matches()) {
                        mToolUtils.setApi(api_et.getText().toString().trim());
                        Intent intent = new Intent(ApiActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(ApiActivity.this, "请输入有效的服务器地址", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
