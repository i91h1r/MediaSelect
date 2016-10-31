package com.github.hyr0318.mediaselect_library.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.hyr0318.mediaselect_library.R;
import com.github.hyr0318.mediaselect_library.utils.PickerConstants;
import com.github.hyr0318.mediaselect_library.utils.U;
import com.github.hyr0318.mediaselect_library.widget.DrawView;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;



/**
 * Description:
 * Author：hyr on 2016/10/28 16:25
 * Email：2045446584@qq.com
 */
public class ApplyRecorderActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = "ApplyRecorderActivity";
    private TextView showRecordState = null;//显示录制状态
    private TextView showRecordTime = null;//录制音频时间
    private ImageView recordDeleteBtn = null;//音频删除按钮
    private ImageView recordBtn = null;//音频录制按钮
    private ImageView recordFinishBtn = null;//音频完成按钮
    private boolean startRecord = true;//控制录制按钮切换
    private String recordPath;//录音路径
    private LinearLayout closeLayout;
    private Context mContext;
    private LinearLayout voiceLayout;
    private MediaPlayer mediaPlayer;
    private ImageView playBtn;//播放按钮
    private boolean isPlaying = true;
    ProgressDialog progressDialog;
    //波形录制音频
    private MediaRecorder mRecorder = null;
    private static final long REFRESH_INTERVAL_MS = 26;
    private boolean keepGoing = true;
    private DrawView view;
    private Timer timer = null;//显示录音时间
    private TimerTask task = null;
    int recordTime = 0;//记录录制音频时间
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    timer = new Timer();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            recordTime++;
                            updateTime(recordTime);
                        }
                    };
                    timer.schedule(task, 0, 1000);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crp_record_view);
        findView();
        fillUi();
    }

    public void findView() {
        mContext = ApplyRecorderActivity.this;
        mediaPlayer = new MediaPlayer();
        showRecordState = (TextView) findViewById(R.id.show_audio_state);
        showRecordTime = (TextView) findViewById(R.id.show_record_time);
        recordDeleteBtn = (ImageView) findViewById(R.id.record_delete_btn);
        recordBtn = (ImageView) findViewById(R.id.record_btn);
        recordFinishBtn = (ImageView) findViewById(R.id.record_finish_btn);
        closeLayout = (LinearLayout) findViewById(R.id.close_layout);
        voiceLayout = (LinearLayout) findViewById(R.id.voice_layout);
        playBtn = (ImageView) findViewById(R.id.play_btn);
        playBtn.setOnClickListener(this);
        recordDeleteBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        recordFinishBtn.setOnClickListener(this);
        closeLayout.setOnClickListener(this);
    }

    /**
     * 动态更新时间
     *
     * @param time
     */
    private void updateTime(final int time) {

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showRecordTime.setText(String.valueOf(getRecordTime(time)));
                showRecordState.setText(mContext.getResources().getString(R.string
                    .audio_recording_txt));
            }
        });

    }

    /**
     * 　　* 时间的处理
     * 　　*
     * 　　* @param time
     * 　　* @return
     */
    public static String getRecordTime(int time) {
        String recordTime = "00:00:00";

        if (time <= 0) {
            return "00:00:00";
        }
        int secondnd = time % 60;
        int minute = time / 60;
        int hour = (time / (60 * 60)) % 60;
        String s = secondnd >= 10 ? String.valueOf(secondnd) : "0" + String.valueOf(secondnd);
        String m = minute >= 10 ? String.valueOf(minute) : "0" + String.valueOf(minute);
        String h = hour >= 10 ? String.valueOf(hour) : "0" + String.valueOf(hour);
        recordTime = h + ":" + m + ":" + s;

        return recordTime;
    }

    public void fillUi() {
        view = new DrawView(this);
        view.invalidate();
        voiceLayout.addView(view);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                runGameLoop();
            }
        });

        thread.start();
    }

    private void runGameLoop() {
        // update the game repeatedly
        while (keepGoing) {
            long durationMs = redraw();
            try {
                Thread.sleep(Math.max(0, REFRESH_INTERVAL_MS - durationMs));
            } catch (InterruptedException e) {
            }
        }
    }

    private long redraw() {

        long t = System.currentTimeMillis();

        // At this point perform changes to the model that the component will
        // redraw

        display_game();


        // return time taken to do redraw in ms
        return System.currentTimeMillis() - t;
    }


    private void display_game() {


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                view.phase += view.phaseShift;
                if (null != mRecorder) {
                    view.amplitude = (view.amplitude + Math.max(mRecorder.getMaxAmplitude() /
                        51805.5336f, 0.01f)) / 2;

                } else {

                    view.amplitude = (view.amplitude + Math.max(0 /
                        51805.5336f, 0.01f)) / 2;
                }

                view.invalidate();

            }
        });

        //        Log.v("Game", "Display Game" + view.phase);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.record_delete_btn) {//录音删除
            deleteRecord();
        } else if (v.getId() == R.id.record_btn) {
            dealRecord();
        } else if (v.getId() == R.id.record_finish_btn) {//录音完成
            finishRecord();

        } else if (v.getId() == R.id.close_layout) {
            finish();
        } else if (v.getId() == R.id.play_btn) {//播放音频

            playRecord();
        }


    }


    /**
     * 播放音频
     */
    private void playRecord() {

        if (null != mediaPlayer) {

            if (mediaPlayer.isPlaying()) {


                playBtn.setBackgroundResource(R.mipmap.audio_pause_btn);
                mediaPlayer.pause();
            } else {
                if (isPlaying) {
                    playMusic(PickerConstants.CRP_AUDIO + recordPath);
                } else {

                    playBtn.setBackgroundResource(R.mipmap.audio_play_btn);
                    mediaPlayer.start();
                }


            }

        }

    }

    /**
     * 暂停音频
     */
    private void pausePlay() {

        if (null != mediaPlayer) {

            if (mediaPlayer.isPlaying()) {

                playBtn.setBackgroundResource(R.mipmap.audio_pause_btn);
                mediaPlayer.pause();
            }

        }
    }


    /**
     * 录音完成
     */
    private void finishRecord() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog = progressDialog.show(mContext, "", getString(R.string.save_audio_tip));
        recordBtn.setEnabled(true);
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat) {

            String[] paths = { Environment
                .getExternalStorageDirectory().getAbsolutePath()};
            String[] mimeTypes = {null};
            MediaScannerConnection.scanFile(mContext, paths, mimeTypes, new MediaScannerConnection
                .OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    progressDialog.dismiss();
                    finish();
                }
            });
        } else {

            updateAudio();
            progressDialog.dismiss();
            finish();
        }


    }

    public void updateAudio() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(PickerConstants.CRP_AUDIO + recordPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }

    /**
     * 删除录音
     */
    private void deleteRecord() {
        File audioFile = new File(PickerConstants.CRP_AUDIO + recordPath);
        if (audioFile.exists()) {

            audioFile.delete();
        }
        recordPath = "";
        showRecordState.setText(getResources().getString(R.string.audio_start_txt));
        showRecordTime.setText(getResources().getString(R.string.audio_init_txt));
        recordBtn.setBackgroundResource(R.mipmap.record_btn_icon);
        recordDeleteBtn.setVisibility(View.GONE);
        recordFinishBtn.setVisibility(View.GONE);
        playBtn.setVisibility(View.INVISIBLE);
        recordBtn.setEnabled(true);
        isPlaying = true;
        recordTime = 0;
    }

    /**
     * 录制音频
     */
    private void dealRecord() {


        if (startRecord) {
            startRecording(System.currentTimeMillis() + ".amr");//开始录音
            doRecord(true);

        } else {
            stopRecording();//停止录音
            doRecord(false);

        }

    }

    private void doRecord(boolean show) {

        if (show) {

            startRecord = false;
            recordBtn.setEnabled(true);
            recordDeleteBtn.setVisibility(View.GONE);
            recordFinishBtn.setVisibility(View.GONE);
            recordBtn.setBackgroundResource(R.mipmap.record_pressed_btn);
            playBtn.setVisibility(View.INVISIBLE);

        } else {
            startRecord = true;
            recordBtn.setEnabled(false);
            recordDeleteBtn.setVisibility(View.VISIBLE);
            recordFinishBtn.setVisibility(View.VISIBLE);
            recordBtn.setBackgroundResource(R.mipmap.record_btn_icon);
            showRecordState.setText(getResources().getString(R.string.audio_finish_txt));
            playBtn.setVisibility(View.VISIBLE);

        }


    }

    /**
     * 开始录音
     */
    private void startRecording(String path) {
        if (!U.sdCardExists()) {
            showRecordState.setText(getResources().getString(R.string.audio_start_txt));
            recordBtn.setEnabled(true);
            return;
        }
        recordPath = path;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mRecorder.setOutputFile(PickerConstants.CRP_AUDIO + path);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
        mHandler.sendEmptyMessage(0);

    }

    /**
     * 停止录音
     */
    private void stopRecording() {
        if (null != mRecorder) {

            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            timer.cancel();
            task.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        stopRecorder(true);
        mediaPlayer.release();
        mediaPlayer = null;
        if (null != task) {

            timer.cancel();
            task.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (null != mRecorder) {

            stopRecorder(false);
            doRecord(false);
            pausePlay();
        }

        if (null != task) {
            timer.cancel();
            task.cancel();
        }
        super.onPause();
    }

    /**
     * 停止释放
     *
     * @param stop
     */
    private void stopRecorder(boolean stop) {

        if (null != mRecorder) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 播放
     *
     * @param filePath 文件路径
     */
    private void playMusic(String filePath) {
        try {
            // 重置
            mediaPlayer.reset();
            // 设置数据源
            mediaPlayer.setDataSource(filePath);
            // 准备播放
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = false;
            // 设置播放完毕监听
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
