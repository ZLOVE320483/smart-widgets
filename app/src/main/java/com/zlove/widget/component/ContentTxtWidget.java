package com.zlove.widget.component;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.view.View;

import com.zlove.widget.library.KVData;
import com.zlove.widget.library.Widget;
import com.zlove.widget.view.ContentTxtView;

public class ContentTxtWidget extends Widget implements Observer<KVData> {

    ContentTxtView contentTxtView;

    @Override
    public void onCreate() {
        mDataCenter.observe(WidgetConstants.DATA_TEXT, this);
        super.onCreate();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        contentTxtView = new ContentTxtView(view);
    }

    @Override
    public void onChanged(@Nullable KVData kvData) {
        if (kvData == null) {
            return;
        }
        String txt = kvData.getData();
        contentTxtView.setText(txt);
    }
}
