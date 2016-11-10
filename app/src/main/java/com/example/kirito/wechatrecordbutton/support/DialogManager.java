package com.example.kirito.wechatrecordbutton.support;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kirito.wechatrecordbutton.R;

/**
 * Created by kirito on 2016.11.07.
 */

public class DialogManager {
    private LayoutInflater mLayoutInflater;
    private Dialog mDialog;
    private Context mContext;
    private ImageView iv_icon,iv_voice;
    private TextView tv_label;

    private static final String TAG = "DialogManager";

    public DialogManager(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void showDialog(){
        mDialog = new Dialog(mContext, R.style.Dialog_Theme);
        View view = mLayoutInflater.inflate(R.layout.dialog,null,false);
        mDialog.setContentView(view);

        iv_icon = (ImageView) mDialog.findViewById(R.id.iv_icon);
        iv_voice = (ImageView) mDialog.findViewById(R.id.iv_voice);
        tv_label = (TextView) mDialog.findViewById(R.id.tv_label);

        mDialog.show();
    }

    public void showRecording(){
        if (mDialog != null && mDialog.isShowing()){
            iv_icon.setVisibility(View.VISIBLE);
            iv_voice.setVisibility(View.VISIBLE);
            iv_icon.setImageResource(R.drawable.recorder);
            iv_voice.setImageResource(R.drawable.v1);

            tv_label.setText(R.string.dialog_recording);
        }
    }

    public void wantToCancel(){
        if (mDialog != null && mDialog.isShowing()){
            iv_icon.setVisibility(View.VISIBLE);
            iv_voice.setVisibility(View.GONE);
            iv_icon.setImageResource(R.drawable.cancel);

            tv_label.setText(R.string.dialog_cancel);
        }
    }

    public void tooShort(){
        if (mDialog != null && mDialog.isShowing()){
            iv_icon.setVisibility(View.VISIBLE);
            iv_voice.setVisibility(View.GONE);
            iv_icon.setImageResource(R.drawable.voice_to_short);

            tv_label.setText(R.string.dialog_too_short);
        }
    }

    public void dismissDialog(){
        if (mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void setVoiceLevel(int level){
        if (mDialog != null && mDialog.isShowing()){
            iv_icon.setVisibility(View.VISIBLE);
            iv_voice.setVisibility(View.VISIBLE);
            iv_icon.setImageResource(R.drawable.recorder);

            int res_id = mContext.getResources().getIdentifier("v" + level,"drawable",mContext.getPackageName());
            iv_voice.setImageResource(res_id);
            tv_label.setText(R.string.dialog_recording);
        }
    }
}
