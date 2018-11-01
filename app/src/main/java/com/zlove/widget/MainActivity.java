package com.zlove.widget;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zlove.widget.component.BannerWidget;
import com.zlove.widget.component.ContentTxtWidget;
import com.zlove.widget.component.WidgetConstants;
import com.zlove.widget.library.DataCenter;
import com.zlove.widget.library.KVData;
import com.zlove.widget.library.ViewModelProvidersWrapper;
import com.zlove.widget.library.WidgetManager;

public class MainActivity extends AppCompatActivity implements Observer<KVData> {

    private DataCenter mDataCenter;
    private WidgetManager mWidgetManager;
    private BannerWidget mBannerWidget;
    private ContentTxtWidget mContentTxtWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View rootView = findViewById(R.id.root_view);

        mDataCenter = DataCenter.create(ViewModelProvidersWrapper.of(this), this);
        mDataCenter.observe(WidgetConstants.DATA_BANNER, this)
                    .observe(WidgetConstants.DATA_TEXT, this);
        mWidgetManager = WidgetManager.of(this, rootView);
        mWidgetManager.setDataCenter(mDataCenter);
        mBannerWidget = new BannerWidget();
        mContentTxtWidget = new ContentTxtWidget();
        mWidgetManager.bind(rootView.findViewById(R.id.banner_container), mBannerWidget)
                        .bind(rootView.findViewById(R.id.content_txt_layout), mContentTxtWidget);

        int[] colorsRes = {R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark};
        mDataCenter.put(WidgetConstants.DATA_BANNER, colorsRes);

        rootView.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] colorsRes2 = {android.R.color.holo_blue_bright, android.R.color.holo_blue_dark, android.R.color.holo_blue_light};
                mDataCenter.put(WidgetConstants.DATA_BANNER, colorsRes2);
            }
        });

        rootView.findViewById(R.id.set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataCenter.put(WidgetConstants.DATA_TEXT, "I am a boy");
            }
        });
    }

    @Override
    public void onChanged(@Nullable KVData kvData) {
        if (kvData == null) {
            return;
        }
        switch (kvData.getKey()) {
            case WidgetConstants.DATA_BANNER:
                Log.d("ZLOVE", "banner");
                break;
            case WidgetConstants.DATA_TEXT:
                Log.d("ZLOVE", "text");
                break;
        }
    }
}
