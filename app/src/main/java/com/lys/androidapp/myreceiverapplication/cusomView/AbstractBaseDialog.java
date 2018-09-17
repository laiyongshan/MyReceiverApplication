package com.lys.androidapp.myreceiverapplication.cusomView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lys.androidapp.myreceiverapplication.R;


/**
 * 功能描述：
 * Created by Administrator on 2018/1/2.
 */

public abstract class AbstractBaseDialog extends Dialog {

    // 上下文
    protected Context context;
    // 窗口默认的宽度比例
    public  float defaultWindowWidthPercent = 0.8f;

    /**
     * 获取content view
     *
     * @return
     */
    protected abstract View getContentView();

    public AbstractBaseDialog(Context context) {
        this(context, R.style.BaseDialog);
    }

    public AbstractBaseDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        setContentView(getContentView());
        initWindow();
    }

    public AbstractBaseDialog(Context context, int theme, float windowWidthPercent) {
        super(context, theme);
        this.context = context;
        this.defaultWindowWidthPercent=windowWidthPercent;
        setContentView(getContentView());
        initWindow();
    }

    private void initWindow() {
        setBaseAnim();
        setDialogWidth(defaultWindowWidthPercent);
    }

    /**
     * 设置最基础的动画
     */
    @SuppressLint("NewApi")
    private void setBaseAnim() {
        /* 设置弹出窗的show和dismiss的动画 */
        getWindow().setWindowAnimations(R.style.BaseDialogAnim);
    }

    /**
     * 设置弹出和消失动画
     *
     * @param resId 动画的资源Id
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    protected void setShowAndDismissAnim(int resId) {
        /* 设置弹出窗的show和dismiss的动画 */
        getWindow().setWindowAnimations(resId);
    }

    /**
     * 清除弹出和消失动画
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    protected void clearAnim() {
        getWindow().setWindowAnimations(0);
    }

    /**
     * 设置弹出框的宽度
     * @param percent
     */
    protected void setDialogWidth(float percent) {
        /* 设置Window的宽高 */
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        // 高度为适应高度
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        // 屏幕宽度
        int width = wm.getDefaultDisplay().getWidth();
        // 屏幕宽度乘以比例就得出最终的Dialog的宽度
        params.width = (int) (width * percent);
        window.setAttributes(params);
    }

    /**
     * 设置弹出框的高度
     * @param percent
     */
    protected void setDialogHeight(float percent) {
        /* 设置Window的宽高 */
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        // 屏幕高度
        int height = wm.getDefaultDisplay().getHeight();
        // 屏幕高度乘以比例就得出最终的Dialog的高度
        params.height = (int) (height * percent);
        window.setAttributes(params);
    }

    /**
     * 设置为全屏
     */
    protected void setFullScreen() {
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    /**
     * 设置弹出框是否靠底边
     */
    protected void setBottomOpen() {
        /* 设置Window的宽高 */
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        // 高度为适应高度
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    protected void setCenterOpen() {
        /* 设置Window的宽高 */
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        // 高度为适应高度
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
}
