package com.example.kirito.wechatrecordbutton.support;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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

    private static final int AUDIO_PREPARED = 0x110;
    private static final int AUDIO_CANCEL = 0x111;
    private static final int AUDIO_VOICE_CHANGE = 0x112;

    //mTime 必须是float型，不能是int型
    private float mTime;

    private int cur_state = BTN_STATE_NORMAL;

    private DialogManager mDialogManager;
    //标志audio recorder是否prepare完毕
    private boolean isRecording;
    private static final int MIN_CABCEL_Y = 100;

    private AudioManager am;
    //是否触发了onLongClick
    private boolean isLongClick;

    private boolean isVoiceConflict;

    private onFinishListener mListener;

    private Handler handler = new Handler();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case AUDIO_PREPARED:
                    //在准备好audio recorder之后
                    isRecording = true;
                    mDialogManager.showDialog();
                    handler.post(timeRunnable);
                    break;
                case AUDIO_CANCEL:
                    mDialogManager.dismissDialog();
                    break;
                case AUDIO_VOICE_CHANGE:
                    mDialogManager.setVoiceLevel(am.getVoiceLevel(7));
                    break;
            }
        }
    };

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording && !isVoiceConflict){
                mTime += 0.1;
                mHandler.sendEmptyMessage(AUDIO_VOICE_CHANGE);
                handler.postDelayed(timeRunnable,100);
            }
        }
    };

    //录音完成的回调
    public interface onFinishListener{
        void finishRecord(String path, float time);
    }

    public void setOnFinishListener(onFinishListener listener){
        mListener = listener;
    }

    private static final String TAG = "AudioButton";

    public AudioButton(Context context) {
        this(context,null);
    }

    public AudioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(context);
        am = AudioManager.getInstance(Environment.getExternalStorageDirectory() + "/zzl_audio");

        am.setAudioPrepareListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongClick = true;
                am.prepareAudio();
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
                if (!isLongClick){
                    reset();
                    mDialogManager.dismissDialog();
                    return super.onTouchEvent(event);
                }else if(!isRecording || mTime < 0.6){
                    //设置isVoiceConflict 为true防止setVoiceLevel方法更改图标，造成图标显示混乱！
                    isVoiceConflict = true;
                    mDialogManager.tooShort();
                    am.cancelAudio();
                    //延时让tooShort的图标显示出来
                    mHandler.sendEmptyMessageDelayed(AUDIO_CANCEL,1300);
                }else if (cur_state == BTN_STATE_RECORDING){//正常退出，获取音频路径，时长
                    am.releaseAudio();
                    mDialogManager.dismissDialog();
                    if (mListener != null){
                        mListener.finishRecord(am.getFilePath(),mTime);
                    }
                }else if (cur_state == BTN_STATE_WANTTOCANCEL){
                    am.cancelAudio();
                    mDialogManager.dismissDialog();
                }
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                    if (isRecording){
                        if(wantToCancel(x,y)){
                            isVoiceConflict = true;
                            changeButtonState(BTN_STATE_WANTTOCANCEL);
                        }else if(!wantToCancel(x,y)){
                            changeButtonState(BTN_STATE_RECORDING);
                        }
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
        isLongClick = false;
        isRecording = false;
        isVoiceConflict = false;
        mTime = 0;
        changeButtonState(BTN_STATE_NORMAL);
    }

    public void changeButtonState(int ste){
        if (cur_state != ste){
            cur_state = ste;
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
        mHandler.sendEmptyMessage(AUDIO_PREPARED);
    }
}
