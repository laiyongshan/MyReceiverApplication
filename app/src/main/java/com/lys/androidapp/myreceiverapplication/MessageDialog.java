package com.lys.androidapp.myreceiverapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

/**
 * @author lys
 * @time 2018/8/30 18:06
 * @desc:
 */

public class MessageDialog extends Dialog {

    TextView message_tv;
    String message;

    public MessageDialog(@NonNull Context context, int themeResId,String message) {
        super(context, themeResId);
        this.message=message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message);
        message_tv=findViewById(R.id.message_tv);
        message_tv.setText(message+"");
    }
}
