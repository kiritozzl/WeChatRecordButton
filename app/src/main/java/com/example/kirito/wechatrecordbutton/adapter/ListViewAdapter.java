package com.example.kirito.wechatrecordbutton.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.kirito.wechatrecordbutton.MainActivity;
import com.example.kirito.wechatrecordbutton.R;

import java.util.List;

/**
 * Created by kirito on 2016.11.10.
 */

public class ListViewAdapter extends ArrayAdapter<MainActivity.RecordItem> {
    private LayoutInflater mLayoutInfalter;
    private int min_length;
    private int max_length;

    public ListViewAdapter(Context context, List<MainActivity.RecordItem> objects) {
        super(context, -1, objects);
        mLayoutInfalter = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        min_length = (int) (metrics.widthPixels * 0.1f);
        max_length = (int) (metrics.widthPixels * 0.8f);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder = null;
        if (convertView == null){
            convertView = mLayoutInfalter.inflate(R.layout.item,parent,false);
            holder = new viewHolder();
            //这里获取的是背景框的FrameLayout的view对象，为了改变背景框的长度
            holder.length = convertView.findViewById(R.id.fl);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        }else {
            holder = (viewHolder) convertView.getTag();
        }
        MainActivity.RecordItem item = getItem(position);
        int time = item.getTime();
        //注意转义字符
        holder.time.setText(time + "\"");
        ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
        //控制录音长度最长为：itemMinWidth + itemMaxWidth,以及类型转换
        if (time <= 60){
            lp.width = (int) (min_length + max_length * time / 60f);
        }else {
            lp.width = min_length + max_length;
        }
        return convertView;
    }

    class viewHolder{
        View length;
        TextView time;
    }
}
