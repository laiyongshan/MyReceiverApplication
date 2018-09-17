package com.lys.androidapp.myreceiverapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lys.androidapp.myreceiverapplication.adapter.CountAdapter;
import com.lys.androidapp.myreceiverapplication.bean.CountBean;
import com.lys.androidapp.myreceiverapplication.presenter.Presenter;
import com.lys.androidapp.myreceiverapplication.utils.ColorUtils;
import com.lys.androidapp.myreceiverapplication.utils.ToolUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author lys
 * @time 2018/9/6 17:24
 * @desc:
 */

public class CountDialog extends Dialog {

    RecyclerView count_rv;
    TextView title_tv;
    SwipeRefreshLayout refresh_layout;

    Context context;

    CountAdapter countAdapter;

    List<CountBean.AccountListBean> list=new ArrayList<>();

    Presenter presenter;
    ToolUtils mToolUtils;

    String paytype="";

    interface  CountListener{
        void getCount(String count);
        void getCountId(String countId);
    }

    CountListener mCountListener;

    public CountDialog(@NonNull Context context, int themeResId,String paytype,CountListener mCountListener) {
        super(context, themeResId);
        this.paytype=paytype;
        presenter=new Presenter();
        mToolUtils=new ToolUtils();

        this.context=context;
        this.mCountListener=mCountListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_count);
        title_tv=findViewById(R.id.title_tv);
        if(paytype.equals("alipay")){
            title_tv.setText("选择监控的支付宝账号");
        }else if(paytype.equals("wechat")){
            title_tv.setText("选择监控的微信账号");
        }

        countAdapter=new CountAdapter(list);


        count_rv=findViewById(R.id.count_rv);
        count_rv.setLayoutManager(new LinearLayoutManager(context));
        count_rv.setAdapter(countAdapter);

        refresh_layout=findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeColors(ColorUtils.Colors);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCountList();
            }
        });

        getCountList();
    }

    private void getCountList(){
        presenter.getAccountList(paytype, mToolUtils.getMerchantNo(), new StringCallback() {

            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                refresh_layout.setRefreshing(true);
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.i("lys","获取账号列表返回的数据："+s.toString());
                CountBean countBean=CountBean.objectFromData(s);
                list=countBean.getAccount_list();
                if(list!=null&&!list.isEmpty()) {
                    countAdapter = new CountAdapter(list);
                    countAdapter.notifyDataSetChanged();
                    count_rv.setAdapter(countAdapter);
                    countAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

//                if(paytype.equals("alipay")){
//                    mToolUtils.setAlipayCount(list.get(position).getAccount());
//                    mToolUtils.setAlipayCountId(list.get(position).getId());
//                }else if(paytype.equals("wechat")){
//                    mToolUtils.setWeChartCount(list.get(position).getAccount());
//                    mToolUtils.setWeChartCountId(list.get(position).getId());
//                }
                            mCountListener.getCount(list.get(position).getAccount());
                            mCountListener.getCountId(list.get(position).getId());

                            dismiss();

                        }
                    });

                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                refresh_layout.setRefreshing(false);
            }

            @Override
            public void onAfter(@Nullable String s, @Nullable Exception e) {
                super.onAfter(s, e);
                refresh_layout.setRefreshing(false);
            }
        });
    }
}
