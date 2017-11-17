package com.example.hayoung.yongchat.service;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by brannpark on 2017. 11. 17..
 */

public interface DataCallback<T> {
    void onResults(@NonNull List<T> items);
    void onResult(@NonNull T item);
}
