package com.lys.androidapp.myreceiverapplication.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lys.androidapp.myreceiverapplication.R;
import com.lys.androidapp.myreceiverapplication.bean.CountBean;

import java.util.List;

/**
 * @author lys
 * @time 2018/9/6 17:38
 * @desc:
 */

public class CountAdapter extends BaseQuickAdapter<CountBean.AccountListBean,BaseViewHolder> {
    public CountAdapter(@Nullable List<CountBean.AccountListBean> data) {
        super(R.layout.item_count, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CountBean.AccountListBean item) {
        ((TextView)helper.getView(R.id.count_tv)).setText(item.getAccount()+"");
    }
}
