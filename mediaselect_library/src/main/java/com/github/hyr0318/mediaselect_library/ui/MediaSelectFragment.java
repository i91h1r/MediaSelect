package com.github.hyr0318.mediaselect_library.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.hyr0318.mediaselect_library.Constans.Constans;
import com.github.hyr0318.mediaselect_library.R;
import com.github.hyr0318.mediaselect_library.event.OkClickListener;
import com.github.hyr0318.mediaselect_library.event.OnItemCheckListener;
import com.github.hyr0318.mediaselect_library.event.OnPhotoClickListener;
import com.github.hyr0318.mediaselect_library.ui.adapter.PhotoGridAdapter;
import com.github.hyr0318.mediaselect_library.utils.FileUtils;
import com.github.hyr0318.mediaselect_library.utils.ImageCaptureManager;
import com.github.hyr0318.mediaselect_library.utils.MediaStoreHelper;
import com.github.hyr0318.mediaselect_library.utils.MessagDialogUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.github.hyr0318.mediaselect_library.Constans.Constans.DEFAULT_MAX_COUNT;
import static com.github.hyr0318.mediaselect_library.ui.MediaSelectActivity.MEDIA_SELECT_TYPE;
import static com.github.hyr0318.mediaselect_library.ui.MediaSelectActivity.SELECT_PHOTOS;
import static com.github.hyr0318.mediaselect_library.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * Description:
 * Author：hyr on 2016/10/28 16:25
 * Email：2045446584@qq.com
 */
@SuppressLint("ValidFragment") public class MediaSelectFragment extends Fragment
    implements View.OnClickListener {
    private ImageCaptureManager captureManager;
    private Context mContext;
    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    private int maxCount = DEFAULT_MAX_COUNT;

    private RelativeLayout okBtnLayout;
    public List<Photo> selectPhoto = null;//选中图片
    private TextView showSelectPCount;

    private List<MediaDirectory> directorieLists;
    private int mediaType = 0;

    private MediaSelectActivity mediaSelectActivity;
    private PhotoGridAdapter photoGridAdapter;
    private ProgressDialog progressDialog = null;
    ScanSdReceiver scanSdReceiver;
    IntentFilter intentfilter;


    public MediaSelectFragment(MediaSelectActivity mediaSelectActivity, int type) {

        this.mediaType = type;

        this.mediaSelectActivity = mediaSelectActivity;
    }


    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            photoGridAdapter.getSelectedPhotos().clear();
            photoGridAdapter.getSelectedPhotoPaths().clear();
            showSelectPCount.setText("");
            showSelectPCount.setVisibility(View.GONE);

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);

        final View rootView = inflater.inflate(R.layout.fragment_media_picker, container, false);

        findView(rootView);

        return rootView;
    }


    private void findView(View rootView) {

        mContext = getActivity();

        okBtnLayout = (RelativeLayout) rootView.findViewById(R.id.ok_btn_layout);
        okBtnLayout.setOnClickListener(this);

        maxCount = getActivity().getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);

        selectPhoto = (List<Photo>) getActivity().getIntent().getSerializableExtra(SELECT_PHOTOS);

        showSelectPCount = (TextView) rootView.findViewById(R.id.show_select_count);

        directorieLists = new ArrayList<>();

        captureManager = new ImageCaptureManager(getActivity());

        scanMedia(this.mediaType);

        photoGridAdapter = new PhotoGridAdapter(getActivity(), directorieLists, this.mediaType) {
            @Override
            public void updateSelectCount(int size) {

                if (size > 0) {
                    showSelectPCount.setVisibility(View.VISIBLE);
                    showSelectPCount.setText(String.valueOf(size));

                } else {

                    showSelectPCount.setVisibility(View.GONE);
                }

            }
        };
        photoGridAdapter.addSelectPhoto(selectPhoto);
        photoGridAdapter.setShowCamera(false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,
            OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager
            .GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoGridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View v, int position, boolean showCamera) {
                final int index = showCamera ? position - 1 :position;
                List<String> photos = photoGridAdapter.getCurrentPhotoPaths();
                switch (mediaType) {
                    case 0://图片
                        int[] screenLocation = new int[2];
                        v.getLocationOnScreen(screenLocation);
                        ImagePagerFragment imagePagerFragment =
                            ImagePagerFragment.newInstance(photos, index, screenLocation,
                                v.getWidth(), v.getHeight());

                        ((MediaSelectActivity) getActivity()).addImagePagerFragment
                            (imagePagerFragment);
                        break;
                    case 1://视频

                        try {
                            Uri uri = Uri.parse("file://" + photos.get(index));
                            // 调用系统自带的播放器
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "video/*");
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case 2://音频
                        /* 将通用android.content.Intent.ACTION_VIEW意图的数据设置为一个音频文件的URI，
                        * 并制定其MIME类型，这样Android就能挑选适当的应用程序进行播放。
                        */
                        try {
                            Uri audioPath = Uri.parse("file://" + photos.get(index));
                            Intent intentAudio = new Intent(Intent.ACTION_VIEW);
                            //此处需要在sd卡中放置一个文件名为good的mp3文件。
                            intentAudio.setDataAndType(audioPath, "audio/*");

                            startActivity(intentAudio);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                }
            }


            @Override
            public void onLongClick(View v, int position) {
                final List<Photo> paths = photoGridAdapter.getSelectedPhotoPaths();
                if (paths.size() > 0) {

                    MessagDialogUtil.getIntence(getActivity())
                        .setOkClickListener(new OkClickListener
                            () {

                            @Override
                            public void onClick(Dialog ok) {

                                deleteFiles(paths);
                                ok.dismiss();
                            }
                        });
                    MessagDialogUtil.getIntence(getActivity()).initDialog(getResources().getString(R
                        .string.delete_select_file), getResources().getString(R
                        .string.message_ok), getResources().getString(R
                        .string.message_cancle));
                }

            }

        });

        photoGridAdapter.setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean OnItemCheck(int position, Photo photo, final boolean isCheck, int
                selectedItemCount) {

                int total = selectedItemCount + (isCheck ? -1 :1);

                if (maxCount <= 1) {
                    List<Photo> photos = photoGridAdapter
                        .getSelectedPhotos();
                    if (!photos.contains(photo)) {
                        photos.clear();
                        photoGridAdapter
                            .notifyDataSetChanged();
                    }
                    return true;
                }

                if (total > maxCount) {
                    Toast.makeText(getActivity(), getString(R.string.over_max_count_tips,
                        maxCount),
                        Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });
        intentfilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentfilter.addDataScheme("file");
        scanSdReceiver = new ScanSdReceiver();
        getActivity().registerReceiver(scanSdReceiver, intentfilter);
    }


    /**
     * photo
     */
    public void takePhoto() {

        try {
            if (null != captureManager) {

                Intent intent = captureManager.dispatchTakePictureIntent();
                startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * audio
     */
    public void recordAudio() {

        Intent recordIntent = new Intent(getActivity(), ApplyRecorderActivity.class);
        startActivityForResult(recordIntent,
            ImageCaptureManager.REQUEST_TAKE_PHOTO);
    }


    @Override public void onClick(View v) {
        if (v.getId() == R.id.ok_btn_layout) {
            if (null != photoGridAdapter) {
                List<Photo> paths = photoGridAdapter.getSelectedPhotoPaths();
                //                if (null != paths && paths.size() > 0) {

                Intent intent = new Intent();
                intent.putExtra(Constans.RESULT_LIST, (Serializable) paths);
                intent.putExtra(MEDIA_SELECT_TYPE, mediaType);
                ((Activity) mContext).setResult(Constans.REQUEST_CODE, intent);
                ((Activity) mContext).finish();

            }

        }

    }


    /**
     * scan
     */
    private void scanMedia(int type) {

        switch (type) {

            case 0:
                MediaStoreHelper.getPhotoDirs(getActivity(),
                    new MediaStoreHelper.MediaResultCallback() {
                        @Override
                        public void onResultCallback(List<MediaDirectory> directories) {
                            directorieLists.clear();
                            directorieLists.addAll(directories);
                            photoGridAdapter.notifyDataSetChanged();
                        }
                    });
                break;
            case 1:
                MediaStoreHelper.getVedioDirs(getActivity(),
                    new MediaStoreHelper.MediaResultCallback() {
                        @Override
                        public void onResultCallback(List<MediaDirectory> directories) {
                            directorieLists.clear();
                            directorieLists.addAll(directories);
                            photoGridAdapter.notifyDataSetChanged();
                        }
                    });
                break;
            case 2:
                MediaStoreHelper.getAudioDirs(getActivity(),
                    new MediaStoreHelper.MediaResultCallback() {
                        @Override
                        public void onResultCallback(List<MediaDirectory> directories) {
                            directorieLists.clear();
                            directorieLists.addAll(directories);
                            photoGridAdapter.notifyDataSetChanged();
                        }
                    });
                break;
        }

    }


    /**
     * delete
     */
    private void deleteFiles(final List<Photo> list) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (null != list && list.size() > 0) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog = progressDialog.show(mContext, "", getResources().getString(R.string
                .delete_tips));
            new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < list.size(); i++) {
                        FileUtils.delete(list.get(i).getPath());
                    }
                    if (isKitKat) {

                        String[] paths = { Environment
                            .getExternalStorageDirectory().getAbsolutePath() };
                        String[] mimeTypes = { null };
                        MediaScannerConnection.scanFile(mContext, paths, mimeTypes, new
                            MediaScannerConnection
                                .OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    progressDialog.dismiss();
                                    Message msg = new Message();
                                    msg.arg1 = mediaType;
                                    mHandler.sendMessage(msg);
                                }
                            });
                    } else {
                        Message msg = new Message();
                        msg.arg1 = mediaType;
                        mHandler.sendMessage(msg);
                        scanSdCard();
                    }

                }
            }).start();
        }

    }


    /**
     * scan
     */
    private void scanSdCard() {
        getActivity().registerReceiver(scanSdReceiver, intentfilter);
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
            Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath()
            )));
    }


    public class ScanSdReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
                progressDialog.dismiss();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            captureManager.galleryAddPic();
            if (directorieLists.size() > 0) {
                String path = captureManager.getCurrentPhotoPath();
                MediaDirectory directory = directorieLists.get(INDEX_ALL_PHOTOS);
                directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path, ""));
                directory.setCoverPath(path);
                photoGridAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        captureManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        captureManager.onRestoreInstanceState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }


    @Override public void onDestroy() {
        super.onDestroy();
        if (scanSdReceiver != null) {

            try {

                getActivity().unregisterReceiver(scanSdReceiver);

            } catch (IllegalArgumentException e) {

                if (e.getMessage().contains("Receiver not registered")) {

                } else {
                    throw e;
                }

            }
        }
    }
}
