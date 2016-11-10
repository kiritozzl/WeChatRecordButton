package com.example.kirito.wechatrecordbutton;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kirito.wechatrecordbutton.adapter.ListViewAdapter;
import com.example.kirito.wechatrecordbutton.support.AudioButton;
import com.example.kirito.wechatrecordbutton.support.MediaPlay;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private AudioButton ab;
    private List<RecordItem> items;
    private ListViewAdapter adapter;
    private View animeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv);
        ab = (AudioButton) findViewById(R.id.btn);
        items = new ArrayList<>();

        ab.setOnFinishListener(new AudioButton.onFinishListener() {
            @Override
            public void finishRecord(String path, float time) {
                RecordItem item = new RecordItem(path, (int) time);
                items.add(item);
                adapter.notifyDataSetChanged();
                //每次更新完listview指向最后的item
                lv.setSelection(items.size() - 1);
            }
        });
        adapter = new ListViewAdapter(MainActivity.this,items);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //每次开启动画前，先设置为默认状态
                if (animeView != null){
                    animeView.setBackgroundResource(R.drawable.adj);
                    animeView = null;
                }
                //通过view获取animeView
                animeView = view.findViewById(R.id.v_anime);
                animeView.setBackgroundResource(R.drawable.voice_animation);
                AnimationDrawable ad = (AnimationDrawable) animeView.getBackground();
                ad.start();

                //播放录制的音频
                MediaPlay.playAudio(items.get(position).getPath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //播放完成，停止动画效果
                        animeView.setBackgroundResource(R.drawable.adj);
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlay.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaPlay.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlay.onRelease();
    }

    //RecordItem放置录音文件的路径和录音时间
    public class RecordItem{
        private String path;
        private int time;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public RecordItem(String path, int time) {

            this.path = path;
            this.time = time;
        }
    }
}
