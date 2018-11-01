package com.zlove.widget.library;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.AsyncLayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WidgetManager extends Fragment {

    private static final String TAG = WidgetManager.class.getCanonicalName();

    private Widget.WidgetCallback widgetCallback = new Widget.WidgetCallback() {

        @Override
        public void startActivity(Intent intent) {
            WidgetManager.this.startActivity(intent);
        }

        @Override
        public void startActivity(Intent intent, @Nullable Bundle options) {
            WidgetManager.this.startActivity(intent, options);
        }

        @Override
        public void startActivityForResult(Intent intent, int requestCode) {
            WidgetManager.this.startActivityForResult(intent, requestCode);
        }

        @Override
        public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
            WidgetManager.this.startActivityForResult(intent, requestCode, options);
        }

        @Override
        public <T extends ViewModel> T getViewModel(Class<T> clazz) {
            return ViewModelProvidersWrapper.of(WidgetManager.this).get(clazz);
        }

        @Override
        public <T extends ViewModel> T getViewModel(Class<T> clazz, @NonNull ViewModelProvider.Factory factory) {
            return ViewModelProvidersWrapper.of(WidgetManager.this, factory).get(clazz);
        }

        @Override
        public <T extends ViewModel> T getMyViewModel(Class<T> clazz, @NonNull ViewModelStoreOwner viewModelStoreOwner) {
            return ViewModelProvidersWrapper.of(WidgetManager.this, viewModelStoreOwner).get(clazz);
        }

        @Override
        public <T extends ViewModel> T getMyViewModel(Class<T> clazz,
                                                      @NonNull ViewModelStoreOwner viewModelStoreOwner,
                                                      @NonNull ViewModelProvider.Factory factory) {
            return ViewModelProvidersWrapper.of(WidgetManager.this, viewModelStoreOwner, factory).get(clazz);
        }

        @Override
        public LifecycleOwner getLifecycleOwner() {
            return WidgetManager.this;
        }

        @Override
        public Activity getActivity() {
            return WidgetManager.this.getActivity();
        }

        @Override
        public WidgetManager getWidgetManager() {
            return WidgetManager.this;
        }
    };

    private Fragment parentFragment;
    private View contentView;
    private Context context;
    private AsyncLayoutInflater asyncLayoutInflater;
    private LayoutInflater syncLayoutInflater;
    private List<Widget> widgets = new CopyOnWriteArrayList<>();
    private DataCenter dataCenter;

    FragmentManager.FragmentLifecycleCallbacks parentDestroyedCallback =
            new FragmentManager.FragmentLifecycleCallbacks() {

                @Override
                public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                    if (f == WidgetManager.this.parentFragment) {
                        fm.unregisterFragmentLifecycleCallbacks(parentDestroyedCallback);
                        f.getChildFragmentManager().beginTransaction()
                                .remove(WidgetManager.this).commitNowAllowingStateLoss();
                    }
                }
            };

    /**
     * Note: call after Activity.setContentView()
     */
    public static WidgetManager of(FragmentActivity activity, View rootView) {
        return create(activity, null, rootView, activity);
    }

    /**
     * Note: call from Fragment.onViewCreated()
     */
    public static WidgetManager of(Fragment fragment, View rootView) {
        return create(null, fragment, rootView, fragment.getContext());
    }

    private static WidgetManager create(FragmentActivity fragmentActivity, Fragment fragment, View rootView, Context context) {
        FragmentManager fragmentManager;
        if (fragmentActivity != null) {
            fragmentManager = fragmentActivity.getSupportFragmentManager();
        } else if (fragment != null) {
            fragmentManager = fragment.getChildFragmentManager();
        } else {
            return null;
        }
        WidgetManager widgetManager = new WidgetManager();
        widgetManager.parentFragment = fragment;
        widgetManager.contentView = rootView;
        widgetManager.context = context;
        widgetManager.asyncLayoutInflater = new AsyncLayoutInflater(widgetManager.context);
        widgetManager.syncLayoutInflater = LayoutInflater.from(widgetManager.context);
        if (fragment != null && fragment.getFragmentManager() != null) {
            fragment.getFragmentManager().registerFragmentLifecycleCallbacks(widgetManager.parentDestroyedCallback, false);
        }
        fragmentManager.beginTransaction().add(widgetManager, TAG).commitNowAllowingStateLoss();
        return widgetManager;
    }

    public WidgetManager setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
        for (Widget widget : widgets) {
            widget.mDataCenter = dataCenter;
        }
        return this;
    }

    public WidgetManager load(@IdRes int containerId, Widget widget) {
        return load(containerId, widget, true);
    }

    public WidgetManager load(@IdRes int containerId, final Widget widget, boolean async) {
        if (widget == null) {
            return this;
        }
        widget.setWidgetCallback(widgetCallback);
        widget.mContext = context;
        widget.mDataCenter = dataCenter;
        final ViewGroup container = contentView.findViewById(containerId);
        widget.mContainerView = container;
        if (widget.getLayoutId() == 0) {
            continueLoad(widget, container, null);
            return this;
        }
        if (async) {
            asyncLayoutInflater.inflate(widget.getLayoutId(), container, new AsyncLayoutInflater.OnInflateFinishedListener() {
                @Override
                public void onInflateFinished(@NonNull View view, int i, @Nullable ViewGroup viewGroup) {
                    if (isRemoving() || isDetached()
                            || getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                        return;
                    }
                    continueLoad(widget, container, view);
                }
            });
            return this;
        } else {
            View contentView = syncLayoutInflater.inflate(widget.getLayoutId(), container, false);
            continueLoad(widget, container, contentView);
            return this;
        }
    }

    private void continueLoad(Widget widget, View parentView, View contentView) {
        widget.mContentView = contentView;
        if (parentView instanceof ViewGroup && contentView != null) {
            ((ViewGroup) parentView).addView(contentView);
        }
        widgets.add(widget);
        getLifecycle().addObserver(widget);
    }

    public WidgetManager bind(@IdRes int viewId, Widget widget) {
        if (widget == null) {
            return this;
        }
        return bind(contentView.findViewById(viewId), widget);
    }

    public WidgetManager bind(View view, Widget widget) {
        if (widget == null) {
            return this;
        }
        widget.setWidgetCallback(widgetCallback);
        widget.mContext = context;
        widget.mDataCenter = dataCenter;
        widget.mContentView = view;
        widgets.add(widget);
        getLifecycle().addObserver(widget);
        return this;
    }

    public WidgetManager unBind(Widget widget) {
        if (widget == null) {
            return this;
        }
        getLifecycle().removeObserver(widget);
        Lifecycle.State state = getLifecycle().getCurrentState();
        switch (state) {
            case INITIALIZED:
                break;
            case CREATED:
                widget.onDestroy();
                break;
            case STARTED:
                widget.onStop();
                widget.onDestroy();
                break;
            case RESUMED:
                widget.onPause();
                widget.onStop();
                widget.onDestroy();
                break;
            case DESTROYED:
                if (!widget.mIsDestroyed) {
                    widget.onDestroy();
                }
                break;
            default:
        }
        widget.setWidgetCallback(null);
        widget.mDataCenter = null;
        widgets.remove(widget);
        return this;
    }

    /**
     * 加载没有 View 的 Widget
     */
    public WidgetManager load(Widget widget) {
        if (widget == null) {
            return this;
        }
        widget.setWidgetCallback(widgetCallback);
        widget.mContext = context;
        widget.mDataCenter = dataCenter;
        widgets.add(widget);
        getLifecycle().addObserver(widget);
        return this;
    }

    public WidgetManager unload(Widget widget) {
        if (widget == null) {
            return this;
        }
        getLifecycle().removeObserver(widget);
        Lifecycle.State state = getLifecycle().getCurrentState();
        switch (state) {
            case DESTROYED:
            case INITIALIZED:
                break;
            case CREATED:
                widget.onDestroy();
                break;
            case STARTED:
                widget.onStop();
                widget.onDestroy();
                break;
            case RESUMED:
                widget.onPause();
                widget.onStop();
                widget.onDestroy();
                break;
            default:
        }
        widget.setWidgetCallback(null);
        widget.mDataCenter = null;
        widgets.remove(widget);

        if (widget.mContainerView != widget.mContentView && widget.mContainerView instanceof ViewGroup) {
            ((ViewGroup) widget.mContainerView).removeAllViews();
        }
        return this;
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Widget widget : widgets) {
            widget.onActivityResult(requestCode, resultCode, data);
        }
    }
}
