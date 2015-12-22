package com.drukido.vrun.interfaces;

import com.parse.ParseObject;

import java.util.List;

public interface GetDataCallBack {
    void onGetFromLocal(List<ParseObject> result, Exception e);
    void onFetchingDone(List<ParseObject> result, Exception e);
}
