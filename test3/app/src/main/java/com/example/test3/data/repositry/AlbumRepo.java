package com.example.test3.data.repositry;

import android.content.Context;
import android.util.Log;

import com.example.test3.data.api.AlbumDao;
import com.example.test3.data.model.AlbumModel;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlbumRepo {
    private List<AlbumModel> listAlbum;
    private Retrofit retrofit;
    private AlbumDao albumDao;
    private Context context;


    public AlbumRepo(Context context) {
        this.context = context;

        retrofit = new Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        albumDao = retrofit.create(AlbumDao.class);
        listAlbum = new ArrayList<>();

    }

    public Observable getListAlbum() {

        Observable listObservable = Observable.fromPublisher(new Flowable() {
            @Override
            protected void subscribeActual(@NonNull final Subscriber subscriber) {

                albumDao.getAlbums().enqueue(new Callback<List<AlbumModel>>() {
                    @Override
                    public void onResponse(Call<List<AlbumModel>> call, Response<List<AlbumModel>> response) {
                        listAlbum.clear();
                        listAlbum.addAll(response.body());
                        Log.d("qqqq", "onsucces ");
                        subscriber.onNext(listAlbum);
                    }


                    @Override
                    public void onFailure(Call<List<AlbumModel>> call, Throwable t) {
                        Log.d("qqqq", "onFailure: " + t.getMessage());
                    }
                });


            }
        }).subscribeOn(Schedulers.io());

        return listObservable;

    }


  public void setAlbum(int id,String title)
  {
      albumDao.setAlbum(id,title).enqueue(new Callback<AlbumModel>() {
          @Override
          public void onResponse(Call<AlbumModel> call, Response<AlbumModel> response) {
              Log.d("zzzzzzzzzzz", "setAlbum: "+response.body().toString());
          }

          @Override
          public void onFailure(Call<AlbumModel> call, Throwable t) {
              Log.d("zzzzzzzzzzz", "onFailure: "+t.getMessage());

          }
      });
  }


}
