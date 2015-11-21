package com.drukido.vrun.interfaces;

/**
 * Created by Ido on 11/21/2015.
 */
public interface OnAsyncTaskFinishedListener {
    void onSuccess(Object result);
    void onError(String errorMessage);
}
