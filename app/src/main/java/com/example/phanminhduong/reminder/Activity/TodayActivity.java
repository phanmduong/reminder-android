package com.example.phanminhduong.reminder.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.phanminhduong.reminder.Adapter.WorkAdapter;
import com.example.phanminhduong.reminder.Model.Work;
import com.example.phanminhduong.reminder.R;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class TodayActivity extends AppCompatActivity {
    private ListView listView,doneWorkListView;
    List<Work> listWork, listDoneWork;
    WorkAdapter workAdapter;
    private int ADD_WORK = 198;
    Bitmap bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        listView = findViewById(R.id.workListView);
        listWork = new LinkedList<>();
        listWork.add(new Work("Viec 1", "1997-11-12", false));
        listWork.add(new Work("Viec 2", "1997-11-12", true));
        listWork.add(new Work("Viec 3", "1997-11-12", false));
        listWork.add(new Work("Viec 4", "1997-11-12", true));
        listWork.add(new Work("Viec 1", "1997-11-12", false));
        listWork.add(new Work("Viec 2", "1997-11-12", true));
        listWork.add(new Work("Viec 3", "1997-11-12", false));
        listWork.add(new Work("Viec 4", "1997-11-12", true));
        listWork.add(new Work("Viec 1", "1997-11-12", false));
        listWork.add(new Work("Viec 2", "1997-11-12", true));
        listWork.add(new Work("Viec 3", "1997-11-12", false));
        listWork.add(new Work("Viec 4", "1997-11-12", true));
        workAdapter = new WorkAdapter(this, listWork);
        listView.setAdapter(workAdapter);

        doneWorkListView = findViewById(R.id.doneWorkListView);
        listDoneWork = new LinkedList<>();
        listDoneWork.add(new Work("Viec xong 1", "1997-11-12", false));
        listDoneWork.add(new Work("Viec xong 2", "1997-11-12", true));
        listDoneWork.add(new Work("Viec xong 3", "1997-11-12", false));
        listDoneWork.add(new Work("Viec xong 4", "1997-11-12", true));
        workAdapter = new WorkAdapter(this, listDoneWork);
        doneWorkListView.setAdapter(workAdapter);
    }

    public void addWork(View v){
        Intent i = new Intent(this, AddWorkActivity.class);
        startActivityForResult(i, ADD_WORK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == ADD_WORK){
            String time = data.getStringExtra("time");
            String image = data.getStringExtra("image");
            String note = data.getStringExtra("note");
            Log.e("Time: ", time);
            Log.e("Image: ", image);
            Log.e("Note: ", note);
            try {
                if (bt != null) {
                    bt.recycle();
                }

                InputStream stream = getContentResolver().openInputStream(Uri.parse(image));

                bt = BitmapFactory.decodeStream(stream);

                stream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            listWork.add(new Work(note, time, false));
            doneWorkListView.setAdapter(new WorkAdapter(this, listDoneWork));

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
