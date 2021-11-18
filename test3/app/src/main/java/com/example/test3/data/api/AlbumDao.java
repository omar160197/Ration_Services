package com.example.test3.data.api;

import com.example.test3.data.model.AlbumModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AlbumDao {

    @GET("photos")
    Call<List<AlbumModel>> getAlbums();


    @FormUrlEncoded
    @POST("photos/")
    Call<AlbumModel> setAlbum(@Field("id") int id, @Field("title") String title);
}
