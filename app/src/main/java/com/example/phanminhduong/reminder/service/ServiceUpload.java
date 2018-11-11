package com.example.phanminhduong.reminder.service;

import com.example.phanminhduong.reminder.model.Image;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceUpload {
    @Multipart
    @POST("/upload-image-froala")
    Call<Image> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);
}
