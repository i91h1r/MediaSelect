package com.github.hyr0318.mediaselect_library.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.github.hyr0318.mediaselect_library.R;
import com.github.hyr0318.mediaselect_library.utils.ScreenUtil;

/**
 * Description:
 * 作者：hyr on 2016/10/28 14:48
 * 邮箱：2045446584@qq.com
 */
public class SelectDialog extends Dialog {

    private Context mContext;
    public TextView takePic;
    public TextView takeVedio;
    public TextView takeAudio;
    public Button cancleBtn;

    private OnTakeAudioClickListener onTakeAudioClickListener;

    private OnTakePicClickListener onTakePicClickListener;

    private OnTakeVideoClickListener onTakeVideoClickListener;

    private  OnCancleClickListener onCancleClickListener ;

    public SelectDialog(Context context) {

        this(context, R.style.select_dialog);
    }


    public SelectDialog(Context context, int themeResId) {
        super(context, themeResId);

        this.mContext = context;

    }


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setCancelable(false);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.media_dialog_item);

        Window window = this.getWindow();

        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setWindowAnimations(R.style.take_photo_anim); //设置窗口弹出动画
        int width = ScreenUtil.getInstance(mContext).getScreenWidth();
        lp.width = width;
        window.setAttributes(lp);

        lp.width = width;
        window.setAttributes(lp);

        initView();

        initEvent();
    }


    private void initEvent() {

    }


    private void initView() {

        takePic = (TextView) findViewById(R.id.take_pic);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (onTakePicClickListener != null) {
                    onTakePicClickListener.onTakePicClick();
                    dismiss();
                }
            }
        });
        takeVedio = (TextView) findViewById(R.id.take_vedio);
        takeVedio.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (onTakeVideoClickListener != null) {
                    onTakeVideoClickListener.onTakeVideoClick();
                }
                dismiss();
            }
        });
        takeAudio = (TextView) findViewById(R.id.take_audio);
        takeAudio.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (onTakeAudioClickListener != null) {
                    onTakeAudioClickListener.onTakeAudioClick();
                }
                dismiss();
            }
        });
        cancleBtn = (Button) findViewById(R.id.cancle_btn);

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(onCancleClickListener != null){
                    onCancleClickListener.onCancleClick();
                }
            }
        });
    }


    public interface OnTakePicClickListener {
        void onTakePicClick();
    }


    public interface OnTakeVideoClickListener {
        void onTakeVideoClick();
    }


    public interface OnTakeAudioClickListener {
        void onTakeAudioClick();
    }

    public  interface  OnCancleClickListener{
        void onCancleClick();
    }

    public void setOnTakeAudioClickListener(OnTakeAudioClickListener onTakeAudioClickListener) {
        this.onTakeAudioClickListener = onTakeAudioClickListener;
    }


    public void setOnTakePicClickListener(OnTakePicClickListener onTakePicClickListener) {
        this.onTakePicClickListener = onTakePicClickListener;
    }


    public void setOnTakeVideoClickListener(OnTakeVideoClickListener onTakeVideoClickListener) {
        this.onTakeVideoClickListener = onTakeVideoClickListener;
    }


    public void setOnCancleClickListener(OnCancleClickListener onCancleClickListener) {
        this.onCancleClickListener = onCancleClickListener;
    }
}
