package com.zlove.widget.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zlove.widget.R;

public class BannerView {

    Context mContext;
    ViewPager viewPager;
    BannerViewPagerAdapter pagerAdapter;

    public BannerView(View view) {
        mContext = view.getContext();
        viewPager = view.findViewById(R.id.viewpager);
    }

    public void bindBannerData(int[] colors) {
        if (pagerAdapter == null) {
            pagerAdapter = new BannerViewPagerAdapter(mContext);
            viewPager.setAdapter(pagerAdapter);
        }
        pagerAdapter.setData(colors);
    }

    static class BannerViewPagerAdapter extends PagerAdapter {

        private int[] colorRes;
        private Context mContext;

        BannerViewPagerAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(int[] colorRes) {
            this.colorRes = colorRes;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return colorRes == null ? 0 : colorRes.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(colorRes[position]);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
