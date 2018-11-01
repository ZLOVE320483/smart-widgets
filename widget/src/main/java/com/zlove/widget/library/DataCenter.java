package com.zlove.widget.library;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class DataCenter extends ViewModel {

    private Map<String, Object> mDataStore= new HashMap<>();
    private Map<String, NextLiveData<KVData>> mLiveDataMap = new HashMap<>();
    private LifecycleOwner mLifecycleOwner;
    private Thread mainThread;
    private Handler handler = new Handler(Looper.getMainLooper());

    public static DataCenter create(ViewModelProvider viewModelProvider, LifecycleOwner lifecycleOwner) {
        DataCenter dataCenter = viewModelProvider.get(DataCenter.class);
        dataCenter.mLifecycleOwner = lifecycleOwner;
        return dataCenter;
    }

    public DataCenter put(final Bundle bundle) {
        if (!isMainThread()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    put(bundle);
                }
            });
            return this;
        }
        if (bundle == null) {
            return this;
        }
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (value == null) {
                continue;
            }
            put(key, value);
        }
        return this;
    }

    public DataCenter put(final String key, final Object data) {
        if (!isMainThread()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    put(key, data);
                }
            });
            return this;
        }
        mDataStore.put(key, data);
        MutableLiveData<KVData> liveData = mLiveDataMap.get(key);
        if (liveData != null) {
            liveData.setValue(new KVData(key, data));
        }
        return this;
    }

    public <T> T get(String key) {
        Object object = mDataStore.get(key);
        if (object != null) {
            return (T) object;
        }
        return null;
    }

    public <T> T get(String key, T defaultValue) {
        if (!mDataStore.containsKey(key)) {
            return defaultValue;
        }
        return get(key);
    }

    public boolean has(String key) {
        return mDataStore.containsKey(key);
    }

    @MainThread
    public DataCenter observe(String key, Observer<KVData> observer) {
        return observe(key, observer, false);
    }

    @MainThread
    public DataCenter observe(String key, Observer<KVData> observer, boolean notifyWhenObserve) {
        if (TextUtils.isEmpty(key) || observer == null) {
            return this;
        }
        NextLiveData<KVData> liveData = getLiveData(key);
        liveData.observe(mLifecycleOwner, observer, notifyWhenObserve);
        return this;
    }

    @MainThread
    public DataCenter observe(String key, Observer<KVData> observer, LifecycleOwner lifecycleOwner) {
        return observe(key, observer, lifecycleOwner, false);
    }

    @MainThread
    public DataCenter observe(String key, Observer<KVData> observer, LifecycleOwner lifecycleOwner, boolean notifyWhenObserve) {
        if (TextUtils.isEmpty(key) || observer == null) {
            return this;
        }
        NextLiveData<KVData> liveData = getLiveData(key);
        liveData.observe(lifecycleOwner, observer, notifyWhenObserve);
        return this;
    }

    @MainThread
    public DataCenter observeForever(String key, Observer<KVData> observer) {
        return observeForever(key, observer, false);
    }

    @MainThread
    public DataCenter observeForever(String key, Observer<KVData> observer, boolean notifyWhenObserve) {
        if (TextUtils.isEmpty(key) || observer == null) {
            return this;
        }
        NextLiveData<KVData> liveData = mLiveDataMap.get(key);
        liveData.observeForever(observer, notifyWhenObserve);
        return this;
    }

    private NextLiveData<KVData> getLiveData(String key) {
        NextLiveData<KVData> liveData = mLiveDataMap.get(key);
        if (liveData == null) {
            liveData = new NextLiveData<>();
            if (mDataStore.containsKey(key)) {
                liveData.setValue(new KVData(key, mDataStore.get(key)));
            }
            mLiveDataMap.put(key, liveData);
        }
        return liveData;
    }

    @MainThread
    public DataCenter removeObserver(String key, Observer<KVData> observer) {
        if (TextUtils.isEmpty(key) || observer == null) {
            return this;
        }
        NextLiveData<KVData> liveData = mLiveDataMap.get(key);
        if (liveData != null) {
            liveData.removeObserver(observer);
        }
        return this;
    }

    @MainThread
    public DataCenter removeObserver(Observer<KVData> observer) {
        if (observer == null) {
            return this;
        }
        for (NextLiveData<KVData> liveData : mLiveDataMap.values()) {
            liveData.removeObserver(observer);
        }
        return this;
    }

    @Override
    protected void onCleared() {
        mDataStore.clear();
        mLiveDataMap.clear();
        mLifecycleOwner = null;
    }

    private boolean isMainThread() {
        if (mainThread == null) {
            mainThread = Looper.getMainLooper().getThread();
        }
        return Thread.currentThread() == mainThread;
    }
}
