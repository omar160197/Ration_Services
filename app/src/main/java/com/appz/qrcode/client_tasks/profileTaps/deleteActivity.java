package com.appz.qrcode.client_tasks.profileTaps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appz.qrcode.R;
import com.appz.qrcode.helperUi.AllFinal;
import com.appz.qrcode.helperUi.NoInternet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class deleteActivity extends AppCompatActivity {

    private final DatabaseReference rationTable = FirebaseDatabase.getInstance().getReference().child(AllFinal.Ration_Data);
    //ui
    ListView listView;
    ProgressDialog progressDialog;
    TextView textView;
    //var
    List ids = new ArrayList();
    List name = new ArrayList();
    String parentID;
    int points;
    //database
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_delete);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textView = findViewById(R.id.txt);
        init();
        //refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child(AllFinal.Ration_Data);
        parentID = getIntent().getStringExtra("id");
        getIDs(rationTable.child(parentID).child(AllFinal.CHILDS));
        getdata(ref.child(parentID).child("points"));
    }


    private void getIDs(final DatabaseReference ref) {

        ids.clear();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot data : dataSnapshot.getChildren()) {
                    if (Character.isDigit(data.getKey().charAt(0))) {
                        ids.add(data.getKey());

                    }
                }

                getNames(ids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getNames(final List ids) {
        if (ids.size() > 0) {
            for (int i = 0; i < ids.size(); i++) {

                final DatabaseReference reference = rationTable.child(parentID).child(AllFinal.CHILDS).child(String.valueOf(ids.get(i)));
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String name2 = dataSnapshot1.getValue(String.class);
                            name.add(name2);
                        }

                        progressDialog.dismiss();
                        listView = findViewById(R.id.listView);

                        textView.setVisibility(View.VISIBLE);
                        ArrayAdapter arrayAdapter = new ArrayAdapter(deleteActivity.this, android.R.layout.simple_list_item_1, name);
                        listView.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                        onitemclick();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } else {
            progressDialog.dismiss();
            textView.setText("There Are No Children To Delete");
            textView.setVisibility(View.VISIBLE);
        }

    }

    private void getdata(DatabaseReference reff) {
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    points = dataSnapshot.getValue(Integer.class);
                } else {
                    Toast.makeText(getApplicationContext(), "no child add child first and try again !", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void onitemclick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (checkInternetConnection()) {
                    Intent intent = new Intent(getApplicationContext(), dialogActivity.class);
                    intent.putExtra("name", String.valueOf(name.get(position)));
                    intent.putExtra("childid", String.valueOf(ids.get(position)));
                    intent.putExtra("parentID", parentID);
                    intent.putExtra("parentPoints", points);
                    startActivity(intent);
                } else
                    startActivity(new Intent(deleteActivity.this, NoInternet.class));
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
