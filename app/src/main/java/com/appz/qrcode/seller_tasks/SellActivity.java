package com.appz.qrcode.seller_tasks;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appz.qrcode.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SellActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public String id;
    private ZXingScannerView scannerView;
    private TextView txtResult;
    private double points;

    private TextView Pointtext;
    private Button IntentButton;


    // public static final String Client_Id = "Client_Id";
    //public static final String Client_Points = "Client_Points";

    // TODO
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        scannerView = findViewById(R.id.zxscan);
        txtResult = findViewById(R.id.txt_result);

        Pointtext = findViewById(R.id.pointtext);
        IntentButton = findViewById(R.id.Button);

        IntentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StoreActivity.class);

                //String clientpoints= Pointtext.getText().toString();
                String cliiintId = txtResult.getText().toString();

                intent.putExtra("Client_Points", points);
                intent.putExtra("Client_Id", cliiintId);
                startActivity(intent);
                finish();
            }
        });

        //Requsting permission
        Dexter.withActivity(this).
                withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(SellActivity.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(SellActivity.this, "you must accept this permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();


    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        txtResult.setText(rawResult.getText());
        id = rawResult.getText();
        getpoints();

    }

    private void getpoints() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Ration_Data")
                .child(id).child("points");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                points = dataSnapshot.getValue(Double.class);
                Log.d("eeeeeeeeeeee", points + "");
                Pointtext.setText(String.valueOf(points));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}