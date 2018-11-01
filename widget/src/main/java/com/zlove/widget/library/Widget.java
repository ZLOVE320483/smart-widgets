package com.zlove.widget.library;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStore;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public abstract class Widget implements LifecycleObserver, ViewModelStoreOwner {

    protected interface WidgetCallback {

        void startActivity(Intent intent);

        void startActivity(Intent intent, @Nullable Bundle options);

        void startActivityForResult(Intent intent, int requestCode);

        void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options);

        <T extends ViewModel> T getViewModel(Class<T> clazz);

        <T extends ViewModel> T getViewModel(Class<T> clazz, @NonNull ViewModelProvider.Factory factory);

        <T extends ViewModel> T getMyViewModel(Class<T> clazz, @NonNull ViewModelStoreOwner viewModelStoreOwner);

        <T extends ViewModel> T getMyViewModel(Class<T> clazz, @NonNull ViewModelStoreOwner viewModelStoreOwner,
                                               @NonNull ViewModelProvider.Factory factory);

        LifecycleOwner getLifecycleOwner();

        Activity getActivity();

        WidgetManager getWidgetManager();
    }

    protected Context mContext;

    /**
     * 如果使用bind, mContainerView == mContentView;
     */
    protected View mContainerView;

    protected View mContentView;

    protected DataCenter mDataCenter;

    private WidgetCallback mWidgetCallback;

    private boolean mIsViewValid;

    boolean mIsDestroyed;

    private ViewModelStore mViewModelStore;

    protected @LayoutRes
    int getLayoutId() {
        return 0;
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        mIsViewValid = true;
        mIsDestroyed = false;
        onBindView(mContentView);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        mIsViewValid = false;
        mIsDestroyed = true;
        if (mViewModelStore != null) {
            mViewModelStore.clear();
        }
    }

    protected void onBindView(View view) {

    }

    protected boolean isViewValid() {
        return mIsViewValid;
    }

    protected void setWidgetCallback(WidgetCallback widgetCallback) {
        this.mWidgetCallback = widgetCallback;
    }

    protected void startActivity(Intent intent) {
        mWidgetCallback.startActivity(intent);
    }

    protected void startActivity(Intent intent, @Nullable Bundle options) {
        mWidgetCallback.startActivity(intent, options);
    }

    protected void startActivityForResult(Intent intent, int requestCode) {
        mWidgetCallback.startActivityForResult(intent, requestCode);
    }

    protected Activity getActivity() {
        return mWidgetCallback.getActivity();
    }

    protected void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        mWidgetCallback.startActivityForResult(intent, requestCode, options);
    }

    protected <T extends ViewModel> T getViewModel(Class<T> clazz) {
        return mWidgetCallback.getViewModel(clazz);
    }

    protected <T extends ViewModel> T getViewModel(Class<T> clazz, @NonNull ViewModelProvider.Factory factory) {
        return mWidgetCallback.getViewModel(clazz, factory);
    }

    protected <T extends ViewModel> T getMyViewModel(Class<T> clazz) {
        return mWidgetCallback.getMyViewModel(clazz, this);
    }

    protected <T extends ViewModel> T getMyViewModel(Class<T> clazz, @NonNull ViewModelProvider.Factory factory) {
        return mWidgetCallback.getMyViewModel(clazz, this, factory);
    }

    protected LifecycleOwner getLifecycleOwner() {
        return mWidgetCallback.getLifecycleOwner();
    }

    protected WidgetManager getWidgetManager() {
        return mWidgetCallback.getWidgetManager();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * Returns owned {@link ViewModelStore}
     *
     * @return a {@code ViewModelStore}
     */
    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (mViewModelStore == null) {
            mViewModelStore = new ViewModelStore();
        }
        return mViewModelStore;
    }
}
