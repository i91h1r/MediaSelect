package com.github.hyr0318.mediaselect_library.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.hyr0318.mediaselect_library.Constans.MediaType;
import com.github.hyr0318.mediaselect_library.R;
import com.github.hyr0318.mediaselect_library.utils.ImageCaptureManager;
import java.io.Serializable;
import java.util.List;


/**
 * Description:
 * Author：hyr on 2016/10/28 16:25
 * Email：2045446584@qq.com
 */
public class MediaSelectActivity extends FragmentActivity implements View.OnClickListener {
    private ImagePagerFragment imagePagerFragment;

    protected FragmentManager mFrgManager = null;
    private int mediaType = 0;
    private LinearLayout closeBtn;
    private Button takePhotoBtn;
    private TextView mediaTitle;
    private Fragment fragment;
    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String MEDIA_SELECT_TYPE = "MEDIA_SELECT_TYPE";
    public final static String SELECT_PHOTOS = "selectPhoto";
    private   RelativeLayout rl_title;


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_media_select_layout);

        initView();

        switchFragment();
    }


    private void switchFragment() {

        if (mediaType == MediaType.PHOTO_SELECT_TYPE) {
            mediaTitle.setText(this.getResources().getString(R.string.please_select_pic));
            takePhotoBtn.setText(this.getResources().getString(R.string.go_to_takepic));
        } else if (mediaType == MediaType.AUDIO_SELECT_TYPE) {
            mediaTitle.setText(this.getResources().getString(R.string.please_select_audio));
            takePhotoBtn.setText(this.getResources().getString(R.string.go_to_audio));
        } else if (mediaType == MediaType.VIDEO_SELECT_TYPE) {
            mediaTitle.setText(this.getResources().getString(R.string.please_select_vedio));
            takePhotoBtn.setText(this.getResources().getString(R.string.go_to_vedio));
        }

        fragment = new MediaSelectFragment(this, mediaType);

        if (fragment != null) {
            FragmentTransaction ft = mFrgManager.beginTransaction();
            ft.replace(R.id.medioSelectFragment, fragment);
            ft.commitAllowingStateLoss();
        }

    }


    private void initView() {

        mFrgManager = getSupportFragmentManager();

        mediaType = getIntent().getIntExtra(MEDIA_SELECT_TYPE, 0);

        closeBtn = (LinearLayout) findViewById(R.id.close_layout);

        takePhotoBtn = (Button) findViewById(R.id.goto_takepic);

        mediaTitle = (TextView) findViewById(R.id.media_title);

        rl_title = (RelativeLayout) findViewById(R.id.rl_title);

        closeBtn.setOnClickListener(this);

        takePhotoBtn.setOnClickListener(this);

    }


    @Override public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.close_layout) {

            this.finish();

        } else if (id == R.id.goto_takepic) {

            if (mediaType == MediaType.PHOTO_SELECT_TYPE) {
                ((MediaSelectFragment) fragment).takePhoto();
            } else if (mediaType == MediaType.AUDIO_SELECT_TYPE) {
                ((MediaSelectFragment) fragment).recordAudio();
            } else if (mediaType == MediaType.VIDEO_SELECT_TYPE) {
                takeVideo();
            }
        }
    }


    /**
     * @param mediaType choose type
     * @param photoCount max count
     * @param requestCode
     */

    public static void openActivity(
        Activity activity,
        int mediaType,
        int photoCount,
        List<Photo> selectPhoto,
        int requestCode) {

        Intent intent = new Intent(activity, MediaSelectActivity.class);
        intent.putExtra(MEDIA_SELECT_TYPE, mediaType);
        intent.putExtra(EXTRA_MAX_COUNT, photoCount);
        intent.putExtra(SELECT_PHOTOS, (Serializable) selectPhoto);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    public void onBackPressed() {
        if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
            imagePagerFragment.runExitAnimation(new Runnable() {
                public void run() {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }


    public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment;
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.container, this.imagePagerFragment)
            .addToBackStack(null)
            .commit();
    }


    public MediaSelectActivity getActivity() {
        return this;
    }


    /**
     * video
     */
    private void takeVideo() {
        final Intent takeVideoIntent = new Intent(
            MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent,
                ImageCaptureManager.REQUEST_TAKE_PHOTO);
        }
    }


}
