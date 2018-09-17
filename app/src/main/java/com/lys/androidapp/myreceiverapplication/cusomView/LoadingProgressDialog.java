package com.lys.androidapp.myreceiverapplication.cusomView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lys.androidapp.myreceiverapplication.R;



/**
 * Created by HJO on 2018/1/4.
 */

public class LoadingProgressDialog extends AbstractBaseDialog {
    private View mRootView;
    private TextView login_progressBar_tv;
     String mText;

    public LoadingProgressDialog(Context context) {
        super(context);
        setCenterOpen();
        setDialogWidth(0.4f);
        setCancelable(false);
    }


    @Override
    protected View getContentView() {
        if (mRootView == null) {
            mRootView = LayoutInflater.from(getContext()).inflate(R.layout.supervise_login_progress_dialog_layout, null);
            login_progressBar_tv= (TextView) mRootView.findViewById(R.id.login_progressBar_tv);
        }
        return mRootView;
    }

    public void showDialog(){
        login_progressBar_tv.setText(mText);
        show();
    }

    public void setProgressText(String text){
        mText=text;
    }
}
