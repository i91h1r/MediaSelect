package com.github.hyr0318.mediaselect_library.utils;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;


public class AudioDirectoryLoader extends CursorLoader {

    final String[] AUDIO_PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.TITLE, MediaStore.Audio
            .Media.DURATION, MediaStore.Audio
            .Media.DISPLAY_NAME, MediaStore.Audio
            .Media.SIZE

    };

    public AudioDirectoryLoader(Context context) {
        super(context);

        setProjection(AUDIO_PROJECTION);
        setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        setSortOrder(MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        setSelection(null);
        setSelectionArgs(null);
    }


    private AudioDirectoryLoader(Context context, Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }


}
