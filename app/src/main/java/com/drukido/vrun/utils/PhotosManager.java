package com.drukido.vrun.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.drukido.vrun.Constants;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.joda.time.DateTime;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotosManager {
    public static final String DIR_USERS_PROFILE_PHOTOS = "/users_profile_photos/";
    private static final String DIR_VRUN_MAIN = "/VRun";
    private static final String JPG_FILE_EXT = ".jpg";
    public static final String DIR_GROUPS_PHOTOS = "/groups_photos/";

    public static void saveCurrUserGroupPhoto(final Context context, final String photoOldPath,
                                            final OnAsyncTaskFinishedListener listener) {
        new AsyncTask <Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                try {
                    File file = new File(photoOldPath);
                    int size = (int) file.length();
                    byte[] bytes = new byte[size];

                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();

                    Group group = ((User)User.getCurrentUser()).getGroup();
                    group.fetchIfNeeded();

                    // Save file on server
                    group.setGroupPhoto(new ParseFile(group.getObjectId(), bytes, "jpg"));
                    group.save();

                    // Save file locally
                    return savePhotoLocally(bytes, group.getObjectId(), DIR_GROUPS_PHOTOS, context);
                } catch (Exception e) {
                    if (e.getMessage() != null) {
                        Log.e(Constants.LOG_TAG, "Error in saving user profile photo:\n" +
                        e.getMessage());
                    } else {
                        Log.e(Constants.LOG_TAG, "Error in saving user profile photo");
                    }
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                if (result) {
                    listener.onSuccess(result);
                } else {
                    listener.onError("Error while saving the group photo");
                }
            }
        }.execute();
    }

    private static boolean savePhotoLocally (byte[] data,String fileName, String internalDir,
                                             Context context) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Create a File object for the parent directory
            File profilePhotosDir =
                    new File(Environment.
                            getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +
                    DIR_VRUN_MAIN + internalDir);

            // Have the object build the directory structure, if needed.
            if (profilePhotosDir.mkdirs() || profilePhotosDir.isDirectory()) {
                    File newPhotoFile = new File(profilePhotosDir.getAbsolutePath() +
                            fileName + JPG_FILE_EXT);
                    FileOutputStream newPhotoOS = new FileOutputStream(newPhotoFile);
                    newPhotoOS.write(data);
                    newPhotoOS.close();

                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
                MediaScannerConnection.scanFile(context,
                        new String[]{newPhotoFile.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
                    return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void savePhotoLocallyInBackground(final Context context,
                                                    final byte[] data,
                                                    final String fileName,
                                                    final String internalDir,
                                                    final OnAsyncTaskFinishedListener listener){
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        // Create a File object for the parent directory
                        File profilePhotosDir = new File(Environment.getExternalStorageDirectory() +
                                DIR_VRUN_MAIN + internalDir);

                        // Have the object build the directory structure, if needed.
                        if (profilePhotosDir.mkdirs() || profilePhotosDir.isDirectory()) {
                            File newPhotoFile = new File(profilePhotosDir.getAbsolutePath() + "/" +
                                    fileName + JPG_FILE_EXT);
                            if (newPhotoFile.mkdirs()) {
                                FileOutputStream newPhotoOS = new FileOutputStream(newPhotoFile);
                                newPhotoOS.write(data);
                                newPhotoOS.close();

                                // Tell the media scanner about the new file so that it is
                                // immediately available to the user.
                                MediaScannerConnection.scanFile(context,
                                        new String[]{newPhotoFile.toString()}, null,
                                        new MediaScannerConnection.OnScanCompletedListener() {
                                            public void onScanCompleted(String path, Uri uri) {
                                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                                Log.i("ExternalStorage", "-> uri=" + uri);
                                            }
                                        });
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                    else {
                        return false;
                    }
                } catch (IOException e) {
                    Log.e(Constants.LOG_TAG, "Error in savePhotoLocallyInBackground");
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    listener.onSuccess(result);
                } else {
                    listener.onError("error");
                }
            }

        }.execute();
    }
}
