package com.appz.qrcode.seller_tasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.appz.qrcode.R;
import com.appz.qrcode.login_tasks.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SellerActivity extends AppCompatActivity {

    // ui
    private Button editStore, sell, logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);


        logout = findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                // Intent intent=new Intent(this,SellActivity.class);
            }
        });


        editStore = findViewById(R.id.button2);
        editStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEditedStore(v);
                // Intent intent=new Intent(this,SellActivity.class);
            }
        });

        sell = findViewById(R.id.button3);
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSell(v);
                // Intent intent=new Intent(this,SellActivity.class);
            }
        });
    }


    public void gotoEditedStore(View view) {
        startActivity(new Intent(getApplicationContext(), EditedStoreActivity.class));
    }

    public void gotoSell(View view) {
        startActivity(new Intent(this, SellActivity.class));
    }
}
