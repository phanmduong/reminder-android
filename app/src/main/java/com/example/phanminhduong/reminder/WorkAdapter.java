package com.example.phanminhduong.reminder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class WorkAdapter extends BaseAdapter {
    private TodayActivity mainActivity;
    private List<Work> workList;

    public WorkAdapter(TodayActivity mainActivity, List<Work> workList) {
        this.mainActivity = mainActivity;
        this.workList = workList;
    }

    @Override
    public int getCount() {
        return workList.size();
    }

    @Override
    public Object getItem(int position) {
        return workList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        MyHolder m = null;
        if (v == null) {
            v = mainActivity.getLayoutInflater().inflate(R.layout.work_list_item_layout, null);
            m = new MyHolder();
            m.checkBox = v.findViewById(R.id.idCheckBox);
            m.textView = v.findViewById(R.id.idTitle);
            v.setTag(m);

        } else {
            m = (MyHolder) v.getTag();
        }
        final Work p = workList.get(position);
        m.checkBox.setChecked(p.isStatus());

        m.textView.setText(p.getNote());

        return v;
    }
    class MyHolder{
        public CheckBox checkBox;
        public TextView textView;
    }
}

