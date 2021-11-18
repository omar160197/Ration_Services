package com.example.test3;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test3.data.model.AlbumModel;
import com.example.test3.data.repositry.AlbumRepo;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    // ui
    TextView callState;
    Button callButton;


    // var
    private AlbumRepo albumRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        actionListeners();


    }

    private void actionListeners() {
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//             albumRepo.getListAlbum().subscribe(new Observer() {
//                 @Override
//                 public void onSubscribe(@NonNull Disposable d) {
//
//                 }
//
//                 @Override
//                 public void onNext(Object o) {
//
//                     List<AlbumModel> list= (List<AlbumModel>) o;
//                     for (AlbumModel albumModel:list)
//                     {
//                         Log.d("qqqqqqqqq", "albums result "+albumModel.toString());
//                     }
//
//                 }
//
//                 @Override
//                 public void onError(@NonNull Throwable e) {
//
//                 }
//
//                 @Override
//                 public void onComplete() {
//
//                 }
//             });


                albumRepo.setAlbum(5,"ezzat");
            }
        });
    }

    private void init() {
        //ui
        callButton = findViewById(R.id.button);



        // var
        albumRepo=new AlbumRepo(MainActivity.this);
    }


}