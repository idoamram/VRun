package com.drukido.vrun.interfaces;

import android.graphics.Bitmap;

public interface GetPhotoCallback {
    void onSuccess(Bitmap photoBitmap);
    void onError();
}
