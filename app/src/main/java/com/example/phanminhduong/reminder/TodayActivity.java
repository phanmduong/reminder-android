package com.example.phanminhduong.reminder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

public class TodayActivity extends AppCompatActivity {
    private ListView listView;
    List<Work> listWork;
    WorkAdapter workAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        listView = findViewById(R.id.workListView);
        listWork = new LinkedList<>();
        listWork.add(new Work("Viec 1", Time.valueOf("12:11:30"), false));
        listWork.add(new Work("Viec 2", Time.valueOf("12:11:30"), true));
        listWork.add(new Work("Viec 3", Time.valueOf("12:11:30"), false));
        listWork.add(new Work("Viec 4", Time.valueOf("12:11:30"), true));
        workAdapter = new WorkAdapter(this, listWork);
        listView.setAdapter(workAdapter);
    }
}
