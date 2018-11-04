package com.example.phanminhduong.reminder.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.phanminhduong.reminder.R;

import java.io.InputStream;
import java.util.Date;


public class AddWorkActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    EditText noteTxt;
    TextView timeTxt ;
    ImageView imageView;
    private String time, image;
    private int year, month, day, hour, minute;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work);
        noteTxt = findViewById(R.id.note);
        timeTxt = findViewById(R.id.timeTxt);
        imageView = findViewById(R.id.imageView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            if(data != null)
            {
                Bitmap bitmap = null;
                try {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    InputStream stream = getContentResolver().openInputStream(data.getData());

                    bitmap = BitmapFactory.decodeStream(stream);
                    image = data.getData().toString();
                    stream.close();
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void update(View v){
        Intent i = new Intent();
        i.putExtra("time", time);
        i.putExtra("note", noteTxt.getText().toString());
        i.putExtra("image", image);
        setResult(198, i);
        finish();
    }

    public void changeImage(View v){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void changeDate(View v){
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
                );
        datePickerDialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year-1900;
        this.month = month;
        this.day = dayOfMonth;
        Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddWorkActivity.this, AddWorkActivity.this,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        Date d = new Date(year,month,day, hour, minute);

        time = new android.text.format.DateFormat().format("a hh:mm:ss  dd-MM-yyyy", d).toString();
        timeTxt.setText(time);

    }


    public void onCancel(View v) {
        super.onBackPressed();
    }
}
