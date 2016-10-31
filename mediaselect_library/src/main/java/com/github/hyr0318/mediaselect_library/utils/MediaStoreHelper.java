package com.github.hyr0318.mediaselect_library.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.github.hyr0318.mediaselect_library.R;
import com.github.hyr0318.mediaselect_library.ui.MediaDirectory;
import java.util.ArrayList;
import java.util.List;
import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;

/**
 * Created by donglua on 15/5/31.
 */
public class MediaStoreHelper {

    public final static int INDEX_ALL_PHOTOS = 0;


    /**
     * 获取图片
     *
     * @param activity
     * @param resultCallback
     */
    public static void getPhotoDirs(FragmentActivity activity, MediaResultCallback
            resultCallback) {
        activity.getSupportLoaderManager()
                .initLoader(0, null, new PhotoDirLoaderCallbacks(activity, resultCallback));
    }


    static class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        Context context;
        MediaResultCallback resultCallback;

        public PhotoDirLoaderCallbacks(Context context, MediaResultCallback resultCallback) {
            this.context = context;
            this.resultCallback = resultCallback;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new PhotoDirectoryLoader(context);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data == null) return;
            try {
                List<MediaDirectory> directories = new ArrayList<>();
                MediaDirectory photoDirectoryAll = new MediaDirectory();
                photoDirectoryAll.setName(context.getString(R.string.all_image));
                photoDirectoryAll.setId("ALL");

                while (data.moveToNext()) {

                    int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
                    String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
                    String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
                    String path = data.getString(data.getColumnIndexOrThrow(DATA));

                    MediaDirectory photoDirectory = new MediaDirectory();
                    photoDirectory.setId(bucketId);
                    photoDirectory.setName(name);

                    if (!directories.contains(photoDirectory)) {
                        photoDirectory.setCoverPath(path);
                        photoDirectory.addPhoto(imageId, path, "");
                        photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow
                                (DATE_ADDED)));
                        directories.add(photoDirectory);
                    } else {
                        directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, path,
                                "");
                    }

                    photoDirectoryAll.addPhoto(imageId, path, "");
                }
                if (photoDirectoryAll.getPhotoPaths().size() > 0) {
                    photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
                }
                directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);

                if (resultCallback != null) {
                    resultCallback.onResultCallback(directories);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    /**
     * 获取视频
     *
     * @param activity
     * @param resultCallback
     */
    public static void getVedioDirs(FragmentActivity activity, MediaResultCallback
            resultCallback) {
        activity.getSupportLoaderManager()
                .initLoader(0, null, new VedioLoaderCallbacks(activity, resultCallback));
    }

    /**
     * 视频
     */
    static class VedioLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        Context context;
        MediaResultCallback resultCallback;

        public VedioLoaderCallbacks(Context context, MediaResultCallback resultCallback) {
            this.context = context;
            this.resultCallback = resultCallback;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new VedioDirectoryLoader(context);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data == null) return;
            try {
                List<MediaDirectory> directories = new ArrayList<>();
                MediaDirectory photoDirectoryAll = new MediaDirectory();
                while (data.moveToNext()) {

                    int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
                    String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
                    String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
                    String path = data.getString(data.getColumnIndexOrThrow(DATA));
                    String time = data.getString(data.getColumnIndexOrThrow(MediaStore.Video
                            .Media.DURATION));

                    MediaDirectory photoDirectory = new MediaDirectory();
                    photoDirectory.setId(bucketId);
                    photoDirectory.setName(name);

                    if (!directories.contains(photoDirectory)) {
                        photoDirectory.setCoverPath(path);
                        photoDirectory.addPhoto(imageId, path, getTimeFromInt(time));
                        photoDirectory.setDuration(time);
                        photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow
                                (DATE_ADDED)));
                        directories.add(photoDirectory);
                    } else {
                        directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, path,
                                getTimeFromInt(time));
                    }

                    photoDirectoryAll.addPhoto(imageId, path, getTimeFromInt(time));
                }
                if (photoDirectoryAll.getPhotoPaths().size() > 0) {
                    photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
                }
                directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);
                if (resultCallback != null) {
                    resultCallback.onResultCallback(directories);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    /**
     * 获取音频
     *
     * @param activity
     * @param resultCallback
     */
    public static void getAudioDirs(FragmentActivity activity, MediaResultCallback
            resultCallback) {
        activity.getSupportLoaderManager()
                .initLoader(0, null, new AudioLoaderCallbacks(activity, resultCallback));
    }

    /**
     * 视频
     */
    static class AudioLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        Context context;
        MediaResultCallback resultCallback;

        public AudioLoaderCallbacks(Context context, MediaResultCallback resultCallback) {
            this.context = context;
            this.resultCallback = resultCallback;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AudioDirectoryLoader(context);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data == null) return;
            try {
                List<MediaDirectory> directories = new ArrayList<>();
                MediaDirectory photoDirectoryAll = new MediaDirectory();
                while (data.moveToNext()) {

                    int imageId = data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio
                            .Media._ID));
                    String path = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio
                            .Media.DATA));
                    String time = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio
                            .Media.DURATION));
                    String title = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio
                            .Media.TITLE));
                    long size = data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio
                            .Media.SIZE));

                    MediaDirectory photoDirectory = new MediaDirectory();
                    photoDirectory.setId(String.valueOf(imageId));
                    photoDirectory.setName(title);
                    if (!directories.contains(photoDirectory)) {
                        photoDirectory.setCoverPath(path);
                        photoDirectory.addPhoto(imageId, path, getTimeFromInt(time));
                        photoDirectory.setDuration(time);
                        photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow
                                (DATE_ADDED)));
                        directories.add(photoDirectory);
                    } else {
                        directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, path,
                                getTimeFromInt(time));
                    }

                    photoDirectoryAll.addPhoto(imageId, path, getTimeFromInt(time));
                }
                if (photoDirectoryAll.getPhotoPaths().size() > 0) {
                    photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
                }
                directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);

                if (resultCallback != null) {
                    resultCallback.onResultCallback(directories);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    public interface MediaResultCallback {
        void onResultCallback(List<MediaDirectory> directories);
    }

    /**
     * 　　* 时间的处理
     * 　　*
     * 　　* @param time
     * 　　* @return
     */
    public static String getTimeFromInt(String duration) {
        String vedioTime = "0:00";

        if (null != duration && !"".equals(duration)) {
            int time = Integer.parseInt(duration);
            if (time <= 0) {
                return "0:00";
            }
            int secondnd = (time / 1000) / 60;
            int million = (time / 1000) % 60;
            String f = String.valueOf(secondnd);
            String m = million >= 10 ? String.valueOf(million) : "0" + String.valueOf(million);
            vedioTime = f + ":" + m;
        }

        return vedioTime;
    }

}
