package com.example.phanminhduong.reminder.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.phanminhduong.reminder.R;
import com.example.phanminhduong.reminder.activity.TodayActivity;
import com.example.phanminhduong.reminder.model.Work;

import java.util.List;

public class DoneWorkAdapter extends BaseAdapter {
    private TodayActivity mainActivity;
    private List<Work> workList;

    public DoneWorkAdapter(TodayActivity mainActivity, List<Work> workList) {
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
            v = mainActivity.getLayoutInflater().inflate(R.layout.done_work_list_item_layout, null);
            m = new MyHolder();
            m.checkBox = v.findViewById(R.id.idCheckBox);
            m.textView = v.findViewById(R.id.idTitle);
            v.setTag(m);

        } else {
            m = (MyHolder) v.getTag();
        }
        final Work p = workList.get(position);
        m.checkBox.setChecked(p.getStatus()!=0);

        m.textView.setText(p.getName());

        return v;
    }
    class MyHolder{
        public CheckBox checkBox;
        public TextView textView;
    }
}

