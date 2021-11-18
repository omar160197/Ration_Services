package com.appz.qrcode.client_tasks.profileTaps;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appz.qrcode.R;
import com.appz.qrcode.client_tasks.profileTaps.adapter.adapter;
import com.appz.qrcode.helperUi.AllFinal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class overviewActivity extends AppCompatActivity {

    private final DatabaseReference rationTable = FirebaseDatabase.getInstance().getReference().child(AllFinal.Ration_Data);
    List id = new ArrayList();
    List name = new ArrayList();
    TextView name_parent, child;
    EditText parent_id, parent_points;
    RecyclerView recyclerView;
    adapter adapter;
    ProgressDialog progressDialog;
    //var
    String parentID, parentName;
    int points;
    //database
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        findbyid();
        init();

    }

    private void init() {
        progressDialog = new ProgressDialog(overviewActivity.this);
        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child(AllFinal.Ration_Data);
        parentID = getIntent().getStringExtra("id");
        getIDs(rationTable.child(parentID).child(AllFinal.CHILDS));

    }

    private void findbyid() {
        recyclerView = findViewById(R.id.recyclerView);
        child = findViewById(R.id.children);
        name_parent = findViewById(R.id.parentName);
        parent_id = findViewById(R.id.parentIDet);
        parent_points = findViewById(R.id.parentPointset);

    }

    private void getIDs(final DatabaseReference ref) {
        id.clear();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (Character.isDigit(data.getKey().charAt(0))) {
                        id.add(data.getKey());
                    }
                }
                getPoints(rationTable.child(parentID).child("points"));
                getParentName(rationTable.child(parentID).child("name"));
                if (id.size() > 0) {
                    getNames(id);
                    child.setVisibility(View.GONE);

                } else {

                    child.setVisibility(View.VISIBLE);
                    child.setText("There Are No Children In This Ration Card");


                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    private void getNames(List ids) {
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

                        adapter = new adapter(getApplicationContext(), id, name);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

    }

    private void getPoints(final DatabaseReference reff) {
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    points = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getParentName(DatabaseReference reff) {
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parentName = dataSnapshot.getValue(String.class);
                setParent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setParent() {

        name_parent.setText(parentName);
        parent_id.setText(parentID);
        parent_points.setText(String.valueOf(points));

    }
}
