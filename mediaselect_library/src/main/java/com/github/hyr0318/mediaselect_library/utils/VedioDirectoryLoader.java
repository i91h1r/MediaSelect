package com.github.hyr0318.mediaselect_library.utils;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * Created by zhang lijie
 */
public class VedioDirectoryLoader extends CursorLoader {

    final String[] VEDIO_PROJECTION = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.TITLE, MediaStore.Video
            .Media.DURATION,

    };

    public VedioDirectoryLoader(Context context) {
        super(context);

        setProjection(VEDIO_PROJECTION);
        setUri(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        setSortOrder(MediaStore.Video.Media.DATE_ADDED + " DESC");
        setSelection(null);
        setSelectionArgs(null);
    }


    private VedioDirectoryLoader(Context context, Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }


}
