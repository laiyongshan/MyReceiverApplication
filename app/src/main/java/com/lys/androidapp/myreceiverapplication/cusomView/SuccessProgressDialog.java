package com.lys.androidapp.myreceiverapplication.cusomView;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.lys.androidapp.myreceiverapplication.R;


/**
 * Created by HJO on 2018/1/4.
 */

public class SuccessProgressDialog extends AbstractBaseDialog {
    private View mRootView;
//    private TextView login_progressBar_tv;
//     String mText;
     Handler handler;

    public SuccessProgressDialog(Context context) {
        super(context);
        setCenterOpen();
        setDialogWidth(0.4f);
        setCancelable(false);
        handler=new Handler();
    }


    @Override
    protected View getContentView() {
        if (mRootView == null) {
            mRootView = LayoutInflater.from(getContext()).inflate(R.layout.supervise_success_progress_dialog_layout, null);
//            login_progressBar_tv=mRootView.findViewById(R.id.success_progressBar_tv);
        }
        return mRootView;
    }

    public void showDialog(){
//        login_progressBar_tv.setText(mText);
        show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        },2000);
    }

//    public void setProgressText(String text){
//        mText=text;
//    }
}
