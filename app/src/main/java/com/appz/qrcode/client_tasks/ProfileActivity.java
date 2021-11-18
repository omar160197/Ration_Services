package com.appz.qrcode.client_tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appz.qrcode.R;
import com.appz.qrcode.client_tasks.profileTaps.addActivity;
import com.appz.qrcode.client_tasks.profileTaps.deleteActivity;
import com.appz.qrcode.client_tasks.profileTaps.overviewActivity;
import com.appz.qrcode.helperUi.AllFinal;
import com.appz.qrcode.helperUi.NoInternet;
import com.appz.qrcode.helperUi.QrActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    Button add, delete, overview, restore;
    FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(AllFinal.Generated);
    boolean ok = false;
    List ids = new ArrayList();
    String id;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        findByID();
    }

    private void init() {
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();
        getids(reference);

    }

    private void isGenerated(List ids) {

        for (int i = 0; i < ids.size(); ++i) {
            if (ids.get(i).equals(CurrentUser.getUid())) {
                ok = true;
                getParentId(reference.child(CurrentUser.getUid()));
                break;
            }
        }

        if (!ok) {
            progressDialog.dismiss();
            add();
            delete();
            overview();
            restoreQR();
        }
    }

    private void getids(DatabaseReference ref) {
        ids.clear();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ids.add(data.getKey());
                }
                isGenerated(ids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void findByID() {
        add = findViewById(R.id.addchild);
        delete = findViewById(R.id.delete);
        overview = findViewById(R.id.view);
        restore = findViewById(R.id.restoreQR);

    }

    private void add() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInternetConnection()) {
                    if (ok) {
                        Intent intent = new Intent(ProfileActivity.this, addActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);

                    } else
                        Toast.makeText(getApplicationContext(), "You Don't Have A QR Code", Toast.LENGTH_LONG).show();
                } else
                    startActivity(new Intent(ProfileActivity.this, NoInternet.class));


            }
        });

    }

    private void getParentId(DatabaseReference ref) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id = dataSnapshot.child("id").getValue(String.class);
                progressDialog.dismiss();
                add();
                delete();
                overview();
                restoreQR();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void delete() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInternetConnection()) {
                    if (ok) {


                        Intent intent = new Intent(ProfileActivity.this, deleteActivity.class);
                        if (id != null || !id.equals("")) {
                            intent.putExtra("id", id);

                            startActivity(intent);

                        } else {
                            Toast.makeText(getApplicationContext(), "no child add child first and try again !", Toast.LENGTH_LONG).show();
                        }

                    } else
                        Toast.makeText(getApplicationContext(), "You Don't Have A QR Code", Toast.LENGTH_LONG).show();
                } else
                    startActivity(new Intent(ProfileActivity.this, NoInternet.class));


            }
        });

    }

    private void overview() {
        overview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInternetConnection()) {
                    if (ok) {

                        Intent intent = new Intent(ProfileActivity.this, overviewActivity.class);
                        intent.putExtra("id", id);

                        startActivity(intent);

                    } else
                        Toast.makeText(getApplicationContext(), "You Don't Have A QR Code", Toast.LENGTH_LONG).show();
                } else
                    startActivity(new Intent(ProfileActivity.this, NoInternet.class));


            }
        });

    }

    private void restoreQR() {
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInternetConnection()) {
                    if (ok) {

                        Intent intent = new Intent(ProfileActivity.this, QrActivity.class);
                        intent.putExtra("idCard", id);

                        startActivity(intent);

                    } else
                        Toast.makeText(getApplicationContext(), "You Don't Have A QR Code", Toast.LENGTH_LONG).show();
                } else
                    startActivity(new Intent(ProfileActivity.this, NoInternet.class));


            }
        });
    }

    private Boolean checkInternetConnection() {
        Boolean internetConnection = false;
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connection = manager.getActiveNetworkInfo();

        if (connection != null) {
            if (connection.getType() == ConnectivityManager.TYPE_WIFI)
                return internetConnection = true;
            else if (connection.getType() == ConnectivityManager.TYPE_MOBILE)
                return internetConnection = true;
            else
                return internetConnection = false;
        }

        return internetConnection;
    }
}
