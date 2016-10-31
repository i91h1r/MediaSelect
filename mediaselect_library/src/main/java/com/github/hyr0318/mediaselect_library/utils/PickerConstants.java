package com.github.hyr0318.mediaselect_library.utils;

import android.os.Environment;
import java.io.File;

/**
 * @author zhanglijie
 *         created at 2016/6/28 11:18
 */
public class PickerConstants {
    public static final String CRP_AUDIO = Environment
            .getExternalStorageDirectory() + "/AudioRecord/";

    static {

        File dfile = new File(CRP_AUDIO);
        if (!dfile.exists()) {
            dfile.mkdirs();
        }
    }

    public static final String CRP_AUDIO_PATH= Environment
            .getExternalStorageDirectory() + "/AudioRecord";

    static {

        File dfile = new File(CRP_AUDIO_PATH);
        if (!dfile.exists()) {
            dfile.mkdirs();
        }
    }
}
