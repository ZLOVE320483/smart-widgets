package com.zlove.widget.view;

import android.view.View;
import android.widget.TextView;

import com.zlove.widget.R;

public class ContentTxtView {

    TextView textView;

    public ContentTxtView(View view) {
        textView = view.findViewById(R.id.content_txt);
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
