package com.drukido.vrun.database.interfaces;

import java.util.List;

/**
 * Created by Ido on 10/24/2015.
 */
public interface BackgroundTaskCallBack {
    void onSuccess(String result, List<Object> data);
    void onError(String error);
}
