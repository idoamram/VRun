package com.drukido.vrun.utils;

public class PhotosManager {
//    public static final String TYPE_USER_PROFILE_PHOTO = "Users/";
//    public static final String TYPE_GROUP_PHOTO = "Groups/";
//    public static final String TYPE_RUN_PHOTO = "Runs/";
//
//    private static final String DIR_VRUN_PHOTOS_MAIN = "/VRun/Photos/";
//    private static final String JPG_FILE_EXT = ".jpg";
//    private static final String FILE_TYPE_JPG = "jpg";
//
//    public static void saveCurrUserOrGroupPhotoInBackground(final Context context,
//                                 final String photoOldPath,
//                                 final String photoType,
//                                 final OnAsyncTaskFinishedListener listener) {
//
//        new AsyncTask<Void,Void,Boolean>() {
//
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//
//                String objectId = null;
//                byte[] photoBytes = null;
//                boolean isSavingToServerSucceeded = false;
//
//                try {
//                    photoBytes = pathToBytes(photoOldPath);
//                    User user = (User) ParseUser.getCurrentUser().fetchIfNeeded();
//
//                    switch (photoType) {
//                        case TYPE_GROUP_PHOTO:
//                            Group group = user.getGroup();
//                            group.fetchIfNeeded();
//                            group.setGroupPhoto(new ParseFile(group.getObjectId() + "_" +
//                                    new DateTime().getMillis(), photoBytes, FILE_TYPE_JPG));
//                            group.save();
//                            objectId = group.getObjectId();
//                            break;
//                        case TYPE_USER_PROFILE_PHOTO:
//                            user.setProfilePhoto(new ParseFile(user.getObjectId() + "_" +
//                                    new DateTime().getMillis(),
//                                    photoBytes, FILE_TYPE_JPG));
//                            user.save();
//                            objectId = user.getObjectId();
//                            break;
//                        default:
//                            break;
//                    }
//
//                    isSavingToServerSucceeded = true;
//                    return true;
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//                } finally {
//                    if (isSavingToServerSucceeded) {
//                        try {
//                            if (!savePhotoLocally(photoBytes, objectId, photoType, context)) {
//                                Log.e(Constants.LOG_TAG, "PhotosManager.savePhotoInBackground" +
//                                        "FAILED to save photo on locally");
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Log.e(Constants.LOG_TAG, "PhotosManager.savePhotoInBackground" +
//                                    "FAILED to save photo on server");
//                        }
//                    }
//                }
//            }
//
//            @Override
//            protected void onPostExecute(Boolean result) {
//                super.onPostExecute(result);
//
//                if(result) {
//                    listener.onSuccess(result);
//                } else {
//                    listener.onError("Got an error while saving the photo");
//                }
//            }
//        }.execute();
//    }
//
//    public static void getCurrUserOrGroupPhotoInBackground(final Context context,
//                                            final String photoType,
//                                            final GetPhotoCallback callback) {
//        new AsyncTask<Void,Bitmap,Void>(){
//
//            boolean isFetched = false;
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//
//                try {
//                    User user = (User) ParseUser.getCurrentUser();
//                    user.fetchIfNeeded();
//                    File photoFile = null;
//
//                    switch (photoType) {
//                        case TYPE_GROUP_PHOTO:
//                            Group group = user.getGroup();
//                            group.fetchIfNeeded();
//                            photoFile = getFileFromLocalStorage(group.getObjectId(),
//                                    photoType, context);
//                            if (photoFile != null) {
//                                publishProgress(BitmapFactory
//                                        .decodeFile(photoFile.getAbsolutePath()));
//
//                                if(!(photoFile.getName().equals(group.getGroupPhoto().getName()))) {
//                                    byte[] bytes = group.getGroupPhoto().getData();
//                                    savePhotoLocally(bytes, group.getObjectId(), TYPE_GROUP_PHOTO,
//                                            context);
//                                    publishProgress(BitmapFactory.decodeByteArray(bytes, 0,
//                                            bytes.length));
//                                }
//                            }
//                            else {
//                                byte[] bytes = group.getGroupPhoto().getData();
//                                savePhotoLocally(bytes, group.getObjectId(), TYPE_GROUP_PHOTO,
//                                        context);
//                                publishProgress(BitmapFactory.decodeByteArray(bytes, 0,
//                                        bytes.length));
//                            }
//                            break;
//                        case TYPE_USER_PROFILE_PHOTO:
//                            photoFile = getFileFromLocalStorage(user.getObjectId(),
//                                    photoType, context);
//                            if (photoFile != null) {
//                                publishProgress(BitmapFactory
//                                        .decodeFile(photoFile.getAbsolutePath()));
//
//                                if(!(photoFile.getName().equals(user.getProfilePhoto().getName()))) {
//                                    byte[] bytes = user.getProfilePhoto().getData();
//                                    savePhotoLocally(bytes, user.getObjectId(),
//                                            TYPE_USER_PROFILE_PHOTO, context);
//                                    publishProgress(BitmapFactory.decodeByteArray(bytes, 0,
//                                            bytes.length));
//                                }
//                            } else {
//                                byte[] bytes = user.getProfilePhoto().getData();
//                                savePhotoLocally(bytes, user.getObjectId(),
//                                        TYPE_USER_PROFILE_PHOTO, context);
//                                publishProgress(BitmapFactory.decodeByteArray(bytes, 0,
//                                        bytes.length));
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    publishProgress(null);
//                }
//
//                return null;
//            }
//
//            @Override
//            protected void onProgressUpdate(Bitmap... values) {
//                super.onProgressUpdate(values);
//                if (values[0] != null) {
//                    callback.onSuccess(values[0]);
//                } else {
//                    callback.onError();
//                }
//            }
//
//        }.execute();
//    }
//
//    public static void getUserPhotoInBackground(final Context context,
//                                                final String photoType,
//                                                final User user,
//                                                final GetPhotoCallback callback) {
//        new AsyncTask<Void,Void,Void>(){
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//
//                try {
//                    user.fetchIfNeeded();
//                    File photoFile = null;
//
//                    photoFile = getFileFromLocalStorage(user.getObjectId(),
//                            photoType, context);
//                    callback.onSuccess(BitmapFactory
//                            .decodeFile(photoFile.getAbsolutePath()));
//                    if(!(photoFile.getName().equals(user.getProfilePhoto().getName()))) {
//                        byte[] bytes = user.getProfilePhoto().getData();
//                        callback.onFetched(BitmapFactory.decodeByteArray(bytes, 0,
//                                bytes.length));
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    callback.onError();
//                }
//
//                return null;
//            }
//
//        }.execute();
//    }
//
//    public static void saveCurrUserGroupPhoto(final Context context, final String photoOldPath,
//                                            final OnAsyncTaskFinishedListener listener) {
//        new AsyncTask <Void,Void,Boolean>() {
//
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//
//                try {
////                    File file = new File(photoOldPath);
////                    int size = (int) file.length();
////                    byte[] bytes = new byte[size];
////
////                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
////                    buf.read(bytes, 0, bytes.length);
////                    buf.close();
//
//                    Group group = ((User)User.getCurrentUser()).getGroup();
//                    group.fetchIfNeeded();
//
//                    // Save file on server
//
//                    byte[] bytes = pathToBytes(photoOldPath);
//
//                    group.setGroupPhoto(new ParseFile(group.getObjectId(), bytes, FILE_TYPE_JPG));
//                    group.save();
//
//                    // Save file locally
//                    return savePhotoLocally(bytes, group.getObjectId(), TYPE_GROUP_PHOTO, context);
//                } catch (Exception e) {
//                    if (e.getMessage() != null) {
//                        Log.e(Constants.LOG_TAG, "Error in saving group photo:\n" +
//                        e.getMessage());
//                    } else {
//                        Log.e(Constants.LOG_TAG, "Error in saving group photo");
//                    }
//                    return false;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(Boolean result) {
//                super.onPostExecute(result);
//
//                if (result) {
//                    listener.onSuccess(result);
//                } else {
//                    listener.onError("Error while saving the group photo");
//                }
//            }
//        }.execute();
//    }
//
//    private static boolean savePhotoLocally (byte[] data,String fileName, String internalDir,
//                                             Context context) throws IOException {
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            // Create a File object for the parent directory
//            File photosDir =
//                    new File(Environment.
//                            getExternalStorageDirectory() + DIR_VRUN_PHOTOS_MAIN + internalDir);
//
//            // Have the object build the directory structure, if needed.
//            if (photosDir.mkdirs() || photosDir.isDirectory()) {
//                    File newPhotoFile = new File(photosDir, fileName + "_" +
//                            (new DateTime().getMillis()) + JPG_FILE_EXT);
//                    FileOutputStream newPhotoOS = new FileOutputStream(newPhotoFile);
//                    newPhotoOS.write(data);
//                    newPhotoOS.close();
//
//                // Tell the media scanner about the new file so that it is
//                // immediately available to the user.
//                MediaScannerConnection.scanFile(context,
//                        new String[]{newPhotoFile.toString()}, null,
//                        new MediaScannerConnection.OnScanCompletedListener() {
//                            public void onScanCompleted(String path, Uri uri) {
//                                Log.i("ExternalStorage", "Scanned " + path + ":");
//                                Log.i("ExternalStorage", "-> uri=" + uri);
//                            }
//                        });
//                    return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
//
//    private static File getFileFromLocalStorage(String objectId, String photoTypeDir,
//                                                   Context context) throws Exception {
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            File targetPhotoDir = new File(Environment.getExternalStorageDirectory()+
//                    DIR_VRUN_PHOTOS_MAIN + photoTypeDir);
//            if (targetPhotoDir.exists()) {
//                File[] allPhotos = targetPhotoDir.listFiles();
//                for(File file:allPhotos){
//                    if(file.getName().matches(objectId + ".*")) {
//                        return file;
//                    }
//                }
//                return null;
//            } else {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }
//
//    public static void savePhotoLocallyInBackground(final Context context,
//                                                    final byte[] data,
//                                                    final String fileName,
//                                                    final String internalDir,
//                                                    final OnAsyncTaskFinishedListener listener){
//        new AsyncTask<Void,Void,Boolean>(){
//
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                try {
//                    return savePhotoLocally(data, fileName, internalDir, context);
//                } catch (IOException e) {
//                    Log.e(Constants.LOG_TAG, "Error in savePhotoLocallyInBackground");
//                    e.printStackTrace();
//                    return false;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(Boolean result) {
//                super.onPostExecute(result);
//                if (result) {
//                    listener.onSuccess(result);
//                } else {
//                    listener.onError("error");
//                }
//            }
//
//        }.execute();
//    }
//
//    private static byte[] pathToBytes(String photoPath) throws IOException {
//        File file = new File(photoPath);
//        int size = (int) file.length();
//        byte[] bytes = new byte[size];
//
//        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//        buf.read(bytes, 0, bytes.length);
//        buf.close();
//
//        return bytes;
//    }
}
