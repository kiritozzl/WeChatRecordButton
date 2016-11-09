package com.example.kirito.wechatrecordbutton.support;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.kirito.wechatrecordbutton.R;

/**
 * Created by kirito on 2016.11.07.
 */

public class AudioButton extends Button implements AudioManager.audioPrepareListener {
    private static final int BTN_STATE_NORMAL = 0x100;
    private static final int BTN_STATE_RECORDING = 0x101;
    private static final int BTN_STATE_WANTTOCANCEL = 0x102;

    private int cur_state;

    private DialogManager mDialogManager;
    //标志audio recorder是否prepare完毕
    private boolean isRecording;
    private static final int MIN_CABCEL_Y = 100;

    private AudioManager am;

    private static final String TAG = "AudioButton";

    public AudioButton(Context context) {
        this(context,null);
    }

    public AudioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(context);
        am = AudioManager.getInstance(Environment.getExternalStorageDirectory() + "/zzl_audio");
        am.prepareAudio();
        am.setAudioPrepareListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //在准备好audio recorder之后
                isRecording = true;
                mDialogManager.showDialog();
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        //是event.getX() 不是getX()
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                changeButtonState(BTN_STATE_RECORDING);
                break;
            case MotionEvent.ACTION_UP:
                if (cur_state == BTN_STATE_RECORDING){

                }else if (cur_state == BTN_STATE_WANTTOCANCEL){

                }
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                if(wantToCancel(x,y)){
                    changeButtonState(BTN_STATE_WANTTOCANCEL);
                }else {
                    changeButtonState(BTN_STATE_RECORDING);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean wantToCancel(int x, int y) {
        //getWidth---520    getHeight---96
        if (x > getWidth() || x < 0){
            return true;
        }else if (y > getHeight() + MIN_CABCEL_Y || y < -MIN_CABCEL_Y){
            return true;
        }
        return false;
    }

    public void reset(){
        changeButtonState(BTN_STATE_NORMAL);
        mDialogManager.dismissDialog();
    }

    public void changeButtonState(int ste){
        if (cur_state != ste){
            switch (ste){
                case BTN_STATE_NORMAL:
                    setText(getResources().getString(R.string.btn_state_normal));
                    setBackgroundResource(R.drawable.btn_state_normal_bcg);
                    break;
                case BTN_STATE_RECORDING:
                    setText(getResources().getString(R.string.btn_state_recordinng));
                    setBackgroundResource(R.drawable.btn_state_recording_bcg);
                    if (isRecording){
                        mDialogManager.showRecording();
                    }
                    break;
                case BTN_STATE_WANTTOCANCEL:
                    setText(getResources().getString(R.string.btn_state_recordinng));
                    setBackgroundResource(R.drawable.btn_state_recording_bcg);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }

    @Override
    public void audioPrepared() {
        
    }
}
