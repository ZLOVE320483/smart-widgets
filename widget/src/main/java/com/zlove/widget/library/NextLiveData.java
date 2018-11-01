package com.zlove.widget.library;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NextLiveData<T> extends MutableLiveData<T> {

    private int mLatestVersion = -1;
    private Map<Observer, NextObserver<T>> nextObserverMap = new HashMap<>();

    private static Handler sMainHandler = new Handler(Looper.getMainLooper());

    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<T> observer) {
        observe(owner, observer, false);
    }

    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<T> observer, boolean notifyWhenObserve) {
        if (nextObserverMap.containsKey(observer)) {
            return;
        }
        NextObserver<T> nextObserver = new NextObserver<>(mLatestVersion, observer, notifyWhenObserve);
        nextObserverMap.put(observer, nextObserver);
        super.observe(owner, nextObserver);
    }

    @MainThread
    public void observeForever(@NonNull final Observer<T> observer) {
        observeForever(observer, false);
    }

    @MainThread
    public void observeForever(@NonNull final Observer<T> observer, boolean notifyWhenObserve) {
        if (nextObserverMap.containsKey(observer)) {
            return;
        }
        NextObserver<T> nextObserver = new NextObserver<>(mLatestVersion, observer, notifyWhenObserve);
        nextObserverMap.put(observer, nextObserver);
        super.observeForever(nextObserver);
    }

    @Override
    public void removeObserver(@NonNull Observer<T> observer) {
        NextObserver<T> nextObserver = nextObserverMap.remove(observer);
        if (nextObserver != null) {
            super.removeObserver(observer);
            return;
        }
        if (observer instanceof NextObserver) {
            Observer key = null;
            Set<Map.Entry<Observer, NextObserver<T>>> entries = nextObserverMap.entrySet();
            for (Map.Entry<Observer, NextObserver<T>> entry : entries) {
                if (observer.equals(entry.getValue())) {
                    key = entry.getKey();
                    super.removeObserver(observer);
                    break;
                }
            }
            if (key != null) {
                nextObserverMap.remove(key);
            }
        }
    }

    @MainThread
    @Override
    public void setValue(T value) {
        mLatestVersion++;
        super.setValue(value);
    }

    @Override
    public void postValue(final T value) {
        sMainHandler.post(new Runnable() {
            @Override
            public void run() {
                setValue(value);
            }
        });
    }

    private class NextObserver<R> implements Observer<R> {

        private int initVersion;
        private Observer<R> observer;
        private boolean notifyWhenObserve;

        public NextObserver(int initVersion, Observer<R> observer, boolean notifyWhenObserve) {
            this.initVersion = initVersion;
            this.observer = observer;
            this.notifyWhenObserve = notifyWhenObserve;
        }

        @Override
        public void onChanged(@Nullable R r) {
            if (!notifyWhenObserve && initVersion >= mLatestVersion) {
                return;
            }
            observer.onChanged(r);
        }
    }

}
