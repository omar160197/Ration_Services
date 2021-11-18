package com.appz.qrcode.helperUi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class WorkerRestPoints extends Worker {
    private String id;
    private DatabaseReference rationTable;
    private Double points;

    public WorkerRestPoints(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        SharedPreferences pref = context.getSharedPreferences("id", Context.MODE_PRIVATE);
        id = pref.getString("id", "");
        rationTable = FirebaseDatabase.getInstance().getReference().child(AllFinal.Ration_Data);
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    @NonNull
    @Override
    public Result doWork() {

        if (!id.equals("")) {

            if (rationTable.child(id) != null) {


                if (rationTable.child(id).child(AllFinal.CHILDS) != null) {
                    rationTable.child(id).child(AllFinal.CHILDS).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long x = dataSnapshot.getChildrenCount() + 1;
                            Log.d("ttttt", x + "");
                            Double x2 = x * 50.0;
                            setPoints(x2);
                            Map<String, Object> map = new HashMap<>();
                            map.put("points", getPoints());

                            rationTable.child(id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("ttttt", "done");

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Map<String, Object> map = new HashMap<>();
                            map.put("points", getPoints());

                            rationTable.child(id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("ttttt", "done");

                                }
                            });
                        }
                    });

                } else {

                    Map<String, Object> map = new HashMap<>();
                    map.put("points", getPoints());

                    rationTable.child(id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ttttt", "done");

                        }
                    });
                }


            } else {
                Log.d("ttttt", "not done");
            }

        }
        return Result.success();
    }
}
