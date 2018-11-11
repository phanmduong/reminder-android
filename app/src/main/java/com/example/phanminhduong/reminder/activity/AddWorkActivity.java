package com.example.phanminhduong.reminder.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.exception.ApolloException;
import com.example.phanminhduong.reminder.ActionCode;
import com.example.phanminhduong.reminder.Data;
import com.example.phanminhduong.reminder.R;
import com.example.phanminhduong.reminder.TodoListMutation;
import com.example.phanminhduong.reminder.graphql.MyApolloClient;
import com.example.phanminhduong.reminder.model.Image;
import com.example.phanminhduong.reminder.model.Work;
import com.example.phanminhduong.reminder.service.ServiceUpload;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddWorkActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    EditText noteTxt, titleTxt;
    TextView timeTxt;
    ImageView imageView;
    private String time, image, note, title;
    private int year, month, day, hour, minute;

    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work);
        noteTxt = findViewById(R.id.note);
        titleTxt = findViewById(R.id.txtTitle);
        timeTxt = findViewById(R.id.timeTxt);
        imageView = findViewById(R.id.imageView);

        prgDialog = new ProgressDialog(this);
        prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prgDialog.setMessage("Đang tải...");
        prgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        prgDialog.setIndeterminate(false);
        prgDialog.setCancelable(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActionCode.PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bitmap bitmap = null;
                try {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }

                    android.net.Uri selectedImage = data.getData();

                    uploadImage(getPath(selectedImage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update(View v) {
        note = noteTxt.getText().toString();
        title = titleTxt.getText().toString();
        if (time == null || note == null || title == null) {
            Toast.makeText(this, "Bạn chưa hoàn thành thông tin!", Toast.LENGTH_LONG).show();
            return;
        }

        try {

            //format date
            java.util.Date parse = new SimpleDateFormat("a hh:mm:ss  dd-MM-yyyy").parse(time);
            String formatted_time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(parse);
            Log.e("time", formatted_time);
            time = formatted_time;

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.e("TOKEN", Data.token);
        TodoListMutation tm = TodoListMutation.builder().token(Data.token).name(title).note(note).deadline(time).group_id(Data.groupId).build();
        MyApolloClient.getApolloClient().mutate(tm).enqueue(new ApolloCall.Callback<TodoListMutation.Data>() {
            @Override
            public void onResponse(@NotNull com.apollographql.apollo.api.Response<TodoListMutation.Data> response) {
                Object obj = response.data();
                Log.e("OBJ", obj.toString());
                AddWorkActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddWorkActivity.this, "Thêm thành công!!!", Toast.LENGTH_LONG).show();

                    }
                });

            }

            @Override
            public void onFailure(@NotNull final ApolloException e) {
                e.printStackTrace();
                AddWorkActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        e.printStackTrace();
                        Toast.makeText(AddWorkActivity.this, "Có lỗi xảy ra!", Toast.LENGTH_LONG).show();

                    }
                });

            }
        });

        Intent i = new Intent();
        i.putExtra("time", time);
        i.putExtra("note", note);
        i.putExtra("title", title);
        i.putExtra("image", image);
        setResult(198, i);
        finish();
    }

    public void changeImage(View v) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, ActionCode.PICK_IMAGE);
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    private void uploadImage(String filePath) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

// Change base URL to your upload server URL.
        ServiceUpload service = new Retrofit.Builder().baseUrl("http://api.colorme.vn").addConverterFactory(GsonConverterFactory.create()).client(client).build().create(ServiceUpload.class);

        File file = new File(filePath);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("image/jpeg"), "image");

        prgDialog.show();
        retrofit2.Call<Image> req = service.postImage(body, name);
        req.enqueue(new Callback<Image>() {
            @Override
            public void onResponse(Call<Image> call, Response<Image> response) {
                image = response.body().getLink();
                Log.e("image", image);
                AddWorkActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgDialog.dismiss();
                        Picasso.get().load(image).into(imageView);
                    }
                });
            }

            @Override
            public void onFailure(Call<Image> call, Throwable t) {
                AddWorkActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgDialog.dismiss();
                        Toast.makeText(AddWorkActivity.this, "Tải ảnh thất bại. Thử lại", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void changeDate(View v) {
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
        this.year = year - 1900;
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
        Date d = new Date(year, month, day, hour, minute);

        time = new android.text.format.DateFormat().format("a hh:mm:ss  dd-MM-yyyy", d).toString();
        timeTxt.setText(time);

    }


    public void onCancel(View v) {
        super.onBackPressed();
    }
}
