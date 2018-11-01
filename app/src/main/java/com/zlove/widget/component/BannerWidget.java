package com.zlove.widget.component;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.view.View;

import com.zlove.widget.library.KVData;
import com.zlove.widget.library.Widget;
import com.zlove.widget.view.BannerView;

public class BannerWidget extends Widget implements Observer<KVData> {

    BannerView mBannerView;

    @Override
    public void onCreate() {
        mDataCenter.observe(WidgetConstants.DATA_BANNER, this);
        super.onCreate();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mBannerView = new BannerView(view);
        int[] colorsRes = mDataCenter.get(WidgetConstants.DATA_BANNER);
        bindBannerData(colorsRes);
    }

    private void bindBannerData(int[] colorsRes) {
        if (colorsRes == null || colorsRes.length == 0) {
            mContentView.setVisibility(View.GONE);
        } else {
            mContentView.setVisibility(View.VISIBLE);
            mBannerView.bindBannerData(colorsRes);
        }
    }

    @Override
    public void onChanged(@Nullable KVData kvData) {
        if (mBannerView == null) {
            return;
        }
        if (kvData != null) {
            int[] colorsRes = kvData.getData();
            bindBannerData(colorsRes);
        }
    }
}
