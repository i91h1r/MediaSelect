package com.github.hyr0318.mediaselect_library.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.github.hyr0318.mediaselect_library.R;
import com.github.hyr0318.mediaselect_library.event.OkClickListener;

/**
 * Created by mypc on 2016/6/29.
 */
public class MessagDialogUtil {
    private static Context mContext;
    private static MessagDialogUtil messagDialog;
    private Dialog seletorDialog;
    private OkClickListener okClickListener;

    public MessagDialogUtil() {

    }

    public OkClickListener getOkClickListener() {
        return okClickListener;
    }

    public void setOkClickListener(OkClickListener okClickListener) {
        this.okClickListener = okClickListener;
    }

    public static MessagDialogUtil getIntence(Activity mc) {
        mContext = mc;
        if (messagDialog == null) {
            synchronized (MessagDialogUtil.class) { // 保证了同一时间只能只能有一个对象访问此同步块
                if (messagDialog == null) {
                    messagDialog = new MessagDialogUtil();
                }
            }
        }
        return messagDialog;
    }

    /**
     * 初始化媒体对话框
     */
    public void initDialog(String tip, String leftBtn, String rightBtn) {
        seletorDialog = new Dialog(mContext, R.style.time_dialog);
        seletorDialog.setCancelable(false);
        seletorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        seletorDialog.setContentView(R.layout.message_dialog_item);
        Window window = seletorDialog.getWindow();
        window.setBackgroundDrawableResource(R.mipmap.com_content);
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setWindowAnimations(R.style.take_photo_anim); //设置窗口弹出动画
        int width = ScreenUtil.getInstance(mContext).getScreenWidth() - 40;
        lp.width = width;
        window.setAttributes(lp);
        seletorDialog.show();
        TextView tipTxt = (TextView) seletorDialog.findViewById(R.id.msg_content);
        final Button okBtn = (Button) seletorDialog.findViewById(R.id.msg_ok_btn);
        Button cancleBtn = (Button) seletorDialog.findViewById(R.id.msg_cancle_btn);
        okBtn.setText(leftBtn);
        cancleBtn.setText(rightBtn);
        tipTxt.setText(tip);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okClickListener) {

                    okClickListener.onClick(seletorDialog);
                }
            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seletorDialog.dismiss();
            }
        });

    }


}
